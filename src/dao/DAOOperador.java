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
import vos.RFC5;
import vos.RFC6;

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
			long documento;
			if(rs.getString("DOCUMENTO") == null)
			{
				documento = 0;
			}
			else
			{
				documento = Long.parseLong(rs.getString("DOCUMENTO"));
			}
			String nombre = rs.getString("NOMBRE");
			long registro;
			if(rs.getString("REGISTRO") == null)
			{
				registro = 0;
			}
			else
			{
				registro = Long.parseLong(rs.getString("REGISTRO"));
			}
			DAOCategoriaOperador daoCategoriaOperador = new DAOCategoriaOperador();			
			daoCategoriaOperador.setConn(conn);		
			CategoriaOperador categoria = daoCategoriaOperador.buscarCategoriaOperador(Long.parseLong(rs.getString("IDCATEGORIA")));			
			DAOEspacio daoEspacio = new DAOEspacio();
			daoEspacio.setConn(conn);

			List<Long> espacios = daoEspacio.buscarEspaciosIdOperador(id);

			operadores.add(new Operador(id, registro, nombre, categoria, espacios, documento));
		}
		
		prepStmt.close();
		
		return operadores;
	}

	public void addOperador(Operador operador) throws SQLException, Exception {
		String sql = "INSERT INTO OPERADORES (id, idCategoria, nombre, registro, documento) VALUES (";
		sql += operador.getId() + ",";
		sql += operador.getCategoria().getId() + ",'";
		sql += operador.getNombre() + "',";
		if(operador.getRegistro() == 0)
		{
			sql += "null,";
		}
		else
		{
			sql += operador.getRegistro() + ",";
		}
		if(operador.getDocumento() == 0)
		{
			sql += "null)";
		}
		else
		{
			sql += operador.getDocumento() + ")";
		}


		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public void updateOperador(Operador operador) throws SQLException, Exception {
		String sql = "UPDATE OPERADORES SET ";		
		sql += "nombre = '" + operador.getNombre() + "',";
		if(operador.getRegistro() == 0)
		{
			sql += "registro = null,";
		}
		else
		{
			sql += "registro = " + operador.getRegistro() + ",";
		}
		if(operador.getDocumento() == 0)
		{
			sql += "documento = null)";
		}
		else
		{
			sql += "documento = " + operador.getDocumento() + ",";
		}
		sql += "idCategoria = " + operador.getCategoria().getId();
		sql += " WHERE ID = " + operador.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
	}

	public void deleteOperador(Operador operador) throws SQLException, Exception {
		String sql = "DELETE FROM OPERADORES";
		sql += " WHERE ID = " + operador.getId();

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		prepStmt.executeQuery();
		prepStmt.close();
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

		long documento;
		if(rs.getString("DOCUMENTO") == null)
		{
			documento = 0;
		}
		else
		{
			documento = Long.parseLong(rs.getString("DOCUMENTO"));
		}
		String nombre = rs.getString("NOMBRE");
		long registro;
		if(rs.getString("REGISTRO") == null)
		{
			registro = 0;
		}
		else
		{
			registro = Long.parseLong(rs.getString("REGISTRO"));
		}
		DAOCategoriaOperador daoCategoriaOperador = new DAOCategoriaOperador();			
		daoCategoriaOperador.setConn(conn);		
		CategoriaOperador categoria = daoCategoriaOperador.buscarCategoriaOperador(Long.parseLong(rs.getString("IDCATEGORIA")));			
		DAOEspacio daoEspacio = new DAOEspacio();
		daoEspacio.setConn(conn);

		List<Long> espacios = daoEspacio.buscarEspaciosIdOperador(id);

		prepStmt.close();
		
		return new Operador(id, registro, nombre, categoria, espacios, documento);
	}


	public long buscarOperadorIdEspacio(long id) throws SQLException, Exception {
		String sql = "SELECT * FROM ESPACIOS WHERE ID =" + id ;

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		if(!rs.next())
		{
			throw new Exception ("No se encontró ningun operador con el espacio que tiene id = "+id);
		}

		Long idOperador = Long.parseLong(rs.getString("IDOPERADOR"));
		
		prepStmt.close();
		
		return idOperador;
	}

	public List<Operador> buscarOperadoresPorCategoria(String categoria) throws SQLException, Exception {
		DAOCategoriaOperador daoCatOperador= new DAOCategoriaOperador();
		daoCatOperador.setConn(conn);

		CategoriaOperador catOp= daoCatOperador.buscarCategoriaOperadorNombre(categoria);

		String sql = "SELECT * FROM OPERADORES WHERE IDCATEGORIA  =" + catOp.getId();

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		ArrayList<Operador> listaRet = new ArrayList<>();
		while(!rs.next()) {
			long id = Long.parseLong(rs.getString("ID"));
			long documento = Long.parseLong(rs.getString("DOCUMENTO"));
			String nombre = rs.getString("NOMBRE");
			long registro = Integer.parseInt(rs.getString("REGISTRO"));
			DAOCategoriaOperador daoCategoriaOperador = new DAOCategoriaOperador();			
			daoCategoriaOperador.setConn(conn);					
			DAOEspacio daoEspacio = new DAOEspacio();
			daoEspacio.setConn(conn);

			List<Long> espacios = daoEspacio.buscarEspaciosIdOperador(id);

			listaRet.add(new Operador(id, registro, nombre, catOp, espacios, documento));		
		}
		
		prepStmt.close();
		
		return listaRet;
	}

	public Operador buscarOperadorNombre(String nombre) throws SQLException, Exception {
		String sql = "SELECT * FROM OPERADORES WHERE ID  ='" + nombre +"'";

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();
		
		if(!rs.next())
		{
			throw new Exception ("No se encontró ningun operador con el nombre = "+ nombre);
		}
		
		Long id = Long.parseLong(rs.getString("ID"));
		
		prepStmt.close();
		
		return buscarOperador(id);
	}

	// RFC1

	public List<RFC1> obtenerIngresosOperadores() throws SQLException, Exception {

		String sql = "SELECT ID, CASE WHEN INGRESOS IS NULL THEN 0 ELSE INGRESOS END AS INGRESOS "+
				"FROM OPERADORES LEFT OUTER JOIN (SELECT ESPACIOS.IDOPERADOR AS IDOP, SUM(RESERVAS.PRECIO) AS INGRESOS "+
				"FROM ESPACIOS INNER JOIN RESERVAS ON RESERVAS.IDESPACIO = ESPACIOS.ID "+
				"WHERE RESERVAS.FECHAINICIO > TO_DATE('2017-01-01','YYYY-MM-DD') "+
				"GROUP BY ESPACIOS.IDOPERADOR)TABLAINGRESOS ON OPERADORES.ID = TABLAINGRESOS.IDOP "+
				"ORDER BY OPERADORES.ID";

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		List<RFC1> ingresos = new ArrayList<RFC1>();

		while (rs.next()) {
			RFC1 nuevo = new RFC1(Long.parseLong(rs.getString("ID")), Double.parseDouble(rs.getString("INGRESOS")));
			ingresos.add(nuevo);
		}

		prepStmt.close();
		
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

		prepStmt.close();
		
		return ocupaciones;
	}

	//RFC5

	public void obtenerUsosPorCategoria(List<RFC5> lista) throws SQLException, Exception
	{
		DAOCategoriaOperador daoCatOperador = new DAOCategoriaOperador();
		daoCatOperador.setConn(conn);

		DAOCategoriaServicio daoCatServicio = new DAOCategoriaServicio();
		daoCatServicio.setConn(conn);

		String sql = "SELECT CATEGORIASOPERADOR.ID, SUM(RESERVAS.DURACION) AS DIASTOTAL, SUM(RESERVAS.PRECIO) AS DINEROTOTAL "+
				"FROM OPERADORES, ESPACIOS, RESERVAS, CATEGORIASOPERADOR " +
				"WHERE OPERADORES.ID = ESPACIOS.IDOPERADOR AND ESPACIOS.ID = RESERVAS.IDESPACIO AND CATEGORIASOPERADOR.ID = OPERADORES.IDCATEGORIA "+
				"GROUP BY CATEGORIASOPERADOR.ID "+
				"ORDER BY CATEGORIASOPERADOR.ID ASC";		

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();	

		while (rs.next()) 
		{
			long id = Long.parseLong(rs.getString("ID"));
			String categoria = daoCatOperador.buscarCategoriaOperador(id).getNombre();
			int diasTotal = Integer.parseInt(rs.getString("DIASTOTAL"));
			double dineroTotal = Double.parseDouble(rs.getString("DINEROTOTAL"));

			String sqlC = "SELECT ID, IDCATEGORIA "+
					"FROM (SELECT DISTINCT CATEGORIASOPERADOR.ID, SERVICIOS.IDCATEGORIA "+
					"FROM OPERADORES, ESPACIOS, SERVICIOS, CATEGORIASOPERADOR "+
					"WHERE OPERADORES.ID = ESPACIOS.IDOPERADOR AND ESPACIOS.ID = SERVICIOS.IDESPACIO AND CATEGORIASOPERADOR.ID = OPERADORES.IDCATEGORIA "+
					"ORDER BY CATEGORIASOPERADOR.ID ASC) WHERE ID = " + id;

			System.out.println("SQL stmt:" + sqlC);
			PreparedStatement prepStmtC = conn.prepareStatement(sqlC);
			recursos.add(prepStmtC);
			ResultSet rsC = prepStmtC.executeQuery();

			List<String> servicios = new ArrayList<String>();

			while (rsC.next()) 
			{
				long idS = Long.parseLong(rsC.getString("IDCATEGORIA"));
				CategoriaServicio catServicio = daoCatServicio.buscarCategoriaServicio(idS);
				servicios.add(catServicio.getNombre());
			}

			RFC5 resultante =  new RFC5("Operador", categoria, diasTotal, dineroTotal, servicios);
			lista.add(resultante);
		}
		
		prepStmt.close();
	}

	//RFC6

	public RFC6 obtenerUsoPorUsuario(long id) throws SQLException, Exception
	{				
		DAOCategoriaServicio daoCatServicio = new DAOCategoriaServicio();
		daoCatServicio.setConn(conn);

		String sql = "SELECT OPERADORES.ID, SUM(RESERVAS.DURACION) AS DIASTOTAL, SUM(RESERVAS.PRECIO) AS DINEROTOTAL "+
				"FROM OPERADORES, ESPACIOS, RESERVAS "+
				"WHERE OPERADORES.ID = ESPACIOS.IDOPERADOR AND ESPACIOS.ID = RESERVAS.IDESPACIO AND OPERADORES.ID = " + id +
				" GROUP BY OPERADORES.ID "+
				"ORDER BY OPERADORES.ID ASC";		

		System.out.println("SQL stmt:" + sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		recursos.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();	

		if(!rs.next()) 
		{
			buscarOperador(id);
			RFC6 resultante =  new RFC6(id, "Operador", 0, 0, new ArrayList<String>());		
			return resultante;	
		}
		else
		{
			int diasTotal = Integer.parseInt(rs.getString("DIASTOTAL"));
			double dineroTotal = Double.parseDouble(rs.getString("DINEROTOTAL"));

			String sqlC = "SELECT DISTINCT OPERADORES.ID, SERVICIOS.IDCATEGORIA "+
					"FROM OPERADORES, ESPACIOS, SERVICIOS "+
					"WHERE OPERADORES.ID = ESPACIOS.IDOPERADOR AND ESPACIOS.ID = SERVICIOS.IDESPACIO AND OPERADORES.ID = "+ id +
					" ORDER BY OPERADORES.ID ASC";

			System.out.println("SQL stmt:" + sqlC);
			PreparedStatement prepStmtC = conn.prepareStatement(sqlC);
			recursos.add(prepStmtC);
			ResultSet rsC = prepStmtC.executeQuery();

			List<String> servicios = new ArrayList<String>();

			while (rsC.next()) 
			{
				long idS = Long.parseLong(rsC.getString("IDCATEGORIA"));
				CategoriaServicio catServicio = daoCatServicio.buscarCategoriaServicio(idS);
				servicios.add(catServicio.getNombre());
			}

			RFC6 resultante =  new RFC6(id, "Operador", diasTotal, dineroTotal, servicios);	
			
			prepStmt.close();
			
			return resultante;
		}		
	}
}
