package ufrr.editora.mb;

import java.io.Serializable;
import java.util.HashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import ufrr.editora.report.Report;

@ManagedBean
@SessionScoped
public class ReportBean implements Serializable {

	private static final long serialVersionUID = 1L;

	// relatório de clientes (todos)
	public void relatorioClientes() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		Report report = new Report("Relatorio-Clientes", params);
		report.pdfReport();
		System.out
				.println("...solicitacao do relatorio de todos os clientes cadastrados");
	}

	// relatório de clientes por categoria
	public void relatorioCategoria() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		Report report = new Report("Relatorio-Categoria", params);
		params.put("categoria", 1);
		report.pdfReport();
		System.out
				.println("...solicitacao do relatorio dos clientes por categoria");
	}

	// relatório de fornecedores (todos)
	public void relatorioFornecedores() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		Report report = new Report("Relatorio-Fornecedores", params);
		report.pdfReport();
		System.out
				.println("...solicitacao do relatorio de todos os fornecedores cadastrados");
	}

}
