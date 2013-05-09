package ufrr.editora.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import ufrr.editora.converter.BaseEntity;

@Entity
@Table(name="tb_produto")
public class Produto implements Serializable, BaseEntity {
		
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String nome; // SE O PRODUTO FOR LIVRO ESTE VAI SER O TITULO DA OBRA
	
	private String editora;
	
	private String autor;
	
	private Long isbn; // CODIGO DE BARRAS COM 13 DIGITOS
	
	private Integer quantidadeMinima; // QUANTIDADE MÍNIMA NO ESTOQUE
	
	private Boolean ativado;
	
	@ManyToOne
	@JoinColumn(name="fk_tipo")
	private TipoProduto tipo;
	
	@ManyToOne
	@JoinColumn(name="fk_categoria")
	private Categoria categoria;
	
	@Transient
	@ManyToOne
	private Usuario usuario;

	
	 /** Get and Set **/

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

	public TipoProduto getTipo() {
		return tipo;
	}

	public void setTipo(TipoProduto tipo) {
		this.tipo = tipo;
	}

	public String getEditora() {
		return editora;
	}

	public void setEditora(String editora) {
		this.editora = editora.toUpperCase();
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor.toUpperCase();
	}

	public Long getIsbn() {
		return isbn;
	}

	public void setIsbn(Long isbn) {
		this.isbn = isbn;
	}

	public Integer getQuantidadeMinima() {
		return quantidadeMinima;
	}

	public void setQuantidadeMinima(Integer quantidadeMinima) {
		this.quantidadeMinima = quantidadeMinima;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Boolean getAtivado() {
		return ativado;
	}

	public void setAtivado(Boolean ativado) {
		this.ativado = ativado;
	}
	
	

}
