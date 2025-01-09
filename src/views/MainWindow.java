package views;

import controllers.ReservationManager;
import controllers.FileManager;
import models.reservation.Reservation;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.MaskFormatter;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.io.File;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The main window of the Room Manager application.
 */
public class MainWindow extends JFrame {
    private final ReservationManager reservationManager;
    private final FileManager fileManager;
    private final RoomTableView tableView;
    private final JLabel dateLabel;
    private LocalDate currentDate;

    /**
     * Constructs a MainWindow.
     *
     * @param reservationManager the reservation manager
     * @param fileManager the file manager
     */
    public MainWindow(ReservationManager reservationManager, FileManager fileManager) {
        this.reservationManager = reservationManager;
        this.fileManager = fileManager;
        this.currentDate = LocalDate.now();

        setTitle("Room Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        tableView = new RoomTableView(reservationManager);
        dateLabel = new JLabel(currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dateLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editDate();
                }
            }
        });
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                getContentPane().requestFocusInWindow();
            }
        });

        setJMenuBar(createMenuBar());
        setLayout(new BorderLayout());
        add(createToolBar(), BorderLayout.NORTH);
        add(new JScrollPane(tableView), BorderLayout.CENTER);

        updateTable();
    }

    /**
     * Creates the menu bar.
     *
     * @return the menu bar
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save Reservations");
        saveItem.addActionListener(e -> saveReservations());
        JMenuItem loadItem = new JMenuItem("Load Reservations");
        loadItem.addActionListener(e -> loadReservations());
        JMenuItem printItem = new JMenuItem("Print Table");
        printItem.addActionListener(e -> printTable());
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(printItem);
        
        JMenu editMenu = new JMenu("Edit");
        JMenuItem addItem = new JMenuItem("Add Reservation");
        addItem.addActionListener(e -> showAddReservationDialog());
        JMenuItem editItem = new JMenuItem("Edit Reservation");
        editItem.addActionListener(e -> showEditReservationDialog());
        JMenuItem removeItem = new JMenuItem("Remove Reservation");
        removeItem.addActionListener(e -> removeSelectedReservation());
        editMenu.add(addItem);
        editMenu.add(editItem);
        editMenu.add(removeItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        
        return menuBar;
    }

    /**
     * Creates the tool bar.
     *
     * @return the tool bar
     */
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton addButton = new JButton("Add Reservation");
        addButton.addActionListener(e -> showAddReservationDialog());

        JButton editButton = new JButton("Edit Reservation");
        editButton.addActionListener(e -> showEditReservationDialog());

        JButton removeButton = new JButton("Remove Reservation");
        removeButton.addActionListener(e -> removeSelectedReservation());

        JButton prevDay = new JButton("←");
        prevDay.addActionListener(e -> changeDate(-1));
        JButton nextDay = new JButton("→");
        nextDay.addActionListener(e -> changeDate(1));

        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(removeButton);
        toolBar.addSeparator();
        toolBar.add(prevDay);
        toolBar.add(dateLabel);
        toolBar.add(nextDay);

        return toolBar;
    }

    /**
     * Shows the dialog to add a new reservation.
     */
    private void showAddReservationDialog() {
        ReservationDialog dialog = new ReservationDialog(this, reservationManager, currentDate);
        dialog.setVisible(true);
        if (dialog.getReservation() != null) {
            updateTable();
        }
    }

    /**
     * Shows the dialog to edit the selected reservation.
     */
    private void showEditReservationDialog() {
        Reservation selectedReservation = tableView.getSelectedReservation();
        if (selectedReservation != null) {
            ReservationDialog dialog = new ReservationDialog(this, reservationManager, selectedReservation);
            dialog.setVisible(true);
            if (dialog.getReservation() != null) {
                updateTable();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No reservation selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Removes the selected reservation.
     */
    private void removeSelectedReservation() {
        Reservation selectedReservation = tableView.getSelectedReservation();
        if (selectedReservation != null) {
            int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove the selected reservation?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                reservationManager.removeReservation(selectedReservation);
                updateTable();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No reservation selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Saves the reservations to a file.
     */
    private void saveReservations() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Reservation Files (*.resv)", "resv"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file.exists()) {
                int response = JOptionPane.showConfirmDialog(this,
                    "File exists. Do you want to overwrite?",
                    "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION);
                if (response != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            try {
                fileManager.saveReservations(file.getPath(), 
                    reservationManager.getAllReservations());
                JOptionPane.showMessageDialog(this, "Reservations saved successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error saving reservations: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Loads the reservations from a file.
     */
    private void loadReservations() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Reservation Files (*.resv)", "resv"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                reservationManager.loadReservations(file.getPath());
                updateTable();
                JOptionPane.showMessageDialog(this, "Reservations loaded successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error loading reservations: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Prints the table.
     */
    private void printTable() {
        try {
            tableView.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error printing table: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates the table with the current date's reservations.
     */
    private void updateTable() {
        tableView.updateData(currentDate);
        dateLabel.setText(currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    /**
     * Changes the current date by a specified number of days.
     *
     * @param days the number of days to change the date by
     */
    private void changeDate(int days) {
        currentDate = currentDate.plusDays(days);
        updateTable();
    }

    /**
     * Edits the current date.
     */
    private void editDate() {
        JFormattedTextField dateField = createFormattedDateField();
        dateField.setValue(currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dateField.requestFocusInWindow();

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Enter new date (dd/MM/yyyy):"), BorderLayout.NORTH);
        panel.add(dateField, BorderLayout.CENTER);

        JDialog dialog = new JDialog(this, "Edit Date", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            try {
                String newDateStr = dateField.getText();
                LocalDate newDate = LocalDate.parse(newDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                currentDate = newDate;
                updateTable();
                dialog.dispose();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please enter the date in dd/MM/yyyy format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Creates a formatted date field.
     *
     * @return the formatted date field
     */
    private JFormattedTextField createFormattedDateField() {
        try {
            MaskFormatter dateFormatter = new MaskFormatter("##/##/####");
            dateFormatter.setPlaceholderCharacter(' ');
            return new JFormattedTextField(dateFormatter);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to create date formatter", e);
        }
    }
}