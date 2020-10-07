import java.io.File;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class hashing {
    public static byte[] convertToSHA(String password) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("SHA-256");
        return m.digest(password.getBytes(StandardCharsets.UTF_8));
    }

    public static String convertBytesToHexString(byte[] hash) {
        BigInteger num = new BigInteger(1, hash);
        StringBuilder string = new StringBuilder(num.toString(16));
        while (string.length() < 32) {
            string.insert(0, '0');
        }
        return string.toString();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Do you want to\n1. Register\nor\n2. Login?");
        int inp = sc.nextInt();
        if (inp == 1) {
            boolean correctEmailCredentials = false;
            boolean correctPasswordCredentials = false;
            String regexForEmail = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
            String regexForPassword = "^(?=.*[0-9])" + "(?=.*[a-z])(?=.*[A-Z])" + "(?=.*[@#$%^&+=])"
                    + "(?=\\S+$).{8,20}$";
            String email = "", password = "";
            while (!correctEmailCredentials) {
                System.out.println("Enter email: ");
                email = sc.next();
                if (email.matches(regexForEmail))
                    correctEmailCredentials = true;
                else
                    System.out.println("Incorrect email");
            }
            while (!correctPasswordCredentials) {
                System.out.println(
                        "Enter password. It should atleast contain: \nOne uppercase \nOne lowercase \nOne numerical\nOne special character. \nIt should contain at least 8 characters and at most 20 characters.");
                password = sc.next();
                if (password.matches(regexForPassword))
                    correctPasswordCredentials = true;
                else
                    System.out.println("Incorrect password");
            }
            System.out.println("Correct credentials entered. Registering user!");
            System.out.println("Hashing the password!");
            long salt = Math.round(Math.random() * 2000 + 1000);
            System.out.println("Salt value is " + salt);
            String updatedPassword = password + String.valueOf(salt);
            try {
                String hashedPassword = convertBytesToHexString(convertToSHA(updatedPassword));
                System.out.println("Hashed Password: " + hashedPassword);
                File file = new File("data.txt");
                if (!file.exists())
                    file.createNewFile();
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                while(randomAccessFile.getFilePointer()<randomAccessFile.length()){
                    System.out.println(randomAccessFile.readLine());
                }
                randomAccessFile.writeBytes(email);
                randomAccessFile.writeBytes("!");
                randomAccessFile.writeBytes(String.valueOf(salt));
                randomAccessFile.writeBytes("$");
                randomAccessFile.writeBytes(hashedPassword);
                randomAccessFile.writeBytes(System.lineSeparator());
                randomAccessFile.close();
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        } else if (inp == 2) {
            System.out.println("Enter email: ");
            String email = sc.next();
            long salt = 0;
            String hashPassword = "";
            boolean isEmailFound = false;
            try {
                File file = new File("data.txt");
                if (!file.exists())
                    file.createNewFile();
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                while(randomAccessFile.getFilePointer()<randomAccessFile.length()){
                    String s = randomAccessFile.readLine();
                    int index1 = s.indexOf('!');
                    String sEmail = s.substring(0, index1);
                    if(sEmail.equals(email)){
                        System.out.println("Username found");
                        isEmailFound = true;
                        int index2 = s.indexOf('$');
                        salt = Long.parseLong(s.substring(index1 + 1,index2));
                        hashPassword = s.substring(index2+1);
                        break;
                    }
                }
                if(isEmailFound){
                    System.out.println("Enter password");
                    String password = sc.next();
                    password += String.valueOf(salt);
                    String hashCheck = convertBytesToHexString(convertToSHA(password));
                    if(hashCheck.equals(hashPassword))
                        System.out.println("Login successful");
                    else
                        System.out.println("Incorrect password. Login insuccesful");
                } else{
                    System.out.println("Email not found");
                }
                randomAccessFile.close();
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
        sc.close();
    }
}