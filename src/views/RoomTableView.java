package views;

import controllers.ReservationManager;
import models.reservation.Reservation;
import models.room.Room;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Table view for displaying room reservations.
 */
public class RoomTableView extends JTable {
    private final ReservationManager manager;
    private final RoomTableModel model;

    /**
     * Constructs a RoomTableView.
     *
     * @param manager the reservation manager
     */
    public RoomTableView(ReservationManager manager) {
        this.manager = manager;
        this.model = new RoomTableModel();
        setModel(model);
        
        setDefaultRenderer(Object.class, new ReservationCellRenderer());
        setRowHeight(50);
        getTableHeader().setReorderingAllowed(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setCellSelectionEnabled(true); // Enable cell selection instead of row selection
    }

    /**
     * Updates the table data for the specified date.
     *
     * @param date the date to update the table for
     */
    public void updateData(LocalDate date) {
        model.updateData(date);
    }

    /**
     * Gets the selected reservation.
     *
     * @return the selected reservation, or null if no reservation is selected
     */
    public Reservation getSelectedReservation() {
        int selectedRow = getSelectedRow();
        int selectedColumn = getSelectedColumn();
        if (selectedRow >= 0 && selectedColumn > 0) {
            return (Reservation) getValueAt(selectedRow, selectedColumn);
        }
        return null;
    }

    /**
     * Table model for the room table view.
     */
    private class RoomTableModel extends AbstractTableModel {
        private final List<Room> rooms;
        private LocalDate currentDate;
        private static final int HOURS = 10; // 8:00 to 18:00

        /**
         * Constructs a RoomTableModel.
         */
        public RoomTableModel() {
            this.rooms = manager.getAllRooms().stream().toList();
            this.currentDate = LocalDate.now();
        }

        /**
         * Updates the table data for the specified date.
         *
         * @param date the date to update the table for
         */
        public void updateData(LocalDate date) {
            this.currentDate = date;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return HOURS;
        }

        @Override
        public int getColumnCount() {
            return rooms.size() + 1; // +1 for time column
        }

        @Override
        public String getColumnName(int column) {
            if (column == 0) return "Time";
            return rooms.get(column - 1).getName();
        }

        @Override
        public Object getValueAt(int row, int column) {
            if (column == 0) {
                LocalTime startTime = LocalTime.of(row + 8, 0);
                LocalTime endTime = startTime.plusHours(1);
                return String.format("%02d:00 - %02d:00", startTime.getHour(), endTime.getHour());
            }

            Room room = rooms.get(column - 1);
            LocalTime time = LocalTime.of(row + 8, 0);
            List<Reservation> reservations = manager.getReservationsForDate(currentDate);

            return reservations.stream()
                .filter(r -> r.getRoom().equals(room))
                .filter(r -> isTimeInReservation(time, r))
                .findFirst()
                .orElse(null);
        }

        /**
         * Checks if a time is within a reservation.
         *
         * @param time the time to check
         * @param reservation the reservation
         * @return true if the time is within the reservation, false otherwise
         */
        private boolean isTimeInReservation(LocalTime time, Reservation reservation) {
            return !time.isBefore(reservation.getStartTime()) && 
                   time.isBefore(reservation.getEndTime());
        }
    }

    /**
     * Cell renderer for reservation cells.
     */
    private class ReservationCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);

            if (column == 0) {
                setHorizontalAlignment(JLabel.CENTER);
                setBackground(table.getBackground());
                setForeground(table.getForeground());
                return c;
            }

            if (value instanceof Reservation reservation) {
                setText(String.format("<html>%s<br>%s</html>",
                    reservation.getReservedBy(),
                    reservation.getType().getDisplayName()));
                setBackground(getReservationColor(reservation));
                setForeground(Color.DARK_GRAY); // Set text color to dark grey
                setHorizontalAlignment(JLabel.CENTER);
            } else {
                setText("");
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }

            return c;
        }

        /**
         * Gets the color for a reservation based on its type.
         *
         * @param reservation the reservation
         * @return the color for the reservation
         */
        private Color getReservationColor(Reservation reservation) {
            return switch (reservation.getType()) {
                case LESSON -> new Color(135, 206, 250); // Light Sky Blue
                case EXAM -> new Color(255, 160, 122);   // Light Salmon
                case CATCH_UP -> new Color(144, 238, 144); // Light Green
                default -> new Color(255, 255, 153);      // Light Yellow
            };
        }
    }
}