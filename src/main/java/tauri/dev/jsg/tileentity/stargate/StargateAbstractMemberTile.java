package tauri.dev.jsg.tileentity.stargate;

import tauri.dev.jsg.stargate.power.StargateAbstractEnergyStorage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;


public abstract class StargateAbstractMemberTile extends TileEntity{

    // ---------------------------------------------------------------------------------
    // Base position

    protected BlockPos basePos;

    public boolean isMerged() {
        return basePos != null;
    }

    @Nullable
    public BlockPos getBasePos() {
        return basePos;
    }

    @Nullable
    public StargateAbstractBaseTile getBaseTile(World world) {
        if (basePos != null)
            return (StargateAbstractBaseTile) world.getTileEntity(basePos);

        return null;
    }

    public void setBasePos(BlockPos basePos) {
        this.basePos = basePos;
        markDirty();
    }


    // ---------------------------------------------------------------------------------
    // NBT

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        if (basePos != null)
            compound.setLong("basePos", basePos.toLong());

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("basePos"))
            basePos = BlockPos.fromLong(compound.getLong("basePos"));

        super.readFromNBT(compound);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return (isMerged() && (capability == CapabilityEnergy.ENERGY)) || super.hasCapability(capability, facing);
    }



    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (isMerged() && (capability == CapabilityEnergy.ENERGY)) {
            return CapabilityEnergy.ENERGY.cast(getEnergyStorage());
        }

        return super.getCapability(capability, facing);
    }

    protected StargateAbstractEnergyStorage getEnergyStorage() {
        if(getBaseTile(world) != null)
            return getBaseTile(world).getEnergyStorage();
        else return new StargateAbstractEnergyStorage();
    }






}
