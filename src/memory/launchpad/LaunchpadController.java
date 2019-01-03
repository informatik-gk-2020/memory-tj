package memory.launchpad;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.scene.paint.Color;
import memory.game.Card;
import memory.game.CardGame;
import memory.launchpad.api.Launchpad;
import memory.launchpad.api.LaunchpadBuffer;
import memory.launchpad.api.LaunchpadButtonListener;

import java.util.HashMap;

public class LaunchpadController implements LaunchpadButtonListener {
    private final Launchpad launchpad;
    private final CardGame game;
    private final LaunchpadBuffer buffer = new LaunchpadBuffer();

    private final HashMap<Card, ChangeListener<Boolean>> listeners = new HashMap<>();

    public LaunchpadController(Launchpad launchpad, CardGame game) {
        this.launchpad = launchpad;
        this.game = game;
        launchpad.setBuffer(buffer);
        launchpad.setButtonListener(this);

        game.getCards().addListener((InvalidationListener) observable -> {
            removeListeners();
            addListeners();
        });
    }

    private void addListeners() {
        var cards = game.getCards();
        for (int i = 0; i < cards.size(); i++) {
            var card = cards.get(i);
            var x = i % 8 + 1;
            var y = i / 8 + 1;

            ChangeListener<Boolean> listener = (observable, oldValue, newValue) -> {
                updateLight(x, y, card);
            };
            card.revealedProperty().addListener(listener);
            listeners.put(card, listener);
            updateLight(x, y, card);
        }
    }

    private void removeListeners() {
        for (var entry : listeners.entrySet()) {
            entry.getKey().revealedProperty().removeListener(entry.getValue());
        }
        listeners.clear();
    }

    private void updateLight(int x, int y, Card card) {
        buffer.setColor(x, y, card.isRevealed() ? card.getPair().getColor() : Color.BLACK);
    }

    @Override
    public void buttonPressed(int x, int y, int pressure) {
        if (x > 0 && x < 9 && y > 0 && y < 9 && game.canRevealCards()) {
            game.revealCard(game.getCards().get((y - 1) * 8 + x - 1));
        }
    }

    @Override
    public void buttonReleased(int x, int y) {

    }

    public void close() {
        launchpad.close();
    }
}
