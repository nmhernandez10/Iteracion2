package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vos.Servicio;
import vos.CategoriaServicio;
import vos.Espacio;

public class DAOServicio {
	private ArrayList<Object> recursos;

	private Connection conn;

	public DAOServicio() {
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

	public ArrayList<Servicio> darServicios() throws SQLException, Exception {
		ArrayList<Servicio> servicios = new ArrayList<Servicio>();

		String sql = "SELECT * FROM SERVICIOS";

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			long id = Long.parseLong(rs.getString("ID"));
			DAOCategoriaServicio daoCategoriaServicio = new DAOCategoriaServicio();			
			daoCategoriaServicio.setConn(conn);		
			CategoriaServicio categoria = daoCategoriaServicio.buscarCategoriaServicio(Long.parseLong(rs.getString("IDCATEGORIA")));
			String descripcion = rs.getString("DESCRIPCION");
			double precioAdicional = Double.parseDouble(rs.getString("PRECIOADICIONAL"));
			int inicioHorario = Integer.parseInt(rs.getString("INICIOHORARIO"));
			int finHorario = Integer.parseInt(rs.getString("FINHORARIO"));
			DAOEspacio daoEspacio = new DAOEspacio();
			daoEspacio.setConn(conn);
			long espacio = Long.parseLong(rs.getString("IDESPACIO"));

			servicios.add(new Servicio(id, categoria, descripcion, precioAdicional, inicioHorario, finHorario, espacio));
		}
		
		prepStmt.close();
		
		return servicios;
	}

	public void addServicio(Servicio servicio) throws SQLException, Exception {
		String sql = "INSERT INTO SERVICIOS (id, idEspacio, idCategoria, descripcion, precioAdicional, inicioHorario, finHorario) VALUES (";
		sql += servicio.getId() + ",";
		sql += servicio.getEspacio() + ",";
		sql += servicio.getCategoria().getId() + ",'";		
		sql += servicio.getDescripcion() + "',";
		sql += servicio.getPrecioAdicional() + ",";
		sql += servicio.getInicioHorario() + ",";
		sql += servicio.getFinHorario() + ")";

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public void updateServicio(Servicio servicio) throws SQLException, Exception {
		String sql = "UPDATE SERVICIOS SET ";
		sql += "idEspacio = " + servicio.getEspacio() + ",";
		sql += "idCategoria = " + servicio.getCategoria().getId() + ",";		
		sql += "descripcion = '" + servicio.getDescripcion() + "',";
		sql += "precioAdicional = " + servicio.getPrecioAdicional() + ",";
		sql += "inicioHorario = " + servicio.getInicioHorario() + ",";
		sql += "finHorario = " + servicio.getFinHorario();
		sql += " WHERE ID = " + servicio.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
	}

	public void deleteServicio(Servicio servicio) throws SQLException, Exception {
		String sql = "DELETE FROM SERVICIOS";
		sql += " WHERE ID = " + servicio.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public Servicio buscarServicio(long id) throws SQLException, Exception {
		String sql = "SELECT * FROM SERVICIOS WHERE ID  =" + id;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		DAOCategoriaServicio daoCategoriaServicio = new DAOCategoriaServicio();
		
		daoCategoriaServicio.setConn(conn);
		
		if(!rs.next())
		{
			throw new Exception ("No se encontró ningún servicio con el id = "+id);
		}
		
		CategoriaServicio categoria = daoCategoriaServicio.buscarCategoriaServicio(Long.parseLong(rs.getString("IDCATEGORIA")));
		String descripcion = rs.getString("DESCRIPCION");
		double precioAdicional = Double.parseDouble(rs.getString("PRECIOADICIONAL"));
		int inicioHorario = Integer.parseInt(rs.getString("INICIOHORARIO"));
		int finHorario = Integer.parseInt(rs.getString("FINHORARIO"));
		
		DAOEspacio daoEspacio = new DAOEspacio();
		daoEspacio.setConn(conn);
		long espacio = Long.parseLong(rs.getString("IDESPACIO"));

		prepStmt.close();
		
		return new Servicio(id, categoria, descripcion, precioAdicional, inicioHorario, finHorario, espacio);
	}

	public List<Long> buscarServiciosIdEspacio(long id) throws SQLException, Exception {
		String sql = "SELECT * FROM SERVICIOS WHERE IDESPACIO =" + id;

		ArrayList<Long> servicios = new ArrayList<Long>();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			long idS = Long.parseLong(rs.getString("ID"));

			servicios.add(idS);
		}
		
		prepStmt.close();
		
		return servicios;

	}
}
