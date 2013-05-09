package ufrr.editora.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Email;

import ufrr.editora.converter.BaseEntity;

@Entity
@Table(name="tb_fornecedor")
public class Fornecedor implements Serializable, BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="cnpj_cpf")
	private String cnpj;
	
	private String nome;
	
	private String telefone1;
	
	private String telefone2;
	
	@Email
	private String email;
	
	private Integer porcentagem;
	
	private Long pis;
	
	private Integer banco;
	
	private String agencia;
	
	private String conta;
	
	private String titularConta;
	
	private String tipoConta;
	
	private String operacao; // apenas para conta da caixa
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="fk_endereco")
	private Endereco endereco;
	
	@Transient
	@ManyToOne
	private Usuario usuario;
		
	/** get and set **/

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome.toUpperCase();
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

	public void setTelefone2(String telefone2) {
		this.telefone2 = telefone2;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email.toLowerCase();
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public Integer getPorcentagem() {
		return porcentagem;
	}

	public void setPorcentagem(Integer porcentagem) {
		this.porcentagem = porcentagem;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Long getPis() {
		return pis;
	}

	public void setPis(Long pis) {
		this.pis = pis;
	}

	public Integer getBanco() {
		return banco;
	}

	public void setBanco(Integer banco) {
		this.banco = banco;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia.toUpperCase();
	}

	public String getConta() {
		return conta;
	}

	public void setConta(String conta) {
		this.conta = conta.toUpperCase();
	}

	public String getTitularConta() {
		return titularConta;
	}

	public void setTitularConta(String titularConta) {
		this.titularConta = titularConta.toUpperCase();
	}

	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	public String getTipoConta() {
		return tipoConta;
	}

	public void setTipoConta(String tipoConta) {
		this.tipoConta = tipoConta;
	}
	
	

}
