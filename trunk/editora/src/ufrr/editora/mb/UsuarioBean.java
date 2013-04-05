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
	
	// Exibe uma lista com as solicitações de acesso
	public List<Usuario> getSolicitacoes() {
		usuariosE = new ArrayList<Usuario>();
		for (Usuario u : this.getUsuarios()) {
				if (u.getStatus()!=true && u.getPerfil().getPerfil().equalsIgnoreCase("solicitação")) {
					usuariosE.add(u);
				}
		}
		return usuariosE;
	}
	
	// Exibe uma lista de usuário ativados
	public List<Usuario> getAtivados() {
		usuariosE = new ArrayList<Usuario>();
		for (Usuario u : this.getUsuarios()) {
			if (u.getStatus() == true && u.getPerfil().getId() <= 4) {
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
	
	// Exibe uma lista de perfil id=5
		public List<Perfil> getPerfil5() {
			perfis = new ArrayList<Perfil>();
			for (Perfil p : this.getPerfis()) {
				if (p.getPerfil().equalsIgnoreCase("solicitação")) {
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
						Query query = dao.query("SELECT u FROM Usuario u WHERE u.nome=?");
						query.setParameter(1, usuario.getNome());
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
		if (this.getUsuario().getPerfil().getId() != 5 && this.getUsuario().getPerfil().getId()!=null) {
			this.getUsuario().setStatus(true);
			// funcionario.setSenha(TransformaStringMD5.md5(funcionario.getSenha()));
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
	
	// Desativar usuário
		public String desativarUsuario() {
			if (this.getUsuario().getPerfil().getId() != 5 && this.getUsuario().getPerfil().getId()!=null) {
				this.getUsuario().setStatus(false);
				// funcionario.setSenha(TransformaStringMD5.md5(funcionario.getSenha()));
				Msg.addMsgInfo("USUÁRIO: " + getUsuario().getNome()
						+ " DESATIVADO");
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
	
	// Função para criar hash da senha informada  
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
	
//	//Método para Edição do funcionário na troca de perfil;
//	public void edita() { 
//			Msg.addMsgInfo("Perfil Alterado Com Sucesso");
//			dao.atualiza(funcionario);
//			this.funcionario = new Funcionario();
//		}
//	

//	
//	//Método para solicitação de cadastro
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
//				this.getFuncionario().setPerfil("Usuário");
//				this.getFuncionario().setLogado(false);
//				dao.adiciona(funcionario);
//				Msg.addMsgInfo("Funcionário Cadastrado Com Sucesso!");
//				this.funcionario = new Funcionario();
//					
//			} else {
//				Msg.addMsgError("Senha diferente do Repetir Senha");
//			}
//		} else {
//			Msg.addMsgError("Já existe um funcionário cadastrado com este Login, teste outro.");
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
//	// Método para Listar todos os Funcionários com Status "Desativado"
//	public List<Funcionario> getFuncionariosD() {
//		funcionariosD = new ArrayList<Funcionario>();
//		for (Funcionario func : this.getFuncionarios()) {
//			if(func.getStatus().equalsIgnoreCase("Desativado") && func.getPerfil().equalsIgnoreCase("Usuário")
//					|| func.getStatus().equalsIgnoreCase("Desativado") && func.getPerfil().equalsIgnoreCase("Administrador")
//					|| func.getStatus().equalsIgnoreCase("Desativado") && func.getPerfil().equalsIgnoreCase("Convenio")
//					|| func.getStatus().equalsIgnoreCase("Desativado") && func.getPerfil().equalsIgnoreCase("Consulta")){
//				funcionariosD.add(func);
//			}
//		}
//		return funcionariosD;
//	}
//	
//	// Método para Listar todos os Funcionários com Status "Bloqueado"
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
//		// Método para ativar funcionário expirado
//				public void ativaFuncionario2(){
//					if(this.getFuncionario().getPerfil().equalsIgnoreCase("Expirado")) {
//						this.getFuncionario().setPerfil("Usuário");
//						this.getFuncionario().setLogado(false);
//						dao.atualiza(funcionario);
//						Msg.addMsgInfo("Usuário Ativado");
//						System.out.println("------------------------------------------------------OK!!!!!!!!!!!");
//					} else {
//						System.out.println("-------------------------------------------------------Algum erro ocorreu e não foi possível ativar o funcionário. Por Favor, contate a administração.");
//					}
//				}
//		
//		// Método para desativar funcionário, cessando sua atilidade.
//		public void desativaFuncionario(){
//			if(this.getFuncionario().getStatus().equalsIgnoreCase("Em Espera") || this.getFuncionario().getStatus().equalsIgnoreCase("Ativado")){
//				this.getFuncionario().setStatus("Desativado");
//				dao.atualiza(funcionario);
//				System.out.println("------------------------------------------------------OK!!!!!!!!!!!");
//			} else {
//				System.out.println("-------------------------------------------------------Algum erro ocorreu e não foi possível ativar o funcionário. Por Favor, contate a administração.");
//			}
//		}
//		
//		// Método para expirar a sessão do funcionário.
//				public void expiraFuncionario(){
//					if(this.getFuncionario().getStatus().equalsIgnoreCase("Ativado")){
//						this.getFuncionario().setPerfil("Expirado");
//						dao.atualiza(funcionario);
//						Msg.addMsgInfo("Sessão Expirada");
//						System.out.println("------------------------------------------------------OK!!!!!!!!!!!");
//					} else {
//						System.out.println("-------------------------------------------------------Algum erro ocorreu e não foi possível expirar o funcionário. Por Favor, contate a administração.");
//					}
//				}
	
	
	
	

	
	
}
