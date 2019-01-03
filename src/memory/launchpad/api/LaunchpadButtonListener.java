package memory.launchpad.api;

public interface LaunchpadButtonListener {
    default void buttonPressed(int x, int y, int pressure) {
    }

    default void buttonPressure(int x, int y, int pressure) {
    }

    default void buttonReleased(int x, int y) {
    }
}
