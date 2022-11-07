package tauri.dev.jsg.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.entity.friendly.TokraEntity;
import tauri.dev.jsg.tileentity.transportrings.TransportRingsAbstractTile;
import tauri.dev.jsg.util.JSGAxisAlignedBB;
import tauri.dev.jsg.util.LinkingHelper;

import java.util.ArrayList;

public class JSGTokraLookForRingsAI extends EntityAIBase {

    private final TokraEntity entity;
    private BlockPos nearestRingsPos;

    public JSGTokraLookForRingsAI(TokraEntity tokra) {
        this.entity = tokra;
    }

    @Override
    public boolean shouldExecute() {
        World world = entity.getWorld();
        BlockPos entityPos = entity.getPos();

        if(world == null) return false;
        if(entityPos == null) return false;

        EntityPlayer nearestPlayer = (EntityPlayer) world.findNearestEntityWithinAABB(EntityPlayer.class, new JSGAxisAlignedBB(new BlockPos(-30, -30, -30).add(entityPos), new BlockPos(30, 30, 30).add(entityPos)), entity);

        BlockPos nearestRings;
        ArrayList<BlockPos> blacklist = new ArrayList<>();
        int loop = 0;
        boolean found = false;
        while (!found && loop < 50) {
            nearestRings = LinkingHelper.findClosestPos(world, entityPos, new BlockPos(20, 20, 20), JSGBlocks.RINGS_BLOCKS, blacklist);
            if (nearestRings == null)
                break;

            TileEntity tile = world.getTileEntity(nearestRings);
            if (tile instanceof TransportRingsAbstractTile) {
                TransportRingsAbstractTile tRings = (TransportRingsAbstractTile) tile;
                if (tRings.isBusy()) {
                    blacklist.add(nearestRings);
                    continue;
                }
                if(nearestPlayer == null && world.getBlockState(tile.getPos().up()).getBlock() == JSGBlocks.DECOR_CRYSTAL_BLOCK) return false;
                if(nearestPlayer != null && world.getBlockState(tile.getPos().up()).getBlock() != JSGBlocks.DECOR_CRYSTAL_BLOCK) return false;

                if (tRings.dialNearestRings(false).ok()) {
                    this.nearestRingsPos = nearestRings;
                    return true;
                }
            } else {
                blacklist.add(nearestRings);
                continue;
            }
            loop++;
        }
        return false;
    }

    @Override
    public void startExecuting() {
        if(nearestRingsPos == null) return;
        entity.moveToBlockPosAndAngles(nearestRingsPos.up(2), entity.rotationYaw, entity.rotationPitch);
    }

    @Override
    public void updateTask() {
        if (nearestRingsPos == null) return;
        World world = entity.getWorld();
        BlockPos entityPos = entity.getPos();
        BlockPos nearestRings;
        nearestRings = LinkingHelper.findClosestPos(world, entityPos, new BlockPos(2, 2, 2), JSGBlocks.RINGS_BLOCKS, new ArrayList<>());
        if(nearestRings != null && (world.getTotalWorldTime() % 60 == 0)){
            TileEntity tile = world.getTileEntity(nearestRings);
            if (tile instanceof TransportRingsAbstractTile) {
                TransportRingsAbstractTile tRings = (TransportRingsAbstractTile) tile;
                if (tRings.isBusy()) return;
                tRings.dialNearestRings(true);
            }
        }
    }
}
