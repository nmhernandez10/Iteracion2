package vos;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Nicolás Mateo Hernández Rojas - nm.hernandez10@uniandes.edu.co
 * @author David Felipe Niño Romero - df.nino10@uniandes.edu.co Clase que
 *         representa a la lista de Habitaciones del modelo AlohAndes
 */

public class ListaHabitaciones {
	@JsonProperty(value = "habitaciones")
	private List<Habitacion> habitaciones;

	public ListaHabitaciones(@JsonProperty(value = "habitaciones") List<Habitacion> habitaciones) {
		this.habitaciones = habitaciones;
	}

	public List<Habitacion> getHabitaciones() {
		return habitaciones;
	}

	public void setHabitaciones(List<Habitacion> habitaciones) {
		this.habitaciones = habitaciones;
	}
}
