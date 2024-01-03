package io.samancore;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.stream.Stream;

@ApplicationScoped
public class GraphUtil {

    public List<String> getDmnNameDependencies(ConditionType type) {
        if (type.isUpdateCascade()) {
            return getDmnNameDependenciesOrdered(type);
        } else {
            return getAllDmnNameDependencies(type);
        }
    }

    public List<String> getDmnNameDependencies(ConditionType type, Set<String> initialNodes) {
        if (type.isUpdateCascade()) {
            return getDmnNameDependenciesOrdered(type, initialNodes);
        } else {
            return getAllDmnNameDependencies(type, initialNodes);
        }
    }

    protected List<String> getDmnNameDependenciesOrdered(ConditionType type) {
        var graph = getGraph(type);
        var evalOrder = new ArrayList<String>();
        var topologicalOrder = new TopologicalOrderIterator<>(graph);
        topologicalOrder.forEachRemaining(v -> {
            var incomingSet = graph.incomingEdgesOf(v);
            if (!incomingSet.isEmpty()) {
                evalOrder.add(v);
            }
        });
        return evalOrder;
    }

    protected List<String> getAllDmnNameDependencies(ConditionType type) {
        return new ArrayList<>(type.getModels().keySet());
    }

    protected List<String> getDmnNameDependenciesOrdered(ConditionType type, Set<String> initialNodes) {
        var graph = getGraph(type);

        var affectedNodes = new ArrayList<String>();
        DepthFirstIterator<String, String> depthFirstIterator;
        try {
            depthFirstIterator = new DepthFirstIterator<>(graph, initialNodes);
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
        depthFirstIterator.forEachRemaining(affectedNodes::add);

        var evalOrder = new ArrayList<String>();
        affectedNodes.forEach(v -> {
            var incomingSet = graph.incomingEdgesOf(v).stream()
                    .map(graph::getEdgeSource)
                    .toList();

            if (incomingSet.stream().anyMatch(affectedNodes::contains)) {
                evalOrder.add(v);
            }
        });
        evalOrder.sort(GraphUtil.Order.newComparator(getDmnNameDependencies(type)));

        return evalOrder;
    }

    protected List<String> getAllDmnNameDependencies(ConditionType type, Set<String> initialNodes) {
        var graph = getGraph(type);
        return initialNodes.stream()
                .flatMap(node -> {
                    if (graph.containsVertex(node)) {
                        return graph.outgoingEdgesOf(node).stream();
                    } else {
                        return Stream.empty();
                    }
                })
                .map(graph::getEdgeTarget)
                .distinct()
                .toList();
    }

    protected DefaultDirectedGraph<String, String> getGraph(ConditionType type) {
        return type.getGraph();
    }

    protected static class Order<E> implements Comparator<E> {
        private final List<E> toCompare;

        Order(List<E> toCompare) {
            this.toCompare = toCompare;
        }

        public static <E> GraphUtil.Order<E> newComparator(List<E> toCompare) {
            return new GraphUtil.Order<>(toCompare);
        }

        public int compare(E o1, E o2) {
            int index1 = toCompare.indexOf(o1);
            int index2 = toCompare.indexOf(o2);

            return index1 - index2;
        }
    }
}
