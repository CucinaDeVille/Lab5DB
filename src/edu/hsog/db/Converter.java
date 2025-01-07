package edu.hsog.db;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.Icon;


public class Converter {

    //Bild von Festplatte lesen
    public static Icon loadIconFromFile(String path) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage();
        int maxX, maxY, max;
        double ratio = 0;
        maxX = image.getWidth(null);
        maxY = image.getHeight(null);
        max = Math.max(maxX, maxY);
        ratio = (250.0 / (float) max);
        image = image.getScaledInstance((int) (maxX * ratio),
                (int) (maxY * ratio), Image.SCALE_DEFAULT);
        ImageIcon ic = new ImageIcon(image);
        return (Icon) ic;
    }

    //Konvertiert Image (Java) in Icon (Swing)
    public static ImageIcon image2ImageIcon(Image i) {
        if (i == null) return null;

        ImageIcon ic = new ImageIcon(i);
        return ic;
    }
    //Konvertiert Icon (Swing) in Image (Java)
    public static Image imageIcon2Image(ImageIcon ic) {
        if (ic == null) return null;

        Image i = ic.getImage();
        return i;
    }

    //Konvertiert Bild in Blob-Objekt → Speichern in DB
    public static Blob imageIcon2Blob(ImageIcon myImageIcon, Connection con) {
        if (myImageIcon == null) return null;
        Blob myBlob = null;

        try {

            ByteArrayOutputStream myByteArrayOutputStream = new ByteArrayOutputStream();
            BufferedImage buImg = new BufferedImage(myImageIcon.getIconWidth(), myImageIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            buImg.getGraphics().drawImage(myImageIcon.getImage(), 0, 0, myImageIcon.getImageObserver());
            ImageIO.write(buImg, "png", myByteArrayOutputStream);
            byte[] myByteArray = myByteArrayOutputStream.toByteArray();
            myBlob = con.createBlob();
            myBlob.setBytes(1, myByteArray);
        } catch (Exception ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return myBlob;
    }
    //Wrapper-Methode zu imageIcon2Blob
    public static Blob icon2Blob(Icon myIcon, Connection con) {
        return imageIcon2Blob((ImageIcon) myIcon, con);
    }


    //Konvertiert Blob-Objekt in ein Bild
    public static ImageIcon blob2ImageIcon(Blob myBlob) {

        if (myBlob == null) return null;

        ImageIcon myImageIcon = null;
        try {
            byte[] byteArray = myBlob.getBytes((long) 1, (int) myBlob.length());
            myImageIcon = new ImageIcon(byteArray);

        } catch (SQLException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return myImageIcon;
    }
    //Wrapper-Methode zu blob2ImageIcon
    public static Icon blob2Icon(Blob myBlob) {
        return (Icon) blob2ImageIcon(myBlob);
    }

    //Konvertiert Icon (Swing) in BufferedImage (Java); notwendig zum Erstellen eines BLOB für DB
    public static BufferedImage iconToBufferedImage(Icon icon) {
        // Create a buffered image with the same dimensions as the icon
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();

        // Paint the icon onto the buffered image
        icon.paintIcon(null, g, 0, 0);
        g.dispose();

        return image;
    }
}


