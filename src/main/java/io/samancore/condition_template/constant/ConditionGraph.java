package io.samancore.condition_template.constant;

import lombok.Getter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.kie.kogito.decision.DecisionModel;

import java.util.Map;

@Getter
public class ConditionGraph {
    private final boolean isUpdateCascade;
    private final String suffix;
    private final Map<String, DecisionModel> models;
    private final DefaultDirectedGraph<String, String> graph;

    public ConditionGraph(boolean isUpdateCascade, String suffix, Map<String, DecisionModel> models, DefaultDirectedGraph<String, String> graph) {
        this.isUpdateCascade = isUpdateCascade;
        this.suffix = suffix;
        this.models = models;
        this.graph = graph;
    }
}
