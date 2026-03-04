package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import model.Tile.TileType;

/**
 * The Board class represents the game board, consisting of a grid of tiles and
 * the pieces placed on it. It manages the state of each tile (e.g., entry
 * point, key, chest, etc.) and supports adding pieces to the board.
 *
 * This class is responsible for initializing the board with a map layout and
 * ensuring that pieces are placed according to the game's rules, such as not
 * overwriting certain tiles or going outside the grid's bounds.
 */
public class Board {

	private final int rowCount;
	private final int colCount;
	private final Tile[][] grid;
	private final Piece[][] pieceGrid; // Stores pieces placed on the board

	private final Piece[][] specialPieceGrid;
	private Point stairsPos;
	private Point entryPos;

	private Point entryP1;
	private Point entryP2;

	private PlacementPreview currentPreview;
	private boolean isDragging;
	private int originalDragRow;
	private int originalDragCol;
	private List<int[]> originalDragShape;
	private List<Piece.Direction> originalDragTrack;

	/**
	 * Maps each puzzle piece to its top-left origin coordinates on the board
	 * grid. The Point represents the (column, row) position where the piece was
	 * initially placed. Used to track the original position of pieces for
	 * drag-and-drop operations and collision checks.
	 */
	private final Map<Piece, Point> pieceOrigins = new HashMap<>();

	/**
	 * Constructs a new Board instance from a 2D map representation. The map
	 * defines the layout of the board and the types of tiles (entry, key,
	 * chest, etc.).
	 *
	 * @param map a 2D array of characters representing the initial layout of
	 * the board
	 */
	public Board(char[][] map) {
		rowCount = map[0].length;    
		colCount = map.length;
		grid = new Tile[rowCount][colCount];
		pieceGrid = new Piece[rowCount][colCount];

		entryPos = null;
		stairsPos = null;

		init(map);
		specialPieceGrid = new Piece[rowCount][colCount];
	}

	public Board(String[][] map) {
		rowCount = map[0].length;    
		colCount = map.length;
		grid = new Tile[rowCount][colCount];
		pieceGrid = new Piece[rowCount][colCount];

		entryP1 = null;
		entryP2 = null;
		stairsPos = null;

		init(map);
		specialPieceGrid = new Piece[rowCount][colCount];
	}

	public Board() {
		this.rowCount = 12;
		this.colCount = 12;
		this.grid = new Tile[rowCount][colCount];
		this.pieceGrid = new Piece[rowCount][colCount];
		this.specialPieceGrid = new Piece[rowCount][colCount];
		initEmptyBoard();
	}


