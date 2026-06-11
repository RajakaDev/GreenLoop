package serviceLayer;

import javax.swing.*;

public class EmailService {

    public static boolean sendEmail(String toEmail, String subject, String messageText) {

        JOptionPane.showMessageDialog(
                null,
                "Email Notification Sent\n\n" +
                        "To: " + toEmail + "\n" +
                        "Subject: " + subject + "\n\n" +
                        messageText
        );

        return true;
    }
}