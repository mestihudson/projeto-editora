package ufrr.editora.mb;

import java.io.Serializable;
import java.util.HashMap;

import ufrr.editora.report.Report;

public class ReportBean implements Serializable {
	
	private static final long serialVersionUID = 1L;

		// relatório de clientes
		public void relarorioClientes() {
			  HashMap<String, Object> params = new HashMap<String, Object>();
			  Report report = new Report("clientes", params);
			  report.pdfReport();
		}

}
