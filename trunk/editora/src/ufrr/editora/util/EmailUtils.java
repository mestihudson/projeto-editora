package ufrr.editora.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import ufrr.editora.mb.LoginBean;

public class EmailUtils {
	 private static final String HOSTNAME = "smtp.gmail.com";
	 private static final String USERNAME = "leoholanda23";
	 private static final String PASSWORD = "holandaarruda04";
	 private static final String EMAILORIGEM = "leoholanda23@gmail.com";
	 
	 @SuppressWarnings("deprecation")
	public static Email conectaEmail() throws EmailException {
	 Email email2 = new SimpleEmail();
	 email2.setHostName(HOSTNAME);
	 email2.setSmtpPort(587);
	 email2.setAuthenticator(new DefaultAuthenticator(USERNAME, PASSWORD));
	 email2.setTLS(true);
	 email2.setFrom(EMAILORIGEM);
	 return email2;
	 }
	 
	 @SuppressWarnings("unused")
	public static void enviaEmail(ufrr.editora.entity.EnviaEmail email) throws EmailException {
	 Email email2 = new SimpleEmail();
	 email2 = conectaEmail();
	 email2.setSubject(email.getTitulo());
	 email2.setMsg(email.getMensagem());
	 email2.addTo(email.getDestino().getLogin());
	 String resposta = email2.send();
	 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "E-mail enviado com sucesso para: " + email.getDestino().getLogin(), "Informação"));
	 }
	 
	 @SuppressWarnings("unused")
		public static void recuperaSenha(LoginBean usuario) throws EmailException {
		 Email email2 = new SimpleEmail();
		 email2 = conectaEmail();
		 email2.setSubject("Recuperação de senha Editora UFRR");
		 email2.setMsg("Editora UFRR" + "\n" + "siga as instruções abaixa para prosseguir com a solicitação" + "\n" + "\n" + "Senha: " + usuario.getUsuario().getSenha() + "\n" + "\n"
		 + "Clique no link e copie a senha informada: " + "http://172.22.10.110:8081/editora/esqueceuSenha.jsf?faces-redirect=true");
		 email2.addTo(usuario.getUsuario().getLogin()); 
		 String resposta = email2.send();
		 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "E-mail enviado com sucesso para: " + usuario.getUsuario().getLogin(), "Informação"));
		 }

}
