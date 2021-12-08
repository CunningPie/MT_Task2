package ru.spbu.mas;

import jade.core.Agent;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class DefaultAgent extends Agent {
    public HashMap<String, Double> linkedAgentsState = new HashMap<>();
    public ArrayList<String> linkedAgents = new ArrayList<>();
    public double number;
    public double alpha;

    public boolean localVoting()
    {
        double sum = 0;

        for (Map.Entry<String, Double> entry : linkedAgentsState.entrySet())
            sum += (entry.getValue() - this.number);

        linkedAgentsState.clear();

        this.number = number + alpha * sum;

        if (alpha * sum < 0.0001)
            return true;

        return false;
    }


    @Override
    protected void setup(){
        int id = Integer.parseInt(getAID().getLocalName());
        alpha = 1.0 / (int)this.getArguments()[0];

        number = (double) this.getArguments()[1];

        linkedAgents = (ArrayList<String>) this.getArguments()[2];

        System.out.println("Agent #" + id + " setuped. Value: " + number + ". Linked with agents: " + String.join(" ", linkedAgents));

        try{
            TimeUnit.SECONDS.sleep(5);                 //1000 milliseconds is one second.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }


        addBehaviour(new FindAverage(this ));
    }
    @Override
    protected void takeDown() {
        System.out.println("Agent " + this.getAID().getLocalName() + " terminating");
    }
}
