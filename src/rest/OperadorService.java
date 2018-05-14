package rest;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tm.AlohAndesTransactionManager;
import vos.Cliente;
import vos.ListaEspacios;
import vos.ListaRFC1;
import vos.ListaRFC3;
import vos.Operador;
import vos.RF1;
import vos.RF3;

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

	// RF1
	
	@POST
	@Path("/agregarOperador")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEspacio(RF1 rf1) {
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {
			
			Operador operador = tm.addOperador(rf1);
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
			return Response.status(200).entity(operador).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
	
	// RFC1
	@GET
	@Path("/ingresos")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getIngresos() {
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {
			ListaRFC1 ingresos = new ListaRFC1(tm.ingresosOperadores());
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
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
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {
			ListaRFC3 ocupaciones = new ListaRFC3(tm.ocupacionOperadores());
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
			return Response.status(200).entity(ocupaciones).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
}
