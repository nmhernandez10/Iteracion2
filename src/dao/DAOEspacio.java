package dao;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import vos.Espacio;
import vos.Habitacion;
import vos.Operador;
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
			double tama�o = Double.parseDouble(rs.getString("TAMA�O"));
			String ubicacion = rs.getString("UBICACION");
			double precio = Double.parseDouble(rs.getString("PRECIO"));
			String fechaRetiro = rs.getString("FECHARETIRO");
			

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

			espacios.add(new Espacio(id, registro, capacidad, tama�o, ubicacion, precio, fechaRetiro, operador,
					reservas, servicios, habitaciones));
		}
		return espacios;
	}

	public void addEspacio(Espacio espacio) throws SQLException, Exception {
		String sql = "INSERT INTO ESPACIOS (ID, IDOPERADOR, CAPACIDAD, REGISTRO, TAMA�O, DIRECCION, PRECIO, FECHARETIRO) VALUES( ";
		sql += espacio.getId() + ",";
		sql += espacio.getOperador() + ",";
		sql += espacio.getCapacidad() + ",";
		sql += espacio.getRegistro() + ",";
		sql += espacio.getTama�o() + ",'";
		sql += espacio.getUbicacion() + "',";
		sql += espacio.getPrecio() + ",";	
		sql += "TO_DATE('"+(espacio.getFechaRetiroDate().getDate()) + "-" + (espacio.getFechaRetiroDate().getMonth() +1) +"-" + (espacio.getFechaRetiroDate().getYear()+1900)  + "','DD-MM-YYYY'))";

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
	}

	public void updateEspacio(Espacio espacio) throws SQLException, Exception {
		String sql = "UPDATE ESPACIOS SET ";
		sql += "idOperador = " + espacio.getOperador() + ",";
		sql += "capacidad = " + espacio.getCapacidad() + ",";
		sql += "registro = " + espacio.getRegistro() + ",";
		sql += "tama�o = " + espacio.getTama�o() + ",";
		sql += "direccion = '" + espacio.getUbicacion() + "',";
		sql += "precio = " + espacio.getPrecio() + ",";		
		sql += "fechaRetiro = TO_DATE('"+(espacio.getFechaRetiroDate().getDate()) + "-" + (espacio.getFechaRetiroDate().getMonth() +1) +"-" + (espacio.getFechaRetiroDate().getYear()+1900)  + "','DD-MM-YYYY')";
		sql += " WHERE id =" + espacio.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
	}

	public void deleteEspacio(Espacio espacio) throws SQLException, Exception 
	{
		String sql = "DELETE FROM ESPACIOS";
		sql += " WHERE ID = " + espacio.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
	}

	public Espacio buscarEspacio(long id) throws SQLException, Exception {
		String sql = "SELECT * FROM ESPACIOS WHERE ID =" + id;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		if(!rs.next())
		{
			throw new Exception ("No se encontr� ning�n espacio con el id = "+id);
		}
		
		long registro = Long.parseLong(rs.getString("REGISTRO"));
		int capacidad = Integer.parseInt(rs.getString("CAPACIDAD"));
		double tama�o = Double.parseDouble(rs.getString("TAMA�O"));
		String ubicacion = rs.getString("DIRECCION");
		double precio = Double.parseDouble(rs.getString("PRECIO"));
		String fechaRetiro = rs.getString("FECHARETIRO");

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

		return new Espacio(id, registro, capacidad, tama�o, ubicacion, precio, fechaRetiro, operador, reservas,
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
			throw new Exception ("No se encontr� ning�n espacio con la habitaci�n que tiene id = "+pId);
		}

		int id = Integer.parseInt(rs.getString("IDESPACIO"));
		int espacio = id;

		return espacio;
	}

	// RFC2

	public List<Espacio> espaciosPopulares() throws Exception, SQLException {
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
		
		return espacios;
	}
}
