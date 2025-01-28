package it.unicam.gutbrain.brain;

import it.unicam.gutbrain.gut.ProteinType;
import lombok.SneakyThrows;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import static it.unicam.gutbrain.Config.env;

public class MicrogliaAgent implements Runnable {

    private final Space space;
    private MicrogliaState state;
    int threshold = env.get("permeability_threshold");

    public MicrogliaAgent(Space space, MicrogliaState state) {
        this.space = space;
        this.state = state;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            changeState();
            if (isGutAboveThreshold(threshold)) {
                processOligomers();
                Thread.sleep(1000);
                updateCytokines();
            }
        }
    }

    @SneakyThrows
    private void updateCytokines() {
        if(this.state == MicrogliaState.ACTIVE) {
            Object[] proCytokines = space.get(new ActualField("CYTOKINE"), new ActualField(CytokineType.PRO_INFLAMMATORY), new FormalField(Integer.class));
            space.put(proCytokines[0], proCytokines[1], (int) proCytokines[2] + 1);
            Object[] nonCytokines = space.get(new ActualField("CYTOKINE"), new ActualField(CytokineType.NON_INFLAMMATORY), new FormalField(Integer.class));
            space.put(nonCytokines[0], nonCytokines[1], Math.max(0, (int) nonCytokines[2] - 1));
        }
        /*
        if (this.state == MicrogliaState.ACTIVE) {
            Object[] nonCytokines = space.get(new ActualField("CYTOKINE"), new ActualField(CytokineType.NON_INFLAMMATORY), new FormalField(Integer.class));
            space.put(nonCytokines[0], nonCytokines[1], (int) nonCytokines[2] + 1);
            Object[] proCytokines = space.get(new ActualField("CYTOKINE"), new ActualField(CytokineType.PRO_INFLAMMATORY), new FormalField(Integer.class));
            space.put(proCytokines[0], proCytokines[1], Math.max(0, (int) proCytokines[2] - 1));
        } else {
            Object[] proCytokines = space.get(new ActualField("CYTOKINE"), new ActualField(CytokineType.PRO_INFLAMMATORY), new FormalField(Integer.class));
            space.put(proCytokines[0], proCytokines[1], (int) proCytokines[2] + 1);
            Object[] nonCytokines = space.get(new ActualField("CYTOKINE"), new ActualField(CytokineType.NON_INFLAMMATORY), new FormalField(Integer.class));
            space.put(nonCytokines[0], nonCytokines[1], Math.max(0, (int) nonCytokines[2] - 1));
        }*/
    }

    @SneakyThrows
    private boolean isGutAboveThreshold(int threshold) {
        Object[] gut = space.query(new ActualField("GUT"), new FormalField(Integer.class));
        return (int) gut[1] <= threshold; //prima era >=
    }

    @SneakyThrows
    private void processOligomers() {
        Object[] oligomers = space.get(new ActualField("OLIGOMER"), new FormalField(ProteinType.class), new FormalField(Integer.class));
        if ((int) oligomers[2] == 0) {
            space.put(oligomers[0], oligomers[1], oligomers[2]);
            return;
        }

        if (state == MicrogliaState.RESTING) {
            state = MicrogliaState.ACTIVE;
//            changeState();
            space.put(oligomers[0], oligomers[1], oligomers[2]);
        } else {
            space.put(oligomers[0], oligomers[1], Math.max(0,(int) oligomers[2] - 1));
        }
    }

    private void changeState() {
        if (state == MicrogliaState.RESTING)
            processStateChange("MICROGLIAACTIVE", MicrogliaState.ACTIVE, MicrogliaState.RESTING);
        else
            processStateChange("MICROGLIARESTING", MicrogliaState.RESTING, MicrogliaState.ACTIVE);
    }

    @SneakyThrows
    private void processStateChange(String changeType, MicrogliaState newState, MicrogliaState oldState) {
        Object[] change = space.getp(new ActualField("CHANGE"), new ActualField(changeType), new FormalField(Integer.class));
        if (change == null)
            return;

        if ((int) change[2] == 0) {
            space.put(change[0], change[1], change[2]);
            return;
        }

        space.put(change[0], change[1], Math.max((int) change[2] - 1,0));
        state = newState;

        Object[] newStateMicroglia = space.get(new ActualField("MICROGLIA"), new ActualField(newState), new FormalField(Integer.class));
        space.put(newStateMicroglia[0], newStateMicroglia[1], (int) newStateMicroglia[2] + 1);

        Object[] oldStateMicroglia = space.get(new ActualField("MICROGLIA"), new ActualField(oldState), new FormalField(Integer.class));
        space.put(oldStateMicroglia[0], oldStateMicroglia[1], Math.max(0, (int) oldStateMicroglia[2] - 1));
    }
}
