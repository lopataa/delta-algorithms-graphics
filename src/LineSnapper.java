public class LineSnapper {
    public static models.Point snap(int x1, int y1, int x2, int y2) {
        int relativeX = x2 - x1;
        int relativeY = y2 - y1;

        // calculate the angle
        double angle = Math.atan2(relativeY, relativeX); // this is in radians

        // snap to PI/4 or 45deg
        double snappedAngle = Math.round(angle / (Math.PI / 4)) * (Math.PI / 4);

        // keep the same length, but move it to the correct distance
        double length = Math.hypot(relativeX, relativeY);

        // now calculate the points under the angle
        double x = (x1 + Math.cos(snappedAngle) * length);
        double y = (y1 + Math.sin(snappedAngle) * length);

        // create the point B
         return new models.Point((int) x, (int) y);
    }
}
