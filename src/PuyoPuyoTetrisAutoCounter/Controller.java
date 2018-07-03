package PuyoPuyoTetrisAutoCounter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    Button openButton;
    @FXML
    Button setPlayer1;
    @FXML
    Button setPlayer2;
    @FXML
    Button previewButton;
    @FXML
    Button okButton;

    SelectArea selectArea;
    Rectangle p1Area, p2Area;
    Stage stage;
    Stage previewStage;
    PreviewWindow previewWindow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        previewStage = new Stage();
        previewStage.initModality(Modality.NONE);
        previewStage.initOwner(stage);
        p1Area = new Rectangle(0,0,0,0);
        p2Area = new Rectangle(0,0,0,0);
    }

    public void setStage (Stage stage) {
        this.stage = stage;
    }

    public void openSelectArea() {
        try {
            selectArea = new SelectArea();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openPreview() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("preview.fxml"));
            SplitPane splitPane = loader.load();
            previewWindow = loader.getController();
            previewWindow.setArea(p1Area,p2Area);
            previewStage.setScene(new Scene(splitPane));
            previewStage.show();
            if (previewWindow.startCapture()) {
                System.out.println("running preview");
            } else {
                System.out.println("failed to run preview");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void setPlayer1Area() {
        p1Area = selectArea.getArea();
    }

    @FXML
    public void setPlayer2Area() {
        p2Area = selectArea.getArea();
    }

    @FXML
    public void completeSetArea() {
        selectArea.closeWindow();
    }

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
}
