package rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tm.AlohAndesTransactionManager;
import vos.Cliente;
import vos.Espacio;
import vos.ListaClientes;
import vos.ListaEspacios;
import vos.RFC4;

@Path("espacios")
public class EspacioService {
	@Context
	private ServletContext context;

	private String getPath() {
		return context.getRealPath("WEB-INF/ConnectionData");
	}

	private String doErrorMessage(Exception e) {
		return "{ \"ERROR\": \"" + e.getMessage() + "\"}";
	}

	// RFC2

	@GET
	@Path("/espaciosPopulares")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response espaciosPopulares() {
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {
			ListaEspacios espacios = new ListaEspacios(tm.espaciosPopulares());
			return Response.status(200).entity(espacios).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
	
	//RFC4
	
	@POST
	@Path("/espaciosDisponibles")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEspacio(RFC4 rfc4) {
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {			
			List<Espacio> espacios = tm.espaciosDisponibles(rfc4);
			return Response.status(200).entity(espacios).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
	
	// RF6

	@DELETE
	@Path("/cancelarEspacio")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEspacio(Espacio espacio) {
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {
			
			espacio = tm.cancelarEspacio(espacio, espacio.getFechaRetiroDate());
			return Response.status(200).entity(espacio).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
	
	// RFC8

	@DELETE
	@Path("/clientesFrecuentes/" + "{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response clientesFrecuentes(@PathParam("id") String idE) 
	{
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {

			ListaClientes clientes = new ListaClientes(new ArrayList<Cliente>());
			return Response.status(200).entity(clientes).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
}
