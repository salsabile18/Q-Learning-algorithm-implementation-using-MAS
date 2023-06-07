package org.example;

import java.util.ArrayList;
import java.util.List;

public class QUtils {
    public static final int MAX_EPOCH = 5;
    public static final double ALPHA = 0.1;
    public static final double GAMMA = 0.9;
    public static final int GRID_SIZE = 6;
    public static final int ACTION_SIZE = 4;

     public static final List<double[][]> qTables = new ArrayList<>();
    public static final double[][] bestQTable = null;

    public static int[][] grid;
    public static final double[][] qTable = new double[GRID_SIZE * GRID_SIZE][ACTION_SIZE];
    public static int[][] actions;
    public static int stateI;
    public static int stateJ;
    public static final double qValue = 0;
}
