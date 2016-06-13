package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.controllers.Direction;
import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.Climber;

public class ClimberCommand extends Command {

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        Direction d = OI.manip.getDPad();
        if (d.isUp()) {
            Climber.setPosition(true);
        } else if (d.isDown()) {
            Climber.setPosition(false);
        }
        if (OI.climberShoot.get()) {
            Climber.shoot();
        }
        if (OI.climb.get()) {
            Climber.setSpeed(1);
        } else {
            Climber.setSpeed(0);
        }
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
