package team397;

import java.util.Random;

import battlecode.common.*;

/*
 * This class is overridden with specific unit logic.
 */
public class RobotLogic 
{	
	public final int ATTACKXPORT = 0;
	public final int ATTACKYPORT = 1;
	public final int SOLDPORTX = 3;
	public final int SOLDPORTY = 4;
	public final int TANKPORTX = 5;
	public final int TANKPORTY = 6;
	
	
	public final int NEXTBUILD = 2;
	
	public RobotController rc;
	public MessageMaster radio;
	public Team myTeam, enemyTeam;
	public static MapLocation[] justVisited = new MapLocation[4];

	public static final int MMSIZE = 9; // must be odd
	public static boolean[][] miniMap= new boolean[MMSIZE][MMSIZE];
	public static int row_0 = 0, col_0 = 0; //these will change to indicate the 0 position of the minimap
	
	public RobotLogic(RobotController controller){
		rc = controller;
		radio = new MessageMaster(rc);
		myTeam = rc.getTeam();
		enemyTeam = myTeam.opponent();
	}
	
	
	/*
	 * This is the attack preference for all units.
	 */
	
	public RobotType[] attackPreference = {RobotType.HQ, 
			  RobotType.TOWER, 
			  RobotType.MISSILE,
			  RobotType.TANK,
			  RobotType.COMMANDER,
			  RobotType.DRONE,
			  RobotType.LAUNCHER,
			  RobotType.BASHER,
			  RobotType.SOLDIER,
			  RobotType.BEAVER,
			  RobotType.MINER,
			  RobotType.AEROSPACELAB,
			  RobotType.TANKFACTORY,
			  RobotType.TRAININGFIELD,
			  RobotType.HELIPAD,
			  RobotType.BARRACKS,
			  RobotType.MINERFACTORY,
			  RobotType.SUPPLYDEPOT,
			  RobotType.TECHNOLOGYINSTITUTE,
			  RobotType.HANDWASHSTATION,
			  RobotType.COMPUTER};
	/*
	 * This method is meant to be overridden for each specific unit logic.
	 */
	public void run() throws GameActionException{}
	
	public void attack(int myRange){
		if(!rc.isWeaponReady()){
			return;
		}
		RobotInfo[] enemies = rc.senseNearbyRobots(myRange, enemyTeam);
		if(enemies.length==0){
			return;
		}
		double minHealth = 2000.0;
		RobotInfo enemyToAttack = enemies[0];
		for(RobotInfo inf : enemies){
			if(inf.health<minHealth){
				minHealth = inf.health;
				enemyToAttack = inf;
			}
		}
		try {
			rc.attackLocation(enemyToAttack.location);
			rc.yield();
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	public void basicSupply(){
		RobotInfo[] myRobots = rc.senseNearbyRobots(GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, myTeam);
		double minSupply = 2000.0;
		if(myRobots.length==0){
			return;
		}
		RobotInfo rob = myRobots[0];
		for(RobotInfo inf : myRobots){
			if(inf.supplyLevel<minSupply){
				minSupply = inf.supplyLevel;
				rob = inf;
			}
		}
		if(minSupply<rc.getSupplyLevel()){
			try {
				rc.transferSupplies((int)((rc.getSupplyLevel()-minSupply)/2.0), rob.location);
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void buildMiniMap(){
		
		MapLocation myLoc = rc.getLocation();
		int x, y;

		for(int i = 0; i <= MMSIZE; i++){
			for(int j = 0; j <= MMSIZE; j++){
				x = myLoc.x - (int) (MMSIZE / 2) + i;
				y = myLoc.y - (int) (MMSIZE / 2) + j;
				if (rc.senseTerrainTile(new MapLocation(x,y)) == TerrainTile.VOID)
					miniMap[i][j] = false;
				else
					miniMap[i][j] = true;
			}
		}
		
		row_0 = 0;
		col_0 = 0;
		
	}
	
	public void updateMiniMap(){
		
		MapLocation last = justVisited[0], now = rc.getLocation();
		
		int movement_x = now.x - last.x, movement_y = now.y - last.y;
		row_0 = (row_0 + movement_x) % MMSIZE;
		
		if (movement_x != 0){
			int replace = (row_0 + MMSIZE - movement_x) % MMSIZE;
			int x = now.x + (int) (MMSIZE / 2) * movement_x, y, realI;
			int centerY = last.y;
			for(int i = 0; i <= MMSIZE; i++){
				y = centerY - (int) (MMSIZE / 2) + i;
				realI = (i + col_0) % MMSIZE;
				if (rc.senseTerrainTile(new MapLocation(x,y)) == TerrainTile.VOID)
					miniMap[replace][realI] = false;
				else
					miniMap[replace][realI] = true;
			}
		}
		
		col_0 = (col_0 + movement_y) % MMSIZE;
		
		if (movement_y != 0){
			int replace = (col_0 + MMSIZE - movement_y) % MMSIZE;
			int y = now.y + (int) (MMSIZE / 2) * movement_y, x, realI;
			int centerX = now.x;
			for(int i = 0; i <= MMSIZE; i++){
				x = centerX - (int) (MMSIZE / 2) + i;
				realI = (i + row_0) % MMSIZE;
				if (rc.senseTerrainTile(new MapLocation(x,y)) == TerrainTile.VOID)
					miniMap[realI][replace] = false;
				else
					miniMap[realI][replace] = true;
			}
		}
		
	}
	
	
	public void roam(Random rand){

		if(!rc.isCoreReady()){
			return;
		}
		int dir = rand.nextInt(8);
		rc.setIndicatorString(0, Double.toString(rc.getHealth()));
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
		if(rc.canMove(movedir)){
			try {
				rc.move(movedir);
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}
/*
	public void goTo(MapLocation goal, int goalRadSq){
		
		
		
	}
	
	public double[] makePF(MapLocation goal, int goalRadSq){
	
		//RobotInfo[] enemyBots = rc.senseNearbyRobots(5, myTeam.opponent());
		RobotInfo[] friendBots = rc.senseNearbyRobots(2, myTeam);
		
		int[][] near = new int[3][3];
		
		//update //update the 'near' variable for all thigns you care about
		
		
		
	}
	
	public void update(int[] spots, MapLocation other, int code) // for processing walls, goal
	{
		//finish, use switch inside for loop
	}
	
	public void update(int[] spots, RobotInfo[] other) // for processing bots
	{
		//finish, use switch inside for loop
	}
	
	private double linear(int d_sq, double weight, int r_sq){
		if (d_sq < r_sq)
			return (double) weight / r_sq * (r_sq - d_sq);
		else
			return 0.;
	}
	
	private double step(int d_sq, double weight, int r_sq){
		if (d_sq <= r_sq)
			return weight;
		else
			return 0;
	}
	
	private double decay(int d_sq, double weight, int r_sq){
		if (d_sq >= r_sq)
			return weight / (1.- (d_sq-r_sq));
		else
			return weight*(d_sq/r_sq)*(d_sq/r_sq);
			
	}
	
	private double decay(int d_sq, double weight){
		return decay(d_sq, weight, 0);
	}
	
	private double decayUpTo(int d_sq, double w_out, double w_in, int r_sq){
		if (d_sq >= r_sq)
			return w_out / (1.- (d_sq-r_sq));
		else
			return w_in;
	}
	
	private double linearUpTo(d_sq, w_out, w_in, r_sq){
		if (d_sq >= r_sq)
			return 
					
					//finish this
	}
	
	public void pot
	*/
}

