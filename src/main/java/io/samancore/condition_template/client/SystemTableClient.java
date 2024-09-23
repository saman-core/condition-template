package io.samancore.condition_template.client;

import io.quarkus.oidc.client.filter.OidcClientFilter;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
