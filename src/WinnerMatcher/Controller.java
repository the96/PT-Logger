import javafx.fxml.Initializable;
import javafx.stage.Stage;
import org.opencv.core.Core;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    Stage stage;
    TestCapture testCapture;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            testCapture = new TestCapture();
        } catch (AWTException e) {
            // TODO 自動生成された catch ブロック
            System.out.println("Catch AWTException");
            e.printStackTrace();
        }
    }

    public void setStage (Stage stage) {
        this.stage = stage;
    }

    /*
    public void judge() {
        Mat p1 = Imgcodecs.imread("1p.png");
        Mat p2 = Imgcodecs.imread("2p.png");
        WinnerMatcher wm = new WinnerMatcher(p1, p2);
        switch (wm.judgeWinner()) {
            case WinnerMatcher.FAILED:
                System.out.println("judge failed");
                break;
            case WinnerMatcher.PLAYER1WIN:
                System.out.println("Player 1 win");
                break;
            case WinnerMatcher.PLAYER2WIN:
                System.out.println("Player 2 win");
                break;
            default:
                break;
        }
    }
    */
}
