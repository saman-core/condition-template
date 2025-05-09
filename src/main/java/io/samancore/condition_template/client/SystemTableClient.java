package io.samancore.condition_template.client;

import io.quarkus.oidc.client.filter.OidcClientFilter;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.math.BigDecimal;
import java.util.Map;

@Path("")
@OidcClientFilter
@RegisterRestClient
public interface SystemTableClient {

    @POST
    @Path("/single/{property}")
    BigDecimal singleNumber(@PathParam("property") String property, Map<String, Object> filters);

    @POST
    @Path("/single/{property}")
    String singleString(@PathParam("property") String property, Map<String, Object> filters);
}
