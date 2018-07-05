package PuyoPuyoTetrisAutoCounter;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    @FXML
    CheckBox englishFlag;

    SelectArea selectArea;
    Rectangle p1Area, p2Area;
    Stage stage;
    Stage previewStage;
    Stage countViewStage;
    PreviewWindow previewWindow;
    CountView countView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        previewStage = new Stage();
        previewStage.initModality(Modality.NONE);
        previewStage.initOwner(stage);
        countViewStage = new Stage();
        countViewStage.initModality(Modality.NONE);
        countViewStage.initOwner(stage);
        countViewStage.setOnCloseRequest(e -> {
            countView.threadRunning = false;
        });
        p1Area = new Rectangle(0,0,0,0);
        p2Area = new Rectangle(0,0,0,0);
    }

    public void setStage (Stage stage) {
        this.stage = stage;
        this.stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(1);
        });
    }

    public void openSelectArea() {
        if (!readySelect())
        try {
            selectArea = new SelectArea();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidArea(Rectangle area) {
        return area.x != 0 && area.y != 0 && area.width != 0 && area.height !=0;
    }

    public boolean readySelect() {
        return selectArea != null;
    }

    public boolean readyArea() {
        return isValidArea(p1Area) && isValidArea(p2Area);
    }

    public void showAlertInvalidArea(boolean isValidP1, boolean isValidP2) {
        Alert alert = new Alert(Alert.AlertType.ERROR,"", ButtonType.CLOSE);
        alert.setTitle("Error: invalid area");
        alert.getDialogPane().setContentText("Invalid area:" + (isValidP1?"":" P1") + (isValidP2?"":" P2") + "\r\n"
                + "Please Set Area.");
        alert.showAndWait();
    }

    @FXML
    public void openPreview() {
        if (!readyArea()) {
            showAlertInvalidArea(isValidArea(p1Area),isValidArea(p2Area));
            return;
        }
        completeSetArea();
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
        if (readySelect()) {
            p1Area = selectArea.getArea();
        } else {
            openSelectArea();
        }
    }

    @FXML
    public void setPlayer2Area() {
        if (readySelect()) {
            p2Area = selectArea.getArea();
        } else {
            openSelectArea();
        }
    }

    @FXML
    public void completeSetArea() {
        selectArea.closeWindow();
    }

    @FXML
    public void countStart() {
        if (!readyArea()) {
            showAlertInvalidArea(isValidArea(p1Area),isValidArea(p2Area));
            return;
        }
        completeSetArea();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("countView.fxml"));
            GridPane gridPane = loader.load();
            countView = loader.getController();
            countViewStage.setScene(new Scene(gridPane));
            countViewStage.show();
            countView.startCount(p1Area,p2Area,englishFlag.isSelected());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
