package ufrr.editora.mb;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.Query;

import ufrr.editora.dao.DAO;
import ufrr.editora.entity.ItemVenda;
import ufrr.editora.util.Msg;

@ManagedBean
@ViewScoped
public class itemVendaBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	private ItemVenda itemVenda = new ItemVenda();
	private List<ItemVenda> itensVendas;
	private List<ItemVenda> itensVendas1;
	private DAO<ItemVenda> dao = new DAO<ItemVenda>(ItemVenda.class);
	
	private Integer totalProdutoVendido;
	private Double totalProdutoValor;
	
	public List<ItemVenda> getItensVendas() {
		if (itensVendas == null) {
			System.out.println("Carregando itens de vendas...");
			itensVendas = new DAO<ItemVenda>(ItemVenda.class).getAllOrder("item.produto.nome, item.produto.isbn");
		}
		return itensVendas;
	}
	
	// serve para prestação de conta
		@SuppressWarnings("unchecked")
		public String getPrestacaoByFornecedorAndData() {
			try {
				Query query = dao.query("SELECT i FROM ItemVenda i WHERE i.item.notaFiscal.fornecedor = ? and i.venda = ?");
				query.setParameter(1, itemVenda.getItem().getNotaFiscal().getFornecedor());
				query.setParameter(2, itemVenda.getVenda());
				itensVendas = query.getResultList();
				
				if (getItensVendas().isEmpty()) {
					Msg.addMsgError("Nenhuma venda efetuada para este fornecedor na data informada");
					return null;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	
	// método para somar a quantidade dos itens vendidos
	public Integer getTotalProdutos() {
		setTotalProdutoVendido(0);
		for (ItemVenda i : getItensVendas()) {
			setTotalProdutoVendido(getTotalProdutoVendido() + i.getQuantidade());
		}
		return totalProdutoVendido;
	}
	
	// método para somar a quantidade dos itens vendidos
		public Double getTotalValor() {
			setTotalProdutoValor(0.00);
			for (ItemVenda i : getItensVendas()) {
				setTotalProdutoValor(getTotalProdutoValor() + i.getTotalProdutoVenda());
			}
			return totalProdutoValor;
		}
		
	// variável para exibir a soma do total dos produtos
//	public Double getValorTotal() {
//		setTotalValor(00.00);
//		for (Item i : getNotaFiscal().getItens()) {
//			setTotalValor(getTotalValor() + i.getTotalPro());
//		}
//		return totalValor;
//	}
	
	// método para somar a quantidade de saida
//	public Integer getTotalSaida() {
//		setTotalProduto(0);
//		for (Item i : getEstoque()) {
//			setTotalProduto(getTotalProduto() + i.getQuantidadeSaida());
//		}
//		return totalProduto;
//	}
	
	
	// Exibe uma lista de itens
//		@SuppressWarnings("unchecked")
//		public List<Item> getItens2() {
//			Query query = dao.query("SELECT i FROM Item i ORDER BY i.produto.nome");
//			itensVendas = query.getResultList();
//			System.out.println("Total de Itens: " + getItens().size());
//			return query.getResultList();
//		}
	
//	public List<Item> getEstoque() {
//		itens1 = new ArrayList<Item>();
//		List<Item> item = new ArrayList<Item>();
//		item = this.getItens();
//		for (int i = 0; i < item.size(); i++) {
//			if (item.get(i).getQuantidadeEntrada() <= item.get(i).getQuantidadeSaida()) {
//
//			}else {
//				if (item.get(i).getNotaFiscal().getStatus().equals(true)) {
//					itens1.add(item.get(i));
//				}
//			}
//		}
//		return itens1;
//	}	
		

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public ItemVenda getItemVenda() {
		return itemVenda;
	}

	public void setItemVenda(ItemVenda itemVenda) {
		this.itemVenda = itemVenda;
	}

	public List<ItemVenda> getItensVendas1() {
		return itensVendas1;
	}

	public void setItensVendas1(List<ItemVenda> itensVendas1) {
		this.itensVendas1 = itensVendas1;
	}

	public DAO<ItemVenda> getDao() {
		return dao;
	}

	public void setDao(DAO<ItemVenda> dao) {
		this.dao = dao;
	}
	
	public Integer getTotalProdutoVendido() {
		return totalProdutoVendido;
	}

	public void setTotalProdutoVendido(Integer totalProdutoVendido) {
		this.totalProdutoVendido = totalProdutoVendido;
	}

	public void setItensVendas(List<ItemVenda> itensVendas) {
		this.itensVendas = itensVendas;
	}

	public Double getTotalProdutoValor() {
		return totalProdutoValor;
	}

	public void setTotalProdutoValor(Double totalProdutoValor) {
		this.totalProdutoValor = totalProdutoValor;
	}	

}
