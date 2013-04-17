package ufrr.editora.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

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
		public static void recuperaSenha(ufrr.editora.entity.Usuario usuario) throws EmailException {
		 Email email2 = new SimpleEmail();
		 email2 = conectaEmail();
		 email2.setSubject("Recuperação de acesso site Editora UFRR");
		 email2.setMsg("Senha: " + usuario.getSenha());
		 email2.addTo(usuario.getLogin()); //Corrigir problema aqui...
		 String resposta = email2.send();
		 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "E-mail enviado com sucesso para: " + usuario.getLogin(), "Informação"));
		 }

}
