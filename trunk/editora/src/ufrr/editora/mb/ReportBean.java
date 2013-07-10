package ufrr.editora.mb;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import ufrr.editora.report.Report;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;

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
	
	// relatório de produtos registrados (todos)
		public void relatorioProdutos() {
			HashMap<String, Object> params = new HashMap<String, Object>();
			Report report = new Report("Relatorio-Produtos", params);
			report.pdfReport();
			System.out.println("...solicitacao do relatorio de todos os produtos cadastrados");
	}
		
	// Gerar relatório das vendas dos clientes pela consulta		  
		public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {
		    Document pdf = (Document) document;  
		    pdf.open();  
		    pdf.setPageSize(PageSize.A4);
		  
		    ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();  
		    String logo = servletContext.getRealPath("") + File.separator + "resources" + File.separator + "img" + File.separator + "logoeditorarelatorio.png";  
		  
		    pdf.add(Image.getInstance(logo));  
		}	

}
