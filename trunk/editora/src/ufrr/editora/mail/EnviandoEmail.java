package ufrr.editora.mail;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

//Esta não é a classe utilizada para envio de email
public class EnviandoEmail {  
	  
    public void enviarEmail() {  
        SimpleEmail email = new SimpleEmail();  
        try {  
            email.setDebug(true);  
            email.setHostName("smtp.gmail.com");  
            email.setAuthentication("leoholanda23@gmail.com", "holandaarruda04");  
//            email.setSSL(true);  
            email.addTo("leoholanda23@gmail.com"); //pode ser qualquer um email  
            email.setFrom("leonardo.holanda@ufrr.br"); //aqui necessita ser o email que voce fara a autenticacao  
            email.setSubject("Teste");  
            email.setMsg("Mensagem Testando");  
            email.send();  
  
        } catch (EmailException e) {  
  
            System.out.println(e.getMessage());  
  
        }  
    }  
  
//    public void enviarEmailComArquivo() {  
//        File f = new File("caminho do arquivo");  
//  
//        EmailAttachment attachment = new EmailAttachment();  
//        attachment.setPath(f.getPath()); // Obtem o caminho do arquivo  
//        attachment.setDisposition(EmailAttachment.ATTACHMENT);  
//        attachment.setDescription("File");  
//        attachment.setName(f.getName()); // Obtem o nome do arquivo  
//  
//        try {  
//            // Create the email message  
//            MultiPartEmail email = new MultiPartEmail();  
//            email.setDebug(true);  
//            email.setHostName("smtp.gmail.com");  
//            email.setAuthentication("teunomedeusuario", "tuasenha");  
//            email.setSSL(true);  
//            email.addTo("teuemail"); //pode ser qualquer um email  
//            email.setFrom("qualemailvocequerenviar"); //aqui necessita ser o email que voce fara a autenticacao  
//            email.setSubject("The file");  
//            email.setMsg("Enviando Arquivo");  
//  
//            // add the attachment  
//            email.attach(attachment);  
//  
//            // send the email  
//            email.send();  
//        } catch (EmailException e) {  
//            e.printStackTrace();  
//        }  
//    }  
}   
