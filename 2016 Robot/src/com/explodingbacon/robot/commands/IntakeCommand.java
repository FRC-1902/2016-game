package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.robot.main.OI;
import com.explodingbacon.robot.subsystems.Intake;
import com.explodingbacon.robot.subsystems.Shooter;

import java.util.List;

public class IntakeCommand extends Command {

    boolean currentState = true, wasPressed = false;

    List<Double> powerList;
    private final int AVERAGER_SIZE = 10; //The number of records to average

    @Override
    public void onInit() {}

    @Override
    public void onLoop() {
        if (OI.intakeMotorIn.get() && !Shooter.hasBall()) {
            Intake.intake(this);
        } else if (OI.intakeMotorOut.get()) {
            Intake.outtake(this);
        } else {
            Intake.stopIntake(this);
        }

        boolean pressed = OI.intakeRetract.getAny();

        if(pressed && !wasPressed) {
            currentState = !currentState;
            Intake.setPosition(currentState);
        }

        /*
        if (Intake.TEST_BALLS && !testedCurrentBall && Shooter.hasBall()) {
            try {
                ((FakeButton) OI.testingBall).set(true);
                Intake.stopIntake(this);
                Intake.getIntake().setUser(this);

                Motor indexer = Shooter.getIndexer();

                indexer.setPower(0);

                powerList = new ArrayList<>();

                Thread.sleep(2000); //Wait for things to calm down

                indexer.setPower(.6); //Outtake

                Thread.sleep(750); //Outtake time

                indexer.setPower(-.6); //Intake

                while(!Shooter.hasBall()) { //Log intake powers until we have the ball
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

                Shooter.setBallWatts(avgPower); //Pass the average to Shooter
                Log.d("Average power: " + avgPower + " watts");

                testedCurrentBall = true;
            } catch (Exception e) {
                Log.e("Ball test error! (in IntakeCommand)");
                e.printStackTrace();
            }
            ((FakeButton) OI.testingBall).set(false);
            Intake.getIntake().setUser(null);
        }*/

        wasPressed = pressed;
    }

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
