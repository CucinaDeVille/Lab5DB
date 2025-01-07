package edu.hsog.db;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.codec.binary.Base64;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;

public class RestoreDB {
    private static boolean silent = false;

    public static void main(String[] args) throws CsvValidationException, FileNotFoundException {
        // saveGadgets("./db_gadgets.csv");
        // saveUsers("./db_users.csv");
        // saveBewertungen("./db_bewertungen.csv");
        deleteAllTables();
        readUsers("./db_users.csv");
        readGadgets("./db_gadgets.csv");
        readBewertungen("./db_bewertungen.csv");
    }

    public static void restoreDB() {
        try {
            silent = true;
            deleteAllTables();
            readUsers("./db_users.csv");
            readGadgets("./db_gadgets.csv");
            readBewertungen("./db_bewertungen.csv");
        } catch ( Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveGadgets(String filename) {
        // SQL-Abfrage, um Daten einschließlich BLOB zu erhalten
        String query = "SELECT url, email, keywords ,description, cover FROM gadgets";
        Globals.initConnectionPool();
        try (Connection connection = Globals.getPoolConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery();
             CSVWriter csvWriter = new CSVWriter(new FileWriter(filename))) {

            // CSV Header
            csvWriter.writeNext(new String[] {"URL", "eMail", "Keywords", "Description", "Cover"})  ;

            // Verarbeite jedes Ergebnis aus der Abfrage
            while (resultSet.next()) {
                String s1 = resultSet.getString("URL");
                String s2 = resultSet.getString("eMail");
                String s3 = resultSet.getString("Keywords");
                String s4 = resultSet.getString("Description");
                Blob blobData = resultSet.getBlob("cover");

                // BLOB in Base64 umwandeln
                String base64Blob = null;
                if (blobData != null) {
                    byte[] blobBytes = blobData.getBytes(1, (int) blobData.length());
                    byte[] encodedBytes = Base64.encodeBase64(blobBytes);
                    base64Blob = new String(encodedBytes);
                }

                // Schreibe in die CSV-Datei
                csvWriter.writeNext(new String[] {s1,s2,s3,s4,base64Blob});
            }

            if (!silent) System.out.println("Daten wurden erfolgreich in Base64 umgewandelt und in der CSV-Datei " + filename + " gespeichert.");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveUsers(String filename) {
        // SQL query to fetch data from the 'users' table
        String query = "SELECT email, passwd FROM users";

        // Initialize the database connection pool
        Globals.initConnectionPool();

        try (Connection connection = Globals.getPoolConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery();
             CSVWriter csvWriter = new CSVWriter(new FileWriter(filename))) {

            // Write the CSV header
            csvWriter.writeNext(new String[]{"Email", "Password"});

            // Process each row from the result set
            while (resultSet.next()) {
                String email = resultSet.getString("email");
                String password = resultSet.getString("passwd");

                // Write the row to the CSV file
                csvWriter.writeNext(new String[]{email, password});
            }

            System.out.println("User data has been successfully saved to the CSV file: " + filename);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveBewertungen(String filename) {
        // SQL query to fetch data from the 'bewertung' table
        String query = "SELECT email, url, gefallen, kommentar FROM bewertung";

        // Initialize the database connection pool
        Globals.initConnectionPool();

        try (Connection connection = Globals.getPoolConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery();
             CSVWriter csvWriter = new CSVWriter(new FileWriter(filename))) {

            // Write the CSV header
            csvWriter.writeNext(new String[]{"Email", "URL", "Gefallen", "Kommentar"});

            // Process each row from the result set
            while (resultSet.next()) {
                String email = resultSet.getString("email");
                String url = resultSet.getString("url");
                int gefallen = resultSet.getInt("gefallen"); // integer column
                String kommentar = resultSet.getString("kommentar");

                // Write the row to the CSV file
                csvWriter.writeNext(new String[]{
                        email,
                        url,
                        String.valueOf(gefallen), // Convert integer to string
                        kommentar
                });
            }

            System.out.println("Bewertung data has been successfully saved to the CSV file: " + filename);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void readBewertungen(String filename) throws CsvValidationException, FileNotFoundException {
        // SQL query to insert data into the 'bewertung' table
        String insertQuery = "INSERT INTO bewertung (email, url, gefallen, kommentar) VALUES (?, ?, ?, ?)";

        // Initialize the database connection pool
        Globals.initConnectionPool();

        FileReader r = new FileReader(filename);
        CSVReader csvReader = new CSVReader(r);

        try (Connection connection = Globals.getPoolConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);) {

            // Skip the header row
            String[] nextLine = csvReader.readNext();

            // Process each row from the CSV file
            while ((nextLine = csvReader.readNext()) != null) {
                // Map CSV columns to variables
                String email = nextLine[0];
                String url = nextLine[1];
                int gefallen = Integer.parseInt(nextLine[2]);
                String kommentar = nextLine[3];

                // Set parameters for the prepared statement
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, url);
                preparedStatement.setInt(3, gefallen);
                preparedStatement.setString(4, kommentar);

                // Execute the insert
                preparedStatement.executeUpdate();
            }

            if (!silent) System.out.println("CSV data has been successfully inserted into the database from " + filename);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void readUsers(String filename) throws CsvValidationException {
        // SQL query to insert data into the 'users' table
        String insertQuery = "INSERT INTO users (email, passwd) VALUES (?, ?)";

        // Initialize the database connection pool
        Globals.initConnectionPool();

        try (Connection connection = Globals.getPoolConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
             CSVReader csvReader = new CSVReader(new FileReader(filename))) {

            // Skip the header row
            String[] nextLine = csvReader.readNext();

            // Process each row from the CSV file
            while ((nextLine = csvReader.readNext()) != null) {
                // Map CSV columns to variables
                String email = nextLine[0];
                String password = nextLine[1];

                // Set parameters for the prepared statement
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);

                // Execute the insert
                preparedStatement.executeUpdate();
            }

            if (!silent) System.out.println("CSV data has been successfully inserted into the 'users' table from " + filename );
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void readGadgets(String filename) throws CsvValidationException {
        // SQL query to insert data into the 'gadgets' table
        String insertQuery = "INSERT INTO gadgets (url, email, keywords, description, cover) VALUES (?, ?, ?, ?, ?)";

        // Initialize the database connection pool
        Globals.initConnectionPool();

        try (Connection connection = Globals.getPoolConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
             CSVReader csvReader = new CSVReader(new FileReader(filename))) {

            // Skip the header row
            String[] nextLine = csvReader.readNext();

            // Process each row from the CSV file
            while ((nextLine = csvReader.readNext()) != null) {
                // Map CSV columns to variables
                String url = nextLine[0];
                String email = nextLine[1];
                String keywords = nextLine[2];
                String description = nextLine[3];
                String base64Blob = nextLine[4];

                // Convert Base64 string back to bytes
                byte[] blobBytes = null;
                if (base64Blob != null && !base64Blob.isEmpty()) {
                    // Convert the Base64 string into a byte array
                    byte[] base64Bytes = base64Blob.getBytes();
                    // Decode the Base64 byte array into the original byte data
                    blobBytes = Base64.decodeBase64(base64Bytes);
                }

                // Set parameters for the prepared statement
                preparedStatement.setString(1, url);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, keywords);
                preparedStatement.setString(4, description);
                if (blobBytes != null) {
                    preparedStatement.setBytes(5, blobBytes); // Insert BLOB as byte array
                } else {
                    preparedStatement.setNull(5, Types.BLOB); // Handle null BLOB
                }

                // Execute the insert
                preparedStatement.executeUpdate();
            }

            if (!silent) System.out.println("CSV data has been successfully inserted into the 'gadgets' table from " + filename );
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllTables() {
        // SQL queries to delete all data from each table
        String deleteBewertungenQuery = "DELETE FROM bewertung";
        String deleteGadgetsQuery = "DELETE FROM gadgets";
        String deleteUsersQuery = "DELETE FROM users";

        // Initialize the database connection pool
        Globals.initConnectionPool();

        try (Connection connection = Globals.getPoolConnection();
             PreparedStatement deleteUsersStmt = connection.prepareStatement(deleteUsersQuery);
             PreparedStatement deleteGadgetsStmt = connection.prepareStatement(deleteGadgetsQuery);
             PreparedStatement deleteBewertungenStmt = connection.prepareStatement(deleteBewertungenQuery)) {

            // Disable foreign key constraints if necessary (e.g., if "bewertung" references "users" or "gadgets")
            // You can disable constraints if needed using:
            // connection.createStatement().execute("ALTER TABLE bewertung DISABLE CONSTRAINT fk_bewertung_user");

            // Execute DELETE statements for each table
            deleteBewertungenStmt.executeUpdate();
            deleteGadgetsStmt.executeUpdate();
            deleteUsersStmt.executeUpdate();

            // Re-enable foreign key constraints after deletion if they were disabled
            // connection.createStatement().execute("ALTER TABLE bewertung ENABLE CONSTRAINT fk_bewertung_user");

            System.out.println("All data from users, gadgets, and bewertung tables has been deleted.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
