package io.samancore;

import io.quarkus.runtime.Startup;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Startup
@ApplicationScoped
public class ConditionTypeStartup {
    private static final Logger log = Logger.getLogger(ConditionTypeStartup.class);

    @ConfigProperty(name = "condition.namespace", defaultValue = "ok")
    String namespace;

    @Inject()
    Application application;

    @PostConstruct
    public void init() {
        log.info("INIT ConditionTypeStartup");
        try {
            var url = Thread.currentThread().getContextClassLoader().getResource("undeleteme.txt");
            var resource = url.getPath().replace("/undeleteme.txt", "");
            log.info("resource: " + resource);

            var dmnNames = Files.walk(Path.of(resource))
                    .filter(p -> p.toString().toLowerCase().endsWith(".dmn"))
                    .map(p -> {
                        var split = p.toString().split("/");
                        return Arrays.stream(split).reduce((first, second) -> second).orElse("").replaceAll("(?i)\\.dmn", "");
                    })
                    .distinct()
                    .toList();

            generateDependencies(dmnNames, ConditionType.VISIBLE);
            generateDependencies(dmnNames, ConditionType.VALUE);
            generateDependencies(dmnNames, ConditionType.DISABLE);
            generateDependencies(dmnNames, ConditionType.ALERT);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void generateDependencies(List<String> dmnNames, ConditionType type) {
        var finalLength = type.getSuffix().length();
        var dependencies = new HashMap<String, List<String>>();

        dmnNames.stream()
                .filter(dmnName -> isConditionDmn(dmnName, type.getSuffix()))
                .forEach(dmnName -> {
                    DecisionModel decision = application.get(org.kie.kogito.decision.DecisionModels.class).getDecisionModel(namespace, dmnName);

                    var propertyName = dmnName.toLowerCase().substring(0, dmnName.length() - finalLength);
                    type.getModels().put(propertyName, decision);
                    var inputs = decision.getDMNModel().getInputs().stream()
                            .map(DMNNode::getName)
                            .map(String::toLowerCase)
                            .toList();

                    log.info("dependencies: " + propertyName);
                    log.info("dependencies: " + inputs);
                    dependencies.put(propertyName, inputs);
                });

        dependencies.forEach((target, value) -> {
            type.getGraph().addVertex(target);
            value.forEach(source -> type.getGraph().addVertex(source));
        });
        dependencies.forEach((target, value) ->
                value.forEach(source -> type.getGraph().addEdge(source, target, source.concat("_").concat(target)))
        );
    }

    private static boolean isConditionDmn(String dmnName, String type) {
        var nameLowerCase = dmnName.toLowerCase();
        return nameLowerCase.endsWith(type);
    }
}
