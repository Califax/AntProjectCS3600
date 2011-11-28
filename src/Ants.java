import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Holds all game data and current game state.
 */
public class Ants {
    /** Maximum map size. */
    public static final int MAX_MAP_SIZE = 256 * 2;

    private int loadTime;

    private int turnTime;

    private int rows;

    private int cols;

    private int turns;

    private int viewRadius2;

    private int attackRadius2;

    private int spawnRadius2;

    //private final boolean visible[][];

    private  Set<Pair> visionOffSets;

    private long turnStartTime;

    private ArrayList<Ant> myAnts = new ArrayList<Ant>();

    private final Set<Tile> enemyAnts = new HashSet<Tile>();

    private final Set<Tile> myHills = new HashSet<Tile>();

    private final Set<Tile> enemyHills = new HashSet<Tile>();

    private final Set<Tile> foodTiles = new HashSet<Tile>();

    private final Set<Order> orders = new HashSet<Order>();
    
    private Tile grid[][]; // Grid of tiles representing map
    
    private Tile gridImage[][];
    
    private static Ants ant; 
    
    private ArrayList<Pair> visibleSquares = new ArrayList<Pair>();
    
    private ArrayList<Pair> foodList = new ArrayList<Pair>(); // store locations of food

	private ArrayList<Pair> oldFoodList = new ArrayList<Pair>();
	
	private ArrayList<Pair> enemyHillList = new ArrayList<Pair>();
	
	private ArrayList<Pair> ourHillList = new ArrayList<Pair>();
	
	private ArrayList<Ant> enemyAntList = new ArrayList<Ant>();
	
    

    public ArrayList<Pair> getOldFoodList() {
		return oldFoodList;
	}

	public void setOldFoodList(ArrayList<Pair> oldFoodList) {
		this.oldFoodList = oldFoodList;
	}

	/**
     * Creates new {@link Ants} object.
     * 
     * @param loadTime timeout for initializing and setting up the bot on turn 0
     * @param turnTime timeout for a single game turn, starting with turn 1
     * @param rows game map height
     * @param cols game map width
     * @param turns maximum number of turns the game will be played
     * @param viewRadius2 squared view radius of each ant
     * @param attackRadius2 squared attack radius of each ant
     * @param spawnRadius2 squared spawn radius of each ant
     */
    private Ants(int loadTime, int turnTime, int rows, int cols, int turns, int viewRadius2,
            int attackRadius2, int spawnRadius2) {
        this.loadTime = loadTime;
        this.turnTime = turnTime;
        this.rows = rows;
        this.cols = cols;
        this.turns = turns;
        this.viewRadius2 = viewRadius2;
        this.attackRadius2 = attackRadius2;
        this.spawnRadius2 = spawnRadius2;
        grid = new Tile[rows][cols];
        gridImage = new Tile[rows][cols]; //josh added for shits
        // ant = new Ants(loadTime, turnTime, rows, cols, turns, viewRadius2, attackRadius2, spawnRadius2);
    }
    
    public static Ants getInstance() {
    	if (ant == null)
    		ant = new Ants(0,0,0,0,0,0,0,0);
    	return ant;
    }
    
    public void setupAnts(int loadTime, int turnTime, int rows, int cols, int turns, int viewRadius2,
            int attackRadius2, int spawnRadius2) {
    	
        ant.loadTime = loadTime;
        ant.turnTime = turnTime;
        ant.rows = rows;
        ant.cols = cols;
        ant.turns = turns;
        ant.viewRadius2 = viewRadius2;
        ant.attackRadius2 = attackRadius2;
        ant.spawnRadius2 = spawnRadius2;
        ant.grid = new Tile[rows][cols]; 
        ant.gridImage = new Tile[rows][cols];
    }
    
    @Override
	public String toString() {
		return "Ants [loadTime=" + loadTime + ", turnTime=" + turnTime
				+ ", rows=" + rows + ", cols=" + cols + ", turns=" + turns
				+ ", viewRadius2=" + viewRadius2 + ", attackRadius2="
				+ attackRadius2 + ", spawnRadius2=" + spawnRadius2
				+ ", turnStartTime=" + turnStartTime + ", myAnts=" + myAnts
				+ ", enemyAnts=" + enemyAnts + ", myHills=" + myHills
				+ ", enemyHills=" + enemyHills + ", foodTiles=" + foodTiles
				+ ", orders=" + orders + ", grid[22][19] =" + (grid[22][19])
				+ "]";
	}

