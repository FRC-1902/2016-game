package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.controllers.FakeButton;
import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.subsystems.IntakeSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;

public class IntakeCommand extends Command {

    boolean currentState = false, wasPressed = false, testedCurrentBall = false;

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
            testedCurrentBall = false; //If this block of code runs, we're intaking a new ball
        } else if (OI.intakeMotorOut.get()) {
            IntakeSubsystem.setSpeed(-1);
            ShooterSubsystem.setIndexer(1);
            ShooterSubsystem.shooterPID.setTarget(0);
        } else if (!OI.shooterRev.get() && !OI.shoot.getAny() && !ShooterSubsystem.shouldShoot()){
            IntakeSubsystem.setSpeed(0);
            ShooterSubsystem.setIndexer(0);
            ShooterSubsystem.shooterPID.setTarget(0);
        }

        boolean pressed = OI.intakeRetract.getAny();

        if(pressed && !wasPressed) {
            currentState = !currentState;
            IntakeSubsystem.setPosition(currentState);
        }

        if (IntakeSubsystem.TEST_BALLS && !testedCurrentBall && ShooterSubsystem.hasBall()) {
            try {
                ((FakeButton) OI.testingBall).set(true);
                IntakeSubsystem.setSpeed(0);
                ShooterSubsystem.shooterPID.setTarget(0);

                Motor indexer = ShooterSubsystem.getIndexer();

                indexer.setPower(0);

                Log.d("Beginning wait");
                Thread.sleep(2000);
                Log.d("Wait over");

                indexer.setPower(.6);

                Log.d("Outtaking");

                Thread.sleep(1500);

                indexer.setPower(-.6);

                Log.d("Intaking");

                while(!ShooterSubsystem.hasBall()) {
                    Thread.sleep(25);
                }

                Log.d("We have the ball");

                double power = indexer.getWatts();
                indexer.setPower(0);

                ShooterSubsystem.setBallWatts(power);
                Log.d("End power: " + power + " watts");
                Log.d("Shot at " + (ShooterSubsystem.shooterPID.getTarget() +
                        ShooterSubsystem.shooterPID.getCurrentError()));

                    /*
                    indexer.rampUpWait(10, false, (Motor.RampUpData data) -> {
                        if (!ShooterSubsystem.hasBall()) {
                            double watts = indexer.getWatts();
                            ShooterSubsystem.setBallWatts(watts);
                            Log.d("Watts for moving ball: " + watts + ", motor speed:" + indexer.getPower() + ", current: " + indexer.getOutputCurrent() + ", battery voltage: " + Robot.getBatteryVoltage());
                            data.setCancelled(true);
                        }
                        return data;
                    });*/

                testedCurrentBall = true;
            } catch (Exception e) {
                Log.e("Ball test error! (in IntakeCommand)");
                e.printStackTrace();
            }
            ((FakeButton) OI.testingBall).set(false);
        }

        wasPressed = pressed;
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
