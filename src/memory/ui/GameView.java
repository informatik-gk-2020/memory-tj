package memory.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import memory.game.CardGame;

public class GameView extends BorderPane {
    private final CardGame game;

    public GameView(CardGame game) {
        this.game = game;

        var grid = new CardsGrid(game);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(8));
        setCenter(grid);
    }
}
