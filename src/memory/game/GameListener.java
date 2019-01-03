package memory.game;

import java.util.List;

public interface GameListener {
    void revealResult(Player player, List<Card> revealedCards, boolean success);
    void resetGame();
}