	public ArrayList<Ant> myAntList() {
    	return myAnts;
    }
    
    public void intitializeTiles() {
        for (int i = 0; i < rows; i++) {  // For every tile in every row and column fill it with initial land.
        	for (int j = 0; j < cols; j++) {
        		grid[i][j]= new Tile(i, j); // Create a new tile object at every location on map
        		gridImage[i][j] = new Tile(i, j);
        }
      }
    }
    
    // calc vision offsets
    public void calcVision() {    
        visionOffSets = new HashSet<Pair>(); 
        int mx = (int)Math.sqrt(viewRadius2);
        for (int row = -mx; row <= mx; ++row) {
            for (int col = -mx; col <= mx; ++col) {
                int d = row * row + col * col;
                if (d <= viewRadius2) {
                	visionOffSets.add(new Pair(row, col));        
                }
            }
        }	
    }
    
    
    public void setVision() {
    	calcVision();
    	
        for (Ant antObj : myAnts) { // for every ant our array list
            for (Pair locOffset : visionOffSets) {
                Pair newLoc = getPair(antObj.getCord(), locOffset);
                Pair antObjLoc = new Pair(antObj.getRow(), antObj.getCol());
                visibleSquares.add(newLoc);
                visibleSquares.add(antObjLoc);
                grid[newLoc.getRow()][newLoc.getCol()].setVisible(true); // Tile is now visible
                grid[antObj.getRow()][antObj.getCol()].setVisible(true);
            }
        }
    }
    
   
    /**
     * Returns timeout for initializing and setting up the bot on turn 0.
     * 
     * @return timeout for initializing and setting up the bot on turn 0
     */
    public int getLoadTime() {
        return loadTime;
    }

    /**
     * Returns timeout for a single game turn, starting with turn 1.
     * 
     * @return timeout for a single game turn, starting with turn 1
     */
    public int getTurnTime() {
        return turnTime;
    }

    /**
     * Returns game map height.
     * 
     * @return game map height
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns game map width.
     * 
     * @return game map width
     */
    public int getCols() {
        return cols;
    }

    /**
     * Returns maximum number of turns the game will be played.
     * 
     * @return maximum number of turns the game will be played
     */
    public int getTurns() {
        return turns;
    }

    /**
     * 
     * Returns squared view radius of each ant.
     * 
     * @return squared view radius of each ant
     */
    public int getViewRadius2() {
        return viewRadius2;
    }

    /**
     * Returns squared attack radius of each ant.
     * 
     * @return squared attack radius of each ant
     */
    public int getAttackRadius2() {
        return attackRadius2;
    }

    /**
     * Returns squared spawn radius of each ant.
     * 
     * @return squared spawn radius of each ant
     */
    public int getSpawnRadius2() {
        return spawnRadius2;
    }

    /**
     * Sets turn start time.
     * 
     * @param turnStartTime turn start time
     */
    public void setTurnStartTime(long turnStartTime) {
        this.turnStartTime = turnStartTime;
    }

    /**
     * Returns how much time the bot has still has to take its turn before timing out.
     * 
     * @return how much time the bot has still has to take its turn before timing out
     */
    public int getTimeRemaining() {
        return turnTime - (int)(System.currentTimeMillis() - turnStartTime);
    }

    
    
    public Tile[][] getGrid() {
    	return grid;
    }
      

    /**
     * Returns location in the specified direction from the specified location.
     * 
     * @param tile location on the game map
     * @param direction direction to look up
     * 
     * @return location in <code>direction</code> from <cod>tile</code>
     */
    public Tile getTile(Tile tile, Aim direction) {
        int row = (tile.getRow() + direction.getRowDelta()) % rows;
        if (row < 0) {
            row += rows;
        }
        int col = (tile.getCol() + direction.getColDelta()) % cols;
        if (col < 0) {
            col += cols;
        }
        return grid[row][col];
    }

