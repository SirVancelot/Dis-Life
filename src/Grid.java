import java.util.Random;

public class Grid {
	public static final int INNER_SIZE = 10;
	public static final int NEIGHBOR_SIZE = 1;
	public static final int GRID_SIZE = INNER_SIZE + 2*NEIGHBOR_SIZE;

	private boolean[][] grid = new boolean[GRID_SIZE][GRID_SIZE];

	public boolean[][] getGrid() {
		return grid;
	}

	public void setGrid(boolean[][] nGrid) {
		for (int i = 0; i < INNER_SIZE; i++) {
			for (int j = 0; j < INNER_SIZE; j++) {
				grid[i + NEIGHBOR_SIZE][j + NEIGHBOR_SIZE] = nGrid[i][j];
			}
		}
	}

	public void tick() {
		boolean[][] newGrid = new boolean[GRID_SIZE][GRID_SIZE];
		for (int i = 1; i < GRID_SIZE - 1; i++) {
			for (int j = 1; j < GRID_SIZE - 1; j++) {
				int liveNeighbors = countLiveNeighbors(i, j);
				if (grid[i][j]) { // live
					if (liveNeighbors < 2) {
						// underpopulation
						newGrid[i][j] = false;
					} else if (liveNeighbors > 3) {
						// overpopulation
						newGrid[i][j] = false;
					} else {
						newGrid[i][j] = true;
					}
				} else { // dead
					if (liveNeighbors == 3) {
						// reproduction
						newGrid[i][j] = true;
					} else {
						newGrid[i][j] = false;
					}
				}
			}
		}
		grid = newGrid;
	}

	private int countLiveNeighbors(int i, int j) {
		int liveNeighbors = 0;
		for (int di = -1; di <= 1; di++) {
			for (int dj = -1; dj <= 1; dj++) {
				if (di == 0 && dj == 0) continue;
				int row_neighbor = i + di;
				int col_neighbor = j + dj;
				if (getCell(row_neighbor, col_neighbor)) {
					liveNeighbors++;
				}
			}
		}
		return liveNeighbors;
	}

	public void setCell(int r, int c, boolean val) {
		grid[r][c] = val;
	}

	// gets a cell in grid, including the neighbor entries
	private boolean getCell(int i, int j) {
		if (i < 0 || j < 0 || i >= GRID_SIZE || j >= GRID_SIZE) {
			return false;
		}
		return grid[i][j];
	}

	@Override public String toString() {
		String res = "";
		for (int r = 0; r < GRID_SIZE; r++) {
			for (int c = 0; c < GRID_SIZE; c++) {
				if (getCell(r, c)) {
					res += "1";
				} else {
					res += "-";
				}
			}
			res += "\n";
		}
		return res;
	}

	public void setLeft(boolean[] data) {
		for (int i = 0; i < GRID_SIZE - 2; i++) {
			grid[i + 1][0] = data[i];
		}
	}

	public boolean[] getLeft() {
		boolean[] data = new boolean[GRID_SIZE - 2];
		for (int i = 0; i < data.length; i++) {
			data[i] = grid[i+1][1];
		}
		return data;
	}

	public void setRight(boolean[] data) {
		for (int i = 0; i < GRID_SIZE - 2; i++) {
			grid[i + 1][GRID_SIZE - 1] = data[i];
		}
	}

	public boolean[] getRight() {
		boolean[] data = new boolean[GRID_SIZE - 2];
		for (int i = 0; i < data.length; i++) {
			data[i] = grid[i + 1][GRID_SIZE - 2];
		}
		return data;
	}

	public void setUp(boolean[] data) {
		for (int i = 0; i < GRID_SIZE - 2; i++) {
			grid[0][i + 1] = data[i];
		}
	}

	public boolean[] getUp() {
		boolean[] data = new boolean[GRID_SIZE - 2];
		for (int i = 0; i < data.length; i++) {
			data[i] = grid[1][i + 1];
		}
		return data;
	}

	public void setDown(boolean[] data) {
		for (int i = 0; i < GRID_SIZE - 2; i++) {
			grid[GRID_SIZE - 1][i + 1] = data[i];
		}
	}

	public boolean[] getDown() {
		boolean[] data = new boolean[GRID_SIZE - 2];
		for (int i = 0; i < data.length; i++) {
			data[i] = grid[GRID_SIZE - 2][i + 1];
		}
		return data;
	}


	public void setUpleft(boolean data) {
		grid[0][0] = data;
	}


	public boolean getUpleft() {
		return grid[1][1];
	}


	public void setUpright(boolean data) {
		grid[0][GRID_SIZE - 1] = data;
	}


	public boolean getUpright() {
		return grid[1][GRID_SIZE - 2];
	}


	public void setDownleft(boolean data) {
		grid[GRID_SIZE - 1][ 0] = data;
	}


	public boolean getDownleft() {
		return grid[ GRID_SIZE - 2][1];
	}


	public void setDownright(boolean data) {
		grid[GRID_SIZE - 1][GRID_SIZE - 1] = data;
	}


	public boolean getDownright() {
		return grid[GRID_SIZE - 2][ GRID_SIZE - 2];
	}
	
	public boolean[] get(int dir) {
		if (NetworkUtils.isDiagonal(dir)) {
			boolean result = false;
			switch(dir) {
				case 0:
					result = getUpleft();
					break;
				case 2:
					result = getUpright();
					break;
				case 5:
					result = getDownleft();
					break;
				case 7:
					result = getDownright();
					break;
			}
			boolean[] resultArray = {result};
			return resultArray;
		} else {
			switch(dir) {
				case 1:
					return getUp();
				case 3:
					return getLeft();
				case 4:
					return getRight();
				case 6:
					return getDown();
			}
		}
		return null;
	}
	
	public void set(int dir, boolean[] data) {
		switch(dir) {
			case 1:
				setUp(data);
				break;
			case 3:
				setLeft(data);
				break;
			case 4:
				setRight(data);
				break;
			case 6:
				setDown(data);
				break;
			case 0:
				setUpleft(data[0]);
				break;
			case 2:
				setUpright(data[0]);
				break;
			case 5:
				setDownleft(data[0]);
				break;
			case 7:
				setDownright(data[0]);
				break;
		}
	}
	
	public void randomize() {
		for (int i = 1; i < GRID_SIZE - 1; i++) {
			for (int j = 1; j < GRID_SIZE - 1; j++) {
				Random r = new Random();
				grid[i][j] = r.nextBoolean();
			}
		}
	}
}
