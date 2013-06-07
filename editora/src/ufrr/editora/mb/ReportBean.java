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

		// relatório de clientes
		public void relatorioClientes() {
			  HashMap<String, Object> params = new HashMap<String, Object>();
			  Report report = new Report("relatorio-clientes", params);
			  report.pdfReport();
			  System.out.println("...solicitacao do relatorio de todos os clientes cadastrados");
		}

}
