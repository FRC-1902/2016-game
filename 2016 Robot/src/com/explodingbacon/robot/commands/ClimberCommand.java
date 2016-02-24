package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.controllers.Direction;
import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.subsystems.ClimberSubsystem;

public class ClimberCommand extends Command {

    public ClimberCommand() {

    }

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        Direction d = OI.manip.getDPad();
        if (ClimberSubsystem.isTouchDown()) {
            ClimberSubsystem.getEncoder().reset();
        }

        /*
        if (d.isUp()) {
            ClimberSubsystem.setPosition(true);
        } else if (d.isDown()) {
            ClimberSubsystem.setPosition(false);
        }

        //START DEBUG CODE

        if (d.isLeft()) {
            ClimberSubsystem.setLatches(true);
        } else if (d.isRight()) {
            ClimberSubsystem.setLatches(false);
        }

        double joyY = OI.manip.getY();
        if (Math.abs(joyY )> 0.1) {
            ClimberSubsystem.climber.setPower(-joyY);
        }
        */
        //END DEBUG CODE

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
