package com.explodingbacon.robot.commands;

import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.actuators.MotorGroup;
import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.sensors.AbstractEncoder;
import com.explodingbacon.bcnlib.utils.Utils;
import com.explodingbacon.robot.subsystems.ClimberSubsystem;
import com.explodingbacon.robot.subsystems.DriveSubsystem;
import com.explodingbacon.robot.subsystems.IntakeSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;
import java.util.ArrayList;
import java.util.List;

public class ShakedownCommand extends Command {

    public String report = "Shakedown report:";

    @Override
    public void onInit() {
        try {
            shakedownGroup(DriveSubsystem.getLeftMotors(), DriveSubsystem.getLeftEncoder(), 0.5, 1); //Left drive
            shakedownGroup(DriveSubsystem.getRightMotors(), DriveSubsystem.getRightEncoder(), 0.5, 1); //Right drive
            shakedownGroup(ClimberSubsystem.getClimberMotors(), ClimberSubsystem.getEncoder(), 0.3, 0.5); //Climber
            shakedownGroup((MotorGroup)new MotorGroup(IntakeSubsystem.getIntakeMotor()).setName("Intake"), null, 0.5, 1); //Intake
            shakedownGroup(ShooterSubsystem.getShooterMotors(), ShooterSubsystem.getEncoder(), 0.5, 1); //Shooter
            shakedownGroup((MotorGroup)new MotorGroup(ShooterSubsystem.getIndexerMotor()).setName("Indexer"), null, 0.5, 1); //Indexer
            Log.i(report);
        } catch (Exception e) {
            Log.e("ShakedownCommand error!");
            e.printStackTrace();
        }
    }

    public void shakedownGroup(MotorGroup group, AbstractEncoder e, double speed, double seconds) {
        try {
            if (e != null) {
                boolean encWorks = false;

                List<Motor> problems = new ArrayList<>();

                for (Motor m : group.getMotors()) {
                    int pos = e.get();
                    m.setPower(speed);
                    Thread.sleep(Math.round(seconds * 1000));
                    m.setPower(0);
                    if (Math.abs(e.get() - pos) > 5) { //If the encoder detected a change when the motor was told to move
                        encWorks = true;
                    } else {
                        problems.add(m);
                    }
                    Thread.sleep(1000);
                }

                for (Motor m : problems) {
                    if (encWorks) {
                        report(m.getName() + " (part of \"" + group.getName() + "\") did not register movement.");
                    }
                }
                if (!encWorks) {
                    report("Either all of \"" + group.getName() + "\" does not work, or it's encoder is not functioning.");
                }
            } else {
                group.testEach(speed, seconds);
                report("\"" + group.getName() + "\" does not have an encoder, so you'll have to watch to see if it's Motor(s) work.");
            }
        } catch (Exception ex) {
            Log.e("ShakedownCommand.shakedownGroup() error!");
            ex.printStackTrace();
        }
    }

    public void report(String s) {
        report = report + Utils.newLine() + s;
    }

    @Override
    public void onLoop() {}

    @Override
    public void onStop() {}

    @Override
    public boolean isFinished() {
        return true;
    }
}
