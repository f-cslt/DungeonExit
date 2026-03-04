package model;

public enum PieceBlueprint {
    PIECE_01(new int[][]{
            {1, 1, 1},
            {0, 0, 1},
            {0, 0, 1}
    }),
    PIECE_02(new int[][]{
            {1, 0, 1},
            {1, 1, 1}
    }),
    PIECE_03(new int[][]{
            {1, 1, 1, 1}
    }),
    PIECE_04(new int[][]{
            {1, 1, 0},
            {0, 1, 1}
    }),
    PIECE_05(new int[][]{
            {1, 1, 1}
    }),
    PIECE_06(new int[][]{
            {1, 1, 0},
            {0, 1, 1},
            {0, 0, 1}
    }),
    PIECE_07(new int[][]{
            {1, 0, 0},
            {1, 1, 1}
    }),
    PIECE_08(new int[][]{
            {1, 0, 0, 0},
            {1, 1, 1, 1}
    }),
    PIECE_09(new int[][]{
            {1, 1, 0},
            {0, 1, 0},
            {0, 1, 1}
    }),
    PIECE_10(new int[][]{
            {1, 1, 1, 1, 1}
    }),
    PIECE_11(new int[][]{
            {1, 0},
            {1, 1}
    }),
    PIECE_12(new int[][]{
            {1}
    }),
    PIECE_13(new int[][]{
            {1, 1}
    }),
    PIECE_14(new int[][]{
            {1, 0},
            {1, 1},
            {0, 1},
            {0, 1}
    }),
    BRIDGE(new int[][]{{1}});


    public final int[][] blueprint;

    public static PieceBlueprint getBlueprint(int index) {
        switch (index) {
            case 1 -> {return PIECE_01;}
            case 2 -> {return PIECE_02;}
            case 3 -> {return PIECE_03;}
            case 4 -> {return PIECE_04;}
            case 5 -> {return PIECE_05;}
            case 6 -> {return PIECE_06;}
            case 7 -> {return PIECE_07;}
            case 8 -> {return PIECE_08;}
            case 9 -> {return PIECE_09;}
            case 10 -> {return PIECE_10;}
            case 11 -> {return PIECE_11;}
            case 12 -> {return PIECE_12;}
            case 13 -> {return PIECE_13;}
            case 14 -> {return PIECE_14;}
            case 15 -> {return BRIDGE;}

            default -> throw new AssertionError();
        }
    }

    public static int getIdBlueprint(PieceBlueprint blueprint) {
        switch (blueprint) {
            case PIECE_01 -> {return 1;}
            case PIECE_02 -> {return 2;}
            case PIECE_03 -> {return 3;}
            case PIECE_04 -> {return 4;}
            case PIECE_05 -> {return 5;}
            case PIECE_06 -> {return 6;}
            case PIECE_07 -> {return 7;}
            case PIECE_08 -> {return 8;}
            case PIECE_09 -> {return 9;}
            case PIECE_10 -> {return 10;}
            case PIECE_11 -> {return 11;}
            case PIECE_12 -> {return 12;}
            case PIECE_13 -> {return 13;}
            case PIECE_14 -> {return 14;}
            case BRIDGE -> {return 15;}

            default -> throw new AssertionError();
        }
    }

    PieceBlueprint(int[][] blueprint) {
        this.blueprint = copyArray(blueprint);
    }
    
    public int[][] getBlueprint() {
        return copyArray(blueprint);
    }
    
    private static int[][] copyArray(int[][] original) {
        if (original == null) return null;
        
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = new int[original[i].length];
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }
}
