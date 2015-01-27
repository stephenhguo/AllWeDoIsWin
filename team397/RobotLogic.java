package team397;

import java.util.Random;

import battlecode.common.*;

/*
 * This class is overridden with specific unit logic.
 */
public class RobotLogic 
{	
	public RobotController rc;
	public MessageMaster radio;
	public Team myTeam, enemyTeam;
	public MapLocation justVisited;
	public Random rand;
	public double goalStrength = 100.;
	public int myRange;

	/* add back in if we want minimap
	public static final int MMSIZE = 9; // must be odd
	public static boolean[][] miniMap= new boolean[MMSIZE][MMSIZE];
	public static int row_0 = 0, col_0 = 0; //these will change to indicate the 0 position of the minimap
	*/
	
	public RobotLogic(RobotController controller){
		rc = controller;
		radio = new MessageMaster(rc);
		myTeam = rc.getTeam();
		enemyTeam = myTeam.opponent();
		rand = new Random(rc.getID());
		myRange = rc.getType().attackRadiusSquared;
	}
	
	
	/*
	 * This is the attack preference for all units.
	 */
	
	public RobotType[] attackPreference = {RobotType.HQ, 
			RobotType.HELIPAD,
			RobotType.TANKFACTORY,
			RobotType.AEROSPACELAB,
			RobotType.MINERFACTORY,
			RobotType.BEAVER,
			RobotType.MINER,
			RobotType.COMPUTER,
			RobotType.TRAININGFIELD,
			RobotType.BARRACKS,
			RobotType.TOWER, 
			RobotType.MISSILE,
			RobotType.TANK,
			RobotType.COMMANDER,
			RobotType.DRONE,
			RobotType.LAUNCHER,
			RobotType.BASHER,
			RobotType.SOLDIER,
			RobotType.SUPPLYDEPOT,
			RobotType.TECHNOLOGYINSTITUTE,
			RobotType.HANDWASHSTATION,
			};
	
	/*
	 * This method is meant to be overridden for each specific unit logic.
	 */
	public RobotType[] huntPreference = {RobotType.HQ, 
			RobotType.MINERFACTORY,
			RobotType.BEAVER,
			RobotType.MINER,
			RobotType.HELIPAD,
			RobotType.TANKFACTORY,
			RobotType.AEROSPACELAB,
			RobotType.COMPUTER,
			RobotType.TRAININGFIELD,
			RobotType.BARRACKS,
			RobotType.SUPPLYDEPOT,
			RobotType.TECHNOLOGYINSTITUTE,
			RobotType.HANDWASHSTATION,
			};
	
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
/* Traub's supply. Not very good because they don't pass to each other.	
	public void basicSupply() throws GameActionException{ //distributes to farther away
		int d_sq_from_HQ = rc.senseHQLocation().distanceSquaredTo(rc.getLocation());
		double wantedSupp = supplyFunction(d_sq_from_HQ)*(rc.getType().supplyUpkeep + 5);
		if (rc.getType() == RobotType.HQ)
			wantedSupp = 0;
		rc.setIndicatorString(1, "Wanted supply: " + wantedSupp);
		if(rc.getSupplyLevel() < wantedSupp)
			return;
		else{
			RobotInfo[] myRobots = rc.senseNearbyRobots(GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, myTeam);
			rc.setIndicatorString(5, "Sensed robots: " + (myRobots.length));
			if (myRobots.length == 0)
				return;
			RobotInfo rob = myRobots[0];
			double maxSupply = 0, other_want, other_diff;
			
			for(RobotInfo inf : myRobots){
				if(inf.health >= 20){
					other_want = supplyFunction(rc.senseHQLocation().distanceSquaredTo(inf.location)) * (inf.type.supplyUpkeep + 5);
					other_diff = other_want - inf.supplyLevel;
					if(other_want > wantedSupp && other_diff > 0){
						other_diff = Math.min(other_diff, other_want - wantedSupp);
						if(other_diff > maxSupply){
							maxSupply = other_diff;
							rob = inf;
						}
					}
				}
			}
			
			MapLocation giveTo = rc.senseRobot(rob.ID).location;
			if(maxSupply<rc.getSupplyLevel() && rc.senseRobotAtLocation(giveTo).equals(rob) && giveTo.distanceSquaredTo(rc.getLocation()) <= GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED){
				rc.transferSupplies((int)(maxSupply), giveTo);
			}
		}
		
	}
	
	private double supplyFunction(int d_sq){
		return Math.pow(d_sq / 50., .7) + 1;
	}
*/

