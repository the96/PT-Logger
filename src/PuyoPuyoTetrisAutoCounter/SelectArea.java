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
    boolean autoFitFlag;
    Rectangle befSize;

    SelectArea() throws AWTException {
        this.addComponentListener(this);
        GlobalScreen.addNativeMouseListener(this);
        this.addWindowListener(this);
        this.setSize(200, 200);
        befSize = new Rectangle(200,200);
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

    public void setAutoFitFlag (boolean flag) {
        autoFitFlag = flag;
    }

    public void reopenWindow() {
        this.setVisible(true);
    }

    public void closeWindow() {
        this.setVisible(false);
    }

    public Rectangle getArea() {
        return area;
    }

    public void reloadBackground() {
        new Thread(() -> {
            try {
                if (autoFitFlag) {
                    Thread.sleep(100);
                    fittingAspectRatio();
                }
                Thread.sleep(100);
                area = convertViewSize(new Rectangle(getX(),getY(),getWidth(),getHeight()));
                this.setVisible(false);
                Thread.sleep(200);
                capture.setRectangle(area);
                ImageIcon img = new ImageIcon(capture.takePicture());
                label = new JLabel(img);
                this.getContentPane().removeAll();
                this.getContentPane().add(label);
                this.setVisible(true);
                befSize = area;
                flag = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public Rectangle convertViewSize(Rectangle rec) {
        Insets insets = this.getInsets();
        Rectangle area = new Rectangle(rec.x + insets.left,rec.y + insets.top ,
                rec.width - insets.left - insets.right,
                rec.height - insets.top - insets.bottom);
        return area;
    }

    public Dimension convertWindowSize(Rectangle rec) {
        Insets insets = this.getInsets();
        Dimension area = new Dimension(rec.width + insets.left + insets.right,
                rec.height + insets.top + insets.bottom);
        return area;
    }

    public Dimension convertWindowSize(int width, int height) {
        Insets insets = this.getInsets();
        Dimension area = new Dimension(width + insets.left + insets.right,
                height + insets.top + insets.bottom);
        return area;
    }

    public void fittingAspectRatio() {
        Rectangle size = convertViewSize(new Rectangle(getX(),getY(),getWidth(),getHeight()));
        double distWidth = Math.abs((double)1 - (double)size.width / (double)befSize.width);
        double distHeight = Math.abs((double)1 - (double)size.height / (double)befSize.height);
        // 変化量の大きいほうに合わせてアスペクト比を合わせる
        double ratio = 1.81293302;
        if (distWidth > distHeight) {
            int h = (int) (size.width * ratio);
            int w = (int) (h / ratio);
            this.setSize(convertWindowSize(w,h));

            System.out.println(w + "/" + h);
        } else {
            int w = (int) (size.height / ratio);
            int h = (int) (w * ratio);
            this.setSize(convertWindowSize(w,h));

            System.out.println(w + "/" + h);
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if (!flag)
            flag = true;
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        if (!flag)
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
        // 一瞬だけクリックしてウィンドウサイズを変更した場合にreloadが発動したいことがあるため、50ms待機しておく
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (flag) {
            reloadBackground();
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
