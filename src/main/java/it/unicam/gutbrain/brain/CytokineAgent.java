package it.unicam.gutbrain.brain;

import lombok.SneakyThrows;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

public class CytokineAgent implements Runnable {

    private final Space space;
    private final CytokineType type;

    public CytokineAgent(Space space, CytokineType type) {
        this.space = space;
        this.type = type;
    }

    @Override
    @SneakyThrows
    public void run() {
        while (true) {
            Object[] microglia = space.get(new ActualField("MICROGLIA"), new FormalField(MicrogliaState.class), new FormalField(Integer.class));
            if (type == CytokineType.PRO_INFLAMMATORY && microglia[1] == MicrogliaState.RESTING) {
                space.put(microglia[0], microglia[1], (int) microglia[2] - 1);
                Object[] activeMicroglia = space.get(new ActualField("MICROGLIA"), new ActualField(MicrogliaState.ACTIVE), new FormalField(Integer.class));
                space.put(activeMicroglia[0], activeMicroglia[1], (int) activeMicroglia[2] + 1);
//                space.put("CHANGE", "MICROGLIAACTIVE", 0);
            } else if (type == CytokineType.NON_INFLAMMATORY && microglia[1] == MicrogliaState.ACTIVE) {
                space.put(microglia[0], microglia[1], (int) microglia[2] - 1);
                Object[] activeMicroglia = space.get(new ActualField("MICROGLIA"), new ActualField(MicrogliaState.RESTING), new FormalField(Integer.class));
                space.put(activeMicroglia[0], activeMicroglia[1], (int) activeMicroglia[2] + 1);
//                space.put("CHANGE", "MICROGLIARESTING", 0);
            } else {
                space.put(microglia[0], microglia[1], microglia[2]);
            }
        }
    }
}
