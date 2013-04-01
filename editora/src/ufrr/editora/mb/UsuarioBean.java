package ufrr.editora.mb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ufrr.editora.dao.DAO;
import ufrr.editora.entity.Usuario;
import ufrr.editora.util.Msg;

@ViewScoped
@ManagedBean
public class UsuarioBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private LoginBean login;
	
	private String senhaCriptografada;
	
	private Usuario usuario = new Usuario();
	private List<Usuario> usuarios;
	private List<Usuario> usuariosE; // Lista com Usuarios "Em Espera"
	private List<Usuario> usuariosD; // Lista com Usuarios "Desativado"
	DAO<Usuario> dao = new DAO<Usuario>(Usuario.class);
	public Boolean cadastro = true;
	
	//AutoComplete Login
	public List<String> autocompletelogin(String nome) {
		List<Usuario> array = dao.getAllByName("login", nome);
		ArrayList<String> nomes = new ArrayList<String>();
		for (int i = 0; i < array.size(); i++) {
			nomes.add(array.get(i).getLogin());
		}
		return nomes;
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

	public List<Usuario> getUsuarios() {
		return usuarios;
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
	public String addUsuario() {
		for (Usuario UsuarioLista : this.getUsuarios()) {
			if(UsuarioLista.getLogin().equalsIgnoreCase(this.usuario.getLogin()) 
					|| UsuarioLista.getPessoa().getCpf().equalsIgnoreCase(this.usuario.getPessoa().getCpf())){
				this.cadastro = false;
				break;
			}
		}
		if(this.usuario.getLogin() == null){
			this.cadastro = false;
		}
		if(this.cadastro == true){
			if (this.getUsuario().getSenha().equals(this.getUsuario().getRepetirSenha())) {
				dao.adiciona(usuario);
				Msg.addMsgInfo("Solicita��o de cadastro enviada com sucesso. Aguarde ativa��o!");
				this.usuario = new Usuario();
				return "index.xhtml";
			} else {
				Msg.addMsgError("Senhas diferentes. Por favor, verifique os campos e digite novamente.");
			}
		} else {
			if(this.usuario.getLogin() == null){
				Msg.addMsgError("Seu login ser� seu email, informe um email para proseguir com o cadastro");
			} else {
				Msg.addMsgError("Este cadastro j� existe");
			}
			return "solicitacao.xhtml";
		}
		return null;
	}
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

	// M�todo para Listar os usu�rios com status "null" (em espera)
	public List<Usuario> getUsuariosE() {
		usuariosE = new ArrayList<Usuario>();
		for (Usuario u : this.getUsuarios()) {
			if(u.getStatus().equals(null)){
				usuariosE.add(u);
			}
		}
		return usuariosE;
	}
	
	// Ativar usu�rio (permitir acesso)
		public String ativaUsuario(){
				if(this.getUsuario().getStatus().equals(null) || this.getUsuario().getStatus().equals(false)){
					this.getUsuario().setStatus(true);
//					funcionario.setSenha(TransformaStringMD5.md5(funcionario.getSenha()));
					Msg.addMsgInfo("Usu�rio: " + getUsuario().getPessoa().getNome() + " ativado com sucesso");
					dao.atualiza(usuario);
					System.out.println("...Usu�rio ativado");
				} else {
					System.out.println("..N�o foi poss�vel ativar funcion�rio");
				}
			return null;
				
		}
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
	
	
	
	/** Getters and Setters **/

	
	
}
