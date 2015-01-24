package team397;

import battlecode.common.*;

public class DroneLogic extends RobotLogic {

	private MapLocation attTarget;
	boolean hunting;
	RobotInfo huntTarget;
	
	
    public DroneLogic(RobotController controller) {
        super(controller);
        attTarget = rc.getLocation();
    }
    
    public void run()
	{
		try {
			basicSupply();
			//roam();
			hunt();
			//attack(myRange);
			if(hunting){
				stalk(huntTarget);
			}
			if(!hunting){
				goTo(rc.senseEnemyHQLocation(),RobotType.HQ.attackRadiusSquared);
				//attTarget = radio.getSwarmLoc(RobotType.DRONE);
				//int attRad = radio.getSwarmRadius(RobotType.DRONE);
				//goTo(attTarget, attRad);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public void hunt(){
    	RobotInfo[] enemies = rc.senseNearbyRobots(myRange, enemyTeam);
		if(enemies.length==0){
			return;
		}
		double minHealth = 2000.0;
		RobotInfo enemyToAttack = enemies[0];
		for(RobotType pref: attackPreference)
		{
			for(RobotInfo inf : enemies){
				if(inf.type.equals(pref)){
					hunting = true;
					huntTarget = inf;
					break;
				}
				/*
				minHealth=2000.0;
				if(inf.health<minHealth){
					minHealth = inf.health;
					enemyToAttack = inf;
				}
				*/
			}	
		}
    }
    
    public void stalk(RobotInfo inf) throws GameActionException{
    	rc.setIndicatorString(0, Boolean.toString(hunting));
    	if(inf.health<=0){
    		hunting = false;
    	}
    	while(inf.health>=0){
    		if(rc.isWeaponReady() && rc.canAttackLocation(inf.location)){
    			rc.attackLocation(inf.location);
    			rc.yield();
    		} else{
    			goTo(inf.location);
    		}
    	}
    }
  
}
