package ufrr.editora.mb;

import java.io.Serializable;
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
	
	// Exibe uma lista com as solicita��es de acesso
	public List<Usuario> getSolicitacoes() {
		usuariosE = new ArrayList<Usuario>();
		for (Usuario u : this.getUsuarios()) {
				if (u.getStatus()!=true && u.getPerfil().getPerfil().equalsIgnoreCase("solicita��o")) {
					usuariosE.add(u);
				}
		}
		return usuariosE;
	}
	
	// Exibe uma lista de usu�rio ativados
	public List<Usuario> getAtivados() {
		usuariosE = new ArrayList<Usuario>();
		for (Usuario u : this.getUsuarios()) {
			if (u.getStatus() == true && u.getPerfil().getId() <= 4) {
				usuariosE.add(u);
			}
		}
		return usuariosE;
	}
		
	// Exibe uma lista de usu�rio ativados != Administrador
	public List<Usuario> getAtivados2() {
		usuariosE = new ArrayList<Usuario>();
		for (Usuario u : this.getUsuarios()) {
			if (u.getStatus() == true && u.getPerfil().getId() <= 4 && u.getPerfil().getId() != 1) {
				usuariosE.add(u);
			}
		}
		return usuariosE;
	}	
	
	// Exibe uma lista de perfil id=5
		public List<Perfil> getPerfil5() {
			perfis = new ArrayList<Perfil>();
			for (Perfil p : this.getPerfis()) {
				if (p.getPerfil().equalsIgnoreCase("solicita��o")) {
					perfis.add(p);
				}
			}
			return perfis;
		}

		
