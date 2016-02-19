package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.framework.AbstractRobot;
import com.explodingbacon.bcnlib.framework.RobotCore;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

public class WPIRobot extends AbstractRobot {

    @Override
    public void robotInit() {
        RobotCore c = new Robot(this);
        setCore(c);
        c.robotInit();
    }
}
