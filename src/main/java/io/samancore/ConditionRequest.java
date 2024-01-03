package io.samancore;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;
import java.util.Set;

@Value
@Jacksonized
@Builder(
        setterPrefix = "set",
        builderMethodName = "newBuilder",
        toBuilder = true
)
public class ConditionRequest {
    Map<String, Object> variables;
    Set<String> modifiedProperties;
    boolean isInitial;
}
