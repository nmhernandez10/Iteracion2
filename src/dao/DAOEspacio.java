package dao;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import vos.CategoriaServicio;
import vos.Cliente;
import vos.Espacio;
import vos.Habitacion;
import vos.Operador;
import vos.RF7;
import vos.RF9;
import vos.RFC4;
import vos.RFC8;
import vos.RFC9;
import vos.Reserva;
import vos.Servicio;

public class DAOEspacio {
	private ArrayList<Object> recursos;

	private Connection conn;

	public DAOEspacio() {
		recursos = new ArrayList<Object>();
	}

	public void cerrarRecursos() {
		for (Object ob : recursos) {
			if (ob instanceof PreparedStatement) {
				try {
					((PreparedStatement) ob).close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		}
	}

	public void setConn(Connection con) {
		this.conn = con;
	}

	public ArrayList<Espacio> darEspacios() throws SQLException, Exception {
		ArrayList<Espacio> espacios = new ArrayList<Espacio>();

		String sql = "SELECT * FROM ESPACIOS";

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			long id = Long.parseLong(rs.getString("ID"));
			long registro = Long.parseLong(rs.getString("REGISTRO"));
			int capacidad = Integer.parseInt(rs.getString("CAPACIDAD"));
			double tamaño = Double.parseDouble(rs.getString("TAMAÑO"));
			String ubicacion = rs.getString("DIRECCION");
			double precio = Double.parseDouble(rs.getString("PRECIO"));
			Date fechaRetiroD = rs.getDate("FECHARETIRO");
			String fechaRetiro = null;
			if(fechaRetiroD != null)
			{
				fechaRetiro = fechaRetiroD.toString();
			}	

			DAOHabitacion daoHabitacion = new DAOHabitacion();
			daoHabitacion.setConn(conn);

			List<Long> habitaciones = daoHabitacion.buscarHabitacionesIdEspacio(id);

			DAOReserva daoReserva = new DAOReserva();
			daoReserva.setConn(conn);

			List<Long> reservas = daoReserva.buscarReservasIdEspacio(id);

			DAOOperador daoOperador = new DAOOperador();
			daoOperador.setConn(conn);

			long operador = daoOperador.buscarOperadorIdEspacio(id);

			DAOServicio daoServicio = new DAOServicio();
			daoServicio.setConn(conn);

			List<Long> servicios = daoServicio.buscarServiciosIdEspacio(id);

			espacios.add(new Espacio(id, registro, capacidad, tamaño, ubicacion, precio, fechaRetiro, operador,
					reservas, servicios, habitaciones));
		}
		
		prepStmt.close();
		
		return espacios;
	}

	public void addEspacio(Espacio espacio) throws SQLException, Exception {
		String sql = "INSERT INTO ESPACIOS (ID, IDOPERADOR, CAPACIDAD, REGISTRO, TAMAÑO, DIRECCION, PRECIO, FECHARETIRO) VALUES( ";
		sql += espacio.getId() + ",";
		sql += espacio.getOperador() + ",";
		sql += espacio.getCapacidad() + ",";
		sql += espacio.getRegistro() + ",";
		sql += espacio.getTamaño() + ",'";
		sql += espacio.getDireccion() + "',";
		sql += espacio.getPrecio() + ",";	
		sql += "TO_DATE('" + espacio.getFechaRetiro() + "','YYYY-MM-DD'))";

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public void updateEspacio(Espacio espacio) throws SQLException, Exception {
		String sql = "UPDATE ESPACIOS SET ";
		sql += "idOperador = " + espacio.getOperador() + ",";
		sql += "capacidad = " + espacio.getCapacidad() + ",";
		sql += "registro = " + espacio.getRegistro() + ",";
		sql += "tamaño = " + espacio.getTamaño() + ",";
		sql += "direccion = '" + espacio.getDireccion() + "',";
		sql += "precio = " + espacio.getPrecio() + ",";		
		sql += "fechaRetiro = TO_DATE('"+ espacio.getFechaRetiro() +"','YYYY-MM-DD')";
		sql += " WHERE id =" + espacio.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public void deleteEspacio(Espacio espacio) throws SQLException, Exception 
	{
		String sql = "DELETE FROM ESPACIOS";
		sql += " WHERE ID = " + espacio.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public Espacio buscarEspacio(long id) throws SQLException, Exception {
		String sql = "SELECT * FROM ESPACIOS WHERE ID =" + id;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		if(!rs.next())
		{
			throw new Exception ("No se encontró ningún espacio con el id = "+id);
		}
		
		long registro = Long.parseLong(rs.getString("REGISTRO"));
		int capacidad = Integer.parseInt(rs.getString("CAPACIDAD"));
		double tamaño = Double.parseDouble(rs.getString("TAMAÑO"));
		String ubicacion = rs.getString("DIRECCION");
		double precio = Double.parseDouble(rs.getString("PRECIO"));
		Date fechaRetiroD = rs.getDate("FECHARETIRO");
		String fechaRetiro = null;
		if(fechaRetiroD != null)
		{
			fechaRetiro = fechaRetiroD.toString();
		}		
		
		DAOHabitacion daoHabitacion = new DAOHabitacion();
		daoHabitacion.setConn(conn);

		List<Long> habitaciones = daoHabitacion.buscarHabitacionesIdEspacio(id);

		DAOReserva daoReserva = new DAOReserva();
		daoReserva.setConn(conn);

		List<Long> reservas = daoReserva.buscarReservasIdEspacio(id);

		DAOOperador daoOperador = new DAOOperador();
		daoOperador.setConn(conn);

		long operador = daoOperador.buscarOperadorIdEspacio(id);

		DAOServicio daoServicio = new DAOServicio();
		daoServicio.setConn(conn);

		List<Long> servicios = daoServicio.buscarServiciosIdEspacio(id);

		prepStmt.close();
		
		return new Espacio(id, registro, capacidad, tamaño, ubicacion, precio, fechaRetiro, operador, reservas,
				servicios, habitaciones);
	}

	public ArrayList<Long> buscarEspaciosIdOperador(long pId) throws SQLException, Exception {
		ArrayList<Long> espacios = new ArrayList<Long>();

		String sql = "SELECT * FROM ESPACIOS WHERE IDOPERADOR = " + pId;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		while (rs.next()) {
			long id = Integer.parseInt(rs.getString("ID"));
			espacios.add(id);
		}
		
		prepStmt.close();
		
		return espacios;
	}

	public ArrayList<Integer> buscarEspaciosReservaCliente(long pId) throws SQLException, Exception {
		
		ArrayList<Integer> espacios = new ArrayList<Integer>();
		
		String sql = "SELECT * FROM RESERVAS WHERE IDCLIENTE = " + pId;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		while (rs.next()) {
			int id = Integer.parseInt(rs.getString("IDESPACIO"));
			espacios.add(id);
		}

		prepStmt.close();
		
		return espacios;
	}

	public long buscarEspacioIdHabitacion(long pId) throws SQLException, Exception {
		String sql = "SELECT * FROM HABITACIONES WHERE ID = " + pId;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		if(!rs.next())
		{
			throw new Exception ("No se encontró ningún espacio con la habitación que tiene id = "+pId);
		}

		int id = Integer.parseInt(rs.getString("IDESPACIO"));
		int espacio = id;

		prepStmt.close();
		
		return espacio;
	}
	
	//RFC7
	
	public List<Espacio> buscarEspaciosIdCategoriaOperador(long idCatOperador) throws SQLException, Exception {

		List<Espacio> espacios = new ArrayList<Espacio>();

		String sql ="SELECT ESPACIOS.ID "+
					"FROM ESPACIOS, OPERADORES "+
					"WHERE ESPACIOS.IDOPERADOR = OPERADORES.ID AND OPERADORES.IDCATEGORIA = "+idCatOperador;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			long idR = Long.parseLong(rs.getString("ID"));

			espacios.add(buscarEspacio(idR));
		}
		
		prepStmt.close();
		
		return espacios;
	}

	// RFC2

	public List<Espacio> obtenerEspaciosPopulares() throws Exception, SQLException {
		String sql = "SELECT ID FROM (SELECT RESERVAS.IDESPACIO AS ID, COUNT(RESERVAS.IDCLIENTE) AS CONTEO FROM RESERVAS GROUP BY RESERVAS.IDESPACIO ORDER BY CONTEO DESC) WHERE ROWNUM <= 20";

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		List<Espacio> espacios = new ArrayList<Espacio>();

		while (rs.next()) {
			long id = Long.parseLong(rs.getString("ID"));
			Espacio resultante = buscarEspacio(id);
			espacios.add(resultante);
		}
		
		prepStmt.close();
		
		return espacios;
	}
	
	//RFC4
	
	public List<Espacio> obtenerEspaciosDisponibles(RFC4 rfc4) throws Exception, SQLException {
		
		String sql = "SELECT ID "+
				"FROM(SELECT ID, COUNT(ID) AS CONTEO "+
				"FROM(SELECT ESPACIOS.ID " +
				"FROM ESPACIOS INNER JOIN SERVICIOS ON ESPACIOS.ID = SERVICIOS.IDESPACIO " +
				"WHERE ESPACIOS.ID NOT IN(SELECT ID "+
				"FROM ESPACIOS "+
				"WHERE FECHARETIRO < TO_DATE('" + rfc4.getFechaMayor() + "','YYYY-MM-DD')) AND ESPACIOS.ID NOT IN (SELECT idEspacio " +
				"FROM(SELECT idEspacio, COUNT(ID) as CONTEO "+
				"FROM RESERVAS "+
				"WHERE FECHAINICIO > TO_DATE('" + rfc4.getFechaMenor() + "','YYYY-MM-DD') AND FECHAINICIO + DURACION < TO_DATE('" + rfc4.getFechaMayor()+"','YYYY-MM-DD') "+
				"GROUP BY idEspacio))";
		
		List<String> servicios = rfc4.getServicios();
		
		DAOCategoriaServicio daoCatServicio = new DAOCategoriaServicio();
		daoCatServicio.setConn(conn);
		
		if(servicios.size() >0)
		{
			sql += " AND(";
		}
		
		int conteo = 0;
		
		for(String idString : servicios)
		{
			conteo ++;
			long idS;
			try
			{				
				CategoriaServicio cat = daoCatServicio.buscarCategoriaServicioNombre(idString);
				idS = cat.getId();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new Exception("No existe una categoría de servicios con el nombre '" + idString + "'");
			}
			
			if(conteo == 1)
			{
				sql += "SERVICIOS.IDCATEGORIA = " + idS;
			}
			else
			{
				sql += " OR SERVICIOS.IDCATEGORIA = " + idS;
			}			
		}
		
		if(servicios.size() >0)
		{
			sql += ")";
		}
		
		sql += ") GROUP BY ID)TABLACATEGORIAS "+
				"WHERE TABLACATEGORIAS.CONTEO = " + rfc4.getServicios().size();
		
		if(servicios.size() == 0)
		{
			sql = "SELECT ESPACIOS.ID "+
					"FROM ESPACIOS INNER JOIN SERVICIOS ON ESPACIOS.ID = SERVICIOS.IDESPACIO "+
					"WHERE ESPACIOS.ID NOT IN(SELECT ID "+
					"FROM ESPACIOS "+
					"WHERE FECHARETIRO < TO_DATE('" + rfc4.getFechaMayor() + "','YYYY-MM-DD')) AND ESPACIOS.ID NOT IN (SELECT idEspacio "+
					"FROM(SELECT idEspacio, COUNT(ID) as CONTEO "+
					"FROM RESERVAS "+
					"WHERE FECHAINICIO > TO_DATE('" + rfc4.getFechaMenor() + "','YYYY-MM-DD') AND FECHAINICIO + DURACION < TO_DATE('" + rfc4.getFechaMayor()+"','YYYY-MM-DD') "+
					"GROUP BY idEspacio))";
		}
		
		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		List<Espacio> espacios = new ArrayList<Espacio>();

		while (rs.next()) {
			long id = Long.parseLong(rs.getString("ID"));
			Espacio resultante = buscarEspacio(id);
			espacios.add(resultante);
		}
		
		prepStmt.close();
		
		return espacios;
	}
	
	//RFC8
	
	public List<RFC8> obtenerClientesFrecuentes(long id) throws SQLException, Exception
	{
		List<RFC8> resultado = new ArrayList<RFC8>();
		
		String sql = "SELECT IDCLIENTE, DURTOTAL, CONTEO "+
						"FROM(SELECT RESERVAS.IDCLIENTE, SUM(RESERVAS.DURACION) AS DURTOTAL, COUNT(RESERVAS.ID) AS CONTEO "+
						"FROM RESERVAS, ESPACIOS "+
						"WHERE RESERVAS.IDESPACIO = ESPACIOS.ID AND RESERVAS.CANCELADO = 'N' AND ESPACIOS.ID = "+id+
						" GROUP BY RESERVAS.IDCLIENTE) "+
						"WHERE DURTOTAL >= 15 OR CONTEO >= 3";
		
		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		DAOCliente daoCliente = new DAOCliente();
		daoCliente.setConn(conn);
		
		while (rs.next()) 
		{
			long idCliente = Long.parseLong(rs.getString("IDCLIENTE"));
			Cliente cliente = daoCliente.buscarCliente(idCliente);
			int durTotal = rs.getInt("DURTOTAL");
			int ocasiones = rs.getInt("CONTEO");
			resultado.add(new RFC8(cliente, durTotal, ocasiones));
		}
		
		prepStmt.close();
		
		return resultado;
	}
	
	//RFC9

	public List<RFC9> obtenerEspaciosPocoDemandados() throws SQLException, Exception
	{
		List<RFC9> resultado = new ArrayList<RFC9>();

		String sql ="SELECT * FROM(SELECT IDESPACIO, MAX(ESPACIODIAS) AS MAXIMOPERIODO "+
						"FROM (WITH FECHAS AS(SELECT ROWNUM AS ID, IDESPACIO, FECHAINICIO, DURACION "+
						"FROM(SELECT IDESPACIO, FECHAINICIO, DURACION "+
						"FROM RESERVAS "+
						"WHERE RESERVAS.CANCELADO = 'N' "+
						"ORDER BY IDESPACIO ASC, FECHAINICIO ASC)) "+
						"SELECT T1.FECHAINICIO - (T2.FECHAINICIO + T2.DURACION) AS ESPACIODIAS, T1.IDESPACIO " +
						"FROM (FECHAS)T1, (FECHAS)T2 "+
						"WHERE T2.ID = T1.ID -1 AND T2.IDESPACIO = T1.IDESPACIO) " +
						"GROUP BY IDESPACIO) WHERE MAXIMOPERIODO > 30";

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) 
		{
			long idEspacio = Long.parseLong(rs.getString("IDESPACIO"));
			Espacio espacio = buscarEspacio(idEspacio);
			int maxPer = rs.getInt("MAXIMOPERIODO");
			resultado.add(new RFC9(espacio, maxPer));
		}
		
		prepStmt.close();

		return resultado;
	}
	
	// RF7
	
	public List<Espacio> obtenerEspaciosRF7(RF7 rf7) throws SQLException, Exception
	{
		DAOCategoriaHabitacion daoCatHabitacion = new DAOCategoriaHabitacion();
		DAOCategoriaOperador daoCatOperador = new DAOCategoriaOperador();
		DAOCategoriaServicio daoCatServicio = new DAOCategoriaServicio();
		
		daoCatHabitacion.setConn(conn);
		daoCatOperador.setConn(conn);
		daoCatServicio.setConn(conn);
		
		List<Espacio> resultado = new ArrayList<Espacio>();

		int numServicios = rf7.getServicios().size();
		long idCatHabitacion = daoCatHabitacion.buscarCategoriaHabitacionNombre(rf7.getTipoHabitacion()).getId();
		long idCatOperador = daoCatOperador.buscarCategoriaOperadorNombre(rf7.getCategoria()).getId();		
		
		String sql ="SELECT ID "+
				"FROM ESPACIOS "+
				"WHERE ID IN(SELECT TABLAESPACIOS.ID "+
				"FROM HABITACIONES,(SELECT ESPACIOS.ID "+
				            "FROM ESPACIOS, (SELECT * "+
				            "FROM OPERADORES "+
				            "WHERE IDCATEGORIA = " + idCatOperador + ") TABLAOPERADORES "+
				            "WHERE ESPACIOS.IDOPERADOR = TABLAOPERADORES.ID) TABLAESPACIOS "+
				            "WHERE HABITACIONES.IDESPACIO = TABLAESPACIOS.ID  AND HABITACIONES.IDCATEGORIA = "+idCatHabitacion+") ";
		
		if (numServicios > 0)
		{
			sql += "AND ID IN(SELECT ID "+
					"FROM(SELECT ID, COUNT(ID) AS CONTEO "+
					"FROM(SELECT TABLAESPACIOS.ID, SERVICIOS.IDCATEGORIA "+
					"FROM SERVICIOS,(SELECT ESPACIOS.ID "+
					"FROM ESPACIOS, (SELECT * "+
					"FROM OPERADORES "+
					"WHERE IDCATEGORIA = "+idCatOperador+") TABLAOPERADORES "+
					"WHERE ESPACIOS.IDOPERADOR = TABLAOPERADORES.ID) TABLAESPACIOS "+
					"WHERE SERVICIOS.IDESPACIO = TABLAESPACIOS.ID";

			sql += " AND (";		

			int contador = 0;

			for(String serV : rf7.getServicios())
			{
				contador ++;
				long idCatServ = daoCatServicio.buscarCategoriaServicioNombre(serV).getId();

				if(contador == 1)
				{
					sql += "SERVICIOS.IDCATEGORIA = " +idCatServ;
				}
				else
				{
					sql += " OR SERVICIOS.IDCATEGORIA = " +idCatServ;
				}
			}

			sql += ")";

			sql +=")TABLACATEGORIAS "+
					"GROUP BY TABLACATEGORIAS.ID) "+
					"WHERE CONTEO = "+numServicios+")";

		}

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) 
		{
			long idEspacio = Long.parseLong(rs.getString("ID"));
			Espacio espacio = buscarEspacio(idEspacio);
			resultado.add(espacio);
		}
		
		prepStmt.close();

		return resultado;
	}
}
