package io.samancore.condition_template.constant;

import io.samancore.common.model.condition.ConditionType;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.*;

public class InstanceConstants {
    public static final Set<String> DMN_INPUTS = new HashSet<>();
    public static final Map<ConditionType, ConditionGraph> CONDITION_GRAPHS = new EnumMap<>(ConditionType.class);

    static {
        CONDITION_GRAPHS.put(ConditionType.VALUE, new ConditionGraph(true, "_value", new HashMap<>(), new DefaultDirectedGraph<>(String.class)));
        CONDITION_GRAPHS.put(ConditionType.VISIBLE, new ConditionGraph(false, "_visible", new HashMap<>(), new DefaultDirectedGraph<>(String.class)));
        CONDITION_GRAPHS.put(ConditionType.DISABLE, new ConditionGraph(false, "_disable", new HashMap<>(), new DefaultDirectedGraph<>(String.class)));
        CONDITION_GRAPHS.put(ConditionType.ALERT, new ConditionGraph(false, "_alert", new HashMap<>(), new DefaultDirectedGraph<>(String.class)));
        CONDITION_GRAPHS.put(ConditionType.VALIDATE, new ConditionGraph(false, "_validate", new HashMap<>(), new DefaultDirectedGraph<>(String.class)));
        CONDITION_GRAPHS.put(ConditionType.OPTIONS, new ConditionGraph(false, "_options", new HashMap<>(), new DefaultDirectedGraph<>(String.class)));
    }

    private InstanceConstants() {
    }
}
