import javax.swing.*;
import java.util.ArrayList;

public class LevelTwo extends SpaceInvaders {
    public LevelTwo() {
        super();
        currentLevel = 1;
        isTransitioning = false;
        lastAlienShotTime = 0;
        alienBulletArray = new ArrayList<>();
        alienColumns = 3;
        alienRows = 2;
        createAliens();
    }
}