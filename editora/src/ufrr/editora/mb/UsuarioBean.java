package ufrr.editora.mb;

import java.io.Serializable;
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
	private List<Usuario> funcionarios;
	private List<Usuario> funcionariosE; // Lista com os Funcionários "Em Espera"
	private List<Usuario> funcionariosD; // Lista com os Funcionários "Desativado"
	DAO<Usuario> dao = new DAO<Usuario>(Usuario.class);
	public Boolean cadastro = true;
	
	
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
	public List<Usuario> getFuncionarios() {
		return funcionarios;
	}
	public void setFuncionarios(List<Usuario> funcionarios) {
		this.funcionarios = funcionarios;
	}
	public List<Usuario> getFuncionariosE() {
		return funcionariosE;
	}
	public void setFuncionariosE(List<Usuario> funcionariosE) {
		this.funcionariosE = funcionariosE;
	}
	public List<Usuario> getFuncionariosD() {
		return funcionariosD;
	}
	public void setFuncionariosD(List<Usuario> funcionariosD) {
		this.funcionariosD = funcionariosD;
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
//	//Método Enviar Mensagem;
//		public void enviar() { 
//				Msg.addMsgInfo("Mensagem Enviada Para Usuário: " + getFuncionario().getNome());
//				dao.atualiza(funcionario);
//				System.out.println("...Mensagem Enviada Para " + getFuncionario().getNome());
//				this.funcionario = new Funcionario();
//			}
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
	public String solicitar() {
		for (Usuario UsuarioLista : this.getFuncionarios()) {
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
				Msg.addMsgInfo("Solicitação de cadastro enviada com sucesso. Aguarde ativação!");
				this.usuario = new Usuario();
				return "index.xhtml";
			} else {
				Msg.addMsgError("Senhas diferentes. Por favor, verifique os campos e digite novamente.");
			}
		} else {
			if(this.usuario.getLogin() == null){
				Msg.addMsgError("Seu login será seu email, informe um email para proseguir com o cadastro");
			} else {
				Msg.addMsgError("Já existe um cadastro com este CPF");
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
//	// Método para Listar todos os Funcionários com Status "Logado"
//	public List<Funcionario> getFuncionariosLogado() {
//		funcionariosE = new ArrayList<Funcionario>();
//		for (Funcionario func : this.getFuncionarios()) {
//			if(func.getLogado().equals(true)){
//				funcionariosE.add(func);
//			}
//		}
//		return funcionariosE;
//	}
//	
//	// Método para Listar todos os Funcionários com Status "Em Espera"
//	public List<Funcionario> getFuncionariosE() {
//		funcionariosE = new ArrayList<Funcionario>();
//		for (Funcionario func : this.getFuncionarios()) {
//			if(func.getStatus().equalsIgnoreCase("Em Espera")){
//				funcionariosE.add(func);
//			}
//		}
//		return funcionariosE;
//	}
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
//	// Método para ativar funcionário, permitindo assim o seu login
//		public String ativaFuncionario(){
//				if(this.getFuncionario().getStatus().equalsIgnoreCase("Em Espera") || this.getFuncionario().getStatus().equalsIgnoreCase("Desativado")){
//					this.getFuncionario().setStatus("Ativado");
//					this.getFuncionario().setAcesso(1);
//					this.getFuncionario().setPagina(1);
//					funcionario.setSenha(TransformaStringMD5.md5(funcionario.getSenha()));
//					funcionario.setCampo_secreto(TransformaStringMD5.md5(funcionario.getCampo_secreto()));
//					Msg.addMsgInfo("Usuário Ativado Com Sucesso");
//					dao.atualiza(funcionario);
//					System.out.println("------------------------------------------------------OK!!!!!!!!!!!");
//				} else {
//					System.out.println("-------------------------------------------------------Algum erro ocorreu e não foi possível ativar o funcionário. Por Favor, contate a administração.");
//				}
//			return null;
//				
//		}
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
	
	
	
	/** Getters and Setters **/

	
	
}
