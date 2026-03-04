package utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.imageio.ImageIO;
import model.Board;
import model.Piece;
import model.PieceBag;
import model.PieceBlueprint;
import model.Tile;

/**
 * An utils class which stores methods related to file.
 */
public class FileHandler {

	// File paths of the sprites
	public static final String PLAYER_IDLE_SPRITE = "/player/IDLE.png";
	public static final String PLAYER_RUN_SPRITE = "/player/RUN.png";





    public static void writeInMapFile(String fileName, Set<Piece> pieces, List<Board> boards) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("res/maps/" + fileName + ".map"))) {
            pw.println("HIGHSCORE:0");
            pw.println("DONE:false");
            pw.println("LIMITS:" + pieces.size());
            pw.print("PIECE:");
            for (Piece p : pieces) {
                pw.print(p.getIndex());
                pw.print(',');
            }
            pw.println();
            for (int i = 0; i < boards.size(); i++){
                pw.println("E" + (i+1) + "{");

                // Print board's grid
                Board board = boards.get(i);
                for (int j = 0; j < board.getColCount(); j++) {
                    for (int k = 0; k < board.getRowCount(); k++) {
                        Tile tile = board.getGrid()[j][k];
                        char c;
                        switch (tile.getType()) {
                            case Tile.TileType.DEFAULT -> c = '.';
                            case Tile.TileType.ENTRY -> c = 'E';
                            case Tile.TileType.KEY -> c = 'K';
                            case Tile.TileType.CHEST -> c = 'C';
                            case Tile.TileType.DRAGON -> c = 'D';
                            case Tile.TileType.EXIT -> c = '>';
                            case Tile.TileType.EMPTY -> c = '1';
                            case Tile.TileType.STAIRS -> c = 'S';
                            case Tile.TileType.PATH -> c = '.';
                            default -> throw new AssertionError();
                        }
                        pw.print(c);
                    }
                    pw.println();
                }
                pw.println("}");
            }
            pw.println("BEST_TIME:");
        } catch (IOException e) {}
    }










    /**
     * Retrieves the best time from the map file
     * 
     * @param file  map file
     * @return      the best time ever done to complete the level as format MM:SS 
     *              or an empty string if best time isn't defined
     */
    public static String getBestTimeFromFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        String bestTime = "";
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("BEST_TIME:")) {
                bestTime = line.substring(10); // "BEST_TIME:".length() = 10
                break;
            }
        }
        scanner.close();
        return bestTime;
    }

    /**
     * Update the new best time in the map file
     * 
     * @param file          map file that needs to be updated
     * @param newBestTime   new best time to save
     */
    public static void updateBestTimeAndHighscore(File file, String newBestTime, int highscore) throws IOException {
        Path path = Paths.get(file.getAbsolutePath());
        List<String> lines = Files.readAllLines(path);
        
        boolean updated = false;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith("HIGHSCORE:")) {
                lines.set(i, "HIGHSCORE:" + highscore);
                updated = true;
            }
            if (lines.get(i).startsWith("DONE")) {
                lines.set(i, "DONE:true");
                updated = true;
            }
            if (lines.get(i).startsWith("BEST_TIME:")) {
                lines.set(i, "BEST_TIME:" + newBestTime);
                updated = true;
                break;
            }
        }
        
        // If the BEST_TIME line does not exist, we do not create it
        // because we assume that the file format is fixed
        
        if (updated) {
            Files.write(path, lines);
        }
    }



    public static PieceBag parsePieceBag(File file) throws FileNotFoundException {
        ArrayList<Piece> pieceBag = new ArrayList<>();
        Scanner scanner = new Scanner(file);
        scanner.nextLine();
        scanner.nextLine();
        String[] line = scanner.nextLine().split(":");
        
        if (!line[0].equals("LIMITS"))
            throw new FileNotFoundException();
        int limits = (line[1].equals("NONE")) ? 15 : Integer.parseInt(line[1]);
        line = scanner.nextLine().split(":");
        if (!line[0].equals("PIECE"))
            throw new FileNotFoundException();
        
        String[] pieces = line[1].split(",");
        for (int i = 0; i < pieces.length; i++) {
            PieceBlueprint pieceBlueprint = PieceBlueprint.getBlueprint(Integer.parseInt(pieces[i]));
            boolean isBridge = (pieceBlueprint == PieceBlueprint.BRIDGE);
            pieceBag.add(new Piece(pieceBlueprint, isBridge, 1));
        }
        scanner.close();
        return new PieceBag(pieceBag, limits);
    }

	public static ArrayList<char[][]> parseMap(File file) throws FileNotFoundException {
        ArrayList<char[][]> map = new ArrayList<>();
		int	counter = 0;
        ArrayList<Integer> lengthLevel = new ArrayList<>();
		String line;
        boolean checkLevel = false;
		Scanner scanner = new Scanner(file);
	
        scanner.nextLine();
        scanner.nextLine();
        line = scanner.nextLine();
        counter = 0;
		while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (!checkLevel && line.contains("{")) {
                checkLevel = true;
                continue;
            }
            if (checkLevel && line.contains("}")) {
                lengthLevel.add(counter);
                checkLevel = false;
            }
            if (checkLevel) {
                counter++;
            }
		}
        scanner.close();
        scanner = new Scanner(file);
        scanner.nextLine();
        char[][] level = new char[1][1];
        for (int i = 0; i < lengthLevel.size(); i++) {
            counter = 0;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (!checkLevel && line.contains("{")) {
                    counter = 0;
                    checkLevel = true;
                    level = new char[lengthLevel.get(i)][];
                    continue;
                }
                if (checkLevel && line.contains("}")) {
                    map.add(level);
                    checkLevel = false;
                }
                if (checkLevel) {
                    level[counter] = new char[line.length()];
                    for (int j = 0; j < line.length(); j++){
                        level[counter][j] = line.charAt(j);
                    }
                    counter++;
                }
            }
        }
        scanner.close();
        return map;
    }

    public static ArrayList<String[][]> parseMapMult(File file) throws FileNotFoundException {
        ArrayList<String[][]> map = new ArrayList<>();
		int	counter = 0;
        ArrayList<Integer> lengthLevel = new ArrayList<>();
		String line;
        boolean checkLevel = false;
		Scanner scanner = new Scanner(file);
	
        scanner.nextLine();
        scanner.nextLine();
        line = scanner.nextLine();
        counter = 0;
		while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (!checkLevel && line.contains("{")) {
                checkLevel = true;
                continue;
            }
            if (checkLevel && line.contains("}")) {
                lengthLevel.add(counter);
                checkLevel = false;
            }
            if (checkLevel) {
                counter++;
            }
		}
        scanner.close();
        scanner = new Scanner(file);
        scanner.nextLine();
        String[][] level = new String[1][1];
        for (int i = 0; i < lengthLevel.size(); i++) {
            counter = 0;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (!checkLevel && line.contains("{")) {
                    counter = 0;
                    checkLevel = true;
                    level = new String[lengthLevel.get(i)][];
                    continue;
                }
                if (checkLevel && line.contains("}")) {
                    map.add(level);
                    checkLevel = false;
                }
                if (checkLevel) {
                    int len = 0;
                    for (int w = 0; w < line.length(); w++) {
                        char c = line.charAt(w);
                        if (!(c == 'K' || c == 'E' || c == 'D' || c == '>' || c == 'C')) {
                            len++;
                        }
                    }
                    level[counter] = new String[len];
                    int y = 0;
                    for (int j = 0; j < len; j++){
                        char c = line.charAt(y);
                        if ((c == 'K' || c == 'E' || c == 'D' || c == '>' || c == 'C')) {
                            level[counter][j] = String.valueOf(c) + String.valueOf(line.charAt(y + 1));
                            y++;
                        }
                        else {
                            level[counter][j] = String.valueOf(c);
                        }
                        y++;
                    }
                    counter++;
                }
            }
        }
        scanner.close();
        return map;
    }

    /**
     * This method aims to return a sprite from a given filepath. It works for
     * sprite sheet with a lot of sprites in it, as well as for single sprite
     * images.
     *
     * @param filepath the image's path
     * @param x x coordinate of the sprite in the sprite sheet (if the sprite
     * sheet has a line of 4 sprites and we want to load the second sprite we
     * would put 1)
     *
     * @param y y coordinate of the sprite in the sprite sheet (if the sprite
     * sheet has a column of 5 sprites and we want to load the first sprite we
     * would put 0)
     *
     * @param width width of the sprite in tiles which is the scalar of default
     * size.
     * @param height height of the sprite in tiles which is the scalar of
     * default size.
     *
     * For these two above take an exemple : a bench will probably be 2 or 3
     * tiles width and 1 tile height. The size of the tile is determined by
     * default size which allow to use this method for every sprite regardless
     * of their size in pixel.
     *
     * @param defaultSize the default size of a sprite is commonly 16 or 32
     * pixels
     * @return
     */
    public static BufferedImage getSpriteImage(String filepath, int x, int y, int width, int height, int defaultSize) {
        BufferedImage img = getSpriteSheet(filepath);
        if (img == null) {
            return null;
        }
        return img.getSubimage(x * defaultSize, y * defaultSize, width * defaultSize, height * defaultSize);
    }

    /**
     * This method load and returns a sprite sheet from the given filepath.
     *
     * @param filepath filepath to the sprite sheet
     * @return a BufferedImage of the the sprite sheet
     */
    public static BufferedImage getSpriteSheet(String filepath) {

        BufferedImage img = null;
        try {
            img = ImageIO.read(FileHandler.class.getResourceAsStream(filepath)); // static method can't use getClass()
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    public static BufferedImage rotateImage(BufferedImage buffImage, double angle) {
        double radian = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(radian));
        double cos = Math.abs(Math.cos(radian));
    
        int width = buffImage.getWidth();
        int height = buffImage.getHeight();
    
        int nWidth = (int) Math.floor((double) width * cos + (double) height * sin);
        int nHeight = (int) Math.floor((double) height * cos + (double) width * sin);
    
        BufferedImage rotatedImage = new BufferedImage(
                nWidth, nHeight, BufferedImage.TYPE_INT_ARGB);
    
        Graphics2D graphics = rotatedImage.createGraphics();
    
        graphics.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    
        graphics.translate((nWidth - width) / 2, (nHeight - height) / 2);
        // rotation around the center point
        graphics.rotate(radian, (double) (width / 2), (double) (height / 2));
        graphics.drawImage(buffImage, 0, 0, null);
        graphics.dispose();
    
        return rotatedImage;
    }
}
