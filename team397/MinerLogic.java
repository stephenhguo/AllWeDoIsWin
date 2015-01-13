package team397;

import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class MinerLogic extends RobotLogic {

	private RobotController myController;

	private boolean mining;

	static Direction facing;
	static Random rand;
	private int myRange;
	private Team enemyTeam;
	private Team myTeam;
	
    public MinerLogic(RobotController controller) {
    	super();
		myController = controller;
		rand = new Random(myController.getID());
		mining = false;
		myRange = myController.getType().attackRadiusSquared;
		enemyTeam = myController.getTeam().opponent();
		myTeam = myController.getTeam();
    }
    
    public void run()
	{
	    rand=new Random(myController.getID());
	    
		try {
			emergencyRoam();
			basicSupply(myController, myTeam);
			attack(myController, myRange, enemyTeam);
			moveAndMine();
			roam(myController, rand);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public void emergencyRoam() {
    	if(myController.senseOre(myController.getLocation())<1.0){
    		roam(myController, rand);
    	}
    }
    
    public void moveAndMine() throws GameActionException{
	    //Finds max ore location
		if(!mining){
		    MapLocation maxOreLoc=myController.getLocation();
		    double maxOre=myController.senseOre(maxOreLoc);
		    boolean mineAtCurrentLoc=true;
		    for (MapLocation m: MapLocation.getAllMapLocationsWithinRadiusSq(maxOreLoc, RobotType.MINER.sensorRadiusSquared)){
		        double oreAtM=myController.senseOre(m);
		        if(oreAtM>maxOre){
		            maxOre=oreAtM;
		            maxOreLoc=m;
		            mineAtCurrentLoc=false;
		        }
		    }
		    //Mines at currentLocation if 
		    if (mineAtCurrentLoc){
		        if (myController.isCoreReady() && myController.canMine()){
		            myController.mine();
		            int fate = rand.nextInt(100);
		            if(fate<40){
		            	mining = true;
		            }
		            myController.yield();
		        }
		    } else{
		        Direction move=myController.getLocation().directionTo(maxOreLoc);
		        if (myController.isCoreReady() && myController.canMove(move)) {
		            myController.move(myController.getLocation().directionTo(maxOreLoc));
		        	myController.yield();
		        } else if((myController.isCoreReady() && myController.canMine())) {
		            myController.mine();
		            myController.yield();
		        }
		    }
		} else {
			 if (myController.isCoreReady() && myController.canMine()){
		            myController.mine();
		            myController.yield();
			 }
			 if(myController.senseOre(myController.getLocation())<1){
				 mining = false;
			 }
		}
	}

}
