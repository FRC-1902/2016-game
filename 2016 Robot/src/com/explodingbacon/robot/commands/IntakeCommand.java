package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.actuators.DoubleSolenoid;
import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.controllers.FakeButton;
import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.main.Robot;
import com.explodingbacon.robot.subsystems.IntakeSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;

import java.util.ArrayList;
import java.util.List;

public class IntakeCommand extends Command {

    boolean currentState = false, wasPressed = false, testedCurrentBall = false;

    List<Double> powerList;
    private final int AVERAGER_SIZE = 10; //The number of records to average

    public IntakeCommand() {
        requires(Robot.intakeSubsystem);
    }

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        if (OI.intakeMotorIn.get() && !ShooterSubsystem.hasBall()) {
            IntakeSubsystem.intake();
            testedCurrentBall = false; //If this block of code runs, we're intaking a new ball
        } else if (OI.intakeMotorOut.get()) {
            IntakeSubsystem.outtake();
        } else if (!OI.shooterRev.get() && !OI.shoot.getAny() && !ShooterSubsystem.shouldVisionShoot()){
            IntakeSubsystem.stopIntake();
        }

        boolean pressed = OI.intakeRetract.getAny();

        if(pressed && !wasPressed) {
            currentState = !currentState;
            IntakeSubsystem.setPosition(currentState);
        }

        if (IntakeSubsystem.TEST_BALLS && !testedCurrentBall && ShooterSubsystem.hasBall()) {
            try {
                ((FakeButton) OI.testingBall).set(true);
                IntakeSubsystem.stopIntake();

                Motor indexer = ShooterSubsystem.getIndexer();

                indexer.setPower(0);

                powerList = new ArrayList<>();

                Thread.sleep(2000); //Wait for things to calm down

                indexer.setPower(.6); //Outtake

                Thread.sleep(750); //Outtake time

                indexer.setPower(-.6); //Intake

                while(!ShooterSubsystem.hasBall()) { //Log intake powers until we have the ball
                    powerList.add(indexer.getWatts());
                    Thread.sleep(25);
                }

                Log.d("Indexer unaveraged: " + indexer.getWatts());

                powerList.add(indexer.getWatts());
                indexer.setPower(0);

                double total = 0; //Pull out the last n values
                int counter = 0;
                for(Double element : powerList.subList(powerList.size() - AVERAGER_SIZE, powerList.size())) {
                    total += element;
                    counter ++;
                }

                double avgPower = total / counter; //Average them

                ShooterSubsystem.setBallWatts(avgPower); //Pass the average to ShooterSubsystem
                Log.d("Average power: " + avgPower + " watts");

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
