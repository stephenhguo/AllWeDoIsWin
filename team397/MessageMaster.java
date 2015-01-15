package team397;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class MessageMaster {

	//these are variables for CHANNELS (1 - 65000)
	public final int[] DRONEATTACK = {0,1}; // (x,y)
	public final int[] SOLDIERATTACK = {3,4}; //(x,y)
	public final int[] TANKATTACK = {5,6}; // (x,y)
	public final int NEXTBUILDING = 2;
	
	//internal codes
	private final int ATTACK = 0;

	public RobotController rc;
	
	
	public MessageMaster(RobotController controller) {
		rc = controller;
	}
	
	public int checkNextBuild() throws GameActionException{
		return rc.readBroadcast(NEXTBUILDING);
	}
	
	public void newBuild(int buildCode) throws GameActionException{
		rc.broadcast(NEXTBUILDING, buildCode);
		
	}

	public MapLocation getAttackLoc(RobotType type) throws GameActionException{
		int[] channel = typeSwitch(type);
		int x = rc.readBroadcast(channel[0]), y = rc.readBroadcast(channel[1]);
		return new MapLocation(x,y);
	}
	
	public void setAttackLoc(MapLocation loc, RobotType type) throws GameActionException{
		int[] channel = typeSwitch(type);
		rc.broadcast(channel[0], loc.x);
		rc.broadcast(channel[1], loc.y);
	}
	
	private int[] typeSwitch(RobotType type){
		return typeSwitch(type,ATTACK);
	}
	
	private int[] typeSwitch(RobotType type, int code){
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
		
		switch(typeCode){
			case 3:
				if (code == ATTACK)
					return TANKATTACK;
				break;
			case 5:
				if (code == ATTACK)
					return DRONEATTACK;
				break;
			case 8:
				if (code == ATTACK)
					return SOLDIERATTACK;
				break;
			default:
				break;		
		}
		
		return new int[0];
	}
}
