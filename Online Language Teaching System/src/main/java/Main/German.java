// src/main/java/Main/German.java
package Main;

// Removed static lists and unused imports
public class German extends Language {
    private static final String LANGUAGE_NAME = "German";

    // Static lists and methods related to students/teachers were removed
    // as user management is handled by ApplicationManager.

    @Override
    public String getLanguageName() {
        return LANGUAGE_NAME;
    }
}