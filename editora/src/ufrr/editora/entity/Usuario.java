package ufrr.editora.entity;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Email;

@Entity
@Table(name="tb_usuario")
public class Usuario {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Email
	private String login; // login = Pessoa.email
	
	private String senha;
	
	@Transient
	private String repetirSenha;
	
	private Boolean status;
	
	@ManyToOne
	private Perfil perfil;
	
	@Transient
	@OneToMany(cascade=CascadeType.ALL, mappedBy="usuario")
	private Collection<Fornecedor> fornecedores = new ArrayList<Fornecedor>();
	
	@Transient
	@OneToMany(cascade=CascadeType.ALL, mappedBy="usuario")
	private Collection<Produto> produtos = new ArrayList<Produto>();
	
	@OneToOne(mappedBy="usuario", fetch=FetchType.EAGER)
	private Pessoa pessoa;
	
	
//	get and set

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public boolean isStatus() {
		return status;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	public Collection<Fornecedor> getFornecedores() {
		return fornecedores;
	}

	public void setFornecedores(Collection<Fornecedor> fornecedores) {
		this.fornecedores = fornecedores;
	}

	public Collection<Produto> getProdutos() {
		return produtos;
	}

	public void setProdutos(Collection<Produto> produtos) {
		this.produtos = produtos;
	}

	public String getRepetirSenha() {
		return repetirSenha;
	}

	public void setRepetirSenha(String repetirSenha) {
		this.repetirSenha = repetirSenha;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
	
}
