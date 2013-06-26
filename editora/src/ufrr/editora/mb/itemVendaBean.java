package ufrr.editora.mb;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
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
	
	private String search;
	private Integer box4Search;
	
	@PostConstruct
	public void init() {

		search = "";
		box4Search = 1;
	}
	
	public List<ItemVenda> getItensVendas() {
		if (itensVendas == null) {
			System.out.println("Carregando itens de vendas...");
			itensVendas = new DAO<ItemVenda>(ItemVenda.class).getAllOrder("item.produto.nome, item.produto.isbn");
		}
		return itensVendas;
	}	
//	
//	// Exibe uma lista com as solicitações de acesso
//		@SuppressWarnings("unchecked")
//		public List<Usuario> getContas() {
//			Query query = dao.query("SELECT i FROM ItemVenda i ORDERBY i.produto.nome, i.produto.isbn");
//			itensVendas1 = query.getResultList();
//			return query.getResultList();
//		}
	
	// Pesquisa pelo fornecedor
		public String getLista() {
			
			if (box4Search.equals(1)) {
				if (search.length() <= 4) {
					init();
					Msg.addMsgError("Informe 5 caracteres para pesquisa");
					return null;
				} else {
					itensVendas1 = dao.getAllByName("obj.item.notaFiscal.fornecedor.nome", search);
					itensVendas1 = dao.getAllOrder("item.produto.nome, item.produto.isbn");
					if (itensVendas1.isEmpty()) {
						init();
						Msg.addMsgError("Nenhum registro encontrado");
					} else {
						return null;
					}
				}

			}
			return null;
		}
	
	@SuppressWarnings("unchecked")
	public String getPrestacaoByFornecedorAndData() {
		try {
			Query query = dao.query("SELECT i FROM ItemVenda i WHERE i.item =?");
			query.setParameter(1, itemVenda.getVenda());
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

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public Integer getBox4Search() {
		return box4Search;
	}

	public void setBox4Search(Integer box4Search) {
		this.box4Search = box4Search;
	}	

}