	public void basicSupply() throws GameActionException{
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
		MapLocation giveTo = rc.senseRobot(rob.ID).location;
		if(minSupply<rc.getSupplyLevel() && rc.senseRobotAtLocation(giveTo).equals(rob) && giveTo.distanceSquaredTo(rc.getLocation()) <= GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED){
			rc.transferSupplies((int)((rc.getSupplyLevel()-minSupply)/2.0), giveTo);

		}
	}
	
	/***********************************************
	 * Movement functions
	 ***********************************************/

	//Uses very little bytecode
	public void basicRandomWalk(){

		if(!rc.isCoreReady()){
			return;
		}
		int dir = rand.nextInt(8);
		//rc.setIndicatorString(0, Double.toString(rc.getHealth()));
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
				justVisited = rc.getLocation();
				rc.move(movedir);
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}
	
	//Uses more bytecode, but is still random walk
	public void roam() throws GameActionException{
		goTo(true, null, 0, 0, 0);
	}
	
	//basic, avoids enemy towers
	public void simpleGoal(MapLocation goal, int goalRadSq) throws GameActionException{
		goTo(true, goal, goalRadSq);
	}
	
	public void simpleGoal(MapLocation goal) throws GameActionException{
		simpleGoal(goal, 0);
	}
	
	//so we can turn off avoidEnTowers
	public void goAttack(boolean avoidEnTowers, MapLocation goal, int goalRadSq) throws GameActionException{
		goTo(avoidEnTowers, goal, goalRadSq);
	}
	
	public void moveAwayFrom(MapLocation goal) throws GameActionException{
		goTo(true, goal, 0, -1*goalStrength, 0);
	}
	
	//would be good for miners or patrol drones
	public void moveToArea(MapLocation goal, int goalRadSq) throws GameActionException{
		goTo(true, goal, goalRadSq, goalStrength, goalStrength);
	}

	
	//the most general moveMethod, everything else should call this if it uses potential
	public void goTo(boolean avoidEnTowers, MapLocation goal, int goalRadSq, double w_out, double w_in) throws GameActionException{
		
		if(!rc.isCoreReady()){
			return;
		}
		
		Direction newDir = nextMove(avoidEnTowers, goal, goalRadSq, w_out, w_in);
		if(rc.canMove(newDir)){
			justVisited = rc.getLocation();
			rc.move(newDir);
		}
	}
	
	public void goTo(boolean avoidEnTowers, MapLocation goal, int goalRadSq) throws GameActionException{
		goTo(avoidEnTowers, goal, goalRadSq, goalStrength, 0);
	}
	
	/***********************************************
	 * NextMove and its Update functions
	 ***********************************************/
	
