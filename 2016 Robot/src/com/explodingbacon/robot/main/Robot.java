/*
 *   (Currently unnamed) 2016 Robot!
 *
 *   Programmed by Ryan Shavell and Dominic Canora.
 *
 *   This project was written by team 1902 for the 2016 FIRST Robotics Competition game, "Stonghold".
 *
 *   Libraries Used:
 *
 *   -BCNLib
 *      -WPILib
 *      -OpenCV
 *      -Jedis
 */
package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.event.EventHandler;
import com.explodingbacon.bcnlib.framework.ExtendableRobot;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.vision.Vision;
import com.explodingbacon.robot.commands.AutonomousCommand;
import com.explodingbacon.robot.commands.DriveCommand;
import com.explodingbacon.robot.commands.IntakeCommand;
import com.explodingbacon.robot.commands.ShooterCommand;
import com.explodingbacon.robot.subsystems.ClimberSubsystem;
import com.explodingbacon.robot.subsystems.DriveSubsystem;
import com.explodingbacon.robot.subsystems.IntakeSubsystem;
import com.explodingbacon.robot.subsystems.ShooterSubsystem;
import com.explodingbacon.robot.vision.VisionTargeting;

public class Robot extends ExtendableRobot {

    public static DriveSubsystem driveSubsystem;
    public static IntakeSubsystem intakeSubsystem;
    public static ShooterSubsystem shooterSubsystem;
    public static ClimberSubsystem climberSubsystem;

    public VisionTargeting visionTargeting;

    public OI oi;

    @Override
    public void robotInit() {
        driveSubsystem = new DriveSubsystem();
        intakeSubsystem = new IntakeSubsystem();
        shooterSubsystem = new ShooterSubsystem();
        climberSubsystem = new ClimberSubsystem();

        Log.l("Subsystems initialized!");

        oi = new OI();

        OI.runCommands(new DriveCommand(), new IntakeCommand(), new ShooterCommand());

        Vision.init();

        visionTargeting = new VisionTargeting();
        visionTargeting.start();

        EventHandler.init(new TempEventHandler()); //TODO: Delete after we confirm the event system works

        Log.l("1902 Robot initialized!");
}

    @Override
    public void autonomousInit() {
        OI.runCommand(new AutonomousCommand());

        super.autonomousInit();
    }
}
