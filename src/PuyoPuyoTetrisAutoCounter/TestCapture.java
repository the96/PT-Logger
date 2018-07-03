package PuyoPuyoTetrisAutoCounter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TestCapture{
	private Robot robot;
	private Rectangle area;
	TestCapture() throws AWTException {
		this.robot = new Robot();
		this.area = new Rectangle(0,0,600,600);
	}
	TestCapture(Rectangle rectangle) throws AWTException{
		this.robot = new Robot();
		this.area = rectangle;
	}
	public void setRectangle(Rectangle rectangle) {
		this.area = rectangle;
	}

	public BufferedImage capture() {
		return robot.createScreenCapture(area);
	}
}