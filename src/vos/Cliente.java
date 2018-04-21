package vos;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.*;

import dao.DAOReserva;

/**
 * @author Nicolás Mateo Hernández Rojas - nm.hernandez10@uniandes.edu.co
 * @author David Felipe Niño Romero - df.nino10@uniandes.edu.co Clase que
 *         representa a los Clientes del modelo AlohAndes
 */

public class Cliente {
	@JsonProperty(value = "id")
	private long id;

	@JsonProperty(value = "documento")
	private long documento;

	@JsonProperty(value = "nombre")
	private String nombre;

	@JsonProperty(value = "edad")
	private int edad;

	@JsonProperty(value = "direccion")
	private String direccion;

	@JsonProperty(value = "reservas")
	private List<Long> reservas;

	@JsonProperty(value = "vinculo")
	private Vinculo vinculo;

	public Cliente(@JsonProperty(value = "id") long id, @JsonProperty(value = "documento") long documento,
			@JsonProperty(value = "nombre") String nombre, @JsonProperty(value = "edad") int edad,
			@JsonProperty(value = "direccion") String direccion, @JsonProperty(value = "vinculo") Vinculo vinculo,
			@JsonProperty(value = "reservas") List<Long> reservas) 
	{
		this.id = id;
		this.documento = documento;
		this.nombre = nombre;
		this.edad = edad;
		this.direccion = direccion;
		this.vinculo = vinculo;
		this.reservas = reservas;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDocumento() {
		return documento;
	}

	public void setDocumento(long documento) {
		this.documento = documento;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getEdad() {
		return edad;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public Vinculo getVinculo() {
		return vinculo;
	}

	public void setVinculo(Vinculo vinculo) {
		this.vinculo = vinculo;
	}

	public List<Long> getReservas() {
		return reservas;
	}

	public void setReservas(List<Long> reservas) {
		this.reservas = reservas;
	}
	
	public boolean reservaHoy(Connection conn, Date fecha) throws SQLException, Exception
	{
		DAOReserva daoReserva = new DAOReserva();
		daoReserva.setConn(conn);
		
		List<Long> reservasId = daoReserva.buscarReservasIdCliente(getId());
		
		List<Reserva> reservas = new ArrayList<Reserva>();
		
		for(long id : reservasId)
		{
			reservas.add(daoReserva.buscarReserva(id));
		}
		
		boolean resHoy = false;
		for (Reserva r : reservas) {
			if (r.getFechaReserva().equals(fecha))
			{
				resHoy = true;
			}
		}
		
		return resHoy;
	}
}
