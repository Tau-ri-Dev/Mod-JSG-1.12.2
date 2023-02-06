package tauri.dev.jsg.stargate;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.stargate.network.StargatePos;
import tauri.dev.jsg.tileentity.stargate.StargateAbstractBaseTile;

public class AutoCloseManager implements INBTSerializable<NBTTagCompound> {

    private StargateAbstractBaseTile gateTile;

    private int secondsPassed;
    private int playersPassed;

    public AutoCloseManager(StargateAbstractBaseTile gateTile) {
        this.gateTile = gateTile;
    }

    public void reset() {
        secondsPassed = 0;
        playersPassed = 0;
    }

    public void playerPassing() {
        playersPassed++;
    }

    /**
     * AutoClose update function (on server) (engaged) (receiving gate).
     * Scan for load status of the source gate every 20 ticks (1 second).
     *
     * @param {@link StargatePos} of the initiating gate.
     * @return {@code True} if the gate should be closed, false otherwise.
     */
    public boolean shouldClose(StargatePos sourceStargatePos) {
        if (gateTile.getWorld().getTotalWorldTime() % 20 == 0) {
            World sourceWorld = sourceStargatePos.getWorld();
            BlockPos sourcePos = sourceStargatePos.gatePos;

            boolean sourceLoaded = sourceWorld.isBlockLoaded(sourcePos);

            if (playersPassed > 0) {
                if (sourceLoaded) {
                    // create scan box
                    AxisAlignedBB scanBox = new AxisAlignedBB(sourcePos.add(new Vec3i(-10, -5, -10)), sourcePos.add(new Vec3i(10, 5, 10)));
                    // scan players in box
                    int playerCount = sourceWorld.getEntitiesWithinAABB(EntityPlayerMP.class, scanBox, player -> !player.isDead).size();
                    // check if player is in the box
                    if (playerCount == 0)
                        // if no, add 1 to secondsPassed
                        secondsPassed++;
                    else
                        // if yes, reset secondsPassed
                        secondsPassed = 0;
                } else {
                    secondsPassed++;
                }
            }

            if (secondsPassed >= JSGConfig.Stargate.autoClose.secondsToAutoclose) {
                return true;
            }
        }

        return false;
    }


    // ------------------------------------------------------------------------
    // NBT

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setInteger("secondsPassed", secondsPassed);
        compound.setInteger("playersPassed", playersPassed);

        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        if (compound == null)
            return;

        secondsPassed = compound.getInteger("secondsPassed");
        playersPassed = compound.getInteger("playersPassed");
    }
}
