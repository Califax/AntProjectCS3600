import java.util.ArrayList;

/**
 * Provides basic game state handling.
 */
public abstract class Bot extends AbstractSystemInputParser {
    private Ants ant = Ants.getInstance();
    private int gameTurnNum = 0;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setup(int loadTime, int turnTime, int rows, int cols, int turns, int viewRadius2,
            int attackRadius2, int spawnRadius2) {
        if (gameTurnNum == 0)
        		ant.setupAnts(loadTime, turnTime, rows, cols, turns, viewRadius2, attackRadius2, spawnRadius2);
    }
    
    /**
     * Returns game state information.
     * 
     * @return game state information
     */
    public Ants getAnts() {
        return Ants.getInstance();
    }
    
    /**
     * Sets game state information.
     * 
     * @param ants game state information to be set
     */
//    protected void setAnts(Ants ants) {
//        this.ants = ants.getAnts();
//    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeUpdate() {
    	
    	
    	gameTurnNum++;
    	if (gameTurnNum == 1)
    		ant.intitializeTiles(); // Set up tile objects in every location on map
       // ant.diffusion(); // test
        ant.setTurnStartTime(System.currentTimeMillis());
        ant.clearMyAnts();
        ant.clearEnemyAntList();
        //ants.clearEnemyAnts();
        ant.clearMyHills();
        ant.clearEnemyHills();
        
        
        removeFood(); // Copy food list into oldFoodList
        ant.clearFood();
     //   ants.clearDeadAnts();
        ant.getOrders().clear();
        ant.clearVisionAndTargets();
      //  ant.updateMyAnts();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addWater(int row, int col) {
        ant.updateTileType(row, col, constants.WATER);
        ant.updateTilePValue(row, col, 0);
        ant.getTile(row, col).setPassable(false);
    }
    
    /**
     * Create new Ant Object
     */
    @Override
    public void addAnt(int row, int col, int owner) {
    		if (owner == constants.PLAYER_ID) {
    			Ant antObj = new Ant(row, col);
    			ant.myAntList().add(antObj);
    			//ants.getGrid()[row][col].setType(constants.MY_ANT);
    			ant.updateTilePValue(row, col, 0); 
    			ant.updateTileVisited(row, col);
    			ant.updateTileType(antObj, constants.MY_ANT);
    		}
    		else {
    			Ant enemyObj = new Ant(row, col);
				ant.getEnemyAntList().add(enemyObj);
				for (Ant antObj: ant.myAntList()) {
					if (ant.getDistance(enemyObj, antObj) < ant.getAttackRadius2() + 2) {
						ant.updateTilePValue(antObj.getRow(), antObj.getCol(), constants.ENEMY_ANT_DANGER);
					}
				}
				
					for (Pair ourHill: ant.getOurHillList()) {
						if (ant.getDistance(enemyObj, ourHill) < 20) // Detect enemy ants, defend hill
							ant.updateTilePValue(ourHill.getRow(), ourHill.getCol(), constants.ENEMY_ANT_DANGER); 
					}
				  
			}
    	}
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addFood(int row, int col) {
        ant.updateTileType(row, col, constants.FOOD);
        ant.updateTilePValue(row, col, constants.FOOD_VALUE);        
        ant.getFoodList().add(new Pair(row, col));
    }
    
    public void removeFood() {
    	for (Pair foodLoc: ant.getFoodList())
    		ant.getOldFoodList().add(foodLoc);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAnt(int row, int col, int owner) {
       for (Ant antObj: ant.myAntList() ) {
    	   if (antObj.getRow() == row && antObj.getCol() == col)
              ant.myAntList().remove(ant);
    		  ant.updateTilePValue(antObj.getRow(), antObj.getCol(), constants.NORMAL_VALUE);
       }
    }
    
    public void removeAnt(Ants ant) {
        ant.myAntList().remove(ant);
     }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addHill(int row, int col, int owner) {
    	Pair checkHill = new Pair(row, col);
    	//System.out.println(owner);
    	if (owner > 0) {	
    		if (!ant.getEnemyHillList().contains(checkHill)) {
    			ant.getEnemyHillList().add(new Pair(row, col));
       // ant.updateHills(owner, new Tile(row, col));
    			
    			ant.updateTilePValue(row, col, constants.ENEMY_HILL_VALUE);
    		}
    	}
    	else {
    		if (!ant.getOurHillList().contains(checkHill)) 
    			ant.getOurHillList().add(new Pair(row, col));
    			ant.updateTilePValue(row, col, constants.OUR_HILL_VALUE);
    	}
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void afterUpdate() {
    	//for (Pair ourHill: ant.getOurHillList()) {
    	//	ant.updateTilePValue(ourHill.getRow(), ourHill.getCol(), constants.OUR_HILL_VALUE);
    	//}
    	
    
    	
    	for (Pair foodLoc: ant.getFoodList()) {
    		if (ant.getOldFoodList().contains(foodLoc)) {
    			ant.getOldFoodList().remove(foodLoc);
    			//System.out.println(foodLoc);
    		}
    	}
    	
    	for (Pair foodLoc: ant.getOldFoodList())  // Will set food that is no longer visible, maybe eaten, back to a p value of 100
    		ant.updateTilePValue(foodLoc.getRow(), foodLoc.getCol(), constants.NORMAL_VALUE);
    			
    	ant.getOldFoodList().clear();		
        ant.setVision();
        ant.diffusion();

    }
}
