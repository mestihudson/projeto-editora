package ufrr.editora.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Email;

import ufrr.editora.converter.BaseEntity;

@Entity
@Table(name="tb_usuario")
public class Usuario implements Serializable, BaseEntity {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String nome;
	
	private String cpf;
	
	@Column(name="data_nascimento")
	@Temporal(TemporalType.TIMESTAMP)
	private Date nascimento;
	
	private String universitario; //ALUNO, PROFESSOR, SERVIDOR UFRR, OUTROS
	
	@Column(name="local_trabalho")
	private String localTrabalho;
	
	private String telefone1;
	
	private String telefone2;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="fk_endereco")
	private Endereco endereco;
	
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
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="usuario")
	private Collection<Mensagem> mensagens = new ArrayList<Mensagem>();
		
	
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
		this.login = login.toLowerCase();
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
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

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome.toUpperCase();
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Date getNascimento() {
		return nascimento;
	}

	public void setNascimento(Date nascimento) {
		this.nascimento = nascimento;
	}

	public String getUniversitario() {
		return universitario;
	}

	public void setUniversitario(String universitario) {
		this.universitario = universitario;
	}

	public String getLocalTrabalho() {
		return localTrabalho;
	}

	public void setLocalTrabalho(String localTrabalho) {
		this.localTrabalho = localTrabalho.toUpperCase();
	}

	public String getTelefone1() {
		return telefone1;
	}

	public void setTelefone1(String telefone1) {
		this.telefone1 = telefone1;
	}

	public String getTelefone2() {
		return telefone2;
	}

	public Collection<Mensagem> getMensagens() {
		return mensagens;
	}

	public void setMensagens(Collection<Mensagem> mensagens) {
		this.mensagens = mensagens;
	}

	public void setTelefone2(String telefone2) {
		this.telefone2 = telefone2;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	

	
}
