import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;

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

        JTable libraryTable = new JTable(libraryTableModel);
        JScrollPane libraryScrollPane = new JScrollPane(libraryTable);
        getContentPane().add(libraryScrollPane, BorderLayout.SOUTH);

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
