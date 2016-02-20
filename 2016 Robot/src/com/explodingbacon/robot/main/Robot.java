/*
  ____        _   _            _               _    _
 |  _ \      | | | |          (_)             | |  | |
 | |_) | __ _| |_| |_ ___ _ __ _ _ __   __ _  | |__| | __ _ _ __ ___
 |  _ < / _` | __| __/ _ \ '__| | '_ \ / _` | |  __  |/ _` | '_ ` _ \
 | |_) | (_| | |_| ||  __/ |  | | | | | (_| | | |  | | (_| | | | | | |
 |____/ \__,_|\__|\__\___|_|  |_|_| |_|\__, | |_|  |_|\__,_|_| |_| |_|
                                        __/ |
                                       |___/

    Programmed by Ryan Shavell and Dominic Canora.

    This project was written by team 1902 for the 2016 FIRST Robotics Competition game, "Stronghold".

    Libraries Used:

    -BCNLib
       -WPILib
       -OpenCV
       -Jedis
 */
package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.framework.DataLogger;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.framework.RobotCore;
import com.explodingbacon.bcnlib.sensors.PDP;
import com.explodingbacon.bcnlib.vision.Vision;
import com.explodingbacon.robot.commands.*;
import com.explodingbacon.robot.subsystems.ClimberSubsystem;
import com.explodingbacon.robot.subsystems.DriveSubsystem;
import com.explodingbacon.robot.subsystems.IntakeSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;
import com.explodingbacon.robot.vision.VisionTargeting;
import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends RobotCore {

    public static DriveSubsystem driveSubsystem;
    public static IntakeSubsystem intakeSubsystem;
    public static ShooterSubsystem shooterSubsystem;
    public static ClimberSubsystem climberSubsystem;
    //public static PDP pdp = new PDP();
    //public static DataLogger logger = new DataLogger();
    public static boolean logging = false;

    public VisionTargeting visionTargeting;

    public OI oi;

    public Robot() {
        super();
    }

    public Robot(IterativeRobot r) {
        super(r);
    }

    @Override
    public void robotInit() {
        super.robotInit();
        //Javascript.init();
        //Vision.init();

        driveSubsystem = new DriveSubsystem();
        intakeSubsystem = new IntakeSubsystem();
        shooterSubsystem = new ShooterSubsystem();
        climberSubsystem = new ClimberSubsystem();

        oi = new OI();

        /*
        if (Vision.isInit()) {
            visionTargeting = new VisionTargeting();
            visionTargeting.start();
        }
        */

        //EventHandler.init(new TempEventHandler()); //TODO: Delete after we confirm the event system works

        Log.i("Battering Ham initialized!");
    }

    public void initControlCommands() {
        OI.runCommands(new DriveCommand(), new IntakeCommand(), new ShooterCommand(), new ClimberCommand());
    }

    @Override
    public void autonomousInit() {
        OI.deleteAllTriggers();
        OI.runCommand(new AutonomousCommand());
        super.autonomousInit();
    }

    @Override
    public void teleopInit() {
        super.teleopInit();
        OI.deleteAllTriggers();
        initControlCommands();

        DriveSubsystem.getADX().reset();
    }

    @Override
    public void testInit() {
        OI.deleteAllTriggers();
        //OI.runCommand(new ShakedownCommand());
    }

    @Override
    public void teleopPeriodic() {
        super.teleopPeriodic();

        //Log.d("Left enc: " + DriveSubsystem.getLeftEncoder().get() + ", Right enc: " + DriveSubsystem.getRightEncoder().get());

        //Log.d("Angle: " + DriveSubsystem.getADX().getAngle());

        //ShooterSubsystem.setIndexer(0.5);
        //Log.d("Indexer current: " + ShooterSubsystem.getIndexer().getOutputCurrent());

        //Log.d("Target: " + ShooterSubsystem.shooterPID.getTarget() + ", Shooter Rate: " +
        // ShooterSubsystem.getEncoder().getRate() + ", Setpoint: " + ShooterSubsystem.shooterPID.getMotorPower());
    }

    @Override
    public void disabledInit() {
        super.disabledInit();
        ShooterSubsystem.shooterPID.setTarget(0);
    }
}
