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

import com.explodingbacon.bcnlib.actuators.Motor;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.framework.RobotCore;
import com.explodingbacon.bcnlib.sensors.PDP;
import com.explodingbacon.bcnlib.vision.Camera;
import com.explodingbacon.bcnlib.vision.Vision;
import com.explodingbacon.robot.commands.*;
import com.explodingbacon.robot.subsystems.ClimberSubsystem;
import com.explodingbacon.robot.subsystems.DriveSubsystem;
import com.explodingbacon.robot.subsystems.IntakeSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;
import com.explodingbacon.robot.vision.VisionTargeting;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.USBCamera;

public class Robot extends RobotCore {

    public static DriveSubsystem driveSubsystem;
    public static IntakeSubsystem intakeSubsystem;
    public static ShooterSubsystem shooterSubsystem;
    public static ClimberSubsystem climberSubsystem;
    public static PDP pdp = new PDP();
    //public static DataLogger logger = new DataLogger();

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
        Vision.init();

        /*
        CameraServer camera = CameraServer.getInstance();
        camera.setQuality(50);
        camera.startAutomaticCapture(new USBCamera("cam0"));
        //camera.startAutomaticCapture("cam0");*/

        driveSubsystem = new DriveSubsystem();
        intakeSubsystem = new IntakeSubsystem();
        shooterSubsystem = new ShooterSubsystem();
        climberSubsystem = new ClimberSubsystem();

        oi = new OI();

        pdp.setLoggingTripping(false);


        //EventHandler.init(new TempEventHandler()); //TODO: Delete after we confirm the event system works

        Log.i("Battering Ham initialized!");
    }

    public void init() {
        ShooterSubsystem.shooterPID.enable();
        DriveSubsystem.shift(false);
    }

    public void initControlCommands() {
        OI.runCommands(new DriveCommand(), new IntakeCommand(), new ShooterCommand(), new ClimberCommand());
        if (Vision.isInit()) {
            OI.runCommand(new VisionTargeting());
        }
    }

    @Override
    public void teleopInit() {
        super.teleopInit();
        OI.deleteAllTriggers();
        initControlCommands();

        init();

        ShooterSubsystem.getLight().enable();
        ClimberSubsystem.setLatches(true);
    }

    @Override
    public void autonomousInit() {
        OI.deleteAllTriggers();

        init();

        IntakeSubsystem.setPosition(false);

        OI.runCommand(new AutonomousCommand());
        //if (Vision.isInit()) OI.runCommand(new VisionTargeting());
        super.autonomousInit();
    }

    @Override
    public void autonomousPeriodic() {

        Log.t("Target: (" + DriveSubsystem.eLeft.getTarget() + ", " + DriveSubsystem.eRight.getTarget() + "); " +
                "Current Value: (" + Math.round(DriveSubsystem.eLeft.getCurrentSourceValue()*1000)/1000f + ", " +
                                    Math.round(DriveSubsystem.eRight.getCurrentSourceValue()*1000)/1000f + "); " +
                "Motor Setpoint: (" + DriveSubsystem.eLeft.getMotorPower() + ", " + DriveSubsystem.eRight.getMotorPower() + ")");


        //DriveSubsystem.gLeft.log();
    }

    @Override
    public void testInit() {
        OI.deleteAllTriggers();

        for (Motor m : DriveSubsystem.getLeft().getMotors()) {
            try {
                m.setPower(1);
                Thread.sleep(1000);
                m.setPower(0);
                Thread.sleep(1000);
            } catch (Exception e) {}
        }

        for (Motor m : DriveSubsystem.getRight().getMotors()) {
            try {
                m.setPower(1);
                Thread.sleep(1000);
                m.setPower(0);
                Thread.sleep(1000);
            } catch (Exception e) {}
        }
        //ClimberSubsystem.climber.testEachWait(0.3, 1);

        //OI.runCommand(new ShakedownCommand());
    }

    @Override
    public void testPeriodic() {
    }

    @Override
    public void teleopPeriodic() {
        super.teleopPeriodic();

        Log.d("Drive Encoders: (" + DriveSubsystem.getLeftEncoder().get() + ", " + DriveSubsystem.getRightEncoder().get() + ")");

        //Log.d("Climber position: " + ClimberSubsystem.getEncoder().get() + ", current: " + pdp.getCurrent(2) + " & " + pdp.getCurrent(1));

        //Log.d("Climber position: " + ClimberSubsystem.getEncoder().get() + ", touch down: " + ClimberSubsystem.isTouchDown());

        //Log.d("Target: " + ShooterSubsystem.shooterPID.getTarget() + ", Shooter Rate: " +
        // ShooterSubsystem.getEncoder().getRate() + ", Setpoint: " + ShooterSubsystem.shooterPID.getMotorPower());
    }

    @Override
    public void disabledInit() {
        //Log.i("Thread Count: " + Utils.getThreadCount());
        super.disabledInit();
        ShooterSubsystem.shooterPID.setTarget(0);
    }
}
