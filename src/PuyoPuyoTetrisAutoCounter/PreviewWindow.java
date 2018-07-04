package PuyoPuyoTetrisAutoCounter;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;

public class PreviewWindow implements Initializable {
    Capture p1Capture;
    Capture p2Capture;
    @FXML
    ImageView player1view;
    @FXML
    ImageView player2view;
    boolean running;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            running = false;
            p1Capture = new Capture();
            p2Capture = new Capture();
        } catch (AWTException e) {
            System.out.println("Catch AWTException");
            e.printStackTrace();
        }
    }

    public void setArea(Rectangle p1area,Rectangle p2area) {
        p1Capture.setRectangle(p1area);
        p2Capture.setRectangle(p2area);
    }

    public boolean startCapture() {
        if (!running) {
            mainThread();
            return true;
        }
        return false;
    }

    private void mainThread() {
        running = true;
        new Thread(() -> {
            while(true){
                long time = System.currentTimeMillis();
                BufferedImage bufferedImage1 = p1Capture.takePicture();
                WritableImage img = SwingFXUtils.toFXImage(bufferedImage1,null);
                player1view.setImage(img);
                BufferedImage bufferedImage2 = p2Capture.takePicture();
                WritableImage img2 = SwingFXUtils.toFXImage(bufferedImage2,null);
                player2view.setImage(img2);
                time = System.currentTimeMillis() - time;
                if (time <= 16) {
                    try {
                        Thread.sleep(16 - time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
