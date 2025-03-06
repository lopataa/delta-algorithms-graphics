public class LineSnapper {
    public static models.Point snap(double x1, double y1, double x2, double y2) {
        double relativeX = x2 - x1;
        double relativeY = y2 - y1;

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
