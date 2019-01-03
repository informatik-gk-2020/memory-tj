package memory.launchpad.api;

import static memory.launchpad.api.LaunchpadDevice.getLightIndex;

public class LaunchpadBuffer {
    private final byte[] colors = new byte[300];
    private Launchpad launchpad;

    public javafx.scene.paint.Color getFXColor(int x, int y) {
        var index = getLightIndex(x, y);
        return javafx.scene.paint.Color.color(
                colors[index] / 63.0,
                colors[index + 1] / 63.0,
                colors[index + 2] / 63.0
        );
    }

    public void setColor(int x, int y, byte r, byte g, byte b) {
        updateColor(x, y, r, g, b);
        requestUpdate();
    }

    public void setColor(int x, int y, javafx.scene.paint.Color color) {
        setColor(
                x, y,
                (byte) (color.getRed() * 63),
                (byte) (color.getGreen() * 63),
                (byte) (color.getBlue() * 63)
        );
    }

    public void fill(int x1, int y1, int x2, int y2, byte r, byte g, byte b) {
        var xMin = Math.min(x1, x2);
        var yMin = Math.min(y1, y2);
        var xMax = Math.max(x1, x2);
        var yMax = Math.max(y1, y2);

        for (var y = yMin; y <= yMax; y++) {
            for (int x = xMin; x <= xMax; x++) {
                updateColor(x, y, r, g, b);
            }
        }

        requestUpdate();
    }

    public void fill(int x1, int y1, int x2, int y2, javafx.scene.paint.Color color) {
        fill(
                x1, y1, x2, y2,
                (byte) (color.getRed() * 63),
                (byte) (color.getGreen() * 63),
                (byte) (color.getBlue() * 63)
        );
    }

    private void updateColor(int x, int y, byte r, byte g, byte b) {
        var i = getLightIndex(x, y) * 3;

        colors[i] = r;
        colors[i + 1] = g;
        colors[i + 2] = b;
    }

    public void requestUpdate() {
        if (launchpad != null)
            launchpad.requestUpdate();
    }

    void setLaunchpad(Launchpad launchpad) {
        this.launchpad = launchpad;
    }

    byte[] getColorValues() {
        return colors;
    }
}
