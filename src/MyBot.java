import java.io.IOException;

/**
 * Starter bot implementation.
 */
public class MyBot extends Bot {
    /**
     * Main method executed by the game engine for starting the bot.
     * 
     * @param args command line arguments
     * 
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        new MyBot().readSystemInput();
    }
    
    
    /**
     * For every ant check every direction in fixed order (N, E, S, W) and move it if the tile is
     * passable.
     */
    @Override
    public void doTurn() {
       // System.out.println(ant.getMyAnts());
    	Ants ant = Ants.getInstance();
    	//System.out.println(ant);
        for (Ant myAnt : ant.getMyAnts()) {
        	//System.out.println(ant);
            // for (Aim direction : Aim.values()) {
            	    Aim direction = ant.getLargestNeighbor(myAnt.getCord());
            		Tile newTile = ant.getTile(myAnt, direction);
              if (newTile.isPassable() && newTile.isTargeted() == false && !newTile.isOccupied()) {
            	   	
                    ant.issueOrder(myAnt, direction);
                    ant.updateTilePValue(myAnt.getRow(), myAnt.getCol(), constants.NORMAL_VALUE / 10);
                  //  System.out.println(newTile);
                    ant.updateTileType(myAnt, constants.LAND); // Set tile where ant was previously back to land
                    //System.out.println(ants.getTile(myAnt));
                    myAnt.setCord(newTile.getRow(), newTile.getCol());  // Update Ant object with new location
                    newTile.setTargeted(true); // location is going to be moved to next turn by a friendly ant
                  //  ant.updateTileType(newTile, constants.MY_ANT);
                    //System.out.println(newTile);
                   // System.out.println(ants.myAntList());
                    }
                }
           }
        
}
