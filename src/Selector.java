import java.util.ArrayList;

public class Selector<T> {
	int ri; // The row index of the next payload.
	int ci; // The col index of the next payload.

	ArrayList<T> lastRow; // The last row of payloads. 
	ArrayList<T> lastCol; // The last col of payloads.
	ArrayList<T> curRow; // The current row of payloads.
	ArrayList<T> curCol; // The current col of payloads.
	
	public Selector() {
		ri = 0;
		ci = 0;
		lastRow = new ArrayList<T>();
		lastCol = new ArrayList<T>();
		curRow = new ArrayList<T>();
		curCol = new ArrayList<T>();
	}
	
	public ArrayList<T> insertAndGetNeighbors(T client) {
		ArrayList<T> neighbors = new ArrayList<T>();
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if (j == 0 && i == 0) continue;
				neighbors.add(getNeighbor(ri + i, ci + j));
			}
		}
		if (curRow.size() == lastRow.size() &&
				curCol.size() == lastCol.size()) {
			curRow.add(client);
			curCol.add(client);
			lastRow = curRow;
			lastCol = curCol;
			curRow = new ArrayList<T>(); 
			curCol = new ArrayList<T>(); 
			ri++;
			ci = 0;
		} else if (curCol.size() < curRow.size()) {
			curCol.add(client);
			ri = lastCol.size();
			ci = curRow.size();
		} else {
			curRow.add(client);
			ri = curCol.size();
			ci = lastRow.size();
		}
		return neighbors;
	}
	
	private T getNeighbor(int row, int col) {
		int cmax = lastCol.size();
		int rmax = lastRow.size();
		
		if (row < 0 || col < 0) return null;

		if (col == cmax && row < curCol.size()) {
			return curCol.get(row);
		} else if (col == cmax - 1 && row < lastCol.size()) {
			return lastCol.get(row);
		} else if (row == rmax && col < curRow.size()) {
			return curRow.get(col);
		} else if (row == rmax - 1 && col < lastRow.size()) {
			return lastRow.get(col);
		}

		return null;
	}
}
