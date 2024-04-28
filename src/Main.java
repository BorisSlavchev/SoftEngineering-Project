import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Main extends JFrame {
    // Declare GUI components from the generated code
    private JPanel panel;
    private JTextField searchField;
    private JTable bookshelfTable;
    private JTable libraryTable;
    private JButton btnMoveToBookshelf;
    private JButton btnReturnToLibrary;
    private JButton btnSaveEdits;
    private JLabel search;

    private DefaultTableModel libraryTableModel;
    private DefaultTableModel bookshelfTableModel;

    private final String LIBRARY_FILE_NAME = "library_books.txt";
    private final String BOOKSHELF_FILE_NAME = "bookshelf_books.txt";

    public Main() {
        // Initialize GUI components from the generated code
        initComponents();

        // Initialize table models
        libraryTableModel = (DefaultTableModel) libraryTable.getModel();
        bookshelfTableModel = (DefaultTableModel) bookshelfTable.getModel();

        // Load data from files
        loadFromFile(LIBRARY_FILE_NAME, libraryTableModel);
        loadFromFile(BOOKSHELF_FILE_NAME, bookshelfTableModel);
    }
    public class BookInfoDialog extends JDialog {
        public BookInfoDialog(JFrame parent, ImageIcon image, String title, String author, String year) {
            super(parent, "Book Information", true); // Set the title of the dialog
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            BookPanel bookPanel = new BookPanel((Main) parent, image, title, author, year);
            JPanel contentPane = new JPanel();
            contentPane.add(bookPanel);
            setContentPane(contentPane);
            pack();
            setLocationRelativeTo(parent);
            setVisible(true);
        }
    }

    public void refreshLibraryTable() {
        libraryTableModel.fireTableDataChanged();
    }
    public void refreshBookshelfTable() {
        bookshelfTableModel.fireTableDataChanged();
    }
    private void initComponents() {

        // Add the generated panel to the JFrame
        getContentPane().add(panel, BorderLayout.NORTH);

        btnSaveEdits.addActionListener(e -> {
            saveToFile(LIBRARY_FILE_NAME, libraryTableModel);
            saveToFile(BOOKSHELF_FILE_NAME, bookshelfTableModel);
        });

        // Initialize table models
        bookshelfTableModel = new NonEditableTableModel();
        bookshelfTableModel.addColumn("Title");
        bookshelfTableModel.addColumn("Author");
        bookshelfTableModel.addColumn("Year Of Publications");
        bookshelfTableModel.addColumn("ISBN");
        bookshelfTableModel.addColumn("Current Pages");
        bookshelfTableModel.addColumn("Total Pages");

        libraryTableModel = new NonEditableTableModel();
        libraryTableModel.addColumn("Library:   Title");
        libraryTableModel.addColumn("Author");
        libraryTableModel.addColumn("Year Of Publications");
        libraryTableModel.addColumn("ISBN");
        libraryTableModel.addColumn("Current Pages");
        libraryTableModel.addColumn("Total Pages");

        // Initialize bookshelf table
        bookshelfTable = new JTable(bookshelfTableModel);
        bookshelfTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane bookshelfScrollPane = new JScrollPane(bookshelfTable);
        getContentPane().add(bookshelfScrollPane, BorderLayout.CENTER);



        // Add mouse listener for bookshelf table
        bookshelfTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = bookshelfTable.rowAtPoint(e.getPoint());
                int col = bookshelfTable.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 0) { // Assuming titles are in the first column
                    String title = (String) bookshelfTableModel.getValueAt(row, col);
                    String author = (String) bookshelfTableModel.getValueAt(row, 1);
                    String year = (String) bookshelfTableModel.getValueAt(row, 2);
                    ImageIcon image = new ImageIcon("covers/" + title.replaceAll("\\s", "").toLowerCase() + ".jpg");
                    BookPanel bookPanel = new BookPanel(Main.this, image, title, author, year);
                    new BookInfoDialog(Main.this, image, title, author, year);
                }
            }
        });
        // Initialize library table
        libraryTable = new JTable(libraryTableModel);
        libraryTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane libraryScrollPane = new JScrollPane(libraryTable);
        getContentPane().add(libraryScrollPane, BorderLayout.SOUTH);

        libraryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = libraryTable.rowAtPoint(e.getPoint());
                int col = libraryTable.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 0) { // Assuming titles are in the first column
                    String title = (String) libraryTableModel.getValueAt(row, col);
                    String author = (String) libraryTableModel.getValueAt(row, 1);
                    String year = (String) libraryTableModel.getValueAt(row, 2);
                    ImageIcon image = new ImageIcon("covers/" + title.replaceAll("\\s", "").toLowerCase() + ".jpg");
                    BookPanel bookPanel = new BookPanel(Main.this,image, title, author, year);
                    new BookInfoDialog(Main.this, image, title, author, year);
                }
            }
        });


        // Add action listeners to buttons
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
            refreshBookshelfTable();
            refreshLibraryTable();
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
                refreshBookshelfTable();
                refreshLibraryTable();
            }
            libraryTable.repaint();
            bookshelfTable.repaint();
            saveToFile(LIBRARY_FILE_NAME, libraryTableModel);
            saveToFile(BOOKSHELF_FILE_NAME, bookshelfTableModel);
        });

        // Add key listener to search field
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = searchField.getText().toLowerCase();
                filterTable(libraryTable, libraryTableModel, query);
                filterTable(bookshelfTable, bookshelfTableModel, query);
            }
        });



        // Set frame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("LibTrack");
        setPreferredSize(new Dimension(1920, 800));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false); // Add this line to make the window not resizable
        pack();
        setLocationRelativeTo(null);
    }
    public JTable getBookshelfTable() {
        return bookshelfTable;
    }
    public JTable getLibraryTable() {
        return libraryTable;
    }
    public DefaultTableModel getLibraryTableModel() {
        return libraryTableModel;
    }
    public DefaultTableModel getBookshelfTableModel() {
        return bookshelfTableModel;
    }
    public JButton getBtnMoveToBookshelf() {
        return btnMoveToBookshelf;
    }
    public JButton getBtnReturnToLibrary() {
        return btnReturnToLibrary;
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

    public static void initializeAndShowGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
    public static void main(String[] args) {
        initializeAndShowGUI();
    }

    private class NonEditableTableModel extends DefaultTableModel {
        @Override
        public boolean isCellEditable(int row, int column) {
            // Make all cells non-editable except the "Current Pages" column
            return column == getColumnCount() - 2; // Assuming "Current Pages" is the second last column
        }
        public void setValueAt(Object aValue, int row, int column) {
            // Check if the column being edited is "Current Pages"
            if (column == getColumnCount() - 2) {
                int totalPages = Integer.parseInt(getValueAt(row, getColumnCount() - 1).toString()); // Assuming Total Pages is the last column
                int currentPages = Integer.parseInt(aValue.toString());
                if (currentPages <= totalPages) {
                    super.setValueAt(aValue, row, column);
                } else {
                    JOptionPane.showMessageDialog(null, "Current Pages cannot be greater than Total Pages", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                super.setValueAt(aValue, row, column);
            }
        }
    }
}
