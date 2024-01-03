package io.samancore;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class ConditionService {

    @ConfigProperty(name = "condition.entry", defaultValue = "result")
    String entry;

    @Inject
    GraphUtil graphUtil;

    @Inject
    DmnUtil dmnUtil;

    public List<Condition> eval(ConditionRequest conditionRequest) {
        var conditions = new ArrayList<Condition>();
        var variables = conditionRequest.getVariables();
        var modifiedProperties = conditionRequest.getModifiedProperties();
        var isInitial = conditionRequest.isInitial();

        conditions.addAll(
                getConditionsByType(ConditionType.VALUE, isInitial, variables, modifiedProperties)
        );
        conditions.addAll(
                getConditionsByType(ConditionType.VISIBLE, isInitial, variables, modifiedProperties)
        );
        conditions.addAll(
                getConditionsByType(ConditionType.DISABLE, isInitial, variables, modifiedProperties)
        );
        conditions.addAll(
                getConditionsByType(ConditionType.ALERT, isInitial, variables, modifiedProperties)
        );

        return conditions;
    }

    protected List<Condition> getConditionsByType(final ConditionType conditionType,
                                                  final boolean isInitial,
                                                  Map<String, Object> variables,
                                                  Set<String> modifiedProperties) {
        var conditions = new ArrayList<Condition>();

        List<String> dmnNameList;
        if (isInitial) {
            dmnNameList = graphUtil.getDmnNameDependencies(conditionType);
        } else {
            dmnNameList = graphUtil.getDmnNameDependencies(conditionType, modifiedProperties);
        }

        dmnNameList.forEach(dmnName -> {
            var result = dmnUtil.execute(conditionType, dmnName, variables, entry);
            var condition = createCondition(dmnName, conditionType, result);
            conditions.add(condition);

            if (conditionType.isUpdateCascade()) {
                variables.put(dmnName, result);
                modifiedProperties.add(dmnName);
            }
        });
        return conditions;
    }

    protected Condition createCondition(String property, ConditionType conditionType, Object value) {
        return Condition.newBuilder()
                .setProperty(property)
                .setConditionType(conditionType)
                .setValue(value)
                .build();
    }
}
