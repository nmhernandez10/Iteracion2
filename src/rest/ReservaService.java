package rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tm.AlohAndesTransactionManager;
import vos.ListaReservas;
import vos.RF7;
import vos.Reserva;

@Path("reservas")
public class ReservaService {
	@Context
	private ServletContext context;

	private String getPath() {
		return context.getRealPath("WEB-INF/ConnectionData");
	}

	private String doErrorMessage(Exception e) {
		return "{ \"ERROR\": \"" + e.getMessage() + "\"}";
	}

	// RF4

	@POST
	@Path("/agregarReserva")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response agregarReserva(Reserva reserva) {
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try 
		{
			System.out.println(reserva.getFechaInicio());
			System.out.println(reserva.getFechaReserva());
			tm.addReserva(reserva, false);	
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
			return Response.status(200).entity(reserva).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
		
	}

	// RF5

	@DELETE
	@Path("/cancelarReserva")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response cancelarReserva(Reserva reserva) {
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {
			reserva = tm.cancelarReserva(reserva, false, false);
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
			return Response.status(200).entity(reserva).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
	
	// RF7
	
	@PUT
	@Path("/reservaColectiva")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response reservaColectiva(RF7 rf7)
	{
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {
			ListaReservas reservas = new ListaReservas(tm.reservaColectiva(rf7));
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
			return Response.status(200).entity(reservas).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
	
	// RF8
	
	@DELETE
	@Path("/cancelarReservaColectiva/" + "{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response cancelarReservaColectiva(@PathParam("id") String idS) {
		long tiempo = System.currentTimeMillis();
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {
			long id = Long.parseLong(idS);
			List<String> resultado = new ArrayList<String>();
			resultado.add(tm.cancelarReservaColectiva(id));
			tiempo = System.currentTimeMillis() - tiempo;
			System.out.println("Esta transacción/consulta duró " + tiempo + " milisegundos");
			return Response.status(200).entity(resultado).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
}
