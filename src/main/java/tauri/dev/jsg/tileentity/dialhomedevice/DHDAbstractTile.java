package tauri.dev.jsg.tileentity.dialhomedevice;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.gui.container.dhd.DHDContainerGuiUpdate;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.packet.StateUpdateRequestToServer;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.renderer.dialhomedevice.DHDAbstractRendererState;
import tauri.dev.jsg.stargate.StargateClosedReasonEnum;
import tauri.dev.jsg.stargate.StargateOpenResult;
import tauri.dev.jsg.stargate.network.SymbolInterface;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.state.dialhomedevice.DHDActivateButtonState;
import tauri.dev.jsg.state.stargate.StargateBiomeOverrideState;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.tileentity.util.IUpgradable;
import tauri.dev.jsg.tileentity.util.ReactorStateEnum;
import tauri.dev.jsg.util.EnumKeyInterface;
import tauri.dev.jsg.util.ILinkable;
import tauri.dev.jsg.util.ItemMetaPair;
import tauri.dev.jsg.util.JSGItemStackHandler;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

@Optional.InterfaceList({@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = "opencomputers"), @Optional.Interface(iface = "li.cil.oc.api.network.WirelessEndpoint", modid = "opencomputers")})
public abstract class DHDAbstractTile extends TileEntity implements ILinkable, IUpgradable, StateProviderInterface, ITickable, Environment, WirelessEndpoint {

    // ---------------------------------------------------------------------------------------------------
    // Gate linking

    public static final EnumSet<BiomeOverlayEnum> SUPPORTED_OVERLAYS = EnumSet.of(BiomeOverlayEnum.NORMAL, BiomeOverlayEnum.FROST, BiomeOverlayEnum.MOSSY, BiomeOverlayEnum.SOOTY, BiomeOverlayEnum.AGED);
    public static final List<Item> SUPPORTED_UPGRADES = Collections.singletonList(JSGItems.CRYSTAL_GLYPH_DHD);
    public static final int BIOME_OVERRIDE_SLOT = 5;
    protected final FluidTank fluidHandler = new FluidTank(new FluidStack(JSGFluids.NAQUADAH_MOLTEN_REFINED, 0), tauri.dev.jsg.config.JSGConfig.dhdConfig.fluidCapacity) {

        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            if (fluid == null) return false;

            return fluid.getFluid() == JSGFluids.NAQUADAH_MOLTEN_REFINED;
        }

