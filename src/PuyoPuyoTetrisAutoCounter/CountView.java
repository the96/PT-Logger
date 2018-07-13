package PuyoPuyoTetrisAutoCounter;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

public class CountView implements Initializable{
    @FXML
    Label p1Name;
    @FXML
    Label p2Name;
    @FXML
    Label p1Score;
    @FXML
    Label p2Score;
    @FXML
    Label p1SetLabel;
    @FXML
    Label p2SetLabel;
    @FXML
    Label winCountLabel;
    @FXML
    Label winCount;
    @FXML
    TextField p1NameField;
    @FXML
    TextField p2NameField;
    @FXML
    TextField p1ScoreField;
    @FXML
    TextField p2ScoreField;
    @FXML
    TextField p1SetField;
    @FXML
    TextField p2SetField;
    GridPane gridPane;

    int p1WinCnt, p2WinCnt;
    int p1SetCnt, p2SetCnt;
    int p1Sets, p2Sets;
    int setNum;
    boolean judgeFlag;
    boolean engFlag;
    boolean threadRunning;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        p1WinCnt = 0;
        p2WinCnt = 0;
        p1SetCnt = 0;
        p2SetCnt = 0;
        p1Sets = 0;
        p2Sets = 0;
        setNum = 2;
        judgeFlag = false;
    }
    public void setGridPane(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    public void startCount(Rectangle p1, Rectangle p2, boolean engFlag) {
        p1WinCnt = 0;
        p2WinCnt = 0;
        this.engFlag = engFlag;
        this.setP1Score(p1WinCnt);
        this.setP2Score(p2WinCnt);
        try {
            Capture p1Cap = new Capture(p1);
            Capture p2Cap = new Capture(p2);
            captureStart(p1Cap,p2Cap);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void stopCount() {
        threadRunning = false;
    }

    public void setP1Name (String name) {
        p1Name.setText(name);
    }
    public void setP2Name (String name) {
        p2Name.setText(name);
    }

    public void setP1Score (int num) {
        Platform.runLater(() -> p1Score.setText(num + ""));
    }
    public void setP2Score(int num) {
        Platform.runLater(() -> p2Score.setText(num + ""));
    }

    public void setNum(int num) {
        setNum = num;
        winCount.setText(this.setNum + "");
    }

    public void setEngFlag(boolean flag) {
        engFlag = flag;
    }

    public void setCountVisible(boolean flag) {
        p1SetLabel.setVisible(flag);
        p2SetLabel.setVisible(flag);
        winCountLabel.setVisible(flag);
        winCount.setVisible(flag);
        if (!flag) {
            gridPane.setPrefSize(gridPane.getWidth(),gridPane.getMinHeight());
        }
    }

    public void captureStart(Capture p1Capture, Capture p2Capture) {
            threadRunning = true;
            new Thread(() -> {
                while(threadRunning){
                    long time = System.currentTimeMillis();
                    long interval = 50;
                    BufferedImage bufferedImage1 = p1Capture.takePicture();
                    BufferedImage bufferedImage2 = p2Capture.takePicture();
                    time = System.currentTimeMillis() - time;
                    switch (this.judge(bufferedImage1,bufferedImage2)) {
                        case WinnerMatcher.FAILED:
                            // Cannnot find winner logo
                            judgeFlag = false;
                            break;
                        case WinnerMatcher.PLAYER1WIN:
                            interval = 5000;
                            if (!judgeFlag) {
                                p1WinCnt++;
                                p1SetCnt++;
                                this.setP1Score(p1WinCnt);
                                System.out.println("p1win");
                                judgeFlag = true;
                            }
                            break;
                        case WinnerMatcher.PLAYER2WIN:
                            interval = 5000;
                            if (!judgeFlag) {
                                p2WinCnt++;
                                p2SetCnt++;
                                this.setP2Score(p2WinCnt);
                                System.out.println("p2win");
                                judgeFlag = true;
                            }
                            break;
                        default:
                            break;
                    }
                    if (p1SetCnt >= setNum || p2SetCnt >= setNum) {
                        if (p1SetCnt > p2SetCnt) {
                            p1Sets++;
                            Platform.runLater(() -> {p1SetLabel.setText(p1Sets + "");});
                        } else {
                            p2Sets++;
                            Platform.runLater(() -> {p2SetLabel.setText(p2Sets + "");});
                        }
                        p1SetCnt = 0;
                        p2SetCnt = 0;
                    }
                    if (time <= interval) {
                        try {
                            Thread.sleep(interval - time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
    }

    public void stopThread() {
        threadRunning = false;
    }

    static Mat convertBufferedImageToMat(BufferedImage bufferedImage) {
        // DataBuffereByteからbyte[]を生成する方法だと上手くキャストできなかったので自力で実装した
        int w = bufferedImage.getWidth();
        int h = bufferedImage.getHeight();
        int[] pixels = new int[w * h * 3];
        bufferedImage.getRaster().getPixels(0, 0, w, h, pixels);
        byte[] bytes = new byte[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            /*
            RGBがそれぞれ配列に格納されている
            何故かRとBの順番がMatlabとBufferedImageで逆になってるぽいので、
            0番目と2番目の要素を入れ替えている
             */

            bytes[i] = (byte) pixels[i+(i%3-1)*-2];
        }
        Mat mat = new Mat(bufferedImage.getHeight(),bufferedImage.getWidth(),CvType.CV_8UC3);
        mat.put(0,0,bytes);
        return mat;
    }

    public int judge(BufferedImage b1, BufferedImage b2) {
        Mat p1 = convertBufferedImageToMat(b1);
        Mat p2 = convertBufferedImageToMat(b2);
        WinnerMatcher wm = new WinnerMatcher(p1, p2, this.engFlag);
        return wm.judgeWinner();
    }
    @FXML
    public void renameP1() {
        Controller.rewriteLabel(p1Name,p1NameField);
    }
    @FXML
    public void renameP2() {
        Controller.rewriteLabel(p2Name,p2NameField);
    }
    @FXML
    public void inputP1() {
        Controller.inputNewText(p1Name,p1NameField);
    }
    @FXML
    public void inputP2() {
        Controller.inputNewText(p2Name,p2NameField);
    }
    @FXML
    public void overwriteScoreP1(){
        if (Controller.rewriteLabelNumber(p1Score,p1ScoreField))
            p1WinCnt = Integer.parseInt(p1ScoreField.getText());
    }
    @FXML
    public void overwriteScoreP2(){
        if (Controller.rewriteLabelNumber(p2Score,p2ScoreField))
            p2WinCnt = Integer.parseInt(p2ScoreField.getText());
    }
    @FXML
    public void inputScoreP1() {
        Controller.inputNewText(p1Score,p1ScoreField);
    }
    @FXML
    public void inputScoreP2() {
        Controller.inputNewText(p2Score,p2ScoreField);
    }
    @FXML
    public void overwriteSetsP1(){
        if (Controller.rewriteLabelNumber(p1SetLabel,p1SetField))
            p1Sets = Integer.parseInt(p1SetField.getText());
    }
    @FXML
    public void overwriteSetsP2(){
        if (Controller.rewriteLabelNumber(p2SetLabel,p2SetField))
            p2Sets = Integer.parseInt(p2SetField.getText());
    }
    @FXML
    public void inputSetsP1() {
        Controller.inputNewText(p1SetLabel,p1SetField);
    }
    @FXML
    public void inputSetsP2() {
        Controller.inputNewText(p2SetLabel,p2SetField);
    }
}
