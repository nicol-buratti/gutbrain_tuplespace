package it.unicam.gutbrain;

import it.unicam.gutbrain.brain.CytokineType;
import it.unicam.gutbrain.brain.MicrogliaState;
import it.unicam.gutbrain.brain.NeuronState;
import it.unicam.gutbrain.gut.AEPState;
import it.unicam.gutbrain.gut.BacteriaStatus;
import it.unicam.gutbrain.gut.ProteinStatus;
import it.unicam.gutbrain.gut.ProteinType;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import static it.unicam.gutbrain.Config.env;

public class Main {

    public static void main(String[] argv) throws InterruptedException {
        Space space = new SequentialSpace();
        Master master = new Master(space);

        // Agents
        space.put("CREATE", "PROTEINGENERATOR", 1);
        space.put("CREATE", "DIETGENERATOR", 1);
        space.put("CREATE", "GUTPERMEABILITYGENERATOR", 1);
        space.put("CREATE", "SPACESTATECATCHER", 1);

        // Gut Agents
        space.put("CREATE", "AEP", env.get("aep_enzyme"));

        // Gut environment
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

        // Brain agents
        space.put("CREATE", "RESTING_MICROGLIA", env.get("resting_microglia"));
        space.put("CREATE", "ACTIVE_MICROGLIA", env.get("active_microglia"));
        space.put("CREATE", "NEURON_HEALTHY", env.get("neuron_healthy"));
        space.put("CREATE", "NEURON_HEALTHY", env.get("neuron_healthy"));
        space.put("CREATE", "NEURON_DAMAGED", env.get("neuron_damaged"));
        space.put("CREATE", "PRO_CYTOKINES", env.get("pro_inflammatory_cytokine"));
        space.put("CREATE", "NON_CYTOKINES", env.get("non_inflammatory_cytokine"));


        // Brain environment
        space.put("MICROGLIA", MicrogliaState.RESTING, env.get("resting_microglia"));
        space.put("MICROGLIA", MicrogliaState.ACTIVE, env.get("active_microglia"));
        space.put("CYTOKINE", CytokineType.NON_INFLAMMATORY, env.get("non_inflammatory_cytokine"));
        space.put("CYTOKINE", CytokineType.PRO_INFLAMMATORY, env.get("pro_inflammatory_cytokine"));
        space.put("NEURON", NeuronState.HEALTHY, env.get("neuron_healthy"));
        space.put("NEURON", NeuronState.DAMAGED, env.get("neuron_damaged"));
        space.put("NEURON", NeuronState.DEAD, env.get("neuron_dead"));


        master.run();
    }
}
