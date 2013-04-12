package ufrr.editora.mb;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.Query;

import ufrr.editora.dao.DAO;
import ufrr.editora.entity.Endereco;
import ufrr.editora.entity.Perfil;
import ufrr.editora.entity.Usuario;
import ufrr.editora.util.Msg;
import ufrr.editora.util.TransformaStringMD5;

@ViewScoped
@ManagedBean
public class UsuarioBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private LoginBean login;
	
	private String senhaCriptografada;
	
	private Usuario usuario = new Usuario();
	private Perfil perfil = new Perfil();
	private Endereco endereco = new Endereco();
	private List<Perfil> perfis;
	private List<Usuario> usuarios;
	private List<Usuario> usuariosL; // Lista com Usuarios "Teste"
	private List<Usuario> usuariosE; // Lista com Usuarios "Em Espera"
	private List<Usuario> usuariosD; // Lista com Usuarios "Desativado"
	
	DAO<Usuario> dao = new DAO<Usuario>(Usuario.class);
	private Long usuarioId;
	
	public Boolean cadastro = true;
	
	public void carregaUsuario() {

		DAO<Usuario> dao = new DAO<Usuario>(Usuario.class);
		if (usuarioId != null && usuarioId != 0) {
			this.usuario = dao.buscaPorId(this.usuarioId);
		}
	}
	
	// Função para criar hash da senha informada
	public static String md5(String senha) {
		String sen = "";
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		BigInteger hash = new BigInteger(1, md.digest(senha.getBytes()));
		sen = hash.toString(16);
		return sen;
	}
	
	// AutoComplete Login
	public List<String> autocompletelogin(String nome) {
		List<Usuario> array = dao.getAllByName("login", nome);
		ArrayList<String> nomes = new ArrayList<String>();
		for (int i = 0; i < array.size(); i++) {
			nomes.add(array.get(i).getLogin());
		}
		return nomes;
	}

	// AutoComplete Nome
	public List<String> autocompletenome(String nome) {
		List<Usuario> array = dao.getAllByName("nome", nome);
		ArrayList<String> nomes = new ArrayList<String>();
		for (int i = 0; i < array.size(); i++) {
			nomes.add(array.get(i).getNome());
		}
		return nomes;
	}
	
/** Lista Usuario **/
	
	public List<Usuario> getUsuarios() {
		if (usuarios == null) {
			System.out.println("Carregando usuarios...");
			usuarios = new DAO<Usuario>(Usuario.class).getAllOrder("nome");
		}
		return usuarios;
	}
	
	// Exibe uma lista com as solicitações de acesso
	public List<Usuario> getSolicitacoes() {
		usuariosE = new ArrayList<Usuario>();
		for (Usuario u : this.getUsuarios()) {
			if (u.getStatus() == false & u.getPerfil().getPerfil().equalsIgnoreCase("solicitação")) {
				System.out.println("Total de Usuários: " + getUsuarios().size());
				usuariosE.add(u);
			}
		}
		return usuariosE;
	}
	
