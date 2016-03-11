package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.ClimberSubsystem;

public class ClimberCommand extends Command {

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        if (OI.deployClimber.get()) {
            ClimberSubsystem.deploy();
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
