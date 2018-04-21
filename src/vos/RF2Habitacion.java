package vos;

import org.codehaus.jackson.annotate.JsonProperty;

public class RF2Habitacion
{
	@JsonProperty(value = "categoria")
	private String categoria;

	@JsonProperty(value = "compartido")
	private boolean compartido;

	@JsonProperty(value = "capacidad")
	private int capacidad;

	public RF2Habitacion(@JsonProperty(value = "categoria") String categoria,
			@JsonProperty(value = "compartido") boolean compartido, @JsonProperty(value = "capacidad") int capacidad) {
		this.capacidad = capacidad;
		this.categoria = categoria;
		this.compartido = compartido;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
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
}
