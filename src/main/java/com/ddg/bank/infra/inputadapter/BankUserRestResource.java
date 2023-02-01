package com.ddg.bank.infra.inputadapter;

import com.ddg.bank.application.security.TokenGenerator;
import com.ddg.bank.domain.model.UserDTO;
import com.ddg.bank.infra.inputport.UserInputPort;
import com.ddg.bank.infra.inputport.validation.UUID;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/bank")
@RequestScoped
public class BankUserRestResource {

    @Inject
    UserInputPort userInputPort;

    @Inject
    TokenGenerator tokenGenerator;

    @PermitAll
    @GET
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@NotEmpty @QueryParam("username") String username, @NotEmpty @QueryParam("password") String password) {
        var user = userInputPort.find(username, password);
        return Response.ok(tokenGenerator.generateToken(user.getName())).build();
    }

    @RolesAllowed("ADMIN")
    @POST
    @Path("/user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@Valid UserDTO userDTO) {
        final UserDTO responseUser = userInputPort.create(userDTO);
        return Response.created(URI.create("/bank/user/" + responseUser.getUserId())).entity(responseUser).build();
    }

    @RolesAllowed("ADMIN")
    @GET
    @Path("/user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response view(@PathParam("userId") @UUID String userId) {
        return Response.ok(userInputPort.view(userId)).build();
    }

}
