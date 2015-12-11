import java.util.ArrayList;

public class SelectorTests {
	public static void main(String[] args) {
		Selector<Integer> sel = new Selector<Integer>();
		
		for (int i = 1; i <= 16; i++) {
			ArrayList<Integer> n = sel.insertAndGetNeighbors(i);
			System.out.println(i + " : " + n);
		}
	}
}
