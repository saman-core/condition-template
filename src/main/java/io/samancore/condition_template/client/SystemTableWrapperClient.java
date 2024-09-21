package io.samancore.condition_template.client;

import io.quarkus.arc.Unremovable;

import java.util.Map;

@Unremovable
public class SystemTableWrapperClient {

    public static String call(String systemTableName, String systemTableProperty, Map<String, Object> conditions) {
        //TODO add rest client
        return "";
    }
}
