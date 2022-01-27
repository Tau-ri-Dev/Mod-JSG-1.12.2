package mrjake.aunis.tileentity.stargate;

import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.config.StargateSizeEnum;
import mrjake.aunis.packet.AunisPacketHandler;
import mrjake.aunis.packet.StateUpdatePacketToClient;
import mrjake.aunis.renderer.biomes.BiomeOverlayEnum;
import mrjake.aunis.renderer.stargate.StargateClassicRendererState.StargateClassicRendererStateBuilder;
import mrjake.aunis.renderer.stargate.StargateUniverseRendererState;
import mrjake.aunis.renderer.stargate.StargateUniverseRendererState.StargateUniverseRendererStateBuilder;
import mrjake.aunis.sound.*;
import mrjake.aunis.stargate.*;
import mrjake.aunis.stargate.merging.StargateAbstractMergeHelper;
import mrjake.aunis.stargate.merging.StargateUniverseMergeHelper;
import mrjake.aunis.stargate.network.*;
import mrjake.aunis.stargate.power.StargateEnergyRequired;
import mrjake.aunis.state.stargate.StargateRendererActionState;
import mrjake.aunis.state.stargate.StargateRendererActionState.EnumGateAction;
import mrjake.aunis.state.stargate.StargateUniverseSymbolState;
import mrjake.aunis.state.State;
import mrjake.aunis.state.StateTypeEnum;
import mrjake.aunis.tileentity.util.ScheduledTask;
import mrjake.aunis.util.AunisAxisAlignedBB;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.*;

import static mrjake.aunis.stargate.network.SymbolUniverseEnum.G37;
import static mrjake.aunis.stargate.network.SymbolUniverseEnum.TOP_CHEVRON;

public class StargateUniverseBaseTile extends StargateClassicBaseTile {

  private long actionsCooldown = 0; //ticks

  public void setUpCooldown(){
    actionsCooldown = 60;
  }


  @Override
  public StargateSizeEnum getStargateSize() {
    return StargateSizeEnum.SMALL;
  }

  // --------------------------------------------------------------------------------
  // Dialing

  public StargateAddress addressToDial;
  private int addressPosition;
  private int maxSymbols;
  private boolean abortDialing;
  private boolean dialingNearby = false;
  private double entryDelay = 0;
  public boolean isFastDialing = false;

  public void dial(StargateAddress stargateAddress, int glyphsToDial, boolean nearby) {
    if(actionsCooldown > 1) return;
    setUpCooldown();
    addAddressToDial(stargateAddress);

    /*addressToDial = stargateAddress;
    addressPosition = 0;
    maxSymbols = glyphsToDial;
    abortDialing = false;
    isFastDialing = false;
    dialingNearby = nearby;

    stargateState = EnumStargateState.DIALING;

    AunisSoundHelper.playSoundEvent(world, getGateCenterPos(), SoundEventEnum.GATE_UNIVERSE_DIAL_START);
    addTask(new ScheduledTask(EnumScheduledTask.STARGATE_DIAL_NEXT, 35));
    sendRenderingUpdate(EnumGateAction.LIGHT_UP_CHEVRONS, 9, true);
    updateChevronLight(9, true);

    markDirty();
    */
  }

  public void abort(boolean force) {
    if(actionsCooldown > 1 && !force) return;
    setUpCooldown();
    //abortDialingSequence(1);
    abortDialing = true;
    entryDelay = 0;
    isFastDialing = false;
    markDirty();
  }

  public void abort() {
    abort(true);
  }

  @Override
  public void failGate() {
    super.failGate();
    entryDelay = 0;
    isFastDialing = false;
    markDirty();

    if (targetRingSymbol != TOP_CHEVRON)
      addSymbolToAddressManual(TOP_CHEVRON, null);
  }

  @Override
  protected void disconnectGate() {
    super.disconnectGate();
    entryDelay = 0;
    isFastDialing = false;

    /*if (targetRingSymbol != TOP_CHEVRON)
      addSymbolToAddressManual(TOP_CHEVRON, null);*/
    addSymbolToAddressManual(TOP_CHEVRON, null);
    markDirty();
  }

