package vos;

import org.codehaus.jackson.annotate.*;

/**
 * @author Nicolás Mateo Hernández Rojas - nm.hernandez10@uniandes.edu.co
 * @author David Felipe Niño Romero - df.nino10@uniandes.edu.co Clase que
 *         representa a las Habitaciones del modelo AlohAndes
 */

public class Habitacion 
{
	@JsonProperty(value = "id")
	private long id;

	@JsonProperty(value = "categoria")
	private CategoriaHabitacion categoria;

	@JsonProperty(value = "compartido")
	private boolean compartido;

	@JsonProperty(value = "capacidad")
	private int capacidad;

	@JsonProperty(value = "espacio")
	private long espacio;

	public Habitacion(@JsonProperty(value = "id") long id,
			@JsonProperty(value = "categoria") CategoriaHabitacion categoria,
			@JsonProperty(value = "compartido") boolean compartido, @JsonProperty(value = "capacidad") int capacidad,
			@JsonProperty(value = "espacio") long espacio) {
		this.id = id;
		this.capacidad = capacidad;
		this.categoria = categoria;
		this.compartido = compartido;
		this.espacio = espacio;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public CategoriaHabitacion getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaHabitacion categoria) {
		this.categoria = categoria;
	}

	public boolean isCompartido() {
		return compartido;
	}

	public void setCompartido(boolean compartido) {
		this.compartido = compartido;
	}

	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	public long getEspacio() {
		return espacio;
	}

	public void setEspacio(long espacio) {
		this.espacio = espacio;
	}
}
