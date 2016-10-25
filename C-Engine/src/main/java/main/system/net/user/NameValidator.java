package main.system.net.user;

public class NameValidator {

    public final static String ALLOWED_CHARACTERS = "qwertyuiopasdfghjklzxcvbnm1234567890!@#$%^&*";

    public final static String EXTENDED_CHARACTERS = ":()~`-_+qwertyuiopasdfghjklzxcvbnm1234567890";

    public final static String MAIL_CHARACTERS = "@.qwertyuiopasdfghjklzxcvbnm1234567890";

    public static boolean checkUserData(String name) {
        if (name != null) {
            String username = name.split(":")[0];

            if (username.length() > 3 && checkChars(username)) {
                {
                    String password = name.split(":")[1];

                    if ((password.length() < 4) && checkChars(password))

                        return true;
                }
            }
        }
        return false;
    }

    public static boolean checkChars(String s) {
        for (Character c : s.toCharArray()) {
            if (!((ALLOWED_CHARACTERS.toUpperCase()).contains(c.toString()) || ALLOWED_CHARACTERS
                    .contains(c.toString()))) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkExtendedChars(String s) {
        for (Character c : s.toCharArray()) {
            if (!((EXTENDED_CHARACTERS.toUpperCase()).contains(c.toString()) || EXTENDED_CHARACTERS
                    .contains(c.toString()))) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkMailChars(String s) {
        for (Character c : s.toCharArray()) {
            if (!((MAIL_CHARACTERS.toUpperCase()).contains(c.toString()) || MAIL_CHARACTERS
                    .contains(c.toString()))) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkMail(String email) {
        return email.contains("@") && email.contains(".") && checkMailChars(email);
    }

    public static boolean checkGameName(String gameName) {

        return (checkExtendedChars(gameName) && (ALLOWED_CHARACTERS
                .contains(gameName.substring(0, 1)) || ALLOWED_CHARACTERS
                .toUpperCase().contains(gameName.substring(0, 1))));
    }
}
