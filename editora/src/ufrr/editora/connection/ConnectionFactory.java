package ufrr.editora.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionFactory {
	
//	public static Connection getConnection() {
//		try {
//			return DriverManager.getConnection("jdbc:postgresql://localhost/editora", "postgres", "leo123");
//		} catch (SQLException e) {
//			throw new RuntimeException();
//		}
//	}
	
	// para servidor de teste
	public static Connection getConnection() {
		try {
			return DriverManager.getConnection("jdbc:postgresql://172.22.10.248:8080/SIS_editora", "csi", "D515_UfRR");
		} catch (SQLException e) {
			throw new RuntimeException();
		}
	}

}
