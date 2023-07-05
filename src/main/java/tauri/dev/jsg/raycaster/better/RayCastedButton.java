package tauri.dev.jsg.raycaster.better;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
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
            vectors.add(getTransposedVector(new Vector3f(v.x, v.y, v.z), rotation, pos, player, translation));
        }

        int i, j, n = vectors.size();
        int count = 0;

        for (i = 0, j = n - 1; i < n; j = i++) {
            if ((vectors.get(i).y > point.y) != (vectors.get(j).y > point.y) && (point.x < (vectors.get(j).x - vectors.get(i).x) * (point.y - vectors.get(i).y) / (vectors.get(j).y - vectors.get(i).y) + vectors.get(i).x)) {
                count++;
            }
        }

        return (count % 2 > 0);
    }
}
