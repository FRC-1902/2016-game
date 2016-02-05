package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.framework.ExtendableRobot;
import com.explodingbacon.robot.commands.DriveCommand;
import com.explodingbacon.robot.commands.IntakeCommand;
import com.explodingbacon.robot.commands.ShooterCommand;
import com.explodingbacon.robot.subsystems.ClimberSubsystem;
import com.explodingbacon.robot.subsystems.DriveSubsystem;
import com.explodingbacon.robot.subsystems.IntakeSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;

public class Robot extends ExtendableRobot {

    public static DriveSubsystem driveSubsystem;
    public static IntakeSubsystem intakeSubsystem;
    public static ShooterSubsystem shooterSubsystem;
    public static ClimberSubsystem climberSubsystem;

    //public VisionThread vision = new VisionThread();

    public OI oi;

    @Override
    public void robotInit() {
        driveSubsystem = new DriveSubsystem();
        intakeSubsystem = new IntakeSubsystem();
        shooterSubsystem = new ShooterSubsystem();
        climberSubsystem = new ClimberSubsystem();

        System.out.println("Subsystems initialized!");

        oi = new OI();

        OI.runCommands(new DriveCommand(), new IntakeCommand(), new ShooterCommand());

        //vision.start();

        System.out.println("1902 Kitbot initialized!");
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
