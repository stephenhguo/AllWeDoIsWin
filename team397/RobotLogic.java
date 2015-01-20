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
	//old roam
/*
	public void roam(){

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
				justVisited = rc.getLocation();
				rc.move(movedir);
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}
*/

	//new roam
	
	public PFObject[] avoidEnemyTowersAndHQ(MapLocation except) throws GameActionException{
		MapLocation[] towers = radio.getEnemyTowerLocs();
		MapLocation enemyHQ = radio.getEnemyHQLoc();
		int len = towers.length + 1, count = 0;
		if (except != null)
			len--;
		PFObject[] result = new PFObject[len];
		result[0] = new AvoidObject(enemyHQ, 24);
		count++;
		
		for(int i = 1; i < len; i++){
			if(!towers[i-1].equals(except)){
				result[count] = new AvoidObject(towers[i-1], 24);
				count++;
			}
		}
		return result;
	}
	
	public PFObject[] avoidEnemyTowersAndHQ() throws GameActionException{
		return avoidEnemyTowersAndHQ(null);
	}
	
	public PFObject[] combine(PFObject[] a, PFObject[] b){
		int a_len = a.length, len = a.length + b.length;
		PFObject[] result = new PFObject[len];
		for(int i = 0; i < len; i++){
			if(i < a_len)
				result[i] = a[i];
			else
				result[i] = b[i - a_len];
		}
		return result;
	}
	
	public PFObject[] combine(PFObject[] a, PFObject b){
		PFObject[] result = new PFObject[a.length + 1];
		for(int i = 0; i < a.length; i++)
			result[i] = a[i];
		result[a.length] = b;
		return result;
	}
	
	
	public void roam() throws GameActionException{
		goTo();
	}
	
	public void roam(PFObject[] objects) throws GameActionException{
		makeNextMove(objects);
	}
	
	public void goTo() throws GameActionException{
		goTo(null, 0);
	}
	
	public void goTo(MapLocation goal) throws GameActionException{
		goTo(goal, 0);
	}
	
	public void goTo(MapLocation goal, int goalRadSq) throws GameActionException{
		
		if(!rc.isCoreReady()){
			return;
		}
		
		Direction newDir = nextMove(goal, goalRadSq);
		justVisited = rc.getLocation();
		if(rc.canMove(newDir)){
			rc.move(newDir);
		}
	}
	
	public Direction nextMove() throws GameActionException{
		return nextMove(null, 0);
	}
	
	public void makeNextMove(PFObject[] objects) throws GameActionException{
		if(!rc.isCoreReady()){
			return;
		}
		
		Direction newDir = nextMove(objects);
		justVisited = rc.getLocation();
		if(rc.canMove(newDir)){
			rc.move(newDir);
		}
	}
	
	public Direction nextMove(PFObject[] objects){
		
		//Pick possible options
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
		
		//Set current location to be less desirable
		//if(none != -1)	
		//	values[none] = -2.5;
		
		counter = 0;
		for(int i = 0; i < canGo.length; i++){
			if (canGo[i]){
				options[counter] = options_unedited[i];
				counter++;
			}
		}
		
		
		//Update field
		if (justVisited != null){
			update(options, values, new LinearObject(justVisited, -1, 2));
		}
		
		PFObject obj;
		for(int i = 0; i < objects.length;  i++){
			obj = objects[i];
			if (obj != null)
				update(options, values, obj);
		}
		
		
		//Pick best direction
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
		
		int possibilities = (int) Math.log10(index) + 1;
		int selection = (int)(rand.nextDouble() * possibilities);
		Direction newDir = options[(int) ((index % Math.pow(10, selection + 1)) / Math.pow(10, selection))];
		
		String str = "";
		for(int i = 0; i < values.length; i++){
			str += options[i] +": " + values[i] + ", ";
		}
		
		rc.setIndicatorString(0, str);
		
		return newDir;	
		
	}
	
	public Direction nextMove(MapLocation goal, int goalRadSq) throws GameActionException{
	
		//RobotInfo[] enemyBots = rc.senseNearbyRobots(5, myTeam.opponent());
		int byte1 = Clock.getBytecodeNum();

		//RobotInfo[] friendBots = rc.senseNearbyRobots(2, myTeam);
		MapLocation[] towers = radio.getEnemyTowerLocs();
		MapLocation myLoc = rc.getLocation(), enemyHQ = radio.getEnemyHQLoc();
		
		//codes
		int GOAL = 0, VOID = 1, TOWER = 2, JUSTVISITED = 3, ENEMYHQ = 4;
		
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
		
		if(none != -1)	
			values[none] = -2.5;
		
		counter = 0;
		for(int i = 0; i < canGo.length; i++){
			if (canGo[i]){
				options[counter] = options_unedited[i];
				counter++;
			}
		}
		
		
		for(int i = 0; i < towers.length; i++){ //update for towers
			if (!towers[i].equals(goal))
				update(options, values, towers[i], TOWER);
		}
		
		if (justVisited != null){
			update(options, values, justVisited, JUSTVISITED);
		}
		
		if(!enemyHQ.equals(goal))	
			update(options, values, enemyHQ, ENEMYHQ);
		
		if (goal != null)
				update(options, values, goal, GOAL, goalRadSq); //update for goal
		
		
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
		
		int possibilities = (int) Math.log10(index) + 1;
		int selection = (int)(rand.nextDouble() * possibilities);
		Direction newDir = options[(int) ((index % Math.pow(10, selection + 1)) / Math.pow(10, selection))];
		
		String str = "";
		for(int i = 0; i < values.length; i++){
			str += options[i] +": " + values[i] + ", ";
		}
		//rc.setIndicatorString(0, str);
		
		//System.out.print(byte2 - byte1);
		
		return newDir;	
	}
	
	public void update(Direction[] options, double[] values, PFObject obj){
		MapLocation spot;
		double val;
		for(int i = 0; i < options.length; i++){
			spot = rc.getLocation().add(options[i]);
			val = obj.calculate(spot);
			values[i] = addPotentials(values[i], val);
		}
	}
	
	public void update(Direction[] options, double[] values, MapLocation other, int code, int rad_sq) // for processing walls, goal
	{
		//finish, use switch inside for loop
		int d_sq;
		double val;
		for(int i = 0; i < options.length; i++){
			d_sq = other.distanceSquaredTo(rc.getLocation().add(options[i]));
			switch(code){
			case 0: //goal
				val = PFObject.decayUpTo(d_sq, goalStrength, -30., rad_sq); break;
			case 1: //void
				val = PFObject.step(d_sq, -30., 0); break;
			case 2: //tower
				val = PFObject.step(d_sq, -30., 24); break;
			case 3: //justvisited
				val = PFObject.linear(d_sq, -1, 2); break;
			case 4: //enemyHQ
				val = PFObject.step(d_sq, -50., 24); break;
			default:
				val = 0;
			}
			values[i] = addPotentials(values[i], val); 
			
		}
	}
	
	
	public void update(Direction[] options, double[] values, RobotInfo other) // for processing bots
	{
		int d_sq;
		double val;
		for(int i = 0; i < options.length; i++){
			d_sq = other.location.distanceSquaredTo(rc.getLocation().add(options[i]));
			if (other.team == myTeam)
				val = PFObject.step(d_sq, -30., 0);
			else
				val = 0;
			
			values[i] = addPotentials(values[i], val); 

		}
	}
	
	public void update(Direction[] options, double[] values, MapLocation other, int code){
		update(options, values, other, code, 0);
	}
	
	public double addPotentials(double a, double b){
		return Math.min(Math.max(a,b), Math.max(a+b, Math.min(a,b)));
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



