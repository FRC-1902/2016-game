package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.controllers.Direction;
import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.subsystems.ClimberSubsystem;

public class ClimberCommand extends Command {

    public ClimberCommand() {
        requires(Robot.climberSubsystem);
    }

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        Direction d = OI.manip.getDPad();
        if (d.isUp()) {
            ClimberSubsystem.setPosition(true);
        } else if (d.isDown()) {
            ClimberSubsystem.setPosition(false);
        }

        if (OI.climberDeploy.get()) {
            ClimberSubsystem.deploy();
        } else if (OI.climberRetract.get()) {
            ClimberSubsystem.retract();
        }
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
