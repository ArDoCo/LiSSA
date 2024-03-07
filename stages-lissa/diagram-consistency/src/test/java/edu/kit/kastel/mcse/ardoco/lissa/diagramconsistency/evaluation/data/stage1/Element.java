/* Licensed under MIT 2023-2024. */
package edu.kit.kastel.mcse.ardoco.lissa.diagramconsistency.evaluation.data.stage1;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an element in a diagram.
 *
 * @param boxId
 *                    The id of the box.
 * @param occurrences
 *                    The occurrences of the element.
 */
public record Element(@JsonProperty("diagram_id") int boxId, @JsonProperty("occurrences") Occurrence[] occurrences) {
}
