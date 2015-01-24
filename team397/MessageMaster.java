package team397;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class MessageMaster {

	//these are variables for CHANNELS (1 - 65000)
	private final int[] DRONEATTACK = {0,1,100}; // (x,y)
	private final int[] SOLDIERATTACK = {3,4,103}; //(x,y)
	private final int[] TANKATTACK = {5,6,105}; // (x,y,rad sq)
	//public final int NEXTBUILDING = 2;
	private final int ENEMYTOWER_LOCS = 1003; //begins list of enemy tower locations
	private int ENEMYTOWERS_NUM = 1002;
	private final int[] ENEMYHQLOC = {1000, 1001};
	
	private final int[] DRONESEARCH = {52};
	private final int[] BEAVERSEARCH = {53};
	private final int[] MINERSEARCH = {54};
	
	private final int BUILD_PHASE = 10;
	private final int BEAVER_COUNT = 11;
	private final int MINEFACT_COUNT = 12;
	private final int MINER_COUNT = 13;
	private final int HELIPAD_COUNT = 14;
	private final int DRONE_COUNT = 15;
	private final int MIT_COUNT = 16;
	private final int TRAINF_COUNT = 17;
	private final int COMM_COUNT = 18;
	private final int BAR_COUNT = 19;
	private final int TANKFAC_COUNT = 20;
	private final int TANK_COUNT = 21;
	
	private final int SYMMETRY = 300; // 301, 302 contain center of map
	
	//internal codes
	private final int SWARM = 0;
	private final int SEARCH = 1;
	private final int CHECK_JOBS = 2;
	
	//external codes
	public final int HUNT_TEAM = 5;
	public final int SUPPLY_TEAM = 3;
	public final int ATTACK_TEAM = 4;
	
	//drone subgroup counters
	private final int[] DRONE_JOB_CODES = {SUPPLY_TEAM, HUNT_TEAM, ATTACK_TEAM};
	private final int[] DRONE_HUNT_COUNT = {456, 467}; //(wanted, existing) so HQ sets wanted and each bot increments existing
	private final int[] DRONE_SUPPLY_COUNT = {202, 203};
	private final int[] DRONE_ATTACK_COUNT = {204, 205};
	

	public RobotController rc;
	
	
	public MessageMaster(RobotController controller) {
		rc = controller;
	}
	
	public void reportSymmetry(int code, int midx, int midy) throws GameActionException{ //reports symmetry of the board
		rc.broadcast(SYMMETRY, code);
		rc.broadcast(SYMMETRY + 1, midx);
		rc.broadcast(SYMMETRY + 2, midy);
	}

	
	/*public int checkNextBuild() throws GameActionException{ //what to build next
		return rc.readBroadcast(NEXTBUILDING);
	}
	
	public void newBuild(int buildCode) throws GameActionException{ //sets what to build, assumes code is understood by robot
		rc.broadcast(NEXTBUILDING, buildCode);
		
	}*/

	public void searchOrders(RobotType type, boolean shouldS) throws GameActionException{ //sets type to search, whatever search means for that type
		int[] channel = typeSwitch(type, SEARCH);
		int message = 0;
		if(shouldS)
			message = 1;
		rc.broadcast(channel[0], message);
	}
	
	public boolean shouldSearch(RobotType type) throws GameActionException{ //checks if should be in search mode when instantiated or could be checked throughout
		int[] channel = typeSwitch(type, SEARCH);
		return rc.readBroadcast(channel[0]) == 1;
	}

	public MapLocation getSwarmLoc(RobotType type) throws GameActionException{ //gets swarm location
		int[] channel = typeSwitch(type, SWARM);
		int x = rc.readBroadcast(channel[0]), y = rc.readBroadcast(channel[1]);
		return new MapLocation(x,y);
	}
	
	public int getSwarmRadius(RobotType type) throws GameActionException{ //gets radius_sq of swarm **probably should change name**
		int[] channel = typeSwitch(type, SWARM);
		return rc.readBroadcast(channel[2]);
	}
	
	public void setSwarm(MapLocation loc, RobotType type, int rad_sq) throws GameActionException{ //when HQ sets a swarm
		int[] channel = typeSwitch(type, SWARM);
		rc.broadcast(channel[0], loc.x);
		rc.broadcast(channel[1], loc.y);
		rc.broadcast(channel[2], rad_sq);
	}
	
	public void setSwarm(MapLocation loc, RobotType type) throws GameActionException{ //overriden method of r_sq = 0
		setSwarm(loc, type, 0);
	}

	
	public void setEnemyHQLoc(MapLocation loc) throws GameActionException{ //sets EnemyHQ for all future rounds
		rc.broadcast(ENEMYHQLOC[0], loc.x);
		rc.broadcast(ENEMYHQLOC[1], loc.y);
	}
	
	public MapLocation getEnemyHQLoc() throws GameActionException{ //gets Enemy HQ (10 byte code < 50 bytecode)
		return new MapLocation(rc.readBroadcast(ENEMYHQLOC[0]), rc.readBroadcast(ENEMYHQLOC[1]));
	}
	
	public void setEnemyTowerLocs(MapLocation[] spots) throws GameActionException{ //sets all enemy tower locs in a running list and sets how long list is
		rc.broadcast(ENEMYTOWERS_NUM, spots.length);
		for(int i = 0; i < spots.length; i++){
			rc.broadcast(ENEMYTOWER_LOCS + 2*i, spots[i].x);
			rc.broadcast(ENEMYTOWER_LOCS + 2*i + 1, spots[i].y);
		}
	}
	
	public MapLocation[] getEnemyTowerLocs() throws GameActionException{ //gets enemyTower locs (35 bytecode < 100 bytecode)
		int num = rc.readBroadcast(ENEMYTOWERS_NUM);
		MapLocation[] result = new MapLocation[num];
		for(int i = 0; i < num; i ++){
			result[i] = new MapLocation(rc.readBroadcast(ENEMYTOWER_LOCS + 2*i), rc.readBroadcast(ENEMYTOWER_LOCS + 2*i + 1));
		}
		return result;
	}
	
	public int checkJobs(RobotType type) throws GameActionException{
		int[] jobs = typeSwitch(type, CHECK_JOBS), channels;
		int wanted;
		for(int i = 0; i < jobs.length; i++){
			channels = typeSwitch(type, jobs[i]);
			wanted = rc.readBroadcast(channels[0]);
			if(wanted > 0){
				rc.broadcast(channels[0], wanted - 1);
				return jobs[i];
			}
		}
		return -1;
	}
	
	public void setWanted(RobotType type, int team, int wanted) throws GameActionException{
		int[] channels = typeSwitch(type, team);
		rc.broadcast(channels[0], wanted);
	}
	
	public void incAttendance(RobotType type, int team) throws GameActionException{
		int[] channels = typeSwitch(type, team);
		rc.broadcast(channels[1], rc.readBroadcast(channels[1]) + 1);
	}
	
	public int getAttendance(RobotType type, int team) throws GameActionException{
		int[] channels = typeSwitch(type, team);
		return rc.readBroadcast(channels[1]);
	}
	
	public void resetAttendance(RobotType type, int team) throws GameActionException{
		int[] channels = typeSwitch(type, team);
		rc.broadcast(channels[1], 0);
	}
	
	private int[] typeSwitch(RobotType type, int code){ //codes are defined with constants above
		RobotType[] list = {RobotType.HQ, //0
			  RobotType.TOWER, //1
			  RobotType.MISSILE,//2
			  RobotType.TANK,//3
			  RobotType.COMMANDER,//4
			  RobotType.DRONE,//5
			  RobotType.LAUNCHER, //6
			  RobotType.BASHER,//7
			  RobotType.SOLDIER,//8
			  RobotType.BEAVER,//9
			  RobotType.MINER,//10
			  RobotType.AEROSPACELAB, //11
			  RobotType.TANKFACTORY, //12
			  RobotType.TRAININGFIELD, //13
			  RobotType.HELIPAD, //14
			  RobotType.BARRACKS,//15
			  RobotType.MINERFACTORY,//16
			  RobotType.SUPPLYDEPOT,//17
			  RobotType.TECHNOLOGYINSTITUTE, //18
			  RobotType.HANDWASHSTATION, //19
			  RobotType.COMPUTER}; //20
		int typeCode = 0;
		while (list[typeCode] != type && typeCode < list.length)
			typeCode++;
		
		//these should be modified as we expand functionality
		switch(typeCode){ 
			case 3: //tank
				if (code == SWARM)
					return TANKATTACK;
				break;
			case 5: //drone
				if (code == SWARM)
					return DRONEATTACK;
				else if(code == SEARCH)
					return DRONESEARCH;
				else if(code == ATTACK_TEAM)
					return DRONE_ATTACK_COUNT;
				else if(code == HUNT_TEAM)
					return DRONE_HUNT_COUNT;
				else if(code == SUPPLY_TEAM)
					return DRONE_SUPPLY_COUNT;
				else if(code == CHECK_JOBS)
					return DRONE_JOB_CODES;
				break;
			case 8: //solider
				if (code == SWARM)
					return SOLDIERATTACK;
				break;
			case 9: //beaver
				if (code == SEARCH)
					return BEAVERSEARCH;
				break;
			case 10: //miners
				if (code == SEARCH)
					return MINERSEARCH;
				break;
			default:
				break;		
		}
		
		return new int[0];
	}
	
	/*
	 * Gets the build phase, defined below:
	 * 0: headquarters spawns 2 beavers, beaver builds mining factory (triggers phase change)
	 * 1: mining factory spawns (up to) 30 miners, beavers build helipads, which spawn (30 or equal to number of 
	 * 		enemies?) drones, when drone number is met, next phase is triggered
	 * 2: All spawning stops, other than to maintain miner and drone numbers. When resources are sufficient,
	 * 		beavers build tech institute, training field, and then commander. Commander triggers phase change
	 * 3: Beavers build barracks (should not spawn anything) and then tank factory, spawn infinite tanks
	 * 4: If commander dies, stop tank production to save up for new commander, then
	 * 		return to stage 3
	 * ***As appropriate, 2 beavers, 30 miners, 30 drones, and 1 commander are maintained, nonstop tanks
	 */
	public void initializeBuildPhase() throws GameActionException{
		rc.broadcast(BUILD_PHASE, 0);
	}
	
	public int getBuildPhase() throws GameActionException{
		return rc.readBroadcast(BUILD_PHASE);
	}
	
	public void advanceBuildPhase(int adv) throws GameActionException{
		int curPhase = rc.readBroadcast(BUILD_PHASE);
		rc.broadcast(BUILD_PHASE, (curPhase+adv));
	}
	
	public void updateCount(RobotType type, int num) throws GameActionException{
		if(type.equals(RobotType.BEAVER)){
			rc.broadcast(BEAVER_COUNT, num);
		} else if (type.equals(RobotType.MINERFACTORY)){
			rc.broadcast(MINEFACT_COUNT, num);
		} else if (type.equals(RobotType.MINER)){
			rc.broadcast(MINER_COUNT, num);
		} else if (type.equals(RobotType.HELIPAD)){
			rc.broadcast(HELIPAD_COUNT, num);
		} else if (type.equals(RobotType.DRONE)){
			rc.broadcast(DRONE_COUNT, num);
		} else if (type.equals(RobotType.TECHNOLOGYINSTITUTE)){
			rc.broadcast(MIT_COUNT, num);
		} else if (type.equals(RobotType.TRAININGFIELD)){
			rc.broadcast(TRAINF_COUNT, num);
		} else if (type.equals(RobotType.COMMANDER)){
			rc.broadcast(COMM_COUNT, num);
		} else if (type.equals(RobotType.BARRACKS)){
			rc.broadcast(BAR_COUNT, num);
		} else if (type.equals(RobotType.TANKFACTORY)){
			rc.broadcast(TANKFAC_COUNT, num);
		} else if (type.equals(RobotType.TANK)){
			rc.broadcast(TANK_COUNT, num);
		}
	}
	
	public int readCount(RobotType type) throws GameActionException{
		if(type.equals(RobotType.BEAVER)){
			return rc.readBroadcast(BEAVER_COUNT);
		} else if (type.equals(RobotType.MINERFACTORY)){
			return rc.readBroadcast(MINEFACT_COUNT);
		} else if (type.equals(RobotType.MINER)){
			return rc.readBroadcast(MINER_COUNT);
		} else if (type.equals(RobotType.HELIPAD)){
			return rc.readBroadcast(HELIPAD_COUNT);
		} else if (type.equals(RobotType.DRONE)){
			return rc.readBroadcast(DRONE_COUNT);
		} else if (type.equals(RobotType.TECHNOLOGYINSTITUTE)){
			return rc.readBroadcast(MIT_COUNT);
		} else if (type.equals(RobotType.TRAININGFIELD)){
			return rc.readBroadcast(TRAINF_COUNT);
		} else if (type.equals(RobotType.COMMANDER)){
			return rc.readBroadcast(COMM_COUNT);
		} else if (type.equals(RobotType.BARRACKS)){
			return rc.readBroadcast(BAR_COUNT);
		} else if (type.equals(RobotType.TANKFACTORY)){
			return rc.readBroadcast(TANKFAC_COUNT);
		} else if (type.equals(RobotType.TANK)){
			return rc.readBroadcast(TANK_COUNT);
		} else{
			return 0;
		}
	}
	
}