	/**
	 * Constructs a copy of a board
	 *
	 * @param other the board to copy
	 */
	public Board(Board other) {
		this.rowCount = other.rowCount;
		this.colCount = other.colCount;
		this.grid = new Tile[rowCount][colCount];
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < colCount; j++) {
				grid[i][j] = new Tile(other.grid[i][j]);
			}
		}
		pieceGrid = new Piece[rowCount][colCount];

		specialPieceGrid = new Piece[rowCount][colCount];
	}

	/**
	 * Init attributes and classes. For the moment the method creates the grid.
	 *
	 * @param map 2D array of char containing the map
	 */
	private void init(char[][] map) {
		// Create an empty grid and initialize the tiles based on the map
		for (int i = 0; i < colCount; i++) {
			for (int j = 0; j < rowCount; j++) {
				Tile tile = new Tile();
				switch (map[i][j]) {
					case 'E' -> {
						tile.setType(TileType.ENTRY);
						tile.setPlayer(1);
						entryPos = new Point(i, j);}
					case 'K' -> {
						tile.setType(TileType.KEY);
						tile.setPlayer(1);}
					case 'C' -> {
						tile.setType(TileType.CHEST);
                        tile.setPlayer(1);}
					case 'D' -> {
						tile.setType(TileType.DRAGON);
                        tile.setPlayer(1);}
					case '>' -> {
						tile.setType(TileType.EXIT);
						tile.setPlayer(1);}

					case '1' -> {
						tile.setType(TileType.EMPTY);
						tile.setPlayer(1);}
					case 'S' -> {
						tile.setType(TileType.STAIRS);
						stairsPos = new Point(i, j);
						tile.setPlayer(1);

					}
					default -> {
						tile.setType(TileType.DEFAULT);
						tile.setPlayer(1);}
				}
				this.grid[i][j] = tile;
			}
		}
	}

	private void initEmptyBoard() {
		for (int i = 0; i < colCount; i++) {
			for (int j = 0; j < rowCount; j++) {
				this.grid[i][j] = new Tile(); // DEFAULT in constructor
			}
		}
	}

	private void init(String[][] map) {
		// Create an empty grid and initialize the tiles based on the map
		for (int i = 0; i < colCount; i++) {
			for (int j = 0; j < rowCount; j++) {
				Tile tile = new Tile();
				switch (map[i][j]) {
					case "E1" -> {
						tile.setType(TileType.ENTRY);
						tile.setPlayer(1);
						entryP1 = new Point(i, j);}
					case "K1" -> {
						tile.setType(TileType.KEY);
						tile.setPlayer(1);}
					case "C1" -> {
						tile.setType(TileType.CHEST);
                        tile.setPlayer(1);}
					case "D1" -> {
						tile.setType(TileType.DRAGON);
                        tile.setPlayer(1);}
					case ">1" -> {
						tile.setType(TileType.EXIT);
						tile.setPlayer(1);}
					case "E2" -> {
						tile.setType(TileType.ENTRY);
						tile.setPlayer(2);
						entryP2 = new Point(i, j);}
					case "K2" -> {
						tile.setType(TileType.KEY);
						tile.setPlayer(2);}
					case "C2" -> {
						tile.setType(TileType.CHEST);
                        tile.setPlayer(2);}
					case "D2" -> {
						tile.setType(TileType.DRAGON);
                        tile.setPlayer(2);}
					case ">2" -> {
						tile.setType(TileType.EXIT);
						tile.setPlayer(2);}
					case "1" -> {
						tile.setType(TileType.EMPTY);
						tile.setPlayer(0);}
					case "S" -> {
						tile.setType(TileType.STAIRS);
						stairsPos = new Point(i, j);
						tile.setPlayer(1);
					}
					default -> {
						tile.setType(TileType.DEFAULT);
						tile.setPlayer(0);}
				}
				this.grid[i][j] = tile;
			}
		}
	}

	/**
	 * Checks if a piece can be placed at the given coordinates by validating placement rules.
	 * The method enforces different rules based on whether the piece is a bridge or a regular piece:
	 * - Bridge pieces must be placed on PATH tiles and cannot overlap other bridges
	 * - Regular pieces must be placed on DEFAULT tiles and cannot overlap any existing pieces
	 * - Both types must follow endpoint connection rules for proper path construction
	 *
	 * @param piece Piece to be placed
	 * @param row y coordinate in the grid
	 * @param col x coordinate in the grid
	 * @return true if the piece can be placed according to all rules, false otherwise
	 */
	public boolean canPlacePiece(Piece piece, int row, int col) {
		// Track which pieces we connect to for validation
		Set<Piece> connectedPieces = new HashSet<>();
		boolean connectsToBridge = false;
		boolean connectsToRegularPiece = false;
		
		// Containers for tracking endpoints and connections
		Set<Point> usedNewPieceEndpoints = new HashSet<>();
		Map<Point, Integer> newConnections = new HashMap<>();
		
		// Validate each tile in the piece's shape
		for (int[] offset : piece.getShape()) {
			int r = row + offset[1];
			int c = col + offset[0];
			
			// Basic bounds check
			if (!isWithinBounds(r, c)) return false;
			
			// Apply core placement rules based on piece type
			if (!validateBasicPlacement(piece, r, c)) return false;
			
			// Skip connection checks for bridge pieces
			if (piece.isBridge()) continue;
			
			// Check connections with adjacent cells
			for (int[] dir : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
				int adjR = r + dir[0];
				int adjC = c + dir[1];
				
				if (!isWithinBounds(adjR, adjC)) continue;
				
				Piece adjPiece = getPieceFromPieceGridAt(adjR, adjC);
				Piece adjBridge = getPieceFromSpecialPieceGridAt(adjR, adjC);
				TileType adjTileType = grid[adjR][adjC].getType();
				
				// Handle bridge connections
				if (adjBridge != null) {
					boolean result = handleBridgeConnection(piece, offset, adjBridge, adjR, adjC, r, c, 
														usedNewPieceEndpoints, connectedPieces);
					if (!result) return false;
					connectsToBridge = true;
				}
				// Handle regular piece connections
				else if (adjPiece != null) {
					boolean result = handleRegularPieceConnection(piece, offset, adjPiece, adjR, adjC, r, c, 
															usedNewPieceEndpoints, newConnections, connectedPieces);
					if (!result) return false;
					connectsToRegularPiece = true;
				}
				// Handle checkpoint connections
				else if (isCheckpoint(adjTileType)) {
					boolean result = handleCheckpointConnection(piece, offset, adjTileType, adjR, adjC, r, c, 
															usedNewPieceEndpoints, newConnections);
					if (!result) return false;
				}
			}
		}
		
		// Check for invalid bridge-under-piece connection
		if (connectsToBridge && connectsToRegularPiece) {
			if (connectsToBothBridgeAndPieceUnderBridge(piece, row, col, connectedPieces)) {
				return false;
			}
		}
		
		return true;
	}


	/**
	 * This method checks if all special tiles (ENTRY, KEY, CHEST, DRAGON, EXIT)
	 * are connected in the correct order: ENTRY → KEY → CHEST → DRAGON → EXIT.
	 * It performs multiple BFS searches to ensure each step is reachable sequentially.
	 */
	public boolean checkWinCondition() {
		Point entryPoint = null, keyPoint = null, chestPoint = null, dragonPoint = null, exitPoint = null;

		// Identify positions of all special tiles
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < colCount; j++) {
				TileType type = grid[i][j].getType();
				Point p = new Point(j, i);
				switch (type) {
					case ENTRY -> entryPoint = p;
					case KEY -> keyPoint = p;
					case CHEST -> chestPoint = p;
					case DRAGON -> dragonPoint = p;
					case EXIT -> exitPoint = p;
				}
			}
		}

		// If any required tile is missing, the win condition cannot be met
		if (entryPoint == null || keyPoint == null || chestPoint == null || dragonPoint == null || exitPoint == null) {
			return false;
		}

		// Check if each step is reachable in sequence using BFS
		return bfs(entryPoint, keyPoint) &&
				bfs(keyPoint, chestPoint) &&
				bfs(chestPoint, dragonPoint) &&
				bfs(dragonPoint, exitPoint);
	}

	/**
	 * Performs a Breadth-First Search (BFS) to determine if there is a valid path
	 * from the start position to the target position.
	 *
	 * @param start  The starting tile position
	 * @param target The target tile position
	 * @return true if there is a valid path, false otherwise
	 */
	public boolean bfs(Point start, Point target) {
		Set<Point> visited = new HashSet<>(); // HashSet for O(1) lookup of visited nodes
		Queue<Point> queue = new LinkedList<>(); // LinkedList as a queue for BFS

		queue.add(start);
		visited.add(start);

		int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Movement directions: up, down, left, right

		while (!queue.isEmpty()) {
			Point current = queue.poll();
			if (current.equals(target)) return true; // If target is reached, return true

			for (int[] dir : directions) {
				int newRow = current.y + dir[0];
				int newCol = current.x + dir[1];
				Point newPoint = new Point(newCol, newRow);

				// Ensure movement is within bounds and either on a path or the target tile itself
				if (isWithinBounds(newRow, newCol) &&
						(grid[newRow][newCol].getType() == TileType.PATH || newPoint.equals(target)) &&
						!visited.contains(newPoint)) {

					visited.add(newPoint);
					queue.add(newPoint);
				}
			}
		}
		return false; // No valid path found
	}




	public boolean addCheckpointToBoard(TileType checkpoint, int row, int col) {

		for (int i = 0; i < colCount; i++) {
			for (int j = 0; j < rowCount; j++) {
				if (grid[i][j].getType() == checkpoint) {
					return false;
				}
			}
		}
		grid[row][col].setPlayer(1);
		grid[row][col].setType(checkpoint);
		if (checkpoint == TileType.ENTRY) {
			entryPos = new Point(row, col);
		} else if(checkpoint == TileType.STAIRS) {
			stairsPos = new Point(row, col);
		}
		return true;
	}














	/**************************************************************************** */
	/*
	 ****************************************************************************
	 *         METHODS RELATING TO THE PIECE'S ACTIONS ON THE BOARD             *
	 * **************************************************************************
	 */
	/**************************************************************************** */
	
	/**
	 * Method called to start the drag to move the puzzle piece. This method is
	 * used to store important information such as the initial position of the
	 * piece and the piece itself.
	 *
	 * @param piece piece on which the player has clicked
	 * @param row y position of the piece on the board
	 * @param col x position of the piece on the board
	 */
	public void startDrag(Piece piece, int row, int col) {
		this.originalDragRow = row;
		this.originalDragCol = col;
		this.isDragging = true;
		this.currentPreview = new PlacementPreview(row, col, false, piece);
		this.originalDragShape = new ArrayList<>(piece.getShape());
		this.originalDragTrack = new ArrayList<>(piece.getTrack());
	}

	public void restoreOriginalPiece() {
		Piece draggedPiece = currentPreview.getSelectedPiece();
		if (draggedPiece != null) {
			// Reset piece to its original form
			draggedPiece.getTrack().clear();
			draggedPiece.getTrack().addAll(originalDragTrack);
			draggedPiece.getShape().clear();
			draggedPiece.getShape().addAll(originalDragShape);
		}
	}

	/**
	 * delete the preview of the piece. Useful if the mouse exits the panel, put
	 * the puzzle piece down, etc.
	 */
	public void clearPreview() {
		this.currentPreview = null;
		this.isDragging = false;
	}

	/**
	 * Remove a piece to the board. Need better implementation but works fine
	 * for the moment.
	 *
	 * @param piece the selected piece from the piece bag to remove
	 * @param row the row index (top left) of the piece to remove
	 * @param col the col index (still top left) of the piece to remove
	 */
	public void removePiece(Piece piece) {
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < colCount; j++) {
				if (piece.isBridge()) {
					if (specialPieceGrid[i][j] == piece) {
						specialPieceGrid[i][j] = null;
					}
				} else {
					if (pieceGrid[i][j] == piece) {
						pieceGrid[i][j] = null;
						grid[i][j].setType(TileType.DEFAULT);
					}
				}
			}
		}
		pieceOrigins.remove(piece);
	}

	/**
	 * Adds a piece to the board, ensuring it does not overwrite existing tiles
	 * and stays within the grid's bounds.
	 *
	 * @param piece the piece to be placed on the board
	 * @param row the row index where the piece should be placed
	 * @param col the column index where the piece should be placed
	 * @return true if the piece was successfully added, false otherwise
	 */
	public boolean addPiece(Piece piece, int row, int col) {
		if (!canPlacePiece(piece, row, col)) {
			return false;
		}
		for (int[] offset : piece.getShape()) {
			int r = row + offset[1]; // y
			int c = col + offset[0]; // x

			if (piece.isBridge()) {
				specialPieceGrid[r][c] = piece;
			} else {
				if (piece.getPlayer() == 1) {
					grid[r][c].setType(TileType.PATH);
					grid[r][c].setPlayer(1);
				}
				else {
					grid[r][c].setType(TileType.PATH2);
					grid[r][c].setPlayer(2);
				}
				pieceGrid[r][c] = piece;
			}
		}
		pieceOrigins.put(piece, new Point(col, row)); // Stocker l'origine (col, row)
		return true;
	}















	/*************************************************************************** */
	/*
	 ****************************************************************************
	 *              METHODS RELATING TO CAN PLACE PIECE                         *
	 * **************************************************************************
	 */
	/**************************************************************************** */

	/**
	 * Validates the basic placement rules for a piece at the specified position.
	 * Bridge pieces must be placed on PATH tiles and not overlap other bridges.
	 * Regular pieces must be placed on DEFAULT tiles and not overlap any pieces.
	 * 
	 * @param piece The piece to be placed
	 * @param r The row position to check
	 * @param c The column position to check
	 * @return true if basic placement rules are satisfied, false otherwise
	 */
	private boolean validateBasicPlacement(Piece piece, int r, int c) {
		if (piece.isBridge()) {
			// Bridge pieces must be on PATH tiles and not overlap other bridges
			return ((grid[r][c].getType() == TileType.PATH && piece.getPlayer() == 1) || 
				(grid[r][c].getType() == TileType.PATH2 && piece.getPlayer() == 2)) && specialPieceGrid[r][c] == null;
		} else {
			// Regular pieces must be on DEFAULT tiles and not overlap any pieces
			return grid[r][c].getType() == TileType.DEFAULT && getPieceAt(r, c) == null;
		}
	}

	/**
	 * Handles the validation of connections between a new piece and an existing bridge.
	 * Validates proper endpoint connections and tracks which bridges we connect to.
	 * 
	 * @param piece The piece being placed
	 * @param offset The offset within the piece shape
	 * @param adjBridge The adjacent bridge piece
	 * @param adjR The row of the adjacent bridge tile
	 * @param adjC The column of the adjacent bridge tile
	 * @param r The row of the current piece tile
	 * @param c The column of the current piece tile
	 * @param usedNewPieceEndpoints Set tracking which endpoints of the new piece are already used
	 * @param connectedPieces Set tracking the pieces we connect to
	 * @return true if the connection is valid, false otherwise
	 */
	private boolean handleBridgeConnection(Piece piece, int[] offset, Piece adjBridge, int adjR, int adjC, 
										int r, int c, Set<Point> usedNewPieceEndpoints, 
										Set<Piece> connectedPieces) {
		// Only check for endpoints of the new piece
		if (!isEndpoint(piece, offset)) {
			return true; // Non-endpoints don't need connection validation
		}
		
		// Validate endpoint connections
		if (!arePiecesConnectedAtEndpoints(piece, offset, adjBridge, adjR, adjC)) {
			return false;
		}
		
		// Track that this piece connects to a bridge
		connectedPieces.add(adjBridge);
		
		// Track used endpoints
		boolean isSingleTile = piece.getShape().size() == 1;
		Point newEndpoint = new Point(c, r);
		
		if (!isSingleTile) {
			if (usedNewPieceEndpoints.contains(newEndpoint)) {
				return false;
			}
			usedNewPieceEndpoints.add(newEndpoint);
		}
		
		// Validate adjacent endpoint availability
		return isAdjacentEndpointAvailable(adjBridge, adjR, adjC, r, c);
	}

	/**
	 * Handles the validation of connections between a new piece and an existing regular piece.
	 * Validates proper endpoint connections and tracks which pieces we connect to.
	 * 
	 * @param piece The piece being placed
	 * @param offset The offset within the piece shape
	 * @param adjPiece The adjacent regular piece
	 * @param adjR The row of the adjacent piece tile
	 * @param adjC The column of the adjacent piece tile
	 * @param r The row of the current piece tile
	 * @param c The column of the current piece tile
	 * @param usedNewPieceEndpoints Set tracking which endpoints of the new piece are already used
	 * @param newConnections Map tracking connection counts at each position
	 * @param connectedPieces Set tracking the pieces we connect to
	 * @return true if the connection is valid, false otherwise
	 */
	private boolean handleRegularPieceConnection(Piece piece, int[] offset, Piece adjPiece, int adjR, int adjC, 
											int r, int c, Set<Point> usedNewPieceEndpoints,
											Map<Point, Integer> newConnections, Set<Piece> connectedPieces) {
		// Basic connection validation
		if (!arePiecesConnectedAtEndpoints(piece, offset, adjPiece, adjR, adjC)) {
			return false;
		}
		
		// Only proceed with detailed validation for endpoints
		if (isEndpoint(piece, offset)) {
			// Validate endpoint connections
			if (!arePiecesConnectedAtEndpoints(piece, offset, adjPiece, adjR, adjC)) {
				return false;
			}
			
			// Track the pieces we connect to
			connectedPieces.add(adjPiece);
			
			// Track used endpoints
			boolean isSingleTile = piece.getShape().size() == 1;
			Point newEndpoint = new Point(c, r);
			
			if (!isSingleTile) {
				if (usedNewPieceEndpoints.contains(newEndpoint)) {
					return false;
				}
				usedNewPieceEndpoints.add(newEndpoint);
			}
			
			// Validate adjacent endpoint availability
			if (!isAdjacentEndpointAvailable(adjPiece, adjR, adjC, r, c)) {
				return false;
			}
		}
		
		// Track connection count
		Point pos = new Point(adjC, adjR);
		int count = newConnections.getOrDefault(pos, 0) + 1;
		if (count > 1) {
			return false;
		}
		newConnections.put(pos, count);
		
		return true;
	}

	/**
	 * Handles the validation of connections between a piece and a checkpoint (special tile).
	 * Validates that checkpoints have the correct number of connections and that only
	 * piece endpoints connect to checkpoints.
	 * 
	 * @param piece The piece being placed
	 * @param offset The offset within the piece shape
	 * @param adjTileType The type of the adjacent checkpoint tile
	 * @param adjR The row of the adjacent checkpoint
	 * @param adjC The column of the adjacent checkpoint
	 * @param r The row of the current piece tile
	 * @param c The column of the current piece tile
	 * @param usedNewPieceEndpoints Set tracking which endpoints of the new piece are already used
	 * @param newConnections Map tracking connection counts at each position
	 * @return true if the connection is valid, false otherwise
	 */
	private boolean handleCheckpointConnection(Piece piece, int[] offset, TileType adjTileType, int adjR, int adjC, 
											int r, int c, Set<Point> usedNewPieceEndpoints,
											Map<Point, Integer> newConnections) {
		// Only endpoints can connect to checkpoints
		if (!isEndpoint(piece, offset)) {
			return false;
		}

		// Track connection count
		Point checkpointPos = new Point(adjC, adjR);
		int count = newConnections.getOrDefault(checkpointPos, 0) + 1;
		if (count > 1) {
			return false;
		}
		newConnections.put(checkpointPos, count);

		// Validate connection limit
		int existing = countEndpointsAroundCheckpoint(adjR, adjC);
		if (existing + count > allowedConnections(adjTileType)) {
			return false;
		}

		// Track endpoint usage
		Point newEndpoint = new Point(c, r);
		if (usedNewPieceEndpoints.contains(newEndpoint)) {
			return false;
		}
		usedNewPieceEndpoints.add(newEndpoint);
		
		return true;
	}

  
	/**
	 * Creates a mapping of bridge pieces to the set of regular pieces underneath them.
	 * This method is used to optimize bridge connection validation by pre-computing
	 * which regular pieces are under each bridge.
	 *
	 * @return A map from bridge pieces to sets of regular pieces underneath them
	 */
	private Map<Piece, Set<Piece>> mapBridgesToPiecesUnderneath() {
		Map<Piece, Set<Piece>> bridgeToPieces = new HashMap<>();
		
		// Scan the board once to find all bridges and pieces underneath them
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < colCount; j++) {
				Piece bridge = specialPieceGrid[i][j];
				Piece regularPiece = pieceGrid[i][j];
				
				// If there's a bridge and a regular piece at this position
				if (bridge != null && regularPiece != null) {
					// Add the regular piece to the set of pieces under this bridge
					bridgeToPieces.computeIfAbsent(bridge, k -> new HashSet<>())
								.add(regularPiece);
				}
			}
		}
		
		return bridgeToPieces;
	}

	/**
	 * Finds all bridges that the piece will connect to.
	 * 
	 * @param piece The piece being placed
	 * @param row The row position of the piece
	 * @param col The column position of the piece
	 * @return A set of bridge pieces that the new piece connects to
	 */
	private Set<Piece> findConnectedBridges(Piece piece, int row, int col) {
		Set<Piece> connectedBridges = new HashSet<>();
		
		// Check each tile in the piece's shape
		for (int[] offset : piece.getShape()) {
			int r = row + offset[1];
			int c = col + offset[0];
			
			// Check all adjacent cells
			for (int[] dir : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
				int adjR = r + dir[0];
				int adjC = c + dir[1];
				
				if (!isWithinBounds(adjR, adjC)) continue;
				
				// If there's a bridge in an adjacent cell
				Piece bridge = getPieceFromSpecialPieceGridAt(adjR, adjC);
				if (bridge != null) {
					connectedBridges.add(bridge);
				}
			}
		}
		
		return connectedBridges;
	}

	/**
	 * Checks if the piece is connecting to both a bridge and a piece that is under that bridge,
	 * which would create an invalid connection.
	 * 
	 * This method has been optimized to avoid excessive nested loops by:
	 * 1. Pre-computing which regular pieces are under each bridge
	 * 2. Finding bridges the new piece connects to
	 * 3. Checking for intersection between connected pieces and pieces under those bridges
	 * 
	 * @param piece The piece being placed
	 * @param row The starting row position of the piece
	 * @param col The starting column position of the piece
	 * @param connectedPieces Set of regular pieces we connect to
	 * @return true if an invalid bridge-under-piece connection is found, false otherwise
	 */
	private boolean connectsToBothBridgeAndPieceUnderBridge(Piece piece, int row, int col, Set<Piece> connectedPieces) {
		// Skip the check if we don't connect to any regular pieces
		if (connectedPieces.isEmpty()) {
			return false;
		}
		
		// Get all bridges that this piece connects to
		Set<Piece> connectedBridges = findConnectedBridges(piece, row, col);
		if (connectedBridges.isEmpty()) {
			return false;
		}
		
		// Map each bridge to the set of regular pieces underneath it
		Map<Piece, Set<Piece>> bridgeToPieces = mapBridgesToPiecesUnderneath();
		
		// For each bridge we connect to, check if we also connect to any piece underneath it
		for (Piece bridge : connectedBridges) {
			Set<Piece> piecesUnderBridge = bridgeToPieces.get(bridge);
			if (piecesUnderBridge != null) {
				// Check for intersection between connected pieces and pieces under this bridge
				for (Piece connectedPiece : connectedPieces) {
					if (piecesUnderBridge.contains(connectedPiece)) {
						return true; // Invalid: connecting to both bridge and piece under it
					}
				}
			}
		}
		
		return false;
	}

	/**
	 * Checks that the end of the adjacent piece (adjPiece) located in (adjR,
	 * adjC) is available to establish a new connection. The (fromRow, fromCol)
	 * parameter corresponds to the tile in piece A that is in contact with the
	 * extremity of B.
	 *
	 */
	private boolean isAdjacentEndpointAvailable(Piece adjPiece, int adjR, int adjC, int fromRow, int fromCol) {
		boolean isSingleTile = adjPiece.getShape().size() == 1;
		// For multi-tile pieces: Block if any other connections exist (other than the current one)
		if (!isSingleTile) {
			for (int[] d : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
				int nr = adjR + d[0];
				int nc = adjC + d[1];
				
				if (!isWithinBounds(nr, nc) || (nr == fromRow && nc == fromCol)) {
					continue;
				}
	
				// Check for checkpoints or other pieces
				TileType tileType = grid[nr][nc].getType();
				Piece neighbor = getPieceFromPieceGridAt(nr, nc);
				boolean hasOtherPiece = neighbor != null && neighbor != adjPiece;

				if (isCheckpoint(tileType)|| hasOtherPiece) {
					return false;
				}            
			}
		}
		// For single-tile pieces: Allow up to 2 connections (checkpoints or pieces)
		else{
			int existingConnections = 0;
			for (int[] d : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
				int nr = adjR + d[0];
				int nc = adjC + d[1];
				if (!isWithinBounds(nr, nc) || (nr == fromRow && nc == fromCol)) {
					continue; // Skip current connection or out-of-bounds
				}
				// Count checkpoints or other pieces
				TileType tileType = grid[nr][nc].getType();
				Piece neighbor = getPieceFromPieceGridAt(nr, nc);
				boolean hasConnection = isCheckpoint(tileType) || (neighbor != null && neighbor != adjPiece);
				
				if (hasConnection) {
					existingConnections++;
				}
			}
			// Block if already has 2 connections
			if (!adjPiece.isBridge() && existingConnections >= 2) {
				return false;
			}
			else if(adjPiece.isBridge() && existingConnections >= 4){
				return false;
			}
		}
		return true;
	}
	/**
	 * Counts the number of endpoints around a given checkpoint on the board.
	 *
	 * @param checkpointRow The x-coordinate of the checkpoint.
	 * @param checkpointCol The y-coordinate of the checkpoint.
	 * @return The number of endpoints around the checkpoint.
	 */
	private int countEndpointsAroundCheckpoint(int checkpointRow, int checkpointCol) {
		int count = 0;
		for (int[] d : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
			int adjR = checkpointRow + d[0];
			int adjC = checkpointCol + d[1];

			if (!isWithinBounds(adjR, adjC)) {
				continue;
			}
			Piece p = getPieceAt(adjR, adjC);
			if (p != null) {
				Point origin = pieceOrigins.get(p);
				int offsetX = adjC - origin.x;
				int offsetY = adjR - origin.y;
				if (isEndpoint(p, new int[]{offsetX, offsetY})) {
					count++;
				}
			}
		}
		return count;
	}

	private int allowedConnections(TileType type) {
		return switch (type) {
			case DRAGON, KEY, CHEST ->
				2;
			case ENTRY, EXIT ->
				1;
			case BRIDGE, STAIRS ->
				4;
			default ->
				0;
		};
	}

	public boolean isWithinBounds(int row, int col) {
		return row >= 0 && row < rowCount && col >= 0 && col < colCount;
	}

	public boolean isCheckpoint(TileType tile){
		return (tile == TileType.ENTRY || tile == TileType.EXIT
				|| tile == TileType.KEY || tile == TileType.CHEST
				|| tile == TileType.DRAGON || tile == TileType.STAIRS);
	}

	/**
	 * Checks if a coordinate in a piece's shape is an endpoint.
	 *
	 * @param piece The puzzle piece.
	 * @param offset The [x,y] offset relative to the piece's origin.
	 * @return true if the offset is the first or last element of the piece's
	 * shape.
	 */
	private boolean isEndpoint(Piece piece, int[] offset) {
		List<int[]> shape = piece.getShape();
		int[] first = shape.get(0);
		int[] last = shape.get(shape.size() - 1);
		return (offset[0] == first[0] && offset[1] == first[1]) || (offset[0] == last[0] && offset[1] == last[1]);
	}

	/**
	 * Gets the origin (top-left corner) of an adjacent piece.
	 *
	 * @param adjPiece The adjacent piece.
	 * @param adjR Row (y coordinate) of the adjacent tile.
	 * @param adjC Column (x coordinate) of the adjacent tile.
	 * @return The origin [x,y] of the adjacent piece.
	 */
	private Point getAdjacentPieceOrigin(Piece adjPiece, int adjR, int adjC) {
		Point origin = pieceOrigins.get(adjPiece);
		return origin != null ? origin : new Point(adjC, adjR);
	}

	/**
	 * Checks if two adjacent pieces are connected at their endpoints.
	 *
	 * @param newPiece The new piece being placed.
	 * @param newOffset Offset of the new piece's tile.
	 * @param adjPiece The adjacent existing piece.
	 * @param adjR Row of the adjacent tile.
	 * @param adjC Column of the adjacent tile.
	 * @return true if the connection is valid.
	 */
	private boolean arePiecesConnectedAtEndpoints(Piece newPiece, int[] newOffset, Piece adjPiece, int adjR, int adjC) {
		// Check if the tile from the new piece is an edge (or extremity)
		boolean isNewEndpoint = isEndpoint(newPiece, newOffset);
		// Check if the adjacent tile is an edge (or extremity) of its piece
		Point adjOrigin = getAdjacentPieceOrigin(adjPiece, adjR, adjC);
		int adjOffsetX = adjC - adjOrigin.x;
		int adjOffsetY = adjR - adjOrigin.y;
		boolean isAdjEndpoint = isEndpoint(adjPiece, new int[]{adjOffsetX, adjOffsetY});

		return isNewEndpoint && isAdjEndpoint;
	}











	/******************************************************** */
	/*
	* *******************************************************
	*                   GETTERS / SETTERS                   *
	* *******************************************************
	*
	 */
	/********************************************************* */

	
	/**
	 * Returns the piece at the given coordinates. Return null if no piece is
	 * placed at this position.
	 *
	 * @param row y coordinate in the grid
	 * @param col x coordinate in the grid
	 * @return the piece at this position in the grid
	 */
	public Piece getPieceAt(int row, int col) {
		// Weird bug that causes trouble, so to prevent ArrayOutOfBounds I check first
		if (!isWithinBounds(row, col)) {
			return null;
		}
		Piece special = specialPieceGrid[row][col];
		if (special != null) return special;
		
		return pieceGrid[row][col];
	}

	public Piece getPieceFromPieceGridAt(int row, int col){
		if (!isWithinBounds(row, col)) {
			return null;
		}
		return pieceGrid[row][col];
	}

	public Piece getPieceFromSpecialPieceGridAt(int row, int col){
		if (!isWithinBounds(row, col)) {
			return null;
		}
		return specialPieceGrid[row][col];
	}

	@Override
	public String toString() {
		String result = "";
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < colCount; j++) {
				result += grid[i][j].getLetter() + " ";
			}
			result += "\n";
		}
		return result;
	}

	/**
	 * Retrieves the tile at the specified coordinates.
	 *
	 * @param row the row index of the tile
	 * @param col the column index of the tile
	 * @return the tile at the specified location
	 */
	public Tile getTile(int row, int col) {
		return grid[row][col];
	}

	public void setTile(Tile type, int row, int col) {
		this.grid[row][col] = type;
	}

	/**
	 * Returns the number of rows in the board.
	 *
	 * @return the row count
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * Returns the number of columns in the board.
	 *
	 * @return the column count
	 */
	public int getColCount() {
		return colCount;
	}

	/**
	 * Returns the 2D array of pieces placed on the board.
	 *
	 * @return a 2D array representing the piece grid
	 */
	public Piece[][] getPieceGrid() {
		return pieceGrid;
	}

	public int getOriginalDragRow() {
		return originalDragRow;
	}

	public int getOriginalDragCol() {
		return originalDragCol;
	}

	public void setPlacementPreview(int row, int col, boolean isValid, Piece piece) {
		if (!isDragging) {
			this.currentPreview = new PlacementPreview(row, col, isValid, piece);
		}
	}

	public PlacementPreview getPreview() {
		return currentPreview;
	}

	public void setPreview(PlacementPreview preview) {
		this.currentPreview = preview;
	}

	public boolean isDragging() {
		return isDragging;
	}

	public Point getPieceOrigin(Piece piece) {
		return pieceOrigins.get(piece);
	}

	public List<int[]> getOriginalDragShape() {
		return originalDragShape;
	}

	public List<Piece.Direction> getOriginalDragTrack() {
		return originalDragTrack;
	}

	public Tile[][] getGrid() {
		return grid;
	}

	public Point getStairsPos() {
		return stairsPos;
	}

	public String getStringCheckPoint(Point p) {
		if (specialPieceGrid[p.x][p.y] != null && specialPieceGrid[p.x][p.y].isBridge()) {
			return "b";
		}
		switch (grid[p.x][p.y].getType()) {
			case DRAGON -> {return "d";}
			case EXIT -> {return "e";}
			case KEY -> {return "k";}
			case CHEST -> {return "c";}
			case STAIRS -> {return "s";}
			default -> {return "";}
		}
	}

	public Point getEntryPos(int player) {
		switch (player) {
			case 0 -> {return entryPos;}
			case 1 -> {return entryP1;}
			case 2 -> {return entryP2;}
		}
		return null;
	}

    public Piece[][] getSpecialPieceGrid() {
        return specialPieceGrid;
    }

	public String getNextTile(int player, Point pos, Point prev) {
		if (pos.x > 0 && grid[pos.x - 1][pos.y].getType() != TileType.DEFAULT && !(prev.x == pos.x - 1 && prev.y == pos.y)
		&& (player == 0 || (grid[pos.x - 1][pos.y].getPlayer() == player || grid[pos.x - 1][pos.y].getType() == TileType.STAIRS))) {
			return "U";
		}
		if (pos.y > 0 && grid[pos.x][pos.y - 1].getType() != TileType.DEFAULT && !(prev.x == pos.x && prev.y == pos.y - 1)
		&& (player == 0 || (grid[pos.x][pos.y - 1].getPlayer() == player || grid[pos.x][pos.y - 1].getType() == TileType.STAIRS))) {
			return "L";
		}
		if (pos.x + 1 < colCount && grid[pos.x + 1][pos.y].getType() != TileType.DEFAULT && !(prev.x == pos.x + 1 && prev.y == pos.y)
		&& (player == 0 || (grid[pos.x + 1][pos.y].getPlayer() == player || grid[pos.x + 1][pos.y].getType() == TileType.STAIRS))) {
			return "D";
		}
		if (pos.y + 1 < colCount && grid[pos.x][pos.y + 1].getType() != TileType.DEFAULT && !(prev.x == pos.x && prev.y == pos.y + 1)
		&& (player == 0 || (grid[pos.x][pos.y + 1].getPlayer() == player || grid[pos.x][pos.y + 1].getType() == TileType.STAIRS))) {
			return "R";
		}
		return "N";
	}

	public String getNextTileStairs(int player, Point pos) {
		String s = "";
		if (pos.x > 0 && grid[pos.x - 1][pos.y].getType() != TileType.DEFAULT 
		&& (player == 0 || (grid[pos.x - 1][pos.y].getPlayer() == player))) {
			s += "U";
		}
		if (pos.x + 1 < colCount && grid[pos.x + 1][pos.y].getType() != TileType.DEFAULT 
		&& (player == 0 || (grid[pos.x + 1][pos.y].getPlayer() == player))) {
			s += "D";
		}
		if (pos.y > 0 && grid[pos.x][pos.y - 1].getType() != TileType.DEFAULT
		&& (player == 0 || (grid[pos.x][pos.y - 1].getPlayer() == player))) {
			s += "L";
		}
		if (pos.y + 1 < colCount && grid[pos.x][pos.y + 1].getType() != TileType.DEFAULT
		&& (player == 0 || (grid[pos.x][pos.y + 1].getPlayer() == player))) {
			s += "R";
		}
		return s;
	}

	public String getNextTileBridge(int player, Point pos, Point prev) {
		if (prev.x - pos.x < 0 && grid[pos.x + 1][pos.y].getType() != TileType.DEFAULT
		&& (player == 0 || (grid[pos.x + 1][pos.y].getPlayer() == player))) {
			return "D";
		} else if (prev.x - pos.x > 0 && grid[pos.x - 1][pos.y].getType() != TileType.DEFAULT
		&& (player == 0 || (grid[pos.x - 1][pos.y].getPlayer() == player))) {
			return "U";
		} else if (prev.y - pos.y < 0 && grid[pos.x][pos.y + 1].getType() != TileType.DEFAULT
		&& (player == 0 || (grid[pos.x][pos.y + 1].getPlayer() == player))) {
			return "R";
		} else if (prev.y - pos.y > 0 && grid[pos.x][pos.y - 1].getType() != TileType.DEFAULT
		&& (player == 0 || (grid[pos.x][pos.y - 1].getPlayer() == player))) {
			return "L";
		}
		return "N";
	}
}
