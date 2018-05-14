package rest;

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
import vos.Espacio;
import vos.ListaEspacios;
import vos.ListaRFC5;
import vos.RFC6;
import vos.RFC7;

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
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {
			ListaRFC5 espacios = new ListaRFC5(tm.usosPorCategoria());
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
			return Response.status(200).entity(espacios).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}

	// RFC6

	@GET
	@Path("/usoUsuario/"+ "{tipo}"+"/"+"{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEspacio(@PathParam("id") String idS, @PathParam("tipo") String tipo) {
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try 
		{
			long id = Long.parseLong(idS);
			RFC6 usuario = tm.usoPorUsuario(id, tipo);
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
			return Response.status(200).entity(usuario).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
	
	//RFC7
	@POST
	@Path("/analisisOperacion")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response analizarOperacion(RFC7 rfc7) throws Exception {
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try 
		{
			List<String> resultados = tm.analizarOperacion(rfc7);
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
			return Response.status(200).entity(resultados).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		
	}
	
}
