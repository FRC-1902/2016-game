package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.controllers.Direction;
import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.Mantis;

public class MantisCommand extends Command {

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        Direction d = OI.manip.getDPad();
        if (d.isUp()) {
            Mantis.upMode();
        } else if (d.isRight() || d.isLeft()) {
            Mantis.transportMode();
        } else if (d.isDown()) {
            Mantis.downMode();
        }
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
