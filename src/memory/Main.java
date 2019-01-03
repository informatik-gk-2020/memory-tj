package memory;

import javafx.application.Application;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import memory.game.CardGame;
import memory.launchpad.LaunchpadController;
import memory.launchpad.api.Launchpad;
import memory.ui.GameView;
import memory.ui.LaunchpadSelectionDialog;

public class Main extends Application {
    private final Launchpad launchpad = new Launchpad();
    private final CardGame game = new CardGame();
    private final LaunchpadController launchpadController = new LaunchpadController(launchpad, game);

    @Override
    public void start(Stage primaryStage) throws Exception {
        var gameView = new GameView(game);
        game.reset();

        var rootPane = new BorderPane(gameView);

        var toolbar = createToolbar(primaryStage);
        rootPane.setTop(toolbar);

        var scene = new Scene(rootPane);
        var camera = new PerspectiveCamera();
        camera.setFieldOfView(50);
        scene.setCamera(camera);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Memory");
        primaryStage.show();

        primaryStage.setOnHidden(event -> {
            launchpadController.close();
        });
    }

    private ToolBar createToolbar(Stage stage) {
        var launchpadButton = new Button("Launchpad");
        launchpadButton.setOnAction(event -> {
            var dialog = new LaunchpadSelectionDialog();
            dialog.initOwner(stage);
            dialog.showAndWait()
                    .filter(buttonType -> buttonType == ButtonType.OK)
                    .ifPresent(buttonType -> dialog.apply(launchpad));
        });

        return new ToolBar(launchpadButton);
    }
}
