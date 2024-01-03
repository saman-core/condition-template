package io.samancore;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class GraphUtilTest {
    DefaultDirectedGraph<String, String> graph = new DefaultDirectedGraph<>(String.class);

    @InjectSpy
    GraphUtil graphUtil;

    @BeforeEach
    void setUp() {
        graph.addVertex("a");
        graph.addVertex("b");
        graph.addVertex("c");
        graph.addVertex("d");
        graph.addVertex("e");
        graph.addVertex("f");
        graph.addVertex("g");
        graph.addVertex("h");
        graph.addVertex("i");
        graph.addVertex("j");
        graph.addVertex("k");
        graph.addVertex("l");


        graph.addEdge("a", "b", "1");
        graph.addEdge("b", "d", "2");
        graph.addEdge("d", "c", "3");
        graph.addEdge("e", "d", "4");
        graph.addEdge("e", "f", "5");
        graph.addEdge("f", "g", "6");
        graph.addEdge("h", "e", "7");
        graph.addEdge("i", "h", "8");
        graph.addEdge("j", "k", "9");
        graph.addEdge("f", "d", "10");
        graph.addEdge("g", "b", "11");
        graph.addEdge("l", "k", "12");
    }

    void all() {
        var graph = new DefaultDirectedGraph<String, String>(String.class);
        graph.addVertex("a");
        graph.addVertex("b");
        graph.addVertex("c");
        graph.addVertex("d");
        graph.addVertex("e");
        graph.addVertex("f");
        graph.addVertex("g");
        graph.addVertex("h");
        graph.addVertex("i");
        graph.addVertex("j");
        graph.addVertex("k");
        graph.addVertex("l");


        graph.addEdge("a", "b", "1");
        graph.addEdge("b", "d", "2");
        graph.addEdge("d", "c", "3");
        graph.addEdge("e", "d", "4");
        graph.addEdge("e", "f", "5");
        graph.addEdge("f", "g", "6");
        graph.addEdge("h", "e", "7");
        graph.addEdge("i", "h", "8");
        graph.addEdge("j", "k", "9");
        graph.addEdge("f", "d", "10");
        graph.addEdge("g", "b", "11");
        graph.addEdge("l", "k", "12");

        var origen = "e";
        var events = graph.outgoingEdgesOf(origen);
        System.out.println("---------------------");
        System.out.println(events);
        System.out.println("---------------------");
        for (var event : events) {
            System.out.println(graph.getEdgeTarget(event));
        }


        var dependencies = new ArrayList<String>();
        var topologicalOrder = new TopologicalOrderIterator<>(graph);
        topologicalOrder.forEachRemaining(dependencies::add);
        /*
        topologicalOrder.forEachRemaining(v -> {
            var incomingSet = graph.incomingEdgesOf(v);
            if (!incomingSet.isEmpty()) {
                dependencies.add(v);
            }
        });

         */

        System.out.println(dependencies);
        System.out.println("---------------------");


        var initialNodes = Arrays.asList("f", "g");
        var result = new ArrayList<String>();
        var depthFirstIterator = new DepthFirstIterator<>(graph, initialNodes);
        depthFirstIterator.forEachRemaining(result::add);

        var result2 = new ArrayList<String>();
        result.forEach(v -> {
            var incomingSet = graph.incomingEdgesOf(v).stream()
                    .map(graph::getEdgeSource)
                    .toList();

            if (incomingSet.stream().anyMatch(result::contains)) {
                result2.add(v);
            }
        });


        System.out.println(result2);
        System.out.println("---------------------");


        result2.sort(GraphUtil.Order.newComparator(dependencies));

        System.out.println(result2);
        System.out.println("---------------------");

        assertTrue(true);
    }

    @Test
    void getDmnNameDependencies() {
    }

    @Test
    void testGetDmnNameDependencies() {
    }

    @Test
    void getDmnNameDependenciesOrdered() {
        Mockito.when(graphUtil.getGraph(ConditionType.VALUE)).thenReturn(graph);

        var dependencies = graphUtil.getDmnNameDependenciesOrdered(ConditionType.VALUE);
        assertTrue(dependencies.size() > 0);
    }

    @Test
    void getDmnNameDependenciesOrderedOverloading() {
        Mockito.when(graphUtil.getGraph(ConditionType.VALUE)).thenReturn(graph);

        var initialNodes = Set.of("a", "l");
        var dependencies = graphUtil.getDmnNameDependenciesOrdered(ConditionType.VALUE, initialNodes);
        assertTrue(dependencies.size() > 0);
    }

    @Test
    void getAllDmnNameDependencies() {
    }

    @Test
    void getAllDmnNameDependenciesOverloading() {
        Mockito.when(graphUtil.getGraph(ConditionType.ALERT)).thenReturn(graph);

        var initialNodes = Set.of("b", "e");
        var dependencies = graphUtil.getAllDmnNameDependencies(ConditionType.ALERT, initialNodes);
        assertTrue(dependencies.size() > 0);
    }

    @Test
    void getGraph() {
    }
}