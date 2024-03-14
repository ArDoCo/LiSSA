package edu.kit.informatik.kastel.mcse.ardoco.lissa.diagram2architecture

import edu.kit.kastel.mcse.ardoco.core.api.output.ArDoCoResult
import edu.kit.kastel.mcse.ardoco.core.common.RepositoryHandler
import edu.kit.kastel.mcse.ardoco.core.tests.eval.CodeProject
import edu.kit.kastel.mcse.ardoco.core.tests.eval.GoldStandardProject
import edu.kit.kastel.mcse.ardoco.core.tests.eval.ProjectHelper
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.concurrent.atomic.AtomicBoolean

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Disabled
class TraceLinkDiagramEvaluationIT<T : GoldStandardProject> {
    @DisplayName("Evaluate SAD-Diagram-Code TLR")
    @ParameterizedTest(name = "{0}")
    @MethodSource("getNonHistoricalCodeProjects")
    fun evaluateSadDiagramCodeTlrIT(codeProject: CodeProject) {
        analyzeCodeDirectly.set(false)
        if (analyzeCodeDirectly.get()) cleanUpCodeRepository(codeProject)

        val evaluation = SadDiagramCodeTraceabilityLinkRecoveryEvaluation()
        val results: ArDoCoResult = evaluation.runTraceLinkEvaluation(codeProject)
        Assertions.assertNotNull(results)
    }

    companion object {
        protected const val LOGGING_ARDOCO_CORE: String = "org.slf4j.simpleLogger.log.edu.kit.kastel.mcse.ardoco.core"
        protected var analyzeCodeDirectly: AtomicBoolean = ProjectHelper.ANALYZE_CODE_DIRECTLY

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            System.setProperty(LOGGING_ARDOCO_CORE, "info")
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            System.setProperty(LOGGING_ARDOCO_CORE, "error")
        }

        private fun cleanUpCodeRepository(codeProject: CodeProject) {
            RepositoryHandler.removeRepository(codeProject.codeLocation)
        }

        @JvmStatic
        private fun getNonHistoricalCodeProjects(): List<CodeProject> {
            return listOf(CodeProject.TEAMMATES)
        }
    }
}
