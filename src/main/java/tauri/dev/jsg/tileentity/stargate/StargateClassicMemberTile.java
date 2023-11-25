package tauri.dev.jsg.tileentity.stargate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.Rotation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdatePacketToClient;
import tauri.dev.jsg.packet.StateUpdateRequestToServer;
import tauri.dev.jsg.stargate.EnumMemberVariant;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateProviderInterface;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.state.stargate.StargateCamoState;
import tauri.dev.jsg.state.stargate.StargateLightState;
import tauri.dev.jsg.util.FacingHelper;
import tauri.dev.jsg.util.main.JSGProps;

import java.util.Objects;

/**
 * TileEntity for ring blocks and chevron blocks
 *
 * @author MrJake
 */
public abstract class StargateClassicMemberTile extends StargateAbstractMemberTile implements StateProviderInterface, ITickable {

    private TargetPoint targetPoint;
    protected EnumFacing facingVertical;

    public EnumFacing getFacingVertical(){
        return (facingVertical == null ? EnumFacing.SOUTH : facingVertical);
    }
    public void setVerticalFacing(EnumFacing f){
        this.facingVertical = f;
        markDirty();
    }

    @Override
    public void update() {
        //if(world.getBlockState(pos).getValue(JSGProps.FACING_VERTICAL) != getFacingVertical())
        //    world.setBlockState(pos, world.getBlockState(pos).withProperty(JSGProps.FACING_VERTICAL, getFacingVertical()));
    }

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            targetPoint = new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512);
        } else {
            JSGPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.CAMO_STATE));
            JSGPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.LIGHT_STATE));
        }
    }

    @Override
    public void rotate(Rotation rotation) {
        IBlockState state = world.getBlockState(pos);

        EnumFacing facing = state.getValue(JSGProps.FACING_HORIZONTAL);
        world.setBlockState(pos, state.withProperty(JSGProps.FACING_HORIZONTAL, rotation.rotate(facing)));
    }

    // ---------------------------------------------------------------------------------

    /**
     * Is chevron block emitting light
     */
    private boolean isLitUp;

    public void setLitUp(boolean isLitUp) {
        boolean sync = isLitUp != this.isLitUp;

        this.isLitUp = isLitUp;
        markDirty();

        if (sync) {
            sendState(StateTypeEnum.LIGHT_STATE, getState(StateTypeEnum.LIGHT_STATE));
        }
    }

    public boolean isLitUp(IBlockState state) {
        return state.getValue(JSGProps.MEMBER_VARIANT) == EnumMemberVariant.CHEVRON && isLitUp;
    }


    // ---------------------------------------------------------------------------------
    private IBlockState camoBlockState;

    /**
     * Should only be called from server. Updates camoBlockState and
     * syncs the change to clients.
     *
     * @param camoBlockState Camouflage block state.
     */
    public void setCamoState(IBlockState camoBlockState) {
        // JSG.logger.debug("Setting camo for " + pos + " to " + camoBlockState);

        this.camoBlockState = camoBlockState;
        markDirty();

        if (!world.isRemote) {
            sendState(StateTypeEnum.CAMO_STATE, getState(StateTypeEnum.CAMO_STATE));
        } else {
            JSG.warn("Tried to set camoBlockState from client. This won't work!");
        }
    }

    public IBlockState getCamoState() {
        return camoBlockState;
    }

    public ItemStack getCamoItemStack() {
        if (camoBlockState != null) {
            Block block = camoBlockState.getBlock();

            if (block == Blocks.SNOW_LAYER)
                return null;

            int quantity = 1;
            int meta;

            if (block instanceof BlockSlab && ((BlockSlab) block).isDouble()) {
                quantity = 2;
                meta = block.getMetaFromState(camoBlockState);

                if (block == Blocks.DOUBLE_STONE_SLAB)
                    block = Blocks.STONE_SLAB;

                else if (block == Blocks.DOUBLE_STONE_SLAB2)
                    block = Blocks.STONE_SLAB2;

                else if (block == Blocks.DOUBLE_WOODEN_SLAB)
                    block = Blocks.WOODEN_SLAB;

                else if (block == Blocks.PURPUR_DOUBLE_SLAB)
                    block = Blocks.PURPUR_SLAB;
            } else {
                meta = block.getMetaFromState(camoBlockState);
            }

            return new ItemStack(block, quantity, meta);
        } else {
            return null;
        }
    }

    // ---------------------------------------------------------------------------------
    // NBT

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("isLitUp", isLitUp);

        if (camoBlockState != null) {
            compound.setString("doubleSlabBlock", Objects.requireNonNull(camoBlockState.getBlock().getRegistryName()).toString());
            compound.setInteger("doubleSlabMeta", camoBlockState.getBlock().getMetaFromState(camoBlockState));
        }

        compound.setInteger("facingVertical", FacingHelper.toInt(facingVertical));

        return super.writeToNBT(compound);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        isLitUp = compound.getBoolean("isLitUp");

        if (compound.hasKey("doubleSlabBlock")) {
            Block dblSlabBlock = Block.getBlockFromName(compound.getString("doubleSlabBlock"));
            if (dblSlabBlock != null)
                camoBlockState = dblSlabBlock.getStateFromMeta(compound.getInteger("doubleSlabMeta"));
        }

        facingVertical = FacingHelper.fromInt(compound.getInteger("facingVertical"));

        super.readFromNBT(compound);
    }


    // ---------------------------------------------------------------------------------
    // States

    protected void sendState(StateTypeEnum type, State state) {
        if (world.isRemote)
            return;

        if (targetPoint != null) {
            JSGPacketHandler.INSTANCE.sendToAllTracking(new StateUpdatePacketToClient(pos, type, state), targetPoint);
        } else {
            JSG.debug("targetPoint was null trying to send " + type + " from " + this.getClass().getCanonicalName());
        }
    }

    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case CAMO_STATE:
                return new StargateCamoState(camoBlockState);

            case LIGHT_STATE:
                return new StargateLightState(isLitUp);

            default:
                return null;
        }
    }

    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case CAMO_STATE:
                return new StargateCamoState();

            case LIGHT_STATE:
                return new StargateLightState();

            default:
                return null;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case CAMO_STATE:
                StargateCamoState memberState = (StargateCamoState) state;
                camoBlockState = memberState.getState();

                world.markBlockRangeForRenderUpdate(pos, pos);
                break;

            case LIGHT_STATE:
                isLitUp = ((StargateLightState) state).isLitUp();
                world.notifyLightSet(pos);
                world.checkLightFor(EnumSkyBlock.BLOCK, pos);

                break;

            default:
                break;
        }
    }
}