	//Does heavy lifting of picking next spot to move
	public Direction nextMove(boolean avoidEnTowers, MapLocation goal, int goalRadSq, double w_out, double w_in) throws GameActionException{
	

		MapLocation myLoc = rc.getLocation();
		
		//codes for update function
		int VOID = 1, TOWER = 2, JUSTVISITED = 3, ENEMYHQ = 4;
		
		//This block just builds up the options and values variables, blocking out void and occupied spaces
		Direction[] options_unedited = Direction.values(), options;
		boolean[] canGo = new boolean[options_unedited.length];
		int counter = 0, none = -1;
		for(int i = 0; i < canGo.length; i++){
			if (!options_unedited[i].equals(Direction.NONE))
				canGo[i] = rc.canMove(options_unedited[i]);
			else{
				canGo[i] = true;
				none = counter;
			}
			if(canGo[i])
				counter++;
			
		}
		options = new Direction[counter];
		double[] values = new double[counter];
		
		//if(none != -1)	
		//	values[none] = -.5;
		
		counter = 0;
		for(int i = 0; i < canGo.length; i++){
			if (canGo[i]){
				options[counter] = options_unedited[i];
				counter++;
			}
		}
		
		//Avoids enemy towers if wanted
		if (avoidEnTowers){
			MapLocation[] towers = radio.getEnemyTowerLocs();
			MapLocation enemyHQ = radio.getEnemyHQLoc();
			for(int i = 0; i < towers.length; i++){ //update for towers
				if (!towers[i].equals(goal))
					update(options, values, towers[i], TOWER);
			}
			if(!enemyHQ.equals(goal))	
				update(options, values, enemyHQ, ENEMYHQ);
		}
		
		//Makes repulsive just was square
		if (justVisited != null){
			update(options, values, justVisited, JUSTVISITED);
		}
		
		//updates for goal using special goalUpdate method
		if (goal != null)
				goalUpdate(options, values, goal, goalRadSq, w_out, w_in); //update for goal
		
		//Picks directions with max potential
		int index = 0, count = 0;
		double maxVal = -1000;
		for(int i = 0; i < values.length; i++){
			if(values[i] > maxVal){
				maxVal = values[i];
				index = i;
				count++;
			}
			else if(values[i] == maxVal){
				index += i * Math.pow(10, count);
				count++;
			}
		}
		
		//randomly picks one of these max valued directions
		int possibilities = (int) Math.log10(index) + 1;
		int selection = (int)(rand.nextDouble() * possibilities);
		Direction newDir = options[(int) ((index % Math.pow(10, selection + 1)) / Math.pow(10, selection))];
		
		//sets indicator string
		String str = "";
		for(int i = 0; i < values.length; i++){
			str += options[i] +": " + values[i] + ", ";
		}

		
		return newDir;	
	}
	
	public Direction nextMove(boolean avoidEnTowers, MapLocation goal) throws GameActionException{
		return nextMove(true, goal, 0, goalStrength, 0.);
	}
	
	public Direction nextMove(boolean avoidEnTowers, MapLocation goal, double weight) throws GameActionException{
		return nextMove(true, goal, 0, weight, 0.);
	}
	
	public Direction nextMove() throws GameActionException{
		return nextMove(true, null);
	}
	
	// for processing walls, towers, enemyHQ, and justVisited
	public void update(Direction[] options, double[] values, MapLocation other, int code, int rad_sq)
	{
		int d_sq;
		double val;
		for(int i = 0; i < options.length; i++){
			d_sq = other.distanceSquaredTo(rc.getLocation().add(options[i]));
			switch(code){
			case 1: //void
				val = step(d_sq, -30., 0); break;
			case 2: //tower
				val = step(d_sq, -30., 25); break;
			case 3: //justvisited
				val = linear(d_sq, -1, 1); break;
			case 4: //enemyHQ
				val = step(d_sq, -50., 25); break;
			default:
				val = 0;
			}
			values[i] = addPotentials(values[i], val); 
			
		}
	}
	
	public void goalUpdate(Direction[] options, double[] values, MapLocation gLoc, int r_sq, double w_out, double w_in){ // for processing goal
		int d_sq;
		double val;
		for(int i = 0; i < options.length; i++){
			d_sq = gLoc.distanceSquaredTo(rc.getLocation().add(options[i]));
			val = decayUpTo(d_sq, w_out, w_in, r_sq);
			values[i] = addPotentials(values[i], val); 
		}
	}
	
	public void goalUpdate(Direction[] options, double[] values, MapLocation gLoc, int r_sq){
		goalUpdate(options, values, gLoc, r_sq, goalStrength, -1*goalStrength);
	}
	
