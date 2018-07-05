package PuyoPuyoTetrisAutoCounter;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.BufferedInputStream;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        p1WinCnt = 0;
        p2WinCnt = 0;
    }
    public void startCount(Rectangle p1, Rectangle p2) {
        p1WinCnt = 0;
        p2WinCnt = 0;
        this.setP1Score(p1WinCnt);
        this.setP2Score(p2WinCnt);
        //this.setP1Name(p1name);
        //this.setP2Name(p2name);
        Capture p1Cap = null;
        try {
            p1Cap = new Capture(p1);
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
        p1Score.setText(num + "");
    }
    public void setP2Score(int num) {
        p2Score.setText(num + "");
    }
    public void captureThread(Capture p1Capture, Capture p2Capture) {
            new Thread(() -> {
                //while(true){
                    long time = System.currentTimeMillis();
                    BufferedImage bufferedImage1 = p1Capture.takePicture();
                    BufferedImage bufferedImage2 = p2Capture.takePicture();
                    time = System.currentTimeMillis() - time;
                    switch (this.judge(bufferedImage1,bufferedImage2)) {
                        case WinnerMatcher.FAILED:
                            // Cannnot find winner logo
                            System.out.println("cannot judge");
                            break;
                        case WinnerMatcher.PLAYER1WIN:
                            p1WinCnt++;
                            this.setP1Score(p1WinCnt);
                            System.out.println("p1win");
                            break;
                        case WinnerMatcher.PLAYER2WIN:
                            p2WinCnt++;
                            this.setP2Score(p2WinCnt);
                            System.out.println("p2win");
                            break;
                        default:
                            break;
                    }
                    if (time <= 50) {
                        try {
                            Thread.sleep(50 - time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                //}
            }).start();
    }

    static Mat convertBufferedImageToMat(BufferedImage bufferedImage) {
        int w = bufferedImage.getWidth();
        int h = bufferedImage.getHeight();
        int[] pixels = new int[w * h * 3];
        bufferedImage.getRaster().getPixels(0, 0, w, h, pixels);
        byte[] bytes = new byte[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            bytes[i] = (byte) pixels[i];
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
