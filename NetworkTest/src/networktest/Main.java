package networktest;

import networktest.bcnlib.Server;
import networktest.quneo.Pad;
import networktest.quneo.QuNeo;
import networktest.quneo.QuNeoColor;

import javax.swing.*;

public class Main extends JFrame {

    public static QuNeo quneo;
    public static Client client = null;
    public static Server server = null;

    public Main() {  //48:  112, 113, 114, 115 : BOTTOM : 96, 97, 98, 99
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


            /*
            Pad p = new Pad(48);
            p.onNotePress(() -> p.setGridColor(QuNeoColor.ORANGE, QuNeoColor.RED, QuNeoColor.RED, QuNeoColor.ORANGE,
                    127, 127, 127, 17));
            p.onNoteRelease(() -> p.setColor(QuNeoColor.NONE, 0));
            //new QuNeoInput(49);
            */
        } else {
            new Main();
            client = new Client();
        }
    }
}
