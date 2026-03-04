package listeners;

public interface LevelListener {

    void switchToLevelSelector();
    void checkWin();
    void changeLevel(int index);
    void solve();
    String getCurrentTime();
    void clean();
}
