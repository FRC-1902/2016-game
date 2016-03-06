package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.controllers.Direction;
import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.ClimberSubsystem;

public class ClimberCommand extends Command {

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        Direction d = OI.manip.getDPad();
        if (d.isUp()) {
            ClimberSubsystem.upMode();
        } else if (d.isRight() || d.isLeft()) {
            ClimberSubsystem.transportMode();
        } else if (d.isDown()) {
            ClimberSubsystem.downMode();
        } else if (OI.hookMode.get()) {
            ClimberSubsystem.hookMode();
        } else if (OI.climb.get()) {
            ClimberSubsystem.climb();
        }
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
