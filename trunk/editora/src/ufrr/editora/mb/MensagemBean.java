package ufrr.editora.mb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import ufrr.editora.dao.DAO;
import ufrr.editora.entity.Mensagem;
import ufrr.editora.entity.Usuario;
import ufrr.editora.util.Msg;

@ManagedBean
@ViewScoped
public class MensagemBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	private Mensagem mensagem = new Mensagem();
	private Usuario usuario = new Usuario();
	private List<Mensagem> mensagens;
	private List<Mensagem> mensagensR;
	private DAO<Mensagem> dao = new DAO<Mensagem>(Mensagem.class);
	
	/** List of Messages **/
	
	public List<Mensagem> getMensagens() {
		if (mensagens == null) {
			System.out.println("Carregando usuarios...");
			mensagens = new DAO<Mensagem>(Mensagem.class).getAllDesc("id");
		}
		return mensagens;
	}
	
//	 Exibe uma lista de mensagens recebidas para o id logado
//	public List<Mensagem> getRecebidas() {
//		System.out.println("...entrou nas mensagens");
//		mensagensR = new ArrayList<Mensagem>();
//		List<Mensagem> msgs = new ArrayList<Mensagem>();
//		msgs = this.getMensagens();
//		for (int i = 0; i < msgs.size() - 1; i++) {
//			if (msgs.get(i).getDestinatario().getId().equals(this.loginBean.getUsuario().getId())) {
//				mensagensR.add(msgs.get(i));
//			}
//		}
//		return msgs;
//	}
	
	
//	 Exibe uma lista de mensagens recebidas pelo id logado
		public List<Mensagem> getRecebidas() {
			mensagensR = new ArrayList<Mensagem>();
			for (Mensagem m : this.getMensagens()) {
				if (m.getDestinatario().getId().equals(this.loginBean.getUsuario().getId())) {
					mensagensR.add(m);
					System.out.println("Mensagem para: " + getLoginBean().getUsuario().getNome());
				}
			}
			return mensagensR;
		}
		
	public void enviarMensagem() {
		if (mensagem.getDescricao().isEmpty()) {
			Msg.addMsgError("Preencha corretamente a descrição da mensagem");
			
		}
		if (mensagem.getId() == null) {
			this.getMensagem().setUsuario(this.loginBean.getUsuario());
			DAO<Usuario> userDao = new DAO<Usuario>(Usuario.class);
			Usuario user = userDao.buscaPorId(this.loginBean.getUsuario().getId());
			user.getMensagens().add(mensagem);
			Msg.addMsgInfo("Mensagem enviada para usuário " + getMensagem().getDestinatario().getNome());
			dao.adiciona(mensagem);
			this.mensagem = new Mensagem();
			
		} else {
			Msg.addMsgError("Erro ao enviar mensagem, tente novamente");
			
		}
		mensagens = dao.getAllDesc("id");
	}	

	public Mensagem getMensagem() {
		return mensagem;
	}

	public void setMensagem(Mensagem mensagem) {
		this.mensagem = mensagem;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public DAO<Mensagem> getDao() {
		return dao;
	}

	public void setDao(DAO<Mensagem> dao) {
		this.dao = dao;
	}

	public void setMensagens(List<Mensagem> mensagens) {
		this.mensagens = mensagens;
	}
	
	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}


	public List<Mensagem> getMensagensR() {
		return mensagensR;
	}


	public void setMensagensR(List<Mensagem> mensagensR) {
		this.mensagensR = mensagensR;
	}
	
}

