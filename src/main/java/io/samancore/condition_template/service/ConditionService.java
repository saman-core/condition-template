package io.samancore.condition_template.service;

import io.samancore.condition_template.constant.InstanceConstants;
import io.samancore.common.model.condition.Condition;
import io.samancore.common.model.condition.ConditionRequest;
import io.samancore.common.model.condition.ConditionType;
import io.samancore.condition_template.util.DmnUtil;
import io.samancore.condition_template.util.GraphUtil;
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
        fillVariables(variables);
        var modifiedProperties = conditionRequest.getModifiedProperties();
        var isEvalAll = conditionRequest.isEvalAll();

        conditions.addAll(
                getConditionsByType(ConditionType.VALUE, isEvalAll, variables, modifiedProperties)
        );
        conditions.addAll(
                getConditionsByType(ConditionType.VALIDATE, isEvalAll, variables, modifiedProperties)
        );
        if (conditionRequest.getWithoutPresentation() == null || !conditionRequest.getWithoutPresentation()) {
            conditions.addAll(
                    getConditionsByType(ConditionType.VISIBLE, isEvalAll, variables, modifiedProperties)
            );
            conditions.addAll(
                    getConditionsByType(ConditionType.DISABLE, isEvalAll, variables, modifiedProperties)
            );
            conditions.addAll(
                    getConditionsByType(ConditionType.ALERT, isEvalAll, variables, modifiedProperties)
            );
            conditions.addAll(
                    getConditionsByType(ConditionType.OPTIONS, isEvalAll, variables, modifiedProperties)
            );
        }

        return conditions;
    }

    protected void fillVariables(Map<String, Object> variables) {
        InstanceConstants.DMN_INPUTS.forEach(input -> variables.putIfAbsent(input, null));
    }

    protected List<Condition> getConditionsByType(final ConditionType conditionType,
                                                  final boolean isEvalAll,
                                                  Map<String, Object> variables,
                                                  Set<String> modifiedProperties) {
        var conditions = new ArrayList<Condition>();

        List<String> dmnNameList;
        if (isEvalAll) {
            dmnNameList = graphUtil.getDmnNameDependencies(conditionType);
        } else {
            dmnNameList = graphUtil.getDmnNameDependencies(conditionType, modifiedProperties);
        }

        dmnNameList.forEach(dmnName -> {
            var result = dmnUtil.execute(conditionType, dmnName, variables, entry);
            var condition = createCondition(dmnName, conditionType, result);
            if (condition.getValue() != null)
                conditions.add(condition);

            if (InstanceConstants.CONDITION_GRAPHS.get(conditionType).isUpdateCascade()) {
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
