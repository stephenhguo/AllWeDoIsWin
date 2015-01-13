package team397;

import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class TankLogic extends RobotLogic {

	private RobotController myController;
	private int myRange;
	private Team enemyTeam;
	private Random rand;
	private MapLocation attTarget;
	
    public TankLogic(RobotController controller) {
        super();
        myController = controller;
        myRange = myController.getType().attackRadiusSquared;
		enemyTeam = myController.getTeam().opponent();
		rand = new Random(myController.getID());
    }
    
    public void run(){
    	attack(myController, myRange, enemyTeam);
    	attTarget = getAttTarget();
		move(attTarget);
		roam(myController, rand);
    }
    
    public MapLocation getAttTarget(){
		int msgx;
		int msgy;
		try {
			msgx = myController.readBroadcast(TANKPORTX);
			msgy = myController.readBroadcast(TANKPORTY);
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
