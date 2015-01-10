package bunniesAndHonies;

import battlecode.common.*;

public class TowerLogic extends RobotLogic{

	private RobotController myController;
	
	public TowerLogic(RobotController controller)
	{
		super();
		myController = controller;
	}
	
	public void run()
	{
		try {
			//attack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