/** Pesquisa Usuario **/
		@SuppressWarnings("unchecked")
		public String getListaUsuariosByName() {
			if (usuario.getNome().contains("'")
					|| usuario.getNome().contains("@")
					|| usuario.getNome().contains("/")
					|| usuario.getNome().contains("*")
					|| usuario.getNome().contains("<")
					|| usuario.getNome().contains(">")
					|| usuario.getNome().contains("#")) {

				Msg.addMsgError("Cont�m caracter(es) inv�lido(s)");
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
					System.out.println("Chegou Aqui... Processando informa��es...");
					try {
						Query query = dao.query("SELECT u FROM Usuario u WHERE u.nome=?");
						query.setParameter(1, usuario.getNome());
						usuariosD = query.getResultList();
						System.out.println("Usu�rio encontrado com sucesso...");
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
			}
		}
		
	// Ativar usu�rio (permitir acesso)
	public String ativarUsuario() {
		if (this.getUsuario().getPerfil().getId() != 5 && this.getUsuario().getPerfil().getId()!=null) {
			this.getUsuario().setStatus(true);
			// funcionario.setSenha(TransformaStringMD5.md5(funcionario.getSenha()));
			Msg.addMsgInfo("USU�RIO: " + getUsuario().getNome()
					+ " ATIVADO COM SUCESSO");
			dao.atualiza(usuario);
			System.out.println("...Usu�rio ativado");
			return "/pages/usuario/autorizarAcesso.xhtml";
		} else {
			System.out.println("..N�o foi poss�vel ativar usu�rio");
			Msg.addMsgError("USU�RIO: " + getUsuario().getNome()
					+ " N�O FOI ATIVADO. TENTE NOVAMENTE");
		}
		return "/pages/usuario/autorizarAcesso.xhtml";

	}
	
	// Desativar usu�rio
		public String desativarUsuario() {
			if (this.getUsuario().getPerfil().getId() != 5 && this.getUsuario().getPerfil().getId()!=null) {
				this.getUsuario().setStatus(false);
				// funcionario.setSenha(TransformaStringMD5.md5(funcionario.getSenha()));
				Msg.addMsgInfo("USU�RIO: " + getUsuario().getNome()
						+ " DESATIVADO");
				dao.atualiza(usuario);
				this.usuario = new Usuario();
				System.out.println("...Usu�rio desativado");
				return "/pages/usuario/desativarAcesso.xhtml";
			} else {
				System.out.println("..N�o foi poss�vel desativar usu�rio");
				Msg.addMsgError("USU�RIO: " + getUsuario().getNome()
						+ " N�O FOI DESATIVADO. TENTE NOVAMENTE");
			}
			return "/pages/usuario/desativarAcesso.xhtml";

		}
		
		//solicitacao de cadastro
		public String addPessoa() {
			for (Usuario usuarios : this.getUsuarios()) {
				if (usuarios.getCpf().equalsIgnoreCase(this.getUsuario().getCpf()) ||
						usuarios.getLogin().equalsIgnoreCase(this.getUsuario().getLogin())) {
					this.cadastro = false;
					break;
				}
			}

			if (this.cadastro == true) {
				if (getUsuario().getSenha().equalsIgnoreCase(this.getUsuario().getRepetirSenha())) {
					usuario.setStatus(false);
					usuario.setEndereco(endereco);
					dao.adiciona(usuario);
					Msg.addMsgInfo("Solicita��o de acesso enviada com sucesso. Aguarde autoriza��o!");
					this.usuario = new Usuario();
					System.out.println("...Solicita��o enviada");
					return "index.xhtml";
				} else {
					System.out.println("...Senhas diferentes");
					Msg.addMsgError("Senhas diferentes, tente de novo");
				}
			} else {
				System.out.println("...cadastro existente");
				Msg.addMsgError("Este cadastro j� existe");
			}
			usuarios = dao.getAllOrder("nome");
			this.cadastro = true;
			return null;
		}
		
		// atualiza perfil
		public String updatePerfil() {
			if (usuario.getId()!=null) {
				Msg.addMsgInfo("Perfil Alterado Com Sucesso");
				dao.update(usuario);
				this.usuario = new Usuario();
				
			}
			return "/pages/usuario/modificarPefil.xhtml";
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
	
	// Fun��o para criar hash da senha informada  
//    public static String md5(String senha) {  
//        String sen = "";  
//        MessageDigest md = null;  
//        try {  
//            md = MessageDigest.getInstance("MD5");  
//        } catch (NoSuchAlgorithmException e) {  
//            e.printStackTrace();  
//        }  
//        BigInteger hash = new BigInteger(1, md.digest(senha.getBytes()));  
//        sen = hash.toString(16);  
//        return sen;  
//    }  
	
//	//M�todo para Edi��o do funcion�rio na troca de perfil;
//	public void edita() { 
//			Msg.addMsgInfo("Perfil Alterado Com Sucesso");
//			dao.atualiza(funcionario);
//			this.funcionario = new Funcionario();
//		}
//	

//	
//	//M�todo para solicita��o de cadastro
//	public void grava() {
//		MsgDAO dd = new MsgDAO();
//		this.funcionario.setMsg(dd.pegaMsg());
//		for (Funcionario UsuarioLista : this.getFuncionarios()) {
//			if(UsuarioLista.getLogin().equalsIgnoreCase(this.funcionario.getLogin())){
//				this.cadastro = false;
//				break;
//			}
//		}
//		if(this.cadastro == true){
//			if (this.getFuncionario().getSenha().equals(this.getUsuario().getRep_senha()) ) {
//				this.getFuncionario().setStatus("Em Espera");
//				this.getFuncionario().setPerfil("Usu�rio");
//				this.getFuncionario().setLogado(false);
//				dao.adiciona(funcionario);
//				Msg.addMsgInfo("Funcion�rio Cadastrado Com Sucesso!");
//				this.funcionario = new Funcionario();
//					
//			} else {
//				Msg.addMsgError("Senha diferente do Repetir Senha");
//			}
//		} else {
//			Msg.addMsgError("J� existe um funcion�rio cadastrado com este Login, teste outro.");
//		}
//		 this.funcionarios = dao.getAllOrder("nome");
//		 this.cadastro = true;
//	}
//	
//	
//	public List<Funcionario> getFuncionarios() {
//		if (funcionarios == null) {
//			System.out.println("Carregando Usuarios...");
//			funcionarios = new DAO<Funcionario>(Funcionario.class).getAllOrder("nome");
//		}
//		return funcionarios;
//	}
//
//	public boolean isLogado() {
//		return funcionario.getLogin() != null;
//	}
//	
//	
//	// M�todo para Listar todos os Funcion�rios com Status "Desativado"
//	public List<Funcionario> getFuncionariosD() {
//		funcionariosD = new ArrayList<Funcionario>();
//		for (Funcionario func : this.getFuncionarios()) {
//			if(func.getStatus().equalsIgnoreCase("Desativado") && func.getPerfil().equalsIgnoreCase("Usu�rio")
//					|| func.getStatus().equalsIgnoreCase("Desativado") && func.getPerfil().equalsIgnoreCase("Administrador")
//					|| func.getStatus().equalsIgnoreCase("Desativado") && func.getPerfil().equalsIgnoreCase("Convenio")
//					|| func.getStatus().equalsIgnoreCase("Desativado") && func.getPerfil().equalsIgnoreCase("Consulta")){
//				funcionariosD.add(func);
//			}
//		}
//		return funcionariosD;
//	}
//	
//	// M�todo para Listar todos os Funcion�rios com Status "Bloqueado"
//		public List<Funcionario> getFuncionariosB() {
//			funcionariosD = new ArrayList<Funcionario>();
//			for (Funcionario func : this.getFuncionarios()) {
//				if(func.getPerfil().equalsIgnoreCase("Bloqueado")){
//					funcionariosD.add(func);
//				}
//			}
//			return funcionariosD;
//		}
//	

//		
//		// M�todo para ativar funcion�rio expirado
//				public void ativaFuncionario2(){
//					if(this.getFuncionario().getPerfil().equalsIgnoreCase("Expirado")) {
//						this.getFuncionario().setPerfil("Usu�rio");
//						this.getFuncionario().setLogado(false);
//						dao.atualiza(funcionario);
//						Msg.addMsgInfo("Usu�rio Ativado");
//						System.out.println("------------------------------------------------------OK!!!!!!!!!!!");
//					} else {
//						System.out.println("-------------------------------------------------------Algum erro ocorreu e n�o foi poss�vel ativar o funcion�rio. Por Favor, contate a administra��o.");
//					}
//				}
//		
//		// M�todo para desativar funcion�rio, cessando sua atilidade.
//		public void desativaFuncionario(){
//			if(this.getFuncionario().getStatus().equalsIgnoreCase("Em Espera") || this.getFuncionario().getStatus().equalsIgnoreCase("Ativado")){
//				this.getFuncionario().setStatus("Desativado");
//				dao.atualiza(funcionario);
//				System.out.println("------------------------------------------------------OK!!!!!!!!!!!");
//			} else {
//				System.out.println("-------------------------------------------------------Algum erro ocorreu e n�o foi poss�vel ativar o funcion�rio. Por Favor, contate a administra��o.");
//			}
//		}
//		
//		// M�todo para expirar a sess�o do funcion�rio.
//				public void expiraFuncionario(){
//					if(this.getFuncionario().getStatus().equalsIgnoreCase("Ativado")){
//						this.getFuncionario().setPerfil("Expirado");
//						dao.atualiza(funcionario);
//						Msg.addMsgInfo("Sess�o Expirada");
//						System.out.println("------------------------------------------------------OK!!!!!!!!!!!");
//					} else {
//						System.out.println("-------------------------------------------------------Algum erro ocorreu e n�o foi poss�vel expirar o funcion�rio. Por Favor, contate a administra��o.");
//					}
//				}
	
	
	
	

	
	
}