    public Tile getTile(Ant ant, Aim direction) {
        int row = (ant.getRow() + direction.getRowDelta()) % rows;
        if (row < 0) {
            row += rows;
        }
        int col = (ant.getCol() + direction.getColDelta()) % cols;
        if (col < 0) {
            col += cols;
        }
        return grid[row][col];
    }
    /**
     * Returns location with the specified offset from the specified location.
     * 
     * @param tile location on the game map
     * @param offset offset to look up
     * 
     * @return location with <code>offset</code> from <cod>tile</code>
     */
    public Pair getPair(Pair pair, Pair offset) {
        int row = (pair.getRow() + offset.getRow()) % rows;
        if (row < 0) {
            row += rows;
        }
        int col = (pair.getCol() + offset.getCol()) % cols;
        if (col < 0) {
            col += cols;
        }
        return new Pair(row, col);
    }
    
    public Tile getTile(int row, int col) {
    	return grid[row][col];
    }
    
    public Tile getTile(Ant ant) {
    	return grid[ant.getRow()][ant.getCol()];
    }
    

    /**
     * Returns a arrayList containing all my ants
     * 
     * @return an arrayList containing all my ants locations
     */
    public ArrayList<Ant> getMyAnts() {
        return myAnts;
    }
    

    /**
     * Returns a set containing all enemy ants locations.
     * 
     * @return a set containing all enemy ants locations
     */
    public Set<Tile> getEnemyAnts() {
        return enemyAnts;
    }

    /**
     * Returns a set containing all my hills locations.
     * 
     * @return a set containing all my hills locations
     */
    public Set<Tile> getMyHills() {
        return myHills;
    }

    /**
     * Returns a set containing all enemy hills locations.
     * 
     * @return a set containing all enemy hills locations
     */
    public Set<Tile> getEnemyHills() {
        return enemyHills;
    }

    /**
     * Returns a set containing all food locations.
     * 
     * @return a set containing all food locations
     */
    public Set<Tile> getFoodTiles() {
        return foodTiles;
    }

    /**
     * Returns all orders sent so far.
     * 
     * @return all orders sent so far
     */
    public Set<Order> getOrders() {
        return orders;
    }

    /**
     * Calculates distance between two locations on the game map.
     * 
     * @param t1 one location on the game map
     * @param t2 another location on the game map
     * 
     * @return distance between <code>t1</code> and <code>t2</code>
     */
    public int getDistance(Pair t1, Pair t2) {
        int rowDelta = Math.abs(t1.getRow() - t2.getRow());
        int colDelta = Math.abs(t1.getCol() - t2.getCol());
        rowDelta = Math.min(rowDelta, rows - rowDelta);
        colDelta = Math.min(colDelta, cols - colDelta);
        return rowDelta * rowDelta + colDelta * colDelta;
    }
    
    public int getDistance(Ant t1, Pair t2) {
        int rowDelta = Math.abs(t1.getRow() - t2.getRow());
        int colDelta = Math.abs(t1.getCol() - t2.getCol());
        rowDelta = Math.min(rowDelta, rows - rowDelta);
        colDelta = Math.min(colDelta, cols - colDelta);
        return rowDelta * rowDelta + colDelta * colDelta;
    }

    public int getDistance(Ant t1, Ant t2) {
        int rowDelta = Math.abs(t1.getRow() - t2.getRow());
        int colDelta = Math.abs(t1.getCol() - t2.getCol());
        rowDelta = Math.min(rowDelta, rows - rowDelta);
        colDelta = Math.min(colDelta, cols - colDelta);
        return rowDelta * rowDelta + colDelta * colDelta;
    }
    /**
     * Returns one or two orthogonal directions from one location to the another.
     * 
     * @param t1 one location on the game map
     * @param t2 another location on the game map
     * 
     * @return orthogonal directions from <code>t1</code> to <code>t2</code>
     */
    public List<Aim> getDirections(Tile t1, Tile t2) {
        List<Aim> directions = new ArrayList<Aim>();
        if (t1.getRow() < t2.getRow()) {
            if (t2.getRow() - t1.getRow() >= rows / 2) {
                directions.add(Aim.NORTH);
            } else {
                directions.add(Aim.SOUTH);
            }
        } else if (t1.getRow() > t2.getRow()) {
            if (t1.getRow() - t2.getRow() >= rows / 2) {
                directions.add(Aim.SOUTH);
            } else {
                directions.add(Aim.NORTH);
            }
        }
        if (t1.getCol() < t2.getCol()) {
            if (t2.getCol() - t1.getCol() >= cols / 2) {
                directions.add(Aim.WEST);
            } else {
                directions.add(Aim.EAST);
            }
        } else if (t1.getCol() > t2.getCol()) {
            if (t1.getCol() - t2.getCol() >= cols / 2) {
                directions.add(Aim.EAST);
            } else {
                directions.add(Aim.WEST);
            }
        }
        return directions;
    }

