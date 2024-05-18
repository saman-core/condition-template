package io.samancore.condition_template;

import io.quarkus.runtime.Startup;
import io.samancore.condition_template.constant.InstanceConstants;
import io.samancore.common.model.condition.ConditionType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.kogito.Application;
import org.kie.kogito.decision.DecisionModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@Startup
@ApplicationScoped
public class ConditionTypeStartup {
    private static final Logger log = Logger.getLogger(ConditionTypeStartup.class);

    @ConfigProperty(name = "condition.namespace", defaultValue = "saman-core")
    String namespace;

    @Inject
    Application application;

    @PostConstruct
    public void init() {
        log.info("INIT ConditionTypeStartup");
        Stream<Path> paths = null;
        try {
            var url = Thread.currentThread().getContextClassLoader().getResource("undeleteme.txt");
            assert url != null;
            var resource = url.getPath().replace("/undeleteme.txt", "");
            log.info("resource: " + resource);

            paths = Files.walk(Path.of(resource));
            List<String> dmnNames = paths
                    .filter(p -> p.toString().toLowerCase().endsWith(".dmn"))
                    .map(p -> {
                        var split = p.toString().split("/");
                        return Arrays.stream(split).reduce((first, second) -> second).orElse("").replaceAll("(?i)\\.dmn", "");
                    })
                    .distinct()
                    .toList();
            InstanceConstants.DMN_INPUTS.addAll(returnAllDependencies(dmnNames));

            Arrays.stream(ConditionType.values())
                    .toList()
                    .forEach(conditionType -> generateDependencies(dmnNames, conditionType));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (paths != null)
                paths.close();
        }
    }

    protected List<String> returnAllDependencies(List<String> dmnNames) {
        var dependencies = new ArrayList<String>();
        dmnNames.forEach(dmnName -> {
            DecisionModel decision = application.get(org.kie.kogito.decision.DecisionModels.class).getDecisionModel(namespace, dmnName);
            var inputs = decision.getDMNModel().getInputs().stream()
                    .map(DMNNode::getName)
                    .toList();
            dependencies.addAll(inputs);
        });
        return dependencies;
    }

    protected void generateDependencies(List<String> dmnNames, ConditionType type) {
        var finalLength = InstanceConstants.CONDITION_GRAPHS.get(type).getSuffix().length();
        var dependencies = new HashMap<String, List<String>>();

        dmnNames.stream()
                .filter(dmnName -> isConditionDmn(dmnName, InstanceConstants.CONDITION_GRAPHS.get(type).getSuffix()))
                .forEach(dmnName -> {
                    DecisionModel decision = application.get(org.kie.kogito.decision.DecisionModels.class).getDecisionModel(namespace, dmnName);

                    var propertyName = dmnName.substring(0, dmnName.length() - finalLength);
                    InstanceConstants.CONDITION_GRAPHS.get(type).getModels().put(propertyName, decision);
                    var inputs = decision.getDMNModel().getInputs().stream()
                            .map(DMNNode::getName)
                            .toList();

                    log.info("dependencies: " + propertyName);
                    log.info("dependencies: " + inputs);
                    dependencies.put(propertyName, inputs);
                });

        dependencies.forEach((target, value) -> {
            InstanceConstants.CONDITION_GRAPHS.get(type).getGraph().addVertex(target);
            value.forEach(source -> InstanceConstants.CONDITION_GRAPHS.get(type).getGraph().addVertex(source));
        });
        dependencies.forEach((target, value) ->
                value.forEach(source -> InstanceConstants.CONDITION_GRAPHS.get(type).getGraph().addEdge(source, target, source.concat("_").concat(target)))
        );
    }

    protected static boolean isConditionDmn(String dmnName, String type) {
        var nameLowerCase = dmnName.toLowerCase();
        return nameLowerCase.endsWith(type);
    }
}
