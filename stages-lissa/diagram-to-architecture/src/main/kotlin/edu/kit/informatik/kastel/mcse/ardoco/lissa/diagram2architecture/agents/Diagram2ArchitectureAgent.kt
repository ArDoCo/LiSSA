package edu.kit.informatik.kastel.mcse.ardoco.lissa.diagram2architecture.agents

import edu.kit.informatik.kastel.mcse.ardoco.lissa.diagram2architecture.informants.Diagram2ArchitectureInformant
import edu.kit.kastel.mcse.ardoco.core.data.DataRepository
import edu.kit.kastel.mcse.ardoco.core.pipeline.agent.PipelineAgent
import java.io.File

class Diagram2ArchitectureAgent(architectureFile: File, dataRepository: DataRepository) : PipelineAgent(
    listOf(Diagram2ArchitectureInformant(architectureFile, dataRepository)),
    ID,
    dataRepository
) {
    companion object {
        const val ID = "Diagram2ArchitectureAgent"
    }
}
