package it.unicam.gutbrain;

import org.jspace.ActualField;
import org.jspace.Space;

enum AEPState {
    ACTIVE,
    HYPERACTIVE
}

public class AEPAgent implements Runnable {

    private final Space space;
    private final AEPState state;

    public AEPAgent(Space space) {
        this.space = space;
        this.state = AEPState.ACTIVE;
    }

    @Override
    public void run() {
        try {
            while (true) {
                space.get(new ActualField("PROTEIN"));
                System.out.println("AEP Proteina presa");
                space.put("CREATE", "CLEAVED_PROTEIN");
                space.put("CREATE", "CLEAVED_PROTEIN");
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