  @Override
  public void addSymbolToAddressManual(SymbolInterface targetSymbol, Object context) {
    if (context != null) stargateState = EnumStargateState.DIALING_COMPUTER;
    else stargateState = EnumStargateState.DIALING;

    if (stargateState.dialingComputer() && dialedAddress.size() == 0 && !targetSymbol.equals(G37)) {
      AunisSoundHelper.playSoundEvent(world, getGateCenterPos(), SoundEventEnum.GATE_UNIVERSE_DIAL_START);
      sendRenderingUpdate(EnumGateAction.LIGHT_UP_CHEVRONS, 9, true);

      NBTTagCompound taskData = new NBTTagCompound();
      taskData.setInteger("symbolToDial", targetSymbol.getId());
      addTask(new ScheduledTask(EnumScheduledTask.STARGATE_DIAL_NEXT, 35, taskData));
      ringSpinContext = context;
    } else super.addSymbolToAddressManual(targetSymbol, context);
  }

  @Override
  public void update(){
    if(actionsCooldown > 0)
      actionsCooldown--;
    super.update();
  }

  public void addSymbolToAddressFast(SymbolInterface symbol) {
    targetRingSymbol = symbol;
    int delay = (int) ((entryDelay-20 > 10) ? Math.round(entryDelay-20) : Math.round(entryDelay));
    addTask(new ScheduledTask(EnumScheduledTask.STARGATE_SPIN_FINISHED, delay));
    markDirty();
  }

  // todo(Mine): something here starts loop and ends after gate stops spinning
  public void addAddressToDial(StargateAddress address) {
    addressToDial = address;
    StargateAddressDynamic addressV2 = new StargateAddressDynamic(SymbolTypeEnum.UNIVERSE);
    addressV2.clear();
    addressV2.addAll(address);
    if(!canDialAddress(addressV2)){
      AunisSoundHelper.playSoundEvent(world, getGateCenterPos(), SoundEventEnum.GATE_UNIVERSE_DIAL_FAILED);
      addressToDial = null;
      markDirty();
      return;
    }
    addressPosition = 0;
    maxSymbols = addressToDial.getSize();
    abortDialing = false;
    dialingNearby = false;
    isFastDialing = true;
    stargateState = EnumStargateState.DIALING_COMPUTER;

    if (dialedAddress.size() == 0) {
      AunisSoundHelper.playSoundEvent(world, getGateCenterPos(), SoundEventEnum.GATE_UNIVERSE_DIAL_START);
      sendRenderingUpdate(EnumGateAction.LIGHT_UP_CHEVRONS, 9, true);
      super.addSymbolToAddressManual(G37, null);
      int duration = StargateClassicSpinHelper.getAnimationDuration(360);
      entryDelay = ((double) duration/maxSymbols) - 40;
      addTask(new ScheduledTask(EnumScheduledTask.STARGATE_DIAL_NEXT, 15, null));
      ringSpinContext = null;
      StargateAbstractBaseTile targetTile = Objects.requireNonNull(network.getStargate(address)).getTileEntity();
      if(targetTile != null){
        targetTile.isIncoming = true;
        targetTile.markDirty();
        targetTile.incomingWormhole(maxSymbols, (int) Math.round(entryDelay));
      }

    }

    markDirty();
  }

  @Override
  public void incomingWormhole(int dialedAddressSize){
    prepareGateToConnect(dialedAddressSize, 10);

    super.incomingWormhole(9);
  }

  @Override
  public void incomingWormhole(int dialedAddressSize, int time){
    prepareGateToConnect(dialedAddressSize, time);

    super.incomingWormhole(9, false);
  }

