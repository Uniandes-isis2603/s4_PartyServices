/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.partyServices.resources;

import co.edu.uniandes.csw.partyServices.dtos.ClienteDetailDTO;

import co.edu.uniandes.csw.partyServices.dtos.ClienteDTO;
import co.edu.uniandes.csw.partyServices.ejb.ClienteLogic;
import co.edu.uniandes.csw.partyServices.entities.ClienteEntity;
import co.edu.uniandes.csw.partyServices.exceptions.BusinessLogicException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

/**
 * Clase que implementa el recurso "Clientes"
 *
 * @author Elias David Negrete Salgado
 */
@Path("clientes")
@Produces("application/json")
@Consumes("application/json")
@RequestScoped
public class ClienteResource {

    @Inject
    private ClienteLogic clienteLogic;

    private static final Logger LOGGER = Logger.getLogger(ClienteResource.class.getName());

    @GET
    @Path("{clientesId: \\d+}")
    public ClienteDTO getCliente(@PathParam("clientesId") Long clienteId) throws BusinessLogicException {

        LOGGER.log(Level.INFO, "ClienteResource getCliente: input: {0}", clienteId);
        ClienteEntity clienteEntity = clienteLogic.getCliente(clienteId);
        if (clienteEntity == null) {
            throw new WebApplicationException("El recurso /clientes/" + clienteId + " no existe.", 404);
        }
        ClienteDetailDTO clienteDetailDTO = new ClienteDetailDTO(clienteEntity);
        LOGGER.log(Level.INFO, "ClienteResource getCliente: output: {0}", clienteDetailDTO.toString());
        return clienteDetailDTO;

    }

    /**
     * Busca y devuelve todos los clientes que existen en la aplicacion.
     *
     * @return JSONArray {@link ClienteDetailDTO} - Los clientes encontrados en la
     * aplicación. Si no hay ninguno retorna una lista vacía.
     */
    @GET
    public List<ClienteDetailDTO> getClientes() {
        LOGGER.info("ClienteResource getClientes: input: void");
        List<ClienteDetailDTO> listaCliente = listEntity2DetailDTO(clienteLogic.getClientes());
        LOGGER.log(Level.INFO, "ClienteResource getClientes: output: {0}", listaCliente.toString());
        return listaCliente;
    }

    @POST
    public ClienteDTO createCliente(ClienteDTO pCliente) throws BusinessLogicException {
        ClienteDTO nuevoClienteDTO = new ClienteDTO(clienteLogic.createCliente(pCliente.toEntity()));
        LOGGER.log(Level.INFO, "ClienteResource create: output: {0}", nuevoClienteDTO.toString());
        return nuevoClienteDTO;
    }

    @PUT
    @Path("{clientesId: \\d+}")
    public ClienteDTO modificarCliente(@PathParam("clientesId") Long clienteId, ClienteDTO pCliente) throws BusinessLogicException {

        LOGGER.log(Level.INFO, "ClienteResource updateCliente: input: id: {0} , cliente: {1}", new Object[]{clienteId, pCliente.toString()});
        pCliente.setId(clienteId);
        if (clienteLogic.getCliente(clienteId) == null) {
            throw new WebApplicationException("El recurso /clientes/" + clienteId + " no existe.", 404);
        }
        ClienteDetailDTO DTO = new ClienteDetailDTO(clienteLogic.updateCliente(pCliente.toEntity()));
        LOGGER.log(Level.INFO, "ClienteResource updateCliente: output: {0}", DTO.toString());
        return DTO;
    }

    @DELETE
    @Path("{clientesId: \\d+}")
    public void deleteCliente(@PathParam("clientesId") Long clienteId) throws BusinessLogicException {
        LOGGER.log(Level.INFO, "ClienteResource delete: input: {0}", clienteId);
        ClienteEntity entity = clienteLogic.getCliente(clienteId);
        if (entity == null) {
            throw new WebApplicationException("El recurso /clientes/" + clienteId + " no existe.", 404);
        }
        clienteLogic.deleteCliente(clienteId);
        LOGGER.info("BookResource deleteBook: output: void");
    }

    /**
     * Convierte una lista de entidades a DTO.
     *
     * @param entityList corresponde a la lista de clientes de tipo Entity que
     * vamos a convertir a DTO.
     * @return la lista de clientes en forma DTO (json)
     */
    private List<ClienteDetailDTO> listEntity2DetailDTO(List<ClienteEntity> entityList) {
        List<ClienteDetailDTO> list = new ArrayList<>();
        for (ClienteEntity entity : entityList) {
            list.add(new ClienteDetailDTO(entity));
        }
        return list;
    }

    @Path("{clientesId: \\d+}/pagoss")
    public Class<PagoResource> getPagoResource(@PathParam("clientesId") Long clientesId) throws BusinessLogicException {
        if (clienteLogic.getCliente(clientesId) == null) {
            throw new WebApplicationException("El recurso /clientes/" + clientesId + "/pagos no existe.", 404);
        }
        return PagoResource.class;
    }

}
