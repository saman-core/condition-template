package io.samancore;

import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.dmn.rest.DMNJSONUtils;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class DmnUtil {

    public Object execute(ConditionType type, String modelName, Map<String, Object> variables, String entry) {
        var dmnModel = getModelByName(type, modelName);
        var decisionResult = dmnModel.evaluateAll(DMNJSONUtils.ctx(dmnModel, variables));

        if (decisionResult.hasErrors()) {
            throw new IllegalArgumentException();
        }
        return decisionResult.getContext().get(entry);
    }

    protected DecisionModel getModelByName(ConditionType type, String name) {
        return type.getModels().get(name);
    }
}
