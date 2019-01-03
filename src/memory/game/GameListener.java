package memory.game;

import java.util.List;

public interface GameListener {
    default void gameEnded(Player winner) {
    }

    default void revealResult(Player player, List<Card> revealedCards, boolean success) {
    }

    default void resetGame() {
    }
}
