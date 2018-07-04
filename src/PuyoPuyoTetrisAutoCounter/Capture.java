package PuyoPuyoTetrisAutoCounter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Capture {
	private Robot robot;
	private Rectangle area;
	Capture() throws AWTException {
		this.robot = new Robot();
		this.area = new Rectangle(0,0,600,600);
	}
	Capture(Rectangle rectangle) throws AWTException{
		this.robot = new Robot();
		this.area = rectangle;
	}
	public void setRectangle(Rectangle rectangle) {
		this.area = rectangle;
	}

	public BufferedImage takePicture() {
		return robot.createScreenCapture(area);
	}
}