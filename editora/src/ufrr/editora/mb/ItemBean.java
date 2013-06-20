package ufrr.editora.mb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.Query;

import ufrr.editora.dao.DAO;
import ufrr.editora.entity.Item;

@ManagedBean
@ViewScoped
public class ItemBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	private Item item = new Item();
	private List<Item> itens;
	private List<Item> itens1;
	private DAO<Item> dao = new DAO<Item>(Item.class);
	
	private Integer totalProduto;
	
	// método para somar a quantidade de entrada
	public Integer getTotalEntrada() {
		setTotalProduto(0);
		for (Item i : getEstoque()) {
			setTotalProduto(getTotalProduto() + i.getQuantidadeEntrada());
		}
		return totalProduto;
	}
	
	// método para somar a quantidade de saida
	public Integer getTotalSaida() {
		setTotalProduto(0);
		for (Item i : getEstoque()) {
			setTotalProduto(getTotalProduto() + i.getQuantidadeSaida());
		}
		return totalProduto;
	}
	
	// quantidade atual do total
	public Integer getTotalAtual() {
		return getTotalEntrada() - getTotalSaida();
		
	}
	
	public List<Item> getItens() {
		if (itens == null) {
			System.out.println("Carregando itens...");
			itens = new DAO<Item>(Item.class).getAllOrder("produto.nome, notaFiscal.lote");
		}
		return itens;
	}
	
	// Exibe uma lista de itens
		@SuppressWarnings("unchecked")
		public List<Item> getItens2() {
			Query query = dao.query("SELECT i FROM Item i ORDER BY i.produto.nome");
			itens = query.getResultList();
			System.out.println("Total de Itens: " + getItens().size());
			return query.getResultList();
		}
		
		// Lista de produtos desativados (sem livros)
		public List<Item> getEstoqueCritico() {
			itens1 = new ArrayList<Item>();
			List<Item> item = new ArrayList<Item>();
			item = this.getItens();
			for (int i = 0; i < item.size(); i++) {
				if (item.get(i).getQuantidadeEntrada() == item.get(i).getQuantidadeSaida()) {
				}else {
					if (item.get(i).getProduto().getQuantidadeMinima()>=item.get(i).getQuantidadeAtual() 
							&& item.get(i).getNotaFiscal().getStatus().equals(true)) {
						itens1.add(item.get(i));
					}	
				}
				
			}
			return itens1;
		}	
		
	// Lista de produtos desativados (sem livros)
	public List<Item> getEstoque() {
		itens1 = new ArrayList<Item>();
		List<Item> item = new ArrayList<Item>();
		item = this.getItens();
		for (int i = 0; i < item.size(); i++) {
			if (item.get(i).getQuantidadeEntrada() <= item.get(i).getQuantidadeSaida()) {

			}else {
				if (item.get(i).getNotaFiscal().getStatus().equals(true)) {
					itens1.add(item.get(i));
				}
			}
		}
		return itens1;
	}

		public List<Item> getTeste() {
			itens1 = new ArrayList<Item>();
			List<Item> item = new ArrayList<Item>();
			item = this.getEstoque();
			for (int i = 0; i < item.size(); i++) {
//				if (item.get(i).getQuantidadeSaida()==1) {
				if (item.get(i).getProduto().equals(getItem().getProduto())) {
					if (itens.size() > i++) {
						itens1.remove(item.get(i));
					}else {
						return null;
					}
				}
			}
			return itens1;
		}
	
	//criar outra array lista dentro do método getEstoque
	
		

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public DAO<Item> getDao() {
		return dao;
	}

	public void setDao(DAO<Item> dao) {
		this.dao = dao;
	}

	public void setItens(List<Item> itens) {
		this.itens = itens;
	}

	public Integer getTotalProduto() {
		return totalProduto;
	}

	public void setTotalProduto(Integer totalProduto) {
		this.totalProduto = totalProduto;
	}
	
	
	

}
