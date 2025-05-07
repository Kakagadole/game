import java.util.*;

public class LevelManager {
    private List<String[][]> levels = new ArrayList<>();
    private int currentLevel = 0;

    public LevelManager() {
        initLevels();
    }

    private void initLevels() {
        levels.add(new String[][] {
                {"A", " ", "A", " ", "A"},
                {"A", "A", "A", "A", "A"}
        });

        levels.add(new String[][] {
                {"A", "A", "A", "A", "A"},
                {" ", "A", "A", "A", " "},
                {"A", " ", " ", " ", "A"}
        });

        levels.add(new String[][] {
                {"A", "A", "A", "A", "A"},
                {"A", "A", "A", "A", "A"},
                {"A", " ", "A", " ", "A"}
        });
    }

    public String[][] getCurrentLevelMap() {
        return levels.get(currentLevel);
    }

    public void nextLevel() {
        if (currentLevel < levels.size() - 1) {
            currentLevel++;
        }
    }

    public boolean hasMoreLevels() {
        return currentLevel < levels.size() - 1;
    }

    public int getCurrentLevelNumber() {
        return currentLevel + 1;
    }

    public void reset() {
        currentLevel = 0;
    }
}

