import java.net.Socket;
import java.util.ArrayList;

public class NetworkUtils {
	
	// dir is 0-7
	public static int reverseDir(int dir) {
		return 7 - dir;
	}
	
	public static boolean isDiagonal(int dir) {
		return dir == 0 || dir == 2 || dir == 5 || dir == 7;
	}
	
	public enum Kneighbor {
		UPLEFT,
		UP,
		UPRIGHT,
		LEFT,
		RIGHT,
		DOWNLEFT,
		DOWN,
		DOWNRIGHT
	}
}
