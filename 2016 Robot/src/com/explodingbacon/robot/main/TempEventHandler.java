package com.explodingbacon.robot.main;

import com.explodingbacon.bcnlib.controllers.Button;
import com.explodingbacon.bcnlib.event.AutonomousStartEvent;
import com.explodingbacon.bcnlib.event.ButtonPressEvent;
import com.explodingbacon.bcnlib.event.EventListener;
import com.explodingbacon.bcnlib.event.TeleopStartEvent;

public class TempEventHandler {

    @EventListener
    public void teleopStart(TeleopStartEvent e) {
        System.out.println("TeleopStartEvent success!");
    }

    @EventListener
    public void autonomousStart(AutonomousStartEvent e) {
        System.out.println("AutonomousStartEvent success!");
    }

    @EventListener
    public void buttonPressed(ButtonPressEvent e) {
        Button b = e.getButton();
        System.out.println("ButtonPressEvent success! " + b.get());
    }
}
