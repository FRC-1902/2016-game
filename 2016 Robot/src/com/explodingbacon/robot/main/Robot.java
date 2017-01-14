package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.framework.Command;
import com.explodingbacon.bcnlib.framework.Log;
import com.explodingbacon.bcnlib.framework.RobotCore;
import edu.wpi.first.wpilibj.IterativeRobot;
import java.util.ArrayList;
import java.util.List;

public class Robot extends RobotCore {

    //public static DataLogger logger = new DataLogger();

    //private static Solenoid kitLight;

    private Shooter shooter;

    public OI oi;

    public Robot(IterativeRobot r) {
        super(r);
    }

    @Override
    public void robotInit() {
        super.robotInit();

        shooter = new Shooter();
        //Vision.init();

        oi = new OI();

        Log.i("Battering Ham initialized!");
    }

    public void initTeleopCommands() {
        List<Command> commands = new ArrayList<>();
        commands.add(new ShooterCommand());
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
    }

    @Override
    public void autonomousInit() {
        super.autonomousInit();
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
    }

    @Override
    public void autonomousPeriodic() {
        super.autonomousPeriodic();
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
