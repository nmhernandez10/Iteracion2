package vos;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.codehaus.jackson.annotate.*;

/**
 * @author Nicolás Mateo Hernández Rojas - nm.hernandez10@uniandes.edu.co
 * @author David Felipe Niño Romero - df.nino10@uniandes.edu.co Clase que
 *         representa a las Reservas del modelo AlohAndes
 */

public class Reserva {
	
	@JsonProperty(value = "id")
	private long id;
	
	@JsonProperty(value = "idCliente")
	private long idCliente;

	@JsonProperty(value = "idEspacio")
	private long idEspacio;
	
	@JsonProperty(value = "idColectiva")
	private long idColectiva;

	@JsonProperty(value = "fechaInicio")
	private String fechaInicio;

	private Date fechaInicioDate;
	
	@JsonProperty(value = "duracion")
	private int duracion;

	@JsonProperty(value = "fechaReserva")
	private String fechaReserva;

	private Date fechaReservaDate;
	
	@JsonProperty(value = "cancelado")
	private boolean cancelado;

	@JsonProperty(value = "precio")
	private double precio;

	public Reserva(@JsonProperty(value = "id") long id, @JsonProperty(value = "idCliente") long idCliente, @JsonProperty(value = "idEspacio") long idEspacio,
			@JsonProperty(value = "idColectiva") long idColectiva, @JsonProperty(value = "fechaInicio") String fechaInicio, @JsonProperty(value = "duracion") int duracion,
			@JsonProperty(value = "fechaReserva") String fechaReserva,
			@JsonProperty(value = "cancelado") boolean cancelado, @JsonProperty(value = "precio") double precio) {
		this.id = id;
		this.idCliente = idCliente;
		this.idEspacio = idEspacio;
		this.idColectiva = idColectiva;
		this.fechaInicio = fechaInicio;
		this.duracion = duracion;
		this.fechaReserva = fechaReserva;
		this.precio = precio;
		this.cancelado = cancelado;
		
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			this.fechaInicioDate = new Date(format.parse(this.fechaInicio).getTime());
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			this.fechaReservaDate = new Date(format.parse(this.fechaReserva).getTime());
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

	public long getIdColectiva() {
		return idColectiva;
	}

	public void setIdColectiva(long idColectiva) {
		this.idColectiva = idColectiva;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public long getIdCliente() {
		return idCliente;
	}

	public boolean isCancelado() {
		return cancelado;
	}

	public void setCancelado(boolean cancelado) {
		this.cancelado = cancelado;
	}

	public void setIdCliente(long idCliente) {
		this.idCliente = idCliente;
	}

	public String getFechaReserva() {
		return fechaReserva;
	}

	public void setFechaReserva(String fechaReserva) {
		this.fechaReserva = fechaReserva;
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			this.fechaReservaDate = new Date(format.parse(this.fechaReserva).getTime());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public long getIdEspacio() {
		return idEspacio;
	}

	public void setIdEspacio(long idEspacio) {
		this.idEspacio = idEspacio;
	}

	public String getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(String fechaInicio) {
		this.fechaInicio = fechaInicio;
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			this.fechaInicioDate = new Date(format.parse(this.fechaInicio).getTime());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	public Date calcularFechaFin() 
	{
		if (duracion==0)
		{
			return fechaInicioDate;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaInicioDate); 
		calendar.add(Calendar.DAY_OF_YEAR, duracion);  
		return calendar.getTime(); 
	}

	public Date calcularFechaConDiasDespues(int diasMas) {
		if (diasMas==0)
		{
			return fechaInicioDate;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaInicioDate); 
		calendar.add(Calendar.DAY_OF_YEAR, diasMas);  
		return calendar.getTime(); 
	}

	public Date getFechaInicioDate() {
		return fechaInicioDate;
	}

	public void setFechaInicioDate(Date fechaInicioDate) {
		this.fechaInicioDate = fechaInicioDate;
		this.fechaInicio =  (this.fechaInicioDate.getYear() +1900) + "-" + (this.fechaInicioDate.getMonth() +1) +"-" + this.fechaInicioDate.getDate();
	}

	public Date getFechaReservaDate() {
		return fechaReservaDate;
	}

	public void setFechaReservaDate(Date fechaReservaDate) {
		this.fechaReservaDate = fechaReservaDate;
		this.fechaReserva =  (this.fechaReservaDate.getYear() +1900) + "-" + (this.fechaReservaDate.getMonth() +1) +"-" + this.fechaReservaDate.getDate();
	}	
	
	public boolean isVigente(Date fechaInicioAnalizada, Date fechaFinalAnalizada)
	{
		boolean rpta = false;
		
		if(!isCancelado() && getFechaInicioDate().before(fechaFinalAnalizada) && calcularFechaFin().after(fechaInicioAnalizada))
		{
			rpta = true;
		}
		
		return rpta;
	}
}
