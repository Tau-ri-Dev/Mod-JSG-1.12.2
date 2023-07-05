package tauri.dev.jsg.raycaster.better;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import tauri.dev.jsg.raycaster.util.RaycasterPolygon;
import tauri.dev.jsg.raycaster.util.RaycasterVertex;
import tauri.dev.vector.Vector2f;
import tauri.dev.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class RayCastedButton {
    public List<Vector3f> vectors;
    public int buttonId;

    public RayCastedButton(int buttonId, List<Vector3f> vectors) {
        this.buttonId = buttonId;
        this.vectors = vectors;
    }

    private Vector2f getTransposedVector(Vector3f v, float rotation, BlockPos pos, EntityPlayer player, Vector3f translation) {
        RaycasterVertex current = new RaycasterVertex(v.x, v.y, v.z);
        return current.rotate(rotation).localToGlobal(pos, translation).calculateDifference(player).getViewport(player.getLookVec());
    }

    public boolean isPointInsideButton(Vector2f point, float rotation, BlockPos pos, EntityPlayer player, Vector3f translation) {
        List<Vector2f> vectors = new ArrayList<>();

        for (Vector3f v : this.vectors) {
            vectors.add(getTransposedVector(v, rotation, pos, player, translation));
        }

        return RaycasterPolygon.checkInside(vectors, point);
    }
}
