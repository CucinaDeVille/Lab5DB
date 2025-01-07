package edu.hsog.db;

import javax.swing.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DBQueries {

    //Login verifizieren
    public static boolean verifyLogin(String email, String passwd, GUI gui) {
        String sql = "select count (*)\n" +
                "from users\n" +
                "where email = ? and passwd = ?";

        try {

            //Statement erstellen mit String
            PreparedStatement pst = gui.connection.prepareStatement(sql);

            //Parameter setzen für "?"
            pst.setString(1, email);
            pst.setString(2, passwd);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; //true, wenn Benutzer gefunden wurde
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false; //Bei Fehler oder ungültigen Daten
    }

    //Anzahl Gadgets ermitteln
    public static int countGadgets(GUI gui) {
        int count = 0;  //Variable für die Anzahl
        String sql = "select count (*) as total\n" +
                "from gadgets";
        //Verbindung und Statement erstellen
        try {
            PreparedStatement pst = gui.connection.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            // Ergebnis abrufen
            if (rs.next()) {
                count = rs.getInt("total");
                return count; //Anzahl zurückgeben
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return 0; //Keine Ergebnisse
    }

    //Neuen User Registrieren
    public static boolean registerUser(String email, String passwd, GUI gui) {

        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        //Zunächst prüfen, ob der Benutzer bereits existiert
        try {
            PreparedStatement pst = gui.connection.prepareStatement(sql);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            //Fall, dass User mit dieser E-Mail schon existiert
            if (rs.next() && rs.getInt(1) > 0) {
                return false;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false; //Fehler beim Überprüfen der E-Mail
        }

        //Benutzer existiert noch nicht → registrieren
        String insertQuery = "INSERT INTO users (email, passwd) VALUES (?, ?)";

        try {
            PreparedStatement pst = gui.connection.prepareStatement(insertQuery);
            pst.setString(1, email); //Parameter E-Mail einsetzen
            pst.setString(2, passwd); //Parameter Passwort einsetzen
            int rowsAffected = pst.executeUpdate(); //Abfrage ausführen

            //Wenn eine Zeile betroffen ist, wurde der Benutzer erfolgreich registriert
            return rowsAffected > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false; //Fehler beim Einfügen des neuen Benutzers
        }
    }

    //Gadgets der URL nach absteigend abfragen
    public static List<DTO> getBestRatedGadgets(GUI gui) {

        //Liste mit Gadgets anlegen
        List<DTO> gadgets = new ArrayList<>();

        //Abfrage der Gadgets aus DB
        String sql = "select url as gadget_url, email as verkaeufer_email, keywords, description, cover\n" +
                "from gadgets\n" +
                "order by url asc";

        try {
            ResultSet rs = gui.connection.createStatement().executeQuery(sql);

            //Durch RS iterieren und für jeden Eintrag ein DTO-Objekt anlegen
            while (rs.next()) {

                //BLOB für Cover abrufen
                Blob coverBlob = rs.getBlob("cover");

                //BLOB in Icon umwandeln
                Icon cover = null;
                if (coverBlob != null){
                    cover = Converter.blob2Icon(coverBlob);
                }

                //DTO für jedes Gadget mit durchschnittlicher Bewertung erstellen
                DTO gadget = new DTO(
                        rs.getString("gadget_url"),
                        rs.getString("verkaeufer_email"),
                        rs.getString("keywords"),
                        rs.getString("description"),
                        cover,

                        //Bewertung noch auf 0 gesetzt → Kommt noch
                        0.0,

                        //Kommentare noch leer → Kommen noch
                        ""
                );

                //Kommentare und Rating abfragen
                getCommentsAndAverageRating(gadget);

                //An Liste anhängen
                gadgets.add(gadget);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return gadgets;
    }

    //Kommentare zu Gadget-Element holen
    public static void getCommentsAndAverageRating (DTO gadget){
        String sql = "select gefallen, kommentar, url\n" +
                "from bewertung\n" +
                "where url = ?\n" +
                "order by kommentar desc";
        try (Connection con = Globals.getPoolConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            //Parameter setzen für "?"
            pst.setString(1, gadget.getUrl());

            int summeRatings = 0;
            int anzahlRatings = 0;
            //String comments = "";

            //Prepared-statement ausführen
            try (ResultSet rs = pst.executeQuery()) {
                List<String> commentList = new ArrayList<>();
                while (rs.next()){
                    summeRatings += rs.getInt("gefallen");
                    anzahlRatings++;
                    //comments += "- " + rs.getString("kommentar") + "\n";
                    String comment = "- " + rs.getString("kommentar");
                    commentList.add(comment);
                }
                Collections.reverse(commentList);
                //String comments = String.join("\n", commentList) + "\n";
                String comments = String.join(System.lineSeparator(), commentList) + System.lineSeparator();
                gadget.setComments(comments);

                gadget.setAverageRating((double) summeRatings /anzahlRatings);
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Kommentar und Bewertung zu Gadget hinzufügen
    public static boolean addCommentRating (GUI gui, String url, String email_bewerter, String comment, int rating) {

        //Prüfen, ob Nutzer hat schon Kommentar zu dem Produkt abgegeben hat
        String sqlCheck = "select count (*)\n" +
                "from bewertung\n" +
                "where url =? and email =?";
        try {
            PreparedStatement pst = gui.connection.prepareStatement(sqlCheck);
            pst.setString(1, url);
            pst.setString(2, email_bewerter);
            ResultSet rs = pst.executeQuery();

            //Fall, dass User mit dieser E-Mail schon kommentiert hat
            if (rs.next() && rs.getInt(1) > 0) {
                boolean successfullyUpdated = false;

                String sqlUpdate = "UPDATE bewertung\n" +
                        "SET kommentar = ?,\n" +
                        "gefallen = ?\n" +
                        "WHERE email = ? \n" +
                        "  AND url = ?";

                try {
                    PreparedStatement pstUpdate = gui.connection.prepareStatement(sqlUpdate);

                    //Werte einfügen
                    pstUpdate.setString(1, comment);
                    pstUpdate.setInt(2, rating);
                    pstUpdate.setString(3, email_bewerter);
                    pstUpdate.setString(4, url);

                    //Ausführen
                    int rowsAffected = pstUpdate.executeUpdate();

                    if (rowsAffected > 0){
                        successfullyUpdated = true;
                        return successfullyUpdated;
                    }
                    return successfullyUpdated;

                } catch (SQLException e){
                    throw new RuntimeException("Ein fehler ist aufgetreten: " + e.getMessage());
                }
            }

            //User hat für dieses Produkt noch nicht kommentiert
            else {
                System.out.println("Nutzer hat hier noch nicht kommentiert");

                boolean succcessfulyInserted = false;

                String sqlInsert = "insert into bewertung (email, url, gefallen, kommentar)\n" +
                        "values (?, ?, ?, ?)";

                try {
                    PreparedStatement pstInsert = gui.connection.prepareStatement(sqlInsert);

                    //Werte einfügen
                    pstInsert.setString(1, email_bewerter);
                    pstInsert.setString(2, url);
                    pstInsert.setInt(3, rating);
                    pstInsert.setString(4, comment);

                    //Ausführen
                    int rowsAffected = pstInsert.executeUpdate();

                    if (rowsAffected > 0){
                        succcessfulyInserted = true;
                        return succcessfulyInserted;
                    }
                    return succcessfulyInserted;

                } catch (SQLException e){
                    throw new RuntimeException("Ein fehler ist aufgetreten: " + e.getMessage());
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false; //Fehler beim Ausführen
        }
    }

    //Produkt erstellen/ updaten
    public static void createUpdateGadget(GUI gui, String url, String email_verkaeufer, String keywords, String description, Icon cover){
        String sqlIsInDB = "select *\n" +
                "from gadgets\n" +
                "where url = ?";

        try {
            PreparedStatement pst = gui.connection.prepareStatement(sqlIsInDB);
            pst.setString(1, url);
            ResultSet rs = pst.executeQuery();

            //Produkt schon vorhanden UND Verkäufer ist angemeldet → aktualisieren
            if (rs.next() && rs.getString(2).equals(email_verkaeufer)){
                String sqlUpdateGadget = "UPDATE Gadgets\n" +
                        "SET Keywords = ?, Description = ?, Cover = ?\n" +
                        "WHERE URL = ?";

                try {
                    PreparedStatement pstUpdate = gui.connection.prepareStatement(sqlUpdateGadget);

                    //Werte einfügen
                    pstUpdate.setString(1, keywords);
                    pstUpdate.setString(2, description);
                    pstUpdate.setBlob(3, Converter.icon2Blob(cover, gui.connection));
                    pstUpdate.setString(4, url);

                    //Ausführen
                    int rowsAffected = pstUpdate.executeUpdate();
                    if (rowsAffected > 0){
                        System.out.println("Erfolgreich aktualisiert");
                    }

                } catch (SQLException e){
                    throw new RuntimeException("Ein fehler ist aufgetreten: " + e.getMessage());
                }
            }

            //Produkt noch nicht vorhanden → anlegen
            else {
                String sqlCreateNewGadget = "INSERT INTO Gadgets (url, email, keywords, description, cover)\n" +
                        "VALUES (?, ?, ?, ?, ?)";

                try {
                    PreparedStatement pstInsert = gui.connection.prepareStatement(sqlCreateNewGadget);

                    //Werte einfügen
                    pstInsert.setString(1, url);
                    pstInsert.setString(2, email_verkaeufer);
                    pstInsert.setString(3, keywords);
                    pstInsert.setString(4, description);
                    pstInsert.setBlob(5, Converter.icon2Blob(cover, gui.connection));

                    //Ausführen
                    int rowsAffected = pstInsert.executeUpdate();
                    if (rowsAffected > 0){
                        System.out.println("Erfolgreich eingestellt");
                    }

                } catch (SQLException e){
                    throw new RuntimeException("Ein fehler ist aufgetreten: " + e.getMessage());
                }
            }

        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //Produkt löschen (als Verkäufer)
    public static void deleteItem(GUI gui, String url){
        String sqlDelete = "DELETE FROM Gadgets\n" +
                "WHERE URL = ?";

        try {
            PreparedStatement pst = gui.connection.prepareStatement(sqlDelete);
            pst.setString(1, url);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0){
                System.out.println("Produkt erfolgreich gelöscht");
            }
            else {
                System.out.println("Fehler beim Löschen");
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
