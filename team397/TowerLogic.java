package team397;

import battlecode.common.*;

public class TowerLogic extends RobotLogic{

	private int myRange;
	
	public TowerLogic(RobotController controller)
	{
		super(controller);
		myRange = rc.getType().attackRadiusSquared;
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
