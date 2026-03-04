package model;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import static utils.FileHandler.*;

public class MultiplayerModel {

    private ArrayList<Board> board;
    private int nbBoard;
    private int currentBoard;

	private int currentRound;
    private long roundTime = 30;

    private PieceBag pieceBagPlayer1;
	private PieceBag pieceBagPlayer2;
     
    // Timer variables
    private long startTime;
    private long elapsedTime; // Stored in seconds

    // Best time variables
    private String bestTime;
    private File currentLevelFile;

    /**
     * Creates a level based on a txt file configuration.
     * It initializes the board and the piece bag. 
     * 
     * @param path the path of the file config
     */
    public void initMultModels(File path){
        ArrayList<String[][]> map;
        board = new ArrayList<>();
        try {
            // Load best time
            try {
                bestTime = getBestTimeFromFile(path);
            } catch (FileNotFoundException e) {
                bestTime = "";
            }

            map = parseMapMult(path);

            this.pieceBagPlayer1 = parsePieceBag(path);
			this.pieceBagPlayer2 = new PieceBag(pieceBagPlayer1);
            this.pieceBagPlayer1.setPlayerPiece(1);
            this.pieceBagPlayer2.setPlayerPiece(2);

            for (int i = 0; i < map.size(); i++) {
                this.board.add(new Board(map.get(i)));
            }
            nbBoard = map.size();
            currentBoard = 0;
            // Initialize timer
            startTime = System.currentTimeMillis();
            elapsedTime = 0;
			
			currentRound = 1;
        } catch (FileNotFoundException e) {}  
    }

    public void changeLevel(int index) {
        if (index < 0 || index >= nbBoard)
            return ;
        currentBoard = index;
    }

	public int getCurrentRound() {
		return currentRound;
	}

	public void setCurrentRound(int currentRound) {
		this.currentRound = currentRound;
	}

    /**
     * Updates the level model. This method can be used for updating game-specific logic or mechanics
     * related to the level, although currently it does not perform any actions.
     */
    public void update() {
        // Update the elapsed time
        if (startTime > 0) {
            elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        }
    }

     /**
     * Compare current time with the best time and return true if current time
     * if better (i.e smaller)
     * 
     * @param currentTime   current time in format MM:SS
     * @param bestTime      best time in format MM:SS
     * @return              true if currentTime < bestTime
     */
    public static boolean isNewBestTime(String currentTime, String bestTime) {
        // If the best time is empty, the current time is always better
        if (bestTime == null || bestTime.isEmpty()) {
            return true;
        }
        
        // Converts time in seconds
        int currentSeconds = timeToSeconds(currentTime);
        int bestSeconds = timeToSeconds(bestTime);
        
        // Compare seconds
        return currentSeconds < bestSeconds;
    }
    
    /**
     * Converts a time in MM:SS format into seconds.   
     * 
     * @param time  Time in format MM:SS
     * @return      Time in seconds
     */
    private static int timeToSeconds(String time) {
        String[] parts = time.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return minutes * 60 + seconds;
    }

    /**
     * Resets the timer for the level.
     */
    public void resetTimer() {
        startTime = System.currentTimeMillis();
        elapsedTime = 0;
    }

    /**
     * Checks if the current time is better than the best time and updates it if necessary.
     * 
     * @return true if a new best time was set, false otherwise
     */
    public boolean checkAndUpdateBestTime() {
        String currentTime = getFormattedTime();
        
        if (isNewBestTime(currentTime, bestTime)) {
            // Update the best time in memory
            bestTime = currentTime;
            
            // Update the best time in the file
            try {
                updateBestTimeAndHighscore(currentLevelFile, currentTime, 0);
                return true;
            } catch (IOException e) {
            }
        }
        
        return false;
    }


    /**
     * Retrieves the current Board object, representing the game board for this level.
     *
     * @return the current board
     */
    public Board getBoard(int index) {
        return board.get(index);
    }

    public ArrayList<Board> getBoard() {
        return board;
    }

    public int getCurrentBoard() {
        return currentBoard;
    }

    public int getNbBoard() {
        return nbBoard;
    }

    /**
     * Retrieves the current PieceBag, which contains the collection of puzzle pieces available for use
     * in the level.
     *
     * @return the current PieceBag
     */
    public PieceBag getPieceBagPlayer1() {
        return pieceBagPlayer1;
    }

	public PieceBag getPieceBagPlayer2() {
        return pieceBagPlayer2;
    }

    /**
     * Gets the elapsed time in seconds since the level was initialized.
     * 
     * @return The elapsed time in seconds
     */
    public long getElapsedTimeInSeconds() {
        return elapsedTime;
    }

    /**
     * Gets the formatted time string in MM:SS format.
     * 
     * @return The formatted time string
     */
    public String getFormattedTime() {
        long seconds = elapsedTime % 60;
        return String.format("%02d / 30",  seconds);
    }
    
    /**
     * Gets the best time for this level.
     * 
     * @return The best time in MM:SS format, or empty string if no best time exists
     */
    public String getBestTime() {
        return bestTime;
    }

    public long getRoundTime() {
        return roundTime;
    }
}
