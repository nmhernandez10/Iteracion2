package rest;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tm.AlohAndesTransactionManager;
import vos.Cliente;
import vos.Espacio;
import vos.RF3;

@Path("clientes")
public class ClienteService {
	@Context
	private ServletContext context;

	private String getPath() {
		return context.getRealPath("WEB-INF/ConnectionData");
	}

	private String doErrorMessage(Exception e) {
		return "{ \"ERROR\": \"" + e.getMessage() + "\"}";
	}
	
	// RF3
	
	@POST
	@Path("/agregarCliente")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEspacio(RF3 rf3) {
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {
			
			Cliente cliente = tm.addCliente(rf3);
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
			return Response.status(200).entity(cliente).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
	
}
