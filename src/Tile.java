/**
 * Represents a tile of the game map.
 */
public class Tile implements Comparable<Tile> {
    private final int row;
    
    private final int col;
    
    private double lastVisited = 0;
    
    boolean targeted = false; // True if another ant has issued an order to move to this location
    
    private double pValue = constants.NORMAL_VALUE; // default value of land is 100

	private boolean isVisible = false; // not visible by default
    
    private boolean isPassable = true;
    
    private int type = constants.LAND;
    
    private int enemyInRadius = 0; // store number of enemies that can attack this tile
    
    private int numFriendlyPotential = 0;
    
    public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public boolean isOccupied() { // tiles are not set to ants very often so needs fixing
		if (this.type == constants.MY_ANT)
			return true;
		else
			return false;
	}

	public boolean isPassable() { // Cannot pass if water
		if (type == constants.WATER) {
			return false;
		}
		else
			return true;
	}

	public void setPassable(boolean isPassable) {
		this.isPassable = isPassable;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/**
     * Creates new {@link Tile} object.
     * 
     * @param row row index
     * @param col column index
     */
    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
  
    public boolean isTargeted() {
    	return targeted;
    }
    
    public void setTargeted(boolean targeted) {
    	this.targeted = targeted;
    }
    /**
     * Returns row index.
     * 
     * @return row index
     */
    public int getRow() {
        return row;
    }
    
    public double getPValue() {
    	return pValue;
    }
    
    public void setPValue(double pValue) {
    	this.pValue = pValue;
    }
    
    public void increasePValue(double incPValue) {
    	this.pValue += incPValue;
    }
    
    /**
     * Returns column index.
     * 
     * @return column index
     */
    public int getCol() {
        return col;
    }
    
    /** 
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Tile o) {
        return hashCode() - o.hashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return row * Ants.MAX_MAP_SIZE + col;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof Tile) {
            Tile tile = (Tile)o;
            result = row == tile.row && col == tile.col;
        }
        return result;
    }
    
    @Override
	public String toString() {
		return "Tile [row=" + row + ", col=" + col + ", numFriendlyPotential " + numFriendlyPotential + ", targeted=" + targeted
				+ ", pValue=" + pValue + ", isOccupied " + isOccupied() + ", isVisible=" + isVisible
				+ ", isPassable=" + isPassable() + ", type=" + type + "]";
	}

	public double getLastVisited() {
		return lastVisited;
	}

	public void setLastVisited(int lastVisited) {
		this.lastVisited = lastVisited;
	}
	
	public void increaseLastVisited() {
		if (lastVisited < 10)
			lastVisited += 0.05;
	}
	
	public void decreaseLastVisited() {
		if (lastVisited > 0)
			lastVisited -= 0.05;
	}

	public int getEnemyInRadius() {
		return enemyInRadius;
	}

	public void setEnemyInRadius(int enemyInRadius) {
		this.enemyInRadius = enemyInRadius;
	}
	
	public void increaseEnemyInRadius() {
		this.enemyInRadius++;
	}
	
	public void decreaseEnemyInRadius() {
		this.enemyInRadius--;
	}

	public int getNumFriendlyPotential() {
		return numFriendlyPotential;
	}

	public void setNumFriendlyPotential(int numFriendlyPotential) {
		this.numFriendlyPotential = numFriendlyPotential;
	}
	
	public void increaseNumFriendlyPotential() {
		numFriendlyPotential++;
	}
	
	public void decreaseNumFriendlyPotential() {
		numFriendlyPotential--;
	}
}
