package edu.hsog.db;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class GUI extends JFrame {

    //Verbindung
    Connection connection;

    //Navigator für die Liste
    GadgetNavigator navigator;

    private void updateGUI (DTO currentGadget){
        if (currentGadget == null) {
            gadgetUrlTextfield.setText("Keine Gadgets verfügbar.");
            imageLabel.setIcon(null);
            return;
        }

        //URL-Feld befüllen
        gadgetUrlTextfield.setText(currentGadget.getUrl());

        //Beschreibung befüllen
        keywordTextfield.setText(currentGadget.getKeywords());

        //Beschreibung einfügen
        descriptionTextarea.setText(currentGadget.getDescription());

        //Kommentare laden
        commentsTextarea.setText(currentGadget.getComments());

        //Bewertung einfügen
        double averageRating = currentGadget.getAverageRating(); //Attribut des Objekts

        //Auf deutsches Format umstellen
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);
        //Nachkommastellen erzwingen (würden sonst abgeschnitten)
        numberFormat.setMinimumFractionDigits(1);
        numberFormat.setMaximumFractionDigits(4);
        String formattedRating = numberFormat.format(averageRating);
        System.out.println("Formattiertes Rating: " + formattedRating);

        //ratingLabel.setText(String.valueOf(currentGadget.getAverageRating()));
        ratingLabel.setText(formattedRating);

        //Bild
        imageLabel.setIcon(currentGadget.getCover());

        //Verkäufer E-Mail
        ownerLabel.setText(currentGadget.getEmail_verkaeufer());
    }


    public GUI(){

        //"exitButton" ist der FieldName bei GUI.form!
        //Programm beenden
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Successfull termination beim Drücken des Exit-Buttons
                System.out.println("Programm beenden...");
                statusLabel.setText("not connected");
                System.exit(0);
            }
        });

        //Verbindung initialisieren
        initConnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //Verbindung aus Connectionpool holen
                Globals.initConnectionPool();
                connection = Globals.getPoolConnection();
                statusLabel.setText("verbunden");
                System.out.println("Verbindung erfolgreich hergestellt");

                //Gadgets laden
                navigator = new GadgetNavigator();
                navigator.loadGadgets(GUI.this);
                System.out.println("Liste mit Gadgets geladen!");
            }
        });

        //Bild hochladen
        loadImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setName("imageJFch");
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(GUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                    Icon icon = Converter.loadIconFromFile(selectedFile.getAbsolutePath());
                    imageLabel.setIcon(icon);
                    imageLabel.setText("");
                }
            }
        });

        //Als User anmelden
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = userTextfield.getText();
                String passwd = passwortTextField.getText();
                //System.out.println(email + passwd);
                boolean loginSuccess = DBQueries.verifyLogin(email, passwd, GUI.this);

                //Login erfolgreich
                if (loginSuccess){
                    statusLabel.setText("logged in");
                    System.out.println("Login erfolgreich");
                }
                //Login nicht erfolgreich
                else {
                    statusLabel.setText("not logged in");
                }
            }
        });

        //Anzahl berechnen
        countButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int count = DBQueries.countGadgets(GUI.this);
                    countLabel.setText("Count: " + count);
                } catch (Exception exception){
                    System.err.println("Fehler beim Berechnen der Anzahl: " + exception.getMessage());
                    exception.printStackTrace();
                }
            }
        });

        //Neuen User anlegen
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = userTextfield.getText();
                String passwd = passwortTextField.getText();

                //Überprüfen auf leere Felder
                if (email.isEmpty() || passwd.isEmpty()) {

                    //Pop-up-Fenster mit Fehlermeldung
                    JOptionPane.showMessageDialog(null, "Bitte füllen Sie beide Felder aus.", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //In Datenbank anlegen
                boolean success = DBQueries.registerUser(email, passwd, GUI.this);
                if (success) {
                    JOptionPane.showMessageDialog(null, "Benutzer erfolgreich registriert.", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                    statusLabel.setText("registered");
                    System.out.println("Nutzer erfolgreich angelegt");
                } else {
                    JOptionPane.showMessageDialog(null, "Fehler bei der Registrierung. Möglicherweise existiert die E-Mail bereits.", "Fehler", JOptionPane.ERROR_MESSAGE);
                    statusLabel.setText("not registered");
                }
            }
        });

        //Beliebtestes Gadget anzeigen
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //Über Navigator bestes Gadget finden
                DTO bestGadget = navigator.getBestRatedGadget();

                //GUI befüllen
                updateGUI(bestGadget);

            }
        });

        //Alle Felder leeren
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Liste aller zu leerenden Felder
                JComponent[] components = {
                        countLabel, userTextfield, passwortTextField, ownerLabel, ratingLabel,
                        gadgetUrlTextfield, keywordTextfield, commentsTextarea, descriptionTextarea, commentTextfield, imageLabel
                };

                for (JComponent component : components) {
                    if (component instanceof JLabel) {
                        ((JLabel) component).setText("");
                        ((JLabel) component).setIcon(null);
                    } else if (component instanceof JTextField) {
                        ((JTextField) component).setText("");
                    } else if (component instanceof JTextArea) {
                        ((JTextArea) component).setText("");
                    }
                }
            }
        });

        //Letztes Gadget anzeigen
        lastButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DTO letztesGadget = navigator.getLastGadget();
                updateGUI(letztesGadget);
            }
        });

        //Gadget davor anzeigen
        previousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DTO gadgetDavor = navigator.previous();
                updateGUI(gadgetDavor);
            }
        });

        //Gadget danach anzeigen
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DTO gadgetDanach = navigator.next();
                updateGUI(gadgetDanach);
            }
        });

        //Erstes Gadget anzeigen
        firstButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Ausgangszustand
                if (statusLabel.getText().equals("logged in")){
                    DTO erstesGadget = navigator.getFirst();
                    updateGUI(erstesGadget);
                }
            }
        });

        //Gadget bearbeiten (wenn man Verkäufer ist)/ neues Gadget anlegen
        saveItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //Produkt besteht aus:
                String url = gadgetUrlTextfield.getText();
                String emailVerkaeufer = userTextfield.getText();
                String keywords = keywordTextfield.getText();
                String description = descriptionTextarea.getText();
                Icon cover = imageLabel.getIcon();

                //Daten verarbeiten
                DBQueries.createUpdateGadget(GUI.this, url, emailVerkaeufer, keywords, description, cover);

                //Produkt neu laden (mit neuem/ aktualisiertem Gadget)
                navigator.loadGadgets(GUI.this);
                DTO currentGadget = navigator.getCurrentGadget();

                //GUI aktualisieren
                updateGUI(currentGadget);
            }
        });

        //Produkt löschen (wenn man Verkäufer ist)
        deleteItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String angemeldeterUser = userTextfield.getText();
                String verkaeufer = ownerLabel.getText();

                if (angemeldeterUser.equals(verkaeufer)){
                    String url = gadgetUrlTextfield.getText();
                    DBQueries.deleteItem(GUI.this, url);

                    //Produkte neu laden (mit einem Produkt weniger jetzt)
                    navigator.loadGadgets(GUI.this);

                    //Erstes Produkt anzeigen
                    DTO currentGadget = navigator.getFirst();

                    //GUI aktualisieren
                    updateGUI(currentGadget);
                }

                else {
                    System.out.println("Löschen nicht erlaubt!");
                }
            }
        });

        //Kommentar mit Rating abgeben
        saveWritingWithComments.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //Werte rausspeichern
                int rating = likeSlider.getValue();
                String comment = commentTextfield.getText();
                String gadget = gadgetUrlTextfield.getText();
                String bewerter = userTextfield.getText();

                //Kommentar in DB ergänzen
                boolean inserted = DBQueries.addCommentRating(GUI.this, gadget, bewerter, comment, rating);

                //Pop-up-Fenster mit Fehlermeldung
                if (!inserted){
                    JOptionPane.showMessageDialog(null, "Kommentar konnte nicht gespeichert werden!", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //Produkt neu laden (mit neuen Kommentaren)
                navigator.loadGadgets(GUI.this);
                DTO currentGadget = navigator.getCurrentGadget();

                //GUI aktualisieren
                updateGUI(currentGadget);
            }
        });
    }

    public JPanel getMasterPanel() {
        return masterPanel;
    }

    //https://www.youtube.com/watch?v=m6lOWvbYeIc

    //Überpanel, in dem alles enthalten ist
    private JPanel masterPanel;
    private JButton initConnectButton;
    private JButton countButton;
    private JButton exitButton;
    private JTextField userTextfield;
    private JTextField passwortTextField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton previousButton;
    private JButton nextButton;
    private JButton firstButton;
    private JButton lastButton;
    private JButton searchButton;
    private JButton clearButton;
    private JLabel countLabel;
    private JLabel statusLabel;
    private JTextField gadgetUrlTextfield;
    private JTextField keywordTextfield;
    private JTextArea descriptionTextarea;
    private JButton loadImageButton;
    private JButton saveItemButton;
    private JButton deleteItemButton;
    private JTextField commentTextfield;
    private JButton saveWritingWithComments;
    private JSlider likeSlider;
    private JTextArea commentsTextarea;
    private JLabel imageLabel;
    private JLabel ownerLabel;
    private JLabel ratingLabel;
}