    /**
     * Clears game state information about my ants locations.
     */
    public void clearMyAnts() {
        for (Ant myAnt : myAnts) {
            grid[myAnt.getRow()][myAnt.getCol()].setType(constants.LAND); // Set to land from where Ant was previously
        }
        myAnts.clear();
    }
    
    public void updateMyAnts() {
    	for (Ant myAnt : myAnts) {
    		//System.out.println(myAnt);
            grid[myAnt.getRow()][myAnt.getCol()].setType(constants.MY_ANT); // Set type to Ant for everywhere there I have an Ant
        }
    }

    /**
     * Clears game state information about enemy ants locations.
     */
    public void clearEnemyAnts() {
        for (Tile enemyAnt : enemyAnts) {
       //    map[enemyAnt.getRow()][enemyAnt.getCol()] = Ilk.LAND;
        }
        enemyAnts.clear();
    }

    /**
     * Clears game state information about food locations.
     */
    public void clearFood() {
        foodTiles.clear();
    }

    /**
     * Clears game state information about my hills locations.
     */
    public void clearMyHills() {
        myHills.clear();
    }

    /**
     * Clears game state information about enemy hills locations.
     */
    public void clearEnemyHills() {
    	enemyHillList.clear();
    }

    /**
     * Clears game state information about dead ants locations.
     */
//    public void clearDeadAnts() {
//        //currently we do not have list of dead ants, so iterate over all map
//        for (int row = 0; row < rows; row++) {
//            for (int col = 0; col < cols; col++) {
//                if (grid[row][col] == Ilk.DEAD) {
//                    map[row][col] = Ilk.LAND;
//                }
//            }
//        }
//    }

