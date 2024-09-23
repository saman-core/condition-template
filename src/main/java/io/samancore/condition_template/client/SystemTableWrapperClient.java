package io.samancore.condition_template.client;

import io.quarkus.arc.Unremovable;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

@Unremovable
public class SystemTableWrapperClient {
    private static final Logger log = Logger.getLogger(SystemTableWrapperClient.class);
    public static String urlPrefix;
    public static String urlSuffix;

    private SystemTableWrapperClient() {
    }

    public static BigDecimal fetchNumber(String systemTableName, String systemTableProperty, Map<String, Object> conditions) {
        log.debug("SystemTableWrapperClient.fetchNumber");
        var url = generateUrl(systemTableName);
        var conditionTemplateRestClient = RestClientBuilder.newBuilder()
                .baseUri(URI.create(url))
                .build(SystemTableClient.class);
        return conditionTemplateRestClient.singleNumber(systemTableProperty, conditions);
    }

    public static String fetchString(String systemTableName, String systemTableProperty, Map<String, Object> conditions) {
        log.debug("SystemTableWrapperClient.fetchNumber");
        var url = generateUrl(systemTableName);
        var conditionTemplateRestClient = RestClientBuilder.newBuilder()
                .baseUri(URI.create(url))
                .build(SystemTableClient.class);
        return conditionTemplateRestClient.singleString(systemTableProperty, conditions);
    }

    private static String generateUrl(String systemTableName) {
        return urlPrefix.concat(systemTableName).concat(urlSuffix);
    }
}
