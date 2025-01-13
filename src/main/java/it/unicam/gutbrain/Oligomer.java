package it.unicam.gutbrain;

import org.jspace.ActualField;
import org.jspace.Space;


public class Oligomer implements Runnable {

    private final Space space;
    private final ProteinType oligomerType;

    public Oligomer(Space space, ProteinType oligomerType) {
        this.space = space;
        this.oligomerType = oligomerType;
    }

    @Override
    public void run() {
        try {
            // TODO aggiungi syn e alpha
            while (true) {
                space.get(new ActualField("CLEAVED_PROTEIN"));
                space.get(new ActualField("CLEAVED_PROTEIN"));
                System.out.println("Proteina cleaved");
                space.put("CREATE", "OLIGOMER");
                System.out.println("Creato Oligomer");
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
