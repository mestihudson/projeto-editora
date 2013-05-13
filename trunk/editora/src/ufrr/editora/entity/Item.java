package ufrr.editora.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="tb_item")
public class Item implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private Integer quantidade;
	
	private Double valorCusto;
	
	private Double valorVenda;
	
	@ManyToOne
	private Produto produto;
	
	@ManyToOne
	private NotaFiscal notaFiscal;
	
	@Transient
	private Double totalValor;
	
	//get and set

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Double getValorCusto() {
		return valorCusto;
	}

	public void setValorCusto(Double valorCusto) {
		this.valorCusto = valorCusto;
	}

	public Double getValorVenda() {
		return valorVenda;
	}

	public void setValorVenda(Double valorVenda) {
		this.valorVenda = valorVenda;
	}

	public NotaFiscal getNotaFiscal() {
		return notaFiscal;
	}

	public void setNotaFiscal(NotaFiscal notaFiscal) {
		this.notaFiscal = notaFiscal;
	}
	
	public Double getTotalValor() {
		return totalValor;
	}

	public void setTotalValor(Double totalValor) {
		this.totalValor = totalValor;
	}

	// vari�vel para exibir o total R$ dos produtos
	public Double getTotal() {
		if (quantidade != null && valorCusto != null)
			return quantidade * valorCusto;
		else
			return null;	
	}
	
	// vari�vel para exibir a soma do total dos produtos
		public Double getValorTotalProdutos() {
			setTotalValor(00.00);
			for (Item i : getNotaFiscal().getItens()) {
				setTotalValor(getTotalValor() + i.getTotal());
			}
			return totalValor;
		}
}
