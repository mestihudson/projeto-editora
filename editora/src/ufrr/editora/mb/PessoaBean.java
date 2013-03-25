package ufrr.editora.mb;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ufrr.editora.entity.Endereco;
import ufrr.editora.entity.Pessoa;
import ufrr.editora.entity.Usuario;

@ManagedBean
@ViewScoped
public class PessoaBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Pessoa pessoa = new Pessoa();
	private Usuario usuario = new Usuario();
	private Endereco endereco = new Endereco();
	
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

	
	
	

}