  public void prepareGateToConnect(int dialedAddressSize, int time){
    time = time * dialedAddressSize;
    int period = time - 2000;
    if(period < 0) period = 0;
    this.stargateState = EnumStargateState.INCOMING;
    sendRenderingUpdate(EnumGateAction.CLEAR_CHEVRONS, 9, true);
    // do spin animation

    final int[] i = {1};
    Timer timer = new Timer();

    if(((time/1000)*20)+40 >= StargateClassicSpinHelper.getAnimationDuration(360)){
      timer.schedule(new TimerTask() {
        public void run() {
          if (irisMode == EnumIrisMode.AUTO && isOpened() && isIncoming) {
            toggleIris();
            isIncoming = false;
          }
          if(!isIncoming){
            stargateState = EnumStargateState.IDLE;
            markDirty();
          }
          timer.cancel();
        }
      }, period, 100);

      addSymbolToAddressManual(G37, null);

      sendRenderingUpdate(EnumGateAction.LIGHT_UP_CHEVRONS, 9, true);
      sendSignal(null, "stargate_incoming_wormhole", new Object[]{dialedAddressSize});
      playSoundEvent(StargateSoundEventEnum.INCOMING);

    }
    else {

      timer.schedule(new TimerTask() {
        public void run() {
          if (isIncoming){
            if (irisMode == EnumIrisMode.AUTO && isOpened()) {
              toggleIris();
            }
            sendRenderingUpdate(EnumGateAction.LIGHT_UP_CHEVRONS, 9, true);
            sendSignal(null, "stargate_incoming_wormhole", new Object[]{dialedAddressSize});
            playSoundEvent(StargateSoundEventEnum.INCOMING);
          }
          isIncoming = false;
          markDirty();
          timer.cancel();
        }
      }, period, 100);

    }
  }

  @Override
  protected boolean onGateMergeRequested() {
    return StargateUniverseMergeHelper.INSTANCE.checkBlocks(world, pos, facing);
  }

  @Override
  protected void addFailedTaskAndPlaySound() {
    addTask(new ScheduledTask(EnumScheduledTask.STARGATE_FAIL, 20));
    addTask(new ScheduledTask(EnumScheduledTask.STARGATE_FAILED_SOUND, 20));
  }

  @Override
  protected int getMaxChevrons() {
    return 9;
  }

  @Override
  protected int getOpenSoundDelay() {
    return super.getOpenSoundDelay() + 10;
  }

  public static final EnumSet<BiomeOverlayEnum> SUPPORTED_OVERLAYS = EnumSet.of(BiomeOverlayEnum.NORMAL, BiomeOverlayEnum.FROST, BiomeOverlayEnum.MOSSY, BiomeOverlayEnum.AGED);

  @Override
  public EnumSet<BiomeOverlayEnum> getSupportedOverlays() {
    return SUPPORTED_OVERLAYS;
  }

  // --------------------------------------------------------------------------------
  // Scheduled tasks

