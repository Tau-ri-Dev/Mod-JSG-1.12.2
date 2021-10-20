package mrjake.aunis.tileentity.stargate;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.config.StargateDimensionConfig;
import mrjake.aunis.config.StargateSizeEnum;
import mrjake.aunis.gui.container.StargateContainerGuiUpdate;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.stargate.ChevronEnum;
import mrjake.aunis.renderer.stargate.StargateAbstractRendererState;
import mrjake.aunis.renderer.stargate.StargatePegasusRendererState;
import mrjake.aunis.sound.SoundEventEnum;
import mrjake.aunis.sound.SoundPositionedEnum;
import mrjake.aunis.sound.StargateSoundEventEnum;
import mrjake.aunis.sound.StargateSoundPositionedEnum;
import mrjake.aunis.stargate.EnumScheduledTask;
import mrjake.aunis.stargate.EnumSpinDirection;
import mrjake.aunis.stargate.EnumStargateState;
import mrjake.aunis.stargate.merging.StargateAbstractMergeHelper;
import mrjake.aunis.stargate.merging.StargatePegasusMergeHelper;
import mrjake.aunis.stargate.network.*;
import mrjake.aunis.state.*;
import mrjake.aunis.state.StargateRendererActionState.EnumGateAction;
import mrjake.aunis.tileentity.DHDPegasusTile;
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

