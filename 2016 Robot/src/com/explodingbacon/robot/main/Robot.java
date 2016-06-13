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

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.framework.RobotCore;
import com.explodingbacon.bcnlib.vision.Vision;
import com.explodingbacon.robot.commands.*;
import com.explodingbacon.robot.subsystems.*;
import com.explodingbacon.robot.vision.VisionTargeting;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;
import java.util.List;

public class Robot extends RobotCore {

    public static Drive drive = null;
    public static Intake intake = null;
    public static Shooter shooter = null;
    public static Climber climber = null;
    public static SendableChooser autoChooser;
    public static SendableChooser posChooser;
    public static SendableChooser defenseChooser;
    //public static DataLogger logger = new DataLogger();

    public static final boolean real = false; //False = kitbot

    //private static Solenoid kitLight;

    public OI oi;

    public Robot(IterativeRobot r) {
        super(r);
    }

    @Override
    public void robotInit() {
        super.robotInit();

        Vision.init();

        drive = new Drive();
        //intake = new Intake();
        shooter = new Shooter();
        climber = new Climber();

        oi = new OI();

        autoChooser = new SendableChooser();
        autoChooser.initTable(NetworkTable.getTable("TableThing"));
        autoChooser.addDefault("Cross (10 points, Neutral Zone facing defense)", AutonomousCommand.Type.CROSS);
        autoChooser.addDefault("Cross w/ High Goal (20 points, Neutral Zone facing defense)", AutonomousCommand.Type.ONE_BOULDER_NEUTRAL);
        //autoChooser.addDefault("Spy High Goal (10 points, Spy Box facing High Goal)", AutonomousCommand.Type.ONE_BOULDER_SPY_NOCROSS);
        //autoChooser.addDefault("Spy High Goal w/ Cross (20 points, Spy Box facing High Goal)", AutonomousCommand.Type.ONE_BOULDER_SPY);
        //autoChooser.addObject("Two Boulder Spy (30 Points, Spy Box facing High Goal)", AutonomousCommand.Type.TWO_BOULDER_SPY);
        //autoChooser.addObject("Two Boulder Neutral (30 Points, Neutral Zone facing defense)", AutonomousCommand.Type.TWO_BOULDER_NEUTRAL);
        autoChooser.addObject("Nothing (0 Points, Anywhere)", AutonomousCommand.Type.NOTHING);
        SmartDashboard.putData("Autonomous Chooser", autoChooser);

        posChooser = new SendableChooser();
        posChooser.initTable(NetworkTable.getTable("TableThing"));
        //TODO: tune these angles
        posChooser.addDefault("Right", 1);
        posChooser.addObject("Left", -1);
        SmartDashboard.putData("Castle Turn Direction", posChooser);

        defenseChooser = new SendableChooser();
        defenseChooser.initTable(NetworkTable.getTable("TableThing"));
        defenseChooser.addDefault("Normal Static", AutonomousCommand.Defense.NORMAL);
        defenseChooser.addObject("Rock Wall", AutonomousCommand.Defense.ROCK_WALL);
        //defenseChooser.addObject("Cheval De Frise", AutonomousCommand.Defense.CHEVAL);
        //defensheCooser.addObject("Portcullis", AutonomousCommand.Defense.PORTCULLIS);
        SmartDashboard.putData("Defense Type Chooser", defenseChooser);

        SmartDashboard.putNumber("Auto Delay", 3);

        Log.i("Battering Ham initialized!");
    }

    public void initTeleopCommands() {
        List<Command> commands = new ArrayList<>();
        if (drive != null) commands.add(new DriveCommand());
        if (intake != null) commands.add(new IntakeCommand());
        if (shooter != null) commands.add(new ShooterCommand());
        //if (climber != null) commands.add(new ClimberCommand());
        if (Vision.isInit()) commands.add(new VisionTargeting());
        OI.runCommands(commands.toArray(new Command[commands.size()]));
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
        Drive.setDriverControlled(true);
        //Drive.tankDrive(0.25, 0);

        //SmartDashboard.putNumber("Turn Amnt", 90);
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

        //Log.d("Target: " + Shooter.shooterPID.getTarget() + ", Shooter Rate: " +
        // Shooter.getEncoder().getRate() + ", Setpoint: " + Shooter.shooterPID.getMotorPower());

        /*
        if(OI.manip.a.get()) {
            Drive.gLeft.reTune(SmartDashboard.getNumber("Gyro kP"), SmartDashboard.getNumber("Gyro kI"),
                    SmartDashboard.getNumber("Gyro kD"));
            Drive.gRight.reTune(SmartDashboard.getNumber("Gyro kP"), SmartDashboard.getNumber("Gyro kI"),
                    SmartDashboard.getNumber("Gyro kD"));

            while(OI.manip.a.get());
        }

        if(OI.manip.b.get()) {
            Drive.gyroTurn(SmartDashboard.getNumber("Turn Amnt"), 10);
        }
        */
    }

    @Override
    public void autonomousPeriodic() {
        super.autonomousPeriodic();
        /*
        Log.t("Target: (" + Drive.eLeft.getTarget() + ", " + Drive.eRight.getTarget() + "); " +
                "Current Value: (" + Math.round(Drive.eLeft.getCurrentSourceValue()*1000)/1000f + ", " +
                                    Math.round(Drive.eRight.getCurrentSourceValue()*1000)/1000f + "); " +
                "Motor Setpoint: (" + Drive.eLeft.getMotorPower() + ", " + Drive.eRight.getMotorPower() + ")");

        */
        //Drive.gLeft.log();
    }

    @Override
    public void testPeriodic() {
        super.testPeriodic();
    }

    @Override
    public void disabledPeriodic() {
        super.disabledPeriodic();
        /*
        double sdExposure = SmartDashboard.getNumber("Camera Exposure");
        if (VisionTargeting.currentExposure != sdExposure)) {
            VisionTargeting.camera.setExposure(sdExposure);
            VisionTargeting.currentExposure = sdExposure;
            Log.i("Camera exposure updated!");
        }
        */
    }
}
