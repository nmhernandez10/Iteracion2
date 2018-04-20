package rest;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tm.AlohAndesTransactionManager;
import vos.ListaEspacios;
import vos.ListaRFC1;
import vos.ListaRFC3;

@Path("operadores")
public class OperadorService {

	@Context
	private ServletContext context;

	private String getPath() {
		return context.getRealPath("WEB-INF/ConnectionData");
	}

	private String doErrorMessage(Exception e) {
		return "{ \"ERROR\": \"" + e.getMessage() + "\"}";
	}

	// RFC1
	@GET
	@Path("/ingresos")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getBoletas() {
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {
			ListaRFC1 ingresos = new ListaRFC1(tm.ingresosOperadores());
			return Response.status(200).entity(ingresos).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
	
	//RFC3
	
	@GET
	@Path("/ocupaciones")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response ocupacionOperadores() {
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {
			ListaRFC3 ocupaciones = new ListaRFC3(tm.ocupacionOperadores());
			return Response.status(200).entity(ocupaciones).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
}