public class StargatePegasusBaseTile extends StargateClassicBaseTile implements ILinkable {
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
    if(stargateState == EnumStargateState.DIALING || stargateState == EnumStargateState.DIALING_COMPUTER || stargateState == EnumStargateState.IDLE ) {
      addTask(new ScheduledTask(EnumScheduledTask.STARGATE_FAIL, stargateState.dialingComputer() ? 83 : 53));
      playSoundEvent(StargateSoundEventEnum.DIAL_FAILED);
    }
  }


  // ------------------------------------------------------------------------
  // Stargate connection

  @Override
  public void openGate(StargatePos targetGatePos, boolean isInitiating) {
    super.openGate(targetGatePos, isInitiating);

    if (isLinkedAndDHDOperational()) {
      getLinkedDHD(world).activateSymbol(SymbolPegasusEnum.BRB);
    }
  }

  // ------------------------------------------------------------------------
  // Stargate Network

  @Override
  public SymbolTypeEnum getSymbolType() {
    return SymbolTypeEnum.PEGASUS;
  }

  @Override
  protected AunisAxisAlignedBB getHorizonTeleportBox(boolean server) {
    return getStargateSizeConfig(server).teleportBox;
  }

  @Override
  protected int getMaxChevrons() {
    return isLinkedAndDHDOperational() && stargateState != EnumStargateState.DIALING_COMPUTER && !getLinkedDHD(world).hasUpgrade(DHDPegasusTile.DHDUpgradeEnum.CHEVRON_UPGRADE) ? 7 : 9;
  }

  @Override
  public void addSymbolToAddress(SymbolInterface symbol) {

    if (isLinkedAndDHDOperational()) {
      getLinkedDHD(world).activateSymbol((SymbolPegasusEnum) symbol);
    }

    if (symbol.origin() && dialedAddress.size() >= 6 && dialedAddress.equals(StargateNetwork.EARTH_ADDRESS) && !network.isStargateInNetwork(StargateNetwork.EARTH_ADDRESS)) {
      if (StargateDimensionConfig.netherOverworld8thSymbol()) {
        if (dialedAddress.size() == 7 && dialedAddress.getLast() == SymbolPegasusEnum.SUBIDO) {
          dialedAddress.clear();
          dialedAddress.addAll(network.getLastActivatedOrlins().subList(0, 7));
        }
      } else {
        dialedAddress.clear();
        dialedAddress.addAll(network.getLastActivatedOrlins().subList(0, 6));
      }
    }

    super.addSymbolToAddress(symbol);
  }


  public void incomingWormhole(int dialedAddressSize){
    super.incomingWormhole(dialedAddressSize);

    if (isLinkedAndDHDOperational()) {
      getLinkedDHD(world).clearSymbols();
    }

    prepareGateToConnect(dialedAddressSize);
  }


  public void prepareGateToConnect(int dialedAddressSize){
    // --- do spin animation ---

    if(stargateState.dialingComputer()) {
      addTask(new ScheduledTask(EnumScheduledTask.STARGATE_SPIN_FINISHED, 0));
    }

    boolean allowIncomingAnimation = AunisConfig.stargateConfig.allowIncomingAnimations;

    if(allowIncomingAnimation) {
      playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, true);
      addTask(new ScheduledTask(EnumScheduledTask.STARGATE_SPIN_FINISHED, 80));
/*
      spinDirection = EnumSpinDirection.COUNTER_CLOCKWISE;


      ChevronEnum targetChevron = ChevronEnum.C1;

      ChevronEnum currentChevron = ChevronEnum.C7;

      int indexDiff = slotFromChevron(currentChevron) - slotFromChevron(targetChevron);

      float distance = (float) Math.abs(indexDiff);
      if (distance <= 20) distance += 36;



      float distance = 360;
      int duration = (int) (distance);



      AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.SPIN_STATE, new StargateSpinState(SymbolPegasusEnum.AAXEL, spinDirection, false)), targetPoint);
      addTask(new ScheduledTask(EnumScheduledTask.STARGATE_SPIN_FINISHED, duration - 1));
      addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_OPEN, duration - 1));
      addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_DIM, duration - 1));

      isSpinning = true;
      spinStartTime = world.getTotalWorldTime();
      markDirty();
*/

      // ----------

      final int[] i = {1};
      Timer timer = new Timer();
      timer.schedule(new TimerTask() {
        public void run() {
          if (i[0] <= dialedAddressSize) {
            sendRenderingUpdate(EnumGateAction.LIGHT_UP_CHEVRONS, i[0], false);
            playSoundEvent(StargateSoundEventEnum.CHEVRON_OPEN);
            i[0]++;
          } else {
            timer.cancel();
          }
        }
      }, 0, 400);
    }
    else{
      final int[] i = {1};
      Timer timer = new Timer();
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
      playSoundEvent(StargateSoundEventEnum.CHEVRON_OPEN);
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
    return StargatePegasusMergeHelper.INSTANCE;
  }


  // ------------------------------------------------------------------------
  // Linking

  private BlockPos linkedDHD = null;

  private int linkId = -1;

  @Override
  public boolean canLinkTo() {
    return isMerged() && !isLinked();
  }

  @Override
  public int getLinkId() {
    return linkId;
  }

  @Nullable
  public DHDPegasusTile getLinkedDHD(World world) {
    if (linkedDHD == null) return null;

    return (DHDPegasusTile) world.getTileEntity(linkedDHD);
  }

  public boolean isLinked() {
    return linkedDHD != null && world.getTileEntity(linkedDHD) instanceof DHDPegasusTile;
  }

  public boolean isLinkedAndDHDOperational() {
    if (!isLinked()) return false;

    DHDPegasusTile dhdTile = getLinkedDHD(world);
    if (!dhdTile.hasControlCrystal()) return false;

    return true;
  }

  public void setLinkedDHD(BlockPos dhdPos, int linkId) {
    this.linkedDHD = dhdPos;
    this.linkId = linkId;

    markDirty();
  }

  public void updateLinkStatus() {
    BlockPos closestDhd = LinkingHelper.findClosestUnlinked(world, pos, LinkingHelper.getDhdRange(), AunisBlocks.DHD_PEGASUS_BLOCK, this.getLinkId());
    int linkId = LinkingHelper.getLinkId();

    if (closestDhd != null) {
      DHDPegasusTile dhdTile = (DHDPegasusTile) world.getTileEntity(closestDhd);

      dhdTile.setLinkedGate(pos, linkId);
      setLinkedDHD(closestDhd, linkId);
      markDirty();
    }
  }

  // ------------------------------------------------------------------------
  // NBT

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
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
        return SoundPositionedEnum.PEGASUS_RING_ROLL;
    }

    return null;
  }

  @Override
  protected SoundEventEnum getSoundEvent(StargateSoundEventEnum soundEnum) {
    switch (soundEnum) {
      case OPEN:
        return SoundEventEnum.GATE_PEGASUS_OPEN;
      case CLOSE:
        return SoundEventEnum.GATE_MILKYWAY_CLOSE;
      case DIAL_FAILED:
        return stargateState.dialingComputer() ? SoundEventEnum.GATE_PEGASUS_DIAL_FAILED : SoundEventEnum.GATE_PEGASUS_DIAL_FAILED;
      case INCOMING:
        return SoundEventEnum.GATE_MILKYWAY_INCOMING;
      case PEGASUS_INCOMING:
        return SoundEventEnum.GATE_PEGASUS_INCOMING;
      case CHEVRON_OPEN:
        return SoundEventEnum.GATE_PEGASUS_CHEVRON_OPEN;
      case CHEVRON_SHUT:
        return SoundEventEnum.GATE_PEGASUS_CHEVRON_SHUT;
    }

    return null;
  }


  // ------------------------------------------------------------------------
  // Ticking and loading

  @Override
  public BlockPos getGateCenterPos() {
    return pos.offset(EnumFacing.UP, 4);
  }

  @Override
  protected boolean onGateMergeRequested() {
    return StargatePegasusMergeHelper.INSTANCE.checkBlocks(world, pos, facing);
  }

  private BlockPos lastPos = BlockPos.ORIGIN;
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
  public static final EnumSet<BiomeOverlayEnum> SUPPORTED_OVERLAYS = EnumSet.of(BiomeOverlayEnum.NORMAL, BiomeOverlayEnum.FROST, BiomeOverlayEnum.MOSSY, BiomeOverlayEnum.AGED);

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
  protected StargatePegasusRendererState.StargatePegasusRendererStateBuilder getRendererStateServer() {
    return new StargatePegasusRendererState.StargatePegasusRendererStateBuilder(super.getRendererStateServer()).setStargateSize(stargateSize);
  }

  @Override
  protected StargateAbstractRendererState createRendererStateClient() {
    return new StargatePegasusRendererState();
  }

  @Override
  public StargatePegasusRendererState getRendererStateClient() {
    return (StargatePegasusRendererState) super.getRendererStateClient();
  }


  // -----------------------------------------------------------------
  // States

  @Override
  public State createState(StateTypeEnum stateType) {
    switch (stateType) {

      case RENDERER_STATE:
        return new StargatePegasusRendererState();

      case DHD_ACTIVATE_BUTTON:
        return new DHDActivateButtonPegasusState();

      case GUI_UPDATE:
        return new StargateContainerGuiUpdate();

      case BIOME_OVERRIDE_STATE:
        return new StargateBiomeOverrideState();

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

          case CHEVRON_ACTIVATE:
            getRendererStateClient().spinHelper.setIsSpinning(false);
            ChevronEnum chevron = gateActionState.modifyFinal ? ChevronEnum.getFinal() : getRendererStateClient().chevronTextureList.getNextChevron();
            getRendererStateClient().lockChevron(getRendererStateClient().spinHelper.getTargetSymbol().getId(), chevron);

            break;

          default:
            break;
        }

        break;

      case SPIN_STATE:
        if (getRendererStateClient().chevronTextureList.getNextChevron().rotationIndex == 1) {
          getRendererStateClient().slotToGlyphMap.clear();
        }

        break;

      default:
        break;
    }

    super.setState(stateType, state);
  }

  protected long getSpinStartOffset() {
    return slotFromChevron(getRendererStateClient().chevronTextureList.getCurrentChevron());
  }

  // TODO(sentialx): refactor
  public int slotFromChevron(ChevronEnum chevron) {
    return new int[]{9, 5, 1, 33, 29, 25, 21, 17, 13}[chevron.rotationIndex];
  }

  public void addSymbolToAddressDHD(SymbolInterface targetSymbol) {
    if(AunisConfig.dhdConfig.animatePegDHDDial) {
      Object context = null;
      stargateState = EnumStargateState.DIALING;

      targetRingSymbol = targetSymbol;
      spinDirection = spinDirection.opposite();

      ChevronEnum targetChevron = targetSymbol.origin() ? ChevronEnum.getFinal() : ChevronEnum.valueOf(dialedAddress.size());
      ChevronEnum currentChevron = dialedAddress.size() == 0 ? ChevronEnum.C1 : ChevronEnum.valueOf(targetChevron.index - 1);

      if (targetSymbol.origin() && dialedAddress.size() == 6) currentChevron = ChevronEnum.C6;

      int indexDiff = slotFromChevron(currentChevron) - slotFromChevron(targetChevron);

      EnumSpinDirection counterDirection = indexDiff < 0 ? EnumSpinDirection.COUNTER_CLOCKWISE : EnumSpinDirection.CLOCKWISE;

      if (spinDirection == counterDirection) {
        indexDiff = 36 - Math.abs(indexDiff);
      }

      float distance = (float) Math.abs(indexDiff);
      if (distance <= 20) distance += 36;

      int duration = (int) (distance);

      //Aunis.logger.debug("addSymbolToAddressManual: " + "current:" + currentRingSymbol + ", " + "target:" + targetSymbol + ", " + "direction:" + spinDirection + ", " + "distance:" + distance + ", " + "duration:" + duration + ", " + "moveOnly:" + moveOnly);

      AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.SPIN_STATE, new StargateSpinState(targetRingSymbol, spinDirection, false)), targetPoint);
      addTask(new ScheduledTask(EnumScheduledTask.STARGATE_SPIN_FINISHED, duration - 1));
      playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, true);

      isSpinning = true;
      spinStartTime = world.getTotalWorldTime();

      ringSpinContext = context;
      if (context != null)
        sendSignal(context, "stargate_dhd_chevron_engaged", new Object[]{dialedAddress.size(), stargateWillLock(targetRingSymbol), targetSymbol.getEnglishName()});

    }
    else{
      addSymbolToAddress(targetSymbol);
      stargateState = EnumStargateState.DIALING;

      if (stargateWillLock(targetSymbol)) {
        isFinalActive = true;
      }

      sendSignal(null, "stargate_dhd_chevron_engaged", new Object[]{dialedAddress.size(), isFinalActive, targetSymbol.getEnglishName()});
      addTask(new ScheduledTask(EnumScheduledTask.STARGATE_ACTIVATE_CHEVRON, 10));
    }
    markDirty();
  }

  @Override
  public void addSymbolToAddressManual(SymbolInterface targetSymbol, @Nullable Object context) {
    stargateState = EnumStargateState.DIALING_COMPUTER;

    targetRingSymbol = targetSymbol;

    boolean moveOnly = targetRingSymbol == currentRingSymbol;

    if (moveOnly) {
      addTask(new ScheduledTask(EnumScheduledTask.STARGATE_SPIN_FINISHED, 0));
    } else {
      spinDirection = spinDirection.opposite();

      ChevronEnum targetChevron = targetSymbol.origin() ? ChevronEnum.getFinal() : ChevronEnum.valueOf(dialedAddress.size());
      ChevronEnum currentChevron = dialedAddress.size() == 0 ? ChevronEnum.C1 : ChevronEnum.valueOf(targetChevron.index - 1);

      if (targetSymbol.origin() && dialedAddress.size() == 6) currentChevron = ChevronEnum.C6;

      int indexDiff = slotFromChevron(currentChevron) - slotFromChevron(targetChevron);

      EnumSpinDirection counterDirection = indexDiff < 0 ? EnumSpinDirection.COUNTER_CLOCKWISE : EnumSpinDirection.CLOCKWISE;

      if (spinDirection == counterDirection) {
        indexDiff = 36 - Math.abs(indexDiff);
      }

      float distance = (float) Math.abs(indexDiff);
      if (distance <= 20) distance += 36;

      int duration = (int) (distance);

      Aunis.logger.debug("addSymbolToAddressManual: " + "current:" + currentRingSymbol + ", " + "target:" + targetSymbol + ", " + "direction:" + spinDirection + ", " + "distance:" + distance + ", " + "duration:" + duration + ", " + "moveOnly:" + moveOnly);

      AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.SPIN_STATE, new StargateSpinState(targetRingSymbol, spinDirection, false)), targetPoint);
      addTask(new ScheduledTask(EnumScheduledTask.STARGATE_SPIN_FINISHED, duration - 1));
      playPositionedSound(StargateSoundPositionedEnum.GATE_RING_ROLL, true);

      isSpinning = true;
      spinStartTime = world.getTotalWorldTime();

      ringSpinContext = context;
      if (context != null)
        sendSignal(context, "stargate_spin_start", new Object[]{dialedAddress.size(), stargateWillLock(targetRingSymbol), targetSymbol.getEnglishName()});
    }

    markDirty();
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
        if(!super.dialingAborted) addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_OPEN, 0));
        else super.dialingAborted = false;

        break;

      case STARGATE_CHEVRON_OPEN:
        playSoundEvent(StargateSoundEventEnum.CHEVRON_OPEN);
        sendRenderingUpdate(EnumGateAction.CHEVRON_OPEN, 0, false);

        if (canAddSymbol(targetRingSymbol)) {
          addSymbolToAddress(targetRingSymbol);

          if (stargateWillLock(targetRingSymbol)) {
            addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_OPEN_SECOND, 0));
            if (!checkAddressAndEnergy(dialedAddress).ok())
              addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_FAIL, 60));
          } else addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_OPEN_SECOND, 0));
        } else addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_FAIL, 60));

        break;

      case STARGATE_CHEVRON_OPEN_SECOND:
        playSoundEvent(StargateSoundEventEnum.CHEVRON_OPEN);
        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_LIGHT_UP, 0));

        break;

      case STARGATE_CHEVRON_LIGHT_UP:
        if (stargateWillLock(targetRingSymbol)) sendRenderingUpdate(EnumGateAction.CHEVRON_ACTIVATE, 0, true);
        else sendRenderingUpdate(EnumGateAction.CHEVRON_ACTIVATE, 0, false);

        updateChevronLight(dialedAddress.size(), isFinalActive);

        addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_CLOSE, 1));

        break;

      case STARGATE_CHEVRON_CLOSE:
        if (stargateWillLock(targetRingSymbol)) {
          stargateState = EnumStargateState.IDLE;
          sendSignal(ringSpinContext, "stargate_spin_chevron_engaged", new Object[]{dialedAddress.size(), true, targetRingSymbol.getEnglishName()});
        } else addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_DIM, 1));

        break;

      case STARGATE_CHEVRON_DIM:
        //sendRenderingUpdate(EnumGateAction.CHEVRON_DIM, 0, false);
        stargateState = EnumStargateState.IDLE;

        sendSignal(ringSpinContext, "stargate_spin_chevron_engaged", new Object[]{dialedAddress.size(), false, targetRingSymbol.getEnglishName()});

        break;

      case STARGATE_CHEVRON_FAIL:
        sendRenderingUpdate(EnumGateAction.CHEVRON_CLOSE, 0, false);
        dialingFailed(checkAddressAndEnergy(dialedAddress));
        /*
         *
         * addTask(new ScheduledTask(EnumScheduledTask.STARGATE_ENGAGE, 0));
         * addTask(new ScheduledTask(EnumScheduledTask.STARGATE_CHEVRON_DIM, 1));
         *
         */

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
