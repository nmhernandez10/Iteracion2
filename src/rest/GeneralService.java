package rest;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tm.AlohAndesTransactionManager;
import vos.Espacio;
import vos.ListaEspacios;

@Path("general")
public class GeneralService {
	@Context
	private ServletContext context;

	private String getPath() {
		return context.getRealPath("WEB-INF/ConnectionData");
	}

	private String doErrorMessage(Exception e) {
		return "{ \"ERROR\": \"" + e.getMessage() + "\"}";
	}

	// RFC5

	@GET
	@Path("/usoUsuarios")
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

	// RFC6

	@GET
	@Path("/usoUsuario/"+ "{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEspacio(Espacio espacio, @PathParam("id") String idS) {
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {
			long id = Long.parseLong(idS);
			espacio = tm.cancelarEspacio(espacio, espacio.getFechaRetiroDate());
			return Response.status(200).entity(espacio).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
}
