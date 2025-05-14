package Main;

public class ProficiencyLevel {
    private String level;

    public ProficiencyLevel() {
        level = "beginner".toUpperCase();
    }

    public ProficiencyLevel(String level) {
        setLevel(level);
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        if (level == null || level.isEmpty()) {
            System.out.println("Error: class ProficiencyLevel, method setLevel, level arg is null or empty ");
        } else if (level.equalsIgnoreCase("beginner") || level.equalsIgnoreCase("intermediate") || level.equalsIgnoreCase("advanced")) {
            //level.lowercase().equal(string) == level.equalsIgnoreCase
            this.level = level.toUpperCase();
        } else {
            System.out.println("Error: class ProficiencyLevel, method setLevel, wrong set, it should be beginner or or intermediate or advanced.");
        }
    }


}
