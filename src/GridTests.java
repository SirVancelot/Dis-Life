public class GridTests {
	public static void main(String[] args) {
		Grid grid = new Grid();
		
		grid.setCell(1, 2, true);
		grid.setCell(2, 2, true);
		grid.setCell(3, 2, true);
		
		for (int i = 0; i < 12; i ++) {
			System.out.println(grid);
			grid.tick();
		}
		
		grid.setCell(0, 2, true);
		for (int i = 0; i < 12; i ++) {
			System.out.println(grid);
			grid.tick();
		}
		
		boolean[] tr = new boolean[10];
		for (int i = 0; i < tr.length; i++) {
			tr[i] = true;
		}
		
		grid.setLeft(tr);
		System.out.println(grid);
		
		
		grid.setRight(tr);
		System.out.println(grid);
		
		grid.setUp(tr);
		System.out.println(grid);

		grid.setDown(tr);
		System.out.println(grid);
		
		grid.setUpleft(true);
		System.out.println(grid);
		
		
		grid.setUpright(true);
		System.out.println(grid);
		
		grid.setDownleft(true);
		System.out.println(grid);
		
		
		grid.setDownright(true);
		System.out.println(grid);

		for (int i = 0; i < 12; i ++) {
			System.out.println(grid);
			grid.tick();
		}
	}
}
