package team397;

import battlecode.common.*;

public class TowerLogic extends RobotLogic{

	private RobotController myController;
	private int myRange;
	private Team enemyTeam;
	private Team myTeam;
	
	public TowerLogic(RobotController controller)
	{
		super();
		myController = controller;
		myRange = myController.getType().attackRadiusSquared;
		myTeam = myController.getTeam();
		enemyTeam = myController.getTeam().opponent();
	}
	
	public void run()
	{
		try {
			basicSupply(myController, myTeam);
			attack(myController, myRange, enemyTeam);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
