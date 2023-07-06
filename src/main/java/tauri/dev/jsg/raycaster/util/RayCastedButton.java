package tauri.dev.jsg.raycaster.util;

import tauri.dev.vector.Vector3f;

import java.util.List;

public class RayCastedButton {
    public List<Vector3f> vectors;
    public int buttonId;

    public RayCastedButton(int buttonId, List<Vector3f> vectors) {
        this.buttonId = buttonId;
        this.vectors = vectors;
    }
}
