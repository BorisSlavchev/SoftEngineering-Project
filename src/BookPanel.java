import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BookPanel extends JPanel {
        private LibraryAndBookshelf libraryAndBookshelf;
        private ImageIcon image;
        private String title;
        private String author;
        private String year;
        private String description;
        private String currentPages;
        private String totalPages;
        private String rating;
        private JButton actionButton;
        private JPanel ratingPanel;

        public BookPanel(LibraryAndBookshelf libraryAndBookshelf, ImageIcon image, String title, String author, String year, String description, String currentPages, String totalPages, String rating) {
            this.libraryAndBookshelf = libraryAndBookshelf;
            this.image = image;
            this.title = title;
            this.author = author;
            this.year = year;
            this.description = description;
            this.currentPages = currentPages;
            this.totalPages = totalPages;
            this.rating = rating;

            setPreferredSize(new Dimension(800, 600));
            setLayout(new BorderLayout());

            JLabel ratingQuestionLabel = new JLabel("Rate this book!");
            ratingQuestionLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Adjust font size
            ratingQuestionLabel.setBounds(490, 340, 400, 30);
            add(ratingQuestionLabel, BorderLayout.SOUTH);
            // Initialize rating panel
            ratingPanel = new JPanel();
            ratingPanel.setLayout(new GridLayout(1, 5));
            for (int i = 1; i <= 5; i++) {
                JLabel label = new JLabel(String.valueOf(i));
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                ratingPanel.add(label);
            }

            // Add mouse listener to rating panel
            // Add mouse listener to rating panel
            ratingPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int width = ratingPanel.getWidth();
                    int x = e.getX();
                    int starWidth = width / 5;
                    int newRating = x / starWidth;

                    // Ensure the rating is within the range 1-5
                    newRating = Math.max(1, Math.min(5, newRating + 1));
                    setRating(String.valueOf(newRating));
                }
            });

            // Add components to the panel
            ratingPanel.setPreferredSize(new Dimension(300, 50)); // Set the size
            ratingPanel.setBounds(400, 370, 300, 50);
            add(ratingPanel, BorderLayout.SOUTH);
            JButton updateCurrentPagesButton = new JButton("Update Current Pages");
            updateCurrentPagesButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String newCurrentPages = JOptionPane.showInputDialog(BookPanel.this, "Enter new current pages:", "Update Current Pages", JOptionPane.PLAIN_MESSAGE);
                    if (newCurrentPages != null) { // Check if the user clicked OK
                        System.out.println(Integer.parseInt(newCurrentPages));

                        try {
                            int current = Integer.parseInt(newCurrentPages);
                            int total = Integer.parseInt(totalPages);
                            if (current >= 0 && current <= total) {
                                libraryAndBookshelf.updateBookProgress(title, current);
                            } else if (current >= total && current <= total) {
                                JOptionPane.showMessageDialog(BookPanel.this, "Invalid input: Current pages cannot exceed total pages", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            else if (!isInteger(currentPages)){
                                JOptionPane.showMessageDialog(BookPanel.this, "Invalid input: Please enter a number", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            else {
                                JOptionPane.showMessageDialog(BookPanel.this, "Invalid input: Unknown error", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(BookPanel.this, "Invalid input: Please enter a valid number", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
            updateCurrentPagesButton.setBounds(500, 500, 250, 40);
            add(updateCurrentPagesButton, BorderLayout.SOUTH);

            // Check if the book is already in the bookshelf
            boolean isInBookshelf = false;
            DefaultTableModel bookshelfModel = libraryAndBookshelf.getBookshelfTableModel();
            DefaultTableModel libraryModel = libraryAndBookshelf.getLibraryTableModel();
            JTable bookshelfTable = libraryAndBookshelf.getBookshelfTable();
            JTable libraryTable = libraryAndBookshelf.getLibraryTable();
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
                    libraryAndBookshelf.getBtnReturnToLibrary().doClick();
                } else {
                    libraryAndBookshelf.getBtnMoveToBookshelf().doClick();
                }
                bookshelfTable.repaint();
                libraryTable.repaint();
                libraryAndBookshelf.refreshBookshelfTable();
                libraryAndBookshelf.refreshLibraryTable();
                libraryAndBookshelf.repaint();
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
        String[] descriptionLines = splitDescription(description, 30);
        g.drawString("Description: ", startX, startY + 60);
        for (int i = 0; i < descriptionLines.length; i++) {
            g.drawString(descriptionLines[i], startX, startY + 80 + (i * 20));
        }
    }

    // Setter method to update the rating
    public void setRating(String rating) {
        if (Integer.parseInt(rating) < 1) {
            this.rating = "1";
        } else if (Integer.parseInt(rating) > 5) {
            this.rating = "5";
        } else {
            this.rating = rating;
        }
        updateRatingColor();
        libraryAndBookshelf.updateBookRating(title, Integer.parseInt(rating));
    }

    // Method to update the color of the rating bar based on the rating value
    private void updateRatingColor() {
        Color color;
        if (Integer.parseInt(rating) <= 2) {
            color = Color.RED;
        } else if (Integer.parseInt(rating) == 3) {
            color = Color.ORANGE;
        } else {
            color = Color.GREEN;
        }
        for (Component component : ratingPanel.getComponents()) {
            JLabel label = (JLabel) component;
            int value = Integer.parseInt(label.getText());
            label.setForeground(value <= Integer.parseInt(rating) ? color : Color.BLACK);
        }
    }

    public boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
    public void setDescription(String description) {
            this.description = description;
            repaint();
    }

    private String[] splitDescription(String description, int lineWidth) {
        // Split the description into lines that fit within the specified line width
        String[] words = description.split("\\s+");
        StringBuilder currentLine = new StringBuilder();
        StringBuilder nextLine = new StringBuilder();
        for (String word : words) {
            if (currentLine.length() + word.length() <= lineWidth) {
                currentLine.append(word).append(" ");
            } else {
                nextLine.append(currentLine.toString().trim()).append("\n");
                currentLine.setLength(0);
                currentLine.append(word).append(" ");
            }
        }
        nextLine.append(currentLine.toString().trim());
        return nextLine.toString().split("\n");
    }

}
