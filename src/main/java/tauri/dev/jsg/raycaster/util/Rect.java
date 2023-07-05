package tauri.dev.jsg.raycaster.util;

import tauri.dev.vector.Vector2f;

import java.util.Arrays;
import java.util.List;

public class Rect {
    private final List<Line> lines;
    
    public Rect(Line lineNorth, Line lineSouth, Line lineWest, Line lineEast) {
        this.lines = Arrays.asList(lineNorth, lineSouth, lineWest, lineEast);
    }

    public Rect(List<Line> lines) {
        this.lines = lines;
    }

    public boolean checkForPointInBox(Vector2f p) {
        int intersects = 0;

        float a = 0.3f;
        float b = p.y - (a*p.x);

        for (Line currentLine : lines) {
            currentLine.setVerticalOffset(0);

            Vector2f inter = currentLine.getIntersect(a, b);

            if (inter.x > p.x) {
                float x0 = currentLine.getVertWithOffset(0).x;
                float x1 = currentLine.getVertWithOffset(1).x;

                float y0 = currentLine.getVertWithOffset(0).y;
                float y1 = currentLine.getVertWithOffset(1).y;

                float xMax = Math.max(x0, x1);
                float xMin = Math.min(x0, x1);

                float yMax = Math.max(y0, y1);
                float yMin = Math.min(y0, y1);

                if (inter.x > xMin && inter.x < xMax && inter.y > yMin && inter.y < yMax) {
                    intersects++;
                }
            }

        }

        return (intersects%2 > 0);
    }
}