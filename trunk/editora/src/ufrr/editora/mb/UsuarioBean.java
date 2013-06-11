package ufrr.editora.mb;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ufrr.editora.dao.DAO;
import ufrr.editora.entity.Endereco;
import ufrr.editora.entity.Perfil;
import ufrr.editora.entity.Usuario;
import ufrr.editora.util.EmailUtils;
import ufrr.editora.util.Msg;
import ufrr.editora.util.TransformaStringMD5;
import ufrr.editora.validator.Validator;

@ViewScoped
@ManagedBean
public class UsuarioBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{loginBean}")
	private LoginBean login;

	private EntityManager entityManager;

	private String senhaCriptografada;

	private Usuario usuario = new Usuario();
	private Perfil perfil = new Perfil();
	private Endereco endereco = new Endereco();
	private List<Perfil> perfis;
	private List<Usuario> usuarios;
	private List<Usuario> usuariosE; // Lista em branco
	private Validator<Usuario> validator;
	private String search;
	private Integer box4Search;

	DAO<Usuario> dao = new DAO<Usuario>(Usuario.class);
	private Long usuarioId;

	public Boolean cadastro = true;

	@PostConstruct
	public void init() {

		usuario = new Usuario();
		endereco = new Endereco();
		validator = new Validator<Usuario>(Usuario.class);
		search = "";
		box4Search = 1;
		box4Search = 2;
		box4Search = 3;
	}
	
	/** Função para criar hash da senha informada **/
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

	public void carregaUsuario() {

		DAO<Usuario> dao = new DAO<Usuario>(Usuario.class);
		if (usuarioId != null && usuarioId != 0) {
			this.usuario = dao.buscaPorId(this.usuarioId);
		}
	}
	
	/** AutoComplets **/

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
	
	// AutoComplete clientes
	public List<String> autocompleteClientes(String nome) {
		List<Usuario> array = dao.getAllByName("nome", nome);
		ArrayList<String> nomes = new ArrayList<String>();
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i).getPerfil().getId() == 4) {
				nomes.add(array.get(i).getNome());
			}
		}
		return nomes;
	}
	
	// AutoComplete clientes
	public List<String> autocompleteVendedor(String nome) {
		List<Usuario> array = dao.getAllByName("nome", nome);
		ArrayList<String> nomes = new ArrayList<String>();
		for (int i = 0; i < array.size(); i++) {
			if (array.get(i).getPerfil().getId() <= 3
					&& array.get(i).getId() != 1) {
				nomes.add(array.get(i).getNome());
			}
		}
		return nomes;
	}

	// AutoComplete Nome e CPF juntos (somente para Cliente)
	public List<String> autocomplete(String nome) {
		ArrayList<String> nomes = new ArrayList<String>();
		if (box4Search.equals(1)) {
			if (!search.contains("'")) {
				List<Usuario> array = dao.getAllByName("nome", nome);
				for (int i = 0; i < array.size(); i++) {
					if (array.get(i).getPerfil().getId() == 4 && array.get(i).getId() != 1) {
						nomes.add(array.get(i).getNome());
					}
				}
			}
		} else if (box4Search.equals(2)) {
			if (!search.contains("'")) {
				List<Usuario> array = dao.getAllByName("cpf", nome);
				for (int i = 0; i < array.size(); i++) {
					if (array.get(i).getPerfil().getId() == 4 && array.get(i).getId() != 1) {
					nomes.add(array.get(i).getCpf());
					}
				}
			}
		}
		return nomes;
	}

	/** List of Users **/

	public List<Usuario> getUsuarios() {
		if (usuarios == null) {
			System.out.println("Carregando usuarios...");
			usuarios = new DAO<Usuario>(Usuario.class).getAllOrder("nome, id");
		}
		return usuarios;
	}

	// Exibe uma lista com as solicitações de acesso
	@SuppressWarnings("unchecked")
	public List<Usuario> getSolicitacoes() {
		Query query = dao
				.query("SELECT u FROM Usuario u WHERE u.status = false AND u.aceitaSolicitacao = true AND u.perfil = 4");
		usuarios = query.getResultList();
		System.out.println("Total de Usuários: " + getUsuarios().size());
		return query.getResultList();
	}

	// Exibe uma lista de usuário funcionários ativados
	@SuppressWarnings("unchecked")
	public List<Usuario> getAtivados() {
		Query query = dao
				.query("SELECT u FROM Usuario u WHERE u.status = true AND u.perfil <= 3 ORDER BY u.nome");
		usuarios = query.getResultList();
		System.out.println("Total de Usuários: " + getUsuarios().size());
		return query.getResultList();
	}

	// Exibe uma lista de usuário ativados sem clientes e administrador
	@SuppressWarnings("unchecked")
	public List<Usuario> getAtivadosSemAdministrador() {
		Query query = dao
				.query("SELECT u FROM Usuario u WHERE u.status = true AND u.perfil <= 3 and u.perfil != 1 ORDER BY u.nome");
		usuarios = query.getResultList();
		System.out.println("Total de Usuários: " + getUsuarios().size());
		return query.getResultList();
	}
	
	// Exibe uma lista de usuários desativados
	@SuppressWarnings("unchecked")
	public List<Usuario> getUsuariosDesativados() {
		Query query = dao
				.query("SELECT u FROM Usuario u WHERE u.status = false AND u.perfil != 4 ORDER BY u.nome");
		usuarios = query.getResultList();
		System.out.println("Total de Clientes: " + getUsuarios().size());
		return query.getResultList();
	}

	// Exibe uma lista de usuários desativados sem administrador
	@SuppressWarnings("unchecked")
	public List<Usuario> getUsuariosDesativados2() {
		Query query = dao
				.query("SELECT u FROM Usuario u WHERE u.status = false AND u.perfil != 4 and u.perfil != 1 ORDER BY u.nome");
		usuarios = query.getResultList();
		System.out.println("Total de Clientes: " + getUsuarios().size());
		return query.getResultList();
	}

	// Exibe uma lista de clientes ativados
	@SuppressWarnings("unchecked")
	public List<Usuario> getClientes() {
		Query query = dao.query("SELECT u FROM Usuario u WHERE u.status = true AND u.perfil = 4 AND u.id <> 1 ORDER BY u.nome");
		usuarios = query.getResultList();
		System.out.println("Total de Clientes: " + getUsuarios().size());
		return query.getResultList();
	}

	// Exibe uma lista de clientes no geral (ativados e desativados)
	public List<Usuario> getClientesCadastrados() {
		usuariosE = new ArrayList<Usuario>();
		List<Usuario> us = new ArrayList<Usuario>();
		us = this.getUsuarios();
		for (int i = 0; i < us.size(); i++) {
			if (us.get(i).getPerfil().getId() == 4 && us.get(i).getId() != 1) {
				usuariosE.add(us.get(i));
			}
		}
		return usuariosE;
	}
	
	// Exibe uma lista de clientes no geral (ativados e desativados) sem o id = 1
	public List<Usuario> getClientesCadastrados2() {
		usuariosE = new ArrayList<Usuario>();
		List<Usuario> us = new ArrayList<Usuario>();
		us = this.getUsuarios();
		for (int i = 0; i < us.size(); i++) {
			if (us.get(i).getPerfil().getId() == 4) {
				usuariosE.add(us.get(i));
			}
		}
		return usuariosE;
	}
	
	
	/** Consultas **/
	
	// pesquisa nota pelo id
	@SuppressWarnings("unchecked")
	public void getClienteById() {
		if (usuario.getId() == null || usuario.getId() == 0) {
			Msg.addMsgError("Informe corretamente o código do cliente");

		} else {
			try {
				Query query = dao.query("SELECT u FROM Usuario u WHERE u.id=? and u.id != 1 and u.perfil = 4");
				query.setParameter(1, usuario.getId());
				usuarios = query.getResultList();
				if (usuarios.isEmpty()) {
					init();
					Msg.addMsgError("Nenhum registro encontrado");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Pesquisa usuário pelo nome
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
				Msg.addMsgError("Nenhum registro encontrado");
				return null;

			} else {
				System.out.println("Chegou Aqui... Processando informações...");
				try {
					Query query = dao
							.query("SELECT u FROM Usuario u WHERE u.nome LIKE ?");
					query.setParameter(1, usuario.getNome() + "%");
					usuarios = query.getResultList();
					System.out.println("...Usuário encontrado com sucesso");
				} catch (Exception e) {
					e.printStackTrace();
					System.out
							.println("...erro: Usuário não pode ser pesquisado!");
				}
				return null;
			}
		}
	}

	// Pesquisa usuário pelo CPF
	@SuppressWarnings("unchecked")
	public String getListaUsuariosByCPF() {
		if (usuario.getNome().contains("'") || usuario.getNome().contains("@")
				|| usuario.getNome().contains("/")
				|| usuario.getNome().contains("*")
				|| usuario.getNome().contains("<")
				|| usuario.getNome().contains(">")
				|| usuario.getNome().contains("#")) {

			Msg.addMsgError("Contém caracter(es) inválido(s)");
			return null;
		}
		if (usuario.getCpf().length() != 14) {
			Msg.addMsgError("Informe o CPF correto");
			return null;

		} else {
			usuarios = dao.getAllByName("cpf", usuario.getCpf());
			if (usuarios.isEmpty()) {
				Msg.addMsgError("CPF não encontrado");
				return null;

			} else {
				System.out.println("Chegou Aqui... Processando informações...");
				try {
					Query query = dao
							.query("SELECT u FROM Usuario u WHERE u.cpf=? and u.perfil = 4");
					query.setParameter(1, usuario.getCpf());
					usuarios = query.getResultList();
					System.out.println("...Usuário encontrado com sucesso");
				} catch (Exception e) {
					e.printStackTrace();
					System.out
							.println("...erro: Usuário não pode ser pesquisado!");
				}
				return null;
			}
		}
	}

	// Pesquisa Usuario pelo nome e cpf
	public String getListarClientes() {

		if (box4Search.equals(2)) {
			if (search.contains("'") || search.contains("@")
					|| search.contains("*")) {
				init();
				Msg.addMsgError("Contém caractér(es) inválido(s)");
				return null;
			} else {
				if (search.length() <= 10) {
					init();
					Msg.addMsgError("Informe o CPF corretamente");
					System.out.println("...Informe o CPF corretamente para consultar cliente");
					return null;
				} else {
					usuarios = dao.getAllByName("obj.cpf", search);
					if (getClientesCadastrados().isEmpty()) {
						init();
						Msg.addMsgError("Nenhum registro encontrado");
						System.out.println("...Cliente não encontrado");
						return null;
					} else {
						
					}
				}
			}
		} else if (box4Search.equals(1)) {
			if (search.contains("'") || search.contains("@")
					|| search.contains("*")
					|| search.contains(".")
					|| search.contains("/")
					|| search.contains("-")) {
				init();
				Msg.addMsgError("Contém caractér(es) inválido(s)");
				return null;
			} else {
				if (search.length() <= 4) {
					init();
					Msg.addMsgError("Informe pelo menos 5 caracteres");
					System.out.println("...Informe pelo menos 5 caracteres para consultar cliente");
					return null;
				} else {
					usuarios = dao.getAllByName("obj.nome", search);
					if (getClientesCadastrados().isEmpty()) {
						init();
						Msg.addMsgError("Nenhum registro encontrado");
						System.out.println("...Cliente não encontrado");
						return null;
					} else {
						
					}
				}
			}
		}
		return null;
	}

	/** Actions **/

	// aqui funcionário efetua o cadastro do cliente sem incluir a senha de
	// acesso
	public String addClienteIn() {
		try {
			boolean all = true;
			if (!validarNomeUK_login()) {
				all = false;
			}
			if (!validarNomeUK_cpf()) {
				all = false;
			}
			if (!all) {
				System.out
						.println("...erro ao cadastrar, este cliente já está cadastrado");
				Msg.addMsgError("ESTE EMAIL OU CPF JÁ ESTÁ CADASTRADO. FAÇA UMA CONSULTA PARA VERIFICAR");
				return "/pages/usuario/cadastrarCliente.jsf";
			} else {
				usuario.setEndereco(endereco);
				usuario.setStatus(true);
				usuario.setAceitaSolicitacao(true);
				usuario.setEndereco(endereco);
				usuario.setSenha("[{(UFRR000DTI-CSI001EDITORA002SISTEMA003)}]");
				usuario.setSenha(TransformaStringMD5.md5(usuario.getSenha()));
				dao.adiciona(usuario);
				init();
				Msg.addMsgInfo("CADASTRO EFETUADO COM SUCESSO");
				System.out.println("...cadastro efetuado com sucesso!");
			}
		} catch (Exception e) {
			init();
			e.printStackTrace();
			System.out
					.println("...Alguma coisa deu errada ao cadastrar cliente");
		}
		return null;
	}

	// auto cadastro do cliente para comprar produto (não precisará de permissão
	// do administrador)
	public String addCliente() {
		try {
			boolean all = true;
			if (!validarNomeUK_login()) {
				all = false;
			}
			if (!validarNomeUK_cpf()) {
				all = false;
			}
			if (getUsuario().getTelefone1().equalsIgnoreCase(
					this.getUsuario().getTelefone2())) {
				Msg.addMsgError("NÚMERO DE TELEFONES NÃO PODEM SER IGUAIS, POR FAVOR INFORME OUTRO");
				System.out.println("...erro: número de telefones iguais");
			}
			if (!all) {
				System.out
						.println("...erro ao cadastrar, este cliente já está cadastrado");
				Msg.addMsgError("EMAIL OU CPF JÁ ESTÁ CADASTRADO NO SISTEMA");
				return "/pages/usuario/cadastrarCliente.jsf";
			} else {
				if (getUsuario().getSenha().equalsIgnoreCase(
						this.getUsuario().getRepetirSenha())) {
					usuario.setSenha(TransformaStringMD5.md5(usuario.getSenha()));
					usuario.setStatus(true);
					usuario.setAceitaSolicitacao(true);
					usuario.setEndereco(endereco);
					dao.adiciona(usuario);
					init();
					Msg.addMsgInfo("SEU CADASTRO FOI EFETUADO COM SUCESSO");
					this.usuario = new Usuario();
					System.out
							.println("...cadastro de cliente efetuado com sucesso");
					return "index.xhtml";
				} else {
					System.out.println("...Senhas diferentes");
					Msg.addMsgError("SENHAS DIFERENTES, TENTE NOVAMENTE");
				}
			}
		} catch (Exception e) {
			init();
			e.printStackTrace();
			System.out
					.println("...Alguma coisa deu errada ao cadastrar cliente");
		}
		return null;
	}

	// solicitação de acesso ao sistema(somente para funcionários)
	public String addPessoa() {
		try {
			boolean all = true;
			if (!validarNomeUK_login()) {
				all = false;
			}
			if (!validarNomeUK_cpf()) {
				all = false;
			}
			if (!all) {
				System.out
						.println("...erro ao cadastrar, este cliente já está cadastrado");
				Msg.addMsgError("CPF OU EMAIL JÁ CONTÉM REGISTRO NO SISTEMA, TENTE OUTRO.");
				return "/pages/usuario/cadastrarCliente.jsf";
			} else {
				if (getUsuario().getSenha().equalsIgnoreCase(
						this.getUsuario().getRepetirSenha())) {
					usuario.setStatus(false);
					usuario.setAceitaSolicitacao(true);
					usuario.setEndereco(endereco);
					usuario.setSenha(TransformaStringMD5.md5(usuario.getSenha()));
					dao.adiciona(usuario);
					init();
					Msg.addMsgInfo("SOLICITAÇÃO DE ACESSO ENVIADA COM SUCESSO. AGUARDE AUTORIZAÇÃO!");
					this.usuario = new Usuario();
					System.out.println("...Solicitação enviada");
					return "index.xhtml";
				} else {
					System.out.println("...Senhas diferentes");
					Msg.addMsgError("SENHAS DIFERENTES, TENTE NOVAMENTE");
				}
			}
		} catch (Exception e) {
			init();
			e.printStackTrace();
			System.out
					.println("...Alguma coisa deu errada ao cadastrar cliente");
		}
		return null;
	}
	
	// Não aceira solicitacao de usuario
	public String naoAceitar() {
		System.out.println(this.getUsuario().getNome());
		if (this.getUsuario().getPerfil().getId() != 5
				&& this.getUsuario().getPerfil().getId() != null) {
			this.getUsuario().setStatus(false);
			this.getUsuario().setAceitaSolicitacao(false);
			Msg.addMsgFatal("USUÁRIO: " + getUsuario().getNome()
					+ " SOLICITAÇÃO NÃO ACEITA");
			dao.atualiza(usuario);
			System.out.println("...Usuário ativado");
			return "/pages/usuario/autorizarAcesso.xhtml";
		}
		return "/pages/usuario/autorizarAcesso.xhtml";

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

	// atualiza perfil
	public void updatePerfil() {
		if (this.getUsuario().getPerfil().getId() != 5
				&& this.getUsuario().getPerfil().getId() != null) {
			this.getUsuario().setStatus(true);
			Msg.addMsgInfo("PERFIL DE " + getUsuario().getNome()
					+ " FOI MODIFICADO PARA "
					+ getUsuario().getPerfil().getPerfil());
			dao.atualiza(usuario);
			System.out.println("...Perfil de usuário: "
					+ getUsuario().getNome() + " modificado");
		} else {
			System.out.println("...Não foi possível modificar perfil");
			Msg.addMsgError("NÃO FOI POSSÍVEL EFETUAR OPERAÇÃO. TENTE NOVAMENTE");
		}
	}

	// update Cliente
	public void updateCliente() {
		if (usuario.getTelefone1()
				.equalsIgnoreCase(getUsuario().getTelefone2())) {
			Msg.addMsgError("NÚMEROS DE TELEFONE NÃO PODEM SER IGUAIS");
		} else {
			if (usuario.getId() != null) {
				Msg.addMsgInfo("CADASTRO DO CLIENTE: " + getUsuario().getNome()
						+ " FOI ALTERADO COM SUCESSO");
				dao.atualiza(usuario);
				System.out.println("...Cadastro do cliente: "
						+ getUsuario().getNome() + " alterado com sucesso.");
				this.usuario = new Usuario();
			} else {
				System.out
						.println("...Não foi possível alterar cadastro do cliente");
				Msg.addMsgError("NÃO FOI POSSÍVEL EFETUAR OPERAÇÃO. TENTE NOVAMENTE");
			}
		}
	}

	// recuperar acesso no sistema
	public void recuperaSenha() {
		try {
			EmailUtils.recuperaSenha(login);
		} catch (EmailException ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Erro! Occoreu um erro ao tentar enviar o email",
							"Erro"));
			System.out.println("...Erro ao tentar enviar o email para solicitacao esqueci minha senha");
			Logger.getLogger(EmailBean.class.getName()).log(Level.ERROR, null, ex);
		}
	}


	/** validação UK Login */

	public boolean validarNomeUK_login() {
		return validator.validarNomeUK("login", usuario.getLogin());
	}

	public void checkNomeUK_login(AjaxBehaviorEvent event) {
		if (validarNomeUK_login()) {
			if (usuario.getLogin().isEmpty()) {
				validator.setResultNome("");
			}
		}
	}

	/** validação para não repetir CPF */

	public boolean validarNomeUK_cpf() {
		return validator.validarNomeUK("cpf", usuario.getCpf());
	}

	public void checkNomeUK_cpf(AjaxBehaviorEvent event) {
		if (validarNomeUK_cpf()) {
			if (usuario.getCpf().isEmpty()) {
				validator.setResultNome("");
			}
		}
	}

	/** ajax */

	public void checkBox4Search(AjaxBehaviorEvent event) {

	}

	public void checkNome(AjaxBehaviorEvent event) {
		validarNome();
		if (usuario.getNome().isEmpty()) {
			validator.setResultNome("");
		}
	}

	public boolean validarNome() {
		return validator.validarNome(usuario.getNome());
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

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public Integer getBox4Search() {
		return box4Search;
	}

	public void setBox4Search(Integer box4Search) {
		this.box4Search = box4Search;
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

	public List<Usuario> getUsuariosE() {
		return usuariosE;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Validator<Usuario> getValidator() {
		return validator;
	}

	public void setValidator(Validator<Usuario> validator) {
		this.validator = validator;
	}

}
