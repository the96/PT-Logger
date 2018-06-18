import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class Main {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String args[]) {
		Mat p1 = Imgcodecs.imread("1p.png");
		Mat p2 = Imgcodecs.imread("2p.png");
		WinnerMatcher wm = new WinnerMatcher(p1, p2);
		switch (wm.judgeWinner()) {
			case WinnerMatcher.FAILED:
				System.out.println("judge failed");
				break;
			case WinnerMatcher.PLAYER1WIN:
				System.out.println("Player 1 win");
				break;
			case WinnerMatcher.PLAYER2WIN:
				System.out.println("Player 2 win");
				break;
			default:
				break;
		}
	}
}
