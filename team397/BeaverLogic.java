package team397;

import java.util.Random;

import battlecode.common.*;

public class BeaverLogic extends RobotLogic{

	private MapLocation attTarget;
	private boolean mining, exploring;
	static Direction facing;
	
	public BeaverLogic(RobotController controller) throws GameActionException
	{
		super(controller);
		attTarget = rc.getLocation();
		exploring = radio.shouldSearch(RobotType.BEAVER);
		mining = false;
	}
	
	public void run() throws GameActionException
	{
		//for beavers, what if we change this to a, if under attack, fight back, else go mine/build
		//attack();
//			MapLocation attTarget = getAttTarget();
//			move(attTarget);
		//rc.setIndicatorString(1, "Searching: " + exploring);
		basicSupply();
		//buildNext();

		attack(myRange);
		phaseBuild();
		//moveAndMine();
		roam();
		
		//attTarget = getAttTarget();
		//move(attTarget);

	}
	
	public void phaseBuild() throws GameActionException{
		int phase = radio.getBuildPhase();
		int mineFacNum = radio.readCount(RobotType.MINERFACTORY);
		int heliNum = radio.readCount(RobotType.HELIPAD);
		int MITNum = radio.readCount(RobotType.TECHNOLOGYINSTITUTE);
		int TrainFNum = radio.readCount(RobotType.TRAININGFIELD);
		int BarNum = radio.readCount(RobotType.BARRACKS);
		int TankFNum = radio.readCount(RobotType.TANKFACTORY);
		switch (phase){
		case 0:
			if(build(RobotType.MINERFACTORY)){
				radio.advanceBuildPhase(1);
				rc.yield();
			}
			return;		
		case 1:
			if(mineFacNum<1){
				if(build(RobotType.MINERFACTORY)){
					rc.yield();
				}
			}
			if(heliNum<2){
				if(build(RobotType.HELIPAD)){
					rc.yield();
				}
			}
			return;
		case 2:
			if(mineFacNum<1){
				if(build(RobotType.MINERFACTORY)){
					rc.yield();
				}
			}
			if(heliNum<2){
				if(build(RobotType.HELIPAD)){
					rc.yield();
				}
			}
			rc.setIndicatorString(1, "Outside "+MITNum);
			if(MITNum<1){
				rc.setIndicatorString(0, "Inside "+radio.readCount(RobotType.DRONE));
				if(build(RobotType.TECHNOLOGYINSTITUTE)){
					rc.yield();
				}
			}
			if(TrainFNum<1){
				if(build(RobotType.TECHNOLOGYINSTITUTE)){
					rc.yield();
				}
			}
			return;
		case 3:
		case 4:
			if(BarNum<1){
				if(build(RobotType.BARRACKS)){
					rc.yield();
				}
			}
			if(TankFNum<2){
				if(build(RobotType.TANKFACTORY)){
					rc.yield();
				}
			}
			if(mineFacNum<1){
				if(build(RobotType.MINERFACTORY)){
					rc.yield();
				}
			}
			if(MITNum<1){
				if(build(RobotType.TECHNOLOGYINSTITUTE)){
					rc.yield();
				}
			}
			if(TrainFNum<1){
				if(build(RobotType.TECHNOLOGYINSTITUTE)){
					rc.yield();
				}
			}
			if(heliNum<2){
				if(build(RobotType.HELIPAD)){
					rc.yield();
				}
			}
			return;
		default:
			return;
		}
	}
	
	public boolean build(RobotType type) throws GameActionException{
		if(!rc.isCoreReady()){
			return false;
		}
		boolean canBuild = false;
		if(rc.hasBuildRequirements(type)){
			Direction builddir = Direction.NORTH;
			for(Direction dir : Direction.values()){
				if(rc.canBuild(dir, type)){
					builddir = dir;
					canBuild = true;
					break;
				}
			}
			if(canBuild){
				rc.build(builddir, type);
			}
		}
		return canBuild;
	}
	
	/*
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
	}*/
	
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
