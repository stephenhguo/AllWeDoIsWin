package bunniesAndHonies;

import java.util.Random;

import battlecode.common.*;

public class BeaverLogic extends RobotLogic{

	private RobotController myController;
	Random rand;
	private final int ATTACKXPORT = 0;
	private final int ATTACKYPORT = 1;
	private MapLocation attTarget;
	private int myRange;
	private Team enemyTeam;
	
	public BeaverLogic(RobotController controller)
	{
		super();
		myController = controller;
		attTarget = controller.getLocation();
		rand = new Random(myController.getID());
		myRange = myController.getType().attackRadiusSquared;
		enemyTeam = myController.getTeam().opponent();
	}
	
	public void run()
	{
		try {
			buildNext();
			
			attack(myController, myRange, enemyTeam);
			//roam();
			
			//attTarget = getAttTarget();
			//move(attTarget);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public MapLocation getAttTarget(){
		int msgx;
		int msgy;
		try {
			msgx = myController.readBroadcast(ATTACKXPORT);
			msgy = myController.readBroadcast(ATTACKYPORT);
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			msgx = 0;
			msgy = 0;
		}

		return new MapLocation(msgx,msgy);		
	}
	
	public void buildNext(){
		if(!myController.isCoreReady()){
			return;
		}
		int nextBuilding;
		try {
			nextBuilding = myController.readBroadcast(NEXTBUILD);
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			nextBuilding = 0;
		}
		RobotType nextRob = getType(nextBuilding);
		if(myController.hasBuildRequirements(nextRob)){
			boolean canBuild = false;
			Direction builddir = Direction.NORTH;
			for(Direction dir : Direction.values()){
				if(myController.canBuild(dir, nextRob)){
					builddir = dir;
					canBuild = true;
					break;
				}
			}
			if(canBuild){
				try {
					myController.build(builddir, nextRob);
					myController.broadcast(NEXTBUILD, (nextBuilding+1)%8);
					myController.yield();
				} catch (GameActionException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		}
	}
	
	public void move(MapLocation target){
		Direction movedir = myController.getLocation().directionTo(target);
		if(myController.canMove(movedir)){
			try {
				myController.move(movedir);
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		} else {
			//roam();
			myController.disintegrate();
		}
	}
	
	public void roam(){
		int dir = rand.nextInt(8);
		myController.setIndicatorString(0, Double.toString(myController.getHealth()));
		Direction movedir;
		switch(dir){
		case 0:
			movedir = Direction.NORTH;
			break;
		case 1:
			movedir = Direction.NORTH_EAST;
			break;
		case 2:
			movedir = Direction.EAST;
			break;
		case 3:
			movedir = Direction.SOUTH_EAST;
			break;
		case 4:
			movedir = Direction.SOUTH;
			break;
		case 5:
			movedir = Direction.SOUTH_WEST;
			break;
		case 6:
			movedir = Direction.WEST;
			break;
		case 7:
			movedir = Direction.NORTH_WEST;
			break;
		default:
			movedir=Direction.NORTH;
		}
		try {
			myController.move(movedir);
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	private RobotType getType(int i){
		switch(i){
		case 0:
			return RobotType.HELIPAD;
		case 1:
			return RobotType.MINERFACTORY;
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
		default:
			return RobotType.SUPPLYDEPOT;
		}
	}
}
