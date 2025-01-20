package it.unicam.gutbrain;

import it.unicam.gutbrain.gut.*;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import static it.unicam.gutbrain.Config.env;

public class Main {

    public static void main(String[] argv) throws InterruptedException {
        Space space = new SequentialSpace();
        Master master = new Master(space);

        space.put("CREATE", "PROTEINGENERATOR", 1);
        space.put("CREATE", "DIETGENERATOR", 1);
        space.put("CREATE", "GUTPERMEABILITYGENERATOR", 1);
        space.put("CREATE", "SPACESTATECATCHER", 1);
        space.put("CREATE", "AEP", env.get("aep_enzyme"));

        space.put("GUT", 0); // impermeability
        space.put("AEP", AEPState.ACTIVE, env.get("aep_enzyme"));
        space.put("AEP", AEPState.HYPERACTIVE, 0);
        space.put("PROTEIN", ProteinType.ALPHA, ProteinStatus.NORMAL, env.get("alpha_proteins"));
        space.put("PROTEIN", ProteinType.TAU, ProteinStatus.NORMAL, env.get("tau_proteins"));
        space.put("PROTEIN", ProteinType.ALPHA, ProteinStatus.CLEAVED, 0);
        space.put("PROTEIN", ProteinType.TAU, ProteinStatus.CLEAVED, 0);
        space.put("OLIGOMER", ProteinType.ALPHA, env.get("alpha_oligomers"));
        space.put("OLIGOMER", ProteinType.TAU, env.get("tau_oligomers"));
        space.put("BACTERIA", BacteriaStatus.GOOD, env.get("good_bacteria"));
        space.put("BACTERIA", BacteriaStatus.PATHOGENIC, env.get("pathogenic_bacteria"));
        space.put("DIET", env.get("sugar"), env.get("milk"), env.get("salt"));

        master.run();
    }
}
