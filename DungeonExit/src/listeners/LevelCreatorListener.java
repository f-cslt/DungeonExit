package listeners;

import model.Piece;
import model.Tile;

public interface LevelCreatorListener {

    void switchToMenu();
    void addBoard();
    void removeBoard();
    void changeLevel(int index);
    void openFullPieceBag(); // Opens a piece bag with all pieces to select 
    void handlePieceSelection(Piece piece);
    void checkpointClicked(Tile.TileType checkpoint);
    void save();
    void checkWin(); // Validates whether the current level path is a winning one
    void solve();
    void clean();
    void saveWithName(String levelName);
    void changeMusicVictoryToBasic();
}

