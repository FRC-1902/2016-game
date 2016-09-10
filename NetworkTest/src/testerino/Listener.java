package testerino;

import testerino.bcnlib.Log;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class Listener implements KeyListener {

    public List<String> pressedKeys = new ArrayList<>();

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (!pressedKeys.contains("key"+e.getKeyCode())) {
            pressedKeys.add("key" + e.getKeyCode());
            Main.client.sendMessage("keyPress:" + e.getKeyCode());
            Log.i("Key pressed: " + e.getKeyChar());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove("key"+e.getKeyCode());
    }
}
