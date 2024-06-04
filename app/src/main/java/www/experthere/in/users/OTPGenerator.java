package www.experthere.in.users;

import java.util.Random;

public class OTPGenerator {

    public static void main(String[] args) {
        // Example usage
        String otp = generateOTP();
        System.out.println("Generated OTP: " + otp);
    }

    public static String generateOTP() {
        // Length of the OTP
        int otpLength = 6;

        // Characters to use for generating OTP
        String otpCharacters = "0123456789";

        // Random object
        Random random = new Random();

        // StringBuilder to store the OTP
        StringBuilder otpBuilder = new StringBuilder(otpLength);

        // Generate OTP
        for (int i = 0; i < otpLength; i++) {
            int index = random.nextInt(otpCharacters.length());
            char otpChar = otpCharacters.charAt(index);
            otpBuilder.append(otpChar);
        }

        return otpBuilder.toString();
    }
}
