import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class PinCode {
    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        File file = new File("pin.txt");
        //Checks whether or not there is a txt file
        //If there is, then the user will only be asked to input their pin
        //If there isn't the user would be asked to set up a pin
        if(file.exists() && !file.isDirectory()){
            System.out.println("File exists");
            checkPin(file, jFrame);
        }else{
            System.out.println("File doesn't exist");
            setPin(jFrame);
        }
    }

    public static void setPin(JFrame jFrame){
        boolean pinSet = false;
        //Loop repeats until the user finally sets a pin
        while(!pinSet){
            String input =  JOptionPane.showInputDialog(jFrame, "Set your pin");
            //Mainly to check if the user pressed X
            if(input == null){
                System.exit(0);
            }
            //Checks whether or not the value is empty
            if(input.equals("") || input.equals(" ")){
                JOptionPane.showMessageDialog(jFrame, "Please input a value");
            }else{
                try{
                    int getPin = Integer.parseInt(input);
                    //Length of the pin
                    int length = (int)(Math.log10(getPin)+1);
                    System.out.println("Length: " + length);
                    //Length should be in between 4-8 digits
                    if(length < 4 || length > 8){
                        JOptionPane.showMessageDialog(jFrame, "Number should be in between 4-8 digits");
                    }else{
                        FileWriter fileWriter = new FileWriter("pin.txt");
                        fileWriter.write(input);
                        fileWriter.close();
                        System.out.println("Pin set");
                        pinSet = true;
                        //The system exit was only for the code to close quickly, you can remove it
                        System.exit(0);
                    }
                    //Check whether or not the number inputted is an integer or not
                }catch(NumberFormatException e){
                    JOptionPane.showMessageDialog(jFrame, "Please input integers");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void checkPin(File file, JFrame jFrame){
        int pinCode = 0;
        try{
            Scanner scanner = new Scanner(file);
            //Reads the pin in the file
            while(scanner.hasNextLine()){
                String pin = scanner.nextLine();
                pinCode = Integer.parseInt(pin);
                System.out.println(pin);
            }
            scanner.close();
            boolean pinEntered = false;
            int tries = 0;
            while(!pinEntered) {
                String input = JOptionPane.showInputDialog(jFrame, "Input your pin");
                //Check if the user pressed X
                if(input == null){
                    System.exit(0);
                }
                //There has to be a value inputted
                if (input.equals("") || input.equals(" ")) {
                    JOptionPane.showMessageDialog(jFrame, "Please input a value");
                } else {
                    try {
                        int getPin = Integer.parseInt(input);
                        int length = (int) (Math.log10(getPin) + 1);
                        //Number has to be in between 4 to 8 digits
                        if (length < 4 || length > 8) {
                            JOptionPane.showMessageDialog(jFrame, "Number should be in between 4-8 digits");
                        } else {
                            //Checks if there has been 3 attempts (it says 2 but the popup comes up 3 times)
                            //Also checks whether the final attempt does not equal the pin in the file
                            if(tries >= 2 && getPin != pinCode){
                                JOptionPane.showMessageDialog(jFrame, "Too many tries");
                                pinEntered = true;
                                //The system exit was only for the code to close quickly, you can remove it
                                System.exit(0);
                            }else{
                                if(getPin == pinCode){
                                    JOptionPane.showMessageDialog(jFrame, "Unlocked");
                                    pinEntered = true;
                                    //The system exit was only for the code to close quickly, you can remove it
                                    System.exit(0);
                                }else{
                                    JOptionPane.showMessageDialog(jFrame, "Wrong pin");
                                    tries++;
                                }
                            }
                        }
                        //Check if the number is an integer or not
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(jFrame, "Please input integers");
                    }
                }
            }
        }catch(IOException e){
            System.out.println("File error");
            e.printStackTrace();
        }
    }
}