//	public List<Usuario> getSolicitacoes2() {
//		System.out.println("...entrou solicitação 2");
//		usuariosE = new ArrayList<Usuario>();
//		List<Usuario> usuarioss = new ArrayList<Usuario>();
//		usuarioss = this.getUsuarios();
//		for (int i = 0; i < usuarioss.size()-1; i++) {
//			if (usuarioss.get(i).getStatus()==false && usuarioss.get(i).getPerfil().getPerfil().equalsIgnoreCase("solicitação")) {
//				usuariosE.add(usuarioss.get(i));
//				usuarioss.remove(i);
//				Msg.addMsgInfo("Nova(s) solicitação(ões) existente(s)");
//			}	
//		}
//		return usuarioss;
//	}
	
	// Exibe uma lista de usuário ativados
		public List<Usuario> getTodosAtivados() {
			usuariosE = new ArrayList<Usuario>();
			for (Usuario u : this.getUsuarios()) {
				if (u.getStatus() == true) {
					System.out.println(getUsuarios().size());
					usuariosE.add(u);
				}
			}
			return usuariosE;
		}
	
	// Exibe uma lista de usuário ativados sem clientes
	public List<Usuario> getAtivados() {
		usuariosE = new ArrayList<Usuario>();
		for (Usuario u : this.getUsuarios()) {
			if (u.getStatus() == true && u.getPerfil().getId() <= 3) {
				System.out.println(getUsuarios().size());
				usuariosE.add(u);
			}
		}
		return usuariosE;
	}
	
	// Exibe uma lista de clientes ativados
	public List<Usuario> getClientes() {
		usuariosE = new ArrayList<Usuario>();
		for (Usuario u : this.getUsuarios()) {
			if (u.getPerfil().getId() == 4) {
				usuariosE.add(u);
			}
		}
		return usuariosE;
	}
	
	// Exibe uma lista de usuário ativados
		public List<Usuario> getUsuarios2() {
			usuariosE = new ArrayList<Usuario>();
			for (Usuario u : this.getUsuarios()) {
				if (u.getStatus() != true && u.getPerfil().getId()<=4) {
					usuariosE.add(u);
				}
			}
			return usuariosE;
		}
		
	// Exibe uma lista de usuário ativados != Administrador
	public List<Usuario> getAtivados2() {
		usuariosE = new ArrayList<Usuario>();
		for (Usuario u : this.getUsuarios()) {
			if (u.getStatus() == true && u.getPerfil().getId() <= 4 && u.getPerfil().getId() != 1) {
				usuariosE.add(u);
			}
		}
		return usuariosE;
	}	

		
	/** Pesquisa Usuario **/
	@SuppressWarnings("unchecked")
	public String getListaUsuariosByName() {
		if (usuario.getNome().contains("'") || usuario.getNome().contains("@")
				|| usuario.getNome().contains("/")
				|| usuario.getNome().contains("*")
				|| usuario.getNome().contains("<")
				|| usuario.getNome().contains(">")
				|| usuario.getNome().contains("#")) {

			Msg.addMsgError("Contém caracter(es) inválido(s)");
			return null;
		}
		if (usuario.getNome().length() <= 2) {
			Msg.addMsgError("Informe pelo menos 3 caracteres");
			return null;

		} else {
			usuarios = dao.getAllByName("nome", usuario.getNome());
			if (usuarios.isEmpty()) {
				Msg.addMsgInfo("Nenhum registro encontrado");
				return null;

			} else {
				System.out.println("Chegou Aqui... Processando informações...");
				try {
					Query query = dao
							.query("SELECT u FROM Usuario u WHERE u.nome LIKE ?"); 
					query.setParameter(1, usuario.getNome() + "%");
					usuariosD = query.getResultList();
					System.out.println("Usuário encontrado com sucesso...");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		}
	}
		
	// Ativar usuário (permitir acesso)
	public String ativarUsuario() {
		System.out.println(this.getUsuario().getNome());
		if (this.getUsuario().getPerfil().getId() != 5
				&& this.getUsuario().getPerfil().getId() != null) {
			this.getUsuario().setStatus(true);
			Msg.addMsgInfo("USUÁRIO: " + getUsuario().getNome()
					+ " ATIVADO COM SUCESSO");
			dao.atualiza(usuario);
			System.out.println("...Usuário ativado");
			return "/pages/usuario/autorizarAcesso.xhtml";
		} else {
			System.out.println("..Não foi possível ativar usuário");
			Msg.addMsgError("USUÁRIO: " + getUsuario().getNome()
					+ " NÃO FOI ATIVADO. TENTE NOVAMENTE");
		}
		return "/pages/usuario/autorizarAcesso.xhtml";

	}

	// Reativar usuário (exceto solicitação)
	public String reativarUsuario() {
		if (this.getUsuario().getPerfil().getId() != 5
				&& this.getUsuario().getPerfil().getId() != null) {
			this.getUsuario().setStatus(true);
			Msg.addMsgInfo("USUÁRIO: " + getUsuario().getNome()
					+ " REATIVADO COM SUCESSO");
			dao.atualiza(usuario);
			System.out.println("...Usuário Reativado");
			return "/pages/usuario/reativarAcesso.xhtml";
		} else {
			System.out.println("..Não foi possível reativar usuário");
			Msg.addMsgError("USUÁRIO: " + getUsuario().getNome()
					+ " NÃO FOI POSSÍVEL REATIVA-LO. TENTE NOVAMENTE");
		}
		return "/pages/usuario/reativarAcesso.xhtml";

	}

	// Desativar usuário
	public String desativarUsuario() {
		if (this.getUsuario().getPerfil().getId() != 5
				&& this.getUsuario().getPerfil().getId() != null) {
			this.getUsuario().setStatus(false);
			Msg.addMsgInfo("USUÁRIO: " + getUsuario().getNome() + " DESATIVADO");
			dao.atualiza(usuario);
			this.usuario = new Usuario();
			System.out.println("...Usuário desativado");
			return "/pages/usuario/desativarAcesso.xhtml";
		} else {
			System.out.println("..Não foi possível desativar usuário");
			Msg.addMsgError("USUÁRIO: " + getUsuario().getNome()
					+ " NÃO FOI DESATIVADO. TENTE NOVAMENTE");
		}
		return "/pages/usuario/desativarAcesso.xhtml";

	}
		
	// solicitacao de cadastro
	public String addPessoa() {
		for (Usuario usuarios : this.getUsuarios()) {
			if (usuarios.getCpf().equalsIgnoreCase(this.getUsuario().getCpf())
					|| usuarios.getLogin().equalsIgnoreCase(
							this.getUsuario().getLogin())) {
				this.cadastro = false;
				break;
			}
		}

		if (this.cadastro == true) {
			if (getUsuario().getSenha().equalsIgnoreCase(
					this.getUsuario().getRepetirSenha())) {
				usuario.setStatus(false);
				usuario.setEndereco(endereco);
				usuario.setSenha(TransformaStringMD5.md5(usuario.getSenha()));
				dao.adiciona(usuario);
				Msg.addMsgInfo("Solicitação de acesso enviada com sucesso. Aguarde autorização!");
				this.usuario = new Usuario();
				System.out.println("...Solicitação enviada");
				return "index.xhtml";
			} else {
				System.out.println("...Senhas diferentes");
				Msg.addMsgError("Senhas diferentes, tente de novo");
			}
		} else {
			System.out.println("...cadastro existente");
			Msg.addMsgError("Este cadastro já existe");
		}
		usuarios = dao.getAllOrder("nome");
		this.cadastro = true;
		return null;
	}

	// Cadastro de cliente (não precisa de permissão) próprio cliente
	public String addCliente() {
		for (Usuario usuarios : this.getUsuarios()) {
			if (usuarios.getCpf().equalsIgnoreCase(this.getUsuario().getCpf())
					|| usuarios.getLogin().equalsIgnoreCase(
							this.getUsuario().getLogin())) {
				this.cadastro = false;
				break;
			}
		}

		if (this.cadastro == true) {
			if (getUsuario().getTelefone1().equalsIgnoreCase(this.getUsuario().getTelefone2())) {
				Msg.addMsgError("Número de telefones não podem ser iguais, informe outro.");
				System.out.println("...erro: número de telefones iguais");
				return null;
			}
			if (getUsuario().getSenha().equalsIgnoreCase(
					this.getUsuario().getRepetirSenha())) {
				usuario.setSenha(TransformaStringMD5.md5(usuario.getSenha()));
				usuario.setStatus(true);
				usuario.setEndereco(endereco);
				dao.adiciona(usuario);
				Msg.addMsgInfo("Cadastro efetuado com sucesso");
				this.usuario = new Usuario();
				System.out.println("...cadastro de cliente efetuado com sucesso");
				return "index.xhtml";
			} else {
				System.out.println("...Senhas diferentes");
				Msg.addMsgError("Senhas diferentes, tente novamente");
			}
		} else {
			System.out.println("...cadastro existente");
			Msg.addMsgError("Este email ou CPF já está cadastrado. Se persirtir entre em contato: editora@ufrr.br ou 3621-3111");
		}
		usuarios = dao.getAllOrder("nome");
		this.cadastro = true;
		return null;
	}
	
	// Cadastro de cliente pelo funcionário da EDITORA (sem senha)
	public String addClienteIn() {
		for (Usuario usuarios : this.getUsuarios()) {
			if (usuarios.getCpf().equalsIgnoreCase(this.getUsuario().getCpf())
					|| usuarios.getLogin().equalsIgnoreCase(
							this.getUsuario().getLogin())) {
				this.cadastro = false;
				break;
			}
		}

		if (this.cadastro == true) {
			if (getUsuario().getId() == null) {
				usuario.setStatus(true);
				usuario.setEndereco(endereco);
				dao.adiciona(usuario);
				Msg.addMsgInfo("CADASTRO DO CLIENTE " + getUsuario().getNome() + " REALIZADO COM SUCESSO");
				this.endereco = new Endereco();
				this.usuario = new Usuario();
				System.out
						.println("...cadastro de cliente efetuado com sucesso");
				return "index.xhtml";
			} else {
				System.out.println("...Cadastro de cliente feito pelo funcionário não efetuado");
				Msg.addMsgError("Cadastro não efetuado, tente novamente.");
			}
		} else {
			System.out.println("...cadastro existente");
			Msg.addMsgError("Este email ou CPF já está cadastrado. Faça uma pesquisa para verificar");
		}
		usuarios = dao.getAllOrder("nome");
		this.cadastro = true;
		return null;
	}


	// atualiza perfil
	public void updatePerfil() {
		if (this.getUsuario().getPerfil().getId() != 5
				&& this.getUsuario().getPerfil().getId() != null) {
			this.getUsuario().setStatus(true);
			Msg.addMsgInfo("PERFIL DE " + getUsuario().getNome()
					+ " FOI MODIFICADO PARA " + getUsuario().getPerfil().getPerfil());
			dao.atualiza(usuario);
			System.out.println("...Perfil de usuário: " + getUsuario().getNome() + " modificado");
		} else {
			System.out.println("...Não foi possível modificar perfil");
			Msg.addMsgError("NÃO FOI POSSÍVEL EFETUAR OPERAÇÃO. TENTE NOVAMENTE");
		}
	}
	
	// update Cliente
	public String updateCliente() {
		if (usuario.getId() != null) {
			Msg.addMsgInfo("Cliente Alterado Com Sucesso");
			dao.update(usuario);
			this.usuario = new Usuario();

		}
		return "/pages/cliente/cadastrarCliente.xhtml";
	}


	
	/** Getters and Setters **/
	
	public List<Perfil> getPerfis() {
			return perfis;
		}

	public LoginBean getLogin() {
		return login;
	}

	public void setLogin(LoginBean login) {
		this.login = login;
	}

	public String getSenhaCriptografada() {
		return senhaCriptografada;
	}

	public void setSenhaCriptografada(String senhaCriptografada) {
		this.senhaCriptografada = senhaCriptografada;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public void setUsuariosE(List<Usuario> usuariosE) {
		this.usuariosE = usuariosE;
	}

	public List<Usuario> getUsuariosD() {
		return usuariosD;
	}

	public void setUsuariosD(List<Usuario> usuariosD) {
		this.usuariosD = usuariosD;
	}
	
	public DAO<Usuario> getDao() {
		return dao;
	}
	public void setDao(DAO<Usuario> dao) {
		this.dao = dao;
	}
	public Boolean getCadastro() {
		return cadastro;
	}
	public void setCadastro(Boolean cadastro) {
		this.cadastro = cadastro;
	}
	
	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	public void setPerfis(List<Perfil> perfis) {
		this.perfis = perfis;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public Long getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	public List<Usuario> getUsuariosL() {
		return usuariosL;
	}

	public void setUsuariosL(List<Usuario> usuariosL) {
		this.usuariosL = usuariosL;
	}

	public List<Usuario> getUsuariosE() {
		return usuariosE;
	}
	
}
