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
import vos.Operador;
import vos.RF1;
import vos.RF2;
import vos.RF2Habitacion;
import vos.RF2Servicio;
import vos.RF3;
import vos.RF7;
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

			if (espacio.getCapacidad() < espacio.calcularOcupacionEnFecha(reserva.getFechaInicioDate(), conn) + 1) {
				throw new Exception("La nueva reserva excediría la capacidad del espacio a reservar");
			}

			if (daoOperador.buscarOperador(espacio.getOperador()).getCategoria().getNombre().toUpperCase().equals("VIVIENDA_UNIVERSITARIA")
					&& (cliente.getVinculo().getNombre().toUpperCase().equals("ESTUDIANTE") || cliente.getVinculo().getNombre().toUpperCase().equals("PROFESOR")
							|| cliente.getVinculo().getNombre().toUpperCase().equals("EMPLEADO")
							|| cliente.getVinculo().getNombre().toUpperCase().equals("PROFESOR_INVITADO"))) {
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
				throw new Exception("La reserva no puede cancelarse porque ya estaba cancelada.");
			}

			Date fechaCancelacion = new Date();

			if (reserva.getFechaInicioDate().before(fechaCancelacion))
			{
				throw new Exception("Esta reserva ya está en curso, no puede cancelarse.");
			}

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
				daoCliente.cerrarRecursos();
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
				daoOperador.cerrarRecursos();
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

	//RFC7
	public List<Date> analizarOperacion(RFC7 rfc7) throws Exception {
		DAOOperador daoOperador = new DAOOperador();
		DAOReserva daoReserva = new DAOReserva();

		RFC7 resultado = rfc7;
		try {
			this.conn = darConexion();
			daoOperador.setConn(conn);
			daoReserva.setConn(conn);

			ArrayList<Operador> listaOperadores = (ArrayList<Operador>)daoOperador.buscarOperadoresPorCategoria(rfc7.getcategoria());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
		
		String msgError = "";
		
		try {
			this.conn = darConexion();
			daoEspacio.setConn(conn);
			daoReserva.setConn(conn);
						
			List<Reserva> resultado = new ArrayList<Reserva>();

			List<Espacio> espaciosCandidatos = daoEspacio.obtenerEspaciosRF7(rf7);

			int cantidadRestante = rf7.getCantidad();
			
			long idMayor = 0;
			
			List<Reserva> reservas = daoReserva.darReservas();
			
			for(Reserva reserva : reservas)
			{
				if(reserva.getId() > idMayor)
				{
					idMayor = reserva.getId();
				}
			}
			
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			conn.setAutoCommit(false);
			
			for(Espacio espacioCandidato : espaciosCandidatos)
			{
				int numHabitaciones = espacioCandidato.getHabitaciones().size();			
				
				if(cantidadRestante > 0)
				{
					idMayor++;
					Reserva agregada = new Reserva(idMayor,rf7.getIdCliente(),espacioCandidato.getId(), rf7.getFechaInicio(), rf7.getDuracion(), null, false, espacioCandidato.getPrecio() * rf7.getDuracion());
					agregada.setFechaReservaDate(new Date());
					try
					{
						addReserva(agregada, true);
						if (cantidadRestante - numHabitaciones < 0)
						{
							cantidadRestante = 0;
						}
						else
						{
							cantidadRestante -= numHabitaciones;
						}
						resultado.add(agregada);	
					}
					catch(Exception e)
					{
						msgError += "No se pudo agregar la reserva al espacio " + espacioCandidato.getId() + " porque "+e.getMessage().toLowerCase();
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
}
