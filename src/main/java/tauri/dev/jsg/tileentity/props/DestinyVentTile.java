package tauri.dev.jsg.tileentity.props;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.packet.JSGPacketHandler;
import tauri.dev.jsg.packet.StateUpdateRequestToServer;
import tauri.dev.jsg.sound.JSGSoundHelper;
import tauri.dev.jsg.sound.SoundEventEnum;
import tauri.dev.jsg.state.State;
import tauri.dev.jsg.state.StateTypeEnum;
import tauri.dev.jsg.state.stargate.StargateCamoState;

import javax.annotation.Nonnull;
import java.util.Objects;

public class DestinyVentTile extends DestinyBearingTile {

    public float getAnimationStage(double tick) {
        double time = Math.max((tick - animationStart - ANIMATION_DELAY_BEFORE), 0);
        float animationStage = 0;
        if (time <= (ANIMATION_DELAY_BETWEEN + OPEN_ANIMATION_LENGTH * 2))
            animationStage = (float) Math.max(0, Math.min(1, (Math.sin(Math.min((time / OPEN_ANIMATION_LENGTH), 1) * Math.PI) * ((float) ANIMATION_DELAY_BETWEEN / OPEN_ANIMATION_LENGTH))));

        return animationStage;
    }

    public static final int ANIMATION_DELAY_BEFORE = 10;
    public static final int OPEN_ANIMATION_LENGTH = 80;
    public static final int ANIMATION_DELAY_BETWEEN = 240;

    public DestinyVentRenderState rendererState = new DestinyVentRenderState(false, -1);

    public static class DestinyVentRenderState extends DestinyBearingRenderState {

        public DestinyVentRenderState(boolean isActive, long animationStart) {
            super(isActive);
            this.animationStart = animationStart;
        }


        public DestinyVentRenderState animate(boolean open, long animationStart) {
            setActive(open);
            this.animationStart = animationStart;
            return this;
        }

        public long animationStart;

        @Override
        public void toBytes(ByteBuf buf) {
            super.toBytes(buf);
            buf.writeLong(animationStart);
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            super.fromBytes(buf);
            animationStart = buf.readLong();
        }
    }

    public long animationStart = -1;

    public boolean soundPlayed = false;

    @Override
    public DestinyVentRenderState getRendererState() {
        return rendererState;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!world.isRemote) {
            sendState(StateTypeEnum.CAMO_STATE, getState(StateTypeEnum.CAMO_STATE));
        } else {
            JSGPacketHandler.INSTANCE.sendToServer(new StateUpdateRequestToServer(pos, StateTypeEnum.CAMO_STATE));
        }
    }

    @Override
    public void update() {
        super.update();

        if (!world.isRemote) {
            boolean isOpen = (getAnimationStage(world.getTotalWorldTime()) == 1);

            if (!isOpen && soundPlayed) {
                soundPlayed = false;
                markDirty();
            } else if (isOpen && !soundPlayed) {
                soundPlayed = true;
                markDirty();
                JSGSoundHelper.playSoundEvent(world, pos, SoundEventEnum.DESTINY_BLASTER);
            }
        }
    }

    // CLIENT ONLY
    public void startAnimation() {
        this.animationStart = world.getTotalWorldTime();
        this.soundPlayed = false;
        markDirty();
        sendState(StateTypeEnum.RENDERER_STATE, getState(StateTypeEnum.RENDERER_STATE));
    }

    // Server
    @Override
    public State getState(StateTypeEnum stateType) {
        switch (stateType) {
            case CAMO_STATE:
                return new StargateCamoState(camoBlockState);

            case RENDERER_STATE:
                return getRendererState().animate(isActive, animationStart);

            default:
                return null;
        }
    }

    // Server
    @Override
    public State createState(StateTypeEnum stateType) {
        switch (stateType) {
            case CAMO_STATE:
                return new StargateCamoState();
            case RENDERER_STATE:
                return getRendererState().animate(isActive, animationStart);
            default:
                return null;
        }
    }

    // Client
    @Override
    public void setState(StateTypeEnum stateType, State state) {
        switch (stateType) {
            case CAMO_STATE:
                StargateCamoState memberState = (StargateCamoState) state;
                camoBlockState = memberState.getState();

                world.markBlockRangeForRenderUpdate(pos, pos);
                break;
            case RENDERER_STATE:
                this.animationStart = ((DestinyVentRenderState) state).animationStart;
                break;
            default:
                break;
        }
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

    // ------------------------------------------------------------

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        this.animationStart = compound.getLong("animationStart");

        if (compound.hasKey("camoBlock")) {
            Block dblSlabBlock = Block.getBlockFromName(compound.getString("camoBlock"));
            if (dblSlabBlock != null)
                camoBlockState = dblSlabBlock.getStateFromMeta(compound.getInteger("camoBlocMeta"));
        }

        super.readFromNBT(compound);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        compound.setLong("animationStart", animationStart);

        if (camoBlockState != null) {
            compound.setString("camoBlock", Objects.requireNonNull(camoBlockState.getBlock().getRegistryName()).toString());
            compound.setInteger("camoBlocMeta", camoBlockState.getBlock().getMetaFromState(camoBlockState));
        }
        return super.writeToNBT(compound);
    }
}
