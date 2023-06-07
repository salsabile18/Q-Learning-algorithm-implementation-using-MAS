package org.example;

import jade.wrapper.AgentContainer;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ControllerException;

public class MainContainer {

   public static void main(String[] args) throws ControllerException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.GUI,"true");
        AgentContainer agentContainer = runtime.createMainContainer(profile);
        agentContainer.start();
    }

}