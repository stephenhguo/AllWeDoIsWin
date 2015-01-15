package team397;

import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class MinerLogic extends RobotLogic {

	private boolean mining;

	static Direction facing;
	static Random rand;
	private int myRange;
	
    public MinerLogic(RobotController controller) {
    	super(controller);
		rand = new Random(rc.getID());
		mining = false;
		myRange = rc.getType().attackRadiusSquared;
    }
    
    public void run()
	{
	    rand=new Random(rc.getID());
	    
		try {
			emergencyRoam();
			basicSupply();
			attack(myRange);
			moveAndMine();
			roam(rand);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public void emergencyRoam() {
    	if(rc.senseOre(rc.getLocation())<1.0){
    		roam(rand);
    	}
    }
    
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
		            rc.move(rc.getLocation().directionTo(maxOreLoc));
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

}
