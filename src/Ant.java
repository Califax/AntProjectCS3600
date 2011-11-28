
public class Ant {
	
	private int row;
	private int col;
	
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	
	public Ant(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	public void setCord(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	public Pair getCord() {
		return new Pair(row, col);
	}
	
	@Override
	public String toString() {
		return "Ant [row=" + row + ", col=" + col + "]";
	}

}
