package views;

import controllers.ReservationManager;
import controllers.FileManager;
import models.reservation.Reservation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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
        setSize(1000, 600);
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
        add(createStatusBar(), BorderLayout.SOUTH);

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
        editMenu.add(addItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        
        return menuBar;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton addButton = new JButton("Add Reservation");
        addButton.addActionListener(e -> showAddReservationDialog());

        JButton prevDay = new JButton("←");
        prevDay.addActionListener(e -> changeDate(-1));
        JButton nextDay = new JButton("→");
        nextDay.addActionListener(e -> changeDate(1));

        toolBar.add(addButton);
        toolBar.addSeparator();
        toolBar.add(prevDay);
        toolBar.add(dateLabel);
        toolBar.add(nextDay);

        return toolBar;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        JLabel statusLabel = new JLabel(" Ready");
        statusBar.add(statusLabel, BorderLayout.WEST);
        return statusBar;
    }

    private void showAddReservationDialog() {
        ReservationDialog dialog = new ReservationDialog(this, reservationManager, currentDate);
        dialog.setVisible(true);
        if (dialog.getReservation() != null) {
            updateTable();
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