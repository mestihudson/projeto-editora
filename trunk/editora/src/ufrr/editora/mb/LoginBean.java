package ufrr.editora.mb;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import ufrr.editora.dao.DAO;
import ufrr.editora.dao.UsuarioDAO;
import ufrr.editora.entity.Usuario;
import ufrr.editora.util.Msg;
import ufrr.editora.util.TransformaStringMD5;

@SessionScoped
@ManagedBean
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Usuario usuario = new Usuario();
	private String senhaVerifica;
	DAO<Usuario> dao = new DAO<Usuario>(Usuario.class);
	
	private String senhaCriptografada;
	
	
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public DAO<Usuario> getDao() {
		return dao;
	}
	public void setDao(DAO<Usuario> dao) {
		this.dao = dao;
	}
	
	//efetua login
	public String efetuaLogin() {
		UsuarioDAO dao = new UsuarioDAO();
		this.usuario = dao.existe(this.usuario);
		if (this.usuario != null) {
			if (this.getUsuario().getStatus() == null || this.getUsuario().getStatus().equals(false)) {
				Msg.addMsgError("Acesso não permitido");
				System.out.println("Acesso não permitido");
				return null;
			} else {
				if (this.getUsuario().getStatus().equals(true) && this.getUsuario().getPerfil().getId() == 1 ||
						this.getUsuario().getStatus().equals(true) && this.getUsuario().getPerfil().getId() == 2) {
					Msg.addMsgInfo("SEJA BEM VINDO " + getUsuario().getNome() + ". SISTEMA DE VENDAS EDITORA");
					System.out.println("usuario: " + getUsuario().getNome() + " entrou no sistema");
					return "/pages/home/home.xhtml";
					
				}else {
					Msg.addMsgInfo("SEJA BEM VINDO " + getUsuario().getNome() + ". SISTEMA DE VENDAS EDITORA");
					return "/pages/fornecedor/cadastrarFornecedor.xhtml?faces-redirect=true";
				}
			}
		} else {
			this.usuario = new Usuario();
			Msg.addMsgFatal("Senha ou Login Inválido");
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
	
	// Método para redireciona o usuario para a página inicial
		public String redirect() {
			if (this.getUsuario().getPerfil().getId() == 1) {
				return "/pages/home/home.xhtml?faces-redirect=true";
			}
			if (this.getUsuario().getPerfil().getId() == 2) {
				return "/pages/home/home.xhtml?faces-redirect=true";
			}
			if (this.getUsuario().getPerfil().getId() == 3) {
				return "homeVenda.xhtml?faces-redirect=true";
			}else {
				return "cliente.xhtml?faces-redirect=true";
			}
		}
		
	// atualiza usuário
	public String updateUsuario() {
		usuario.setSenha(TransformaStringMD5.md5(usuario.getSenha()));
		dao.atualiza(usuario);
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
	
	
}
