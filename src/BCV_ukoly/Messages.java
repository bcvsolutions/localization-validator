package BCV_ukoly;

import java.util.ArrayList;

/**
 * Associating messages for comparing
 *
 * @author Petr Hanák
 *
 */
public class Messages {

    // references for language instances
    private static ArrayList<Messages> language = new ArrayList<>();
    // all messages with paths
    private ArrayList<String> messages = new ArrayList<>();
    // differences between files
    private static ArrayList<String> defects = new ArrayList<>();

    public static ArrayList<String> getDefects() {
        return defects;
    }

    public void addMessage(String message) {
        messages.add(message);
    }

    public static void addLanguage(Messages messages) {
        language.add(messages);
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public static ArrayList<Messages> getLanguages() {
        return language;
    }

    public static void addDefect(String defect) {
        defects.add(defect);
    }

    public static void clear() {
        language.clear();
        defects.clear();
    }
}
