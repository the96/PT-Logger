package PuyoPuyoTetrisAutoCounter;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/*
 * フィールドの広さ = 173:334 = およそ1:1.93
 * ロゴサイズ = 125:47
 * フィールドとロゴの横幅の比は
 * 173:125 = 1.384:1
 * ロゴサイズの横幅*1.384のサイズになるように横幅をリサイズすればよい
 */

public class WinnerMatcher {
	static String win_path = "winner.png";
	public static final int PLAYER1WIN = 1;
	public static final int PLAYER2WIN = 2;
	public static final int FAILED = -1;
	Mat win;
	Mat p1;
	Mat p2;

	public WinnerMatcher(Mat p1, Mat p2) {
		win = Imgcodecs.imread(win_path,Imgcodecs.IMREAD_COLOR);
		Size fieldSize = new Size(win.cols() * 1.384,win.rows() * 7.106);
		this.p1 = p1;
		this.p2 = p2;
		Imgproc.resize(this.p1, this.p1, fieldSize);
		Imgproc.resize(this.p2, this.p2, fieldSize);
	}
	public WinnerMatcher(String p1_path, String p2_path) {
		win = Imgcodecs.imread(win_path,Imgcodecs.IMREAD_COLOR);
		Size fieldSize = new Size(win.cols() * 1.384,win.rows() * 7.106);
		this.p1 = Imgcodecs.imread(p1_path);
		Imgproc.resize(this.p1, this.p1, fieldSize);
		this.p2 = Imgcodecs.imread(p2_path);
		Imgproc.resize(this.p2, this.p2, fieldSize);
	}

	public int judgeWinner() {
		double p1Win = matchWinner(p1,"P1");
		double p2Win = matchWinner(p2,"P2");
		if (p1Win < 0.6 && p2Win < 0.6)	{
			return FAILED;
		}
		if (p1Win > p2Win) {
			return PLAYER1WIN;
		}
		return PLAYER2WIN;
	}

	public double matchWinner(Mat field,String player) {
		Mat result = new Mat(field.rows() - win.rows() + 1,field.cols() - win.cols() + 1, CvType.CV_32FC1);
		Mat resultImg = field.clone();
		Imgproc.matchTemplate(field, win, result, Imgproc.TM_CCOEFF_NORMED);
		Imgproc.threshold(result, result, 0.5, 1.0, Imgproc.THRESH_TOZERO);
		double max = 0;
		Point matching = new Point();
		for (int y = 0; y < result.rows(); y++) {
			for (int x = 0; x < result.cols(); x++) {
				double color = result.get(y, x)[0];
				if (max < color) {
					matching.x = x;
					matching.y = y;
					max = color;
				}
			}
		}
		if (max > 0.0) {
			Point matchEnd = new Point(matching.x + win.cols(), matching.y + win.rows());
			Imgproc.rectangle(resultImg, matching, matchEnd, new Scalar(255,0,0));
		}
		Imgcodecs.imwrite("res" + player + ".png", resultImg);
		//System.out.println("max color:" + max);
		return max;
	}
}
