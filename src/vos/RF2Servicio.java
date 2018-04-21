package vos;

import org.codehaus.jackson.annotate.JsonProperty;

public class RF2Servicio {
	
	@JsonProperty(value = "categoria")
	private String categoria;

	@JsonProperty(value = "descripcion")
	private String descripcion;

	@JsonProperty(value = "precioAdicional")
	private double precioAdicional;

	@JsonProperty(value = "inicioHorario")
	private int inicioHorario;

	@JsonProperty(value = "finHorario")
	private int finHorario;

	public RF2Servicio(@JsonProperty(value = "categoria") String categoria,
			@JsonProperty(value = "descripcion") String descripcion,
			@JsonProperty(value = "precioAdicional") double precioAdicional,
			@JsonProperty(value = "inicioHorario") int inicioHorario,
			@JsonProperty(value = "finHorario") int finHorario) {
		this.categoria = categoria;
		this.descripcion = descripcion;
		this.precioAdicional = precioAdicional;
		this.inicioHorario = inicioHorario;
		this.finHorario = finHorario;
	}


	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getPrecioAdicional() {
		return precioAdicional;
	}

	public void setPrecioAdicional(double precioAdicional) {
		this.precioAdicional = precioAdicional;
	}

	public int getInicioHorario() {
		return inicioHorario;
	}

	public void setInicioHorario(int inicioHorario) {
		this.inicioHorario = inicioHorario;
	}

	public int getFinHorario() {
		return finHorario;
	}

	public void setFinHorario(int finHorario) {
		this.finHorario = finHorario;
	}
}
