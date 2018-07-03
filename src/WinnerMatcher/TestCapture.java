import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class TestCapture extends JFrame{
	private Robot robot;
	private Rectangle area;
	TestCapture() throws AWTException {
		super("test capture frame");
		robot = new Robot();
		area = new Rectangle(0,0,600,600);
	}

	public BufferedImage capture() {
		return robot.createScreenCapture(area);
	}
}