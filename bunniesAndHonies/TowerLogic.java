package bunniesAndHonies;

import battlecode.common.*;

public class TowerLogic extends RobotLogic{

	private RobotController myController;
	private int myRange;
	private Team enemyTeam;
	
	public TowerLogic(RobotController controller)
	{
		super();
		myController = controller;
		myRange = myController.getType().attackRadiusSquared;
		enemyTeam = myController.getTeam().opponent();
	}
	
	public void run()
	{
		try {
			attack(myController, myRange, enemyTeam);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
