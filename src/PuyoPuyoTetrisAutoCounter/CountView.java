package PuyoPuyoTetrisAutoCounter;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

public class CountView implements Initializable {
    @FXML
    Label p1Name;
    @FXML
    Label p2Name;
    @FXML
    Label p1Score;
    @FXML
    Label p2Score;

    int p1WinCnt, p2WinCnt;
    boolean judgeFlag;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        p1WinCnt = 0;
        p2WinCnt = 0;
        judgeFlag = false;
    }
    public void startCount(Rectangle p1, Rectangle p2) {
        p1WinCnt = 0;
        p2WinCnt = 0;
        this.setP1Score(p1WinCnt);
        this.setP2Score(p2WinCnt);
        //this.setP1Name(p1name);
        //this.setP2Name(p2name);
        try {
            Capture p1Cap = new Capture(p1);
            Capture p2Cap = new Capture(p2);
            captureThread(p1Cap,p2Cap);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void setP1Name (String name) {

    }
    public void setP2Name (String name) {

    }

    public void setP1Score (int num) {
        Platform.runLater(() -> p1Score.setText(num + ""));
    }

    public void setP2Score(int num) {
        Platform.runLater(() -> p2Score.setText(num + ""));
    }
    public void captureThread(Capture p1Capture, Capture p2Capture) {
            new Thread(() -> {
                while(true){
                    long time = System.currentTimeMillis();
                    long interval = 50;
                    BufferedImage bufferedImage1 = p1Capture.takePicture();
                    BufferedImage bufferedImage2 = p2Capture.takePicture();
                    time = System.currentTimeMillis() - time;
                    System.out.println("judge " + System.currentTimeMillis() / 1000);
                    switch (this.judge(bufferedImage1,bufferedImage2)) {
                        case WinnerMatcher.FAILED:
                            // Cannnot find winner logo
                            judgeFlag = false;
                            break;
                        case WinnerMatcher.PLAYER1WIN:
                            if (!judgeFlag) {
                                p1WinCnt++;
                                this.setP1Score(p1WinCnt);
                                System.out.println("p1win");
                                judgeFlag = true;
                                interval = 1500;
                            }
                            break;
                        case WinnerMatcher.PLAYER2WIN:
                            if (!judgeFlag) {
                                p2WinCnt++;
                                this.setP2Score(p2WinCnt);
                                System.out.println("p2win");
                                judgeFlag = true;
                                interval = 1500;
                            }
                            break;
                        default:
                            break;
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

    static Mat convertBufferedImageToMat(BufferedImage bufferedImage) {
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
        WinnerMatcher wm = new WinnerMatcher(p1, p2);
        return wm.judgeWinner();
    }
}
