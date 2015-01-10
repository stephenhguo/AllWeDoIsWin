package bunniesAndHonies;

import java.util.Random;
import battlecode.common.*;

public class BeaverLogic extends RobotLogic{

	private RobotController myController;
	private final int ATTACKPORT = 0;
	private MapLocation attTarget;
	
	public BeaverLogic(RobotController controller)
	{
		super();
		myController = controller;
		attTarget = controller.getLocation();
	}
	
	public void run()
	{
		try {
			//for beavers, what if we change this to a, if under attack, fight back, else go mine/build
			//attack();
			MapLocation attTarget = getAttTarget();
			move(attTarget);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public MapLocation getAttTarget(){
		int msg;
		try {
			msg = myController.readBroadcast(ATTACKPORT);
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			msg = 0;
		}
		int x = msg/1000;
		int y = (msg-x*1000);
		return new MapLocation(x,y);		
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
			roam();
		}
	}
	
	public void roam(){
		Random rand = new Random();
		int dir = rand.nextInt(8);
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
}
