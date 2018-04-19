package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vos.Cliente;
import vos.Reserva;
import vos.Vinculo;

public class DAOCliente {
	private ArrayList<Object> recursos;

	private Connection conn;

	public DAOCliente() {
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

	public ArrayList<Cliente> darClientes() throws SQLException, Exception {
		ArrayList<Cliente> clientes = new ArrayList<Cliente>();

		String sql = "SELECT * FROM CLIENTES";

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			long id = Long.parseLong(rs.getString("ID"));
			long identificacion = Long.parseLong(rs.getString("DOCUMENTO"));
			String nombre = rs.getString("NOMBRE");
			int edad = Integer.parseInt(rs.getString("EDAD"));
			String direccion = rs.getString("DIRECCION");
			DAOVinculo daoVinculo = new DAOVinculo();			
			daoVinculo.setConn(conn);		
			Vinculo vinculo= daoVinculo.buscarVinculo(Long.parseLong(rs.getString("IDVINCULO")));	
			DAOReserva daoReserva = new DAOReserva();
			daoReserva.setConn(conn);

			List<Long> reservas = daoReserva.buscarReservasIdCliente(id);

			clientes.add(new Cliente(id, identificacion, nombre, edad, direccion, vinculo, reservas));
		}
		return clientes;
	}

	public void addCliente(Cliente cliente) throws SQLException, Exception {
		String sql = "INSERT INTO CLIENTES (id, idVinculo, documento, nombre, edad, direccion) VALUES (";
		sql += cliente.getId() + ",";
		sql += cliente.getVinculo().getId() + ",";
		sql += cliente.getIdentificacion() + ",'";
		sql += cliente.getNombre() + "',";		
		sql += cliente.getEdad() + ",'";
		sql += cliente.getDireccion() + "')";		

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
	}

	public void updateCliente(Cliente cliente) throws SQLException, Exception {
		String sql = "UPDATE CLIENTES SET ";
		sql += "idVinculo = " + cliente.getVinculo().getId() + ",";
		sql += "documento = " + cliente.getIdentificacion() + ",";
		sql += "nombre = '" + cliente.getNombre() + "',";		
		sql += "edad = " + cliente.getEdad() + ",";
		sql += "direccion = '" + cliente.getDireccion();
		sql += "' WHERE ID = " + cliente.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
	}

	public void deleteCliente(Cliente cliente) throws SQLException, Exception {
		String sql = "DELETE FROM CLIENTES";
		sql += " WHERE ID = " + cliente.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
	}

	public Cliente buscarCliente(long id) throws SQLException, Exception {
		String sql = "SELECT * FROM CLIENTES WHERE ID  =" + id;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		if(!rs.next())
		{
			throw new Exception ("No se encontró ningún cliente con el id = "+id);
		}
		
		long identificacion = Long.parseLong(rs.getString("DOCUMENTO"));
		String nombre = rs.getString("NOMBRE");
		int edad = Integer.parseInt(rs.getString("EDAD"));
		String direccion = rs.getString("DIRECCION");
		DAOVinculo daoVinculo = new DAOVinculo();			
		daoVinculo.setConn(conn);		
		Vinculo vinculo= daoVinculo.buscarVinculo(Long.parseLong(rs.getString("IDVINCULO")));			

		DAOReserva daoReserva = new DAOReserva();
		daoReserva.setConn(conn);

		List<Long> reservas = daoReserva.buscarReservasIdCliente(id);

		return new Cliente(id, identificacion, nombre, edad, direccion, vinculo, reservas);
	}

	public Cliente buscarClienteIdReserva(long id) throws SQLException, Exception {
		String sql = "SELECT * FROM RESERVAS WHERE ID  =" + id ;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		if(!rs.next())
		{
			return null;
		}
		
		Long idCliente = Long.parseLong(rs.getString("IDCLIENTE"));
		return buscarCliente(idCliente);
	}
}
