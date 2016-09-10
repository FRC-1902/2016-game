package testerino;

import testerino.bcnlib.Log;
import javax.swing.*;

public class Main extends JFrame {

    public static Client client = null;

    public Main() {
        setSize(100, 100);
        addKeyListener(new Listener());
        setVisible(true);
    }

    public static void main(String[] args) {
        int result = JOptionPane.showConfirmDialog(null, "Boot as server?");
        Log.i(result + "");
        new Main();
    }
}
