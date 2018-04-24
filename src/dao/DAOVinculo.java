package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import vos.Vinculo;
import vos.CategoriaServicio;
import vos.Cliente;

public class DAOVinculo 
{
	private ArrayList<Object> recursos;

	private Connection conn;

	public DAOVinculo() {
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
	
	public ArrayList<Vinculo> darVinculos() throws SQLException, Exception {
		ArrayList<Vinculo> categoriasCliente = new ArrayList<Vinculo>();

		String sql = "SELECT * FROM VINCULOS";

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			long id = Long.parseLong(rs.getString("ID"));
			String categoria = rs.getString("CATEGORIA");
			String descripcion = rs.getString("DESCRIPCION");

			categoriasCliente.add(new Vinculo(id, categoria, descripcion));
		}
		
		prepStmt.close();
		
		return categoriasCliente;
	}

	public void addVinculo(Vinculo vinculo) throws SQLException, Exception {
		String sql = "INSERT INTO VINCULOS VALUES (";
		sql += "ID = " + vinculo.getId() + ",";
		sql += "NOMBRE= " + vinculo.getNombre() + ",";
		sql += "DESCRIPCION = " + vinculo.getDescripcion() + ")";		

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public void updateVinculo(Vinculo vinculo) throws SQLException, Exception {
		String sql = "UPDATE VINCULOS SET ";
		sql += "ID = " + vinculo.getId() + ",";
		sql += "NOMBRE= '" + vinculo.getNombre() + "',";
		sql += "DESCRIPCION = '" + vinculo.getDescripcion() + "')";		

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public void deleteVinculo(Vinculo vinculo) throws SQLException, Exception {
		String sql = "DELETE FROM VINCULOS";
		sql += " WHERE ID = " + vinculo.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public Vinculo buscarVinculo(long id) throws SQLException, Exception {
		String sql = "SELECT * FROM VINCULOS WHERE ID  =" + id;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		if(!rs.next())
		{
			throw new Exception ("No se encontró ningún vínculo con el id = "+id);
		}
		
		String categoria = rs.getString("NOMBRE");
		String descripcion = rs.getString("DESCRIPCION");
		
		prepStmt.close();

		return new Vinculo(id, categoria, descripcion);
	}
	
	public Vinculo buscarVinculoIdCliente(long id) throws SQLException, Exception{
		DAOCliente daoCliente = new DAOCliente();
		
		daoCliente.setConn(conn);
		
		Cliente cliente = daoCliente.buscarCliente(id);
		
		return cliente.getVinculo();
	}
	
	public Vinculo buscarVinculoNombre(String nombre) throws SQLException, Exception 
	{
		String sql = "SELECT * FROM VINCULOS WHERE NOMBRE ='" + nombre + "'";

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		if(!rs.next())
		{
			throw new Exception ("No se encontró ningún vínculo (con Uniandes) con el nombre '"+ nombre +"'");
		}
		
		long id = Long.parseLong(rs.getString("ID"));
		String descripcion = rs.getString("DESCRIPCION");
		
		prepStmt.close();

		return new Vinculo(id, nombre, descripcion);
	}
}
