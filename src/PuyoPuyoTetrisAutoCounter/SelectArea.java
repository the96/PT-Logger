package PuyoPuyoTetrisAutoCounter;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SelectArea extends JFrame implements ComponentListener,NativeMouseInputListener {
    Capture capture;
    Rectangle area;
    JLabel label;
    long time;
    boolean flag;
    boolean clickFlag;

    SelectArea() throws AWTException {
        this.addComponentListener(this);
        try {
            GlobalScreen.registerNativeHook();
            suppressLogger();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
        GlobalScreen.addNativeMouseListener(this);
        this.setSize(200, 200);
        label = new JLabel();
        this.getContentPane().add(label);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        capture = new Capture();
        reloadBackground();
        time = System.currentTimeMillis();
        flag = false;
        clickFlag = false;
    }

    public void closeWindow() {
        this.setVisible(false);
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public Rectangle getArea() {
        return area;
    }

    /**
     * JNativeHookのロギングを抑制する。
     *
     * mainで一度設定してもそのあとでJNativeHook側でセットされるっぽいので別スレッドで2秒待ってセット。
     *
     * https://qiita.com/Getaji/items/8ad1887761ac222b61a2 からお借りしました
     */
    private static void suppressLogger() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });

        executorService.schedule(() -> {
            final Logger jNativeHookLogger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            if (jNativeHookLogger.getLevel() != Level.WARNING) {
                synchronized (jNativeHookLogger) {
                    jNativeHookLogger.setLevel(Level.WARNING);
                }
            }
        }, 2, TimeUnit.SECONDS);
    }

    public void reloadBackground() throws AWTException {
        new Thread(() -> {
            try {
                Thread.sleep(50);
                Insets insets = this.getInsets();
                area = new Rectangle(this.getX() + insets.left,this.getY() + insets.top, this.getWidth() - insets.left - insets.right, this.getHeight()  - insets.top - insets.bottom);
                this.setVisible(false);
                Thread.sleep(200);
                capture.setRectangle(area);
                ImageIcon img = new ImageIcon(capture.takePicture());
                label = new JLabel(img);
                this.getContentPane().removeAll();
                this.getContentPane().add(label);
                this.setVisible(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        flag = true;
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        flag = true;
    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {

    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {

    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
        if (flag) {
            try {
                reloadBackground();
            } catch (AWTException e) {
                e.printStackTrace();
            }
            flag = false;
        }
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {

    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {

    }
}
