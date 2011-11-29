import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.*;
/**
 * Provides basic game state handling.
 */
public abstract class Bot extends AbstractSystemInputParser {
    private Ants ant = Ants.getInstance();
    private int gameTurnNum = 0;
    private Logger logger;
    private BufferedWriter out;
    private ArrayList<Ant> attackValueReset = new ArrayList<Ant>();
    /**
     * {@inheritDoc}
     */
    @Override
    public void setup(int loadTime, int turnTime, int rows, int cols, int turns, int viewRadius2,
            int attackRadius2, int spawnRadius2) {
        if (gameTurnNum == 0) {
        	ant.setupAnts(loadTime, turnTime, rows, cols, turns, viewRadius2, attackRadius2, spawnRadius2);
            
        try {
        	 LogManager lm = LogManager.getLogManager();
            // Create a file handler that write log record to a file called my.log
            //FileHandler handler = new FileHandler("log.txt");
            FileWriter fstream = new FileWriter("out.txt");
            out = new BufferedWriter(fstream);
            // Add to the desired logger
           //logger = Logger.getLogger("Bot");
       	 	//lm.addLogger(logger);
           // logger.addHandler(handler);
          //  logger.fine("test");
          //  logger.log(Level.INFO, "test 1");
        } 
        catch (IOException e) {
        }
      } // end if
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
        
        for (Ant enemyAnt: attackValueReset) { // Reset those high values set to attack enemy ants
        	ant.updateTilePValue(enemyAnt.getRow(), enemyAnt.getCol(), constants.NORMAL_VALUE);
        }
        attackValueReset.clear();
        
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
        ant.getTile(row, col).setPassable(false); // set grid tiles to unpassable
        ant.getTileImage(row, col).setPassable(false); // set grid image tiles to unpassable
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
    			ant.updateTilePValue(row, col, constants.FRIENDLY_ANT_VALUE);  // Will set the value of the tile to 0 wherever we have an Ant
    			ant.updateTileVisited(row, col); // Set lastVisited to 0 on the tile since ant is on tile
    			ant.updateTileType(antObj, constants.MY_ANT); // Set tile type to ant tile 
    			ant.updateFriendly(antObj); // update tiles that this ant can potentially move there
    		}
    		else {
    			Ant enemyObj = new Ant(row, col);
				ant.getEnemyAntList().add(enemyObj); // add enemy to enemyList
				ant.updateTilePValue(row, col, constants.ENEMY_ANT_VALUE); // give negative value to enemy ant tile
				//ant.updateEnemyInRadius(enemyObj); // update tiles around to indicate this ant can attack these tiles
    		}
    	}
    public void checkEnemiesFirst() {
    	for (Ant enemyObj: ant.getEnemyAntList()) {
    		for (Ant antObj: ant.myAntList()) {
    			if (ant.getDistance(enemyObj, antObj) < ant.getAttackRadius2() + 4) { // one of our ants is near an enemy ant
    			//	ant.updateTilePValue(antObj.getRow(), antObj.getCol(), constants.ENEMY_ANT_DANGER);
    				ant.updateTilePValue(enemyObj.getRow(), enemyObj.getCol(), constants.ENEMY_ANT_VALUE);
    			}
    		}
    	}
    }
    public void checkEnemies() {
    	for (Ant enemyObj: ant.getEnemyAntList()) {
//    		for (Ant antObj: ant.myAntList()) {
//    			if (ant.getDistance(enemyObj, antObj) < ant.getAttackRadius2() + 3) { // one of our ants is near an enemy ant
//    			//	ant.updateTilePValue(antObj.getRow(), antObj.getCol(), constants.ENEMY_ANT_DANGER);
//    				ant.updateTilePValue(enemyObj.getRow(), enemyObj.getCol(), constants.ENEMY_ANT_VALUE);
//    			}
//    		}
    			for (Pair ourHill: ant.getOurHillList()) {
    				if (ant.getDistance(enemyObj, ourHill) < 20) // Detect enemy ants, defend hill
    					ant.updateTilePValue(ourHill.getRow(), ourHill.getCol(), constants.ENEMY_ANT_DANGER);
					}
    			
    			if (ant.updateEnemyInRadius(enemyObj) > 2) {
    			//	ant.updateTilePValue(enemyObj.getRow(), enemyObj.getCol(), constants.ENEMY_RADIUS_VALUE); // attack this ant
    				ant.updateRadiusValues(enemyObj);
    				attackValueReset.add(enemyObj);
    			}
    			
    		}
    	}
    //} // end checkEnemies
    
    
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
    		 // ant.updateTilePValue(antObj.getRow(), antObj.getCol(), constants.NORMAL_VALUE);
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
    
    public void printDiffValues() {
//    	try {
//    	 out.write("Turn Number: " + gameTurnNum);
//    	 out.newLine();
//    	}
// 		catch (Exception e) {}
    	
    	 for (int i = 0; i < ant.getRows(); i++) {  // For every tile in every row and column fill it with initial land.
         	for (int j = 0; j < ant.getCols(); j++) {
         		if (ant.getGrid()[i][j].isVisible()) {
         		try {
         		out.write("" + ant.getGrid()[i][j]);
         		out.newLine();
         		}
         		catch (Exception e) {}
         		}
         	}
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
    	ant.setVision(); // set what ants can see
    //	checkEnemiesFirst();
//    	if (gameTurnNum < 31) {
//        	try {
//        		out.write("Turn Number: " + gameTurnNum);
//        		out.newLine();
//        	}
//    		catch (Exception e) {}
//        	
//    		printDiffValues();
//    	}
    	
    	 checkEnemies(); // update tiles due to enemies including ones near our hill
    
    	
    	for (Pair foodLoc: ant.getFoodList()) {
    		if (ant.getOldFoodList().contains(foodLoc)) {
    			ant.getOldFoodList().remove(foodLoc);
    			//System.out.println(foodLoc);
    		}
    	}
    	
    	for (Pair foodLoc: ant.getOldFoodList())  // Will set food that is no longer visible, maybe eaten, back to a p value of 100
    		ant.updateTilePValue(foodLoc.getRow(), foodLoc.getCol(), constants.AFTER_FOOD_VALUE);
    			
    	ant.getOldFoodList().clear();		
      //  ant.setVision();
        ant.diffusion();
        ant.diffusion();
        //checkEnemies(); // update tiles due to enemies including ones near our hill
    }
}
