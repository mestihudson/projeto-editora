package ufrr.editora.mb;

import java.io.Serializable;
import java.util.ArrayList;
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
	private List<Usuario> usuarios;
	private List<Pessoa> pessoas;
	private List<Pessoa> usuariosNulls;
	private DAO<Pessoa> dao = new DAO<Pessoa>(Pessoa.class);
	
	public Boolean cadastro = true;
	
	/** List of peoples **/
	public List<Pessoa> getPessoas() {
		if (pessoas == null) {
			System.out.println("Carregando pessoas...");
			pessoas = new DAO<Pessoa>(Pessoa.class).getAllOrder("nome");
		}
		return pessoas;
	}
	
	// Exibe uma lista com o usuario de status null
	public List<Pessoa> getUsuarioNulls() {
		usuariosNulls = new ArrayList<Pessoa>();
		for (Pessoa p : this.getPessoas()) {
			if (p.getId() == 1) {
				usuariosNulls.add(p);
			}
		}
		return usuariosNulls;
	}
	
	//solicitacao de cadastro
	public String addPessoa() {
		for (Pessoa pessoas : this.getPessoas()) {
			if (pessoas.getCpf().equalsIgnoreCase(this.getPessoa().getCpf())) {
				this.cadastro = false;
				break;
			}
		}

		if (this.cadastro == true) {
			if (getUsuario().getSenha().equalsIgnoreCase(this.getUsuario().getRepetirSenha())) {
//				pessoa.getUsuario().getPerfil().setId((long) 1);
				pessoa.setUsuario(usuario);
				pessoa.setEndereco(endereco);
				dao.adiciona(pessoa);
				Msg.addMsgInfo("Solicitação de acesso enviada com sucesso. Aguarde autorização!");
				this.pessoa = new Pessoa();
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
		pessoas = dao.getAllOrder("nome");
		this.cadastro = true;
		return null;
	}
	
	//atualiza usuário
	public String updatePessoa() {
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
		return null;
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

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public List<Pessoa> getUsuariosNulls() {
		return usuariosNulls;
	}

	public void setUsuariosNulls(List<Pessoa> usuariosNulls) {
		this.usuariosNulls = usuariosNulls;
	}

	

}
