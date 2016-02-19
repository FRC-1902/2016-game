package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.subsystems.ClimberSubsystem;
import com.explodingbacon.robot.subsystems.IntakeSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;

public class IntakeCommand extends Command {

    Boolean currentState = false, buttonHeld = false;

    public IntakeCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        if (OI.intakeMotorIn.get() && !ShooterSubsystem.hasBall()) {
            IntakeSubsystem.setSpeed(1);
            ShooterSubsystem.shooterPID.setTarget(ShooterSubsystem.INTAKE_RATE);
            ShooterSubsystem.setIndexer(-1);
        } else if (OI.intakeMotorOut.get()) {
            IntakeSubsystem.setSpeed(-1);
            ShooterSubsystem.setIndexer(1);
            ShooterSubsystem.shooterPID.setTarget(0);
        } else if (!OI.shooterRev.get() && !OI.shoot.getAny() && !ShooterSubsystem.shouldShoot()){
            IntakeSubsystem.setSpeed(0);
            ShooterSubsystem.setIndexer(0);
            ShooterSubsystem.shooterPID.setTarget(0);
        }

        //IntakeSubsystem.setPosition(OI.intakeRetract.getAny());

        if(OI.intakeRetract.getAny() && !buttonHeld) {
            IntakeSubsystem.setPosition(!currentState);
            currentState = !currentState;
        }

        buttonHeld = OI.intakeRetract.getAny();
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
