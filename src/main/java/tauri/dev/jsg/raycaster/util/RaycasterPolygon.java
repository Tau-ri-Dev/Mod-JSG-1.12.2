package tauri.dev.jsg.raycaster.util;

import tauri.dev.vector.Vector2f;

import java.util.List;

public class RaycasterPolygon {
    public static class Line {
        public Vector2f p1, p2;

        public Line(Vector2f p1, Vector2f p2) {
            this.p1 = p1;
            this.p2 = p2;
        }
    }

    public static boolean onLine(Line l1, Vector2f p) {
        // Check whether p is on the line or not
        return p.x <= Math.max(l1.p1.x, l1.p2.x)
                && p.x <= Math.min(l1.p1.x, l1.p2.x)
                && (p.y <= Math.max(l1.p1.y, l1.p2.y)
                && p.y <= Math.min(l1.p1.y, l1.p2.y));
    }

    public static int getDirection(Vector2f a, Vector2f b, Vector2f c) {
        double val = (b.y - a.y) * (c.x - b.x)
                - (b.x - a.x) * (c.y - b.y);

        if (val == 0)

            // Collinear
            return 0;

        else if (val < 0)

            // Anti-clockwise direction
            return 2;

        // Clockwise direction
        return 1;
    }

    public static boolean isIntersect(Line l1, Line l2) {
        // Four direction for two lines and points of other
        // line
        int dir1 = getDirection(l1.p1, l1.p2, l2.p1);
        int dir2 = getDirection(l1.p1, l1.p2, l2.p2);
        int dir3 = getDirection(l2.p1, l2.p2, l1.p1);
        int dir4 = getDirection(l2.p1, l2.p2, l1.p2);

        // When intersecting
        if (dir1 != dir2 && dir3 != dir4)
            return true;

        // When p2 of line2 are on the line1
        if (dir1 == 0 && onLine(l1, l2.p1))
            return true;

        // When p1 of line2 are on the line1
        if (dir2 == 0 && onLine(l1, l2.p2))
            return true;

        // When p2 of line1 are on the line2
        if (dir3 == 0 && onLine(l2, l1.p1))
            return true;

        // When p1 of line1 are on the line2
        return dir4 == 0 && onLine(l2, l1.p2);
    }

    public static boolean checkInside(List<Vector2f> poly, Vector2f p) {
        int n = poly.size();

        // When polygon has less than 3 edge, it is not
        // polygon

        if (n < 3)
            return false;

        // Create a point at infinity, y is same as point p
        Vector2f pt = new Vector2f(9999, p.y);
        Line exline = new Line(p, pt);
        int count = 0;
        int i = 0;
        do {

            // Forming a line from two consecutive points of
            // poly
            Line side = new Line(poly.get(i), poly.get((i + 1) % n));
            if (isIntersect(side, exline)) {

                // If side is intersects exline
                if (getDirection(side.p1, p, side.p2) == 0)
                    return onLine(side, p);
                count++;
            }
            i = (i + 1) % n;
        } while (i != 0);

        // When count is odd
        return (count % 2 > 0);
    }
}
