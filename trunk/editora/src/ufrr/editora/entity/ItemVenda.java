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
@Table(name = "tb_item_venda")
public class ItemVenda implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Item item;
	
	@ManyToOne
	private Venda venda;

	private Integer quantidade;
	
	@Transient
	private Boolean quantidadeN; // variável para não deixar cadastrar produtos com quantidade maior ao estoque

	// get and set

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Venda getVenda() {
		return venda;
	}

	public void setVenda(Venda venda) {
		this.venda = venda;
	}
	
	public Boolean getQuantidadeN() {
		if (item.getQuantidadeEntrada() - item.getQuantidadeSaida() - quantidade <= 0) 
			return true;
		else 
			return false;
	}

	public void setQuantidadeN(Boolean quantidadeN) {
		this.quantidadeN = quantidadeN;
	}

	// variável para exibir o total valor do produto
	public Double getTotal() {
		if (quantidade != null && item.getValorVenda() != null)
			return quantidade * item.getValorVenda();
		else
			return null;
	}

}
