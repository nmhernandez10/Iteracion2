package dao;

import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import vos.Cliente;
import vos.Espacio;
import vos.Reserva;

public class DAOReserva {
	private ArrayList<Object> recursos;

	private Connection conn;

	public DAOReserva() {
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

	public ArrayList<Reserva> darReservas() throws SQLException, Exception {
		ArrayList<Reserva> reservas = new ArrayList<Reserva>();

		String sql = "SELECT * FROM RESERVAS";

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			long id = Long.parseLong(rs.getString("ID"));
			long idCliente = Long.parseLong(rs.getString("IDCLIENTE"));
			long idEspacio = Long.parseLong(rs.getString("IDESPACIO"));	
			Date fechaInicioD = rs.getDate("FECHAINICIO");
			String fechaInicio = fechaInicioD.toString();
			int duracion = Integer.parseInt(rs.getString("DURACION"));
			Date fechaReservaD = rs.getDate("FECHARESERVA");
			String fechaReserva = fechaReservaD.toString();
			double precio = Double.parseDouble(rs.getString("PRECIO"));
			boolean cancelado = false;
			if (rs.getString("CANCELADO").equals('Y')) {
				cancelado = true;
			}
			reservas.add(new Reserva(id,idCliente, idEspacio, fechaInicio, duracion, fechaReserva, cancelado, precio));
		}
		return reservas;
	}

	public void addReserva(Reserva reserva) throws SQLException, Exception
	{
		String sql = "INSERT INTO RESERVAS (id, idEspacio, idCliente, duracion, fechaInicio, fechaReserva, precio, cancelado) VALUES (";
		sql += reserva.getId() + ",";
		sql += reserva.getIdEspacio() + ",";
		sql += reserva.getIdCliente() + ",";
		sql += reserva.getDuracion() + ",";
		sql += "TO_DATE('"+ reserva.getFechaInicio() + "','YYYY-MM-DD'),";
		sql += "TO_DATE('"+ reserva.getFechaReserva() + "','YYYY-MM-DD'),";

		DAOCliente daoCliente = new DAOCliente();
		DAOEspacio daoEspacio = new DAOEspacio();
		daoCliente.setConn(conn);
		daoEspacio.setConn(conn);

		Espacio espacio = daoEspacio.buscarEspacio(reserva.getIdEspacio());

		if (!reserva.isCancelado()) {
			reserva.setPrecio(espacio.getPrecio() * reserva.getDuracion());
		}

		char cancelado = 'N';
		if (reserva.isCancelado()) {
			cancelado = 'Y';
		}
		sql += reserva.getPrecio() + ",";
		sql +="'"+cancelado + "')";
		

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
	}

	public void updateReserva(Reserva reserva) throws SQLException, Exception {
		String sql = "UPDATE RESERVAS SET ";
		sql += "idCliente =" +reserva.getIdCliente() +",";
		sql += "idEspacio =" +reserva.getIdEspacio() +",";
		sql += "duracion = " + reserva.getDuracion() + ",";
		sql += "fechaInicio = TO_DATE('"+ reserva.getFechaInicio() + "','YYYY-MM-DD'),";
		sql += "fechaReserva = TO_DATE('"+ reserva.getFechaReserva() + "','YYYY-MM-DD'),";
		DAOCliente daoCliente = new DAOCliente();
		DAOEspacio daoEspacio = new DAOEspacio();
		daoCliente.setConn(conn);
		daoEspacio.setConn(conn);

		Cliente cliente = daoCliente.buscarCliente(reserva.getIdCliente());
		Espacio espacio = daoEspacio.buscarEspacio(reserva.getIdEspacio());

		if (!reserva.isCancelado()) {
			reserva.setPrecio(espacio.getPrecio() * reserva.getDuracion());
		}

		char cancelado = 'N';
		if (reserva.isCancelado()) {
			cancelado = 'Y';
		}
		sql += "precio = " + reserva.getPrecio() + ",";
		sql += "cancelado = '" + cancelado;
		sql += "' WHERE ID = " + reserva.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
	}

	public void deleteReserva(Reserva reserva) throws SQLException, Exception {
		String sql = "DELETE FROM RESERVAS";
		sql += " WHERE ID " + reserva.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
	}
	
	public Reserva buscarReserva(long id) throws SQLException, Exception {
		String sql = "SELECT * FROM RESERVAS WHERE ID = " + id;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		if(!rs.next())
		{
			throw new Exception ("No se encontró ninguna reserva con el id = "+ id);
		}
		
		long idCliente = Long.parseLong(rs.getString("IDCLIENTE"));
		long idEspacio = Long.parseLong(rs.getString("IDESPACIO"));
		Date fechaInicioD = rs.getDate("FECHAINICIO");
		String fechaInicio = fechaInicioD.toString();
		int duracion = Integer.parseInt(rs.getString("DURACION"));
		Date fechaReservaD = rs.getDate("FECHARESERVA");
		String fechaReserva = fechaReservaD.toString();
		boolean cancelado = false;
		double precio = Double.parseDouble(rs.getString("PRECIO"));
		if (rs.getString("CANCELADO").equals("Y")) {
			cancelado = true;
		}

		return new Reserva(id, idCliente, idEspacio, fechaInicio, duracion, fechaReserva, cancelado, precio);
	}
	
	public ArrayList<Long> buscarReservasIdClienteIdEspacio(long idCliente, long idEspacio) throws SQLException, Exception {
		
		ArrayList<Long> reservas = new ArrayList<Long>();
		
		String sql = "SELECT * FROM RESERVAS WHERE IDCLIENTE = " + idCliente + " AND IDESPACIO = " + idEspacio;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		while(rs.next())
		{
			long idR = Long.parseLong(rs.getString("ID"));
			reservas.add(idR);
		}		
		
		return reservas;
	}

	public ArrayList<Long> buscarReservasIdCliente(long idCliente) throws SQLException, Exception {

		ArrayList<Long> reservas = new ArrayList<Long>();

		String sql = "SELECT * FROM RESERVAS WHERE IDCLIENTE = " + idCliente;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			long idR = Long.parseLong(rs.getString("ID"));
			reservas.add(idR);
		}
		return reservas;
	}

	public ArrayList<Long> buscarReservasIdEspacio(long idEspacio) throws SQLException, Exception {

		ArrayList<Long> reservas = new ArrayList<Long>();

		String sql = "SELECT * FROM RESERVAS WHERE IDESPACIO = " + idEspacio;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			long idR = Long.parseLong(rs.getString("ID"));

			reservas.add(idR);
		}
		return reservas;
	}
}
