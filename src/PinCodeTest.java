import javax.swing.*;
import org.junit.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

public class PinCodeTest {

    @Test
    public void testSetPin() {
        // Create a temporary file for testing
        File tempFile;
        try {
            tempFile = File.createTempFile("pin", ".txt");
            tempFile.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Mock user input
        String input = "1234";
        MockUserInput.setInput(input);

        // Mock JOptionPane
        MockJOptionPane mockJOptionPane = new MockJOptionPane();
        mockJOptionPane.setExpectedMessage("Set your pin");

        // Call the method to be tested
        LibTrack.setPin(mockJOptionPane.getJFrame());

        // Verify that the pin is set correctly
        try {
            Scanner scanner = new Scanner(tempFile);
            String encryptedPin = scanner.nextLine();
            String decryptedPin = LibTrack.decrypt(encryptedPin);
            assertEquals(input, decryptedPin);
            scanner.close();
            System.out.println("everything is fine");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCheckPin() {
        // Create a temporary file for testing
        File tempFile;
        try {
            tempFile = File.createTempFile("pin", ".txt");
            FileWriter fileWriter = new FileWriter(tempFile);
            String encryptedPin = LibTrack.encrypt("1234");
            fileWriter.write(encryptedPin);
            fileWriter.close();
            tempFile.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Mock user input
        String input = "1234";
        MockUserInput.setInput(input);

        // Mock JOptionPane
        MockJOptionPane mockJOptionPane = new MockJOptionPane();
        mockJOptionPane.setExpectedMessage("Input your pin");

        // Call the method to be tested
        LibTrack.checkPin(tempFile, mockJOptionPane.getJFrame());

        // Verify that the pin is checked correctly
        assertTrue(mockJOptionPane.isMessageShown("Unlocked"));
        System.out.println("everything is fine");
    }

    // Helper class to mock user input
    static class MockUserInput {
        private static String input;

        public static void setInput(String input) {
            MockUserInput.input = input;
        }

        public static String getInput() {
            return input;
        }
    }

    // Helper class to mock JOptionPane
    static class MockJOptionPane {
        private String expectedMessage;
        private boolean messageShown;

        public void setExpectedMessage(String expectedMessage) {
            this.expectedMessage = expectedMessage;
        }

        public JFrame getJFrame() {
            return new JFrame();
        }

        public void showMessageDialog(JFrame jFrame, String message) {
            if (expectedMessage != null && expectedMessage.equals(message)) {
                messageShown = true;
            }
        }

        public String showInputDialog(JFrame jFrame, String message) {
            if (expectedMessage != null && expectedMessage.equals(message)) {
                return MockUserInput.getInput();
            }
            return null;
        }

        public boolean isMessageShown(String message) {
            return messageShown;
        }
    }
}