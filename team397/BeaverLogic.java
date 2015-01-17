package team397;

import battlecode.common.*;

public class BeaverLogic extends RobotLogic{

	private boolean mining, exploring;
	private int myRange;
	
	public BeaverLogic(RobotController controller) throws GameActionException
	{
		super(controller);
		exploring = radio.shouldSearch(RobotType.BEAVER);
		mining = false;
	}
	
	public void run() throws GameActionException
	{
		//for beavers, what if we change this to a, if under attack, fight back, else go mine/build
		//attack();
//			MapLocation attTarget = getAttTarget();
//			move(attTarget);
		rc.setIndicatorString(1, "Searching: " + exploring);
		basicSupply();
		buildNext();

		attack(myRange);
		moveAndMine();
		//roam();
		
		//attTarget = getAttTarget();
		//move(attTarget);

	}
	
	public void buildNext() throws GameActionException{
		if(!rc.isCoreReady()){
			return;
		}
		
		int nextBuilding = radio.checkNextBuild();

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
	
	public void move(MapLocation target) throws GameActionException{
		Direction movedir = rc.getLocation().directionTo(target);
		if(rc.canMove(movedir)){
			try {
				rc.move(movedir);
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		} else {
			roam();
		}
	}

	/**
	 * Finds MapLocation of max ore in sensor radius and moves toward it.
	 * @throws GameActionException 
	 */
	public void moveAndMine() throws GameActionException{
	    //Finds max ore location

		if(!mining){
			if (exploring){
				RobotInfo[] nearby = rc.senseNearbyRobots(24, myTeam);
				if (nearby.length == 0 && 24 <= rc.getLocation().distanceSquaredTo(rc.senseHQLocation())){
					exploring = false;
					goalStrength = 100.;
				}
				else{
					goalStrength = -500.;
					roam();
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
