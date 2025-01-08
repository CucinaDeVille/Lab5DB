package edu.hsog.db;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DBQueries {

    //TODO: Methoden hier in try-with-Ressource umbauen!!

    //Login verifizieren - Umgeschrieben
    public static boolean verifyLogin(String email, String passwd) {
        String sql = "select count (*)\n" +
                "from users\n" +
                "where email = ? and passwd = ?";

        try (Connection connection = Globals.getPoolConnection();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            //Parameter setzen für "?"
            pst.setString(1, email);
            pst.setString(2, passwd);

            try(ResultSet rs = pst.executeQuery()){
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0; //true, wenn Benutzer gefunden wurde
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false; //Bei Fehler oder ungültigen Daten
    }

    //Anzahl Gadgets ermitteln - Umgeschrieben
    public static int countGadgets() {
        int count = 0;  //Variable für die Anzahl
        String sql = "select count (*) as total\n" +
                "from gadgets";
        //Verbindung und Statement erstellen
        try (Connection connection = Globals.getPoolConnection();
             PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()
        ) {
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

    //Neuen User Registrieren - Umgeschrieben
    public static boolean registerUser(String email, String passwd) {

        //Prüfen, ob User schon existiert
        String sqlCheck = "SELECT COUNT(*) FROM users WHERE email = ?";

        //Benutzer existiert noch nicht → registrieren
        String insertQuery = "INSERT INTO users (email, passwd) VALUES (?, ?)";

        try (Connection connection = Globals.getPoolConnection();
             PreparedStatement pstCheck = connection.prepareStatement(sqlCheck);
             PreparedStatement pstInsert = connection.prepareStatement(insertQuery)) {

            //Zunächst prüfen, ob der Benutzer bereits existiert
            pstCheck.setString(1, email); //Paramerter setzen

            //Ausführen
            try(ResultSet rs = pstCheck.executeQuery()){
                //Fall, dass User mit dieser E-Mail schon existiert
                if (rs.next() && rs.getInt(1) > 0) {
                    return false;
                }
            }
            //-----------------------------------------------------
            //Benutzer einfügen
            pstInsert.setString(1, email); //Parameter E-Mail einsetzen
            pstInsert.setString(2, passwd); //Parameter Passwort einsetzen

            int rowsAffected = pstInsert.executeUpdate(); //Abfrage ausführen

            //Wenn eine Zeile betroffen ist, wurde der Benutzer erfolgreich registriert
            return rowsAffected > 0; //True, wenn größer 0, sonst false

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false; //Fehler beim Überprüfen der E-Mail
        }
    }

    //Gadgets der URL nach absteigend abfragen - Umgeschrieben
    public static List<DTO> getBestRatedGadgets() {

        //Liste mit Gadgets anlegen
        List<DTO> gadgets = new ArrayList<>();

        //Abfrage der Gadgets aus DB
        String sql = "select url as gadget_url, email as verkaeufer_email, keywords, description, cover\n" +
                "from gadgets\n" +
                "order by url asc";

        try (Connection connection = Globals.getPoolConnection();
             PreparedStatement pst = connection.prepareStatement(sql)) {

            try(ResultSet rs = pst.executeQuery()){
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
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return gadgets;
    }

    //Kommentare zu Gadget-Element holen - Umgeschrieben
    public static void getCommentsAndAverageRating (DTO gadget){
        String sql = "select gefallen, kommentar, url\n" +
                "from bewertung\n" +
                "where url = ?\n" +
                "order by kommentar desc";

        try (Connection connection = Globals.getPoolConnection();
            PreparedStatement pst = connection.prepareStatement(sql)) {

            //Parameter setzen für "?"
            pst.setString(1, gadget.getUrl());

            //Prepared-statement ausführen
            try(ResultSet rs = pst.executeQuery()){
                int summeRatings = 0;
                int anzahlRatings = 0;
                //String comments = "";

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

                //Durchschnitt berechnen, wenn Bewertungen vorhanden sind
                if (anzahlRatings > 0){
                    gadget.setAverageRating((double) summeRatings /anzahlRatings);
                } else {
                    gadget.setAverageRating(0.0);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Kommentar und Bewertung zu Gadget hinzufügen - Umgeschrieben
    public static boolean addCommentRating (String url, String email_bewerter, String comment, int rating) {

        //Prüfen, ob Nutzer hat schon Kommentar zu dem Produkt abgegeben hat
        String sqlCheck = "select count (*)\n" +
                "from bewertung\n" +
                "where url =? and email =?";
        try (Connection connection = Globals.getPoolConnection();
             PreparedStatement pstCheck = connection.prepareStatement(sqlCheck)) {

            //Parameter setzen
            pstCheck.setString(1, url);
            pstCheck.setString(2, email_bewerter);

            //Ausführen
            try(ResultSet rs = pstCheck.executeQuery()){
                //Fall, dass User mit dieser E-Mail schon kommentiert hat
                if (rs.next() && rs.getInt(1) > 0) {
                    //Benutzer hat bereits kommentiert → Kommentar und Bewertung aktualisieren
                    return updateCommentRating(connection, url, email_bewerter, comment, rating);
                }
                //User hat für dieses Produkt noch nicht kommentiert
                else {
                    System.out.println("Nutzer hat hier noch nicht kommentiert");
                    // Benutzer hat noch nicht kommentiert, neuen Kommentar einfügen
                    return insertCommentRating(connection, url, email_bewerter, comment, rating);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false; //Fehler beim Ausführen
        }
    }

    //Hilfsmethode zum Aktualisieren eines Kommentars
    private static boolean updateCommentRating(Connection connection, String url, String email_bewerter, String comment, int rating) {
        String sqlUpdate = "UPDATE bewertung\n" +
                "SET kommentar = ?,\n" +
                "gefallen = ?\n" +
                "WHERE email = ? \n" +
                "  AND url = ?";

        try (PreparedStatement pstUpdate = connection.prepareStatement(sqlUpdate)) {
            pstUpdate.setString(1, comment);
            pstUpdate.setInt(2, rating);
            pstUpdate.setString(3, email_bewerter);
            pstUpdate.setString(4, url);

            //Ausführen
            return pstUpdate.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Aktualisieren des Kommentars: " + e.getMessage());
        }
    }

    //Hilfsmethode für neuen Kommentar
    private static boolean insertCommentRating(Connection connection, String url, String email_bewerter, String comment, int rating) {
        String sqlInsert = "insert into bewertung (email, url, gefallen, kommentar)\n" +
                "values (?, ?, ?, ?)";

        try (PreparedStatement pstInsert = connection.prepareStatement(sqlInsert)) {
            pstInsert.setString(1, email_bewerter);
            pstInsert.setString(2, url);
            pstInsert.setInt(3, rating);
            pstInsert.setString(4, comment);

            // Ausführen
            return pstInsert.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Einfügen des Kommentars: " + e.getMessage());
        }
    }





    //Produkt erstellen/ updaten - Umgeschrieben
    public static void createUpdateGadget(String url, String email_verkaeufer, String keywords, String description, Icon cover){
        String sqlCheck = "select *\n" +
                "from gadgets\n" +
                "where url = ?";

        try (Connection connection = Globals.getPoolConnection();
             PreparedStatement pstCheck = connection.prepareStatement(sqlCheck)) {

            //Parameter setzen
            pstCheck.setString(1, url);

            //Ausführen
            try (ResultSet rs = pstCheck.executeQuery()) {

                //Produkt mit dieser URL existiert
                if (rs.next()){
                    //Wer ist Verkäufer?
                    String existingEmail = rs.getString(2);

                    //Produkt schon vorhanden UND Verkäufer ist angemeldet → aktualisieren
                    if (existingEmail.equals(email_verkaeufer)) {
                        //Produkt aktualisieren
                        updateGadget(connection, url, keywords, description, cover);
                    }
                }

                //Produkt noch nicht vorhanden → anlegen
                else {
                    createGadget(connection, url, email_verkaeufer, keywords, description, cover);
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //Hilfsmethode zu createUpdate
    private static void updateGadget(Connection connection, String url, String keywords, String description, Icon cover) {
        String sqlUpdate = "UPDATE Gadgets\n" +
                "SET Keywords = ?, Description = ?, Cover = ?\n" +
                "WHERE URL = ?";

        try (PreparedStatement pstUpdate = connection.prepareStatement(sqlUpdate)) {
            pstUpdate.setString(1, keywords);
            pstUpdate.setString(2, description);
            pstUpdate.setBlob(3, Converter.icon2Blob(cover, connection));
            pstUpdate.setString(4, url);

            int rowsAffected = pstUpdate.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Erfolgreich aktualisiert");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Aktualisieren: " + e.getMessage(), e);
        }
    }

    //Hilfsmethode zu createUpdate
    private static void createGadget(Connection connection, String url, String email_verkaeufer, String keywords, String description, Icon cover) {
        String sqlInsert = "INSERT INTO Gadgets (url, email, keywords, description, cover)\n" +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstInsert = connection.prepareStatement(sqlInsert)) {

            //Werte einfügen
            pstInsert.setString(1, url);
            pstInsert.setString(2, email_verkaeufer);
            pstInsert.setString(3, keywords);
            pstInsert.setString(4, description);
            pstInsert.setBlob(5, Converter.icon2Blob(cover, connection));

            int rowsAffected = pstInsert.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Erfolgreich hinzugefügt");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Fehler beim Hinzufügen des Gadgets: " + e.getMessage(), e);
        }
    }





    //Produkt löschen (als Verkäufer)
    public static void deleteItem(String url){
        String sqlDelete = "DELETE FROM Gadgets\n" +
                "WHERE URL = ?";

        try(Connection connection = Globals.getPoolConnection();
            PreparedStatement pst = connection.prepareStatement(sqlDelete)) {

            //Werte setzen
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
