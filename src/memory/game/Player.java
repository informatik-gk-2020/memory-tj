package memory.game;

import javafx.beans.property.SimpleIntegerProperty;

public class Player {
    private final SimpleIntegerProperty score = new SimpleIntegerProperty();

    public int getScore() {
        return score.get();
    }

    public SimpleIntegerProperty scoreProperty() {
        return score;
    }

    public void setScore(int score) {
        this.score.set(score);
    }

    public void incrementScore() {
        setScore(getScore() + 1);
    }

    public void reset() {
        setScore(0);
    }
}
