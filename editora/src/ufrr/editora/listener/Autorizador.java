package ufrr.editora.listener;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import ufrr.editora.mb.LoginBean;

public class Autorizador implements PhaseListener {

	private static final long serialVersionUID = 1L;
	
	public void beforePhase(PhaseEvent event) {
		
	}
	
	public void afterPhase(PhaseEvent event) {
		FacesContext context = event.getFacesContext();
		
		if ("/index.xhtml".equals(context.getViewRoot().getViewId())) {
			return;
		}
		
		// Obtendo LoginBean da Sess�o
		LoginBean loginBean = context.getApplication().evaluateExpressionGet(context, "#{loginBean}", LoginBean.class);
		
		if (!loginBean.isLogado()) {
			NavigationHandler handler = context.getApplication().getNavigationHandler();
			handler.handleNavigation(context, null, "/index.xhtml?faces-redirect=true");
			context.renderResponse();
			
			//Efetua a Renderiza��o da tela
			context.renderResponse();
		}
	}
	
	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}
}