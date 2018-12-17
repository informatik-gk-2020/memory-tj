package memory.ui;

import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import memory.Utils;
import memory.game.Card;

public class CardView extends Rectangle {
    private final Card card;

    public CardView(Card card) {
        this.card = card;

        ObjectBinding<Paint> color = Bindings.select(this.card, "pair", "color");
        setFill(Color.GRAY);

        setWidth(50);
        setHeight(50);

        setArcHeight(8);
        setArcWidth(8);

        var scale = new ScaleTransition(Duration.millis(100), this);
        scale.setToX(1.4);
        scale.setToY(1.4);
        scale.setToZ(1.4);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);

        var rotate = new RotateTransition(Duration.millis(200), this);
        rotate.setAxis(Rotate.Y_AXIS);

        card.revealedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                return;

            rotate.stop();
            scale.stop();

            rotate.setToAngle(newValue ? 180 : 0);
            rotate.playFromStart();
            scale.playFromStart();

            Utils.delay(Duration.millis(100), () -> {
                if (newValue) {
                    fillProperty().bind(color);
                } else {
                    fillProperty().unbind();
                    setFill(Color.GRAY);
                }
            });
        });

        setOnMouseClicked(event -> {
            if (card.getGame().canRevealCards())
                card.getGame().revealCard(card);
        });
    }

    public Card getCard() {
        return card;
    }
}
