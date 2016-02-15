package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.controllers.Button;
import com.explodingbacon.bcnlib.event.AutonomousStartEvent;
import com.explodingbacon.bcnlib.event.ButtonPressEvent;
import com.explodingbacon.bcnlib.event.EventListener;
import com.explodingbacon.bcnlib.event.TeleopStartEvent;
import com.explodingbacon.bcnlib.framework.Log;

public class TempEventHandler {

    @EventListener
    public void teleopStart(TeleopStartEvent e) {
        Log.d("TeleopStartEvent success!");
    }

    @EventListener
    public void autonomousStart(AutonomousStartEvent e) {
        Log.d("AutonomousStartEvent success!");
    }

    @EventListener
    public void buttonPressed(ButtonPressEvent e) {
        Button b = e.getButton();
        Log.d("ButtonPressEvent success! " + b.get());
    }
}
