package memory;

import javafx.application.Application;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.stage.Stage;
import memory.game.CardGame;
import memory.ui.GameView;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        var game = new CardGame();
        var gameView = new GameView(game);
        game.reset();

        var scene = new Scene(gameView);
        var camera = new PerspectiveCamera();
        camera.setFieldOfView(50);
        scene.setCamera(camera);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Memory");
        primaryStage.show();
    }
}
