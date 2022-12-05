package tauri.dev.jsg.entity;

import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;

public class EntityKino extends EntityFlying {
    public EntityPlayer playerControlling;
    private BlockPos playerPos;

    public EntityKino(World worldIn) {
        super(worldIn);
    }

    public boolean beginControlling(EntityPlayerMP player) {
        if (playerControlling != null) return false;
        playerControlling = player;
        playerPos = player.getPosition();
        WorldServer world = player.getServerWorld();
        world.spawnEntity(player);

        player.startRiding(this, true);
        return true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!this.world.isRemote && playerControlling != null) {
            this.setPositionAndRotation(playerControlling.posX, playerControlling.posY, playerControlling.posZ, playerControlling.cameraYaw, playerControlling.cameraPitch);
        }
    }

    @Override
    public boolean processInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if (player instanceof EntityPlayerMP)
            return beginControlling((EntityPlayerMP) player);

        return false;
    }

}
