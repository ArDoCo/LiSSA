/* Licensed under MIT 2023-2024. */
package edu.kit.kastel.mcse.ardoco.lissa.diagramconsistency.agents;

import java.io.File;
import java.util.List;

import edu.kit.kastel.mcse.ardoco.core.data.DataRepository;
import edu.kit.kastel.mcse.ardoco.core.pipeline.agent.PipelineAgent;
import edu.kit.kastel.mcse.ardoco.lissa.diagramconsistency.informants.DiagramProviderInformant;

/**
 * Agent that provides a diagram.
 */
public class DiagramProviderAgent extends PipelineAgent {
    /**
     * Creates a new DiagramProviderAgent.
     *
     * @param data
     *                    The DataRepository.
     * @param diagramFile
     *                    The diagram file.
     */
    public DiagramProviderAgent(DataRepository data, File diagramFile) {
        super(List.of(new DiagramProviderInformant(data, diagramFile)), DiagramProviderAgent.class.getSimpleName(), data);
    }

    /**
     * Creates a new DiagramProviderAgent that will load the diagram from the given file.
     *
     * @param diagramFile
     *                       The diagram file.
     * @param dataRepository
     *                       The DataRepository.
     * @return The DiagramProviderAgent.
     */
    public static DiagramProviderAgent get(File diagramFile, DataRepository dataRepository) {
        return new DiagramProviderAgent(dataRepository, diagramFile);
    }
}
