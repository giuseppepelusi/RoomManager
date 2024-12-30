package views;

import controllers.ReservationManager;
import controllers.FileManager;
import models.reservation.Reservation;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.File;

public class MainWindow extends JFrame {
    private final ReservationManager reservationManager;
    private final FileManager fileManager;
    private final RoomTableView tableView;
    private final JLabel dateLabel;
    private LocalDate currentDate;

    public MainWindow(ReservationManager reservationManager, FileManager fileManager) {
        this.reservationManager = reservationManager;
        this.fileManager = fileManager;
        this.currentDate = LocalDate.now();

        setTitle("Room Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        // Create main components
        tableView = new RoomTableView(reservationManager);
        dateLabel = new JLabel(currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // Create menu bar
        setJMenuBar(createMenuBar());

        // Create main layout
        setLayout(new BorderLayout());
        add(createToolBar(), BorderLayout.NORTH);
        add(new JScrollPane(tableView), BorderLayout.CENTER);

        updateTable();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
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
        
        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem addItem = new JMenuItem("Add Reservation");
        addItem.addActionListener(e -> showAddReservationDialog());
        JMenuItem removeItem = new JMenuItem("Remove Reservation");
        removeItem.addActionListener(e -> removeSelectedReservation());
        editMenu.add(addItem);
        editMenu.add(removeItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        
        return menuBar;
    }

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

    private void showAddReservationDialog() {
        ReservationDialog dialog = new ReservationDialog(this, reservationManager, currentDate);
        dialog.setVisible(true);
        if (dialog.getReservation() != null) {
            updateTable();
        }
    }

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
                JOptionPane.showMessageDialog(this, "Reservation removed successfully!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No reservation selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveReservations() {
        JFileChooser fileChooser = new JFileChooser();
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

    private void loadReservations() {
        JFileChooser fileChooser = new JFileChooser();
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

    private void updateTable() {
        tableView.updateData(currentDate);
        dateLabel.setText(currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    private void changeDate(int days) {
        currentDate = currentDate.plusDays(days);
        updateTable();
    }
}