  @Override
  public void executeTask(EnumScheduledTask scheduledTask, NBTTagCompound customData) {
    switch (scheduledTask) {
      case STARGATE_DIAL_NEXT:
        if (customData != null && customData.hasKey("symbolToDial"))
          super.addSymbolToAddressManual(getSymbolType().valueOfSymbol(customData.getInteger("symbolToDial")), ringSpinContext);
        else if (!isFastDialing)
          addSymbolToAddressManual(addressPosition >= maxSymbols ? getSymbolType().getOrigin() : addressToDial.get(addressPosition), null);
        else if (isFastDialing)
          addSymbolToAddressFast(addressPosition >= maxSymbols ? getSymbolType().getOrigin() : addressToDial.get(addressPosition));

        addressPosition++;

        break;

      case STARGATE_SPIN_FINISHED:
        if (targetRingSymbol != TOP_CHEVRON) {
          if(targetRingSymbol != G37) {
            if (canAddSymbol(targetRingSymbol) && !abortDialing) {
              addSymbolToAddress(targetRingSymbol);
              activateSymbolServer(targetRingSymbol);

              if (stargateState.dialingComputer() && !isFastDialing) {
                stargateState = EnumStargateState.IDLE;
                addTask(new ScheduledTask(EnumScheduledTask.STARGATE_DIAL_FINISHED, 10));
              } else {
                if (!stargateWillLock(targetRingSymbol)) {
                  if(isFastDialing) addTask(new ScheduledTask(EnumScheduledTask.STARGATE_DIAL_FINISHED, 5));
                  addTask(new ScheduledTask(EnumScheduledTask.STARGATE_DIAL_NEXT, isFastDialing ? 2 : 14));
                }
                else {
                  if(isFastDialing)
                    currentRingSymbol = TOP_CHEVRON;
                  isFastDialing = false;
                  if(!dialedAddress.contains(SymbolUniverseEnum.G17))
                    this.dialedAddress.addSymbol(SymbolUniverseEnum.G17);
                  markDirty();
                  attemptOpenAndFail();
                }
              }
            } else {
              dialingFailed(abortDialing ? StargateOpenResult.ABORTED : StargateOpenResult.ADDRESS_MALFORMED);

              stargateState = EnumStargateState.IDLE;
              abortDialing = false;
            }
          }
        }
        else if(abortDialing){
          dialingFailed(StargateOpenResult.ABORTED);

          stargateState = EnumStargateState.IDLE;
          abortDialing = false;
        }
        else stargateState = EnumStargateState.IDLE;

        markDirty();
        break;

      case STARGATE_FAILED_SOUND:
        playSoundEvent(StargateSoundEventEnum.DIAL_FAILED);
        break;

      case STARGATE_DIAL_FINISHED:
        sendSignal(ringSpinContext, "stargate_spin_chevron_engaged", new Object[]{dialedAddress.size(), stargateWillLock(targetRingSymbol), targetRingSymbol.getEnglishName()});
        break;

      default:
        break;
    }

    super.executeTask(scheduledTask, customData);
  }

