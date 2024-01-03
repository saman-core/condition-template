package io.samancore;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.kie.kogito.decision.DecisionModel;

import java.util.HashMap;
import java.util.Map;

public enum ConditionType {
    VALUE(0, true, "_value", new HashMap<>(), new DefaultDirectedGraph<>(String.class)),
    VISIBLE(1, false, "_visible", new HashMap<>(), new DefaultDirectedGraph<>(String.class)),
    DISABLE(2, false, "_disable", new HashMap<>(), new DefaultDirectedGraph<>(String.class)),
    ALERT(3, false, "_alert", new HashMap<>(), new DefaultDirectedGraph<>(String.class));

    private final int value;
    private final boolean isUpdateCascade;
    private final String suffix;
    private final Map<String, DecisionModel> models;
    private final DefaultDirectedGraph<String, String> graph;

    ConditionType(int value, boolean isUpdateCascade, String suffix, Map<String, DecisionModel> models, DefaultDirectedGraph<String, String> graph) {
        this.value = value;
        this.isUpdateCascade = isUpdateCascade;
        this.suffix = suffix;
        this.models = models;
        this.graph = graph;
    }

    public int getValue() {
        return value;
    }

    public String getSuffix() {
        return suffix;
    }

    public Map<String, DecisionModel> getModels() {
        return models;
    }

    public DefaultDirectedGraph<String, String> getGraph() {
        return graph;
    }

    public boolean isUpdateCascade() {
        return isUpdateCascade;
    }
}
