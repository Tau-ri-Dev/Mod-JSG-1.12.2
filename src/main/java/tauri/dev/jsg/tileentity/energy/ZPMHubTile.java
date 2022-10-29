package tauri.dev.jsg.tileentity.energy;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdateRequestToServer;
import tauri.dev.jsg.stargate.power.StargateAbstractEnergyStorage;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.state.energy.ZPMHubRendererUpdate;
import tauri.dev.jsg.util.JSGItemStackHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ZPMHubTile extends TileEntity implements ITickable, ICapabilityProvider, StateProviderInterface {

    public static final int SLIDING_ANIMATION_LENGTH = 80;// int ticks

    protected final ItemStackHandler itemStackHandler = new JSGItemStackHandler(3) {

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if(stack.getItem() instanceof ItemBlock){
                ItemBlock itemBlock = (ItemBlock) stack.getItem();
                return itemBlock.getBlock() == JSGBlocks.ZPM;
            }
            return false;
        }

        @Override
        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_UPDATE), targetPoint);
            markDirty();
        }
    };

    private final StargateAbstractEnergyStorage energyStorage = new StargateAbstractEnergyStorage(JSGConfig.powerConfig.zpmCapacity, JSGConfig.powerConfig.zpmHubMaxEnergyTransfer) {

        @Override
        protected void onEnergyChanged() {
            markDirty();
        }
    };

    protected int energyStoredLastTick = 0;
    protected int energyTransferedLastTick = 0;
    private NetworkRegistry.TargetPoint targetPoint;

    public long animationStart;
    public boolean isAnimating;
    public boolean isSlidingUp;

    // Only on client
    public int zpm1Level;
    public int zpm2Level;
    public int zpm3Level;
    // ---------------

    public NetworkRegistry.TargetPoint getTargetPoint() {
        return targetPoint;
    }


    // ------------------------------------------------------------------------
    // NBT

    public StargateAbstractEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            targetPoint = new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
        } else {
            JSGPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.RENDERER_UPDATE));
        }
    }


    // -----------------------------------------------------------------------------
    // Power system

    @Override
    public void update() {
        if (!world.isRemote) {
            for (EnumFacing facing : EnumFacing.VALUES) {
                TileEntity tile = world.getTileEntity(pos.offset(facing));

                if (tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
                    int extracted = energyStorage.extractEnergy(JSGConfig.powerConfig.zpmHubMaxEnergyTransfer, true);
                    extracted = Objects.requireNonNull(tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite())).receiveEnergy(extracted, false);

                    energyStorage.extractEnergy(extracted, false);
                }
            }

            energyTransferedLastTick = energyStorage.getEnergyStored() - energyStoredLastTick;
            energyStoredLastTick = energyStorage.getEnergyStored();
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("energyStorage", getEnergyStorage().serializeNBT());
        compound.setTag("itemStackHandler", itemStackHandler.serializeNBT());

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        getEnergyStorage().deserializeNBT(compound.getCompoundTag("energyStorage"));
        itemStackHandler.deserializeNBT(compound.getCompoundTag("itemStackHandler"));

        super.readFromNBT(compound);
    }

    public int getEnergyTransferedLastTick() {
        return energyTransferedLastTick;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityEnergy.ENERGY) || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY)
            return CapabilityEnergy.ENERGY.cast(getEnergyStorage());

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);

        return super.getCapability(capability, facing);
    }

    public static int getZPMPowerLevel(int energyStored, int maxEnergy){
        return Math.round(energyStored / (float) maxEnergy * 5);
    }


    // -----------------------------------------------------------------------------
    // State

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_UPDATE:
                ItemStack stack1 = itemStackHandler.getStackInSlot(0);
                ItemStack stack2 = itemStackHandler.getStackInSlot(1);
                ItemStack stack3 = itemStackHandler.getStackInSlot(2);

                int zpm1Level = stack1.isEmpty() ? -1 : getZPMPowerLevel(stack1.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored(), stack1.getCapability(CapabilityEnergy.ENERGY, null).getMaxEnergyStored());
                int zpm2Level = stack2.isEmpty() ? -1 : getZPMPowerLevel(stack2.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored(), stack2.getCapability(CapabilityEnergy.ENERGY, null).getMaxEnergyStored());
                int zpm3Level = stack3.isEmpty() ? -1 : getZPMPowerLevel(stack3.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored(), stack3.getCapability(CapabilityEnergy.ENERGY, null).getMaxEnergyStored());

                return new ZPMHubRendererUpdate(animationStart, isAnimating, isSlidingUp, zpm1Level, zpm2Level, zpm3Level);

            case GUI_UPDATE:
                //return new CapacitorContainerGuiUpdate(energyStorage.getEnergyStored(), energyTransferedLastTick);

            default:
                return null;
        }
    }


    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case RENDERER_UPDATE:
                return new ZPMHubRendererUpdate();

            case GUI_UPDATE:
                //return new CapacitorContainerGuiUpdate();

            default:
                return null;
        }
    }


    @Override
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case RENDERER_UPDATE:
                ZPMHubRendererUpdate s = (ZPMHubRendererUpdate) state;
                this.animationStart = s.animationStart;
                this.isAnimating = s.isAnimating;
                this.isSlidingUp = s.slidingUp;
                this.zpm1Level = s.zpm1Level;
                this.zpm2Level = s.zpm2Level;
                this.zpm3Level = s.zpm3Level;
                world.markBlockRangeForRenderUpdate(pos, pos);
                markDirty();
                break;

            case GUI_UPDATE:
                /*CapacitorContainerGuiUpdate guiUpdate = (CapacitorContainerGuiUpdate) state;
                energyStorage.setEnergyStored(guiUpdate.energyStored);
                energyTransferedLastTick = guiUpdate.energyTransferedLastTick;
                break;*/

            default:
                break;
        }
    }
}
