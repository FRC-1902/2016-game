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

import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.framework.RobotCore;
import com.explodingbacon.bcnlib.sensors.PDP;
import com.explodingbacon.bcnlib.vision.Vision;
import com.explodingbacon.robot.commands.*;
import com.explodingbacon.robot.subsystems.*;
import com.explodingbacon.robot.vision.VisionTargeting;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends RobotCore {

    public static DriveSubsystem driveSubsystem;
    public static IntakeSubsystem intakeSubsystem;
    public static ShooterSubsystem shooterSubsystem;
    public static ClimberSubsystem climberSubsystem;
    public static PDP pdp = new PDP();
    public static SendableChooser autoChooser;
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
        //Vision.init();

        driveSubsystem = new DriveSubsystem();
        intakeSubsystem = new IntakeSubsystem();
        shooterSubsystem = new ShooterSubsystem();
        climberSubsystem = new ClimberSubsystem();

        oi = new OI();

        pdp.setLoggingTripping(false);

        /*
        autoChooser = new SendableChooser();
        autoChooser.initTable(NetworkTable.getTable("AutoTable"));
        autoChooser.addDefault("One Boulder (22 points, Neutral Zone facing defense)", AutonomousCommand.Type.ONE_BOULDER);
        autoChooser.addObject("Two Boulder Spy (30 Points, Spy Box facing High Goal)", AutonomousCommand.Type.TWO_BOULDER_SPY);
        //autoChooser.addObject("Two Boulder Neutral (30 Points, Neutral Zone facing defense)", AutonomousCommand.Type.TWO_BOULDER_NEUTRAL);
        autoChooser.addObject("Nothing (0 Points, Anywhere)", AutonomousCommand.Type.NOTHING);
        SmartDashboard.putData("Autonomous Chooser", autoChooser);
        */

        Log.i("Battering Ham initialized!");
    }

    public void initTeleopCommands() {
        OI.runCommands(new IntakeCommand(), new ShooterCommand());
        //OI.runCommand(new ClimberCommand());
        OI.runCommand(new DriveCommand());

        if (Vision.isInit()) {
            OI.runCommand(new VisionTargeting());
        }
    }

    @Override
    public void enabledInit() {
        super.enabledInit();
        OI.deleteAllTriggers();
    }

    @Override
    public void teleopInit() {
        super.teleopInit();
        initTeleopCommands();
        /*
        ShooterSubsystem.getShooter().setPower(1);
        DriveSubsystem.getLeft().setPower(1);
        DriveSubsystem.getRight().setPower(1);
        */
        ShooterSubsystem.getLight().enable();
    }

    @Override
    public void autonomousInit() {
        super.autonomousInit();

        OI.runCommand(new AutonomousCommand());
        if (Vision.isInit()) OI.runCommand(new VisionTargeting());
    }

    @Override
    public void testInit() {
        super.testInit();
    }

    @Override
    public void disabledInit() {
        super.disabledInit();
    }

    @Override
    public void enabledPeriodic() {
        super.enabledPeriodic();
    }

    @Override
    public void teleopPeriodic() {
        super.teleopPeriodic();

        //Log.d("Left Setpoint: " + DriveSubsystem.getLeft().getPower());
        //Log.d("Shooter User: " + ShooterSubsystem.getShooter().getUser() + ", Driver User: " + DriveSubsystem.getLeft().getUser());
        //Log.d("Target: " + ShooterSubsystem.shooterPID.getTarget() + ", Shooter Rate: " +
        // ShooterSubsystem.getEncoder().getRate() + ", Setpoint: " + ShooterSubsystem.shooterPID.getMotorPower());
    }

    @Override
    public void autonomousPeriodic() {
        super.autonomousPeriodic();
        /*
        Log.t("Target: (" + DriveSubsystem.eLeft.getTarget() + ", " + DriveSubsystem.eRight.getTarget() + "); " +
                "Current Value: (" + Math.round(DriveSubsystem.eLeft.getCurrentSourceValue()*1000)/1000f + ", " +
                                    Math.round(DriveSubsystem.eRight.getCurrentSourceValue()*1000)/1000f + "); " +
                "Motor Setpoint: (" + DriveSubsystem.eLeft.getMotorPower() + ", " + DriveSubsystem.eRight.getMotorPower() + ")");

        */
        //DriveSubsystem.gLeft.log();
    }

    @Override
    public void testPeriodic() {
        super.testPeriodic();
    }

    @Override
    public void disabledPeriodic() {
        super.disabledPeriodic();
    }
}
