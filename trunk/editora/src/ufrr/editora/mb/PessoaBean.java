package ufrr.editora.mb;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ufrr.editora.dao.DAO;
import ufrr.editora.entity.Endereco;
import ufrr.editora.entity.Pessoa;
import ufrr.editora.entity.Usuario;
import ufrr.editora.util.Msg;

@ManagedBean
@ViewScoped
public class PessoaBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Pessoa pessoa = new Pessoa();
	private Usuario usuario = new Usuario();
	private Endereco endereco = new Endereco();
	private List<Pessoa> pessoas;
	private DAO<Pessoa> dao = new DAO<Pessoa>(Pessoa.class);
	
	public Boolean cadastro = true;
	
	//solicitacao de cadastro
	public String addPessoa() {
//		for (Pessoa pessoas : this.getPessoas()) {
//			if(pessoas.getCpf().equalsIgnoreCase(this.getPessoa().getCpf())) { 
//				this.cadastro = false;
//				break;
//			}
//		}
		if(this.cadastro == true){
			if(this.pessoa.getUsuario().getLogin().isEmpty() && this.pessoa.getUsuario().getLogin().length() > 10 ){
				Msg.addMsgError("Seu login será seu email, informe um email correto para proseguir com o cadastro");
			}	
		if(this.cadastro == true){
			if (getUsuario().getSenha().equals(this.getUsuario().getRepetirSenha())) {
				pessoa.setUsuario(usuario);
				pessoa.setEndereco(endereco);
				dao.adiciona(pessoa);
				Msg.addMsgInfo("Solicitação de cadastro enviada com sucesso. Aguarde ativação!");
				this.pessoa = new Pessoa();
				this.usuario = new Usuario();
				return "index.xhtml";
			} else {
				Msg.addMsgError("Senhas diferentes. Por favor, verifique os campos e digite novamente.");
			}
		} else {
			if(this.usuario.getLogin() == null){
				Msg.addMsgError("Seu login será seu email, informe um email para proseguir com o cadastro");
			} else {
				Msg.addMsgError("Este cadastro já existe");
			}
			return "solicitacao.xhtml";
			}
		}
		return "solicitacao.xhtml";
	}
	
	//atualiza usuário
	public String updatePessoa() {
//		for (Pessoa pessoas : this.getPessoas()) {
//			if(pessoas.getCpf().equalsIgnoreCase(this.getPessoa().getCpf())) { 
//				this.cadastro = false;
//				break;
//			}
//		}
		if(this.cadastro == true){
			if(this.pessoa.getUsuario().getLogin().isEmpty() && this.pessoa.getUsuario().getLogin().length() > 10 ){
				Msg.addMsgError("Seu login será seu email, informe um email correto para proseguir com o cadastro");
			}	
		if(this.cadastro == true){
			if (getUsuario().getSenha().equals(this.getUsuario().getRepetirSenha())) {
				pessoa.setUsuario(usuario);
				pessoa.setEndereco(endereco);
				dao.atualiza(pessoa);
				Msg.addMsgInfo("Cadastro atualizado com sucesso");
				return "/pages/usuario/senhaCadastro.xhtml";
			} else {
				Msg.addMsgError("Senhas diferentes. Por favor, verifique os campos e digite novamente.");
			}
		} else {
			if(this.usuario.getLogin() == null){
				Msg.addMsgError("Seu login será seu email, informe um email para proseguir com atualização");
			} else {
				Msg.addMsgError("Este cadastro já existe");
			}
			return "solicitacao.xhtml";
			}
		}
		return "solicitacao.xhtml";
	}
	
		
	/** get and set **/
	
	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Pessoa> getPessoas() {
		return pessoas;
	}

	public void setPessoas(List<Pessoa> pessoas) {
		this.pessoas = pessoas;
	}

	public DAO<Pessoa> getDao() {
		return dao;
	}

	public void setDao(DAO<Pessoa> dao) {
		this.dao = dao;
	}

	public Boolean getCadastro() {
		return cadastro;
	}

	public void setCadastro(Boolean cadastro) {
		this.cadastro = cadastro;
	}

	
	
	

}
