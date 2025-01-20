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

    public MicrogliaAgent(Space space, MicrogliaState state) {
        this.space = space;
        this.state = state;
    }

    @Override
    @SneakyThrows
    public void run() {
        int threshold = env.get("permeability_threshold");
        while (true) {
            Object[] gut = space.query(new ActualField("GUT"), new FormalField(Integer.class));
            if ((int) gut[1] < threshold)
                continue;

            Object[] oligomers = space.get(new ActualField("OLIGOMER"), new FormalField(ProteinType.class), new FormalField(Integer.class));
            if ((int) oligomers[2] == 0) {
                space.put(oligomers[0], oligomers[1], oligomers[2]);
                continue;
            }
            if (state == MicrogliaState.RESTING) {
                state = MicrogliaState.ACTIVE;
                space.put(oligomers[0], oligomers[1], oligomers[2]);
            } else
                space.put(oligomers[0], oligomers[1], (int) oligomers[2] - 1);
        }
    }
}
