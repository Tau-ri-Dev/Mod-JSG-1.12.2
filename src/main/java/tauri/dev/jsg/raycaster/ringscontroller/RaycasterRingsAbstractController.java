package tauri.dev.jsg.raycaster.ringscontroller;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import tauri.dev.jsg.raycaster.Raycaster;
import tauri.dev.jsg.raycaster.util.Ray;
import tauri.dev.jsg.renderer.transportrings.controller.TRControllerAbstractRenderer;
import tauri.dev.jsg.util.FacingToRotation;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.vector.Vector3f;

import java.util.List;

public abstract class RaycasterRingsAbstractController extends Raycaster {

    @Override
    protected int getRayGroupCount() {
        return 4;
    }

    public void onActivated(World world, BlockPos pos, EntityPlayer player) {
        EnumFacing facing = world.getBlockState(pos).getValue(JSGProps.FACING_HORIZONTAL);
        float rotation = FacingToRotation.getIntRotation(facing, false);

        super.onActivated(world, pos, player, rotation, EnumHand.MAIN_HAND);
    }

    @Override
    protected Vector3f getTranslation(World world, BlockPos pos) {
        EnumFacing facing = world.getBlockState(pos).getValue(JSGProps.FACING_HORIZONTAL);

        return TRControllerAbstractRenderer.getTranslation(facing);
    }

    @Override
    protected boolean brbCheck(List<Ray> brbRayList, Vec3d lookVec, EntityPlayer player, BlockPos pos, EnumHand hand) {
        return false;
    }
}
