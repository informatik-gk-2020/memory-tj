package memory.game;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class Pair {
    private final SimpleObjectProperty<Color> color;

    public Pair(Color color) {
        this.color = new SimpleObjectProperty<>(color);
    }

    public Color getColor() {
        return color.get();
    }

    public SimpleObjectProperty<Color> colorProperty() {
        return color;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }
}
