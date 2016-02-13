package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.subsystems.ClimberSubsystem;
import com.explodingbacon.robot.subsystems.IntakeSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;

public class IntakeCommand extends Command {

    public IntakeCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        if (OI.intakeMotorIn.get() && !ShooterSubsystem.hasBall()) {
            IntakeSubsystem.setSpeed(1);
            ShooterSubsystem.setShooter(-.8);
            ShooterSubsystem.setIndexer(-1);
        } else if (OI.intakeMotorOut.get()) {
            IntakeSubsystem.setSpeed(-1);
        } else {
            IntakeSubsystem.setSpeed(0);
        }
        if (OI.manip.start.get()) {
            ClimberSubsystem.climber.setPower(1);
        } else {
            ClimberSubsystem.climber.setPower(0);
        }
        IntakeSubsystem.setPosition(!OI.intakeRetract.getAny());
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
