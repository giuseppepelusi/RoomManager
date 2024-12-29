package views;

import controllers.ReservationManager;
import models.reservation.*;
import models.room.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Vector;

public class ReservationDialog extends JDialog {
    private final ReservationManager manager;
    private Reservation reservation;
    private LocalDate date;

    private JComboBox<Room> roomCombo;
    private JComboBox<LocalTime> startTimeCombo;
    private JComboBox<LocalTime> endTimeCombo;
    private JTextField nameField;
    private JComboBox<ReservationType> typeCombo;

    public ReservationDialog(Frame owner, ReservationManager manager, LocalDate date) {
        super(owner, "New Reservation", true);
        this.manager = manager;
        this.date = date;

        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        add(createMainPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Room selection
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Room:"), gbc);
        gbc.gridx = 1;
        roomCombo = new JComboBox<>(new Vector<>(manager.getAllRooms()));
        roomCombo.addActionListener(e -> updateStartTimeCombo());
        panel.add(roomCombo, gbc);

        // Start time
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Start Time:"), gbc);
        gbc.gridx = 1;
        startTimeCombo = new JComboBox<>();
        startTimeCombo.addActionListener(e -> updateEndTimeCombo());
        panel.add(startTimeCombo, gbc);

        // End time
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("End Time:"), gbc);
        gbc.gridx = 1;
        endTimeCombo = new JComboBox<>();
        panel.add(endTimeCombo, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Reserved By:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);

        // Type
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(ReservationType.values());
        panel.add(typeCombo, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> createReservation());
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        
        panel.add(okButton);
        panel.add(cancelButton);
        
        return panel;
    }

    private void updateStartTimeCombo() {
        Room selectedRoom = (Room) roomCombo.getSelectedItem();
        startTimeCombo.removeAllItems();

        if (selectedRoom != null) {
            int maxStartHour = selectedRoom instanceof Laboratory ? 16 : 17;
            for (int hour = 8; hour <= maxStartHour; hour++) {
                startTimeCombo.addItem(LocalTime.of(hour, 0));
            }
            startTimeCombo.setSelectedItem(LocalTime.of(8, 0)); // Default to 8:00
        }

        updateEndTimeCombo();
    }

    private void updateEndTimeCombo() {
        Room selectedRoom = (Room) roomCombo.getSelectedItem();
        LocalTime startTime = (LocalTime) startTimeCombo.getSelectedItem();
        endTimeCombo.removeAllItems();

        if (selectedRoom != null && startTime != null) {
            int increment = selectedRoom.getMinReservationIncrement();
            int maxDuration = selectedRoom.getMaxReservationDuration();
            LocalTime endTime = startTime.plusHours(increment);

            while (!endTime.isAfter(LocalTime.of(18, 0)) && 
                   !endTime.isBefore(startTime) && 
                   endTime.isBefore(startTime.plusHours(maxDuration + 1))) {
                endTimeCombo.addItem(endTime);
                endTime = endTime.plusHours(increment);
            }

            // Ensure 18:00 is included if it is a valid end time
            if (endTimeCombo.getItemCount() > 0 && endTimeCombo.getItemAt(endTimeCombo.getItemCount() - 1).isBefore(LocalTime.of(18, 0))) {
                LocalTime lastEndTime = endTimeCombo.getItemAt(endTimeCombo.getItemCount() - 1);
                if (lastEndTime.plusHours(increment).equals(LocalTime.of(18, 0))) {
                    endTimeCombo.addItem(LocalTime.of(18, 0));
                }
            }

            // Remove 18:00 if it exceeds the max duration for classrooms
            if (selectedRoom instanceof Classroom && startTime.plusHours(maxDuration).isBefore(LocalTime.of(18, 0))) {
                endTimeCombo.removeItem(LocalTime.of(18, 0));
            }

            // Ensure end times are available for start times between 15:00 and 17:00 for classrooms
            if (selectedRoom instanceof Classroom) {
                LocalTime maxEndTime = startTime.plusHours(maxDuration);
                if (maxEndTime.isAfter(LocalTime.of(18, 0))) {
                    maxEndTime = LocalTime.of(18, 0);
                }
                endTimeCombo.removeAllItems();
                endTime = startTime.plusHours(increment);
                while (!endTime.isAfter(maxEndTime) && !endTime.isBefore(startTime)) {
                    endTimeCombo.addItem(endTime);
                    endTime = endTime.plusHours(increment);
                }
            }
        }
    }

    private void createReservation() {
        Room room = (Room) roomCombo.getSelectedItem();
        LocalTime startTime = (LocalTime) startTimeCombo.getSelectedItem();
        LocalTime endTime = (LocalTime) endTimeCombo.getSelectedItem();
        String reservedBy = nameField.getText();
        ReservationType type = (ReservationType) typeCombo.getSelectedItem();

        reservation = new Reservation(room, date, startTime, endTime, reservedBy, type);
        if (manager.addReservation(reservation)) {
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add reservation. Please check the details and try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Reservation getReservation() {
        return reservation;
    }
}