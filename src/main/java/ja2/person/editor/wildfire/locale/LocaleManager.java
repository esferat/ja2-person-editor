package ja2.person.editor.wildfire.locale;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocaleManager {

    private static LocaleManager instance;
    private ResourceBundle messages;
    private Locale currentLocale;

    // Private constructor to prevent external instantiation
    private LocaleManager() {
        // Default to English if no locale is set
        this.currentLocale = new Locale("ru", "RU");
        loadMessages(this.currentLocale);
    }

    // Public static method to get the single instance of the class
    public static synchronized LocaleManager getInstance() {
        if (instance == null) {
            instance = new LocaleManager();
        }
        return instance;
    }

    // Method to load messages for a given locale
    private void loadMessages(Locale locale) {
        try {
            // "messages" is the base name of your resource bundle files (e.g., messages.properties, messages_es.properties)
            this.messages = ResourceBundle.getBundle("messages", locale);
        } catch (Exception e) {
            System.err.println("Error loading resource bundle for locale " + locale + ": " + e.getMessage());
            // Fallback to default locale if specific locale bundle is not found
            this.messages = ResourceBundle.getBundle("messages", new Locale("ru", "RU"));
        }
    }

    // Method to set a new locale and reload messages
    public void setLocale(Locale locale) {
        if (!this.currentLocale.equals(locale)) {
            this.currentLocale = locale;
            loadMessages(this.currentLocale);
        }
    }

    // Method to get a localized string
    public String getString(String key) {
        try {
            return messages.getString(key);
        } catch (Exception e) {
            System.err.println("Missing key '" + key + "' for locale " + currentLocale);
            return "!!" + key + "!!"; // Indicate missing key
        }
    }

    // Method to get the current locale
    public Locale getCurrentLocale() {
        return currentLocale;
    }
}
