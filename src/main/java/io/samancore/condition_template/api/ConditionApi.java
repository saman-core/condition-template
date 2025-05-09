package io.samancore.condition_template.api;

import io.samancore.common.model.condition.Condition;
import io.samancore.common.model.condition.ConditionRequest;
import io.samancore.condition_template.service.ConditionService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Path("/conditions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConditionApi {

    @Inject
    ConditionService service;

    @POST
    @RolesAllowed({"admin"})
    @Path("/eval")
    public List<Condition> eval(ConditionRequest initialConditionRequest) {
        var conditionRequestBuilder = initialConditionRequest.toBuilder();

        if (initialConditionRequest.getModifiedProperties() == null) {
            conditionRequestBuilder = conditionRequestBuilder.setModifiedProperties(new HashSet<>());
        }
        if (initialConditionRequest.getVariables() == null) {
            conditionRequestBuilder = conditionRequestBuilder.setVariables(new HashMap<>());
        }
        return service.eval(conditionRequestBuilder.build());
    }
}