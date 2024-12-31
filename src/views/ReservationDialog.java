package views;

import controllers.ReservationManager;
import models.reservation.*;
import models.room.*;
import utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservationDialog extends JDialog {
    private final ReservationManager manager;
    private Reservation reservation;
    private LocalDate date;

    private JComboBox<Room> roomCombo;
    private JComboBox<LocalTime> startTimeCombo;
    private JComboBox<LocalTime> endTimeCombo;
    private JTextField nameField;
    private JComboBox<ReservationType> typeCombo;
    private JPanel featurePanel;
    private JCheckBox whiteboardCheckBox;
    private JCheckBox projectorCheckBox;
    private JCheckBox pcsCheckBox;
    private JCheckBox outletsCheckBox;
    private JButton okButton;

    public ReservationDialog(Frame owner, ReservationManager manager, LocalDate date) {
        this(owner, manager, date, "New Reservation");
    }

    public ReservationDialog(Frame owner, ReservationManager manager, LocalDate date, String title) {
        super(owner, title, true);
        this.manager = manager;
        this.date = date;

        setSize(400, 380);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        add(createMainPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        updateOkButtonState();
    }

    public ReservationDialog(Frame owner, ReservationManager manager, Reservation reservation) {
        this(owner, manager, reservation.getDate(), "Edit Reservation");
        this.reservation = reservation;

        roomCombo.setSelectedItem(reservation.getRoom());
        roomCombo.setEnabled(false); // Disable room selection for editing

        startTimeCombo.setSelectedItem(reservation.getStartTime());
        endTimeCombo.setSelectedItem(reservation.getEndTime());
        nameField.setText(reservation.getReservedBy());
        typeCombo.setSelectedItem(reservation.getType());

        updateOkButtonState();
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
        roomCombo = new JComboBox<>();
        roomCombo.addItem(null); // Add placeholder item
        manager.getAllRooms().forEach(roomCombo::addItem);
        roomCombo.addActionListener(e -> {
            updateRoomFeatures();
            updateOkButtonState();
        });
        panel.add(roomCombo, gbc);

        // Room features
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        featurePanel = new JPanel(new GridLayout(2, 2));
        whiteboardCheckBox = createFeatureCheckBox("Whiteboard");
        projectorCheckBox = createFeatureCheckBox("Projector");
        pcsCheckBox = createFeatureCheckBox("PCs");
        outletsCheckBox = createFeatureCheckBox("Electrical Outlets");
        featurePanel.add(whiteboardCheckBox);
        featurePanel.add(projectorCheckBox);
        featurePanel.add(pcsCheckBox);
        featurePanel.add(outletsCheckBox);
        panel.add(featurePanel, gbc);
        gbc.gridwidth = 1;

        // Start time
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Start Time:"), gbc);
        gbc.gridx = 1;
        startTimeCombo = new JComboBox<>();
        startTimeCombo.addActionListener(e -> {
            updateEndTimeCombo();
            updateOkButtonState();
        });
        panel.add(startTimeCombo, gbc);

        // End time
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("End Time:"), gbc);
        gbc.gridx = 1;
        endTimeCombo = new JComboBox<>();
        endTimeCombo.addActionListener(e -> updateOkButtonState());
        panel.add(endTimeCombo, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Reserved By:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        nameField.getDocument().addDocumentListener(new SimpleDocumentListener(this::updateOkButtonState));
        panel.add(nameField, gbc);

        // Type
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(ReservationType.values());
        typeCombo.addActionListener(e -> updateOkButtonState());
        panel.add(typeCombo, gbc);

        return panel;
    }

    private JCheckBox createFeatureCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setEnabled(false);
        return checkBox;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        okButton = new JButton("OK");
        okButton.addActionListener(e -> createReservation());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        panel.add(okButton);
        panel.add(cancelButton);

        return panel;
    }

    private void updateRoomFeatures() {
        Room selectedRoom = (Room) roomCombo.getSelectedItem();
        if (selectedRoom instanceof Classroom classroom) {
            whiteboardCheckBox.setVisible(true);
            projectorCheckBox.setVisible(true);
            pcsCheckBox.setVisible(false);
            outletsCheckBox.setVisible(false);
            whiteboardCheckBox.setSelected(classroom.hasWhiteboard());
            projectorCheckBox.setSelected(classroom.hasProjector());
        } else if (selectedRoom instanceof Laboratory laboratory) {
            whiteboardCheckBox.setVisible(false);
            projectorCheckBox.setVisible(false);
            pcsCheckBox.setVisible(true);
            outletsCheckBox.setVisible(true);
            pcsCheckBox.setSelected(laboratory.hasPCs());
            outletsCheckBox.setSelected(laboratory.hasElectricalOutlets());
        } else {
            whiteboardCheckBox.setVisible(false);
            projectorCheckBox.setVisible(false);
            pcsCheckBox.setVisible(false);
            outletsCheckBox.setVisible(false);
        }
        updateStartTimeCombo();
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
                    !endTime.isAfter(startTime.plusHours(maxDuration))) {
                endTimeCombo.addItem(endTime);
                endTime = endTime.plusHours(increment);
            }

            // Ensure at least one end time is available
            if (endTimeCombo.getItemCount() == 0) {
                endTimeCombo.addItem(LocalTime.of(18, 0));
            }
        }
    }

    private void createReservation() {
        Room room = (Room) roomCombo.getSelectedItem();
        LocalTime startTime = (LocalTime) startTimeCombo.getSelectedItem();
        LocalTime endTime = (LocalTime) endTimeCombo.getSelectedItem();
        String reservedBy = nameField.getText();
        ReservationType type = (ReservationType) typeCombo.getSelectedItem();

        // Validate reservation
        List<Reservation> existingReservations = manager.getReservationsForDate(date);
        ValidationUtils.ValidationResult validationResult = ValidationUtils.validateReservationTime(room, startTime, endTime, date);
        if (!validationResult.isValid()) {
            JOptionPane.showMessageDialog(this, validationResult.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        validationResult = ValidationUtils.validateReservedBy(reservedBy);
        if (!validationResult.isValid()) {
            JOptionPane.showMessageDialog(this, validationResult.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        validationResult = ValidationUtils.validateNoConflict(new Reservation(room, date, startTime, endTime, reservedBy, type), existingReservations, reservation);
        if (!validationResult.isValid()) {
            JOptionPane.showMessageDialog(this, validationResult.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (reservation == null) {
            reservation = new Reservation(room, date, startTime, endTime, reservedBy, type);
            if (manager.addReservation(reservation)) {
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add reservation. Please check the details and try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            reservation.setDate(date);
            reservation.setStartTime(startTime);
            reservation.setEndTime(endTime);
            reservation.setReservedBy(reservedBy);
            reservation.setType(type);
            dispose();
        }
    }

    public Reservation getReservation() {
        return reservation;
    }

    private void updateOkButtonState() {
        boolean allFieldsFilled = roomCombo.getSelectedItem() != null &&
                                  startTimeCombo.getSelectedItem() != null &&
                                  endTimeCombo.getSelectedItem() != null &&
                                  !nameField.getText().trim().isEmpty() &&
                                  typeCombo.getSelectedItem() != null;
        okButton.setEnabled(allFieldsFilled);
    }

    private static class SimpleDocumentListener implements javax.swing.event.DocumentListener {
        private final Runnable onChange;

        public SimpleDocumentListener(Runnable onChange) {
            this.onChange = onChange;
        }

        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            onChange.run();
        }

        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            onChange.run();
        }

        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            onChange.run();
        }
    }
}