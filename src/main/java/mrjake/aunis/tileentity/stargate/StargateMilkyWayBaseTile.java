package mrjake.aunis.tileentity.stargate;

import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.config.StargateDimensionConfig;
import mrjake.aunis.config.StargateSizeEnum;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.stargate.StargateAbstractRendererState;
import mrjake.aunis.renderer.stargate.StargateMilkyWayRendererState;
import mrjake.aunis.renderer.stargate.StargateMilkyWayRendererState.StargateMilkyWayRendererStateBuilder;
import mrjake.aunis.sound.SoundEventEnum;
import mrjake.aunis.sound.SoundPositionedEnum;
import mrjake.aunis.sound.StargateSoundEventEnum;
import mrjake.aunis.sound.StargateSoundPositionedEnum;
import mrjake.aunis.stargate.EnumScheduledTask;
import mrjake.aunis.stargate.EnumStargateState;
import mrjake.aunis.stargate.merging.StargateAbstractMergeHelper;
import mrjake.aunis.stargate.merging.StargateMilkyWayMergeHelper;
import mrjake.aunis.stargate.network.*;
import mrjake.aunis.state.StargateRendererActionState;
import mrjake.aunis.state.StargateRendererActionState.EnumGateAction;
import mrjake.aunis.state.StargateSpinState;
import mrjake.aunis.state.State;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.tileentity.DHDTile;
import mrjake.aunis.tileentity.DHDTile.DHDUpgradeEnum;
import mrjake.aunis.tileentity.util.ScheduledTask;
import mrjake.aunis.util.AunisAxisAlignedBB;
import mrjake.aunis.util.ILinkable;
import mrjake.aunis.util.LinkingHelper;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StargateMilkyWayBaseTile extends StargateClassicBaseTile implements ILinkable {
  @Override
  public StargateSizeEnum getStargateSize() {
    return stargateSize;
  }

  // ------------------------------------------------------------------------
  // Stargate state

  @Override
  protected void disconnectGate() {
    super.disconnectGate();

    if (isLinkedAndDHDOperational()) getLinkedDHD(world).clearSymbols();
  }

  @Override
  protected void failGate() {
    super.failGate();

    if (isLinkedAndDHDOperational()) getLinkedDHD(world).clearSymbols();
  }

  @Override
  protected void addFailedTaskAndPlaySound() {
    addTask(new ScheduledTask(EnumScheduledTask.STARGATE_FAIL, stargateState.dialingComputer() ? 83 : 53));
    playSoundEvent(StargateSoundEventEnum.DIAL_FAILED);
  }


  // ------------------------------------------------------------------------
  // Stargate connection

  @Override
  public void openGate(StargatePos targetGatePos, boolean isInitiating) {
    super.openGate(targetGatePos, isInitiating);

    if (isLinkedAndDHDOperational()) {
      getLinkedDHD(world).activateSymbol(SymbolMilkyWayEnum.BRB);
    }
  }

  // ------------------------------------------------------------------------
  // Stargate Network

  @Override
  public SymbolTypeEnum getSymbolType() {
    return SymbolTypeEnum.MILKYWAY;
  }

  @Override
  protected AunisAxisAlignedBB getHorizonTeleportBox(boolean server) {
    return getStargateSizeConfig(server).teleportBox;
  }

  public void addSymbolToAddressDHD(SymbolMilkyWayEnum symbol) {
    addSymbolToAddress(symbol);
    stargateState = EnumStargateState.DIALING;

    if (stargateWillLock(symbol)) {
      isFinalActive = true;
    }

    sendSignal(null, "stargate_dhd_chevron_engaged", new Object[]{dialedAddress.size(), isFinalActive, symbol.englishName});
    addTask(new ScheduledTask(EnumScheduledTask.STARGATE_ACTIVATE_CHEVRON, 10));

    markDirty();
  }

  @Override
  protected int getMaxChevrons() {
    return isLinkedAndDHDOperational() && stargateState != EnumStargateState.DIALING_COMPUTER && !getLinkedDHD(world).hasUpgrade(DHDUpgradeEnum.CHEVRON_UPGRADE) ? 7 : 9;
  }

  @Override
  public void addSymbolToAddress(SymbolInterface symbol) {
    if (symbol.origin() && dialedAddress.size() >= 6 && dialedAddress.equals(StargateNetwork.EARTH_ADDRESS) && !network.isStargateInNetwork(StargateNetwork.EARTH_ADDRESS)) {
      if (StargateDimensionConfig.netherOverworld8thSymbol()) {
        if (dialedAddress.size() == 7 && dialedAddress.getLast() == SymbolMilkyWayEnum.SERPENSCAPUT) {
          dialedAddress.clear();
          dialedAddress.addAll(network.getLastActivatedOrlins().subList(0, 7));
        }
      } else {
        dialedAddress.clear();
        dialedAddress.addAll(network.getLastActivatedOrlins().subList(0, 6));
      }
    }

    super.addSymbolToAddress(symbol);

    if (isLinkedAndDHDOperational()) {
      getLinkedDHD(world).activateSymbol((SymbolMilkyWayEnum) symbol);
    }
  }

  @Override
  public void addSymbolToAddressManual(SymbolInterface targetSymbol, Object context) {
    stargateState = EnumStargateState.DIALING_COMPUTER;

    super.addSymbolToAddressManual(targetSymbol, context);
  }

  public void incomingWormhole(int dialedAddressSize){
    super.incomingWormhole(dialedAddressSize);

    if (isLinkedAndDHDOperational()) {
      getLinkedDHD(world).clearSymbols();
    }

    prepareGateToConnect(dialedAddressSize);
    markDirty();
  }

  // incoming animation


  public void prepareGateToConnect(int dialedAddressSize){
    if(stargateState.dialingComputer()) {
      addTask(new ScheduledTask(EnumScheduledTask.STARGATE_SPIN_FINISHED, 0));
    }

    boolean allowIncomingAnimation = AunisConfig.stargateConfig.allowIncomingAnimations;
    final int[] i = {1};
    Timer timer = new Timer();
    if(allowIncomingAnimation) {
      timer.schedule(new TimerTask() {
        public void run() {
          if (i[0] <= dialedAddressSize) {
            sendRenderingUpdate(EnumGateAction.LIGHT_UP_CHEVRONS, i[0], false);
            playSoundEvent(StargateSoundEventEnum.INCOMING);
            i[0]++;
          } else {
            //sendRenderingUpdate(EnumGateAction.LIGHT_UP_CHEVRONS, dialedAddressSize, true);
            timer.cancel();
          }
        }
      }, 0, 400);
    }
    else{
      timer.schedule(new TimerTask() {
        public void run() {
          if (i[0] <= 2) {
            sendRenderingUpdate(EnumGateAction.LIGHT_UP_CHEVRONS, dialedAddressSize, false);
            i[0]++;
          } else {
            timer.cancel();
          }
        }
      }, 0, 100);
      playSoundEvent(StargateSoundEventEnum.INCOMING);
    }
  }


  // ------------------------------------------------------------------------
  // Merging

  @Override
  public void onGateBroken() {
    super.onGateBroken();

    if (isLinked()) {
      getLinkedDHD(world).clearSymbols();
      getLinkedDHD(world).setLinkedGate(null, -1);
      setLinkedDHD(null, -1);
    }
  }

  @Override
  protected void onGateMerged() {
    super.onGateMerged();
    this.updateLinkStatus();
  }

  @Override
  public StargateAbstractMergeHelper getMergeHelper() {
    return StargateMilkyWayMergeHelper.INSTANCE;
  }


  // ------------------------------------------------------------------------
  // Linking

  private BlockPos linkedDHD = null;

  private int linkId = -1;

  @Nullable
  public DHDTile getLinkedDHD(World world) {
    if (linkedDHD == null) return null;

    return (DHDTile) world.getTileEntity(linkedDHD);
  }

  public boolean isLinked() {
    return linkedDHD != null && world.getTileEntity(linkedDHD) instanceof DHDTile;
  }

  public boolean isLinkedAndDHDOperational() {
    if (!isLinked()) return false;

    DHDTile dhdTile = getLinkedDHD(world);
    if (!dhdTile.hasControlCrystal()) return false;

    return true;
  }

  public void setLinkedDHD(BlockPos dhdPos, int linkId) {
    this.linkedDHD = dhdPos;
    this.linkId = linkId;

    markDirty();
  }

  public void updateLinkStatus() {
    BlockPos closestDhd = LinkingHelper.findClosestUnlinked(world, pos, LinkingHelper.getDhdRange(), AunisBlocks.DHD_BLOCK, this.getLinkId());
    int linkId = LinkingHelper.getLinkId();

    if (closestDhd != null) {
      DHDTile dhdTile = (DHDTile) world.getTileEntity(closestDhd);

      dhdTile.setLinkedGate(pos, linkId);
      setLinkedDHD(closestDhd, linkId);
      markDirty();
    }
  }

  @Override
  public boolean canLinkTo() {
    return isMerged() && !isLinked();
  }

  @Override
  public int getLinkId() {
    return linkId;
  }

  // ------------------------------------------------------------------------
  // NBT

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    if (isLinked()) {
      compound.setLong("linkedDHD", linkedDHD.toLong());
      compound.setInteger("linkId", linkId);
    }

    compound.setInteger("stargateSize", stargateSize.id);

    return super.writeToNBT(compound);
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    if (compound.hasKey("linkedDHD")) this.linkedDHD = BlockPos.fromLong(compound.getLong("linkedDHD"));
    if (compound.hasKey("linkId")) this.linkId = compound.getInteger("linkId");

    if (compound.hasKey("patternVersion")) stargateSize = StargateSizeEnum.SMALL;
    else {
      if (compound.hasKey("stargateSize")) stargateSize = StargateSizeEnum.fromId(compound.getInteger("stargateSize"));
      else stargateSize = StargateSizeEnum.LARGE;
    }

    super.readFromNBT(compound);
  }

  @Override
  public boolean prepare(ICommandSender sender, ICommand command) {
    setLinkedDHD(null, -1);

    return super.prepare(sender, command);
  }


  // ------------------------------------------------------------------------
  // Sounds

  @Override
  protected SoundPositionedEnum getPositionedSound(StargateSoundPositionedEnum soundEnum) {
    switch (soundEnum) {
      case GATE_RING_ROLL:
        return SoundPositionedEnum.MILKYWAY_RING_ROLL;
    }

    return null;
  }

  @Override
  protected SoundEventEnum getSoundEvent(StargateSoundEventEnum soundEnum) {
    switch (soundEnum) {
      case OPEN:
        return SoundEventEnum.GATE_MILKYWAY_OPEN;
      case CLOSE:
        return SoundEventEnum.GATE_MILKYWAY_CLOSE;
      case DIAL_FAILED:
        return stargateState.dialingComputer() ? SoundEventEnum.GATE_MILKYWAY_DIAL_FAILED_COMPUTER : SoundEventEnum.GATE_MILKYWAY_DIAL_FAILED;
      case INCOMING:
        return SoundEventEnum.GATE_MILKYWAY_INCOMING;
      case CHEVRON_OPEN:
        return SoundEventEnum.GATE_MILKYWAY_CHEVRON_OPEN;
      case CHEVRON_SHUT:
        return SoundEventEnum.GATE_MILKYWAY_CHEVRON_SHUT;
    }

    return null;
  }


  // ------------------------------------------------------------------------
  // Ticking and loading

  @Override
  public BlockPos getGateCenterPos() {
    return pos.offset(EnumFacing.UP, 4);
  }

  private BlockPos lastPos = BlockPos.ORIGIN;

  @Override
  protected boolean onGateMergeRequested() {
    if (stargateSize != AunisConfig.stargateSize) {
      StargateMilkyWayMergeHelper.INSTANCE.convertToPattern(world, pos, facing, stargateSize, AunisConfig.stargateSize);
      stargateSize = AunisConfig.stargateSize;
    }

    return StargateMilkyWayMergeHelper.INSTANCE.checkBlocks(world, pos, facing);
  }

  @Override
  public void update() {
    super.update();

    if (!world.isRemote) {
      if (!lastPos.equals(pos)) {
        lastPos = pos;

        updateLinkStatus();
        markDirty();
      }
    }
  }

  public static final EnumSet<BiomeOverlayEnum> SUPPORTED_OVERLAYS = EnumSet.of(BiomeOverlayEnum.NORMAL, BiomeOverlayEnum.FROST, BiomeOverlayEnum.MOSSY, BiomeOverlayEnum.AGED, BiomeOverlayEnum.SOOTY);

  @Override
  public EnumSet<BiomeOverlayEnum> getSupportedOverlays() {
    return SUPPORTED_OVERLAYS;
  }

  // ------------------------------------------------------------------------
  // Killing and block vaporizing

  @Override
  protected AunisAxisAlignedBB getHorizonKillingBox(boolean server) {
    return getStargateSizeConfig(server).killingBox;
  }

  @Override
  protected int getHorizonSegmentCount(boolean server) {
    return getStargateSizeConfig(server).horizonSegmentCount;
  }

  @Override
  protected List<AunisAxisAlignedBB> getGateVaporizingBoxes(boolean server) {
    return getStargateSizeConfig(server).gateVaporizingBoxes;
  }


  // ------------------------------------------------------------------------
  // Rendering

  private StargateSizeEnum stargateSize = AunisConfig.stargateSize;

  /**
   * Returns stargate state either from config or from client's state.
   * THIS IS NOT A GETTER OF stargateSize.
   *
   * @param server Is the code running on server
   *
   * @return Stargate's size
   */
  private StargateSizeEnum getStargateSizeConfig(boolean server) {
    return server ? AunisConfig.stargateSize : getRendererStateClient().stargateSize;
  }

  @Override
  protected StargateMilkyWayRendererStateBuilder getRendererStateServer() {
    return new StargateMilkyWayRendererStateBuilder(super.getRendererStateServer()).setStargateSize(stargateSize);
  }

  @Override
  protected StargateAbstractRendererState createRendererStateClient() {
    return new StargateMilkyWayRendererState();
  }

  @Override
  public StargateMilkyWayRendererState getRendererStateClient() {
    return (StargateMilkyWayRendererState) super.getRendererStateClient();
  }


  // -----------------------------------------------------------------
  // States

  @Override
  public State createState(StateTypeEnum stateType) {
    switch (stateType) {


      default:
        return super.createState(stateType);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void setState(StateTypeEnum stateType, State state) {
    switch (stateType) {
      case RENDERER_UPDATE:
        StargateRendererActionState gateActionState = (StargateRendererActionState) state;

        switch (gateActionState.action) {
          case CHEVRON_OPEN:
            getRendererStateClient().openChevron(world.getTotalWorldTime());
            break;

          case CHEVRON_CLOSE:
            getRendererStateClient().closeChevron(world.getTotalWorldTime());
            break;

          default:
            break;
        }

        break;

      default:
        break;
    }

    super.setState(stateType, state);
  }


  // -----------------------------------------------------------------
  // Scheduled tasks

  @Override
  public void executeTask(EnumScheduledTask scheduledTask, NBTTagCompound customData) {
    switch (scheduledTask) {
      case STARGATE_ACTIVATE_CHEVRON:
        stargateState = EnumStargateState.IDLE;
        markDirty();

        playSoundEvent(StargateSoundEventEnum.CHEVRON_OPEN);
        sendRenderingUpdate(EnumGateAction.CHEVRON_ACTIVATE, -1, isFinalActive);
        updateChevronLight(dialedAddress.size(), isFinalActive);
        //			AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.RENDERER_UPDATE, new StargateRendererActionState(EnumGateAction.CHEVRON_ACTIVATE, -1, customData.getBoolean("final"))), targetPoint);
        break;

      case STARGATE_SPIN_FINISHED:
        if(!super.dialingAborted) addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_OPEN, 7));
        else super.dialingAborted = false;

        break;

      case STARGATE_CHEVRON_OPEN:
        playSoundEvent(StargateSoundEventEnum.CHEVRON_OPEN);
        sendRenderingUpdate(EnumGateAction.CHEVRON_OPEN, 0, false);

        if (canAddSymbol(targetRingSymbol)) {
          addSymbolToAddress(targetRingSymbol);

          if (stargateWillLock(targetRingSymbol)) {
            if (checkAddressAndEnergy(dialedAddress).ok()) {
              addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_OPEN_SECOND, 7));
            } else addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_FAIL, 70));
          } else addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_OPEN_SECOND, 7));
        } else addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_FAIL, 70));

        break;

      case STARGATE_CHEVRON_OPEN_SECOND:
        playSoundEvent(StargateSoundEventEnum.CHEVRON_OPEN);
        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_LIGHT_UP, 3));

        break;

      case STARGATE_CHEVRON_LIGHT_UP:
        if (stargateWillLock(targetRingSymbol)) sendRenderingUpdate(EnumGateAction.CHEVRON_ACTIVATE, 0, true);
        else sendRenderingUpdate(EnumGateAction.CHEVRON_ACTIVATE_BOTH, 0, false);

        updateChevronLight(dialedAddress.size(), isFinalActive);

        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_CLOSE, 10));

        break;

      case STARGATE_CHEVRON_CLOSE:
        playSoundEvent(StargateSoundEventEnum.CHEVRON_SHUT);
        sendRenderingUpdate(EnumGateAction.CHEVRON_CLOSE, 0, false);

        if (stargateWillLock(targetRingSymbol)) {
          stargateState = EnumStargateState.IDLE;
          sendSignal(ringSpinContext, "stargate_spin_chevron_engaged", new Object[]{dialedAddress.size(), true, targetRingSymbol.getEnglishName()});
        } else addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_DIM, 10));

        break;

      case STARGATE_CHEVRON_DIM:
        sendRenderingUpdate(EnumGateAction.CHEVRON_DIM, 0, false);
        stargateState = EnumStargateState.IDLE;

        sendSignal(ringSpinContext, "stargate_spin_chevron_engaged", new Object[]{dialedAddress.size(), false, targetRingSymbol.getEnglishName()});

        break;

      case STARGATE_CHEVRON_FAIL:
        sendRenderingUpdate(EnumGateAction.CHEVRON_CLOSE, 0, false);
        dialingFailed(checkAddressAndEnergy(dialedAddress));

        break;

      default:
        break;
    }

    super.executeTask(scheduledTask, customData);
  }

  @Override
  public int getSupportedCapacitors() {
    return 3;
  }
}
