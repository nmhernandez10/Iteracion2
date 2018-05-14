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
import vos.ListaRFC8;
import vos.ListaRFC9;
import vos.RF2;
import vos.RF3;
import vos.RF9;
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

	// RF2
	
	@POST
	@Path("/agregarEspacio")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEspacio(RF2 rf2) {
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {
			
			Espacio espacio = tm.addEspacio(rf2);
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
			return Response.status(200).entity(espacio).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
	
	// RFC2

	@GET
	@Path("/espaciosPopulares")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response espaciosPopulares() {
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {
			ListaEspacios espacios = new ListaEspacios(tm.espaciosPopulares());
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
			return Response.status(200).entity(espacios).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
	
	// RFC4
	
	@POST
	@Path("/espaciosDisponibles")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response espaciosDisponibles(RFC4 rfc4) {
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {			
			List<Espacio> espacios = tm.espaciosDisponibles(rfc4);
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
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
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {
			espacio = tm.cancelarEspacio(espacio, espacio.getFechaRetiroDate(), false);
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
			return Response.status(200).entity(espacio).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
	
	// RFC8

	@GET
	@Path("/clientesFrecuentes/" + "{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response clientesFrecuentes(@PathParam("id") String idE) 
	{
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {

			ListaRFC8 clientes = tm.clientesFrecuentes(Long.parseLong(idE));
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
			return Response.status(200).entity(clientes).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
	
	// RFC9
	
	@GET
	@Path("/espaciosPocoDemandados")
	@Produces(MediaType.APPLICATION_JSON)
	public Response espaciosPocoDemandados() 
	{
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {

			ListaRFC9 espacios = tm.espaciosPocoDemandados();
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
			return Response.status(200).entity(espacios).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}	
	
	@PUT
	@Path("/deshabilitarEspacio")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deshabilitarEspacio(RF9 rf9)
	{
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {

			List<String> resultados = tm.deshabilitarEspacio(rf9);
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
			return Response.status(200).entity(resultados).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
	
	@PUT
	@Path("/habilitarEspacio/" + "{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response habilitarEspacio(@PathParam("id") String idS)
	{
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {
			Long id = Long.parseLong(idS);
			List<String> resultados = new ArrayList<String>();
			resultados.add(tm.habilitarEspacio(id));
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
			return Response.status(200).entity(resultados).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
}