  private void activateSymbolServer(SymbolInterface symbol) {
    AunisPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, StateTypeEnum.STARGATE_UNIVERSE_ACTIVATE_SYMBOL, new StargateUniverseSymbolState((SymbolUniverseEnum) symbol, false)), targetPoint);
  }

  // --------------------------------------------------------------------------------
  // States

  @Override
  public State createState(StateTypeEnum stateType) {
    switch (stateType) {
      case STARGATE_UNIVERSE_ACTIVATE_SYMBOL:
        return new StargateUniverseSymbolState();

      default:
        return super.createState(stateType);
    }
  }

  @Override
  public void setState(StateTypeEnum stateType, State state) {
    switch (stateType) {
      case STARGATE_UNIVERSE_ACTIVATE_SYMBOL:
        StargateUniverseSymbolState symbolState = (StargateUniverseSymbolState) state;

        if (symbolState.dimAll) getRendererStateClient().clearSymbols(world.getTotalWorldTime());
        else getRendererStateClient().activateSymbol(world.getTotalWorldTime(), symbolState.symbol);

        break;

      case RENDERER_UPDATE:
        StargateRendererActionState gateActionState = (StargateRendererActionState) state;

        switch (gateActionState.action) {
          case CLEAR_CHEVRONS:
            getRendererStateClient().clearSymbols(world.getTotalWorldTime());
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

  // --------------------------------------------------------------------------------
  // NBT

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    compound.setInteger("addressPosition", addressPosition);

    if (addressToDial != null) compound.setTag("addressToDial", addressToDial.serializeNBT());

    compound.setInteger("maxSymbols", maxSymbols);
    compound.setBoolean("abortDialing", abortDialing);
    compound.setBoolean("dialingNearby", dialingNearby);

    compound.setLong("actionsCooldown", actionsCooldown);

    return super.writeToNBT(compound);
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);

    addressPosition = compound.getInteger("addressPosition");
    addressToDial = new StargateAddress(compound.getCompoundTag("addressToDial"));
    maxSymbols = compound.getInteger("maxSymbols");
    abortDialing = compound.getBoolean("abortDialing");
    dialingNearby = compound.getBoolean("dialingNearby");

    actionsCooldown = compound.getLong("actionsCooldown");
  }


  // --------------------------------------------------------------------------------
  // Stargate Network

  @Override
  public SymbolTypeEnum getSymbolType() {
    return SymbolTypeEnum.UNIVERSE;
  }

  @Override
  protected StargateEnergyRequired getEnergyRequiredToDial(StargatePos targetGatePos) {
    return super.getEnergyRequiredToDial(targetGatePos).mul(AunisConfig.powerConfig.stargateUniverseEnergyMul);
  }

  @Override
  protected boolean checkAddressLength(StargateAddressDynamic address, StargatePos targetGatePosition) {
    return super.checkAddressLength(address, targetGatePosition);
  }

  // --------------------------------------------------------------------------------
  // Teleportation

  @Override
  protected AunisAxisAlignedBB getHorizonTeleportBox(boolean server) {
    return StargateSizeEnum.SMALL.teleportBox;
  }

  @Override
  public BlockPos getGateCenterPos() {
    return pos.up(4);
  }

  @Override
  protected AunisAxisAlignedBB getHorizonKillingBox(boolean server) {
    return StargateSizeEnum.SMALL.killingBox;
  }

  @Override
  protected int getHorizonSegmentCount(boolean server) {
    return StargateSizeEnum.SMALL.horizonSegmentCount;
  }

  @Override
  protected List<AunisAxisAlignedBB> getGateVaporizingBoxes(boolean server) {
    return StargateSizeEnum.SMALL.gateVaporizingBoxes;
  }


  // --------------------------------------------------------------------------------
  // Sounds

  @Override
  protected SoundPositionedEnum getPositionedSound(StargateSoundPositionedEnum soundEnum) {
    switch (soundEnum) {
      case GATE_RING_ROLL:
        return SoundPositionedEnum.UNIVERSE_RING_ROLL;
    }

    return null;
  }

  @Override
  protected SoundEventEnum getSoundEvent(StargateSoundEventEnum soundEnum) {
    switch (soundEnum) {
      case OPEN:
        return SoundEventEnum.GATE_UNIVERSE_OPEN;
      case CLOSE:
        return SoundEventEnum.GATE_UNIVERSE_CLOSE;
      case DIAL_FAILED:
        return SoundEventEnum.GATE_UNIVERSE_DIAL_FAILED;
      case INCOMING:
        return SoundEventEnum.GATE_UNIVERSE_DIAL_START;
      case CHEVRON_OPEN:
        return SoundEventEnum.GATE_MILKYWAY_CHEVRON_OPEN;
      case CHEVRON_SHUT:
        return targetRingSymbol == TOP_CHEVRON ? SoundEventEnum.GATE_UNIVERSE_CHEVRON_TOP_LOCK : SoundEventEnum.GATE_UNIVERSE_CHEVRON_LOCK;
    }

    return null;
  }


  // --------------------------------------------------------------------------------
  // Merging

  @Override
  public StargateAbstractMergeHelper getMergeHelper() {
    return StargateUniverseMergeHelper.INSTANCE;
  }


  // --------------------------------------------------------------------------------
  // Renderer states

  @Override
  protected StargateClassicRendererStateBuilder getRendererStateServer() {
    return new StargateUniverseRendererStateBuilder(super.getRendererStateServer()).setDialedAddress((stargateState.initiating() || stargateState.dialing()) ? dialedAddress : new StargateAddressDynamic(getSymbolType())).setActiveChevrons(stargateState.idle() ? 0 : 9);
  }

  @Override
  protected StargateUniverseRendererState createRendererStateClient() {
    return new StargateUniverseRendererState();
  }

  @Override
  public StargateUniverseRendererState getRendererStateClient() {
    return (StargateUniverseRendererState) super.getRendererStateClient();
  }

  @Override
  public int getSupportedCapacitors() {
    return AunisConfig.powerConfig.universeCapacitors;
  }
}
