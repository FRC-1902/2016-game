package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.framework.AbstractRobot;
import com.explodingbacon.bcnlib.framework.RobotCore;

public class WPIRobot extends AbstractRobot {

    //TODO: If this does not work, we need to make it give itself a core in robotInit() and then call core.robotInit();

    //public WPIRobot() {
        //setCore(new Robot(this));
    //}


    @Override
    public void robotInit() {
        RobotCore c = new Robot(this);
        setCore(c);
        c.robotInit();
    }

}
