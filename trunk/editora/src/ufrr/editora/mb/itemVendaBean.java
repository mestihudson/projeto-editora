package ufrr.editora.mb;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.Query;

import ufrr.editora.dao.DAO;
import ufrr.editora.entity.Item;
import ufrr.editora.entity.ItemVenda;
import ufrr.editora.util.Msg;

@ManagedBean
@ViewScoped
public class itemVendaBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	private ItemVenda itemVenda = new ItemVenda();
	private Item item = new Item();
	private List<ItemVenda> itensVendas;
	private List<ItemVenda> itensVendas1;
	private DAO<ItemVenda> dao = new DAO<ItemVenda>(ItemVenda.class);
	
	private Integer totalProdutoVendido;
	private Double totalProdutoValor;
	
	private String search;
	private Integer box4Search;
	
	private Integer mes;
	
	private Integer ano;
	
	@PostConstruct
	public void init() {

		search = "";
		box4Search = 1;
		box4Search = 2;
		box4Search = 3;
	}
	
//	
//	// Exibe uma lista com as solicitacoes de acesso
//		@SuppressWarnings("unchecked")
//		public List<Usuario> getContas() {
//			Query query = dao.query("SELECT i FROM ItemVenda i ORDERBY i.produto.nome, i.produto.isbn");
//			itensVendas1 = query.getResultList();
//			return query.getResultList();
//		}
	
	
	/** list **/
	
	public List<ItemVenda> getItensVendas() {
		if (itensVendas == null) {
			System.out.println("Carregando itens de vendas...");
			itensVendas = new DAO<ItemVenda>(ItemVenda.class).getAllOrder("item.produto.nome, item.produto.isbn");
		}
		return itensVendas;
	}	
	
	// Pesquisa venda pelo cpf
	public String getListaVendaFornecedorByCPF() {
		if (box4Search.equals(1)) {
			itensVendas1 = dao.getAllByName(
					"obj.item.notaFiscal.fornecedor.cnpj", search);
			if (itensVendas1.isEmpty()) {
				init();
				Msg.addMsgError("NENHUMA VENDA EFETUADA PARA ESTE CPF");
				return null;
			} else {
				return null;
			}
		}
		return null;
	}
	
	// Pesquisa venda pelo cpf
		public String getListaVendaFornecedorByCNPJ() {
			if (box4Search.equals(2)) {
				itensVendas1 = dao.getAllByName(
						"obj.item.notaFiscal.fornecedor.cnpj", search);
				if (itensVendas1.isEmpty()) {
					init();
					Msg.addMsgError("NENHUMA VENDA EFETUADA PARA ESTE CPF");
					return null;
				} else {
					return null;
				}
			}
			return null;
		}
		
	// Pesquisa venda pelo cliente, data e vendedor
	public String getListaVendaFornecedorByNome() {

			if (box4Search.equals(4)) {
					if (search.length() <= 4) {
						init();
						Msg.addMsgError("INFORME 5 CARACTERES PARA PESQUISA");
						return null;
					} else {
						itensVendas1 = dao.getAllByName("obj.item.notaFiscal.fornecedor.nome", search);
						if (itensVendas1.isEmpty()) {
							init();
							Msg.addMsgError("NENHUMA VENDA EFETUADA PARA ESTE FORNECEDOR");
							return null;
						} else {
							return null;
						}
					}
				}
			return null;
	}
	
	// presta��o de conta
	// Consulta pelo fornecedor
	
	// (este m�todo deve funcionar, com a consulta do fornecedor e em seguida o mes) - Falta fazer o mes!!!
		public String getLista() {
			
			if (box4Search.equals(1)) {
				if (search.length() <= 4) {
					init();
					Msg.addMsgError("INFORME 5 CARACTERES PARA PESQUISA");
					return null;
				} else {
					itensVendas1 = dao.getAllByName("obj.item.notaFiscal.fornecedor.nome", search);
					itensVendas1 = dao.getAllOrder("item.produto.nome, item.produto.isbn");
					if (itensVendas1.isEmpty()) {
						init();
						Msg.addMsgError("NENHUMA VENDA EFETUADA!");
					} else {
						return null;
					}
				}

			}
			return null;
		}
	
	//  Preta��o de conta com fornecedor	
		@SuppressWarnings("unchecked")
		public String getPrestacaoByDate() {
				try {
					Query query = dao.query("SELECT v FROM ItemVenda v WHERE extract(month from v.venda.dataVenda) = ? and extract(year from v.venda.dataVenda) = ? order by v.venda.dataVenda");
					query.setParameter(1, getMes());
					query.setParameter(2, getAno());
					itensVendas1 = dao.getAllByName("obj.item.notaFiscal.fornecedor.nome", search);
					itensVendas1 = query.getResultList();
					
					// n�o est� fazendo filtragem com o fornecedor, somente com o m�s e ano!
					// n�o houve sucesso ao tentar fazer pelo sql ex: itemVenda.item.notaFiscal.fornecedor.ano 'returned null' 'item'
					
					if (itensVendas1.isEmpty()) {
						init();
						Msg.addMsgError("NENHUMA VENDA EFETUADA PARA OS DADOS INFORMADOS");
					} else {
						return null;
					}

				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("... erro na consulta do caixa");
				}
				return null;
		}
	
	/** calculo **/
	
	// m�todo para somar a quantidade dos itens vendidos
	public Integer getTotalProdutos() {
		setTotalProdutoVendido(0);
		for (ItemVenda i : getItensVendas()) {
			setTotalProdutoVendido(getTotalProdutoVendido() + i.getQuantidade());
		}
		return totalProdutoVendido;
	}
	
	// m�todo para somar a quantidade dos itens vendidos
		public Double getTotalValor() {
			setTotalProdutoValor(0.00);
			for (ItemVenda i : getItensVendas1()) {
				setTotalProdutoValor(getTotalProdutoValor() + i.getTotalProdutoVenda());
			}
			return totalProdutoValor;
		}
				

	/** get and set **/
		
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

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}	

}
