package tauri.dev.jsg.raycaster.better;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import tauri.dev.vector.Vector2f;
import tauri.dev.vector.Vector3f;

import java.util.List;

@SuppressWarnings("all")
public abstract class BetterRaycaster {
    protected abstract List<RayCastedButton> getButtons();

    protected abstract Vector3f getTranslation(World world, BlockPos pos);

    protected abstract boolean buttonClicked(World world, EntityPlayer player, int buttonId, BlockPos pos, EnumHand hand);

    public boolean onActivated(World world, BlockPos pos, EntityPlayer player, float rotation, EnumHand hand) {
        for (RayCastedButton button : getButtons()) {
            Vec3d playerLook = player.getLookVec();
            Vector2f lookVec = new Vector2f((float) playerLook.x, (float) playerLook.z);
            if (button.isPointInsideButton(lookVec, rotation, pos, player, getTranslation(world, pos))) {
                return buttonClicked(world, player, button.buttonId, pos, hand);
            }
        }
        return false;
    }
}
