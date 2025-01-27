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
            MicrogliaState microgliaToExtract = type == CytokineType.PRO_INFLAMMATORY ? MicrogliaState.RESTING : MicrogliaState.ACTIVE;
            Object[] microglia = getMicroglia(microgliaToExtract);

            updateMicrogliaCount(microglia, -1);
            MicrogliaState newState = (type == CytokineType.PRO_INFLAMMATORY) ? MicrogliaState.ACTIVE : MicrogliaState.RESTING;
            updateMicrogliaCount(getMicroglia(newState), 1);
            createChangeStateTuple(newState == MicrogliaState.ACTIVE ? "MICROGLIAACTIVE" : "MICROGLIARESTING");
        }
    }

    @SneakyThrows
    private void updateMicrogliaCount(Object[] microglia, int delta) {
        space.put(microglia[0], microglia[1], (int) microglia[2] + delta);
    }

    @SneakyThrows
    private Object[] getMicroglia(MicrogliaState state) {
        return space.get(new ActualField("MICROGLIA"), new ActualField(state), new FormalField(Integer.class));
    }

    @SneakyThrows
    private void createChangeStateTuple(String toCreate) {
        Object[] changes = space.getp(new ActualField("CHANGE"), new ActualField(toCreate), new FormalField(Integer.class));
        int newCount = (changes == null) ? 1 : (int) changes[2] + 1;
        space.put("CHANGE", toCreate, newCount);
    }
}
