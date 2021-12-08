package ru.spbu.mas;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FindAverage extends CyclicBehaviour {
    private final DefaultAgent agent;
    private boolean firstSend = true;
    private boolean finishProcess = false;
    private boolean isInforming = false;
    private int startToBreak = 5;
    private final double prb = 0.9;
    private boolean sended = false;

    FindAverage(DefaultAgent agent) {
        this.agent = agent;
    }


    private double noise(double min, double max)
    {
        return ((Math.random() * (max - min)) + min);
    }

    private void trySendMessage(ACLMessage msg, double probability)
    {
        if (Math.random() < probability || startToBreak > 0) {
            startToBreak--;
            agent.send(msg);
        }
    }

    private void informFinish(AID receiver)
    {
        ACLMessage dataMessage = new ACLMessage(ACLMessage.INFORM);
        dataMessage.setContent(String.valueOf(agent.number));
        String str;

        if (receiver == null) {
            for (String linkedAgent : agent.linkedAgents)
                dataMessage.addReceiver(new AID(linkedAgent, AID.ISLOCALNAME));
            str = String.join(" ", agent.linkedAgents);
        }
        else{
            dataMessage.addReceiver(receiver);
            str = receiver.getLocalName();
        }

        isInforming = true;
        System.out.println("Agent #" + this.agent.getLocalName() + " informed: " + agent.number + " Agents: " + str);

        if (Objects.equals(this.agent.getLocalName(), "0") && !sended) {
            System.out.println("\u001B[31m" + "Agent #" + this.agent.getLocalName() + " send message to center with value: " + agent.number + "\u001B[0m");
            sended = true;
        }

        trySendMessage(dataMessage, prb);
    }

    private void requestState(AID receiver)
    {
        ACLMessage dataMessage = new ACLMessage(ACLMessage.REQUEST);
        dataMessage.setContent("STATE");
        StringBuilder str = new StringBuilder();

        if (receiver == null)
            for (String linkedAgent : agent.linkedAgents) {
                dataMessage.addReceiver(new AID(linkedAgent, AID.ISLOCALNAME));
                str.append(" ").append(linkedAgent);
            }
        else
        {
            dataMessage.addReceiver(receiver);
            str.append(receiver.getLocalName());
        }
        System.out.println("Agent #" + this.agent.getLocalName() + " requested state from Agents:" + str);

        trySendMessage(dataMessage, prb);
    }

    private void proposeState(AID sender)
    {
        ACLMessage reply = new ACLMessage(ACLMessage.PROPOSE);
        reply.setContent(String.valueOf(agent.number + noise(0, 0.01)));
        reply.addReceiver(sender);

        trySendMessage(reply, prb);

        System.out.println("Agent #" + this.agent.getLocalName() + " proposed state: " + reply.getContent() + " to Agent: " + sender.getLocalName());
    }

    @Override
    public void action() {
        ACLMessage msg = agent.receive();

        // Начало подсчета, агент запрашивает значения своих соседей
        if (firstSend) {
            requestState(null);
            firstSend = false;
        }
        // Получение сообщения
        if (msg != null)
        {
            // Ответ на запрос значения
            if (msg.getPerformative() == ACLMessage.REQUEST)
            {
                if (!finishProcess) {
                    proposeState(msg.getSender());
                }
                else {
                    informFinish(msg.getSender());
                }
            }
            // Получение сообщения со значением
            else if (msg.getPerformative() == ACLMessage.PROPOSE) {
                if (!finishProcess) {
                    if (agent.linkedAgentsState.containsKey(msg.getSender().getLocalName())) {
                        finishProcess = agent.localVoting();
                        if (!finishProcess) {
                            requestState(null);
                        }
                    } else {
                        requestState(msg.getSender());
                    }

                    agent.linkedAgentsState.put(msg.getSender().getLocalName(), Double.valueOf(msg.getContent()));
                }
                else {
                    informFinish(null);
                }
            }
            else if (msg.getPerformative() == ACLMessage.INFORM && !isInforming) {
                agent.number = Double.parseDouble(msg.getContent());
                informFinish(null);
            }
        }
    }
}
