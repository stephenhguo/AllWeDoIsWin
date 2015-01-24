package team397;

import battlecode.common.*;

public class DroneLogic extends RobotLogic {

	private MapLocation attTarget;
	boolean hunting;
	RobotInfo huntTarget;
	private int team;
	
	
    public DroneLogic(RobotController controller) throws GameActionException {
        super(controller);
        attTarget = rc.getLocation();
        team = radio.checkJobs(RobotType.DRONE);
    }
    

    public void run() throws GameActionException
	{
		
		if(team == -1)
			team = radio.checkJobs(RobotType.DRONE);
		if(team == radio.HUNT_TEAM)
			team = radio.shouldHunterSwitch();
		if(team != -1)
			radio.incAttendance(RobotType.DRONE, team);
		
		rc.setIndicatorString(1, "Team: " + team);
		
		basicSupply();
		
		if(team == radio.HUNT_TEAM){
			attack(myRange);
			moveToArea(rc.senseEnemyHQLocation(),225);
			/*hunt();
			if(hunting){
				stalk(huntTarget);
			}
			if(!hunting){
				attack(myRange);
				moveToArea(rc.senseEnemyHQLocation(),170);
				//attTarget = radio.getSwarmLoc(RobotType.DRONE);
				//int attRad = radio.getSwarmRadius(RobotType.DRONE);
				//simpleGoal(attTarget, attRad);
				  
				 
			}
			*/
		}
		else{
			attack(myRange);
			attTarget = radio.getSwarmLoc(RobotType.DRONE);
			int attRad = radio.getSwarmRadius(RobotType.DRONE);
			if (attTarget.equals(rc.senseHQLocation()))
				simpleGoal(attTarget, attRad);
			else
				goAttack(false, attTarget, attRad);
		}
	}

    public void hunt(){
    	RobotInfo[] enemies = rc.senseNearbyRobots(myRange, enemyTeam);
		if(enemies.length==0){
			return;
		}
		RobotInfo enemyToAttack = null;
		for(RobotType pref: huntPreference)
		{
			for(RobotInfo inf : enemies){
				if(inf.type.equals(pref)){
					hunting = true;
					huntTarget = inf;
					break;
				}
			}	
		}
    }
    
    public void stalk(RobotInfo inf) throws GameActionException{
    	rc.setIndicatorString(3, Boolean.toString(hunting));
    	if(inf.health<=0){
    		hunting = false;
    	}
    	while(inf.health>=0){
    		if(rc.isWeaponReady() && rc.canAttackLocation(inf.location)){
    			rc.attackLocation(inf.location);
    			rc.yield();
    		} else{
    			simpleGoal(inf.location);
    		}
    	}
    }
  
}
