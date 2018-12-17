package memory.game;

import javafx.beans.property.SimpleBooleanProperty;

public class Card {
    private final CardGame game;
    private final Pair pair;

    private final SimpleBooleanProperty revealed = new SimpleBooleanProperty();

    public Card(CardGame game, Pair pair) {
        this.game = game;
        this.pair = pair;
    }

    public CardGame getGame() {
        return game;
    }

    public Pair getPair() {
        return pair;
    }

    public boolean isRevealed() {
        return revealed.get();
    }

    public SimpleBooleanProperty revealedProperty() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed.set(revealed);
    }
}
