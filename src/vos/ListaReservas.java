package vos;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Nicolás Mateo Hernández Rojas - nm.hernandez10@uniandes.edu.co
 * @author David Felipe Niño Romero - df.nino10@uniandes.edu.co Clase que
 *         representa a la lista de Reservas del modelo AlohAndes
 */

public class ListaReservas {
	@JsonProperty(value = "reservas")
	private List<Reserva> reservas;

	public ListaReservas(@JsonProperty(value = "reservas") List<Reserva> reservas) {
		this.reservas = reservas;
	}

	public List<Reserva> getReservas() {
		return reservas;
	}

	public void setReservas(List<Reserva> reservas) {
		this.reservas = reservas;
	}
}
