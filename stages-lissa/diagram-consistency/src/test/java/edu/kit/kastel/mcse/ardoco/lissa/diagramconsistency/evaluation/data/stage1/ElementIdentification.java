/* Licensed under MIT 2023-2024. */
package edu.kit.kastel.mcse.ardoco.lissa.diagramconsistency.evaluation.data.stage1;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * All diagram elements and their identified occurrences in the models.
 *
 * @param name
 *                 The name of the diagram.
 * @param elements
 *                 The elements.
 */
public record ElementIdentification(@JsonProperty("name") String name, @JsonProperty("elements") Element[] elements) {
}
