package ufrr.editora.mb;

import java.io.Serializable;
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
	private DAO<Item> dao = new DAO<Item>(Item.class);
	
	public List<Item> getItens() {
		if (itens == null) {
			System.out.println("Carregando itens...");
			itens = new DAO<Item>(Item.class).getAllOrder("notaFiscal.numero, id");
		}
		return itens;
	}
	
	// Exibe uma lista de itens
		@SuppressWarnings("unchecked")
		public List<Item> getItensProduto() {
			Query query = dao.query("SELECT i count(item.produto) FROM Item i");
			itens = query.getResultList();
			System.out.println("Total de Itens: " + getItens().size());
			return query.getResultList();
		}
		
//		sql
//		select count(item.produto_id) as "Registros",
//	     produto.nome as "Descricao",
//	     sum(item.quantidadeEntrada) as "Soma da quantidade de entrada"
//	     from 	tb_item 	item,
//	     tb_produto	produto
//	     where 	item.produto_id = produto.id
//	     and	item.produto_id in (25, 29, 30, 31, 32, 33, 34)
//
//		group by item.produto_id,
//		produto.nome

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
	
	
	

}
