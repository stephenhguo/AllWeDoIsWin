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
    	if(curOre>0){
    		if (rc.isCoreReady() && rc.canMine()){
                rc.mine();
            }
    	} else{
    		if(!rc.isCoreReady()){
    			return;
    		}
    		next = loc.add(facing);
    		Direction tempface = facing;
	    	for(int i=0; i<8; i++){
	    		tempface = tempface.rotateRight();
	    		MapLocation tempnext = loc.add(tempface);
	    		if(rc.senseOre(tempnext)>0 && !rc.isLocationOccupied(tempnext) && rc.canMove(tempface)){
	    			rc.move(tempface);
	    			return;
	    		}
	    	}
	    	moveAwayFrom(loc.add(facing.opposite()));	
    	}
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
