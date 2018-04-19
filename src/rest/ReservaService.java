package rest;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tm.AlohAndesTransactionManager;
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
	@Path("/reserva")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response agregarReserva(Reserva reserva) {
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try 
		{
			System.out.println(reserva.getFechaInicio());
			System.out.println(reserva.getFechaReserva());
			tm.addReserva(reserva);
			return Response.status(200).entity(reserva).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}

	// RF5

	@DELETE
	@Path("/reserva")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response cancelarReserva(Reserva reserva) {
		AlohAndesTransactionManager tm = new AlohAndesTransactionManager(getPath());

		try {
			reserva = tm.cancelarReserva(reserva);
			return Response.status(200).entity(reserva).build();
		} catch (Exception e) {
			return Response.status(500).entity(doErrorMessage(e)).build();
		}
	}
}
