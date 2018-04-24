package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vos.CategoriaOperador;
import vos.Espacio;
import vos.Habitacion;
import vos.CategoriaHabitacion;

public class DAOHabitacion {
	private ArrayList<Object> recursos;

	private Connection conn;

	public DAOHabitacion() {
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

	public ArrayList<Habitacion> darHabitaciones() throws SQLException, Exception {
		ArrayList<Habitacion> habitacions = new ArrayList<Habitacion>();

		String sql = "SELECT * FROM HABITACIONES";

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			long id = Long.parseLong(rs.getString("ID"));
			int capacidad = Integer.parseInt(rs.getString("CAPACIDAD"));
			boolean compartido = false;
			if (rs.getString("COMPARTIDO").equals('Y')) {
				compartido = true;
			}
			DAOCategoriaHabitacion daoCategoriaHabitacion = new DAOCategoriaHabitacion();			
			daoCategoriaHabitacion.setConn(conn);		
			CategoriaHabitacion categoria = daoCategoriaHabitacion.buscarCategoriaHabitacion(Long.parseLong(rs.getString("IDCATEGORIA")));			
			DAOEspacio daoEspacio = new DAOEspacio();
			daoEspacio.setConn(conn);
			long espacio = daoEspacio.buscarEspacioIdHabitacion(id);

			habitacions.add(new Habitacion(id, categoria, compartido, capacidad, espacio));
		}
		
		prepStmt.close();
		
		return habitacions;
	}

	public void addHabitacion(Habitacion habitacion) throws SQLException, Exception {
		String sql = "INSERT INTO HABITACIONES (id, idEspacio, idCategoria, capacidad, compartido) VALUES (";
		sql += habitacion.getId() + ",";
		sql += habitacion.getEspacio() + ",";
		sql += habitacion.getCategoria().getId() + ",";
		sql += habitacion.getCapacidad() + ",'";
		char comp;
		if (habitacion.isCompartido()) {
			comp = 'Y';
		} else {
			comp = 'N';
		}
		sql += comp + "')";

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public void updateHabitacion(Habitacion habitacion) throws SQLException, Exception {
		String sql = "UPDATE HABITACIONES SET ";
		sql += "idEspacio = " + habitacion.getEspacio() + ",";
		sql += "idEategoria = " + habitacion.getCategoria().getId() + ",";
		sql += "capacidad = " + habitacion.getCapacidad() + ",";
		char comp;
		if (habitacion.isCompartido()) {
			comp = 'Y';
		} else {
			comp = 'N';
		}
		sql += "compartido = " + comp;
		sql += " WHERE ID = " + habitacion.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public void deleteHabitacion(Habitacion habitacion) throws SQLException, Exception {
		String sql = "DELETE FROM HABITACIONES";
		sql += " WHERE ID = " + habitacion.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public Habitacion buscarHabitacion(long id) throws SQLException, Exception {
		String sql = "SELECT * FROM HABITACIONES WHERE ID  =" + id;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		if(!rs.next())
		{
			throw new Exception ("No se encontró ninguna habitación con el id = "+id);
		}
		
		int capacidad = Integer.parseInt(rs.getString("CAPACIDAD"));
		boolean compartido = false;
		if (rs.getString("COMPARTIDO").equals('Y')) {
			compartido = true;
		}
		DAOCategoriaHabitacion daoCategoriaHabitacion = new DAOCategoriaHabitacion();			
		daoCategoriaHabitacion.setConn(conn);		
		CategoriaHabitacion categoria = daoCategoriaHabitacion.buscarCategoriaHabitacion(Long.parseLong(rs.getString("IDCATEGORIA")));			
		DAOEspacio daoEspacio = new DAOEspacio();
		daoEspacio.setConn(conn);
		long espacio = daoEspacio.buscarEspacioIdHabitacion(id);

		prepStmt.close();
		
		return new Habitacion(id, categoria, compartido, capacidad, espacio);
	}

	public List<Long> buscarHabitacionesIdEspacio(long id) throws SQLException, Exception {
		String sql = "SELECT * FROM HABITACIONES WHERE IDESPACIO  =" + id ;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		List<Long> habitaciones = new ArrayList<Long>();
		while (rs.next()) {
			long idH = Long.parseLong(rs.getString("ID"));
			habitaciones.add(idH);
		}
		
		prepStmt.close();

		return habitaciones;
	}
}
