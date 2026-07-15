package utils;

import io.github.cdimascio.dotenv.Dotenv;

public class ConfigReader {
    private static Dotenv dotenv;

    static {
        try {
            dotenv = Dotenv.configure().ignoreIfMissing().load();
        } catch (Exception e) {
            System.err.println("Error cargando archivo .env: " + e.getMessage());
        }
    }

    public static String get(String key) {
        if (dotenv != null) {
            String value = dotenv.get(key);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        // Fallback a System environment variables properties si Dotenv no lo encontró
        return System.getenv(key);
    }
}
