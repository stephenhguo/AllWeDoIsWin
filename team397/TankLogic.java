package team397;

import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class TankLogic extends RobotLogic {

	private MapLocation commanderLoc;
	
    public TankLogic(RobotController controller) {
        super(controller);
    }
    
    public void run() throws GameActionException{
    	basicSupply();
		attack(myRange);
		commanderLoc = radio.getSwarmLoc(RobotType.TANK);
		int radius = radio.getSwarmRadius(RobotType.TANK);
		PFObject[] objects;
		if (radius >= 0){
			objects = new PFObject[1];
			objects[0] = new SurroundObject(commanderLoc, 30., -30., 4);
		}
		else if(commanderLoc.equals(rc.senseHQLocation())){
			objects = avoidEnemyTowersAndHQ();
			PFObject[] base = {new SurroundObject(commanderLoc, 0), new LineObject(commanderLoc, new MapLocation(-1,-1), 2, -80)};
			objects = combine(objects, base);
		}
		else
			objects = avoidEnemyTowersAndHQ();
			
		makeNextMove(objects);

    }
    /*
    public void run(){
    	attack(myController, myRange, enemyTeam);
    	attTarget = getAttTarget();
        myRange = rc.getType().attackRadiusSquared;
    }
    
    public void run() throws GameActionException{
    	attack(myRange);
    	attTarget = radio.getSwarmLoc(RobotType.TANK);
		move(attTarget);
		roam();
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
	*/
}