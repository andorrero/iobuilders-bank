package com.ddg.bank.infra.inputadapter;

import com.ddg.bank.domain.model.WalletDTO;
import com.ddg.bank.domain.model.WalletMovementDTO;
import com.ddg.bank.domain.model.WalletTransferenceDTO;
import com.ddg.bank.infra.inputport.WalletInputPort;
import com.ddg.bank.infra.inputport.validation.UUID;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/bank")
@RequestScoped
public class BankWalletRestResource {

    @Inject
    WalletInputPort walletInputPort;

    @RolesAllowed({"USER", "ADMIN"})
    @POST
    @Path("/wallet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@Valid WalletDTO walletDTO) {
        final WalletDTO responseWallet = walletInputPort.create(walletDTO);
        return Response.created(URI.create("/bank/wallet/" + responseWallet.getWalletId())).entity(responseWallet).build();
    }

    @RolesAllowed({"USER", "ADMIN"})
    @GET
    @Path("/wallet/{walletId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response view(@PathParam("walletId") @UUID String walletId) {
        return Response.ok(walletInputPort.view(walletId)).build();
    }

    @RolesAllowed({"USER", "ADMIN"})
    @POST
    @Path("/wallet/{walletId}/deposit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deposit(@PathParam("walletId") @UUID String walletId, @Valid WalletMovementDTO walletMovementDTO) {
        final WalletMovementDTO responseWalletMovement = walletInputPort.deposit(walletId, walletMovementDTO);
        return Response.ok().entity(responseWalletMovement).build();
    }

    @RolesAllowed({"USER", "ADMIN"})
    @POST
    @Path("/wallet/transference")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transference(@Valid WalletTransferenceDTO walletTransferenceDTO) {
        final WalletTransferenceDTO responseWalletTransference = walletInputPort.transference(walletTransferenceDTO);
        return Response.ok().entity(responseWalletTransference).build();
    }

}
