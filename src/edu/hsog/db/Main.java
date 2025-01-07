package edu.hsog.db;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {

        //Fenster anlegen
        JFrame frame = generateJFrame();
        frame.setVisible(true);
    }

    public static JFrame generateJFrame(){
        //Neues Fenster-Objekt erzeugen
        JFrame jframe = new JFrame();

        //Auf "x" klicken beendet Programm
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Größe festlegen
        jframe.setSize(800, 600);

        //GUI anlegen und Masterpanel "drankleben"
        GUI gui = new GUI();
        jframe.getContentPane().add(gui.getMasterPanel());
        gui.getMasterPanel().setPreferredSize(new Dimension(800, 600));
        return jframe;
    }
}