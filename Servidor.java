/*
 * To change this lipanse header, choose Lipanse Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.nio.ByteOrder;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Color;
import java.awt.Graphics;
import java.net.SocketException;
import java.nio.ByteBuffer;


/**
 *
 * @author rodrigo
 */
public class Servidor extends JFrame {

    Graphics gra;
    Panel pan;
    int puerto;

    public Servidor(int puerto) {
        setTitle("Servidor Fouerier");
        this.puerto = puerto;
        pan = new Panel();
        pan.repaint();
        add(pan);
        setLocation(0, 0);
        setSize(800, 600);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        this.addWindowListener(new EventoVentna());

    }

    /*
        Adapta la coordenda real x del plano cartesiano a los parámetros establecidos
     */
    private int coord_x(double x) {
        double real_x = x + this.getWidth() / 2;
        return (int) real_x;
    }

    /*
        Adapta la coordenda real y del plano cartesiano a los parámetros establecidos
     */
    private int coord_y(double y) {
        double real_y = -y + this.getHeight() / 2;
        return (int) (real_y);
    }

    public void chooseColor(int f) {
        switch (f) {
            case -5:
                gra.setColor(Color.decode("#0C1A00"));
                break;
            case -6:
                gra.setColor(Color.decode("#DFE500"));
                break;
        }
    }

    /*
        Pinta la señal diente de sierra
     */
    public void pintaSenal() {
        gra.setColor(Color.decode("#FC00E1"));
        gra.drawLine(coord_x(-90), coord_y(-95), coord_x(90), coord_y(95)); //d
        gra.drawLine(coord_x(90), coord_y(95), coord_x(-90 + 250), coord_y(-95));
        gra.drawLine(coord_x(-90 + 250), coord_y(-95), coord_x(90 + 250), coord_y(95));//d
        gra.drawLine(coord_x(90 + 250), coord_y(95), coord_x(-90 + 250 * 2), coord_y(-95));
        gra.drawLine(coord_x(-90 + 250 * 2), coord_y(-95), coord_x(90 + 250 * 2), coord_y(95));//d
        gra.drawLine(coord_x(-90), coord_y(-95), coord_x(90 - 250), coord_y(95));
        gra.drawLine(coord_x(-90 - 250), coord_y(-95), coord_x(90 - 250), coord_y(95)); //d   
        gra.drawLine(coord_x(-90 - 250), coord_y(-95), coord_x(90 - 250 * 2), coord_y(95));
        gra.drawLine(coord_x(-90 - 250 * 2), coord_y(-95), coord_x(90 - 250 * 2), coord_y(95));//d
    }

    /*
        Pinta los ejes del plano cartesiano
     */
    public void pintaEjes() {
        gra.setColor(Color.green);
        gra.drawLine(0, this.getHeight() / 2, this.getWidth(), this.getHeight() / 2);
        gra.drawLine(this.getWidth() / 2, 0, this.getWidth() / 2, this.getHeight());
    }

    public void Init() {
        pintaEjes();
        pintaSenal();
        gra.setColor(Color.decode("#DFE500"));
        int[] co = new int[4];
        try {
            DatagramSocket socketUDP = new DatagramSocket(this.puerto);
            byte[] buffer = new byte[32];
            while (true) {
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                socketUDP.receive(reply);
                byte[] data = reply.getData();
                ByteBuffer bu = ByteBuffer.wrap(data);
                bu.order(ByteOrder.LITTLE_ENDIAN);
                co[0] = bu.getInt();
                co[1] = bu.getInt();
                co[2] = bu.getInt();
                co[3] = bu.getInt();
                chooseColor(co[3]);
                //Pintamos rectas 
                gra.drawLine(co[0], co[1] + 150, co[2], co[3] + 150);
            }

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }

    public class EventoVentna implements WindowListener {

        @Override
        public void windowOpened(WindowEvent e) {
            Init();
        }

        @Override
        public void windowClosing(WindowEvent e) {
             //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void windowClosed(WindowEvent e) {
             //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void windowIconified(WindowEvent e) {
             //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
             //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void windowActivated(WindowEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
             Init();
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            //To change body of generated methods, choose Tools | Templates.
        }

    }

    public class Panel extends JPanel {

        public Panel() {
            this.setBackground(Color.decode("#0C1A00"));
        }

        @Override
        public void paint(Graphics g) {
            super.paintComponent(g);
            gra = getGraphics();
        }
    }

    public static void main(String[] args) {
        Servidor s = new Servidor(9005);
    }
}
