import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    	// Pair ourHill = null;
       // System.out.println(ant.getMyAnts());
    	final Ants ant = Ants.getInstance();
    	ArrayList<Pair> hills = ant.getOurHillList(); // COULD BE MORE THAN ONE HILL FIX FOR THIS CASE
    	//System.out.println(ant);

    	ArrayList<Ant> antList = ant.getMyAnts();
    	//Collections.shuffle(antList);
    	if (!hills.isEmpty()) { // if we have a hill sort by ants furthest from hill
    		final Pair ourHill = hills.get(0);
    		Collections.sort(antList, new Comparator<Ant>(){

			@Override
			public int compare(Ant ant1, Ant ant2) { // sort by those farthest from hill
				int dist1;
				int dist2;
				dist1 = ant.getDistance(ant1, ourHill);
				dist2 = ant.getDistance(ant2, ourHill);
				
				if (dist1 > dist2) {
					return 1;
				}
				
				else if (dist1 < dist2) {
					return -1;
				}
				
				else {
					return 0;
				}
			}
    		
    	});
    	} // end if
       // for (Ant myAnt : ant.getMyAnts()) {
    	for (Ant myAnt : antList) {
        	//System.out.println(ant);
            // for (Aim direction : Aim.values()) {
            	    Aim direction = ant.getLargestNeighbor(myAnt.getCord());
            		Tile newTile = ant.getTile(myAnt, direction);
            		
              if (newTile.isPassable() && newTile.isTargeted() == false && !newTile.isOccupied()) {
            	   	
                    ant.issueOrder(myAnt, direction);
                   // ant.updateTilePValue(myAnt.getRow(), myAnt.getCol(), constants.NORMAL_VALUE);
                  //  System.out.println(newTile);
                    ant.updateTileType(myAnt, constants.LAND); // Set tile where ant was previously back to land
                    //System.out.println(ants.getTile(myAnt));
                    myAnt.setCord(newTile.getRow(), newTile.getCol());  // Update Ant object with new location
                    newTile.setTargeted(true); // location is going to be moved to next turn by a friendly ant
                    ant.updateTileType(newTile, constants.MY_ANT);
                    //ant.updateTilePValue(newTile.getRow(), newTile.getCol(), constants.FRIENDLY_ANT_VALUE); // set tile to 0 ant moved to
                    //System.out.println(newTile);
                   // System.out.println(ants.myAntList());
                    }
                }
           }
        
}