	public void goalUpdate(Direction[] options, double[] values, MapLocation gLoc){
		goalUpdate(options, values, gLoc, 0);
	}
	
	
	public void update(Direction[] options, double[] values, RobotInfo other) // for processing bots
	{
		int d_sq;
		double val;
		for(int i = 0; i < options.length; i++){
			d_sq = other.location.distanceSquaredTo(rc.getLocation().add(options[i]));
			if (other.team == myTeam)
				val = step(d_sq, -30., 0);
			else
				val = 0;
			
			values[i] = addPotentials(values[i], val); 

		}
	}
	
	public void update(Direction[] options, double[] values, MapLocation other, int code){
		update(options, values, other, code, 0);
	}
	
	/***********************************************
	 * Potential functions
	 ***********************************************/
	
	
	public double addPotentials(double a, double b){
		return Math.min(Math.max(a,b), Math.max(a+b, Math.min(a,b)));
	}

	
	public double linear(int d_sq, double weight, int r_sq){
		if (d_sq < r_sq)
			return (double) weight / r_sq * (r_sq - d_sq);
		else
			return 0.;
	}
	
	public double step(int d_sq, double weight, int r_sq){
		if (d_sq <= r_sq)
			return weight;
		else
			return 0;
	}
	
	public double decay(int d_sq, double weight, int r_sq){
		if (d_sq >= r_sq)
			return weight / (1.+ (d_sq-r_sq));
		else
			return weight*(d_sq/r_sq)*(d_sq/r_sq);
			
	}
	
	public double decay(int d_sq, double weight){
		return decay(d_sq, weight, 0);
	}
	
	public double decayUpTo(int d_sq, double w_out, double w_in, int r_sq){
		if (d_sq > r_sq)
			return w_out / (1.+ (d_sq-r_sq));
		else
			return w_in;
	}
	
	public double linearUpTo(int d_sq, double w_out, double w_in, int boundary_sq, int r_sq){
		if (d_sq >= r_sq)
			return w_out / r_sq * (boundary_sq + r_sq - d_sq);
		else
			return w_in;
	}
	
	public double repulsiveCorner(int d_x, int d_y, double weight, int quad, int r_sq){
		if(quad == 1 && d_x > 0 && d_y > 0)
			return weight*(r_sq - d_x - d_y);
		else if(quad == 2 && d_x < 0 && d_y > 0)
			return weight*(r_sq + d_x - d_y);
		else if(quad == 3 && d_x < 0 && d_y < 0)
			return weight*(r_sq + d_x + d_y);
		else if(quad == 4 && d_x > 0 && d_y < 0)
			return weight*(r_sq - d_x + d_y);
		else
			return 0.;
	}
	
	
	//Add back in if we want minimap
	/*public void buildMiniMap(){
		
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
		
		MapLocation last = justVisited, now = rc.getLocation();
		
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
	
	public boolean inMiniMap(MapLocation loc){
		MapLocation myLoc = rc.getLocation();
		return Math.abs(myLoc.x - loc.x) <= (int) (MMSIZE / 2) && Math.abs(myLoc.y - loc.y) <= (int) (MMSIZE / 2);
	}
	
	public MapLocation locAtIndex(int i, int j){
		int dx = (MMSIZE - i + row_0) % MMSIZE;
		int dy = (MMSIZE - j + col_0) % MMSIZE;
		MapLocation myLoc = rc.getLocation();
		return new MapLocation(myLoc.x + dx, myLoc.y + dy);
	}
	*/
	
	/*
	We can add this back into nextMove if we decide we want walls to be repulsive
	MapLocation[] voidSQ = new MapLocation[(int)(MMSIZE*MMSIZE / 2 + 1)]; // get void squares
	int count = 0;
	for(int i = 0; i < MMSIZE; i++){
		for(int j = 0; j < MMSIZE; j++)
		{
			if (!miniMap[i][j]){
				voidSQ[count] = locAtIndex(i,j);
				count++;
			}
					
		}
	}
	
	for(int i = 0; i < count; i++)
		update(near, voidSQ[i], VOID); //update for void spots  
		*/
	
}



