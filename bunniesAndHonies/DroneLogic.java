package bunniesAndHonies;

import java.util.Random;

import battlecode.common.*;

public class DroneLogic extends RobotLogic {

	private RobotController myController;
	Random rand;
	private MapLocation attTarget;
	private int myRange;
	private Team enemyTeam;
	private Team myTeam;
	
    public DroneLogic(RobotController controller) {
        super();
        myController = controller;
        rand = new Random(myController.getID());
        attTarget = myController.getLocation();
        myRange = myController.getType().attackRadiusSquared;
		enemyTeam = myController.getTeam().opponent();
		myTeam = myController.getTeam();
    }
    
    public void run()
	{
		try {
			basicSupply(myController, myTeam);
			attack(myController, myRange, enemyTeam);
			attTarget = getAttTarget();
			move(attTarget);
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

}
