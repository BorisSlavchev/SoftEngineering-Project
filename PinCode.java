import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.Scanner;

public class PinCode {
    // AES encryption/decryption key
    private static final String AES_KEY = "0123456789abcdef"; // 16 characters for 128-bit key

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        File file = new File("pin.txt");
        //Checks whether or not there is a txt file
        //If there is, then the user will only be asked to input their pin
        //If there isn't the user would be asked to set up a pin
        if (file.exists() && !file.isDirectory()) {
            System.out.println("File exists");
            checkPin(file, jFrame);
        } else {
            System.out.println("File doesn't exist");
            setPin(jFrame);
        }
    }

    public static void setPin(JFrame jFrame) {
        boolean pinSet = false;
        //Loop repeats until the user finally sets a pin
        while(!pinSet) {
            String input = JOptionPane.showInputDialog(jFrame, "Set your pin");
            //Mainly to check if the user pressed X
            if (input == null) {
                System.exit(0);
            }
            //Checks whether or not the value is empty
            if (input.trim().isEmpty()) {
                JOptionPane.showMessageDialog(jFrame, "Please input a value");
            } else {
                try{
                    //Length of the pin
                    int length = input.length();
                    System.out.println("Length: " + length);
                    //Length should be in between 4-8 digits
                    if(length < 4 || length > 8){
                        JOptionPane.showMessageDialog(jFrame, "Number should be in between 4-8 digits");
                    }else{
                        // Encrypt the pin before writing to file
                        String encryptedPin = encrypt(input);
                        FileWriter fileWriter = new FileWriter("pin.txt");
                        fileWriter.write(encryptedPin);
                        fileWriter.close();
                        System.out.println("Pin set");
                        pinSet = true;
                        //The system exit was only for the code to close quickly, you can remove it
                        System.exit(0);
                    }
                    //Check whether or not the number inputted is an integer or not
                } catch(NumberFormatException e){
                    JOptionPane.showMessageDialog(jFrame, "Please input integers");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void checkPin(File file, JFrame jFrame) {
        String pinCode = "";
        try {
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()) {
                String encryptedPin = scanner.nextLine();
                // Decrypt the pin read from file
                String decryptedPin = decrypt(encryptedPin);
                pinCode = decryptedPin;
                System.out.println(decryptedPin);
            }
            scanner.close();
            boolean pinEntered = false;
            int tries = 0;
            while(!pinEntered) {
                String input = JOptionPane.showInputDialog(jFrame, "Input your pin");
                if(input == null){
                    System.exit(0);
                }
                if (input.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(jFrame, "Please input a value");
                } else {
                    try {
                        int length = input.length();
                        if (length < 4 || length > 8) {
                            JOptionPane.showMessageDialog(jFrame, "Number should be in between 4-8 digits");
                        } else {
                            if (tries >= 2 && !(input.equals(pinCode))){
                                JOptionPane.showMessageDialog(jFrame, "Too many tries");
                                pinEntered = true;
                                System.exit(0);
                            } else {
                                if(input.equals(pinCode)){
                                    JOptionPane.showMessageDialog(jFrame, "Unlocked");
                                    pinEntered = true;
                                    System.exit(0);
                                }else{
                                    JOptionPane.showMessageDialog(jFrame, "Wrong pin");
                                    tries++;
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(jFrame, "Please input integers");
                    }
                }
            }
        } catch(IOException e) {
            System.out.println("File error");
            e.printStackTrace();
        }
    }

    // AES encryption method
    public static String encrypt(String input) {
        try {
            // Create AES key spec from the key bytes
            SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes(), "AES");

            // Create AES cipher instance
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            // Initialize cipher in encryption mode with the key
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            // Encrypt the input text
            byte[] encryptedBytes = cipher.doFinal(input.getBytes());

            // Encode the encrypted bytes to Base64 for easy storage/transmission
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // AES decryption method
    public static String decrypt(String input) {
        try {
            // Create AES key spec from the key bytes
            SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes(), "AES");

            // Create AES cipher instance
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            // Initialize cipher in decryption mode with the set key
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            // Decode the input to get the encrypted bytes
            byte[] encryptedBytes = Base64.getDecoder().decode(input);

            // Decrypt the encrypted bytes
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // Convert the decrypted bytes to string
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}