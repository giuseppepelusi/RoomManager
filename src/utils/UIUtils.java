package utils;

import javax.swing.*;
import java.awt.*;

public class UIUtils {
    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
            message,
            "Information",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean showConfirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent,
            message,
            "Confirm",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }

//    public static void addTableStripes(JTable table) {
//        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                Component c = super.getTableCellRendererComponent(table, value,
//                        isSelected, hasFocus, row, column);
//                if (!isSelected) {
//                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
//                }
//                return c;
//            }
//        });
//    }
}