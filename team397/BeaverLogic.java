package team397;

import java.util.Random;

import battlecode.common.*;

public class BeaverLogic extends RobotLogic{

	private MapLocation attTarget;
	private boolean mining;

	static Direction facing;
	static Random rand;

	private int myRange;
	
	public BeaverLogic(RobotController controller)
	{
		super(controller);
		attTarget = rc.getLocation();
		rand = new Random(rc.getID());
		myRange = rc.getType().attackRadiusSquared;
		mining = false;
	}
	
	public void run() throws GameActionException
	{
	    rand=new Random(rc.getID());
	    


		//for beavers, what if we change this to a, if under attack, fight back, else go mine/build
		//attack();
//			MapLocation attTarget = getAttTarget();
//			move(attTarget);
		basicSupply();
		buildNext();

		attack(myRange);
		moveAndMine();
		roam(rand);
		
		//attTarget = getAttTarget();
		//move(attTarget);

	}
	
	public void buildNext() throws GameActionException{
		if(!rc.isCoreReady()){
			return;
		}
		
		int nextBuilding = radio.checkNextBuild();
		System.out.println(nextBuilding);

		RobotType nextRob = getType(nextBuilding);
		if(rc.hasBuildRequirements(nextRob)){
			boolean canBuild = false;
			Direction builddir = Direction.NORTH;
			for(Direction dir : Direction.values()){
				if(rc.canBuild(dir, nextRob)){
					builddir = dir;
					canBuild = true;
					break;
				}
			}
			if(canBuild){
				rc.build(builddir, nextRob);
				radio.newBuild(nextBuilding + 1);
				rc.yield();
			}
		}
	}
	
	public void move(MapLocation target){
		Direction movedir = rc.getLocation().directionTo(target);
		if(rc.canMove(movedir)){
			try {
				rc.move(movedir);
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		} else {
			roam(rand);
		}
	}

	/**
	 * Finds MapLocation of max ore in sensor radius and moves toward it.
	 * @throws GameActionException 
	 */
	public void moveAndMine() throws GameActionException{
	    //Finds max ore location
		if(!mining){
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
		    } else{
		        Direction move=rc.getLocation().directionTo(maxOreLoc);
		        if (rc.isCoreReady() && rc.canMove(move))
		            rc.move(rc.getLocation().directionTo(maxOreLoc));
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
	
	private RobotType getType(int i){
		switch(i){
		case 0:
			return RobotType.MINERFACTORY;
		case 1:
			return RobotType.HELIPAD;
		/*
		case 2:
			return RobotType.BARRACKS;
		case 3:
			return RobotType.TANKFACTORY;
			
			
		
		case 2:
			return RobotType.SUPPLYDEPOT;
		case 3:
			return RobotType.BARRACKS;
		case 4:
			return RobotType.TANKFACTORY;
		case 5:
			return RobotType.AEROSPACELAB;
		case 6:
			return RobotType.TECHNOLOGYINSTITUTE;
		case 7:
			return RobotType.TRAININGFIELD;
		*/
		default:
			return RobotType.HELIPAD;
			
		}
	}
}
