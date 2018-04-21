package vos;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.*;

import dao.DAOHabitacion;
import dao.DAOReserva;

/**
 * @author Nicolás Mateo Hernández Rojas - nm.hernandez10@uniandes.edu.co
 * @author David Felipe Niño Romero - df.nino10@uniandes.edu.co Clase que
 *         representa a los Espacios del modelo AlohAndes
 */

public class Espacio {
	@JsonProperty(value = "id")
	private long id;

	@JsonProperty(value = "registro")
	private long registro;

	@JsonProperty(value = "capacidad")
	private int capacidad;

	@JsonProperty(value = "tamaño")
	private double tamaño;

	@JsonProperty(value = "direccion")
	private String direccion;

	@JsonProperty(value = "precio")
	private double precio;

	@JsonProperty(value = "fechaRetiro")
	private String fechaRetiro;
	
	private Date fechaRetiroDate;

	@JsonProperty(value = "operador")
	private long operador;

	@JsonProperty(value = "reservas")
	private List<Long> reservas;

	@JsonProperty(value = "servicios")
	private List<Long> servicios;

	@JsonProperty(value = "habitaciones")
	private List<Long> habitaciones;

	public Espacio(@JsonProperty(value = "id") long id, @JsonProperty(value = "registro") long registro,
			@JsonProperty(value = "capacidad") int capacidad, @JsonProperty(value = "tamaño") double tamaño,
			@JsonProperty(value = "direccion") String direccion, @JsonProperty(value = "precio") double precio,
			@JsonProperty(value = "fechaRetiro") String fechaRetiro, @JsonProperty(value = "operador") long operador,
			@JsonProperty(value = "reservas") List<Long> reservas,
			@JsonProperty(value = "servicios") List<Long> servicios,
			@JsonProperty(value = "habitaciones") List<Long> habitaciones) {
		this.id = id;
		this.registro = registro;
		this.capacidad = capacidad;
		this.tamaño = tamaño;
		this.direccion = direccion;
		this.precio = precio;
		this.fechaRetiro = fechaRetiro;		
		this.operador = operador;
		this.reservas = reservas;
		this.servicios = servicios;
		this.habitaciones = habitaciones;
		
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			this.fechaRetiroDate = new Date(format.parse(this.fechaRetiro).getTime());
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}		
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

	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
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

	public void setFechaRetiro(String fechaCancelacion) {
		this.fechaRetiro = fechaCancelacion;
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			this.fechaRetiroDate = new Date(format.parse(this.fechaRetiro).getTime());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public long getOperador() {
		return operador;
	}

	public void setOperador(long operador) {
		this.operador = operador;
	}

	public List<Long> getReservas() {
		return reservas;
	}

	public void setReservas(List<Long> reservas) {
		this.reservas = reservas;
	}

	public List<Long> getServicios() {
		return servicios;
	}

	public void setServicios(List<Long> servicios) {
		this.servicios = servicios;
	}

	public List<Long> getHabitaciones() {
		return habitaciones;
	}

	public void setHabitaciones(List<Long> habitaciones) {
		this.habitaciones = habitaciones;
	}

	public int calcularOcupacionEnFecha(java.util.Date date, Connection conn) throws SQLException, Exception 
	{
		DAOReserva daoReserva = new DAOReserva();
		daoReserva.setConn(conn);
		
		List<Long> reservasId = daoReserva.buscarReservasIdEspacio(getId());
		
		List<Reserva> reservas = new ArrayList<Reserva>();
		
		for(long ido : reservasId)
		{
			reservas.add(daoReserva.buscarReserva(ido));
		}
		
		int ocupacion = 0;
		for (Reserva r : reservas) {
			if (r.getFechaInicioDate().before(date) && (r.getFechaInicioDate().getMonth() * 30 + r.getFechaInicioDate().getDay()
					+ r.getDuracion() <= date.getMonth() * 30 + date.getDay())) {
				ocupacion++;
			}
		}
		return ocupacion;
	}

	public void calcularCapacidad(Connection conn) throws SQLException, Exception 
	{
		DAOHabitacion daoHabitacion = new DAOHabitacion();
		daoHabitacion.setConn(conn);
		
		List<Long> habitacionesId = daoHabitacion.buscarHabitacionesIdEspacio(getId());
		
		List<Habitacion> habitaciones = new ArrayList<Habitacion>();
		
		for(long ido : habitacionesId)
		{
			habitaciones.add(daoHabitacion.buscarHabitacion(ido));
		}
		
		int capa = 0;

		for (Habitacion h : habitaciones) {
			capa += h.getCapacidad();
		}
		this.capacidad = capa;
	}

	public Date getFechaRetiroDate() 
	{
		return fechaRetiroDate;
	}

	public void setFechaRetiroDate(Date fechaRetiroDate) {
		this.fechaRetiroDate = fechaRetiroDate;
		this.fechaRetiro =(this.fechaRetiroDate.getYear() +1900) + "-" + (this.fechaRetiroDate.getMonth() +1) +"-" +  this.fechaRetiroDate.getDate();
	}
}
