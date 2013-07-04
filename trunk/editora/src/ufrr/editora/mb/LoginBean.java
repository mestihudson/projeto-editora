package ufrr.editora.mb;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import ufrr.editora.dao.DAO;
import ufrr.editora.dao.UsuarioDAO;
import ufrr.editora.entity.Usuario;
import ufrr.editora.entity.Venda;
import ufrr.editora.util.Msg;
import ufrr.editora.util.TransformaStringMD5;

@SessionScoped
@ManagedBean
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Venda venda = new Venda();
	private Usuario usuario = new Usuario();
	private String senhaVerifica;
	DAO<Usuario> dao2 = new DAO<Usuario>(Usuario.class);
	
	private String senhaCriptografada;
	
	
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public DAO<Usuario> getDao() {
		return dao2;
	}
	public void setDao(DAO<Usuario> dao) {
		this.dao2 = dao;
	}
	
	public LoginBean() {
		@SuppressWarnings("unused")
		Usuario usuario = new Usuario();
	}
	
	// efetua login
	public String efetuaLogin() {
		UsuarioDAO dao = new UsuarioDAO();
		this.usuario = dao.existe(this.usuario);
		if (this.usuario != null) {
			if (this.getUsuario().getStatus() == null
					|| this.getUsuario().getStatus().equals(false)) {
				Msg.addMsgError("Aguarde liberação do acesso");
				System.out.println("...acesso não permitido, aguarde liberar o acesso");
				this.usuario = new Usuario();
				return null;
			} else {

				if (this.getUsuario().getStatus().equals(true)
						&& this.getUsuario().getPerfil().getId() == 1
						|| this.getUsuario().getStatus().equals(true)
						&& this.getUsuario().getPerfil().getId() == 2) {
					Msg.addMsgInfo("SEJA BEM VINDO " + getUsuario().getNome()
							+ ". SISTEMA DE VENDAS EDITORA");
					System.out.println("usuario: " + getUsuario().getNome()
							+ "\n" + " entrou no sistema");
					usuario.setEsqueciSenha(false);
					dao2.atualiza(usuario);
					return "/pages/home/home.xhtml";
				}

				if (this.getUsuario().getStatus().equals(true)
						&& this.getUsuario().getPerfil().getId() == 3) {
					Msg.addMsgInfo("SEJA BEM VINDO " + getUsuario().getNome()
							+ ". SISTEMA DE VENDAS EDITORA");
					System.out.println("usuario: " + getUsuario().getNome()
							+ "\n" + " entrou no sistema");
					usuario.setEsqueciSenha(true);
					dao2.atualiza(usuario);
					return "/pages/home/home.xhtml";

				} else {
					Msg.addMsgInfo("SEJA BEM VINDO " + getUsuario().getNome()
							+ ". SISTEMA DE VENDAS EDITORA");
					return "/pages/home/home.xhtml";
				}

			}
		} else {
			Msg.addMsgFatal("Senha ou login inválido");
			System.out.println("...senha ou login invalido");
			this.usuario = new Usuario();
			return null;
		}
	}
	
	// solicitação de Senha
	public String esqueceuSenha() {
		UsuarioDAO dao = new UsuarioDAO();
		this.usuario = dao.trocaSenha(this.usuario);
		if (this.usuario != null) {
			if (this.getUsuario().getId() == null
					|| this.getUsuario().getStatus().equals(false)) {
				Msg.addMsgError("Usuário não encontrado");
				System.out
						.println("...Usuário não existe ou ainda não foi ativado para pedir solicitação de senha");
				return null;
			} else {
				if (this.getUsuario().getStatus().equals(true)) {
					System.out.println("...usuario: " + getUsuario().getNome()
							+ " entrou para solicitação de senha");
					usuario.setEsqueciSenha(true);
					dao2.atualiza(usuario);
					return "/pages/usuario/dados.xhtml?faces-redirect=true";

				} else {
					System.out.println("...Ocorreu um erro ao tentar recuperar a senha");
					return null;
				}
			}
		} else {
			System.out.println("...Digite corretamente as informações para recuperar seu acesso");
			Msg.addMsgFatal("Registro não encontrado."
					+ " Digite seu CPF corretamente"
					+ " caso persistir o erro, entre em contato com a Editora UFRR");
			this.usuario = new Usuario();
			return null;
		}
	}
		
	// acesso após solicitação do esqueceuSenha
	public String esqueceuSenha2() {
		UsuarioDAO dao = new UsuarioDAO();
		this.usuario = dao.senhaCriptografada(this.usuario);
		if (this.usuario != null) {
			if (this.getUsuario().getStatus().equals(true)) {
				System.out.println("...Troca de senha");
				return "/pages/usuario/trocarSenha.xhtml";
			} else {
				System.out.println("...Ocorreu um erro ao tentar trocar a senha");
				return null;
			}
		} else {
			System.out.println("...Digite corretamente as informações para recuperar seu acesso");
			Msg.addMsgFatal("Digite a senha correta conforme enviada para seu email");
			this.usuario = new Usuario();
			return null;
		}
	}
		
	
	//autorização para alterar dados do cadastro
	public String updateLogin() {
		setSenhaVerifica(TransformaStringMD5.md5(getSenhaVerifica()));
		if (getSenhaVerifica().equalsIgnoreCase(LoginBean.this.usuario.getSenha())) {	
				if (this.getUsuario().getSenha().isEmpty()) {
					Msg.addMsgInfo("Informe sua senha para atualizar seus dados");
					
				}else {
					return "/pages/usuario/atualizarCadastro.xhtml?faces-redirect=true";
				}
			}else {	
			Msg.addMsgFatal("Senha inválida");
			System.out.println("Chegou aqui...");
			System.out.println(LoginBean.this.getUsuario().getLogin());
			return "/pages/usuario/senhaCadastro.xhtml";		
			
			}
		return null;
		}
	
	//autorização para alterar dados do cadastro
		public String trocaSenha() {
			if (this.usuario.getSenha().equalsIgnoreCase(this.usuario.getRepetirSenha())) {
				usuario.setSenha(TransformaStringMD5.md5(usuario.getSenha()));
				dao2.atualiza(usuario);
				Msg.addMsgInfo("Operação executada com sucesso");
				System.out.println("...Senha alterada depois de solicita-la por email");
				
				FacesContext facesContext = FacesContext.getCurrentInstance();
				HttpSession session = (HttpSession) facesContext.getExternalContext()
						.getSession(false);
				session.invalidate();
				System.out.println("Saiu do Sistema para atualizar senha");
				return "/index.xhtml";	
			}else {
				Msg.addMsgError("Senha incorreta"); 
			}
			return null;
			}
	
	// Método para redireciona o usuario para a página inicial
		public String redirect() {
			if (this.getUsuario().getPerfil().getId() == 1) {
				return "/pages/home/home.xhtml?faces-redirect=true";
			}
			if (this.getUsuario().getPerfil().getId() == 2) {
				return "/pages/home/home.xhtml?faces-redirect=true";
			}
			if (this.getUsuario().getPerfil().getId() == 3) {
				return "/pages/home/home.xhtml?faces-redirect=true";
			}else {
				return "cliente.xhtml?faces-redirect=true";
			}
		}
		
	// atualiza usuário
	public String updateUsuario() {
		usuario.setSenha(TransformaStringMD5.md5(usuario.getSenha()));
		dao2.atualiza(usuario);
		Msg.addMsgInfo("Cadastro atualizado com sucesso");
		System.out.println("...Cadastro atualizado");
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext()
				.getSession(false);
		session.invalidate();
		System.out.println("Saiu do Sistema");
		return "/index.xhtml";
	}
	
	// Sair do sistema
	public String sair() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext()
				.getSession(false);
		session.invalidate();
		System.out.println("usuario: " + getUsuario().getNome() + " saiu do sistema");
		return "/index.xhtml?faces-redirect=true";
	}
	
	// Sair da recuperação de senha
	public String sair2() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext()
				.getSession(false);
		session.invalidate();
		System.out.println("usuario: " + getUsuario().getNome()
				+ " desistiu de recuperar a senha");
		return "/index.xhtml?faces-redirect=true";
	}
	
	// Derrubar usuario
	public String derrubar() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(false);
		session.getId().equals(getUsuario().getId());
		session.invalidate();
		System.out.println("usuario: " + getUsuario().getNome() + " foi derrubado");
		return "/index.xhtml?faces-redirect=true";
	}
	
	public String solicitacaoCadastro() {
		return "/solicitacao.xhtml?faces-redirect=true";
	}
	
	
	public boolean isLogado() {
		return usuario.getLogin() != null;
	}
	public String getSenhaVerifica() {
		return senhaVerifica;
	}
	public void setSenhaVerifica(String senhaVerifica) {
		this.senhaVerifica = senhaVerifica;
	}
	public String getSenhaCriptografada() {
		return senhaCriptografada;
	}
	public void setSenhaCriptografada(String senhaCriptografada) {
		this.senhaCriptografada = senhaCriptografada;
	}
	public Venda getVenda() {
		return venda;
	}
	public void setVenda(Venda venda) {
		this.venda = venda;
	}
	public DAO<Usuario> getDao2() {
		return dao2;
	}
	public void setDao2(DAO<Usuario> dao2) {
		this.dao2 = dao2;
	}
	
	
	
	
}
