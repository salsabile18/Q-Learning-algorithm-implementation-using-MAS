package org.example;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.util.Arrays;
import java.util.Random;

public class LearningAgent extends Agent {

    private static final int ACTION_SIZE = 4;
    private static final int GRID_SIZE = 6;
    private static final double ALPHA = 0.1;
    private static final double GAMMA = 0.9;
    private static final int MAX_EPOCH = 100;

    private int[][] actions;
    private int[][] grid;
    private double[][] qTable;
    private int stateI;
    private int stateJ;

    @Override
    protected void setup() {
        actions = new int[][]{
                {0, -1}, // gauche
                {0, 1}, // droite
                {1, 0},  // bas
                {-1, 0} // haut
        };

        grid = new int[][]{
                // définir la grille li fiha khder et hmer
                {0, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, -1, 0},
                {0, 0, 0, 0, 0, 0},
                {-1, -1, -1, -1, -1, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1}
        };

        qTable = new double[GRID_SIZE * GRID_SIZE][ACTION_SIZE];

        try {
            DFAgentDescription dfAgentDescription = new DFAgentDescription();
            dfAgentDescription.setName(getAID());
            ServiceDescription serviceDescription = new ServiceDescription();
            serviceDescription.setName("agent");
            serviceDescription.setType("q-learning");
            dfAgentDescription.addServices(serviceDescription);
            DFService.register(this, dfAgentDescription);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }

        SequentialBehaviour sequentialBehaviour = new SequentialBehaviour();
        sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                resetState();
            }
        });
        sequentialBehaviour.addSubBehaviour(new Behaviour() {
            @Override
            public void action() {
                int it = 0;
                int currentState;
                int nextState;
                int act, act1;
                while (it < MAX_EPOCH) {
                    resetState();
                    while (!finished()) {
                        currentState = stateI * GRID_SIZE + stateJ;
                        act = chooseAction(0.4);
                        nextState = executeAction(act);
                        act1 = chooseAction(0);
                        qTable[currentState][act] = qTable[currentState][act] + ALPHA * (grid[stateI][stateJ] + GAMMA * qTable[nextState][act1] - qTable[currentState][act]);
                    }
                    it++;
                }

                showResult();
            }

            @Override
            public boolean done() {
                return grid[stateI][stateJ] == 1;
            }
        });

        sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription dfAgentDescription = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("ga");
                dfAgentDescription.addServices(serviceDescription);
                DFAgentDescription[] dfAgentDescriptions = null;
                try {
                    dfAgentDescriptions = DFService.search(myAgent, dfAgentDescription);
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
                ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
                aclMessage.addReceiver(dfAgentDescriptions[0].getName());
                aclMessage.setContent(Arrays.deepToString(qTable));
                send(aclMessage);
            }
        });

        addBehaviour(sequentialBehaviour);
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
    }

    private void resetState() {
        stateI = 2;
        stateJ = 0;
    }

    private int chooseAction(double eps) {
        Random rn = new Random();
        double bestQ = 0;
        int act = 0;
        if (rn.nextDouble() < eps) {
            // exploration
            act = rn.nextInt(ACTION_SIZE);
            stateI = Math.max(0, Math.min(actions[act][0] + stateI, 2));
            stateJ = Math.max(0, Math.min(actions[act][1] + stateJ, 2));
        } else {
            // exploitation
            int st = stateI * GRID_SIZE + stateJ;
            for (int i = 0; i < ACTION_SIZE; i++) {
                if (qTable[st][i] > bestQ) {
                    bestQ = qTable[st][i];
                    act = i;
                }
            }
        }
        return act;
    }

    private int executeAction(int act) {
        stateI = Math.max(0, Math.min(actions[act][0] + stateI, GRID_SIZE - 1));
        stateJ = Math.max(0, Math.min(actions[act][1] + stateJ, GRID_SIZE - 1));
        return stateI * GRID_SIZE + stateJ;
    }

    private boolean finished() {
        return grid[stateI][stateJ] == 1;
    }

    private void showResult() {
        System.out.println("*******qTable****************");
        for (double[] line : qTable) {
            System.out.print("[");
            for (double qValue : line) {
                System.out.print(qValue + ",");
            }
            System.out.println("]");
        }
        System.out.println();
        resetState();
        while (!finished()) {
            int act = chooseAction(0);
            System.out.println("State: " + (stateI * GRID_SIZE + " " + stateJ) + ", Action: " + act);
            executeAction(act);
        }
        System.out.println("Final State: " + (stateI * GRID_SIZE + " " + stateJ));
    }
}
