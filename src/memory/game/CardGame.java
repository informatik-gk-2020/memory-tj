package memory.game;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import memory.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CardGame {
    private final ObservableList<Card> cards = FXCollections.observableArrayList();
    private final ReadOnlyBooleanWrapper canRevealCards = new ReadOnlyBooleanWrapper();

    private final ArrayList<Card> revealedCards = new ArrayList<>();

    private final Player player1 = new Player();
    private final Player player2 = new Player();

    private final ReadOnlyObjectWrapper<Player> currentPlayer = new ReadOnlyObjectWrapper<>();

    private final ArrayList<GameListener> gameListeners = new ArrayList<>();

    public ObservableList<Card> getCards() {
        return cards;
    }

    public void revealCard(Card card) {
        if (revealedCards.contains(card))
            return;

        card.setRevealed(true);
        revealedCards.add(card);

        if (revealedCards.size() == 2) {
            setCanRevealCards(false);
            Utils.delay(Duration.seconds(1), this::checkPair);
        }
    }

    private void checkPair() {
        if (revealedCards.size() <= 1)
            return;

        var pair = revealedCards.get(0).getPair();
        var isSame = revealedCards.stream().allMatch(c -> c.getPair() == pair);
        var currentPlayer = getCurrentPlayer();

        if (isSame) {
            currentPlayer.incrementScore();
        } else {
            for (var i : revealedCards) {
                i.setRevealed(false);
            }
            nextPlayer();
        }

        for (var listener : gameListeners) {
            listener.revealResult(currentPlayer, revealedCards, isSame);
        }

        revealedCards.clear();
        setCanRevealCards(true);
    }

    private void nextPlayer() {
        setCurrentPlayer(getCurrentPlayer() == player1 ? player2 : player1);
    }

    public void reset() {
        for (var listener : gameListeners) {
            listener.resetGame();
        }

        var newCards = Arrays.stream(colors)
                .flatMap(color -> {
                    var pair = new Pair(color);
                    return Stream.of(new Card(this, pair), new Card(this, pair));
                })
                .collect(Collectors.toList());

        Collections.shuffle(newCards);

        cards.setAll(newCards);
        canRevealCards.set(true);
        revealedCards.clear();

        player1.reset();
        player2.reset();
        setCurrentPlayer(player1);
    }

    public boolean canRevealCards() {
        return canRevealCards.get();
    }

    public ReadOnlyBooleanProperty canRevealCardsProperty() {
        return canRevealCards.getReadOnlyProperty();
    }

    private void setCanRevealCards(boolean canRevealCards) {
        this.canRevealCards.set(canRevealCards);
    }

    public Player getCurrentPlayer() {
        return currentPlayer.get();
    }

    public ReadOnlyObjectProperty<Player> currentPlayerProperty() {
        return currentPlayer.getReadOnlyProperty();
    }

    private void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer.set(currentPlayer);
    }

    public void addGameListener(GameListener listener) {
        gameListeners.add(listener);
    }

    public void removeGameListener(GameListener listener) {
        gameListeners.remove(listener);
    }

    private static final Color[] colors = {
            Color.rgb(26, 188, 156),
            Color.rgb(150, 0, 255),
            Color.rgb(255, 0, 0),
            Color.rgb(0, 255, 0),
            Color.rgb(0, 0, 255),
            Color.rgb(255, 255, 0),
            Color.rgb(0, 255, 255),
            Color.rgb(255, 0, 255),
            Color.rgb(52, 152, 219),
            Color.rgb(155, 89, 182),
            Color.rgb(52, 73, 94),
            Color.rgb(241, 196, 15),
            Color.rgb(230, 126, 34),
            Color.rgb(231, 76, 60),
            Color.rgb(132, 53, 41),
            Color.rgb(14, 63, 143),
            Color.rgb(75, 93, 21),
            Color.rgb(254, 6, 100),
            Color.rgb(35, 84, 130),
            Color.rgb(87, 36, 200),
            Color.rgb(200, 200, 0),
            Color.rgb(100, 100, 0),
            Color.rgb(0, 100, 100),
            Color.rgb(100, 0, 100),
            Color.rgb(100, 255, 150),
            Color.rgb(0, 0, 64),
            Color.rgb(76, 23, 13),
            Color.rgb(255, 0, 128),
            Color.rgb(41, 234, 43),
            Color.rgb(234, 234, 0),
            Color.rgb(43, 76, 12),
            Color.rgb(64, 0, 5)
    };
}
