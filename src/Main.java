import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main extends JFrame {
    private DefaultTableModel libraryTableModel;
    private DefaultTableModel bookshelfTableModel;
    private final String LIBRARY_FILE_NAME = "library_books.txt";
    private final String BOOKSHELF_FILE_NAME = "bookshelf_books.txt";

    public Main() {
        initComponents();
        loadFromFile(LIBRARY_FILE_NAME, libraryTableModel);
        loadFromFile(BOOKSHELF_FILE_NAME, bookshelfTableModel);
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Book Manager");
        setPreferredSize(new Dimension(1920, 800));

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.NORTH);

        JTextField searchField = new JTextField(20); // Search field
        panel.add(searchField);

        JButton btnMoveToBookshelf = new JButton("Move to Bookshelf");
        panel.add(btnMoveToBookshelf);

        JButton btnReturnToLibrary = new JButton("Return to Library");
        panel.add(btnReturnToLibrary);

        bookshelfTableModel = new DefaultTableModel();
        bookshelfTableModel.addColumn("Title");
        bookshelfTableModel.addColumn("Author");
        bookshelfTableModel.addColumn("Year Of Publications");
        bookshelfTableModel.addColumn("ISBN");
        bookshelfTableModel.addColumn("Current Pages");
        bookshelfTableModel.addColumn("Total Pages");

        libraryTableModel = new DefaultTableModel();
        libraryTableModel.addColumn("Title");
        libraryTableModel.addColumn("Author");
        libraryTableModel.addColumn("Year Of Publications");
        libraryTableModel.addColumn("ISBN");
        libraryTableModel.addColumn("Current Pages");
        libraryTableModel.addColumn("Total Pages");

        JTable bookshelfTable = new JTable(bookshelfTableModel);
        JScrollPane bookshelfScrollPane = new JScrollPane(bookshelfTable);
        getContentPane().add(bookshelfScrollPane, BorderLayout.CENTER);
        
        bookshelfTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = bookshelfTable.rowAtPoint(e.getPoint());
                int col = bookshelfTable.columnAtPoint(e.getPoint());
                String title = "";
                String author = "";
                String desc = "";
                if (row >= 0 && col == 0) { // Assuming titles are in the first column
                    title = (String) bookshelfTableModel.getValueAt(row, col);
                    author = (String) bookshelfTableModel.getValueAt(row, 1);
                    desc = (String) bookshelfTableModel.getValueAt(row, 2);
                    ImageIcon image = new ImageIcon("covers/" + title.replaceAll("\\s", "").toLowerCase() + ".jpg");
                    BookPanel bookPanel = new BookPanel(image, title, author, desc);
                    JOptionPane.showMessageDialog(Main.this, bookPanel);
                }
            }
        });


        JTable libraryTable = new JTable(libraryTableModel);
        JScrollPane libraryScrollPane = new JScrollPane(libraryTable);
        getContentPane().add(libraryScrollPane, BorderLayout.SOUTH);


        btnAddRow.addActionListener(e -> {
            String title = jTextFieldTitle.getText();
            String author = jTextFieldAuthor.getText();
            String year = jTextFieldYear.getText();
            String isbn = jTextFieldISBN.getText();
            String currentPages = jTextFieldCurrentPages.getText();
            String totalPages = jTextFieldTotalPages.getText();

            String[] rowData = {title, author, year, isbn, currentPages, totalPages};
            libraryTableModel.addRow(rowData);

            // Clear text fields after adding row
            jTextFieldTitle.setText("");
            jTextFieldAuthor.setText("");
            jTextFieldYear.setText("");
            jTextFieldISBN.setText("");
            jTextFieldCurrentPages.setText("");
            jTextFieldTotalPages.setText("");

            saveToFile(LIBRARY_FILE_NAME, libraryTableModel);
        });

        btnDeleteRow.addActionListener(e -> {
            int[] selectedRows = libraryTable.getSelectedRows();
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                libraryTableModel.removeRow(selectedRows[i]);
            }
            saveToFile(LIBRARY_FILE_NAME, libraryTableModel);
        });

        btnMoveToBookshelf.addActionListener(e -> {
            int[] selectedRows = libraryTable.getSelectedRows();
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                Object[] rowData = new Object[libraryTableModel.getColumnCount()];
                for (int j = 0; j < libraryTableModel.getColumnCount(); j++) {
                    rowData[j] = libraryTableModel.getValueAt(selectedRows[i], j);
                }
                bookshelfTableModel.addRow(rowData);
                libraryTableModel.removeRow(selectedRows[i]);
            }
            saveToFile(LIBRARY_FILE_NAME, libraryTableModel);
            saveToFile(BOOKSHELF_FILE_NAME, bookshelfTableModel);
        });

        btnReturnToLibrary.addActionListener(e -> {
            int[] selectedRows = bookshelfTable.getSelectedRows();
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                Object[] rowData = new Object[bookshelfTableModel.getColumnCount()];
                for (int j = 0; j < bookshelfTableModel.getColumnCount(); j++) {
                    rowData[j] = bookshelfTableModel.getValueAt(selectedRows[i], j);
                }
                libraryTableModel.addRow(rowData);
                bookshelfTableModel.removeRow(selectedRows[i]);
            }
            saveToFile(LIBRARY_FILE_NAME, libraryTableModel);
            saveToFile(BOOKSHELF_FILE_NAME, bookshelfTableModel);
        });

        // Search functionality
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = searchField.getText().toLowerCase();
                filterTable(libraryTable, libraryTableModel, query);
                filterTable(bookshelfTable, bookshelfTableModel, query);
            }
        });

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        pack();
        setLocationRelativeTo(null);
    }

    private void filterTable(JTable table, DefaultTableModel model, String query) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query, 0)); // Filter only on the Title column (index 0)
    }


    private void saveToFile(String fileName, DefaultTableModel tableModel) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    writer.print(tableModel.getValueAt(row, col));
                    if (col < tableModel.getColumnCount() - 1) {
                        writer.print(",");
                    }
                }
                writer.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromFile(String fileName, DefaultTableModel tableModel) {
        File file = new File(fileName);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] rowData = line.split(",");
                    tableModel.addRow(rowData);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}
