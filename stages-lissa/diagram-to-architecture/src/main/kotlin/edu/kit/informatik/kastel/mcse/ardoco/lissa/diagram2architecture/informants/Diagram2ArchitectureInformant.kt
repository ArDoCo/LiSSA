package edu.kit.informatik.kastel.mcse.ardoco.lissa.diagram2architecture.informants

import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import edu.kit.kastel.mcse.ardoco.core.api.models.ArchitectureModelType
import edu.kit.kastel.mcse.ardoco.core.api.models.ModelStates
import edu.kit.kastel.mcse.ardoco.core.api.models.arcotl.ArchitectureModel
import edu.kit.kastel.mcse.ardoco.core.api.models.arcotl.architecture.ArchitectureComponent
import edu.kit.kastel.mcse.ardoco.core.api.models.arcotl.architecture.ArchitectureInterface
import edu.kit.kastel.mcse.ardoco.core.common.JsonHandling
import edu.kit.kastel.mcse.ardoco.core.data.DataRepository
import edu.kit.kastel.mcse.ardoco.core.pipeline.agent.Informant
import java.io.File

class Diagram2ArchitectureInformant(private val architectureFile: File, dataRepository: DataRepository) : Informant(ID, dataRepository) {
    companion object {
        const val ID = "Diagram2ArchitectureInformant"
    }

    override fun process() {
        if (!architectureFile.exists()) {
            throw IllegalArgumentException("Architecture file does not exist")
        }
        val architecture: List<String> = JsonHandling.createObjectMapper().registerKotlinModule().readValue(architectureFile)
        val architectureModels =
            ArchitectureModel(
                architecture.flatMap {
                    listOf(
                        ArchitectureComponent(it, "$it-Component", sortedSetOf(), sortedSetOf(), sortedSetOf(), "BasicComponent"),
                        ArchitectureInterface(it, "$it-Interface", sortedSetOf())
                    )
                }
            )
        val modelStatesOptional = getDataRepository().getData(ModelStates.ID, ModelStates::class.java)
        if (modelStatesOptional.isEmpty) {
            getDataRepository().addData(ModelStates.ID, ModelStates())
        }

        val modelStates = getDataRepository().getData(ModelStates.ID, ModelStates::class.java).orElseThrow()
        modelStates.addModel(ArchitectureModelType.PCM.modelId, architectureModels)
    }
}
