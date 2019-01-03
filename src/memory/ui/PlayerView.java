package memory.ui;

import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import memory.game.Card;
import memory.game.CardGame;
import memory.game.GameListener;
import memory.game.Player;

import java.util.List;

public class PlayerView extends VBox implements GameListener {
    private final ScaleTransition scoreAnimation;
    private final Player player;

    public PlayerView(CardGame game, Player player, String name) {
        super(8);
        setPadding(new Insets(8));
        this.player = player;
        game.addGameListener(this);

        var header = new Label(name);
        header.setFont(Font.font(header.getFont().getFamily(), FontWeight.BOLD, header.getFont().getSize() * 2));

        var isCurrentPlayer = Bindings.equal(game.currentPlayerProperty(), player);
        header.textFillProperty().bind(Bindings.when(isCurrentPlayer)
                .then((Paint) Color.GREEN)
                .otherwise(header.getTextFill()));

        var score = new Label();
        score.setFont(Font.font(score.getFont().getFamily(), score.getFont().getSize() * 1.5));
        score.textProperty().bind(Bindings.createStringBinding(
                () -> player.getScore() + (player.getScore() == 1 ? " Punkt" : " Punkte"),
                player.scoreProperty()
        ));

        scoreAnimation = new ScaleTransition(Duration.millis(100), score);
        scoreAnimation.setToX(1.4);
        scoreAnimation.setToY(1.4);
        scoreAnimation.setToZ(1.4);
        scoreAnimation.setCycleCount(2);
        scoreAnimation.setAutoReverse(true);

        getChildren().addAll(header, score);
    }

    @Override
    public void revealResult(Player player, List<Card> revealedCards, boolean success) {
        if (player == this.player && success) {
            scoreAnimation.playFromStart();
        }
    }

    @Override
    public void resetGame() {

    }
}
