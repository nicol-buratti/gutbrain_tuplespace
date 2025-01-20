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
            Object[] proCytokines = space.query(new ActualField("CYTOKINE"), new ActualField(CytokineType.PRO_INFLAMMATORY), new FormalField(Integer.class));
            Object[] antiCytokines = space.query(new ActualField("CYTOKINE"), new ActualField(CytokineType.NON_INFLAMMATORY), new FormalField(Integer.class));
            int proNumber = (int) proCytokines[2];
            int antiNumber = (int) antiCytokines[2];
            int diff = proNumber - antiNumber;
            if (diff > 0) {
                int inflammation = (diff * 100) / (proNumber - antiNumber) % 100;
                if (random.nextInt(100) < inflammation)
                    changeState();
            }
        }
    }

    @SneakyThrows
    private void changeState() {
        if (state == NeuronState.HEALTHY) {
            state = NeuronState.DAMAGED;
            Object[] healthyNeurons = space.get(new ActualField("NEURON"), new ActualField(NeuronState.HEALTHY), new FormalField(Integer.class));
            space.put(healthyNeurons[0], healthyNeurons[1], (int) healthyNeurons[2] - 1);
            Object[] damagedNeurons = space.get(new ActualField("NEURON"), new ActualField(NeuronState.DAMAGED), new FormalField(Integer.class));
            space.put(damagedNeurons[0], damagedNeurons[1], (int) damagedNeurons[2] + 1);
        } else if (state == NeuronState.DAMAGED) {
            state = NeuronState.DEAD;
            Object[] damagedNeurons = space.get(new ActualField("NEURON"), new ActualField(NeuronState.DAMAGED), new FormalField(Integer.class));
            space.put(damagedNeurons[0], damagedNeurons[1], (int) damagedNeurons[2] - 1);
        }
    }
}
