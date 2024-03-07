/* Licensed under MIT 2023-2024. */
package edu.kit.kastel.mcse.ardoco.lissa.diagramconsistency.agents;

import java.util.List;
import java.util.Optional;

import edu.kit.kastel.mcse.ardoco.core.api.diagramconsistency.DiagramModelLinkState;
import edu.kit.kastel.mcse.ardoco.core.data.DataRepository;
import edu.kit.kastel.mcse.ardoco.core.pipeline.agent.PipelineAgent;
import edu.kit.kastel.mcse.ardoco.lissa.diagramconsistency.DiagramModelLinkStateImpl;
import edu.kit.kastel.mcse.ardoco.lissa.diagramconsistency.informants.DiagramModelLinkInformant;

/**
 * The diagram model matching stage.
 */
public class DiagramModelMatchingAgent extends PipelineAgent {
    /**
     * Creates a new DiagramModelMatchingAgent.
     *
     * @param data
     *             The DataRepository.
     */
    public DiagramModelMatchingAgent(DataRepository data) {
        super(List.of(new DiagramModelLinkInformant(data)), DiagramModelMatchingAgent.class.getSimpleName(), data);
    }

    /**
     * Creates a new DiagramModelMatchingAgent.
     *
     * @param data
     *             The DataRepository.
     * @return The DiagramModelMatchingAgent.
     */
    public static DiagramModelMatchingAgent get(DataRepository data) {
        return new DiagramModelMatchingAgent(data);
    }

    @Override
    protected void initializeState() {
        DataRepository data = this.getDataRepository();
        Optional<DiagramModelLinkState> optionalDiagramModelLinkState = data.getData(DiagramModelLinkState.ID, DiagramModelLinkState.class);
        DiagramModelLinkState state = optionalDiagramModelLinkState.orElseGet(DiagramModelLinkStateImpl::new);

        if (optionalDiagramModelLinkState.isEmpty()) {
            data.addData(DiagramModelLinkState.ID, state);
        }
    }
}
