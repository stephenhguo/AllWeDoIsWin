package team397;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class MinerLogic extends RobotLogic {

	private boolean mining, exploring;
	
	
    public MinerLogic(RobotController controller) throws GameActionException {
    	super(controller);
		mining = false;
		exploring = false; //radio.shouldSearch(RobotType.MINER);
    }
    
    public void run()
	{
		rc.setIndicatorString(1, "Searching: " + exploring);
		try {
			emergencyRoam();
			basicSupply();
			attack(myRange);
			moveAndMine();
			roam();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public void emergencyRoam() throws GameActionException {
    	if(rc.senseOre(rc.getLocation())<1.0){
    		roam();
    	}
    }
    
    public void moveAndMine() throws GameActionException{
	    //Finds max ore location

		if(!mining){
			if (exploring){
				RobotInfo[] nearby = rc.senseNearbyRobots(38, myTeam);
				if (nearby.length == 0){
					exploring = false;
					goalStrength = 100.;
				}
				else{
					goalStrength = -100.;
					goTo(rc.senseHQLocation());
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
		            if(fate<85){
		            	mining = true;
		            }
		            //mining = true;
		            rc.yield();
		        }
		    }
		
		    else{
		        Direction move=rc.getLocation().directionTo(maxOreLoc);
		        if (rc.isCoreReady() && rc.canMove(move))
		            goTo(maxOreLoc);
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
    
    /*
    public void moveAndMine() throws GameActionException{
	    //Finds max ore location
		if(!mining){
		    MapLocation maxOreLoc=rc.getLocation();
		    double maxOre=rc.senseOre(maxOreLoc);
		    boolean mineAtCurrentLoc=true;
		    for (MapLocation m: MapLocation.getAllMapLocationsWithinRadiusSq(maxOreLoc, RobotType.MINER.sensorRadiusSquared)){
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
		            if(fate<40){
		            	mining = true;
		            }
		            rc.yield();
		        }
		    } else{
		        Direction move=rc.getLocation().directionTo(maxOreLoc);
		        if (rc.isCoreReady() && rc.canMove(move)) {
		        	goTo(maxOreLoc);
		        	rc.yield();
		        } else if((rc.isCoreReady() && rc.canMine())) {
		            rc.mine();
		            rc.yield();
		        }
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
}
