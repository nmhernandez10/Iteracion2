package vos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class RF2 {
	@JsonProperty(value = "id")
	private long id;

	@JsonProperty(value = "registro")
	private long registro;

	@JsonProperty(value = "tamaño")
	private double tamaño;

	@JsonProperty(value = "direccion")
	private String direccion;

	@JsonProperty(value = "precio")
	private double precio;

	@JsonProperty(value = "fechaRetiro")
	private String fechaRetiro;

	@JsonProperty(value = "operador")
	private long operador;

	@JsonProperty(value = "servicios")
	private List<RF2Servicio> servicios;

	@JsonProperty(value = "habitaciones")
	private List<RF2Habitacion> habitaciones;

	public RF2(@JsonProperty(value = "id") long id, @JsonProperty(value = "registro") long registro,
			@JsonProperty(value = "tamaño") double tamaño,
			@JsonProperty(value = "direccion") String direccion, @JsonProperty(value = "precio") double precio,
			@JsonProperty(value = "fechaRetiro") String fechaRetiro, @JsonProperty(value = "operador") long operador,
			@JsonProperty(value = "servicios") List<RF2Servicio> servicios,
			@JsonProperty(value = "habitaciones") List<RF2Habitacion> habitaciones) {
		this.id = id;
		this.registro = registro;
		this.tamaño = tamaño;
		this.direccion = direccion;
		this.precio = precio;
		this.fechaRetiro = fechaRetiro;		
		this.operador = operador;
		this.servicios = servicios;
		this.habitaciones = habitaciones;	
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getRegistro() {
		return registro;
	}

	public void setRegistro(long registro) {
		this.registro = registro;
	}

	public double getTamaño() {
		return tamaño;
	}

	public void setTamaño(double tamaño) {
		this.tamaño = tamaño;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public String getFechaRetiro() {
		return fechaRetiro;
	}

	public void setFechaRetiro(String fechaRetiro) {
		this.fechaRetiro = fechaRetiro;
		
	}

	public long getOperador() {
		return operador;
	}

	public void setOperador(long operador) {
		this.operador = operador;
	}

	public List<RF2Servicio> getServicios() {
		return servicios;
	}

	public void setServicios(List<RF2Servicio> servicios) {
		this.servicios = servicios;
	}

	public List<RF2Habitacion> getHabitaciones() {
		return habitaciones;
	}

	public void setHabitaciones(List<RF2Habitacion> habitaciones) {
		this.habitaciones = habitaciones;
	}
}