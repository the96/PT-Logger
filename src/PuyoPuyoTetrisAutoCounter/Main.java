package PuyoPuyoTetrisAutoCounter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.opencv.core.Core;

import java.net.URL;

public class Main extends Application {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Puyopuyo Tetris Winner matcher");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        Controller controller = loader.getController();
        controller.setStage(primaryStage);
    }
}
