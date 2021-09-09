package mrjake.aunis.tileentity.stargate;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.*;
import mrjake.aunis.Aunis;
import mrjake.aunis.stargate.EnumStargateState;
import mrjake.aunis.stargate.network.SymbolTypeEnum;
import mrjake.aunis.stargate.power.StargateAbstractEnergyStorage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Optional.InterfaceList({@Optional.Interface(iface = "li.cil.oc.api.network.EnvironmentHost", modid = "opencomputers"), @Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "opencomputers")})
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
        Network.joinOrCreateNetwork(getBaseTile(world));
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
        return getBaseTile(world).getEnergyStorage();
    }



//    @Override
//    @Optional.Method(modid = "opencomputers")
//    public Node node() {
//        return isMerged() ? getBaseTile(world).node() : null;
//    }
//
//    @Override
//    @Optional.Method(modid = "opencomputers")
//    public void onConnect(Node node) {
//    }
//
//    @Override
//    @Optional.Method(modid = "opencomputers")
//    public void onDisconnect(Node node) {
//    }
//
//    @Override
//    @Optional.Method(modid = "opencomputers")
//    public void onMessage(Message message) {
//    }
//
//    // ------------------------------------------------------------
//    // Methods
//    // function(arg:type[, optionArg:type]):resultType; Description.
//    @Optional.Method(modid = "opencomputers")
//    @Callback(getter = true)
//    public Object[] stargateAddress(Context context, Arguments args) {
//        return isMerged() ? getBaseTile(world).stargateAddress(context, args) : null;
//    }
//
//    @Optional.Method(modid = "opencomputers")
//    @Callback(getter = true)
//    public Object[] dialedAddress(Context context, Arguments args) {
//        return isMerged() ? getBaseTile(world).dialedAddress(context, args) : null;
//    }
//
//    @Optional.Method(modid = "opencomputers")
//    @Callback
//    public Object[] getEnergyStored(Context context, Arguments args) {
//        return isMerged() ? getBaseTile(world).getEnergyStored(context, args) : null;
//    }
//
//    @Optional.Method(modid = "opencomputers")
//    @Callback
//    public Object[] getMaxEnergyStored(Context context, Arguments args) {
//        return isMerged() ? getBaseTile(world).getMaxEnergyStored(context, args) : null;
//    }


//    @Override
//    public Node node() {
//        return isMerged() ? getBaseTile(world).node() : null;
//    }
//
//    @Override
//    public void onConnect(Node node) {
//
//    }
//
//    @Override
//    public void onDisconnect(Node node) {
//
//    }
//
//    @Override
//    public void onMessage(Message message) {
//
//    }
//
//    @Override
//    public void onLoad() {
//        if (isMerged()) Network.joinOrCreateNetwork(getBaseTile(world));
//    }


}
