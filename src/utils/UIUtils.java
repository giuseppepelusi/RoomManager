package utils;

import javax.swing.*;
import java.awt.*;

/**
 * Utility class for UI operations.
 */
public class UIUtils {
    /**
     * Sets the look and feel to the system's look and feel.
     */
    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows an error message dialog.
     *
     * @param parent the parent component
     * @param message the error message
     */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows an information message dialog.
     *
     * @param parent the parent component
     * @param message the information message
     */
    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
            message,
            "Information",
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a confirmation dialog.
     *
     * @param parent the parent component
     * @param message the confirmation message
     * @return true if the user confirmed, false otherwise
     */
    public static boolean showConfirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent,
            message,
            "Confirm",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /**
     * Creates a styled button with the specified text and background color.
     *
     * @param text the button text
     * @param backgroundColor the background color
     * @return the styled button
     */
    public static JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        return button;
    }
}