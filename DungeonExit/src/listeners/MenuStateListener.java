package listeners;

public interface MenuStateListener {
    
    void switchToLevelCreator();
    void switchToLevelSelector();
    void quitGame();
    void switchToGameRules();
}
