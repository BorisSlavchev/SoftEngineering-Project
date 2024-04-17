import javax.swing.*;
import java.awt.*;


public class BookPanel extends JPanel {
    private ImageIcon image;
    private String title;
    private String author;
    private String description;

    public BookPanel(ImageIcon image, String title, String author, String description) {
        this.image = image;
        this.title = title;
        this.author = author;
        this.description = description;
        setPreferredSize(new Dimension(500, 150));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            int width = 100; // Width of the image
            int height = getHeight(); // Height of the panel
            g.drawImage(image.getImage(), 10, 10, width, height - 20, this); // Draw the image
        }
        int startX = 120; // Start position for text
        int startY = 30; // Start position for text
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Title: " + title, startX, startY);
        g.drawString("Author: " + author, startX, startY + 20);
        g.drawString("Description: " + description, startX, startY + 40);
    }

    public void setImage(ImageIcon image) {
        this.image = image;
        repaint(); // Repaint the panel when the image changes
    }

    public void setTitle(String title) {
        this.title = title;
        repaint(); // Repaint the panel when the title changes
    }

    public void setAuthor(String author) {
        this.author = author;
        repaint(); // Repaint the panel when the author changes
    }

    public void setDescription(String description) {
        this.description = description;
        repaint(); // Repaint the panel when the description changes
    }
}
