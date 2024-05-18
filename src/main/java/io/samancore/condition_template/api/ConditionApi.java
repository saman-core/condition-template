package io.samancore.condition_template.api;

import io.samancore.condition_template.model.Condition;
import io.samancore.condition_template.model.ConditionRequest;
import io.samancore.condition_template.service.ConditionService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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