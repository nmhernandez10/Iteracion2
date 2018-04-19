package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import vos.CategoriaOperador;
import vos.Operador;

public class DAOCategoriaOperador 
{
	private ArrayList<Object> recursos;

	private Connection conn;

	public DAOCategoriaOperador() {
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
	
	public ArrayList<CategoriaOperador> darCategoriasOperador() throws SQLException, Exception {
		ArrayList<CategoriaOperador> categoriasOperador = new ArrayList<CategoriaOperador>();

		String sql = "SELECT * FROM CATEGORIASOPERADOR";

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			long id = Long.parseLong(rs.getString("ID"));
			String categoria = rs.getString("NOMBRE");
			String descripcion = rs.getString("DESCRIPCION");

			categoriasOperador.add(new CategoriaOperador(id, categoria, descripcion));
		}
		return categoriasOperador;
	}

	public void addCategoriaOperador(CategoriaOperador categoriaOperador) throws SQLException, Exception {
		String sql = "INSERT INTO CATEGORIASOPERADOR VALUES (";
		sql += "ID = " + categoriaOperador.getId() + ",";
		sql += "NOMBRE= '" + categoriaOperador.getCategoria() + "',";
		sql += "DESCRIPCION = '" + categoriaOperador.getDescripcion() + "')";		

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
	}

	public void updateCategoriaOperador(CategoriaOperador categoriaOperador) throws SQLException, Exception {
		String sql = "UPDATE CATEGORIASOPERADOR SET ";
		sql += "ID = " + categoriaOperador.getId() + ",";
		sql += "NOMBRE= '" + categoriaOperador.getCategoria() + "',";
		sql += "DESCRIPCION = '" + categoriaOperador.getDescripcion() + "')";		

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
	}

	public void deleteCategoriaOperador(CategoriaOperador categoriaOperador) throws SQLException, Exception {
		String sql = "DELETE FROM CATEGORIASOPERADOR";
		sql += " WHERE ID = " + categoriaOperador.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
	}

	public CategoriaOperador buscarCategoriaOperador(long id) throws SQLException, Exception {
		String sql = "SELECT * FROM CATEGORIASOPERADOR WHERE ID  ='" + id + "'";

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		if(!rs.next())
		{
			throw new Exception ("No se encontró ninguna categoría de operador con el id = "+id);
		}
		
		String categoria = rs.getString("NOMBRE");
		String descripcion = rs.getString("DESCRIPCION");

		return new CategoriaOperador(id, categoria, descripcion);
	}
	
	public CategoriaOperador buscarCategoriaOperadorIdOperador(long id) throws SQLException, Exception{
		DAOOperador daoOperador = new DAOOperador();
		
		daoOperador.setConn(conn);
		
		Operador operador = daoOperador.buscarOperador(id);
		
		return operador.getCategoria();
	}
}