        protected void onContentsChanged() {
            markDirty();
        }
    };
    public boolean isLinkedClient;
    protected DHDAbstractRendererState rendererStateClient;
    protected TargetPoint targetPoint;
    protected ReactorStateEnum reactorState = ReactorStateEnum.STANDBY;
    private BlockPos linkedGate = null;
    private int linkId = -1;
    private BlockPos lastPos = BlockPos.ORIGIN;

    // ---------------------------------------------------------------------------------------------------
    // Renderer state

    // ---------------------------------------------------------------------------------------------------
    // Loading and ticking
    private boolean addedToNetwork;
    private boolean hadControlCrystal;
    private final DHDAbstractTile instance = this;
    protected final ItemStackHandler itemStackHandler = new JSGItemStackHandler(6) {

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            Item item = stack.getItem();

            switch (slot) {
                case 0:
                    if (instance instanceof DHDMilkyWayTile)
                        return item == JSGItems.CRYSTAL_CONTROL_MILKYWAY_DHD;
                    if (instance instanceof DHDPegasusTile)
                        return item == JSGItems.CRYSTAL_CONTROL_PEGASUS_DHD;

                case 1:
                case 2:
                case 3:
                    return SUPPORTED_UPGRADES.contains(item) && !hasUpgrade(item);

                case 4:
                    if (stack.getItem() instanceof UniversalBucket) {
                        FluidStack fluid = ((UniversalBucket) stack.getItem()).getFluid(stack);
                        return (fluid != null && fluid.getFluid() == JSGFluids.NAQUADAH_MOLTEN_REFINED);
                    }
                    return false;

                case BIOME_OVERRIDE_SLOT:
                    BiomeOverlayEnum override = tauri.dev.jsg.config.JSGConfig.stargateConfig.getBiomeOverrideItemMetaPairs().get(new ItemMetaPair(stack));
                    if (override == null) return false;

                    return getSupportedOverlays().contains(override);

                default:
                    return true;
            }
        }

        @Override
        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
            return 1;
        }

        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
            super.setStackInSlot(slot, stack);

            if (!world.isRemote && slot == 0) {
                // Crystal changed
                updateCrystal();
            }
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack out = super.extractItem(slot, amount, simulate);

            if (!world.isRemote && slot == 0 && amount > 0 && !simulate) {
                // Removing crystal
                updateCrystal();
            }

            return out;
        }

        @Override
        protected void onContentsChanged(int slot) {
            switch (slot) {
                case BIOME_OVERRIDE_SLOT:
                    sendState(StateTypeEnum.BIOME_OVERRIDE_STATE, new StargateBiomeOverrideState(determineBiomeOverride()));
                    break;

                case 4:
                    ItemStack stack = getStackInSlot(slot);
                    if (stack.getItem() instanceof UniversalBucket) {
                        FluidStack fluid = ((UniversalBucket) stack.getItem()).getFluid(stack);
                        if (fluid != null && fluid.getFluid() == JSGFluids.NAQUADAH_MOLTEN_REFINED) {
                            int amount = fluid.amount;
                            int filled = fluidHandler.fill(fluid, false);
                            if (filled == amount) {
                                setStackInSlot(slot, new ItemStack(Items.BUCKET));
                                fluidHandler.fill(fluid, true);
                            }
                        }
                    }
                    break;


                default:
                    break;
            }

            super.onContentsChanged(slot);
            markDirty();
        }
    };
    // ------------------------------------------------------------
    // Node-related work
    private final Node node = JSG.ocWrapper.createNode(this, "dhd");

    public DHDAbstractRendererState getRendererStateClient() {
        return rendererStateClient;
    }

    @Override
    public void rotate(Rotation rotation) {
        IBlockState state = world.getBlockState(pos);

        int rotationOrig = state.getValue(JSGProps.ROTATION_HORIZONTAL);
        world.setBlockState(pos, state.withProperty(JSGProps.ROTATION_HORIZONTAL, rotation.rotate(rotationOrig, 16)));
    }

    public void setLinkedGate(BlockPos gate, int linkId) {
        this.linkedGate = gate;
        this.linkId = linkId;

        markDirty();
    }

    public boolean isLinked() {
        return this.linkedGate != null;
    }

    public StargateAbstractBaseTile getLinkedGate(IBlockAccess world) {
        if (linkedGate == null) return null;

        return (StargateAbstractBaseTile) world.getTileEntity(linkedGate);
    }

    public abstract void updateLinkStatus(World world, BlockPos pos);

    @Override
    public boolean canLinkTo() {
        return !isLinked();
    }

    @Override
    public int getLinkId() {
        return linkId;
    }

    // -----------------------------------------------------------------------------
    // Symbol activation

    public ReactorStateEnum getReactorState() {
        return reactorState;
    }

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            targetPoint = new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
            hadControlCrystal = hasControlCrystal();
        } else {
            JSGPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_STATE));
        }
    }


    // -----------------------------------------------------------------------------
    // States

    @Override
    public void update() {
        if (!world.isRemote) {
            // This cannot be done in onLoad because it makes
            // Tile invisible to the network sometimes
            if (!addedToNetwork) {
                addedToNetwork = true;
                JSG.ocWrapper.joinWirelessNetwork(this);
                JSG.ocWrapper.joinOrCreateNetwork(this);
            }

            if (!lastPos.equals(pos)) {
                lastPos = pos;
                this.updateLinkStatus(world, pos);
            }

            // Has crystal
            if (hasControlCrystal()) {
                if (isLinked()) {
                    StargateAbstractBaseTile gateTile = getLinkedGate(world);
                    if (gateTile == null) {
                        setLinkedGate(null, -1);

                        JSG.logger.error("Gate didn't unlink properly, forcing...");
                        return;
                    }

                    IEnergyStorage energyStorage = (IEnergyStorage) gateTile.getCapability(CapabilityEnergy.ENERGY, null);

                    int amount = tauri.dev.jsg.config.JSGConfig.dhdConfig.powerGenerationMultiplier;

                    if (reactorState != ReactorStateEnum.STANDBY) {
                        FluidStack simulatedDrain = fluidHandler.drainInternal(amount, false);

                        if (simulatedDrain != null && simulatedDrain.amount >= amount)
                            reactorState = ReactorStateEnum.ONLINE;
                        else reactorState = ReactorStateEnum.NO_FUEL;
                    }

                    if (reactorState == ReactorStateEnum.ONLINE || reactorState == ReactorStateEnum.STANDBY) {
                        float percent = energyStorage.getEnergyStored() / (float) energyStorage.getMaxEnergyStored();
                        //						JSG.info("state: " + reactorState + ", percent: " + percent);

                        if (percent < tauri.dev.jsg.config.JSGConfig.dhdConfig.activationLevel)
                            reactorState = ReactorStateEnum.ONLINE;

                        else if (percent >= JSGConfig.dhdConfig.deactivationLevel)
                            reactorState = ReactorStateEnum.STANDBY;
                    }

                    if (reactorState == ReactorStateEnum.ONLINE) {
                        fluidHandler.drainInternal(amount, true);
                        energyStorage.receiveEnergy(tauri.dev.jsg.config.JSGConfig.dhdConfig.energyPerNaquadah * tauri.dev.jsg.config.JSGConfig.dhdConfig.powerGenerationMultiplier, false);
                    }
                }

                // Not linked
                else {
                    reactorState = ReactorStateEnum.NOT_LINKED;
                }
            }

            // No crystal
            else {
                reactorState = ReactorStateEnum.NO_CRYSTAL;
            }
        }
    }

    // Server
    protected BiomeOverlayEnum determineBiomeOverride() {
        ItemStack stack = itemStackHandler.getStackInSlot(BIOME_OVERRIDE_SLOT);

        if (stack.isEmpty()) {
            return null;
        }

        BiomeOverlayEnum biomeOverlay = tauri.dev.jsg.config.JSGConfig.stargateConfig.getBiomeOverrideItemMetaPairs().get(new ItemMetaPair(stack));

        if (getSupportedOverlays().contains(biomeOverlay)) {
            return biomeOverlay;
        }

        return null;
    }

    public EnumSet<BiomeOverlayEnum> getSupportedOverlays() {
        return SUPPORTED_OVERLAYS;
    }

    public boolean hasControlCrystal() {
        return !itemStackHandler.getStackInSlot(0).isEmpty();
    }


    // -----------------------------------------------------------------------------
    // Item handler

    private void updateCrystal() {
        boolean hasControlCrystal = hasControlCrystal();

        if (hadControlCrystal != hasControlCrystal) {
            if (hasControlCrystal) {
                sendState(StateTypeEnum.RENDERER_STATE, getState(StateTypeEnum.RENDERER_STATE));
            } else {
                clearSymbols();
            }

            hadControlCrystal = hasControlCrystal;
        }
    }

    public abstract void activateSymbol(SymbolInterface symbol);

    public void clearSymbols() {
        world.notifyNeighborsOfStateChange(pos, JSGBlocks.DHD_BLOCK, true);

        sendState(StateTypeEnum.DHD_ACTIVATE_BUTTON, new DHDActivateButtonState(true));
    }

    public void sendState(StateTypeEnum type, State state) {
        if (world.isRemote) return;

        if (targetPoint != null) {
            JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, type, state), targetPoint);
        } else {
            JSG.logger.debug("targetPoint was null trying to send " + type + " from " + this.getClass().getCanonicalName());
        }
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {

            case DHD_ACTIVATE_BUTTON:
                return new DHDActivateButtonState();

            case GUI_UPDATE:
                return new DHDContainerGuiUpdate();

            case BIOME_OVERRIDE_STATE:
                return new StargateBiomeOverrideState();

            default:
                throw new UnsupportedOperationException("EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
        }
    }

    // -----------------------------------------------------------------------------
    // Fluid handler

    @Override
    public void setState(StateTypeEnum stateType, State state) {
        if (stateType == StateTypeEnum.GUI_UPDATE) {
            DHDContainerGuiUpdate guiState = (DHDContainerGuiUpdate) state;

            fluidHandler.setFluid(new FluidStack(JSGFluids.NAQUADAH_MOLTEN_REFINED, guiState.fluidAmount));
            fluidHandler.setCapacity(guiState.tankCapacity);
            reactorState = guiState.reactorState;
            isLinkedClient = guiState.isLinked;
        } else {
            throw new UnsupportedOperationException("EnumStateType." + stateType.name() + " not implemented on " + this.getClass().getName());
        }
    }


    // -----------------------------------------------------------------------------
    // Capabilities

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.DOWN)) || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);

        else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing == null || facing == EnumFacing.DOWN))
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidHandler);

        return super.getCapability(capability, facing);
    }


    // ---------------------------------------------------------------------------------------------------
    // NBT

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if (linkedGate != null) {
            compound.setLong("linkedGate", linkedGate.toLong());
            compound.setInteger("linkId", linkId);
        }

        compound.setTag("itemStackHandler", itemStackHandler.serializeNBT());

        NBTTagCompound fluidHandlerCompound = new NBTTagCompound();
        fluidHandler.writeToNBT(fluidHandlerCompound);
        compound.setTag("fluidHandler", fluidHandlerCompound);

        if (node != null) {
            NBTTagCompound nodeCompound = new NBTTagCompound();
            node.save(nodeCompound);

            compound.setTag("node", nodeCompound);
        }

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("linkedGate")) {
            linkedGate = BlockPos.fromLong(compound.getLong("linkedGate"));
            if (linkedGate.equals(new BlockPos(0, 0, 0))) // 1.8 fix
                linkedGate = null;
        }

        if (compound.hasKey("linkId")) {
            linkId = compound.getInteger("linkId");
        }

        itemStackHandler.deserializeNBT(compound.getCompoundTag("itemStackHandler"));

        if (compound.getBoolean("hasUpgrade") || compound.getBoolean("insertAnimation")) {
            itemStackHandler.setStackInSlot(1, new ItemStack(JSGItems.CRYSTAL_GLYPH_DHD));
        }

        fluidHandler.readFromNBT(compound.getCompoundTag("fluidHandler"));

        if (node != null && compound.hasKey("node")) node.load(compound.getCompoundTag("node"));
    }

    @Override
    public void onChunkUnload() {
        if (node != null) node.remove();

        JSG.ocWrapper.leaveWirelessNetwork(this);
    }

    @Override
    public void invalidate() {
        if (node != null) node.remove();

        JSG.ocWrapper.leaveWirelessNetwork(this);

        super.invalidate();
    }


    // ------------------------------------------------------------
    // Wireless Network
    @Override
    public int x() {
        return pos.getX();
    }

    @Override
    public int y() {
        return pos.getY();
    }

    @Override
    public int z() {
        return pos.getZ();
    }

    @Override
    public World world() {
        return world;
    }

    @Override
    @Optional.Method(modid = "opencomputers")
    public void receivePacket(Packet packet, WirelessEndpoint sender) {
    }

    @Override
    @Optional.Method(modid = "opencomputers")
    public Node node() {
        return node;
    }

    @Override
    @Optional.Method(modid = "opencomputers")
    public void onConnect(Node node) {
    }

    @Override
    @Optional.Method(modid = "opencomputers")
    public void onDisconnect(Node node) {
    }

    @Override
    @Optional.Method(modid = "opencomputers")
    public void onMessage(Message message) {
    }

    public void sendSignal(Object context, String name, Object... params) {
        JSG.ocWrapper.sendSignalToReachable(node, (Context) context, name, params);
    }

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function(symbolName:string) -- Activates DHD symbol")
    public Object[] pressButton(Context context, Arguments args) {
        if (!isLinked())
            return new Object[]{null, "dhd_failure_not_linked", "DHD is not linked to a gate"};
        if (!hasControlCrystal())
            return new Object[]{null, "dhd_failure_no_crystal", "DHD has no control crystal"};
        StargateClassicBaseTile gateTile = (StargateClassicBaseTile) this.getLinkedGate(world);
        if (gateTile == null)
            return new Object[]{null, "dhd_not_connected", "DHD is not connected to stargate"};

        if (!gateTile.getStargateState().idle() && !gateTile.getStargateState().dialingDHD()) {
            return new Object[]{null, "dhd_failure_busy", "Linked stargate is busy, state: " + gateTile.getStargateState().toString()};
        }

        if (gateTile.getDialedAddress().size() == 9) {
            return new Object[]{null, "dhd_failure_full", "Already dialed 9 chevrons"};
        }

        if (args.isInteger(0) || args.isString(0)) {
            SymbolInterface symbol = gateTile.getSymbolFromNameIndex(args.checkAny(0));
            if (symbol != null) {
                if (symbol == gateTile.getSymbolType().getBRB()) {
                    StargateOpenResult result = gateTile.attemptOpenAndFail();
                    if (result.ok())
                        return new Object[]{null, "dhd_engage", "Opening gate"};
                    else if (result == StargateOpenResult.NOT_ENOUGH_POWER)
                        return new Object[]{null, "dhd_engage_failed", "Not enough power to open gate"};
                    else if (result == StargateOpenResult.ADDRESS_MALFORMED)
                        return new Object[]{null, "dhd_engage_failed", "Wrong address"};
                    else
                        return new Object[]{null, "dhd_engage_failed_unknown", "Unknown error! This is a bug!"};
                }

                if (!gateTile.canAddSymbol(symbol)) {
                    return new Object[]{null, "dhd_engage_failed", "Can not add that symbol!"};
                }
                gateTile.addSymbolToAddressDHD(symbol);
            }
        }
        markDirty();

        return new Object[]{null, "dhd_pressed"};
    }

    // ------------------------------------------------------------
    // Methods

    @Optional.Method(modid = "opencomputers")
    @Callback(doc = "function(symbolName:string) -- Activates DHD´s BRB")
    public Object[] pressBRB(Context context, Arguments args) {
        StargateAbstractBaseTile gateTile = this.getLinkedGate(world);
        if (gateTile == null)
            return new Object[]{null, "dhd_not_connected", "DHD is not connected to stargate"};
        if (gateTile.getStargateState().initiating()) {
            gateTile.attemptClose(StargateClosedReasonEnum.REQUESTED);
            return new Object[]{null, "dhd_disengage", "Closing gate..."};
        }
        if (!gateTile.getStargateState().idle()) {
            return new Object[]{null, "dhd_failure_busy", "Linked stargate is busy, state: " + gateTile.getStargateState().toString()};
        }
        StargateOpenResult result = gateTile.attemptOpenAndFail();
        if (result.ok())
            return new Object[]{null, "dhd_engage", "Opening gate"};
        else if (result == StargateOpenResult.NOT_ENOUGH_POWER)
            return new Object[]{null, "dhd_engage_failed", "Not enough power to open gate"};
        else if (result == StargateOpenResult.ADDRESS_MALFORMED)
            return new Object[]{null, "dhd_engage_failed", "Wrong address"};
        return new Object[]{null, "dhd_engage_failed_unknown", "Unknown error! This is a bug!"};
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().add(-1, 0, -1), getPos().add(1, 2, 1));
    }


    // ---------------------------------------------------------------------------------------------------
    // Rendering distance

    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536;
    }

    // TODO Get rid of EnumKeyInterface
    public static enum DHDUpgradeEnum implements EnumKeyInterface<Item> {
        CHEVRON_UPGRADE(JSGItems.CRYSTAL_GLYPH_DHD);

        public Item item;

        private DHDUpgradeEnum(Item item) {
            this.item = item;
        }

        @Override
        public Item getKey() {
            return item;
        }
    }
}
