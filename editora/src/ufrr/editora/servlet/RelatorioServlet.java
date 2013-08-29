package ufrr.editora.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ufrr.editora.connection.ConnectionFactory;
import ufrr.editora.gerador.GeradorRelatorio;

@WebServlet("/relatorio")
public class RelatorioServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {

			String nome = request.getServletContext().getRealPath("/WEB-INF/jasper/Relatorio-Clientes-Categoria.jasper");
			new ConnectionFactory();
			Connection connection = ConnectionFactory.getConnection();
			
			String paramCategoria = request.getParameter("param_categoria");
//			String dataIni = request.getParameter("data_ini");
//			String dataFim = request.getParameter("data_fim");
			
//			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						
//			Date dataInicial = sdf.parse(dataIni);
//			Date dataFinal = sdf.parse(dataFim);
			
			Map<String, Object> parametros = new HashMap<String, Object>();
//			parametros.put("DATA_INI", dataInicial);
//			parametros.put("DATA_FIM", dataFinal);
			parametros.put("CATEGORIA", paramCategoria);
			
			GeradorRelatorio gerador = new GeradorRelatorio(nome, parametros, connection);
			gerador.geraPDFParaOutPutStream(response.getOutputStream());	

			connection.close();
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}
}
