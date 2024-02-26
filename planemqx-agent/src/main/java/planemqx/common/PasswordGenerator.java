package planemqx.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PasswordGenerator {
	
	private static final Random random = new Random(); 
	
	private PasswordGenerator() {
	}

	public static String generatePassword(final int length) {
		String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
		String specialCharacters = "!@#$";
		String numbers = "1234567890";
		String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
		List<Character> password = new ArrayList<>();
		password.add(lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length())));
		password.add(capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length())));
		password.add(specialCharacters.charAt(random.nextInt(specialCharacters.length())));
		password.add(numbers.charAt(random.nextInt(numbers.length())));
		for (int i = 4; i < length; i++) {
			password.add(combinedChars.charAt(random.nextInt(combinedChars.length())));
		}
		Collections.shuffle(password);
		return password.toString();
	}
}