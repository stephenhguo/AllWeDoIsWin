package team397;

import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class MinerLogic extends RobotLogic {

	private boolean mining, exploring;
	private MapLocation loc, next;
	private Direction dir;
	private Direction facing;
	private double originalOre;
	
    public MinerLogic(RobotController controller) throws GameActionException {
    	super(controller);
		loc=rc.getLocation();
		dir=loc.directionTo(rc.senseEnemyHQLocation());
		next=null;
		facing = getRandDir();
    }
    
    public void run()
	{
		rc.setIndicatorString(1, "Searching: " + exploring);
		try {
			mow();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public void emergencyRoam() throws GameActionException {
    	if(rc.senseOre(rc.getLocation())==0.0){
    		roam();
    	}
    }
    
    public void mow() throws GameActionException{
    	loc = rc.getLocation();
    	double curOre = rc.senseOre(loc);
    	if(curOre> originalOre/2){
    		if (rc.isCoreReady() && rc.canMine()){
    			
                rc.mine();
            }
    	} else{
    		if(!rc.isCoreReady()){
    			return;
    		}

    		Direction toHQ = loc.directionTo(rc.senseHQLocation()), tempface;
			Direction[] dirs;
			Direction[] dirs_L = {toHQ.rotateLeft().rotateLeft(),
    				toHQ.rotateLeft().rotateLeft().rotateLeft(),
    				toHQ.rotateLeft(),
    				toHQ.rotateRight().rotateRight(),
    				toHQ.rotateRight().rotateRight().rotateLeft(),
    				toHQ.rotateRight(),
                    toHQ.opposite(),
                    toHQ};
			Direction[] dirs_R = {toHQ.rotateRight().rotateRight(),
    				toHQ.rotateRight().rotateRight().rotateLeft(),
    				toHQ.rotateRight(),
    				toHQ.rotateLeft().rotateLeft(),
    				toHQ.rotateLeft().rotateLeft().rotateLeft(),
    				toHQ.rotateLeft(),
                    toHQ.opposite(),
                    toHQ};
    		if (Xprod(toHQ, facing) >= 0) //if facing to left of HQ
    			dirs = dirs_L;
    		else
    			dirs = dirs_R;
    		double oreThere, maxOre = -1;
    		Direction maxD = null;
    		MapLocation tempnext, maxNext = null;
	    	for(int i=0; i<3; i++){
	    		tempface = dirs[i];
	    		tempnext = loc.add(tempface);
	    		oreThere = rc.senseOre(tempnext);
	    		if(oreThere > maxOre && !rc.isLocationOccupied(tempnext) && rc.canMove(tempface)){
	    			maxOre = oreThere;
	    			maxD = tempface;
	    			maxNext = tempnext;
	    		}
	    	}
    		if(maxD != null && maxOre >= curOre && !rc.isLocationOccupied(maxNext) && rc.canMove(maxD)){
    			originalOre = maxOre;
    			rc.move(maxD);
    			return;
    		}
    		maxOre = -1;
    		maxD = null;
    		maxNext = null;
	    	for(int i=3; i<6; i++){
	    		tempface = dirs[i];
	    		tempnext = loc.add(tempface);
	    		oreThere = rc.senseOre(tempnext);
	    		if(oreThere > maxOre && !rc.isLocationOccupied(tempnext) && rc.canMove(tempface)){
	    			maxOre = oreThere;
	    			maxD = tempface;
	    			maxNext = tempnext;
	    		}
	    	}
    		if(maxD != null && maxOre >= curOre && !rc.isLocationOccupied(maxNext) && rc.canMove(maxD)){
    			originalOre = maxOre;
    			rc.move(maxD);
    			return;
    		}
    		maxOre = -1;
    		maxD = null;
    		maxNext = null;
	    	for(int i=6; i<dirs.length; i++){
	    		tempface = dirs[i];
	    		tempnext = loc.add(tempface);
	    		oreThere = rc.senseOre(tempnext);
	    		if(oreThere > maxOre && !rc.isLocationOccupied(tempnext) && rc.canMove(tempface)){
	    			maxOre = oreThere;
	    			maxD = tempface;
	    			maxNext = tempnext;
	    		}
	    	}
    		if(maxD != null && maxOre >= curOre && !rc.isLocationOccupied(maxNext) && rc.canMove(maxD)){
    			originalOre = maxOre;
    			rc.move(maxD);
    			return;
    		}
	    	
	    	moveAwayFrom(loc.add(facing.opposite()));	
    	}
    }
    
    private int Xprod(Direction dir1, Direction dir2){
    	int[] v1 = toVector(dir1), v2 = toVector(dir2);
    	return v1[0]*v2[1] - v1[1]*v2[0];
    }
    
    private int[] toVector(Direction dir){
    	int[] result = new int[2];
    	if(dir.equals(Direction.NORTH)){
    		result[0] = 0;
    		result[1] = 1;
    	}
    	else if(dir.equals(Direction.NORTH_WEST)){
    		result[0] = -1;
    		result[1] = 1;
    	}
    	else if(dir.equals(Direction.WEST)){
    		result[0] = -1;
    		result[1] = 0;
    	}
    	else if(dir.equals(Direction.SOUTH_WEST)){
    		result[0] = -1;
    		result[1] = -1;
    	}
    	else if(dir.equals(Direction.SOUTH)){
    		result[0] = 0;
    		result[1] = -1;
    	}
    	else if(dir.equals(Direction.SOUTH_EAST)){
    		result[0] = 1;
    		result[1] = -1;
    	}
    	else if(dir.equals(Direction.EAST)){
    		result[0] = 1;
    		result[1] = 0;
    	}
    	else if(dir.equals(Direction.NORTH_EAST)){
    		result[0] = 1;
    		result[1] = 1;
    	}
    	return result;
    }
    
   
   /**
    * This is how the Miners will mine everything from our corner
    * to midfield and then the other end
    */
   public void mowTheLawn() throws GameActionException{
      
      MapLocation loc=rc.getLocation();
      double ore=rc.senseOre(loc);
      if (ore>1){//mine
          if (rc.isCoreReady() && rc.canMine()){
              rc.mine();
          }
      } else{
          //move to next closest spot with ore and mine
          next=loc.add(dir);
          boolean occ=rc.isLocationOccupied(next);
          if (!rc.senseTerrainTile(next).isTraversable() || occ){
              if (rand.nextDouble()>1){
                  next=loc.add(dir.rotateLeft()); 
              } else {
                  next=loc.add(dir.rotateRight());
              }
          } else {
              if (rc.isCoreReady() && rc.canMove(dir)){
                  moveAwayFrom(rc.senseHQLocation());
              } 
          }
      }
   }
   
   
   
   
   
   
   
   /*
    *  
   public void moveAndMine() throws GameActionException{
	    //Finds max ore location

		if(!mining){
			if (exploring){
				RobotInfo[] nearby = rc.senseNearbyRobots(2, myTeam);
				if (nearby.length == 0){
					exploring = false;
					goalStrength = 100.;
				}
				else{
					goalStrength = -100.;
					simpleGoal(rc.senseHQLocation());
					rc.yield();
				}
			}

			MapLocation maxOreLoc=rc.getLocation();
		    double maxOre=rc.senseOre(maxOreLoc);
		    boolean mineAtCurrentLoc=true;
		    for (MapLocation m: MapLocation.getAllMapLocationsWithinRadiusSq(maxOreLoc, RobotType.BEAVER.sensorRadiusSquared)){
		        double oreAtM=rc.senseOre(m);
		        if(oreAtM>maxOre){
		            maxOre=oreAtM;
		            maxOreLoc=m;
		            mineAtCurrentLoc=false;
		        }
		    }
			
			    //Mines at currentLocation if 
		    if (mineAtCurrentLoc){
		        if (rc.isCoreReady() && rc.canMine()){
		            rc.mine();
		            int fate = rand.nextInt(100);
		            if(fate<25){
		            	mining = true;
		            }
		            //mining = true;
		            rc.yield();
		        }
		    }
		
		    else{
		        Direction move=rc.getLocation().directionTo(maxOreLoc);
		        if (rc.isCoreReady() && rc.canMove(move))

		            moveToArea(maxOreLoc, 25);

		        	rc.yield();
		    }
		} else {
			 if (rc.isCoreReady() && rc.canMine()){
		            rc.mine();
		            rc.yield();
			 }
			 if(rc.senseOre(rc.getLocation())<1){
				 mining = false;
			 }
		}
	}
    */
   
   private Direction getRandDir(){
	   Random rand = new Random(rc.getID());
	   int dirNum = rand.nextInt(8);
	   switch(dirNum){
	   case 0:
		   return Direction.NORTH;
	   case 1:
		   return Direction.NORTH_EAST;
	   case 2:
		   return Direction.EAST;
	   case 3:
		   return Direction.SOUTH_EAST;
	   case 4:
		   return Direction.SOUTH;
	   case 5:
		   return Direction.SOUTH_WEST;
	   case 6:
		   return Direction.WEST;
	   default:
		   return Direction.NORTH_WEST;
	   }
   }
}
