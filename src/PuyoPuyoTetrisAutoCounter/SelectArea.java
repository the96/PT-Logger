package PuyoPuyoTetrisAutoCounter;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SelectArea extends JFrame implements ComponentListener,NativeMouseInputListener,WindowListener {
    Capture capture;
    Rectangle area;
    JLabel label;
    long time;
    boolean flag;
    boolean clickFlag;

    SelectArea() throws AWTException {
        this.addComponentListener(this);
        GlobalScreen.addNativeMouseListener(this);
        this.addWindowListener(this);
        this.setSize(200, 200);
        label = new JLabel();
        this.getContentPane().add(label);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setVisible(true);
        capture = new Capture();
        reloadBackground();
        time = System.currentTimeMillis();
        flag = false;
        clickFlag = false;
    }

    public static void registerNativeHook() {
        try {
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.WARNING);
            logger.setUseParentHandlers(false);
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }

    public void reopenWindow() {
        this.setVisible(true);
        this.addComponentListener(this);
        GlobalScreen.addNativeMouseListener(this);

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

    public void reloadBackground() {
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
            reloadBackground();
            flag = false;
        }
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {

    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.closeWindow();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
