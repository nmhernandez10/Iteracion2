package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import vos.CategoriaHabitacion;
import vos.CategoriaOperador;
import vos.Habitacion;

public class DAOCategoriaHabitacion 
{
	private ArrayList<Object> recursos;

	private Connection conn;

	public DAOCategoriaHabitacion() {
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
	
	public ArrayList<CategoriaHabitacion> darCategoriasHabitacion() throws SQLException, Exception {
		ArrayList<CategoriaHabitacion> categoriasHabitacion = new ArrayList<CategoriaHabitacion>();

		String sql = "SELECT * FROM CATEGORIASHABITACION";

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			long id = Long.parseLong(rs.getString("ID"));
			String categoria = rs.getString("NOMBRE");
			String descripcion = rs.getString("DESCRIPCIÓN");

			categoriasHabitacion.add(new CategoriaHabitacion(id, categoria, descripcion));
		}
		
		prepStmt.close();
		
		return categoriasHabitacion;
	}

	public void addCategoriaHabitacion(CategoriaHabitacion categoriaHabitacion) throws SQLException, Exception {
		String sql = "INSERT INTO CATEGORIASHABITACION VALUES (";
		sql += "ID = " + categoriaHabitacion.getId() + ",";
		sql += "NOMBRE= '" + categoriaHabitacion.getNombre() + "',";
		sql += "DESCRIPCION = '" + categoriaHabitacion.getDescripcion() + "')";		

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public void updateCategoriaHabitacion(CategoriaHabitacion categoriaHabitacion) throws SQLException, Exception {
		String sql = "UPDATE CATEGORIASHABITACION SET ";
		sql += "ID = " + categoriaHabitacion.getId() + ",";
		sql += "NOMBRE = '" + categoriaHabitacion.getNombre() + "',";
		sql += "DESCRIPCION = '" + categoriaHabitacion.getDescripcion() + "')";		

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public void deleteCategoriaHabitacion(CategoriaHabitacion categoriaHabitacion) throws SQLException, Exception {
		String sql = "DELETE FROM CATEGORIASHABITACION";
		sql += " WHERE ID = " + categoriaHabitacion.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public CategoriaHabitacion buscarCategoriaHabitacion(long id) throws SQLException, Exception {
		String sql = "SELECT * FROM CATEGORIASHABITACION WHERE ID  = " + id;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		if(!rs.next())
		{
			throw new Exception("No se encontró ninguna categoría de habitación con el id = "+id);
		}
		
		String categoria = rs.getString("NOMBRE");
		String descripcion = rs.getString("DESCRIPCION");
		
		prepStmt.close();
		
		return new CategoriaHabitacion(id, categoria, descripcion);
	}
	
	public CategoriaHabitacion buscarCategoriaHabitacionIdHabitacion(long id) throws SQLException, Exception{
		DAOHabitacion daoHabitacion = new DAOHabitacion();
		
		daoHabitacion.setConn(conn);
		
		Habitacion habitacion = daoHabitacion.buscarHabitacion(id);
		
		return habitacion.getCategoria();
	}
	
	public CategoriaHabitacion buscarCategoriaHabitacionNombre(String nombre) throws SQLException, Exception 
	{
		String sql = "SELECT * FROM CATEGORIASHABITACION WHERE NOMBRE ='" + nombre + "'";

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		if(!rs.next())
		{
			throw new Exception ("No se encontró ninguna categoría de habitacion con el nombre '"+ nombre +"'");
		}
		
		long id = Long.parseLong(rs.getString("ID"));
		String descripcion = rs.getString("DESCRIPCION");

		prepStmt.close();
		
		return new CategoriaHabitacion(id, nombre, descripcion);
	}
}
