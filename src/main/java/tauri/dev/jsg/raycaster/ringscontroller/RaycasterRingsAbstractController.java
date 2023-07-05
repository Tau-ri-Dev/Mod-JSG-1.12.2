package tauri.dev.jsg.raycaster.ringscontroller;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.raycaster.better.BetterRaycaster;
import tauri.dev.jsg.renderer.transportrings.controller.TRControllerAbstractRenderer;
import tauri.dev.jsg.util.FacingHelper;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.vector.Vector3f;

public abstract class RaycasterRingsAbstractController extends BetterRaycaster {


    public void onActivated(World world, BlockPos pos, EntityPlayer player) {
        EnumFacing facing = world.getBlockState(pos).getValue(JSGProps.FACING_HORIZONTAL);
        float rotation = FacingHelper.getIntRotation(facing, false);

        super.onActivated(world, pos, player, rotation, EnumHand.MAIN_HAND);
    }

    @Override
    protected Vector3f getTranslation(World world, BlockPos pos) {
        EnumFacing facing = world.getBlockState(pos).getValue(JSGProps.FACING_HORIZONTAL);

        return TRControllerAbstractRenderer.getTranslation(facing);
    }
}
