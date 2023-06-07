package org.example;

import java.util.Arrays;
import java.util.Random;

public class QLearning {
    private final double ALPHA = 0.1;
    private final double GAMMA = 0.9;
    //private final double EPS = 0.4;//epsilon
    private final int MAX_EPOCH = 20000; //nbr max d'iter(lorsqu'il arrive à etat but)
    private final int GRID_SIZE = 3;
    private final int ACTION_SIZE = 4;//up,down,left,right

    private int [][]grid; //grille va stocker les recompenses(carré)
    private double[][]qTable = new double[GRID_SIZE*GRID_SIZE][ACTION_SIZE]; //stocker q value
    private int[][]actions ;
    private  int stateI; //etat actuelle de l'agent
    private  int stateJ;

    public QLearning(){
        actions = new int[][]{
                {0,-1}, //-1 hit atmchi à gauche(sens opposé de axe y )  (vers gauche)
                {0,1}, // vers droite
                {1,0},  //vers bas
                {-1,0} //vers le haut
        };
        //= new int [ GRID_SIZE][GRID_SIZE]
        grid = new int [][]{
                {0,1,0,0,0,0},
                {0,0,0,0,-1,0},//sens interdit et 0 f x hit x mgdich nt7rku biha
                {0,0,0,0,0,0},
                {-1,-1,-1,-1,-1,0},
                {0,0,0,0,0,0},
                {0,0,0,0,0,1}

        };
    }


    private void resetState(){
        //pour revenir à l'état initiale
        stateI = 2;
        stateJ = 0;
    }

//permet à l'agent de choisir une action à effectuer à partir de l'état actuel.
    private int  chooseAction(double eps ){
         Random rn =  new Random();
         double bestQ = 0 ;
         int act = 0;
         if(rn.nextDouble()<eps){
             //exploration
             act = rn.nextInt(ACTION_SIZE);
             //choisir une action aleatoire
             stateI = Math.max(0,Math.min(actions[act][0]+stateI,2));
             stateJ = Math.max(0,Math.min(actions[act][1]+stateJ,2));

         }else{
             //exploitation
             int st = stateI *GRID_SIZE+stateJ;
             //indice 5 : i=1 , j = 2 => 1*3+2=5
             //action entre 0 et 2
             for (int i = 0; i < ACTION_SIZE; i++) {
                 //cchoisir exploitation qui a la plus grande valeur q "qvalue" (meilleure )
                 if(qTable[st][i]>bestQ){
                     bestQ = qTable[st][i];
                     System.out.println("best qTable : "+bestQ);
                     act = i;
                 }
             }

         }
         return act;
    }

    // exécute l'action choisie par l'agent
    private  int executeAction ( int act){
        stateI = Math.max(0,Math.min(actions[act][0]+stateI,GRID_SIZE-1));
        stateJ = Math.max(0,Math.min(actions[act][1]+stateJ,GRID_SIZE-1));
        return stateI*GRID_SIZE+stateJ;
    }

    private boolean finished(){
        return grid[stateI][stateJ]==1;
    }
    private void showResult(){
        System.out.println("*******gTable*****************");
        for (double[]line:qTable){
            System.out.println("[");
            for (double qvalue:line){
                System.out.println(qvalue+",");
            }
            System.out.println("]");
        }

        System.out.println("");
        resetState();
       while (!finished()){
           int act =  chooseAction(0);
            System.out.println("State : "+(stateI*GRID_SIZE+" "+stateJ)+"action :  "+act);

           executeAction(act);
        }
        System.out.println("Final State : "+(stateI*GRID_SIZE+" "+stateJ));
    }
    public void runQlearning(){
        //definir l'etat actuellec
        int it = 0;
        int currentState;
        int nextState;
        int act , act1;
        while (it<MAX_EPOCH){
            resetState();
            while (!finished()){
                currentState=stateI*GRID_SIZE+stateJ;
                act = chooseAction(0.4);
                nextState = executeAction(act);
                act1 = chooseAction(0);
                qTable[currentState][act]=qTable[currentState][act]+ALPHA*(grid[stateI][stateJ]+GAMMA*qTable[nextState][act1] -qTable[currentState][act]);
            }
            it++;
        }

showResult();    }







}
