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

@SessionScoped
@ManagedBean
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Usuario usuario = new Usuario();
	DAO<Usuario> dao = new DAO<Usuario>(Usuario.class);
	
	
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
	
	public String efetuaLogin() {
		UsuarioDAO dao = new UsuarioDAO();
		this.usuario = dao.existe(this.usuario);
		if (this.usuario != null) {
			if (this.getUsuario().getSenha().equalsIgnoreCase("123")) {
				return "usuarioTrocaSenha.xhtml?faces-redirect=true";
			} else {
				if (this.getUsuario().getStatus().equals(true) && this.getUsuario().getPerfil().getId() == 1 ||
						this.getUsuario().getStatus().equals(true) && this.getUsuario().getPerfil().getId() == 2) {
//					Msg.addMsgInfo("Seja Bem Vindo  " + getUsuario().getPessoa().getNome() + ". Sistema de Vendas Editora");
					return "_template.xhtml?faces-redirect=true";
					
				}else {
					Msg.addMsgInfo("Seja Bem Vindo  " + getUsuario().getPessoa().getNome() + ". Sistema de Vendas Editora");
					return "/pages/fornecedor/cadastrarFornecedor.xhtml?faces-redirect=true";
				}
			}
		} else {
			this.usuario = new Usuario();
			Msg.addMsgFatal("Senha ou Login Inválido");
			return null;
		}
	}
	
	// Método para redireciona o usuario para a página inicial
		public String redireciona() {
			if (this.getUsuario().getPerfil().getId().equals(1)) {
				return "_template.jsf?faces-redirect=true";
			}
			if (this.getUsuario().getPerfil().getId().equals(2)) {
				return "home.xhtml?faces-redirect=true";
			}
			if (this.getUsuario().getPerfil().getId().equals(3)) {
				return "homeVenda.xhtml?faces-redirect=true";
			}else {
				return "cliente.xhtml?faces-redirect=true";
			}
		}
	
//	Sair do sistem
	public String sair() {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) facesContext.getExternalContext()
				.getSession(false);
		session.invalidate();
		System.out.println("Saiu do Sistema");
		return "/index.xhtml?faces-redirect=true";
	}
	
	
	public boolean isLogado() {
		return usuario.getLogin() != null;
	}
	
}
