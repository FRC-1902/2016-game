package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.framework.ExtendableRobot;
import com.explodingbacon.bcnlib.utils.Drive;
import com.explodingbacon.robot.commands.DriveCommand;
import com.explodingbacon.robot.commands.IntakeCommand;
import com.explodingbacon.robot.commands.ShooterCommand;
import com.explodingbacon.robot.subsystems.ClimberSubsystem;
import com.explodingbacon.robot.subsystems.DriveSubsystem;
import com.explodingbacon.robot.subsystems.IntakeSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends ExtendableRobot {

    public static DriveSubsystem driveSubsystem;
    public static IntakeSubsystem intakeSubsystem;
    public static ShooterSubsystem shooterSubsystem;
    public static ClimberSubsystem climberSubsystem;
    public static SendableChooser driverChooser;

    //public VisionThread vision = new VisionThread();

    public OI oi;

    @Override
    public void robotInit() {
        driveSubsystem = new DriveSubsystem();
        intakeSubsystem = new IntakeSubsystem();
        shooterSubsystem = new ShooterSubsystem();
        climberSubsystem = new ClimberSubsystem();

        System.out.println("Subsystems initialized!");

        driverChooser = new SendableChooser();
        driverChooser.initTable(NetworkTable.getTable("Table"));
        driverChooser.addDefault("Two Drivers", "two");
        driverChooser.addObject("One Driver", "one");
        SmartDashboard.putData("Number of Drivers", driverChooser);

        oi = new OI();

        OI.runCommands(new DriveCommand(), new IntakeCommand(), new ShooterCommand());

        driveSubsystem.configureDrive();

        //vision.start();

        System.out.println("1902 Robot initialized!");
    }

    @Override
    public void teleopInit() {
        OI.init();

        super.teleopInit();
    }

    @Override
    public void testInit() {
        OI.init();

        super.testInit();
    }

    @Override
    public void disabledInit() {}
}
