package org.example;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

public class SimpleContainer {
    public static void main(String[] args) throws ControllerException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST,"localhost");
        AgentContainer agentContainer= runtime.createAgentContainer(profile);
        for (int i = 0; i < QUtils.MAX_EPOCH; i++) {
            AgentController learningAgent =agentContainer.createNewAgent("learningAgent"+i,LearningAgent.class.getName(),new Object[]{});
           //agentContainer.start();
           learningAgent.start();
        }

        AgentController masteragent = agentContainer.createNewAgent("masteragent",MasterAgent.class.getName(),new Object[]{});
        masteragent.start();
    }
}