package io.samancore.condition_template.util;

import io.quarkus.test.junit.QuarkusTest;
import io.samancore.common.model.condition.ConditionType;
import io.samancore.condition_template.util.DmnUtil;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;

@QuarkusTest
class DmnUtilTest {

    @Inject
    DmnUtil dmnUtil;

    @Test
    void execute() {
        Map<String, Object> map = Map.of(
                "Driver", Map.of("Points", 2),
                "Violation", Map.of("Type", "speed", "Actual Speed", 120, "Speed Limit", 100));

        String entry = "result";
        String modelName = "traffic_value";

        var result = (Boolean) dmnUtil.execute(ConditionType.VALUE, modelName, map, entry);
        assertFalse(result);
    }
}