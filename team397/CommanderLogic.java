package team397;



import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;


public class CommanderLogic extends RobotLogic {
    
    
    
    public CommanderLogic(RobotController controller) {
    	super(controller);

    }
    
    public void run() throws GameActionException{
    	radio.setSwarm(rc.getLocation(), RobotType.TANK); //Tells Tanks to swarm with this Commander
		attack(myRange);
		MapLocation attTarget = radio.getSwarmLoc(RobotType.COMMANDER);
		//int attRad = radio.getSwarmRadius(RobotType.COMMANDER);
		if(rc.getHealth()<70){
			simpleGoal(rc.senseHQLocation());
		} else if(rc.getHealth()>120){
			goAttack(true,radio.getEnemyHQLoc(),0);
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
