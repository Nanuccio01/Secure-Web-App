package utils;

import java.util.regex.Pattern;

public class InputSanitizer {
	
    // Verifica la validità dell'email
	public static boolean isValidEmail(String email) {
        // Usa una regex per validare l'email
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);       
        return pattern.matcher(email).matches();
	}

    // Verifica la validità della password rispettando i criteri minimi di complessità
	public static String isValidPassword(String password) {
        // Controlla la lunghezza della password
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        // Controlla che contenga almeno una lettera maiuscola
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter.";
        }
        // Controlla che contenga almeno una lettera minuscola
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter.";
        }
        // Controlla che contenga almeno un numero
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one number.";
        }
        // Controlla che contenga almeno un carattere speciale
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|:;,./?].*")) {
            return "Password must contain at least one special character (!@#$%^&*()_+-={}[]:;,./?).";
        }
        // Se la password soddisfa tutti i requisiti, restituisce `null`
        return null;
    }
	
	// Verifica che la password non contenga caratteri non validi
		public static String noInvalidCharacters(String password) {
	        // Controllo dei caratteri speciali proibiti (Blacklist)
	        if (password.matches(".*[<>'\"`\\\\;\\-\\-/\\|:&%].*")) {
	        	return "Password contains invalid special characters.";
	        }
	        return null;
	    }	
}
