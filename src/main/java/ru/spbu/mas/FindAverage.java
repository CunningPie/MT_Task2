package ru.spbu.mas;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Arrays;
import java.util.Objects;

public class FindAverage extends CyclicBehaviour {
    private final DefaultAgent agent;
    private boolean firstSend = true;
    private boolean finishProcess = false;
    private boolean isInforming = false;
    private boolean needToSend = true;

    FindAverage(DefaultAgent agent) {
        this.agent = agent;
    }

    private String arrToString(int [] arr)
    {
        StringBuilder res = new StringBuilder();
        for (int j : arr) res.append(j).append(" ");

        return res.toString();
    }


    @Override
    public void action() {
        ACLMessage msg = agent.receive();

        // Начало подсчета, выполняется один раз
        if (firstSend) {
            String newContent = "";

            for (int i = 0; i < agent.linkedAgents.size(); i++) {
                ACLMessage dataMessage = new ACLMessage(ACLMessage.PROPOSE);
                newContent = arrToString(agent.numbers);
                dataMessage.setContent(newContent);

                for (int j = 0; j < agent.linkedAgents.size(); j++)
                    dataMessage.addReceiver(new AID(agent.linkedAgents.get(j), AID.ISLOCALNAME));

                agent.send(dataMessage);
            }

            System.out.println("Agent #" + this.agent.getLocalName() + " send: " + newContent + " to Agents: " + String.join(" ", agent.linkedAgents));
            firstSend = false;
        }
        // Получение сообщения
        if (msg != null)
        {
            //System.out.println("Agent #" + this.agent.getLocalName() + " received message: " + msg.getContent() +" from Agent #" + msg.getSender().getLocalName());
            // Проверка сообщения на продолжение подсчета
            if (msg.getPerformative() == ACLMessage.PROPOSE && !finishProcess) {
                String[] splited = msg.getContent().split(" ");

                for (int i = 0; i < agent.numbers.length; i++) {
                    int newNum = Integer.parseInt(splited[i]);

                    if (newNum != -1 && newNum != agent.numbers[i]) {
                        agent.numbers[i] = newNum;
                        needToSend = true;
                    }
                }
                finishProcess = Arrays.stream(agent.numbers).noneMatch(x -> x == -1);

                if (needToSend && !finishProcess) {
                    ACLMessage dataMessage = new ACLMessage(ACLMessage.PROPOSE);
                    String newContent = arrToString(agent.numbers);
                    dataMessage.setContent(newContent);

                    StringBuilder receivers = new StringBuilder();

                    for (int j = 0; j < agent.linkedAgents.size(); j++)
                        if (!Objects.equals(agent.linkedAgents.get(j), msg.getSender().getLocalName())) {
                            dataMessage.addReceiver(new AID(agent.linkedAgents.get(j), AID.ISLOCALNAME));
                            receivers.append(agent.linkedAgents.get(j)).append(" ");
                        }

                    agent.send(dataMessage);
                    System.out.println("Agent #" + this.agent.getLocalName() + " send: " + newContent + " to Agents: " + receivers);
                    needToSend = false;
                }

                if (finishProcess)
                {
                    int sum = Arrays.stream(agent.numbers).sum();
                    //System.out.println("Agent #" + this.agent.getLocalName() + " calculate average value: " + (double)sum / agent.numbers.length);

                    if (Objects.equals(this.agent.getLocalName(), "0"))
                        System.out.println("Agent #" + this.agent.getLocalName() + " send message to center with value: " + (double)sum / agent.numbers.length);

                    ACLMessage informMessage = new ACLMessage(ACLMessage.INFORM);
                    String newContent = String.valueOf((double)sum / agent.numbers.length);
                    informMessage.setContent(newContent);

                    for (int j = 0; j < agent.linkedAgents.size(); j++)
                        informMessage.addReceiver(new AID(agent.linkedAgents.get(j), AID.ISLOCALNAME));

                    isInforming = true;
                    finishProcess = true;
                    agent.send(informMessage);
                    agent.doDelete();
                }
            }
            // если полученное сообщение - информирование о конце работы, и текущий агент еще не предупреждал связанных агентов о завершении процесса
            else if (msg.getPerformative() == ACLMessage.INFORM && !isInforming)
            {
                if (Objects.equals(this.agent.getLocalName(), "0"))
                    System.out.println("Agent #" + this.agent.getLocalName() + " send message to center with value: " + msg.getContent());

                System.out.println("Agent #" + this.agent.getLocalName() + " finished work.");

                ACLMessage dataMessage = new ACLMessage(ACLMessage.INFORM);
                String newContent = "Finish process";
                dataMessage.setContent(newContent);

                for (int j = 0; j < agent.linkedAgents.size(); j++)
                    if (!Objects.equals(agent.linkedAgents.get(j), msg.getSender().getLocalName())) {
                        dataMessage.addReceiver(new AID(agent.linkedAgents.get(j), AID.ISLOCALNAME));
                    }

                isInforming = true;
                agent.send(dataMessage);
                agent.doDelete();
            }
        }
    }
}
