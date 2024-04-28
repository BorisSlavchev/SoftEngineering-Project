import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class BookPanel extends JPanel {
        private Main main;
        private ImageIcon image;
        private String title;
        private String author;
        private String year;
        private JButton actionButton;


        public BookPanel(Main main, ImageIcon image, String title, String author, String year) {
            this.main = main;
            this.image = image;
            this.title = title;
            this.author = author;
            this.year = year;

            setPreferredSize(new Dimension(800, 600));
            setLayout(new BorderLayout());

            // Check if the book is already in the bookshelf
            boolean isInBookshelf = false;
            DefaultTableModel bookshelfModel = main.getBookshelfTableModel();
            DefaultTableModel libraryModel = main.getLibraryTableModel();
            JTable bookshelfTable = main.getBookshelfTable();
            JTable libraryTable = main.getLibraryTable();
            for (int i = 0; i < bookshelfModel.getRowCount(); i++) {
                if (title.equals(bookshelfModel.getValueAt(i, 0))) {
                    isInBookshelf = true;
                    break;
                }
            }

            // Initialize buttons
            boolean finalIsInBookshelf;
            if (isInBookshelf) {
                actionButton = new JButton("Remove from Bookshelf");
                finalIsInBookshelf = true;
            } else {
                actionButton = new JButton("Add to Bookshelf");
                finalIsInBookshelf = false;
            }

            // Add buttons to the panel
            add(actionButton, BorderLayout.SOUTH);

            actionButton.addActionListener(e -> {
                if (finalIsInBookshelf) {
                    main.getBtnReturnToLibrary().doClick();
                } else {
                    main.getBtnMoveToBookshelf().doClick();
                }
                bookshelfTable.repaint();
                libraryTable.repaint();
                main.refreshBookshelfTable();
                main.refreshLibraryTable();
                main.repaint();
            });
        }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            int width = 250; // Width of the image
            int height = 400; // Height of the image
            g.drawImage(image.getImage(), 10, 100, width, height, this); // Draw the image
        }
        int startX = 400; // Start position for text
        int startY = 200; // Start position for text
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Title: " + title, startX, startY);
        g.drawString("Author: " + author, startX, startY + 20);
        g.drawString("Year of Publication: " + year, startX, startY + 40);
    }

    public void setImage(ImageIcon image) {
        this.image = image;
        repaint();
    }

    public void setTitle(String title) {
        this.title = title;
        repaint();
    }

    public void setAuthor(String author) {
        this.author = author;
        repaint();
    }

    public void setYear(String year) {
        this.year = year;
        repaint();
    }
}
