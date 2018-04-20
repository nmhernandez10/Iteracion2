package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vos.CategoriaServicio;
import vos.Espacio;
import vos.Operador;
import vos.CategoriaOperador;
import vos.RFC1;
import vos.RFC3;

public class DAOOperador {
	private ArrayList<Object> recursos;

	private Connection conn;

	public DAOOperador() {
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

	public ArrayList<Operador> darOperadores() throws SQLException, Exception {
		ArrayList<Operador> operadores = new ArrayList<Operador>();

		String sql = "SELECT * FROM OPERADORES";

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			long id = Long.parseLong(rs.getString("ID"));
			long documento = Long.parseLong(rs.getString("DOCUMENTO"));
			String nombre = rs.getString("NOMBRE");
			long registro = Integer.parseInt(rs.getString("REGISTRO"));
			DAOCategoriaOperador daoCategoriaOperador = new DAOCategoriaOperador();			
			daoCategoriaOperador.setConn(conn);		
			CategoriaOperador categoria = daoCategoriaOperador.buscarCategoriaOperador(Long.parseLong(rs.getString("IDCATEGORIA")));			
			DAOEspacio daoEspacio = new DAOEspacio();
			daoEspacio.setConn(conn);

			List<Long> espacios = daoEspacio.buscarEspaciosIdOperador(id);

			operadores.add(new Operador(id, registro, nombre, categoria, espacios, documento));
		}
		return operadores;
	}

	public void addOperador(Operador operador) throws SQLException, Exception {
		String sql = "INSERT INTO OPERADORES (id, idCategoria, nombre, registro, documento) VALUES (";
		sql += operador.getId() + ",";
		sql += operador.getCategoria().getId() + ",'";
		sql += operador.getNombre() + "',";
		sql += operador.getRegistro() + ",";
		sql += operador.getDocumento() + ")";
		

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
	}

	public void updateOperador(Operador operador) throws SQLException, Exception {
		String sql = "UPDATE OPERADORES SET ";
		sql += "documento = " + operador.getDocumento() + ",";
		sql += "nombre = '" + operador.getNombre() + "',";
		sql += "registro = " + operador.getRegistro() + ",";
		sql += "idCategoria = " + operador.getCategoria().getId();
		sql += " WHERE ID = " + operador.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
	}

	public void deleteOperador(Operador operador) throws SQLException, Exception {
		String sql = "DELETE FROM OPERADOR";
		sql += " WHERE ID = " + operador.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
	}

	public Operador buscarOperador(long id) throws SQLException, Exception {
		String sql = "SELECT * FROM OPERADORES WHERE ID = " + id ;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		if(!rs.next())
		{
			throw new Exception ("No se encontró ningún operador con el id = "+id);
		}
		
		long documento = Long.parseLong(rs.getString("DOCUMENTO"));
		String nombre = rs.getString("NOMBRE");
		long registro = Long.parseLong(rs.getString("REGISTRO"));
		DAOCategoriaOperador daoCategoriaOperador = new DAOCategoriaOperador();			
		daoCategoriaOperador.setConn(conn);		
		CategoriaOperador categoria = daoCategoriaOperador.buscarCategoriaOperador(Long.parseLong(rs.getString("IDCATEGORIA")));			
		DAOEspacio daoEspacio = new DAOEspacio();
		daoEspacio.setConn(conn);

		List<Long> espacios = daoEspacio.buscarEspaciosIdOperador(id);

		return new Operador(id, registro, nombre, categoria, espacios, documento);
	}

	public long buscarOperadorIdEspacio(long id) throws SQLException, Exception {
		String sql = "SELECT * FROM ESPACIOS WHERE ID  =" + id ;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		if(!rs.next())
		{
			throw new Exception ("No se encontró ningun operador con el espacio que tiene id = "+id);
		}
		
		Long idOperador = Long.parseLong(rs.getString("IDOPERADOR"));
		return idOperador;
	}

	// RFC1

	public List<RFC1> obtenerIngresosOperadores() throws SQLException, Exception {

		String sql = "SELECT ESPACIOS.IDOPERADOR AS ID, SUM(RESERVAS.PRECIO) AS INGRESOS FROM RESERVAS, ESPACIOS WHERE RESERVAS.IDESPACIO = ESPACIOS.ID AND RESERVAS.FECHAINICIO < "
				+ " TO_DATE('01-01-2017','DD-MM-YYYY')" + " GROUP BY ESPACIOS.IDOPERADOR";

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		List<RFC1> ingresos = new ArrayList<RFC1>();

		while (rs.next()) {
			RFC1 nuevo = new RFC1(Long.parseLong(rs.getString("ID")), Double.parseDouble(rs.getString("INGRESOS")));
			ingresos.add(nuevo);
		}

		return ingresos;
	}
	
	//RFC3
	
	public List<RFC3> obtenerOcupacionOperadores() throws SQLException, Exception
	{
		
		String sql = "SELECT OPERADORES.ID AS IDOPERADOR , CASE WHEN TABLACOMPARATIVA.SUMCONT/TABLACOMPARATIVA.SUMTOT IS NULL THEN 0 ELSE TABLACOMPARATIVA.SUMCONT/TABLACOMPARATIVA.SUMTOT END AS INDOCUPACION " +
				"FROM OPERADORES LEFT OUTER JOIN "+
				"(SELECT ESPACIOS.IDOPERADOR AS ID, SUM(TABLACONTEOCEROS.CONTEO) AS SUMCONT, SUM(TABLATOTAL.TOTAL) AS SUMTOT "+
				"FROM ESPACIOS,(SELECT ESPACIOS.ID, TABLACONTEO.CONTEO "+
				"FROM ESPACIOS LEFT OUTER JOIN (SELECT idEspacio, COUNT(id) AS CONTEO "+
				"FROM RESERVAS "+
				"WHERE fechaInicio < sysdate AND (fechaInicio + duracion) > sysdate "+
				"GROUP BY idEspacio) TABLACONTEO ON ESPACIOS.ID = TABLACONTEO.IDESPACIO) TABLACONTEOCEROS, ("+
				"SELECT idEspacio, COUNT(id) AS TOTAL "+
				"FROM RESERVAS "+
				"GROUP BY idEspacio) TABLATOTAL WHERE ESPACIOS.ID = TABLACONTEOCEROS.id AND TABLACONTEOCEROS.id = TABLATOTAL.idEspacio "+
				"GROUP BY idOperador) TABLACOMPARATIVA ON OPERADORES.ID = TABLACOMPARATIVA.ID "+
				"ORDER BY OPERADORES.ID ASC";
		
		System.out.println("SQL stmt:" + sql);
		
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		List<RFC3> ocupaciones = new ArrayList<RFC3>();

		while (rs.next()) {
			RFC3 nuevo = new RFC3(Long.parseLong(rs.getString("IDOPERADOR")), Double.parseDouble(rs.getString("INDOCUPACION")));
			ocupaciones.add(nuevo);
		}

		return ocupaciones;
	}
}
