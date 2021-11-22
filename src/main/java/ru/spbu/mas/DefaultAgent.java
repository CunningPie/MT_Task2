package ru.spbu.mas;

import jade.core.Agent;

import java.util.ArrayList;

import java.util.List;

public class DefaultAgent extends Agent {
    public List<String> linkedAgents = new ArrayList<>();
    public int[] numbers;

    /*
    public boolean checkLeaf()
    {
        int num = Integer.parseInt(this.getLocalName());
        int neighbors = 0;

        for (int i = 0; i < adjMatrix.length; i++)
            if (adjMatrix[num][i] == true)
                neighbors++;

        if (neighbors > 1)
            return false;

        return true;
    }*/


    @Override
    protected void setup(){
        int id = Integer.parseInt(getAID().getLocalName());
        int N = (int)this.getArguments()[0];
        numbers = new int[N];

        linkedAgents = (List<String>) this.getArguments()[2];

        for (int j = 0; j < N; j++)
            numbers[j] = -1;

        numbers[id] = (int)this.getArguments()[1];

        addBehaviour(new FindAverage(this ));
        System.out.println("Agent #" + id + " setuped. Value: " + (int)this.getArguments()[1] + ". Linked with agents: " + String.join(" ", linkedAgents));
    }
    @Override
    protected void takeDown() {
        System.out.println("Agent " + this.getAID().getLocalName() + " terminating");
    }
}
