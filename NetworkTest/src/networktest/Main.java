package networktest;

import networktest.bcnlib.Server;
import networktest.quneo.QuNeo;
import networktest.quneo.QuNeoInput;

import javax.swing.*;

public class Main extends JFrame {

    public static QuNeo quneo;
    public static Client client = null;
    public static Server server = null;

    public Main() {
        setSize(100, 100);
        addKeyListener(new Listener());
        setVisible(true);
        quneo = new QuNeo();
    }

    public static void main(String[] args) {
        int result = JOptionPane.showConfirmDialog(null, "Boot as server?");
        //Log.i(result + "");
        if (result == 0) {
            quneo = new QuNeo();
            server = new Server();
            new QuNeoInput(48);
            new QuNeoInput(49);
        } else {
            new Main();
            client = new Client();
        }
    }
}
