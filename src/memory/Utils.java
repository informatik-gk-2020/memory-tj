package memory;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class Utils {
    private Utils() {
    }

    public static void delay(Duration duration, Runnable callback) {
        var wait = new PauseTransition(duration);
        wait.setOnFinished(event -> callback.run());
        wait.play();
    }
}
