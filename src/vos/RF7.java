package vos;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class RF7 {
	
	@JsonProperty("idCliente")
	private long idCliente;
	
	@JsonProperty("cantidad")
	private int cantidad;
	
	@JsonProperty("categoria")
	private String categoria;
	
	@JsonProperty("tipoHabitacion")
	private String tipoHabitacion;
	
	@JsonProperty("fechaInicio")	
	private String fechaInicio;
	
	@JsonProperty("duracion")
	private int duracion;
	
	@JsonProperty("servicios")
	private List<String> servicios;
	
	public RF7(@JsonProperty("idCliente") long idCliente, @JsonProperty("cantidad") int cantidad, @JsonProperty("categoria") String categoria, @JsonProperty("tipoHabitacion") String tipoHabitacion, @JsonProperty("fechaInicio") String fechaInicio, @JsonProperty("duracion") int duracion, @JsonProperty("servicios") List<String> servicios )
	{
		this.idCliente = idCliente;
		this.cantidad = cantidad;
		this.categoria = categoria;
		this.tipoHabitacion = tipoHabitacion;
		this.fechaInicio = fechaInicio;
		this.duracion = duracion;
		this.servicios = servicios;
		
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getTipoHabitacion() {
		return tipoHabitacion;
	}	
	
	public String getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(String fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public void setTipoHabitacion(String tipoHabitacion) {
		this.tipoHabitacion = tipoHabitacion;
	}

	public List<String> getServicios() {
		return servicios;
	}

	public void setServicios(List<String> servicios) {
		this.servicios = servicios;
	}

	public long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(long idCliente) {
		this.idCliente = idCliente;
	}

	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}
}
