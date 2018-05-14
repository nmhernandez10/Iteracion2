package tm;

import java.io.File;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import dao.DAOCategoriaHabitacion;
import dao.DAOCategoriaOperador;
import dao.DAOCategoriaServicio;
import dao.DAOCliente;
import dao.DAOEspacio;
import dao.DAOHabitacion;
import dao.DAOOperador;
import dao.DAOReserva;
import dao.DAOServicio;
import dao.DAOVinculo;
import vos.CategoriaHabitacion;
import vos.CategoriaOperador;
import vos.CategoriaServicio;
import vos.Cliente;
import vos.Espacio;
import vos.Habitacion;
import vos.ListaRFC8;
import vos.ListaRFC9;
import vos.ListaReservas;
import vos.Operador;
import vos.RF1;
import vos.RF2;
import vos.RF2Habitacion;
import vos.RF2Servicio;
import vos.RF3;
import vos.RF7;
import vos.RF9;
import vos.RFC1;
import vos.RFC3;
import vos.RFC4;
import vos.RFC5;
import vos.RFC6;
import vos.RFC7;
import vos.RFC8;
import vos.RFC9;
import vos.Reserva;
import vos.Servicio;

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
	
	// RF1

	public Operador addOperador(RF1 rf1) throws Exception
	{
		DAOOperador daoOperador = new DAOOperador();
		DAOCategoriaOperador daoCatOperador = new DAOCategoriaOperador();
		try
		{
			this.conn = darConexion();
			daoOperador.setConn(conn);
			daoCatOperador.setConn(conn);
			
			CategoriaOperador catOperador = daoCatOperador.buscarCategoriaOperadorNombre(rf1.getCategoria());
			
			if((catOperador.getNombre().toUpperCase().equals("PERSONA_NATURAL") || catOperador.getNombre().toUpperCase().equals("MIEMBRO_DE_LA_COMUNIDAD") || catOperador.getNombre().toUpperCase().equals("VECINO")) && rf1.getRegistro() != 0)
			{
				throw new Exception ("Este tipo de operador no requiere registro. Déjelo vacío");
			}
			
			if((catOperador.getNombre().toUpperCase().equals("PERSONA_NATURAL") || catOperador.getNombre().toUpperCase().equals("MIEMBRO_DE_LA_COMUNIDAD") || catOperador.getNombre().toUpperCase().equals("VECINO")) && rf1.getDocumento() == 0)
			{
				throw new Exception ("Este tipo de operador requiere un documento");
			}
			
			if((catOperador.getNombre().toUpperCase().equals("HOTEL") || catOperador.getNombre().toUpperCase().equals("HOSTAL") || catOperador.getNombre().toUpperCase().equals("VIVIENDA_UNIVERSITARIA")) && rf1.getRegistro() == 0)
			{
				throw new Exception ("Este tipo de operador requiere registro");
			}
			
			Operador operador = new Operador(rf1.getId(), rf1.getRegistro(), rf1.getNombre(), catOperador , new ArrayList<Long>(), rf1.getDocumento() );
			
			daoOperador.addOperador(operador);

			conn.commit();

			return operador;
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
				daoCatOperador.cerrarRecursos();
				if (this.conn != null)
					this.conn.close();
			} catch (SQLException exception) {
				System.err.println("SQLException closing resources:" + exception.getMessage());
				exception.printStackTrace();
				throw exception;
			}
		}
	}

	// RF2

	public Espacio addEspacio(RF2 rf2) throws Exception
	{
		DAOEspacio daoEspacio = new DAOEspacio();
		DAOHabitacion daoHabitacion = new DAOHabitacion();
		DAOCategoriaHabitacion daoCatHabitacion = new DAOCategoriaHabitacion();
		DAOOperador daoOperador = new DAOOperador();
		DAOServicio daoServicio = new DAOServicio();
		DAOCategoriaServicio daoCatServicio = new DAOCategoriaServicio();
		try
		{
			this.conn = darConexion();
			daoEspacio.setConn(conn);
			daoCatHabitacion.setConn(conn);
			daoHabitacion.setConn(conn);
			daoOperador.setConn(conn);
			daoServicio.setConn(conn);
			daoCatServicio.setConn(conn);
			
			if(rf2.getTamaño() <= 0)
			{
				throw new Exception("No puede haber un espacio con tamaño menor o igual a 0");
			}
			
			if(transformarFecha(rf2.getFechaRetiro()).before(new Date()))
			{
				throw new Exception("No puede agregar un espacio con fecha de retiro menor a la actual");
			}
			
			if(rf2.getHabitaciones().size() == 0)
			{
				throw new Exception("No puede agregar un espacio sin habitaciones");
			}
			
			List<Habitacion> habitacionesHab = new ArrayList<Habitacion>();

			List<Long> habitaciones = new ArrayList<Long>();
			
			List<Habitacion> habitacionesYaExistentes = daoHabitacion.darHabitaciones();
			
			long idMayor = 0;
			
			for(Habitacion habExistente : habitacionesYaExistentes)
			{
				if(habExistente.getId() > idMayor)
				{
					idMayor = habExistente.getId();
				}
			}
			
			int capacidad = 0;
			
			for(RF2Habitacion rf2h : rf2.getHabitaciones())
			{
				idMayor ++;
				CategoriaHabitacion catHabitacion = daoCatHabitacion.buscarCategoriaHabitacionNombre(rf2h.getCategoria());
				
				if(!catHabitacion.getNombre().toUpperCase().equals("ESTÁNDAR") && !daoOperador.buscarOperador(rf2.getOperador()).getCategoria().getNombre().toUpperCase().equals("HOTEL") && !daoOperador.buscarOperador(rf2.getOperador()).getCategoria().getNombre().toUpperCase().equals("HOSTAL"))
				{
					throw new Exception("No se puede asignar habitaciones SUITE o SEMISUITE a espacios de alojamiento de un operador que no sea hotel u hostal");
				}
				
				habitacionesHab.add(new Habitacion(idMayor, catHabitacion, rf2h.isCompartido(), rf2h.getCapacidad(), rf2.getId()));
				habitaciones.add(idMayor);
				capacidad += rf2h.getCapacidad();
			}	
			
			idMayor = 0;
			
			List<Servicio> serviciosServ = new ArrayList<Servicio>();
			
			List<Servicio> serviciosYaExistentes = daoServicio.darServicios();
			
			List<Long> servicios = new ArrayList<Long>();
			
			for(Servicio servExistente : serviciosYaExistentes)
			{
				if(servExistente.getId() > idMayor)
				{
					idMayor = servExistente.getId();
				}
			}
			
			int precio = 0;
			
			for(RF2Servicio rf2s : rf2.getServicios())
			{
				idMayor ++;
				CategoriaServicio catServ = daoCatServicio.buscarCategoriaServicioNombre(rf2s.getCategoria());
				serviciosServ.add(new Servicio(idMayor, catServ, rf2s.getDescripcion(), rf2s.getPrecioAdicional(), rf2s.getInicioHorario(), rf2s.getFinHorario(), rf2.getId()));
				servicios.add(idMayor);
				precio += rf2s.getPrecioAdicional();
			}
			
			if(rf2.getFechaRetiro() == null || rf2.getFechaRetiro().equals(""))
			{
				rf2.setFechaRetiro("2999-12-31");
			}
			
			Espacio espacio = new Espacio(rf2.getId(), rf2.getRegistro(),capacidad, rf2.getTamaño() , rf2.getDireccion(), rf2.getPrecio()+precio, rf2.getFechaRetiro(), rf2.getOperador(), new ArrayList<Long>(), servicios, habitaciones);
			
			conn.setAutoCommit(false);
			
			try
			{
				daoEspacio.addEspacio(espacio);
				for(Habitacion hab : habitacionesHab)
				{
					daoHabitacion.addHabitacion(hab);
				}
				for(Servicio serv : serviciosServ)
				{
					daoServicio.addServicio(serv);
				}
			}
			catch(Exception e)
			{
				conn.rollback();
				conn.setAutoCommit(true);
				throw e;
			}

			conn.commit();
			
			conn.setAutoCommit(true);
			
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
				daoEspacio.cerrarRecursos();
				daoCatHabitacion.cerrarRecursos();
				daoHabitacion.cerrarRecursos();
				daoOperador.cerrarRecursos();
				daoServicio.cerrarRecursos();
				daoCatServicio.cerrarRecursos();
				if (this.conn != null)
					this.conn.close();
			} catch (SQLException exception) {
				System.err.println("SQLException closing resources:" + exception.getMessage());
				exception.printStackTrace();
				throw exception;
			}
		}
	}
	
	// RF3
	
	public Cliente addCliente(RF3 rf3) throws Exception
	{
		DAOCliente daoCliente = new DAOCliente();
		DAOVinculo daoVinculo = new DAOVinculo();
		try
		{
			this.conn = darConexion();
			daoCliente.setConn(conn);
			daoVinculo.setConn(conn);
			
			Cliente cliente = new Cliente(rf3.getId(), rf3.getIdentificacion(), rf3.getNombre(), rf3.getEdad(), rf3.getDireccion(), daoVinculo.buscarVinculoNombre(rf3.getVinculo()), new ArrayList<Long>() );
			
			daoCliente.addCliente(cliente);
			
			conn.commit();
			
			return cliente;
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
				daoVinculo.cerrarRecursos();
				if (this.conn != null)
					this.conn.close();
			} catch (SQLException exception) {
				System.err.println("SQLException closing resources:" + exception.getMessage());
				exception.printStackTrace();
				throw exception;
			}
		}
	}
	
	// RF4

	public void addReserva(Reserva reserva, boolean desdeRF7) throws Exception 
	{
		DAOReserva daoReserva = new DAOReserva();
		DAOCliente daoCliente = new DAOCliente();
		DAOEspacio daoEspacio = new DAOEspacio();
		DAOOperador daoOperador = new DAOOperador();

		try {
			////// Transacción
			if(!desdeRF7)
			{
				this.conn = darConexion();
			}
			
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
			
			if(reserva.isCancelado())
			{
				throw new Exception("No puede agregar una nueva reserva como cancelada");
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

			if (daoOperador.buscarOperador(espacio.getOperador()).getCategoria().getNombre().toUpperCase().equals("MIEMBRO_DE_LA_COMUNIDAD")
					|| daoOperador.buscarOperador(espacio.getOperador()).getCategoria().getNombre().toUpperCase().equals("PERSONA_NATURAL")) {
				if (reserva.getDuracion() <= 30) {
					throw new Exception(
							"La reserva tiene que durar mínimo 30 días si se quiere reservar un espacio de ese operador");
				}
			}

			if (daoOperador.buscarOperador(espacio.getOperador()).getCategoria().getNombre().toUpperCase().equals("VIVIENDA_UNIVERSITARIA")
					&& (cliente.getVinculo().getNombre().toUpperCase().equals("ESTUDIANTE") || cliente.getVinculo().getNombre().toUpperCase().equals("PROFESOR")
							|| cliente.getVinculo().getNombre().toUpperCase().equals("EMPLEADO")
							|| cliente.getVinculo().getNombre().toUpperCase().equals("PROFESOR_INVITADO"))) {
				throw new Exception("Sólo estudiantes, profesores y empleados pueden usar vivienda universitaria");
			}
			
			//Verifico franjas permitidas
			
			List<Long> reservasId = daoReserva.buscarReservasIdCliente(reserva.getIdCliente());
			
			if(!desdeRF7)
			{
				for(long resId : reservasId)
				{
					Reserva res = daoReserva.buscarReserva(resId);
					if(!res.isCancelado() && res.getFechaInicioDate().before(reserva.calcularFechaFin()) && res.calcularFechaFin().after(reserva.getFechaInicioDate()))
					{
						throw new Exception ("El cliente tiene ya reservas en estas fechas");
					}
				}
			}			
			
			reservasId = daoReserva.buscarReservasIdEspacio(reserva.getIdEspacio());
			
			for(long resId : reservasId)
			{
				Reserva res = daoReserva.buscarReserva(resId);
				if(!res.isCancelado() && res.getFechaInicioDate().before(reserva.calcularFechaFin()) && res.calcularFechaFin().after(reserva.getFechaInicioDate()))
				{
					throw new Exception ("El espacio tiene ya reservas en estas fechas");
				}
			}
			
			if (cliente.reservaHoy(conn, fecha)) {
				throw new Exception("No puede hacerse más de una reserva al día");
			}
			
			if(espacio.getFechaRetiro()!= null)
			{
				if (reserva.calcularFechaFin().after(espacio.getFechaRetiroDate())) {
					throw new Exception(
							"No se puede reservar con esta duración y fecha de inicio porque el espacio se retira antes de finalizar la reserva");
				}
			}			

			daoReserva.addReserva(reserva);
			
			if(!desdeRF7)
			{
				conn.commit();
			}
			
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
				daoReserva.cerrarRecursos();
				daoOperador.cerrarRecursos();
				daoEspacio.cerrarRecursos();
				if (this.conn != null && !desdeRF7)
					this.conn.close();
			} catch (SQLException exception) {
				System.err.println("SQLException closing resources:" + exception.getMessage());
				exception.printStackTrace();
				throw exception;
			}
		}
	}

	// RF5

	public Reserva cancelarReserva(Reserva reserva, boolean desdeRF9, boolean desdeRF8) throws Exception {
		DAOReserva daoReserva = new DAOReserva();
		DAOCliente daoCliente = new DAOCliente();
		DAOEspacio daoEspacio = new DAOEspacio();

		try {
			////// Transacción
			if(!desdeRF9 && !desdeRF8)
			{
				this.conn = darConexion();
			}			
			daoReserva.setConn(conn);
			daoCliente.setConn(conn);
			daoEspacio.setConn(conn);

			reserva = daoReserva.buscarReserva(reserva.getId());

			if (reserva.isCancelado())
			{
				throw new Exception("La reserva no puede cancelarse porque ya estaba cancelada.");
			}
			
			if(reserva.calcularFechaFin().before(new Date()))
			{
				throw new Exception("No se puede cancelar una reserva que ya culminó");
			}

			Date fechaCancelacion = new Date();
			
			if (reserva.getFechaInicioDate().before(fechaCancelacion))
			{
				reserva.setCancelado(true);
				reserva.setPrecio(reserva.getPrecio() * 0.5);
			}
			else 
			{
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
			}			
			
			if(desdeRF9)
			{
				reserva.setCancelado(true);
				reserva.setPrecio(0);
			}
			
			daoReserva.updateReserva(reserva);
			
			if(!desdeRF9 && !desdeRF8)
			{
				conn.commit();
			}				

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
				daoCliente.cerrarRecursos();
				daoEspacio.cerrarRecursos();
				if (this.conn != null && !desdeRF9 && !desdeRF8)
					this.conn.close();
			} catch (SQLException exception) {
				System.err.println("SQLException closing resources:" + exception.getMessage());
				exception.printStackTrace();
				throw exception;
			}
		}
	}

	// RF6

	public Espacio cancelarEspacio(Espacio espacio, Date fechaCancelacion, boolean desdeRF9) throws Exception {
		DAOReserva daoReserva = new DAOReserva();
		DAOOperador daoOperador = new DAOOperador();
		DAOEspacio daoEspacio = new DAOEspacio();

		try {
			////// Transacción
			if(!desdeRF9)
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
			
			if(espacio.getFechaRetiroDate().before(fechaCancelacion))
			{
				throw new Exception("No puede cancelar un espacio que ya está cancelado");
			}
			
			for (Reserva r : reservas) {
				if (r.calcularFechaFin().after(fechaCancelacion) && !r.isCancelado()) {
					throw new Exception(
							"Hay reservas hechas en el espacio que culminan después de la cancelación propuesta. Asegúrese que no se está comprometido.");
				}
			}		

			espacio.setFechaRetiroDate(fechaCancelacion);

			daoEspacio.updateEspacio(espacio);
			
			if(!desdeRF9)
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
				daoOperador.cerrarRecursos();
				daoEspacio.cerrarRecursos();
				if (this.conn != null && !desdeRF9)
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

	// RFC3
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
	
	// RFC4
	
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
	
	// RFC5
	
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
	
	// RFC6
	
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

	// RFC7
	
	public List<String> analizarOperacion(RFC7 rfc7) throws Exception {
		DAOOperador daoOperador = new DAOOperador();
		DAOReserva daoReserva = new DAOReserva();
		DAOCategoriaOperador daoCatOperador = new DAOCategoriaOperador();
		DAOEspacio daoEspacio = new DAOEspacio();
		
		try {
			this.conn = darConexion();
			daoOperador.setConn(conn);
			daoReserva.setConn(conn);
			daoCatOperador.setConn(conn);
			daoEspacio.setConn(conn);
			
			if(!rfc7.getTimeUnit().equalsIgnoreCase("DÍA") && !rfc7.getTimeUnit().equalsIgnoreCase("SEMANA") && !rfc7.getTimeUnit().equalsIgnoreCase("MES") && !rfc7.getTimeUnit().equalsIgnoreCase("AÑO") )
			{
				throw new Exception("Ingrese una unidad de tiempo válida entre: día, mes, semana y año");
			}
			
			long idCatOperador = 0;			
			
			idCatOperador = daoCatOperador.buscarCategoriaOperadorNombre(rfc7.getCategoria()).getId();			
			
			List<Reserva> analizadas = daoReserva.buscarReservasIdCategoriaOperador(idCatOperador);
			
			List<String> resultados = new ArrayList<String>();
			
			Date mejorFechaDemanda = new Date();
			Date mejorFechaIngresos = new Date();
			Date mejorFechaOcupacion = new Date();
			
			double mejorValorDemanda = 0;
			double mejorValorIngresos = 0;
			double menorOcupacion = 1;
			
			Date fechaMenor = new Date();
			Date fechaMayor = new Date();
			
			int numEspaciosOperador = daoEspacio.buscarEspaciosIdCategoriaOperador(idCatOperador).size();
			
			for(Reserva reserva : analizadas)
			{
				if(reserva.getFechaInicioDate().before(fechaMenor) && !reserva.isCancelado())
				{
					fechaMenor = agregarMes(reserva.getFechaInicioDate(), -reserva.getFechaInicioDate().getMonth()) ;
					fechaMenor = agregarDía(fechaMenor, -(reserva.getFechaInicioDate().getDate() -1)) ;
				}
				
				if(reserva.calcularFechaFin().after(fechaMayor) && !reserva.isCancelado())
				{
					fechaMayor = agregarMes(reserva.getFechaInicioDate(), 13 - reserva.getFechaInicioDate().getMonth()) ;
					fechaMayor = agregarDía(fechaMayor, (31-reserva.getFechaInicioDate().getDate())) ;
				}
			}		
			
			while(fechaMenor.before(fechaMayor))
			{
				double puntajeDemanda = 0;
				int puntajeIngresos = 0;
				
				Date fechaMenorPlazo = new Date();
				
				if(rfc7.getTimeUnit().equalsIgnoreCase("DÍA"))
				{
					fechaMenorPlazo = agregarDía(fechaMenor, 1);
				}
				else if(rfc7.getTimeUnit().equalsIgnoreCase("SEMANA"))
				{
					fechaMenorPlazo = agregarSemana(fechaMenor, 1);
				}
				else if(rfc7.getTimeUnit().equalsIgnoreCase("MES"))
				{
					fechaMenorPlazo = agregarMes(fechaMenor, 1);
				}
				else if(rfc7.getTimeUnit().equalsIgnoreCase("AÑO"))
				{
					fechaMenorPlazo = agregarAño(fechaMenor, 1);
				}
				
				for(Reserva reserva : analizadas)
				{
					
					if(reserva.isVigente(fechaMenor, fechaMenorPlazo))
					{
						puntajeDemanda++;
						puntajeIngresos += reserva.getPrecio();
					}
				}

				if(puntajeDemanda > mejorValorDemanda)
				{
					mejorValorDemanda = puntajeDemanda;
					mejorFechaDemanda = fechaMenor;
				}
				if(puntajeIngresos > mejorValorIngresos)
				{
					mejorValorIngresos = puntajeIngresos;
					mejorFechaIngresos = fechaMenor;
				}
				if(puntajeDemanda > 0)
				{
					if((puntajeDemanda/numEspaciosOperador) < menorOcupacion)
					{
						menorOcupacion = puntajeDemanda/numEspaciosOperador;
						mejorFechaOcupacion = fechaMenor;
					}
				}				
				
				if(rfc7.getTimeUnit().equalsIgnoreCase("DÍA"))
				{
					fechaMenor = agregarDía(fechaMenor, 1);
				}
				else if(rfc7.getTimeUnit().equalsIgnoreCase("SEMANA"))
				{
					fechaMenor = agregarSemana(fechaMenor, 1);
				}
				else if(rfc7.getTimeUnit().equalsIgnoreCase("MES"))
				{
					fechaMenor = agregarMes(fechaMenor, 1);
				}
				else if(rfc7.getTimeUnit().equalsIgnoreCase("AÑO"))
				{
					fechaMenor = agregarAño(fechaMenor, 1);
				}
			}			
			
			if(rfc7.getTimeUnit().equalsIgnoreCase("DÍA"))
			{
				resultados.add("El día " + mejorFechaDemanda.toString() + " es el de mayor demanda con " + mejorValorDemanda + " reservas");
				resultados.add("El día " + mejorFechaIngresos.toString() + " es el de mayor ingresos con " + mejorValorIngresos + " COP");
				resultados.add("El día " + mejorFechaOcupacion.toString() + " es el de menor ocupación con un " + (menorOcupacion*100) + "%");
			}
			else if(rfc7.getTimeUnit().equalsIgnoreCase("SEMANA"))
			{
				resultados.add("La semana del " + mejorFechaDemanda.toString() + " es el de mayor demanda con " + mejorValorDemanda + " reservas");
				resultados.add("La semana del " + mejorFechaIngresos.toString() + " es el de mayor ingresos con " + mejorValorIngresos + " COP");
				resultados.add("La semana del " + mejorFechaOcupacion.toString() + " es el de menor ocupación con un " + (menorOcupacion*100) + "%");
			}
			else if(rfc7.getTimeUnit().equalsIgnoreCase("MES"))
			{
				resultados.add("El mes " + (mejorFechaDemanda.getMonth() +1)+ " del "+ (mejorFechaDemanda.getYear() + 1900) +" es el de mayor demanda con " + mejorValorDemanda + " reservas");
				resultados.add("El mes " + (mejorFechaIngresos.getMonth() +1) + " del "+ (mejorFechaIngresos.getYear() + 1900) +" es el de mayor ingresos con " + mejorValorIngresos + " COP");
				resultados.add("El mes " + (mejorFechaOcupacion.getMonth() +1) + " del "+ (mejorFechaOcupacion.getYear() + 1900) +" es el de menor ocupación con un " + (menorOcupacion*100) + "%");
			}
			else if(rfc7.getTimeUnit().equalsIgnoreCase("AÑO"))
			{
				resultados.add("El año " + (mejorFechaDemanda.getYear() + 1900) + " es el de mayor demanda con " + mejorValorDemanda + " reservas");
				resultados.add("El año " + (mejorFechaIngresos.getYear() +1900) + " es el de mayor ingresos con " + mejorValorIngresos + " COP");
				resultados.add("El año " + (mejorFechaOcupacion.getYear() + 1900) + " es el de menor ocupación con un " + (menorOcupacion*100) + "%");
			}
			
			return resultados;
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
				daoCatOperador.cerrarRecursos();
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
	
	// RFC8
	
	public ListaRFC8 clientesFrecuentes(long idEspacio) throws Exception
	{
		DAOEspacio daoEspacio = new DAOEspacio();

		try {
			this.conn = darConexion();
			daoEspacio.setConn(conn);

			try
			{
				daoEspacio.buscarEspacio(idEspacio);
			}
			catch(Exception e)
			{
				throw e;
			}

			List<RFC8> resultado = new ArrayList<RFC8>();

			resultado = daoEspacio.obtenerClientesFrecuentes(idEspacio);

			return new ListaRFC8(resultado);
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
	
	// RFC9

	public ListaRFC9 espaciosPocoDemandados() throws Exception
	{
		DAOEspacio daoEspacio = new DAOEspacio();

		try {
			this.conn = darConexion();
			daoEspacio.setConn(conn);

			List<RFC9> resultado = new ArrayList<RFC9>();

			resultado = daoEspacio.obtenerEspaciosPocoDemandados();

			return new ListaRFC9(resultado);
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
	
	// RF7

	public List<Reserva> reservaColectiva(RF7 rf7) throws Exception
	{
		DAOEspacio daoEspacio = new DAOEspacio();
		DAOReserva daoReserva = new DAOReserva();	
		DAOCliente daoCliente = new DAOCliente();
		
		String msgError = "";
		
		try {
			this.conn = darConexion();
			daoEspacio.setConn(conn);
			daoReserva.setConn(conn);
			daoCliente.setConn(conn);			
			
			List<Reserva> resultado = new ArrayList<Reserva>();
			
			if (rf7.getCantidad() <= 0)
			{
				throw new Exception("Especifique un número mayor a 0 de habitaciones a reservas");
			}			
			
			daoCliente.buscarCliente(rf7.getIdCliente());			
			
			List<Espacio> espaciosCandidatos = daoEspacio.obtenerEspaciosRF7(rf7);

			int cantidadRestante = rf7.getCantidad();
			
			long idMayor = 0;
			long idColectivaMayor = 0;
			
			List<Reserva> reservas = daoReserva.darReservas();
			
			for(Reserva reserva : reservas)
			{
				if(reserva.getId() > idMayor)
				{
					idMayor = reserva.getId();
				}
				if(reserva.getIdColectiva() > idColectivaMayor)
				{
					idColectivaMayor = reserva.getIdColectiva();
				}
			}
			
			idColectivaMayor ++;
			
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			conn.setAutoCommit(false);
			
			for(Espacio espacioCandidato : espaciosCandidatos)
			{
				
				if(cantidadRestante > 0)
				{
					idMayor++;
					Reserva agregada = new Reserva(idMayor,rf7.getIdCliente(),espacioCandidato.getId(), idColectivaMayor, rf7.getFechaInicio(), rf7.getDuracion(), null, false, espacioCandidato.getPrecio() * rf7.getDuracion());
					agregada.setFechaReservaDate(new Date());
					try
					{
						addReserva(agregada, true);
						if (cantidadRestante - espacioCandidato.getCapacidad() < 0)
						{
							cantidadRestante = 0;
						}
						else
						{
							cantidadRestante -= espacioCandidato.getCapacidad();
						}
						resultado.add(agregada);	
					}
					catch(Exception e)
					{
						msgError += "No se pudo agregar la reserva al espacio " + espacioCandidato.getId() + " porque "+e.getMessage().toLowerCase() +". ";
					}													
				}				
			}
			
			if(cantidadRestante > 0)
			{
				conn.rollback();
				conn.setAutoCommit(true);
				throw new Exception ("No se pudo realizar la transacción. Faltaron " + cantidadRestante + " habitaciones");
			}
			
			conn.commit();
			
			conn.setAutoCommit(true);
			
			return resultado;
		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage());
			e.printStackTrace();
			throw new Exception (e.getMessage() + ". " + msgError);
		} catch (Exception e) {
			System.err.println("GeneralException:" + e.getMessage());
			e.printStackTrace();
			throw new Exception (e.getMessage() + ". " + msgError);
		} finally {
			try {
				daoEspacio.cerrarRecursos();
				daoReserva.cerrarRecursos();
				if (this.conn != null)
					this.conn.close();
			} catch (SQLException exception) {
				System.err.println("SQLException closing resources:" + exception.getMessage());
				exception.printStackTrace();
				throw new Exception (exception.getMessage() + ". " + msgError);
			}
		}
	}
	
	// RF9
		
	public List<String> deshabilitarEspacio(RF9 rf9) throws SQLException, Exception
	{
		DAOEspacio daoEspacio = new DAOEspacio();
		DAOReserva daoReserva = new DAOReserva();

		try {
			this.conn = darConexion();
			daoEspacio.setConn(conn);
			daoReserva.setConn(conn);
			
			Date fechaDeshabilitacion;
			
			if(rf9.getFechaDeshabilitacion() == null || rf9.getFechaDeshabilitacion().equals(""))
			{
				fechaDeshabilitacion = new Date();
				rf9.setFechaDeshabilitacion((fechaDeshabilitacion.getYear() +1900) + "-" + (fechaDeshabilitacion.getMonth() +1) +"-" + fechaDeshabilitacion.getDate());
			}
			else
			{
				fechaDeshabilitacion = transformarFecha(rf9.getFechaDeshabilitacion());
				
				if(fechaDeshabilitacion.before(new Date()))
				{
					throw new Exception("El espacio ya se encuentra cancelado o deshabilitado, pues se retiró antes de la fecha de deshabilitación");
				}
			}			
			
			Espacio espacio = daoEspacio.buscarEspacio(rf9.getIdEspacio());
			
			List<String> resultado = new ArrayList<String>();
			
			List<Reserva> reservasPorMover = daoReserva.obtenerReservasRF9(rf9);
			
			List<Espacio> espaciosNuevos = daoEspacio.darEspacios();
			
			conn.setAutoCommit(false);
			
			long idMayor = 0;
			
			List<Reserva> reservas = daoReserva.darReservas();
			
			for(Reserva reserva : reservas)
			{
				if(reserva.getId() > idMayor)
				{
					idMayor = reserva.getId();
				}
			}
			
			for(Reserva reserva : reservasPorMover)
			{
				long idEspacioViejo = reserva.getIdEspacio();
				int capacidadVieja = daoEspacio.buscarEspacio(idEspacioViejo).getCapacidad();				
				long idReservaViejo = reserva.getId();
				
				cancelarReserva(reserva, true, false);
				
				conn.commit();
				
				conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				boolean reubicada = false;
				
				for(Espacio espacioN : espaciosNuevos)
				{	
					if(espacio.getId() != espacioN.getId())
					{
						reserva.setIdEspacio(espacioN.getId());
						
						if (reserva.getFechaInicioDate().before(fechaDeshabilitacion))
						{
							reserva.setFechaInicio(rf9.getFechaDeshabilitacion());
						}						
						
						if(espacioN.getCapacidad() >= capacidadVieja)
						{
							try
							{
								idMayor ++;
								reserva.setId(idMayor);
								addReserva(reserva, true);
								
								conn.commit();
								resultado.add("La reserva con el id = " + idReservaViejo +" fue trasladada del espacio con id = " + idEspacioViejo + " al espacio con id = " + espacioN.getId() +" y su nuevo id de reserva es " + reserva.getId());
								reubicada = true;
								break;
							}
							catch(Exception e)
							{
								idMayor --;
								System.out.println(e.getMessage());
								
								conn.rollback();
								
								conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
							}
						}	
					}									
				}
				
				if(!reubicada)
				{
					resultado.add("La reserva con el id = " + idReservaViejo + " no pudo reubicarse");
				}
			}
			
			try
			{
				cancelarEspacio(espacio, fechaDeshabilitacion, true);
			}		
			catch(Exception e)
			{
				conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				conn.rollback();
			}
			
			conn.setAutoCommit(true);
			
			resultado.add("Espacio con id = " + rf9.getIdEspacio() + " exitosamente deshabilitado");
			
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
	
	// RF10
	
	public String habilitarEspacio(Long id) throws Exception {
		DAOEspacio daoEspacio = new DAOEspacio();

		try {
			this.conn = darConexion();
			daoEspacio.setConn(conn);
			
			Espacio habilitado = daoEspacio.buscarEspacio(id);
			
			if(habilitado.getFechaRetiro().equals("2999-12-31"))
			{
				throw new Exception("El espacio con id = " + id + " ya se encuentra habilitado");
			}
			else
			{
				habilitado.setFechaRetiro("2999-12-31");
			}			
			
			daoEspacio.updateEspacio(habilitado);
			
			conn.commit();
			
			return 	"El espacio fue habilitado satisfactoriamente";	
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
	
	// RF8
	
	public String cancelarReservaColectiva(long id) throws Exception 
	{
		DAOEspacio daoEspacio = new DAOEspacio();
		DAOReserva daoReserva = new DAOReserva();	
		
		String msgError = "";
		
		try {
			this.conn = darConexion();
			daoEspacio.setConn(conn);
			daoReserva.setConn(conn);
			
			List<Reserva> reservas = daoReserva.buscarReservasIdColectiva(id);
			
			if(reservas.size() == 0)
			{
				throw new Exception("La reserva colectiva ya fue cancelada, no existe o no tiene reservas individuales asociadas");
			}
			
			int cantidadRestante = reservas.size();
			
			conn.setAutoCommit(false);
			
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);			
			
			String resultado = "";
			
			int contador = 0;
			
			
			for(Reserva reserva : reservas)
			{
				contador ++;
				if(contador == reservas.size())
				{
					resultado += reserva.getId();
				}
				else if (contador == reservas.size() - 1)
				{
					resultado += reserva.getId() + " y ";
				}
				else
				{
					resultado += reserva.getId() + ", ";
				}
				
				try
				{					
					cancelarReserva(reserva, false, true);
					cantidadRestante --;
				}
				catch(Exception e)
				{
					msgError += "No se pudo cancelar la reserva con el id " + reserva.getId() + ". ";
				}							
			}
			
			if(cantidadRestante > 0)
			{
				resultado = "No pudo cancelarse las reservas " + resultado + " que son las que pertenecen a la reserva colectiva " + id;
				conn.rollback();
				conn.setAutoCommit(true);
				throw new Exception ("No se pudo realizar la transacción. Faltaron " + cantidadRestante + " habitaciones");
			}
			else
			{
				resultado = "Se cancelaron satisfactoriamente las reservas " + resultado + " que son las pertenecientes a la reserva colectiva " + id;
			}
			
			conn.commit();
			
			conn.setAutoCommit(true);
			
			return resultado;
		} catch (SQLException e) {
			System.err.println("SQLException:" + e.getMessage());
			e.printStackTrace();
			throw new Exception (e.getMessage() + ". " + msgError);
		} catch (Exception e) {
			System.err.println("GeneralException:" + e.getMessage());
			e.printStackTrace();
			throw new Exception (e.getMessage() + ". " + msgError);
		} finally {
			try {
				daoEspacio.cerrarRecursos();
				daoReserva.cerrarRecursos();
				if (this.conn != null)
					this.conn.close();
			} catch (SQLException exception) {
				System.err.println("SQLException closing resources:" + exception.getMessage());
				exception.printStackTrace();
				throw new Exception (exception.getMessage() + ". " + msgError);
			}
		}
	}
	
	public Date agregarMes(Date fecha, int cantidad)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fecha);
		calendar.add(Calendar.MONTH, cantidad);

		return calendar.getTime();
	}
	
	public Date agregarDía(Date fecha, int cantidad)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fecha);
		calendar.add(Calendar.DAY_OF_YEAR, cantidad);

		return calendar.getTime();
	}
	
	public Date agregarAño(Date fecha, int cantidad)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fecha);
		calendar.add(Calendar.YEAR, cantidad);

		return calendar.getTime();
	}
	
	public Date agregarSemana(Date fecha, int cantidad)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fecha);
		calendar.add(Calendar.WEEK_OF_YEAR, cantidad);

		return calendar.getTime();
	}

	public Date transformarFecha(String fecha)
	{
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return new Date(format.parse(fecha).getTime());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
