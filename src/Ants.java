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
                gridImage[newLoc.getRow()][newLoc.getCol()].setVisible(true); // Tile is now visible
                gridImage[antObj.getRow()][antObj.getCol()].setVisible(true);
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
    
    public Tile getTileImage(int row, int col) {
    	return gridImage[row][col];
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
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grid[row][col].setVisible(false);
                grid[row][col].setTargeted(false); // No longer targeted
                gridImage[row][col].setVisible(false);
                gridImage[row][col].setTargeted(false); // No longer targeted
                grid[row][col].setEnemyInRadius(0); // start with 0 will refill array
                grid[row][col].setNumFriendlyPotential(0);
                gridImage[row][col].setEnemyInRadius(0); // start with 0 will refill array
                gridImage[row][col].setNumFriendlyPotential(0);
            }
        }
        visibleSquares.clear();
    }


    public void updateTilePValue(int row, int col, double pValue) {
        grid[row][col].setPValue(pValue); 
        gridImage[row][col].setPValue(pValue);
    }
    
    public void updateTileVisited(int row, int col) {
        grid[row][col].setLastVisited(0);
        gridImage[row][col].setLastVisited(0);
    }
    
    public void updateTileType(Tile tile, int type) {
        grid[tile.getRow()][tile.getCol()].setType(type); 
        gridImage[tile.getRow()][tile.getCol()].setType(type); 
        }
    
    public void updateTileType(int row, int col, int type) {
        grid[row][col].setType(type); 
        gridImage[row][col].setType(type); 
        
        if (type == constants.WATER) {
        	grid[row][col].setPassable(false);
        	gridImage[row][col].setPassable(false);
        }
        
        }
    
    public void updateTileType(Ant ant, int type) {
        grid[ant.getRow()][ant.getCol()].setType(type); 
        gridImage[ant.getRow()][ant.getCol()].setType(type);       
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

    	
//    	if (typeGrid[row][col].getPValue() == 0) { // Ant, Stop Scent from spreading
//    		return 0;
//    		//return typeGrid[row][col].getPValue();
//    	}
    	
    	if (typeGrid[row][col].isPassable()) { // Not water
    		numPassable++;
    		totalPValue += typeGrid[row][col].getPValue();  // x = (x + a + b + c + d) / numPassable
    		totalPValue += typeGrid[row][col].getLastVisited();	
    		//currentPValue = typeGrid[row][col].getPValue();  			
    	}
    	
    	
    	else {
    	 	 //System.out.println("Num Passable: " + numPassable + " Row: " + row + " Col: " + col);
    		typeGrid[row][col].setPValue(0); // Keep water tiles at 0
    		//System.out.println(typeGrid[row][col]);
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
    
    public Aim getLargestNeighbor(Pair square) { 
    	int row = square.getRow();
    	int col = square.getCol();
    	double largestValue = -25;
    	Aim direction = Aim.NORTH; // Default value so it can't return null

//		if (largestValue < grid[row][col].getPValue()) { // check if current square is best square
//				largestValue = grid[row][col].getPValue();
//				direction = Aim.NORTH;
//		}
			
    	if	(row != 0) {
    		if (grid[row-1][col].isPassable() && !grid[row-1][col].isTargeted()) { // check if water and if it has been targeted
    			if (largestValue < grid[row-1][col].getPValue()) {
    				largestValue = grid[row-1][col].getPValue();
    				direction = Aim.NORTH;
    			}
    		}
    	}
    	
    	else {
    		if (grid[rows-1][col].isPassable() && !grid[rows-1][col].isTargeted()) {
    			if (largestValue < grid[rows-1][col].getPValue()) {
    				largestValue = grid[rows-1][col].getPValue();
    				direction = Aim.NORTH;
    			}
    		}
    	}
    	
    	if	(col != 0) {
    		if (grid[row][col-1].isPassable() && !grid[row][col-1].isTargeted()) {
    			if (largestValue < grid[row][col-1].getPValue()) {
    				largestValue = grid[row][col-1].getPValue();
    				direction = Aim.WEST;
    			}
    		}
    	}
    	
    	else {
    		if (grid[row][cols-1].isPassable()  && !grid[row][cols-1].isTargeted()) {
    			if (largestValue < grid[row][cols-1].getPValue()) {
    				largestValue = grid[row][cols-1].getPValue();
    				direction = Aim.WEST;
    			}
    		}
    	}
    	
      	if	(row != rows-1) {
    		if (grid[row+1][col].isPassable()  && !grid[row+1][col].isTargeted()) {
    			if (largestValue < grid[row+1][col].getPValue()) {
    				largestValue = grid[row+1][col].getPValue();
    				direction = Aim.SOUTH;
    			}
    		}
    	}
    	
    	else {
    		if (grid[0][col].isPassable()  && !grid[0][col].isTargeted()) {
    			if (largestValue < grid[0][col].getPValue()) {
    				largestValue = grid[0][col].getPValue();
    				direction = Aim.SOUTH;
    			}
    		}
    	}
      	
      	if	(col != cols-1) {
    		if (grid[row][col+1].isPassable()  && !grid[row][col+1].isTargeted()) {
    			if (largestValue < grid[row][col+1].getPValue()) {
    				largestValue = grid[row][col+1].getPValue();
    				direction = Aim.EAST;
    			}
    		}
    	}
    	
    	else {
    		if (grid[row][0].isPassable()  && !grid[row][0].isTargeted()) {
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
    	
    	 for (int i = 0; i < rows; i++) {  // For every tile in every row and column calculate new diffusion p values
         	for (int j = 0; j < cols; j++) {
         		gridImage[i][j].setPValue(calculateNewPValue(i, j, grid));
         		if (gridImage[i][j].isVisible() == false) {
         			grid[i][j].increaseLastVisited();
         			gridImage[i][j].increaseLastVisited();
         		}
         		else {
         			gridImage[i][j].decreaseLastVisited();
         			grid[i][j].decreaseLastVisited();
         		}
         	}
    	 }
    	 
    	 for (int i = 0; i < rows; i++) {  
          	for (int j = 0; j < cols; j++) {
          		grid[i][j].setPValue(calculateNewPValue(i, j, gridImage));
          		if (gridImage[i][j].isVisible() == false) {
         			grid[i][j].increaseLastVisited();
         			gridImage[i][j].increaseLastVisited();
         		}
         		else {
         			gridImage[i][j].decreaseLastVisited();
         			grid[i][j].decreaseLastVisited();
         		};
          	}
     	 }
    	 
    	 
    	 for (int i = 0; i < rows; i++) { 
          	for (int j = 0; j < cols; j++) {
          		gridImage[i][j].setPValue(calculateNewPValue(i, j, grid));
         		if (gridImage[i][j].isVisible() == false) {
         			grid[i][j].increaseLastVisited();
         			gridImage[i][j].increaseLastVisited();
         		}
         		else {
         			gridImage[i][j].decreaseLastVisited();
         			grid[i][j].decreaseLastVisited();
         		}
          	}
     	 }
     	 
     	 for (int i = 0; i < rows; i++) {  
           	for (int j = 0; j < cols; j++) {
           		grid[i][j].setPValue(calculateNewPValue(i, j, gridImage));
           		if (gridImage[i][j].isVisible() == false) {
         			grid[i][j].increaseLastVisited();
         			gridImage[i][j].increaseLastVisited();
         		}
         		else {
         			gridImage[i][j].decreaseLastVisited();
         			grid[i][j].decreaseLastVisited();
         		}
           	}
      	 }
     	 
     	for (int i = 0; i < rows; i++) { 
          	for (int j = 0; j < cols; j++) {
          		gridImage[i][j].setPValue(calculateNewPValue(i, j, grid));
         		if (gridImage[i][j].isVisible() == false) {
         			grid[i][j].increaseLastVisited();
         			gridImage[i][j].increaseLastVisited();
         		}
         		else {
         			gridImage[i][j].decreaseLastVisited();
         			grid[i][j].decreaseLastVisited();
         		}
          	}
     	 }
     	 
     	 for (int i = 0; i < rows; i++) {  
           	for (int j = 0; j < cols; j++) {
           		grid[i][j].setPValue(calculateNewPValue(i, j, gridImage));
           		if (gridImage[i][j].isVisible() == false) {
         			grid[i][j].increaseLastVisited();
         			gridImage[i][j].increaseLastVisited();
         		}
         		else {
         			gridImage[i][j].decreaseLastVisited();
         			grid[i][j].decreaseLastVisited();
         		}
           	}
      	 }

    	
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
	
	public int updateEnemyInRadius(Ant enemyAnt) { // updates tiles by one that are in radius of enemy and returns
		int row = enemyAnt.getRow(); // how many friendly ants can move into that radius next turn
		int col = enemyAnt.getCol();
		int numFriendly = 0;
		
		int rowPlusOne = row+1;
		if (rowPlusOne == rows)
			rowPlusOne = 0;
		int colPlusOne = col+1;
		if (colPlusOne == cols)
			colPlusOne = 0;
		int rowMinusOne = row-1;
		if (rowMinusOne == -1)
			rowMinusOne = rows-1;
		int colMinusOne = col-1;
		if (colMinusOne == -1)
			colMinusOne = cols-1;
		int rowMinusTwo = row-2;
		if (rowMinusTwo == -1)
			rowMinusTwo = rows-1;
		if (rowMinusTwo == -2)
			rowMinusTwo = rows-2;
		int colMinusTwo = col-2;
		if (colMinusTwo == -1)
			colMinusTwo = cols-1;
		if (colMinusTwo == -2)
			colMinusTwo = cols-2;
		int rowPlusTwo = row+2;
		if (rowPlusTwo == rows)
			rowPlusTwo = 0;
		if (rowPlusTwo == rows+1)
			rowPlusTwo = 1;
		int colPlusTwo = col+2;
		if (colPlusTwo == cols)
			colPlusTwo = 0;
		if (colPlusTwo == cols+1)
			colPlusTwo = 1;
		
		
		grid[row][col].increaseEnemyInRadius(); // can target own square
		
		if (row != rows-1) {	// 1 row up
			grid[row+1][col].increaseEnemyInRadius();
			numFriendly += grid[row+1][col].getNumFriendlyPotential();
		}
		else {
			grid[0][col].increaseEnemyInRadius();
			numFriendly += grid[0][col].getNumFriendlyPotential();
		}
		
		if (row != rows-1 && row != rows-2)	{ // 2 row up
			grid[row+2][col].increaseEnemyInRadius();
			numFriendly += grid[row+2][col].getNumFriendlyPotential();
		}
		else {
			grid[1][col].increaseEnemyInRadius();
			numFriendly += grid[1][col].getNumFriendlyPotential();
		}
				
		if (row != 0) {
			grid[row-1][col].increaseEnemyInRadius(); // 1 row down
			numFriendly += grid[row-1][col].getNumFriendlyPotential();
		}
		else {
			grid[rows-1][col].increaseEnemyInRadius(); 
			numFriendly += grid[rows-1][col].getNumFriendlyPotential();
		}
		if (row !=0 && row != 1) {					// 2 row down
			grid[row-2][col].increaseEnemyInRadius();
			numFriendly += grid[row-2][col].getNumFriendlyPotential();
		}
		else {
			grid[rows-2][col].increaseEnemyInRadius();
			numFriendly += grid[rows-2][col].getNumFriendlyPotential();
		}
		
		
		if (col != cols-1) {	// 1 col up
			grid[row][col+1].increaseEnemyInRadius();
			numFriendly += grid[row][col+1].getNumFriendlyPotential();
		}
		else {
			grid[row][0].increaseEnemyInRadius();
			numFriendly += grid[row][0].getNumFriendlyPotential();
		}
		
		if (col != cols-1 && col != cols-2)	{ // 2 col up
			grid[row][col+2].increaseEnemyInRadius();
			numFriendly += grid[row][col+2].getNumFriendlyPotential();
		}
		else {
			grid[row][1].increaseEnemyInRadius();
			numFriendly += grid[row][1].getNumFriendlyPotential();
		}
				
		if (col != 0) {
			grid[row][col-1].increaseEnemyInRadius(); // 1 col down
			numFriendly += grid[row][col-1].getNumFriendlyPotential();
		}
		else {
			grid[row][cols-1].increaseEnemyInRadius(); 
			numFriendly += grid[row][cols-1].getNumFriendlyPotential();
		}
		if (col !=0 && col != 1) {					// 2 col down
			grid[row][col-2].increaseEnemyInRadius();
			numFriendly += grid[row][col-2].getNumFriendlyPotential();
		}
		else {
			grid[row][cols-2].increaseEnemyInRadius();
			numFriendly += grid[row][cols-2].getNumFriendlyPotential();
		}
		
		// 1 row down 1 col down (top left is (0,0))
		grid[rowPlusOne][colPlusOne].increaseEnemyInRadius();
		numFriendly += grid[rowPlusOne][colPlusOne].getNumFriendlyPotential();
		
		// 1 row up 1 col up (top left is (0,0))
		grid[rowMinusOne][colMinusOne].increaseEnemyInRadius();
		numFriendly += grid[rowMinusOne][colMinusOne].getNumFriendlyPotential();
		
		// 1 row up 1 col down (top left is (0,0))
		grid[rowPlusOne][colMinusOne].increaseEnemyInRadius();
		numFriendly += grid[rowPlusOne][colMinusOne].getNumFriendlyPotential();
		
		// 1 row down 1 col left (top left is (0,0))
		grid[rowMinusOne][colPlusOne].increaseEnemyInRadius();
		numFriendly += grid[rowMinusOne][colPlusOne].getNumFriendlyPotential();
		
		// 1 row down 2 col left (top left is (0,0))
		grid[rowMinusOne][colMinusTwo].increaseEnemyInRadius();
		numFriendly += grid[rowMinusOne][colMinusTwo].getNumFriendlyPotential();
		
		// 1 row up 2 col left (top left is (0,0))
		grid[rowPlusOne][colMinusTwo].increaseEnemyInRadius();
		numFriendly += grid[rowPlusOne][colMinusTwo].getNumFriendlyPotential();
		
		// 1 row down 2 col right (top left is (0,0))
		grid[rowPlusOne][colPlusTwo].increaseEnemyInRadius();
		numFriendly += grid[rowPlusOne][colPlusTwo].getNumFriendlyPotential();
		
		// 1 row up 2 col right (top left is (0,0))
		grid[rowMinusOne][colPlusTwo].increaseEnemyInRadius();
		numFriendly += grid[rowMinusOne][colPlusTwo].getNumFriendlyPotential();
		
		// 2 rows up 1 col right (top left is (0,0))
		grid[rowMinusTwo][colPlusOne].increaseEnemyInRadius();
		numFriendly += grid[rowMinusTwo][colPlusOne].getNumFriendlyPotential();
				
		// 2 rows down 1 col right (top left is (0,0))
		grid[rowPlusTwo][colPlusOne].increaseEnemyInRadius();
		numFriendly += grid[rowPlusTwo][colPlusOne].getNumFriendlyPotential();
		
		// 2 rows up 1 col left (top left is (0,0))
		grid[rowMinusTwo][colMinusOne].increaseEnemyInRadius();
		numFriendly += grid[rowMinusTwo][colMinusOne].getNumFriendlyPotential();
						
		// 2 rows down 1 col left (top left is (0,0))
		grid[rowPlusTwo][colMinusOne].increaseEnemyInRadius();
		numFriendly += grid[rowPlusTwo][colMinusOne].getNumFriendlyPotential();
		
		return numFriendly;
	}
	
	public void updateRadiusValues(Ant enemyAnt) {
		
		int row = enemyAnt.getRow(); // how many friendly ants can move into that radius next turn
		int col = enemyAnt.getCol();
		
		int rowPlusOne = row+1;
		if (rowPlusOne == rows)
			rowPlusOne = 0;
		int colPlusOne = col+1;
		if (colPlusOne == cols)
			colPlusOne = 0;
		int rowMinusOne = row-1;
		if (rowMinusOne == -1)
			rowMinusOne = rows-1;
		int colMinusOne = col-1;
		if (colMinusOne == -1)
			colMinusOne = cols-1;
		int rowMinusTwo = row-2;
		if (rowMinusTwo == -1)
			rowMinusTwo = rows-1;
		if (rowMinusTwo == -2)
			rowMinusTwo = rows-2;
		int colMinusTwo = col-2;
		if (colMinusTwo == -1)
			colMinusTwo = cols-1;
		if (colMinusTwo == -2)
			colMinusTwo = cols-2;
		int rowPlusTwo = row+2;
		if (rowPlusTwo == rows)
			rowPlusTwo = 0;
		if (rowPlusTwo == rows+1)
			rowPlusTwo = 1;
		int colPlusTwo = col+2;
		if (colPlusTwo == cols)
			colPlusTwo = 0;
		if (colPlusTwo == cols+1)
			colPlusTwo = 1;
	
		if (grid[rowPlusOne][col].isPassable())
			grid[rowPlusOne][col].setPValue(constants.ENEMY_RADIUS_VALUE);
		
		if (grid[rowPlusTwo][col].isPassable())
			grid[rowPlusTwo][col].setPValue(constants.ENEMY_RADIUS_VALUE);
				
		if (grid[rowMinusTwo][col].isPassable())
			grid[rowMinusTwo][col].setPValue(constants.ENEMY_RADIUS_VALUE);
		
		if (grid[rowMinusOne][col].isPassable())
			grid[rowMinusOne][col].setPValue(constants.ENEMY_RADIUS_VALUE);
		
		if (grid[row][colPlusOne].isPassable())
			grid[row][colPlusOne].setPValue(constants.ENEMY_RADIUS_VALUE);
		
		if (grid[row][colPlusTwo].isPassable())
			grid[row][colPlusTwo].setPValue(constants.ENEMY_RADIUS_VALUE);
				
		if (grid[row][colMinusOne].isPassable())
			grid[row][colMinusOne].setPValue(constants.ENEMY_RADIUS_VALUE);
		
		if (grid[row][colMinusTwo].isPassable())
			grid[row][colMinusTwo].setPValue(constants.ENEMY_RADIUS_VALUE);
		
		
		// 1 row down 1 col down (top left is (0,0))
		if (grid[rowPlusOne][colPlusOne].isPassable())
			grid[rowPlusOne][colPlusOne].setPValue(constants.ENEMY_RADIUS_VALUE);
		
		
		// 1 row up 1 col up (top left is (0,0))
		if (grid[rowMinusOne][colMinusTwo].isPassable())
			grid[rowMinusOne][colMinusTwo].setPValue(constants.ENEMY_RADIUS_VALUE);
		
		// 1 row up 1 col down (top left is (0,0))
		if (grid[rowPlusOne][colMinusOne].isPassable())
			grid[rowPlusOne][colMinusOne].setPValue(constants.ENEMY_RADIUS_VALUE);
		
		
		// 1 row down 1 col left (top left is (0,0))
		if (grid[rowMinusOne][colPlusOne].isPassable())
			grid[rowMinusOne][colPlusOne].setPValue(constants.ENEMY_RADIUS_VALUE);
		
		// 1 row down 2 col left (top left is (0,0))
		if (grid[rowMinusOne][colMinusTwo].isPassable())
			grid[rowMinusOne][colMinusTwo].setPValue(constants.ENEMY_RADIUS_VALUE);
		
		// 1 row up 2 col left (top left is (0,0))
		if (grid[rowPlusOne][colMinusTwo].isPassable())
			grid[rowPlusOne][colMinusTwo].setPValue(constants.ENEMY_RADIUS_VALUE);
		
		// 1 row down 2 col right (top left is (0,0))
		if (grid[rowPlusOne][colPlusTwo].isPassable())
			grid[rowPlusOne][colPlusTwo].setPValue(constants.ENEMY_RADIUS_VALUE);
		
		// 1 row up 2 col right (top left is (0,0))
		if (grid[rowMinusOne][colPlusTwo].isPassable())
			grid[rowMinusOne][colPlusTwo].setPValue(constants.ENEMY_RADIUS_VALUE);
				
		// 2 rows up 1 col right (top left is (0,0))
		if (grid[rowMinusTwo][colPlusOne].isPassable())
			grid[rowMinusTwo][colPlusOne].setPValue(constants.ENEMY_RADIUS_VALUE);
				
		// 2 rows down 1 col right (top left is (0,0))
		
		if (grid[rowPlusTwo][colPlusOne].isPassable())
			grid[rowPlusTwo][colPlusOne].setPValue(constants.ENEMY_RADIUS_VALUE);
		
		// 2 rows up 1 col left (top left is (0,0))
		if (grid[rowMinusTwo][colMinusOne].isPassable())
			grid[rowMinusTwo][colMinusOne].setPValue(constants.ENEMY_RADIUS_VALUE);
						
		// 2 rows down 1 col left (top left is (0,0))
		if (grid[rowPlusTwo][colMinusOne].isPassable())
			grid[rowPlusTwo][colMinusOne].setPValue(constants.ENEMY_RADIUS_VALUE);
		
	}
	public void updateFriendly(Ant antObj) {
		int row = antObj.getRow();
		int col = antObj.getCol();
		
		if (grid[row][col].isPassable()) { // Not water
    		grid[row][col].increaseNumFriendlyPotential();     		
    	}
    	
    	if (row !=0) {
    		if (grid[row-1][col].isPassable()) {
        		grid[row-1][col].increaseNumFriendlyPotential();
    		}
    	}
    	
    	else {
    		if (grid[rows-1][col].isPassable()) { // rows is the max rows
        		grid[rows-1][col].increaseNumFriendlyPotential();
    		}
    	}
    	
    	if (row != rows - 1) {
    		if (grid[row+1][col].isPassable()) {
        		grid[row+1][col].increaseNumFriendlyPotential();
    		}
    	}
    	
    	else {
    		if (grid[0][col].isPassable()) { // rows is the max rows
        		grid[0][col].increaseNumFriendlyPotential();
    		}
    	}
    	
    	if (col !=0) {
    		if (grid[row][col-1].isPassable()) {
        		grid[row][col-1].increaseNumFriendlyPotential();
    		}
    	}
    	
    	else {
    		if (grid[row][cols-1].isPassable()) { // cols is the max rows
        		grid[row][cols-1].increaseNumFriendlyPotential();
    		}
    	}
    		
    	
    	if (col != cols-1) {
    		if (grid[row][col+1].isPassable()) {
        		grid[row][col+1].increaseNumFriendlyPotential();
    		}
    	}
    	
    	else {
    		if (grid[row][0].isPassable()) { // rows is the max rows
        		grid[row][0].increaseNumFriendlyPotential();
    		}
    	}
	}
}
