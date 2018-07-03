package PuyoPuyoTetrisAutoCounter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class SelectAreaDialog implements Initializable {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
}
