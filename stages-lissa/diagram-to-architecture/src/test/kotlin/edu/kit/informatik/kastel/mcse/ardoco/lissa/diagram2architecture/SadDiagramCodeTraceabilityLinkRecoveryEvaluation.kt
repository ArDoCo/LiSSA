package edu.kit.informatik.kastel.mcse.ardoco.lissa.diagram2architecture

import edu.kit.informatik.kastel.mcse.ardoco.lissa.diagram2architecture.execution.ArDoCoForSadDiagramCodeTraceabilityLinkRecovery
import edu.kit.kastel.mcse.ardoco.core.api.models.CodeModelType
import edu.kit.kastel.mcse.ardoco.core.api.output.ArDoCoResult
import edu.kit.kastel.mcse.ardoco.core.common.util.DataRepositoryHelper
import edu.kit.kastel.mcse.ardoco.core.common.util.TraceLinkUtilities
import edu.kit.kastel.mcse.ardoco.core.execution.runner.ArDoCoRunner
import edu.kit.kastel.mcse.ardoco.core.tests.eval.CodeProject
import edu.kit.kastel.mcse.ardoco.core.tests.eval.ProjectHelper
import edu.kit.kastel.mcse.ardoco.core.tests.eval.results.ExpectedResults
import edu.kit.kastel.mcse.ardoco.tlr.tests.integration.TraceabilityLinkRecoveryEvaluation
import org.eclipse.collections.api.factory.Lists
import org.eclipse.collections.api.list.ImmutableList
import java.io.File
import java.util.Locale
import java.util.SortedMap
import java.util.TreeMap

class SadDiagramCodeTraceabilityLinkRecoveryEvaluation : TraceabilityLinkRecoveryEvaluation<CodeProject>() {
    override fun resultHasRequiredData(arDoCoResult: ArDoCoResult): Boolean {
        val traceLinks = arDoCoResult.sadCodeTraceLinks
        return !traceLinks.isEmpty()
    }

    public override fun runTraceLinkEvaluation(project: CodeProject): ArDoCoResult {
        return super.runTraceLinkEvaluation(project)
    }

    override fun getAndSetupRunner(codeProject: CodeProject): ArDoCoRunner {
        val name = codeProject.name.lowercase(Locale.getDefault())
        val textInput = codeProject.textFile
        val inputArchitectureModel = ProjectHelper.loadFileFromResources("/extracted_models/$name.json")
        val inputCode: File = getInputCode(codeProject)
        val additionalConfigsMap: SortedMap<String, String> = TreeMap()
        val outputDir: File = File("target/SDCTLREvaluation/$name")
        outputDir.mkdirs()

        val runner = ArDoCoForSadDiagramCodeTraceabilityLinkRecovery(name)
        runner.setUp(textInput, inputArchitectureModel, inputCode, additionalConfigsMap, outputDir)
        return runner
    }

    override fun createTraceLinkStringList(arDoCoResult: ArDoCoResult): ImmutableList<String> {
        val traceLinks = arDoCoResult.sadCodeTraceLinks

        return TraceLinkUtilities.getSadCodeTraceLinksAsStringList(Lists.immutable.ofAll(traceLinks))
    }

    override fun getGoldStandard(codeProject: CodeProject): ImmutableList<String> {
        return codeProject.sadCodeGoldStandard
    }

    override fun enrollGoldStandard(
        goldStandard: ImmutableList<String?>?,
        result: ArDoCoResult?
    ): ImmutableList<String> {
        return TraceabilityLinkRecoveryEvaluation.enrollGoldStandardForCode(goldStandard, result)
    }

    override fun getExpectedResults(codeProject: CodeProject): ExpectedResults {
        return codeProject.expectedResultsForSadSamCode
    }

    override fun getConfusionMatrixSum(arDoCoResult: ArDoCoResult): Int {
        val dataRepository = arDoCoResult.dataRepository()

        val text = DataRepositoryHelper.getAnnotatedText(dataRepository)
        val sentences = text.sentences.size()

        val modelStatesData = DataRepositoryHelper.getModelStatesData(dataRepository)
        val codeModel = modelStatesData.getModel(CodeModelType.CODE_MODEL.modelId)
        val codeModelEndpoints = codeModel.endpoints.size

        return sentences * codeModelEndpoints
    }
}
