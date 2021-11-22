package ru.spbu.mas;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import java.util.ArrayList;
import java.util.List;

public class MainController {
    private static final int numberOfAgents = 10;
    private final ArrayList<String>[] adjMatrix = new ArrayList[numberOfAgents];

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private void initAdjMatrix(int matrCase)
    {
        switch (matrCase){
            // полный граф
            case 0:
                for (int i = 0; i < numberOfAgents; i++) {
                    adjMatrix[i] = new ArrayList<>();
                    for (int j = 0; j < numberOfAgents; j++)
                        if (j != i)
                            adjMatrix[i].add(String.valueOf(j));
                }
                break;
            // цепочка
            case 1:
                adjMatrix[0] = new ArrayList<>();
                adjMatrix[0].add(String.valueOf(1));

                for (int i = 1; i < numberOfAgents - 1; i++) {
                    adjMatrix[i] = new ArrayList<>();
                    adjMatrix[i].add(String.valueOf(i - 1));
                    adjMatrix[i].add(String.valueOf(i + 1));
                }

                adjMatrix[numberOfAgents - 1] = new ArrayList<>();
                adjMatrix[numberOfAgents - 1].add(String.valueOf(numberOfAgents - 2));

                break;
            // цикл
            case 2:
                adjMatrix[0] = new ArrayList<>();
                adjMatrix[0].add(String.valueOf(1));
                adjMatrix[0].add(String.valueOf(numberOfAgents - 1));

                for (int i = 1; i < numberOfAgents - 1; i++) {
                    adjMatrix[i] = new ArrayList<>();
                    adjMatrix[i].add(String.valueOf(i - 1));
                    adjMatrix[i].add(String.valueOf(i + 1));
                }

                adjMatrix[numberOfAgents - 1] = new ArrayList<>();
                adjMatrix[numberOfAgents - 1].add(String.valueOf(0));
                adjMatrix[numberOfAgents - 1].add(String.valueOf(numberOfAgents - 2));

                break;
            // пример 4
            case 3:
                adjMatrix[0] = new ArrayList<>();
                adjMatrix[0].add("1");

                adjMatrix[1] = new ArrayList<>();
                adjMatrix[1].add("0");
                adjMatrix[1].add("6");

                adjMatrix[2] = new ArrayList<>();
                adjMatrix[2].add("4");

                adjMatrix[3] = new ArrayList<>();
                adjMatrix[3].add("4");

                adjMatrix[4] = new ArrayList<>();
                adjMatrix[4].add("2");
                adjMatrix[4].add("3");
                adjMatrix[4].add("8");

                adjMatrix[5] = new ArrayList<>();
                adjMatrix[5].add("6");
                adjMatrix[5].add("8");

                adjMatrix[6] = new ArrayList<>();
                adjMatrix[6].add("1");
                adjMatrix[6].add("5");
                adjMatrix[6].add("7");

                adjMatrix[7] = new ArrayList<>();
                adjMatrix[7].add("6");
                adjMatrix[7].add("9");

                adjMatrix[8] = new ArrayList<>();
                adjMatrix[8].add("4");
                adjMatrix[8].add("5");
                adjMatrix[8].add("9");

                adjMatrix[9] = new ArrayList<>();
                adjMatrix[9].add("7");
                adjMatrix[9].add("8");
                break;
            // пример 5
            case 4:
                adjMatrix[0] = new ArrayList<>();
                adjMatrix[0].add("1");
                adjMatrix[0].add("3");

                adjMatrix[1] = new ArrayList<>();
                adjMatrix[1].add("0");
                adjMatrix[1].add("2");

                adjMatrix[2] = new ArrayList<>();
                adjMatrix[2].add("1");
                adjMatrix[2].add("3");

                adjMatrix[3] = new ArrayList<>();
                adjMatrix[3].add("0");
                adjMatrix[3].add("2");
                adjMatrix[3].add("4");
                adjMatrix[3].add("6");

                adjMatrix[4] = new ArrayList<>();
                adjMatrix[4].add("3");
                adjMatrix[4].add("5");

                adjMatrix[5] = new ArrayList<>();
                adjMatrix[5].add("4");
                adjMatrix[5].add("6");
                adjMatrix[5].add("7");
                adjMatrix[5].add("9");

                adjMatrix[6] = new ArrayList<>();
                adjMatrix[6].add("3");
                adjMatrix[6].add("5");

                adjMatrix[7] = new ArrayList<>();
                adjMatrix[7].add("5");
                adjMatrix[7].add("8");

                adjMatrix[8] = new ArrayList<>();
                adjMatrix[8].add("7");
                adjMatrix[8].add("9");

                adjMatrix[9] = new ArrayList<>();
                adjMatrix[9].add("5");
                adjMatrix[9].add("8");
                break;
        }
    }

    void initAgents() {
        Runtime rt = Runtime.instance();

        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.MAIN_PORT, "10098");
        p.setParameter(Profile.GUI, "true");
        ContainerController cc = rt.createMainContainer(p);

        initAdjMatrix(2);

        int average = 0;

        try
        {
            for (int i = 0; i < MainController.numberOfAgents; i++){
                Object[] args = new Object[3];

                args[0] = MainController.numberOfAgents;
                args[1] = getRandomNumber(0, 10);
                args[2] = adjMatrix[i];

                average += (int)args[1];
                AgentController agent = cc.createNewAgent(Integer.toString(i), "ru.spbu.mas.DefaultAgent", args);
                System.out.println("Agent " + i + " with value: " + args[1] + " is created.");
                agent.start();
            }
            System.out.println("Agents created. Average value: " + (double)average / MainController.numberOfAgents);


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
