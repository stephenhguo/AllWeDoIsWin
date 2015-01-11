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
			roam(myController, rand);
			
			//attTarget = getAttTarget();
			//move(attTarget);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			roam(myController, rand);
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