    /**
     * Clears visible information
     */
    public void clearVisionAndTargets() {
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                grid[row][col].setVisible(false);
                grid[row][col].setTargeted(false); // No longer targeted
                visibleSquares.clear();
            }
        }
    }


    public void updateTilePValue(int row, int col, double pValue) {
        grid[row][col].setPValue(pValue); 
    }
    
    public void updateTileVisited(int row, int col) {
        grid[row][col].setLastVisited(0);
    }
    
    public void updateTileType(Tile tile, int type) {
        grid[tile.getRow()][tile.getCol()].setType(type); 
        }
    
    public void updateTileType(int row, int col, int type) {
        grid[row][col].setType(type); 
        
        if (type == constants.WATER) {
        	grid[row][col].setPassable(false);
        }
        
        }
    
    public void updateTileType(Ant ant, int type) {
        grid[ant.getRow()][ant.getCol()].setType(type); 
        }
    

    /**
     * Updates game state information about hills locations.
     *
     * @param owner owner of hill
     * @param tile location on the game map to be updated
     */
    public void updateHills(int owner, Tile tile) {
        if (owner == constants.ENEMY_HILL)
            enemyHills.add(tile);
        else
            myHills.add(tile);
    }

    /**
     * Issues an order by sending it to the system output.
     * 
     * @param myAnt map tile with my ant
     * @param direction direction in which to move my ant
     */
    public void issueOrder(Ant myAnt, Aim direction) {
        Order order = new Order(myAnt, direction);
        orders.add(order);
        System.out.println(order);
    }
    
    public double calculateNewPValue(int row, int col, Tile[][] typeGrid) {
    	int numPassable = 0;
    	double totalPValue = 0;
    	double currentPValue = 0;
    	double heuristic = 0;
    	Pair pair = new Pair(row, col);
    	
    	if (typeGrid[row][col].getPValue() == 0) { // Ant, Stop Scent from spreading
    		return 0;
    		//return typeGrid[row][col].getPValue();
    	}
    	
    	if (typeGrid[row][col].isPassable()) { // Not water
    		numPassable++;
    		totalPValue += typeGrid[row][col].getPValue();  // x = (x + a + b + c + d) / numPassable
    	//	totalPValue += typeGrid[row][col].getLastVisited();	
    		//currentPValue = typeGrid[row][col].getPValue(); 
    		//numPassable = 0;
//    			
    	}
    	
    	
    	else {
    	 	 //System.out.println("Num Passable: " + numPassable + " Row: " + row + " Col: " + col);
    		return 0; // water
    	}
    	
    	// currentPValue = typeGrid[row][col].getPValue(); // x = x + (a + b + c + d) / numPassable
    	
    	if (row !=0) {
    		if (typeGrid[row-1][col].isPassable()) {
    			numPassable++;
    			totalPValue += typeGrid[row-1][col].getPValue();
    		}
    	}
    	
    	else {
    		if (typeGrid[rows-1][col].isPassable()) { // rows is the max rows
    			numPassable++;
    			totalPValue += typeGrid[rows-1][col].getPValue();

    		}
    	}
    	
    	if (row != rows - 1) {
    		if (typeGrid[row+1][col].isPassable()) {
    			numPassable++;
    			totalPValue += typeGrid[row+1][col].getPValue();
    		}
    	}
    	
    	else {
    		if (typeGrid[0][col].isPassable()) { // rows is the max rows
    			numPassable++;
    			totalPValue += typeGrid[0][col].getPValue();
    		}
    	}
    	
    	if (col !=0) {
    		if (typeGrid[row][col-1].isPassable()) {
    			numPassable++;
    			totalPValue += typeGrid[row][col-1].getPValue();
    		}
    	}
    	
    	else {
    		if (typeGrid[row][cols-1].isPassable()) { // cols is the max rows
    			numPassable++;
    			totalPValue += typeGrid[row][cols-1].getPValue();
    		}
    	}
    		
    	
    	if (col != cols-1) {
    		if (typeGrid[row][col+1].isPassable()) {
    			numPassable++;
    			totalPValue += typeGrid[row][col+1].getPValue();
    		}
    	}
    	
    	else {
    		if (typeGrid[row][0].isPassable()) { // rows is the max rows
    			numPassable++;
    			totalPValue += typeGrid[row][0].getPValue(); 
    		}
    	}
    	
    	// return currentPValue + (totalPValue / numPassable);	
    	 // System.out.println("Num Passable: " + numPassable + " Row: " + row + " Col: " + col);
    	 
    	//if (!ourHillList.isEmpty()) 
			//heuristic = getDistance(pair, ourHillList.get(0)) * 0.1;
    	
    	return (totalPValue / numPassable); // + currentPValue;	
    }
    
    public Aim getLargestNeighbor(Pair square) { // NEED TO ACCOUNT FOR OUT OF BOUNDS
    	int row = square.getRow();
    	int col = square.getCol();
    	double largestValue = 0;
    	Aim direction = Aim.NORTH; // Default value so it can't return null

    	if	(row != 0) {
    		if (grid[row-1][col].isPassable()) {
    			if (largestValue < grid[row-1][col].getPValue()) {
    				largestValue = grid[row-1][col].getPValue();
    				direction = Aim.NORTH;
    			}
    		}
    	}
    	
    	else {
    		if (grid[rows-1][col].isPassable()) {
    			if (largestValue < grid[rows-1][col].getPValue()) {
    				largestValue = grid[rows-1][col].getPValue();
    				direction = Aim.NORTH;
    			}
    		}
    	}
    	
    	if	(col != 0) {
    		if (grid[row][col-1].isPassable()) {
    			if (largestValue < grid[row][col-1].getPValue()) {
    				largestValue = grid[row][col-1].getPValue();
    				direction = Aim.WEST;
    			}
    		}
    	}
    	
    	else {
    		if (grid[row][cols-1].isPassable()) {
    			if (largestValue < grid[row][cols-1].getPValue()) {
    				largestValue = grid[row][cols-1].getPValue();
    				direction = Aim.WEST;
    			}
    		}
    	}
    	
      	if	(row != rows-1) {
    		if (grid[row+1][col].isPassable()) {
    			if (largestValue < grid[row+1][col].getPValue()) {
    				largestValue = grid[row+1][col].getPValue();
    				direction = Aim.SOUTH;
    			}
    		}
    	}
    	
    	else {
    		if (grid[0][col].isPassable()) {
    			if (largestValue < grid[0][col].getPValue()) {
    				largestValue = grid[0][col].getPValue();
    				direction = Aim.SOUTH;
    			}
    		}
    	}
      	
      	if	(col != cols -1) {
    		if (grid[row][col+1].isPassable()) {
    			if (largestValue < grid[row][col+1].getPValue()) {
    				largestValue = grid[row][col+1].getPValue();
    				direction = Aim.EAST;
    			}
    		}
    	}
    	
    	else {
    		if (grid[row][0].isPassable()) {
    			if (largestValue < grid[row][0].getPValue()) {
    				largestValue = grid[row][0].getPValue();
    				direction = Aim.EAST;
    			}
    		}
    	}
      	
      	
    
    	//System.out.println("FINAL " + largestValue);
    	
    	return direction;
    }
    public void diffusion() {
    	
    	 for (int i = 0; i < rows; i++) {  // For every tile in every row and column fill it with initial land.
         	for (int j = 0; j < cols; j++) {
         		gridImage[i][j].setPValue(calculateNewPValue(i, j, grid));
         		if (gridImage[i][j].isVisible() == false)
         			gridImage[i][j].increaseLastVisited();
         	}
    	 }
    	 
    	 for (int i = 0; i < rows; i++) {  // For every tile in every row and column fill it with initial land.
          	for (int j = 0; j < cols; j++) {
          		grid[i][j].setPValue(calculateNewPValue(i, j, gridImage));
          	}
     	 }
    	 
    	 
    	 for (int i = 0; i < rows; i++) {  // For every tile in every row and column fill it with initial land.
          	for (int j = 0; j < cols; j++) {
          		gridImage[i][j].setPValue(calculateNewPValue(i, j, grid));
          		if (gridImage[i][j].isVisible() == false)
          			gridImage[i][j].increaseLastVisited();
          	}
     	 }
     	 
     	 for (int i = 0; i < rows; i++) {  // For every tile in every row and column fill it with initial land.
           	for (int j = 0; j < cols; j++) {
           		grid[i][j].setPValue(calculateNewPValue(i, j, gridImage));
           	}
      	 }
    	 
    	 
//    	// P(square) = (P(Neighbors) / # passable neighbors)
//    	for (Pair square: visibleSquares) {
//    		gridImage[square.getRow()][square.getCol()].setPValue(calculateNewPValue(square.getRow(), square.getCol(), grid));	
//    		//System.out.println(gridImage[square.getRow()][square.getCol()].getPValue());
//    	}
//    	
//    	for (Pair square: visibleSquares) {
//    		grid[square.getRow()][square.getCol()].setPValue(calculateNewPValue(square.getRow(), square.getCol(), gridImage));	
//    	}
    	

    	
    }

	public ArrayList<Pair> getFoodList() {
		return foodList;
	}

	public ArrayList<Pair> getEnemyHillList() {
		return enemyHillList;
	}

	public void setEnemyHillList(ArrayList<Pair> enemyHillList) {
		this.enemyHillList = enemyHillList;
	}

	public ArrayList<Ant> getEnemyAntList() {
		return enemyAntList;
	}

	public void setEnemyAntList(ArrayList<Ant> enemyAntList) {
		this.enemyAntList = enemyAntList;
	}
	
	public void clearEnemyAntList() {
		enemyAntList.clear();
	}

	public ArrayList<Pair> getOurHillList() {
		return ourHillList;
	}

	public void setOurHillList(ArrayList<Pair> ourHillList) {
		this.ourHillList = ourHillList;
	}
	
	
}
