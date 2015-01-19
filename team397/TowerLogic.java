package team397;

import battlecode.common.*;

public class TowerLogic extends RobotLogic{
	
	public TowerLogic(RobotController controller)
	{
		super(controller);
	}
	
	public void run()
	{
		try {
			basicSupply();
			attack(myRange);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
