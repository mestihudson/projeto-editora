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
import ufrr.editora.entity.ItemDevolvido;
import ufrr.editora.entity.NotaFiscal;
import ufrr.editora.entity.Usuario;
import ufrr.editora.util.Msg;

@ManagedBean
@ViewScoped
public class ItemBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	private Item item = new Item();
	private NotaFiscal notaFiscal = new NotaFiscal();
	private ItemDevolvido devolvido = new ItemDevolvido();
	private List<Item> itens;
	private List<Item> itens1;
	private DAO<Item> dao = new DAO<Item>(Item.class);
	private DAO<ItemDevolvido> dao2 = new DAO<ItemDevolvido>(ItemDevolvido.class);
	
	private Integer totalProduto;
	
	private Integer retorno;
	
	/** list **/
	
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
		
	// estoque que n�o aparece valores zerados
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

	
	// lista para a consulta de itens
	
	public List<Item> getItemNotaFiscal() {
		itens1 = new ArrayList<Item>();
		List<Item> item = new ArrayList<Item>();
		item = this.getItens();
		for (int i = 0; i < item.size(); i++) {
				if (item.get(i).getNotaFiscal().getStatus().equals(true)) {
					itens1.add(item.get(i));
			}
		}
		return itens1;
	}
	
	/** calculo */
	
	// m�todo para somar a quantidade de entrada
	public Integer getTotalEntrada() {
		setTotalProduto(0);
		for (Item i : getEstoque()) {
			setTotalProduto(getTotalProduto() + i.getQuantidadeEntrada());
		}
		return totalProduto;
	}
	
	// m�todo para somar a quantidade de saida
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
	
	/** actions **/
	
	// devolve ao estoque quantidade informada
	// somente caso de troca ou devolu��o
	
	public String updateItem() {
		if (item.getId() != null) {
			if(item.getQuantidadeSaida()==0) {
				Msg.addMsgFatal("Nao houve venda deste produto para retorna-lo");
			}else {
				this.getDevolvido().setUsuario(this.loginBean.getUsuario());
				DAO<Usuario> UDao = new DAO<Usuario>(Usuario.class);
				Usuario u = UDao.buscaPorId(this.loginBean.getUsuario().getId());
				u.getItensDevolvidos().add(devolvido);
				
				Msg.addMsgInfo("Produto devolvido ao estoque");
				System.out.println("...produto devolvido ao estoque");
				item.setQuantidadeSaida(getItem().getQuantidadeSaida()-getRetorno());
				devolvido.setItem(item);
				devolvido.setRetorno(getRetorno());
				dao.atualiza(item);
				dao2.adiciona(devolvido);
				this.item = new Item();
				this.devolvido = new ItemDevolvido();
			}
		} else {
			System.out.println("...Nao foi possivel devolver produto ao estoque");
			Msg.addMsgFatal("Nao foi possivel concluir operacao");
		}
		return "/pages/estoque/devolverEstoque.xhtml";
	}
	
	// devolve ao estoque quantidade informada
		public void getDevolverEstoque() {
			if (item.getId()!= null) {
				dao.atualiza(item);
				
			} else {
				Msg.addMsgFatal("Nao foi possivel concluir operacao");
			}
			
		}
	

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

	public List<Item> getItens1() {
		return itens1;
	}

	public void setItens1(List<Item> itens1) {
		this.itens1 = itens1;
	}

	public Integer getRetorno() {
		return retorno;
	}

	public void setRetorno(Integer retorno) {
		this.retorno = retorno;
	}

	public ItemDevolvido getDevolvido() {
		return devolvido;
	}

	public void setDevolvido(ItemDevolvido devolvido) {
		this.devolvido = devolvido;
	}

	public DAO<ItemDevolvido> getDao2() {
		return dao2;
	}

	public void setDao2(DAO<ItemDevolvido> dao2) {
		this.dao2 = dao2;
	}

	public NotaFiscal getNotaFiscal() {
		return notaFiscal;
	}

	public void setNotaFiscal(NotaFiscal notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

}
