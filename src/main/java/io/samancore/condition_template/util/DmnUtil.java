package io.samancore.condition_template.util;

import io.samancore.condition_template.constant.InstanceConstants;
import io.samancore.common.model.condition.ConditionType;
import org.jboss.logging.Logger;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.dmn.rest.DMNJSONUtils;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class DmnUtil {
    private static final Logger log = Logger.getLogger(DmnUtil.class);

    public Object execute(ConditionType type, String modelName, Map<String, Object> variables, String entry) {
        var dmnModel = getModelByName(type, modelName);
        var decisionResult = dmnModel.evaluateAll(DMNJSONUtils.ctx(dmnModel, variables));

        if (decisionResult.hasErrors()) {
            log.error(decisionResult.toString());
            return new Object();
        }
        return decisionResult.getContext().get(entry);
    }

    protected DecisionModel getModelByName(ConditionType type, String name) {
        return InstanceConstants.CONDITION_GRAPHS.get(type).getModels().get(name);
    }
}
