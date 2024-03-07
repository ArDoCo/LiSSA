/* Licensed under MIT 2023-2024. */
package edu.kit.kastel.mcse.ardoco.lissa.execution;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import edu.kit.kastel.mcse.ardoco.core.api.InputDiagramData;
import edu.kit.kastel.mcse.ardoco.core.api.models.ArchitectureModelType;
import edu.kit.kastel.mcse.ardoco.core.common.tuple.Pair;
import edu.kit.kastel.mcse.ardoco.core.common.util.CommonUtilities;
import edu.kit.kastel.mcse.ardoco.core.common.util.DataRepositoryHelper;
import edu.kit.kastel.mcse.ardoco.core.execution.ArDoCo;
import edu.kit.kastel.mcse.ardoco.core.execution.runner.ParameterizedRunner;
import edu.kit.kastel.mcse.ardoco.core.pipeline.AbstractPipelineStep;
import edu.kit.kastel.mcse.ardoco.erid.diagramconnectiongenerator.DiagramConnectionGenerator;
import edu.kit.kastel.mcse.ardoco.erid.diagraminconsistency.DiagramInconsistencyChecker;
import edu.kit.kastel.mcse.ardoco.id.InconsistencyChecker;
import edu.kit.kastel.mcse.ardoco.lissa.DiagramRecognition;
import edu.kit.kastel.mcse.ardoco.tlr.connectiongenerator.ConnectionGenerator;
import edu.kit.kastel.mcse.ardoco.tlr.models.agents.ArCoTLModelProviderAgent;
import edu.kit.kastel.mcse.ardoco.tlr.recommendationgenerator.RecommendationGenerator;
import edu.kit.kastel.mcse.ardoco.tlr.text.providers.TextPreprocessingAgent;

public class ArDoCoForERID extends ParameterizedRunner<ArDoCoForERID.Parameters> {
    public record Parameters(List<Pair<String, File>> diagramNameXFile, File inputText, File inputModelArchitecture,
                             ArchitectureModelType inputArchitectureModelType, SortedMap<String, String> additionalConfigs, File outputDir) {
    }

    public ArDoCoForERID(String projectName, Parameters parameters) {
        super(projectName, parameters);
    }

    @Override
    public List<AbstractPipelineStep> initializePipelineSteps(Parameters p) {
        var pipelineSteps = new ArrayList<AbstractPipelineStep>();

        ArDoCo arDoCo = getArDoCo();
        var dataRepository = arDoCo.getDataRepository();
        dataRepository.getGlobalConfiguration().getWordSimUtils().setConsiderAbbreviations(true);

        var text = CommonUtilities.readInputText(p.inputText);
        if (text.isBlank()) {
            throw new IllegalArgumentException("Cannot deal with empty input text. Maybe there was an error reading the file.");
        }
        DataRepositoryHelper.putInputText(dataRepository, text);
        pipelineSteps.add(TextPreprocessingAgent.get(p.additionalConfigs, dataRepository));

        ArCoTLModelProviderAgent arCoTLModelProviderAgent = ArCoTLModelProviderAgent.get(p.inputModelArchitecture, p.inputArchitectureModelType, null,
                p.additionalConfigs, dataRepository);
        pipelineSteps.add(arCoTLModelProviderAgent);
        dataRepository.addData(InputDiagramData.ID, new InputDiagramData(p.diagramNameXFile()));
        pipelineSteps.add(DiagramRecognition.get(p.additionalConfigs, dataRepository));
        pipelineSteps.add(RecommendationGenerator.get(p.additionalConfigs, dataRepository));
        pipelineSteps.add(new DiagramConnectionGenerator(p.additionalConfigs, dataRepository));
        pipelineSteps.add(new DiagramInconsistencyChecker(p.additionalConfigs, dataRepository));
        pipelineSteps.add(ConnectionGenerator.get(p.additionalConfigs, dataRepository));
        pipelineSteps.add(InconsistencyChecker.get(p.additionalConfigs, dataRepository));

        return pipelineSteps;
    }
}
