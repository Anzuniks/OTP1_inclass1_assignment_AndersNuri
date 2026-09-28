package fi.metropolia.tempconverter;

/**
 * Entry point for the fat jar. A main class that does not extend Application
 * lets JavaFX start from the classpath (java -jar).
 */
public class Launcher {
    public static void main(String[] args) {
        Main.main(args);
    }
}
