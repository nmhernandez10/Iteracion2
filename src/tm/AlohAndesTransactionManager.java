package tm;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import dao.DAOCategoriaOperador;
import dao.DAOCliente;
import dao.DAOEspacio;
import dao.DAOOperador;
import dao.DAOReserva;
import vos.Cliente;
import vos.Espacio;
import vos.Operador;
import vos.RFC1;
import vos.RFC3;
import vos.RFC4;
import vos.RFC5;
import vos.RFC6;
import vos.RFC7;
import vos.Reserva;

public class AlohAndesTransactionManager 
{
	private static final String CONNECTION_DATA_FILE_NAME_REMOTE = "/conexion.properties";

	private String connectionDataPath;

	private String user;

	private String password;

	private String url;

	private String driver;

	private Connection conn;

	public AlohAndesTransactionManager(String contextPathP) {
		connectionDataPath = contextPathP + CONNECTION_DATA_FILE_NAME_REMOTE;
		initConnectionData();
	}

	private void initConnectionData() {
		try {
			File arch = new File(this.connectionDataPath);
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(arch);
			prop.load(in);
			in.close();
			this.url = prop.getProperty("url");
			this.user = prop.getProperty("usuario");
			this.password = prop.getProperty("clave");
			this.driver = prop.getProperty("driver");
			Class.forName(driver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Connection darConexion() throws SQLException {
		System.out.println("Connecting to: " + url + " With user: " + user);
		return DriverManager.getConnection(url, user, password);
	}

	// RF4

	public void addReserva(Reserva reserva) throws Exception 
	{
		DAOReserva daoReserva = new DAOReserva();
		DAOCliente daoCliente = new DAOCliente();
		DAOEspacio daoEspacio = new DAOEspacio();
		DAOOperador daoOperador = new DAOOperador();

		try {
			////// Transacción
			this.conn = darConexion();
			daoReserva.setConn(conn);
			daoCliente.setConn(conn);
			daoEspacio.setConn(conn);
			daoOperador.setConn(conn);

			Cliente cliente = null;
			Espacio espacio = null;
			try {
				cliente = daoCliente.buscarCliente(reserva.getIdCliente());
				espacio = daoEspacio.buscarEspacio(reserva.getIdEspacio());
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				throw e;
			}		
			
			if(reserva.getDuracion() <= 0)
			{
				throw new Exception ("La duración tiene que ser entera y positiva para representar los días de la reserva");
			}
			
			reserva.setFechaReservaDate(new Date());
			Date fecha = reserva.getFechaReservaDate();
			
			if (fecha.after(reserva.getFechaInicioDate())) {
				throw new Exception("La reserva debe iniciar después que la fecha actual");
			}

			if (daoOperador.buscarOperador(espacio.getOperador()).getCategoria().getCategoria().toUpperCase().equals("MIEMBRO_DE_LA_COMUNIDAD")
					|| daoOperador.buscarOperador(espacio.getOperador()).getCategoria().getCategoria().toUpperCase().equals("PERSONA_NATURAL")) {
				if (reserva.getDuracion() <= 30) {
					throw new Exception(
							"La reserva tiene que durar mínimo 30 días si se quiere reservar un espacio de ese operador");
				}
			}

			if (espacio.getCapacidad() < espacio.calcularOcupacionEnFecha(reserva.getFechaInicioDate(), conn) + 1) {
				throw new Exception("La nueva reserva excediría la capacidad del espacio a reservar");
			}

			if (daoOperador.buscarOperador(espacio.getOperador()).getCategoria().getCategoria().toUpperCase().equals("VIVIENDA_UNIVERSITARIA")
					&& (cliente.getVinculo().getVinculo().toUpperCase().equals("ESTUDIANTE") || cliente.getVinculo().getVinculo().toUpperCase().equals("PROFESOR")
							|| cliente.getVinculo().getVinculo().toUpperCase().equals("EMPLEADO")
							|| cliente.getVinculo().getVinculo().toUpperCase().equals("PROFESOR_INVITADO"))) {
				throw new Exception("Sólo estudiantes, profesores y empleados pueden usar vivienda universitaria");
			}
			
			//Verifico franjas permitidas
			
			List<Long> reservasId = daoReserva.buscarReservasIdCliente(reserva.getIdCliente());
			
			for(long resId : reservasId)
			{
				Reserva res = daoReserva.buscarReserva(resId);
				if(res.getFechaInicioDate().before(reserva.calcularFechaFin()) && res.calcularFechaFin().after(reserva.getFechaInicioDate()))
				{
					throw new Exception ("El cliente tiene ya reservas en estas fechas");
				}
			}
			
			reservasId = daoReserva.buscarReservasIdEspacio(reserva.getIdEspacio());
			
			for(long resId : reservasId)
			{
				Reserva res = daoReserva.buscarReserva(resId);
				if(res.getFechaInicioDate().before(reserva.calcularFechaFin()) && res.calcularFechaFin().after(reserva.getFechaInicioDate()))
				{
					throw new Exception ("El espacio tiene ya reservas en estas fechas");
				}
			}
			
			if (cliente.reservaHoy(conn, fecha)) {
				throw new Exception("No puede hacerse más de una reserva al día");
			}

			if (espacio.getFechaRetiro() != null && reserva.calcularFechaFin().after(espacio.getFechaRetiroDate())) {
				throw new Exception(
						"No se puede reservar con esta duración y fecha de inicio porque el espacio se retira antes de finalizar la reserva");
			}

			daoReserva.addReserva(reserva);
			conn.commit();
		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			System.err.println("GeneralException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			try {
				daoCliente.cerrarRecursos();
				if (this.conn != null)
					this.conn.close();
			} catch (SQLException exception) {
				System.err.println("SQLException closing resources:" + exception.getMessage());
				exception.printStackTrace();
				throw exception;
			}
		}
	}

	// RF5

	public Reserva cancelarReserva(Reserva reserva) throws Exception {
		DAOReserva daoReserva = new DAOReserva();
		DAOCliente daoCliente = new DAOCliente();
		DAOEspacio daoEspacio = new DAOEspacio();

		try {
			////// Transacción
			this.conn = darConexion();
			daoReserva.setConn(conn);
			daoCliente.setConn(conn);
			daoEspacio.setConn(conn);
			
			reserva = daoReserva.buscarReserva(reserva.getId());
			
			if (reserva.isCancelado())
			{
				throw new Exception("La reserva ya está cancelada");
			}

			Date fechaCancelacion = new Date();

			if (reserva.getDuracion() < 7 && fechaCancelacion.before(reserva.calcularFechaConDiasDespues(4))) {
				reserva.setCancelado(true);
				reserva.setPrecio(reserva.getPrecio() * 0.1);
			}

			if (reserva.getDuracion() < 7 && fechaCancelacion.after(reserva.calcularFechaConDiasDespues(3))) {
				reserva.setCancelado(true);
				reserva.setPrecio(reserva.getPrecio() * 0.3);
			}

			if (reserva.getDuracion() >= 7 && fechaCancelacion.before(reserva.calcularFechaConDiasDespues(8))) {
				reserva.setCancelado(true);
				reserva.setPrecio(reserva.getPrecio() * 0.1);
			}

			if (reserva.getDuracion() >= 7 && fechaCancelacion.after(reserva.calcularFechaConDiasDespues(7))) {
				reserva.setCancelado(true);
				reserva.setPrecio(reserva.getPrecio() * 0.3);
			}

			daoReserva.updateReserva(reserva);
			
			conn.commit();
			
			return reserva;
		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			System.err.println("GeneralException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			try {
				daoReserva.cerrarRecursos();
				if (this.conn != null)
					this.conn.close();
			} catch (SQLException exception) {
				System.err.println("SQLException closing resources:" + exception.getMessage());
				exception.printStackTrace();
				throw exception;
			}
		}
	}

	// RF6

	public Espacio cancelarEspacio(Espacio espacio, Date fechaCancelacion) throws Exception {
		DAOReserva daoReserva = new DAOReserva();
		DAOOperador daoOperador = new DAOOperador();
		DAOEspacio daoEspacio = new DAOEspacio();

		try {
			////// Transacción
			this.conn = darConexion();
			daoReserva.setConn(conn);
			daoOperador.setConn(conn);
			daoEspacio.setConn(conn);
			
			if(fechaCancelacion == null)
			{
				fechaCancelacion = new Date();
			}
			
			Operador operador = null;
			List<Reserva> reservas = new ArrayList<Reserva>();

			try {
				espacio = daoEspacio.buscarEspacio(espacio.getId());
			} catch (Exception e) {
				throw new Exception("No hay espacio con dicho id para poder cancelarlo.");
			}
			try {
				operador = daoOperador.buscarOperador(espacio.getOperador());
				List<Long> reservasId = daoReserva.buscarReservasIdEspacio(espacio.getId());
				for(long id : reservasId)
				{
					reservas.add(daoReserva.buscarReserva(id));
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				throw e;
			}

			for (Reserva r : reservas) {
				if (r.calcularFechaFin().after(fechaCancelacion)) {
					throw new Exception(
							"Hay reservas hechas en el espacio que culminan después de la cancelación propuesta. Asegúrese que no se está comprometido.");
				}
			}		
			
			espacio.setFechaRetiroDate(fechaCancelacion);
			
			daoEspacio.updateEspacio(espacio);
			
			conn.commit();
			
			return espacio;
		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			System.err.println("GeneralException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			try {
				daoReserva.cerrarRecursos();
				if (this.conn != null)
					this.conn.close();
			} catch (SQLException exception) {
				System.err.println("SQLException closing resources:" + exception.getMessage());
				exception.printStackTrace();
				throw exception;
			}
		}

	}

	// RFC1

	public List<RFC1> ingresosOperadores() throws Exception {
		DAOOperador daoOperador = new DAOOperador();

		List<RFC1> resultado = new ArrayList<RFC1>();
		try {
			this.conn = darConexion();
			daoOperador.setConn(conn);

			resultado = daoOperador.obtenerIngresosOperadores();

			return resultado;
		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			System.err.println("GeneralException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			try {
				daoOperador.cerrarRecursos();
				if (this.conn != null)
					this.conn.close();
			} catch (SQLException exception) {
				System.err.println("SQLException closing resources:" + exception.getMessage());
				exception.printStackTrace();
				throw exception;
			}
		}
	}

	// RFC2

	public List<Espacio> espaciosPopulares() throws Exception {
		DAOEspacio daoEspacio = new DAOEspacio();

		List<Espacio> resultado = new ArrayList<Espacio>();
		try {
			this.conn = darConexion();
			daoEspacio.setConn(conn);

			resultado = daoEspacio.obtenerEspaciosPopulares();

			return resultado;
		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			System.err.println("GeneralException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			try {
				daoEspacio.cerrarRecursos();
				if (this.conn != null)
					this.conn.close();
			} catch (SQLException exception) {
				System.err.println("SQLException closing resources:" + exception.getMessage());
				exception.printStackTrace();
				throw exception;
			}
		}
	}
	
	//RFC3
	
	public List<RFC3> ocupacionOperadores() throws Exception {
		DAOOperador daoOperador = new DAOOperador();

		List<RFC3> resultado = new ArrayList<RFC3>();
		try {
			this.conn = darConexion();
			daoOperador.setConn(conn);

			resultado = daoOperador.obtenerOcupacionOperadores();

			return resultado;
		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			System.err.println("GeneralException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			try {
				daoOperador.cerrarRecursos();
				if (this.conn != null)
					this.conn.close();
			} catch (SQLException exception) {
				System.err.println("SQLException closing resources:" + exception.getMessage());
				exception.printStackTrace();
				throw exception;
			}
		}
	}	
	
	//RFC4
	
	public List<Espacio> espaciosDisponibles(RFC4 rfc4) throws Exception {
		DAOEspacio daoEspacio = new DAOEspacio();

		List<Espacio> resultado = new ArrayList<Espacio>();
		try {
			this.conn = darConexion();
			daoEspacio.setConn(conn);

			resultado = daoEspacio.obtenerEspaciosDisponibles(rfc4);

			return resultado;
		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			System.err.println("GeneralException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			try {
				daoEspacio.cerrarRecursos();
				if (this.conn != null)
					this.conn.close();
			} catch (SQLException exception) {
				System.err.println("SQLException closing resources:" + exception.getMessage());
				exception.printStackTrace();
				throw exception;
			}
		}

	}	
	
	//RFC5
	
	public List<RFC5> usosPorCategoria() throws Exception {
		DAOOperador daoOperador = new DAOOperador();
		DAOCliente daoCliente = new DAOCliente();

		List<RFC5> resultado = new ArrayList<RFC5>();
		try {
			this.conn = darConexion();
			daoCliente.setConn(conn);
			daoOperador.setConn(conn);
			
			daoCliente.obtenerUsosPorCategoria(resultado);
			daoOperador.obtenerUsosPorCategoria(resultado);

			return resultado;
		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			System.err.println("GeneralException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			try {
				daoOperador.cerrarRecursos();
				if (this.conn != null)
					this.conn.close();
			} catch (SQLException exception) {
				System.err.println("SQLException closing resources:" + exception.getMessage());
				exception.printStackTrace();
				throw exception;
			}
		}
	}
	
	//RFC6
	
	public RFC6 usoPorUsuario(long id, String tipo) throws Exception {
		DAOOperador daoOperador = new DAOOperador();
		DAOCliente daoCliente = new DAOCliente();

		RFC6 resultado;
		try {
			this.conn = darConexion();
			daoCliente.setConn(conn);
			daoOperador.setConn(conn);
			
			if(!tipo.equals("cliente") && !tipo.equals("operador"))
			{
				throw new Exception("El servicio sólo es apto para 'cliente' u 'operador', revise que X tiene alguno de esos valores en usoUsuario/X/idUsuario");
			}
			else if(tipo.equals("cliente"))
			{
				resultado = daoCliente.obtenerUsoPorUsuario(id);
			}
			else
			{
				resultado = daoOperador.obtenerUsoPorUsuario(id);
			}		

			return resultado;
		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			System.err.println("GeneralException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			try {
				daoOperador.cerrarRecursos();
				if (this.conn != null)
					this.conn.close();
			} catch (SQLException exception) {
				System.err.println("SQLException closing resources:" + exception.getMessage());
				exception.printStackTrace();
				throw exception;
			}
		}
	}
	
	//RFC7
	public List<Espacio> analizarOperacion(RFC7 rfc7) throws Exception {
		DAOOperador daoOperador = new DAOOperador();
		DAOReserva daoReserva = new DAOReserva();

		RFC7 resultado = rfc7;
		try {
			this.conn = darConexion();
			daoOperador.setConn(conn);
			daoReserva.setConn(conn);

			resultado = daoOperador.obtenerEspaciosDisponibles(rfc7);

			return resultado;
		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			System.err.println("GeneralException:" + e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			try {
				daoEspacio.cerrarRecursos();
				if (this.conn != null)
					this.conn.close();
			} catch (SQLException exception) {
				System.err.println("SQLException closing resources:" + exception.getMessage());
				exception.printStackTrace();
				throw exception;
			}
		}

	}	
}
