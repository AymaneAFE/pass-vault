package passvault.passwordgeneratorservice.service;

import passvault.passwordgeneratorservice.dto.PasswordGeneratorRequest;
import passvault.passwordgeneratorservice.dto.PasswordGeneratorResponse;
import passvault.passwordgeneratorservice.dto.PasswordStrengthResponse;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
public class PasswordGeneratorService {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final String AMBIGUOUS = "0O1lI";

    private final SecureRandom random = new SecureRandom();

    public PasswordGeneratorResponse generate(PasswordGeneratorRequest request) {
        StringBuilder charset = new StringBuilder();

        if (request.isIncludeLowercase())
            charset.append(LOWERCASE);
        if (request.isIncludeUppercase())
            charset.append(UPPERCASE);
        if (request.isIncludeNumbers())
            charset.append(NUMBERS);
        if (request.isIncludeSymbols())
            charset.append(SYMBOLS);

        String charsetStr = charset.toString();

        if (request.isExcludeAmbiguous()) {
            charsetStr = charsetStr.replaceAll("[" + AMBIGUOUS + "]", "");
        }

        String excludedChars = request.getExcludeCharacters();
        if (excludedChars != null && !excludedChars.isEmpty()) {
            for (char c : excludedChars.toCharArray()) {
                charsetStr = charsetStr.replace(String.valueOf(c), "");
            }
        }

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < request.getLength(); i++) {
            int index = random.nextInt(charsetStr.length());
            password.append(charsetStr.charAt(index));
        }

        String generatedPassword = password.toString();
        int strength = calculateStrength(generatedPassword);

        return PasswordGeneratorResponse.builder()
                .password(generatedPassword)
                .strength(strength)
                .strengthLabel(getStrengthLabel(strength))
                .build();
    }

    public PasswordStrengthResponse checkStrength(String password) {
        int score = calculateStrength(password);
        List<String> suggestions = generateSuggestions(password);

        return PasswordStrengthResponse.builder()
                .score(score)
                .label(getStrengthLabel(score))
                .suggestions(suggestions)
                .build();
    }

    private String getStrengthLabel(int score) {
        if (score < 30)
            return "Weak";
        if (score < 50)
            return "Fair";
        if (score < 70)
            return "Good";
        if (score < 90)
            return "Strong";
        return "Very Strong";
    }

    private int calculateStrength(String password) {
        int score = 0;

        // Length score
        score += Math.min(password.length() * 4, 40);

        // Character variety
        if (password.matches(".*[a-z].*"))
            score += 10;
        if (password.matches(".*[A-Z].*"))
            score += 10;
        if (password.matches(".*[0-9].*"))
            score += 10;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*"))
            score += 20;

        // Bonus for mixing
        if (password.length() >= 12)
            score += 10;

        return Math.min(score, 100);
    }

    private List<String> generateSuggestions(String password) {
        List<String> suggestions = new ArrayList<>();

        if (password.length() < 12)
            suggestions.add("Use at least 12 characters");
        if (!password.matches(".*[A-Z].*"))
            suggestions.add("Add uppercase letters");
        if (!password.matches(".*[a-z].*"))
            suggestions.add("Add lowercase letters");
        if (!password.matches(".*[0-9].*"))
            suggestions.add("Add numbers");
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*"))
            suggestions.add("Add special characters");

        return suggestions;
    }

}
