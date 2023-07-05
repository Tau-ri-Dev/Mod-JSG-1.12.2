package tauri.dev.jsg.raycaster;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import tauri.dev.jsg.raycaster.util.Line;
import tauri.dev.jsg.raycaster.util.RayCastedButton;
import tauri.dev.jsg.raycaster.util.RaycasterVertex;
import tauri.dev.jsg.raycaster.util.Rect;
import tauri.dev.vector.Vector2f;
import tauri.dev.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public abstract class Raycaster {
    protected abstract List<RayCastedButton> getButtons();

    protected abstract Vector3f getTranslation(World world, BlockPos pos);

    protected abstract boolean buttonClicked(World world, EntityPlayer player, int buttonId, BlockPos pos, EnumHand hand);

    public boolean onActivated(World world, BlockPos pos, EntityPlayer player, float rotation, EnumHand hand) {
        Vec3d lookVec = player.getLookVec();
        Vector2f lookVec2f = new Vector2f((float) lookVec.x, (float) lookVec.z);

        for (RayCastedButton btn : getButtons()) {
            List<Vector3f> veritices = btn.vectors;
            int n = veritices.size();
            List<Line> lines = new ArrayList<>();

            // Create lines between vectors
            for (int currentVectorIndex = 0; currentVectorIndex < n; currentVectorIndex++) {
                int nextVectorIndex = (currentVectorIndex + 1) % n; // When current is last, use 1st one

                Vector2f currentVector = getTransposed(veritices.get(currentVectorIndex), rotation, world, pos, player);
                Vector2f nextVector = getTransposed(veritices.get(nextVectorIndex), rotation, world, pos, player);

                lines.add(new Line(currentVector, nextVector));
            }

            // Create rect as surface of the button
            Rect rect = new Rect(lines);
            if(lines.size() == 4){
                // Probably normal square button
                // try this for better mapping
                rect = new Rect(lines.get(0), lines.get(2), lines.get(1), lines.get(3));
            }
            if (rect.checkForPointInBox(lookVec2f)) {
                buttonClicked(world, player, btn.buttonId, pos, hand);
                return true;
            }
        }
        return false;
    }

    private Vector2f getTransposed(Vector3f v, float rotation, World world, BlockPos pos, EntityPlayer player) {
        RaycasterVertex current = new RaycasterVertex(v.x, v.y, v.z);

        return current.rotate(rotation).localToGlobal(pos, getTranslation(world, pos)).calculateDifference(player).getViewport(player.getLookVec());
    }
}