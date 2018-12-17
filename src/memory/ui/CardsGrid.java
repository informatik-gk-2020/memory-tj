package memory.ui;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import memory.game.CardGame;

public class CardsGrid extends GridPane {
    private final ObservableList<CardView> cardViews = FXCollections.observableArrayList();
    private final CardGame game;

    public CardsGrid(CardGame game) {
        this.game = game;

        setHgap(8);
        setVgap(8);

        game.getCards().addListener((InvalidationListener) observable -> updateCards());
        updateCards();
    }

    private void updateCards() {
        var cards = game.getCards();
        getChildren().clear();

        for (int i = 0; i < 64 && i < cards.size(); i++) {
            CardView view = new CardView(cards.get(i));
            cardViews.add(view);
            add(view, i % 8, i / 8);
        }
    }
}
