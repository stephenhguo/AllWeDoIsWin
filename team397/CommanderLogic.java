package team397;

import battlecode.common.*;

public class CommanderLogic extends RobotLogic {

	MapLocation attTarget;
    public CommanderLogic(RobotController controller) {
    	super(controller);
    	attTarget = null;
    	
    }
    
    public void run() throws GameActionException{
    	radio.setSwarm(rc.getLocation(), RobotType.TANK);
		attack(myRange);
		attTarget = radio.getSwarmLoc(RobotType.COMMANDER);
		//int attRad = radio.getSwarmRadius(RobotType.COMMANDER);
		PFObject[] objects;
		if (attTarget.equals(rc.senseHQLocation())){
			objects = avoidEnemyTowersAndHQ();
			PFObject[] base = {new SurroundObject(attTarget, 0), new LineObject(attTarget, new MapLocation(-1,-1), 2, -80)};
			objects = combine(objects, base);
		}
		else
			objects = avoidEnemyTowersAndHQ(attTarget);
		if(rc.getHealth()<70){
			goTo(rc.senseHQLocation());
		} else if(rc.getHealth()>120){
			goTo(radio.getEnemyHQLoc());
		}
    	flashIfDying();
    }
    
    public void flashIfDying() throws GameActionException{
    	double health = rc.getHealth();
    	if(health<70.0 && rc.getFlashCooldown()==0){
    		MapLocation self = rc.getLocation();
    		Direction toHQ = self.directionTo(rc.senseHQLocation());
    		MapLocation teleportLoc = self.add(toHQ, 10);
    		int dist = 9;
    		while(rc.isLocationOccupied(teleportLoc) && dist>0){
    			teleportLoc = self.add(toHQ, dist);
    			dist--;
    		}
    		if(dist!=0){
    			rc.castFlash(teleportLoc);
    		}
    	}
    }

}
