package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import vos.CategoriaHabitacion;
import vos.CategoriaServicio;
import vos.Servicio;

public class DAOCategoriaServicio 
{
	private ArrayList<Object> recursos;

	private Connection conn;

	public DAOCategoriaServicio() {
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
	
	public ArrayList<CategoriaServicio> darCategoriasServicio() throws SQLException, Exception {
		ArrayList<CategoriaServicio> categoriasServicio = new ArrayList<CategoriaServicio>();

		String sql = "SELECT * FROM CATEGORIASSERVICIO";

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		while (rs.next()) {
			long id = Long.parseLong(rs.getString("ID"));
			String categoria = rs.getString("NOMBRE");
			String descripcion = rs.getString("DESCRIPCION");

			categoriasServicio.add(new CategoriaServicio(id, categoria, descripcion));
		}
		
		prepStmt.close();
		
		return categoriasServicio;
	}

	public void addCategoriaServicio(CategoriaServicio categoriaServicio) throws SQLException, Exception {
		String sql = "INSERT INTO CATEGORIASSERVICIO VALUES (";
		sql += "ID = " + categoriaServicio.getId() + ",";
		sql += "NOMBRE = '" + categoriaServicio.getNombre() + "',";
		sql += "DESCRIPCION = '" + categoriaServicio.getDescripcion() + "')";		

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public void updateCategoriaServicio(CategoriaServicio categoriaServicio) throws SQLException, Exception {
		String sql = "UPDATE CATEGORIASSERVICIO SET ";
		sql += "ID = " + categoriaServicio.getId() + ",";
		sql += "NOMBRE = '" + categoriaServicio.getNombre() + "',";
		sql += "DESCRIPCION = '" + categoriaServicio.getDescripcion() + "')";		

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public void deleteCategoriaServicio(CategoriaServicio categoriaServicio) throws SQLException, Exception {
		String sql = "DELETE FROM CATEGORIASSERVICIO";
		sql += " WHERE ID = " + categoriaServicio.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public CategoriaServicio buscarCategoriaServicio(long id) throws SQLException, Exception {
		String sql = "SELECT * FROM CATEGORIASSERVICIO WHERE ID  =" + id;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();		
		
		if(!rs.next())
		{
			throw new Exception ("No se encontró ninguna categoría de servicio con el id = "+id);
		}
		
		String categoria = rs.getString("NOMBRE");
		String descripcion = rs.getString("DESCRIPCION");
		
		prepStmt.close();
		
		return new CategoriaServicio(id, categoria, descripcion);
	}
	
	public CategoriaServicio buscarCategoriaServicioIdServicio(long id) throws SQLException, Exception{
		DAOServicio daoServicio = new DAOServicio();
		
		daoServicio.setConn(conn);
		
		Servicio servicio = daoServicio.buscarServicio(id);
		
		return servicio.getCategoria();
	}
	
	public CategoriaServicio buscarCategoriaServicioNombre(String nombre) throws SQLException, Exception 
	{
		String sql = "SELECT * FROM CATEGORIASSERVICIO WHERE NOMBRE ='" + nombre + "'";

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		if(!rs.next())
		{
			throw new Exception ("No se encontró ninguna categoría de servicio con el nombre '"+ nombre +"'");
		}
		
		long id = Long.parseLong(rs.getString("ID"));
		String descripcion = rs.getString("DESCRIPCION");

		prepStmt.close();
		
		return new CategoriaServicio(id, nombre, descripcion);
	}
}
