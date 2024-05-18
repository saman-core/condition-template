package io.samancore.condition_template.model;

import io.samancore.common.model.condition.ConditionType;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder(
        setterPrefix = "set",
        builderMethodName = "newBuilder",
        toBuilder = true
)
public class Condition {
    String property;
    ConditionType conditionType;
    Object value;
}
