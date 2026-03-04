package listeners;

import java.io.File;

public interface  LevelSelectorListener {

    void launchLevel(File file);
    void removeSurePane();
    void putSurePane();
    void switchToMenu();
}
