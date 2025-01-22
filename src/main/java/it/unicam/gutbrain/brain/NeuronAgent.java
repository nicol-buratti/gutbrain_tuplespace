package it.unicam.gutbrain.brain;

import lombok.SneakyThrows;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.Random;

public class NeuronAgent implements Runnable {

    private final Space space;
    private NeuronState state;

    public NeuronAgent(Space space, NeuronState type) {
        this.space = space;
        this.state = type;
    }

    @Override
    @SneakyThrows
    public void run() {
        Random random = new Random();
        while (state != NeuronState.DEAD) {
            int proNumber = (int) space.query(new ActualField("CYTOKINE"), new ActualField(CytokineType.PRO_INFLAMMATORY), new FormalField(Integer.class))[2];
            int antiNumber = (int) space.query(new ActualField("CYTOKINE"), new ActualField(CytokineType.NON_INFLAMMATORY), new FormalField(Integer.class))[2];
            int diff = proNumber - antiNumber;
            int sum = proNumber + antiNumber;

            if (diff <= 0)
                continue;
            int inflammation = (diff * 100) / sum;
            if (random.nextInt(100) < inflammation) {
                changeState();
            }
        }
    }

    @SneakyThrows
    private void changeState() {
        Object[] currentNeurons = space.get(new ActualField("NEURON"), new ActualField(state), new FormalField(Integer.class));
        space.put(currentNeurons[0], currentNeurons[1], (int) currentNeurons[2] - 1);

        if (state == NeuronState.HEALTHY)
            state = NeuronState.DAMAGED;
        else if (state == NeuronState.DAMAGED)
            state = NeuronState.DEAD;

        if (state != NeuronState.DEAD) {
            Object[] nextNeurons = space.get(new ActualField("NEURON"), new ActualField(state), new FormalField(Integer.class));
            space.put(nextNeurons[0], nextNeurons[1], (int) nextNeurons[2] + 1);
        }
    }
}
