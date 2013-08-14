package ufrr.editora.report;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

	public static Connection getConexao() {
		Connection connection = null;
		String url = "org.postgresql.Driver";
		String bd = "jdbc:postgresql://localhost:5432/editora";
		String usuario = "postgres";
		String senha = "leo123";
		try {
			Class.forName(url);
			connection = DriverManager.getConnection(bd, usuario, senha);
			return connection;
		}catch (SQLException e) {
			e.printStackTrace();
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

//	 para o servidor de teste
//	public static Connection getConexao() {
//		Connection connection = null;
//		String url = "org.postgresql.Driver";
//		String bd = "jdbc:postgresql://localhost:5432/SIS_editora";
//		String usuario = "postgres";
//		String senha = "dticsi@ufrr";
//		try {
//			Class.forName(url);
//			connection = DriverManager.getConnection(bd, usuario, senha);
//			return connection;
//		}catch (SQLException e) {
//			e.printStackTrace();
//		}catch(ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

}