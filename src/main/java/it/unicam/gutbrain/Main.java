package it.unicam.gutbrain;

import it.unicam.gutbrain.gut.BacteriaStatus;
import it.unicam.gutbrain.gut.Master;
import it.unicam.gutbrain.gut.ProteinStatus;
import it.unicam.gutbrain.gut.ProteinType;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import static it.unicam.gutbrain.Config.env;

public class Main {

    public static void main(String[] argv) throws InterruptedException {
        Space space = new SequentialSpace();
        //space.put("BRAIN")

        Master master = new Master(space);

        space.put("CREATE", "PROTEINGENERATOR");
        space.put("CREATE", "DIETGENERATOR");
        space.put("CREATE", "GUTPERMEABILITYGENERATOR");

        for (int i = 0; i < env.get("aep_enzyme"); i++) {
            space.put("CREATE", "AEP");
        }
        space.put("GUT", 0); // impermeability
        space.put("PROTEIN", ProteinType.ALPHA, ProteinStatus.NORMAL, env.get("alpha_proteins"));
        space.put("PROTEIN", ProteinType.TAU, ProteinStatus.NORMAL, env.get("tau_proteins"));
        space.put("OLIGOMER", ProteinType.ALPHA, env.get("alpha_oligomers"));
        space.put("OLIGOMER", ProteinType.TAU, env.get("tau_oligomers"));
        space.put("BACTERIA", BacteriaStatus.GOOD, env.get("good_bacteria"));
        space.put("BACTERIA", BacteriaStatus.PATHOGENIC, env.get("pathogenic_bacteria"));
        space.put("DIET", env.get("sugar"), env.get("milk"), env.get("salt"));

        master.run();


    }
}
