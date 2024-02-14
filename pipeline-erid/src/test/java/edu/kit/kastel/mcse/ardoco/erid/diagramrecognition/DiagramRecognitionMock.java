/* Licensed under MIT 2024. */
package edu.kit.kastel.mcse.ardoco.erid.diagramrecognition;

import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import edu.kit.kastel.mcse.ardoco.core.api.diagramrecognition.DiagramGS;
import edu.kit.kastel.mcse.ardoco.core.api.diagramrecognition.DiagramRecognitionState;
import edu.kit.kastel.mcse.ardoco.core.common.util.wordsim.UnicodeCharacterMatchFunctions;
import edu.kit.kastel.mcse.ardoco.core.common.util.wordsim.WordSimUtils;
import edu.kit.kastel.mcse.ardoco.core.data.DataRepository;
import edu.kit.kastel.mcse.ardoco.core.pipeline.ExecutionStage;
import edu.kit.kastel.mcse.ardoco.erid.diagramrecognition.agents.DiagramDisambiguationAgent;
import edu.kit.kastel.mcse.ardoco.erid.diagramrecognition.agents.DiagramReferenceAgent;
import edu.kit.kastel.mcse.ardoco.lissa.DiagramRecognitionStateImpl;
import edu.kit.kastel.mcse.ardoco.tests.eval.GoldStandardDiagrams;

/**
 * This stage is responsible for mocking the [edu.kit.kastel.mcse.ardoco.lissa.DiagramRecognition] stage. It populates the [DiagramRecognitionState] using the
 * [GoldStandardDiagrams] gold standard.
 */
public class DiagramRecognitionMock extends ExecutionStage {

    private GoldStandardDiagrams goldStandardProject;
    private WordSimUtils wordSimUtils;

    public DiagramRecognitionMock(GoldStandardDiagrams goldStandardProject, SortedMap<String, String> additionalConfigs, DataRepository dataRepository) {
        super(List.of(new DiagramDisambiguationAgent(dataRepository), new DiagramReferenceAgent(dataRepository)), DiagramRecognitionMock.class.getSimpleName(),
                dataRepository, additionalConfigs);
        this.goldStandardProject = goldStandardProject;
        this.wordSimUtils = getDataRepository().getGlobalConfiguration().getWordSimUtils();
    }

    @Override
    public void initializeState() {
        logger.info("Creating DiagramRecognitionMock State");
        var diagramRecognitionState = new DiagramRecognitionStateImpl();
        Set<DiagramGS> diagrams = goldStandardProject == null ? Set.of() : goldStandardProject.getDiagramsGoldStandard();

        for (var diagram : diagrams) {
            logger.debug("Loaded Diagram {}", diagram.getResourceName());
            diagramRecognitionState.addDiagram(diagram);
        }
        getDataRepository().addData(DiagramRecognitionState.ID, diagramRecognitionState);
    }

    private UnicodeCharacterMatchFunctions previousCharacterMatchFunction = null;

    @Override
    public void before() {
        super.before();
        previousCharacterMatchFunction = wordSimUtils.getCharacterMatchFunction();
        wordSimUtils.setCharacterMatchFunction(UnicodeCharacterMatchFunctions.EQUAL_OR_HOMOGLYPH);
    }

    @Override
    public void after() {
        wordSimUtils.setCharacterMatchFunction(previousCharacterMatchFunction);
        super.after();
    }
}
