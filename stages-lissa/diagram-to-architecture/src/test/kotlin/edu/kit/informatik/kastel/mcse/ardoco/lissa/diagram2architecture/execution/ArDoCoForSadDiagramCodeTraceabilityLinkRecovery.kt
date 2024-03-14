package edu.kit.informatik.kastel.mcse.ardoco.lissa.diagram2architecture.execution

import edu.kit.informatik.kastel.mcse.ardoco.lissa.diagram2architecture.agents.Diagram2ArchitectureAgent
import edu.kit.kastel.mcse.ardoco.core.common.util.CommonUtilities
import edu.kit.kastel.mcse.ardoco.core.common.util.DataRepositoryHelper
import edu.kit.kastel.mcse.ardoco.core.data.DataRepository
import edu.kit.kastel.mcse.ardoco.core.execution.ArDoCo
import edu.kit.kastel.mcse.ardoco.core.execution.runner.ArDoCoRunner
import edu.kit.kastel.mcse.ardoco.tlr.codetraceability.SadSamCodeTraceabilityLinkRecovery
import edu.kit.kastel.mcse.ardoco.tlr.codetraceability.SamCodeTraceabilityLinkRecovery
import edu.kit.kastel.mcse.ardoco.tlr.connectiongenerator.ConnectionGenerator
import edu.kit.kastel.mcse.ardoco.tlr.models.agents.ArCoTLModelProviderAgent
import edu.kit.kastel.mcse.ardoco.tlr.recommendationgenerator.RecommendationGenerator
import edu.kit.kastel.mcse.ardoco.tlr.text.providers.TextPreprocessingAgent
import edu.kit.kastel.mcse.ardoco.tlr.textextraction.TextExtraction
import java.io.File
import java.util.SortedMap

class ArDoCoForSadDiagramCodeTraceabilityLinkRecovery(projectName: String) : ArDoCoRunner(projectName) {
    fun setUp(
        inputText: File,
        inputArchitectureModel: File,
        inputCode: File,
        additionalConfigs: SortedMap<String, String>,
        outputDir: File?
    ) {
        definePipeline(inputText, inputArchitectureModel, inputCode, additionalConfigs)
        setOutputDirectory(outputDir)
        isSetUp = true
    }

    private fun definePipeline(
        inputText: File,
        inputArchitectureModel: File,
        inputCode: File,
        additionalConfigs: SortedMap<String, String>
    ) {
        val arDoCo: ArDoCo = this.getArDoCo()
        val dataRepository: DataRepository = arDoCo.getDataRepository()

        val text = CommonUtilities.readInputText(inputText)
        require(!text.isBlank()) { "Cannot deal with empty input text. Maybe there was an error reading the file." }
        DataRepositoryHelper.putInputText(dataRepository, text)

        arDoCo.addPipelineStep(TextPreprocessingAgent.get(additionalConfigs, dataRepository))

        val arCoTLModelProviderAgent: ArCoTLModelProviderAgent =
            ArCoTLModelProviderAgent.get(
                null,
                null,
                inputCode,
                additionalConfigs,
                dataRepository
            )
        arDoCo.addPipelineStep(arCoTLModelProviderAgent)

        val diagram2Architecture = Diagram2ArchitectureAgent(inputArchitectureModel, dataRepository)
        arDoCo.addPipelineStep(diagram2Architecture)

        arDoCo.addPipelineStep(TextExtraction.get(additionalConfigs, dataRepository))
        arDoCo.addPipelineStep(RecommendationGenerator.get(additionalConfigs, dataRepository))
        arDoCo.addPipelineStep(ConnectionGenerator.get(additionalConfigs, dataRepository))

        arDoCo.addPipelineStep(SamCodeTraceabilityLinkRecovery.get(additionalConfigs, dataRepository))

        arDoCo.addPipelineStep(SadSamCodeTraceabilityLinkRecovery.get(additionalConfigs, dataRepository))
    }
}
