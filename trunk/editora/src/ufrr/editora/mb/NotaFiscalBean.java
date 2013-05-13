package ufrr.editora.mb;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.Query;

import ufrr.editora.dao.DAO;
import ufrr.editora.entity.Item;
import ufrr.editora.entity.NotaFiscal;
import ufrr.editora.entity.Produto;
import ufrr.editora.util.Msg;

@ManagedBean
@ViewScoped
public class NotaFiscalBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private NotaFiscal notaFiscal = new NotaFiscal();
	private Item item = new Item();
	private Long idProduto;
	private DAO<NotaFiscal> dao = new DAO<NotaFiscal>(NotaFiscal.class);
	private String search, resultValidarUK;
	private Double totalValor;
	
	
	// m�todo para cadastrar nota
	public String addNota() {
		boolean all = true;
		if (!validarNota()) {
			all = false;
		}
		if (notaFiscal.getValor() == null || notaFiscal.getValor() == 0) {
			Msg.addMsgError("Informe o valor total da nota fiscal");
			all = false;
		}
		if (notaFiscal.getItens().isEmpty()) {
			Msg.addMsgError("N�o � poss�vel cadastrar nota fiscal sem produto");
			all = false;
		}
		if (!all) {
			System.out.println("...Erro ao cadastrar nota: nota fiscal j� existe");
		} else {
//			if (item.getNotaFiscal().getValor().equals(getItem().getTotalValor())) {
				DAO<NotaFiscal> dao = new DAO<NotaFiscal>(NotaFiscal.class);
				Msg.addMsgInfo("Nota Fiscal cadastrada com sucesso");
				dao.adiciona(notaFiscal);
				item = new Item();
				notaFiscal = new NotaFiscal();
				return "/pages/notafiscal/cadastrarNotaFiscal.xhtml";	
		}
		return null;
	}
	
	// m�todo para adicionar itens a nota fiscal
	public void guardaItem() {
		
		boolean all = true;
		if(item.getQuantidade() == null || item.getQuantidade() == 0) {
			Msg.addMsgError("Informe a quantidade");
			all = false;
		}
		if(item.getValorCusto() == null || item.getValorCusto() == 0.00) {
			Msg.addMsgError("Informe o valor de custo");
			all = false;
		}
		if(item.getValorVenda() == null || item.getValorVenda() == 0.00) {
			Msg.addMsgError("Informe o valor de venda");
			all = false;
		}
		if (!all) {
				System.out.println("...Erro ao cadastrar nota: inconsistencia nos dados do item");	
		}else {
			DAO<Produto> dao = new DAO<Produto>(Produto.class);
			Produto produto = dao.buscaPorId(idProduto);
			item.setProduto(produto);

			notaFiscal.getItens().add(item);
			item.setNotaFiscal(notaFiscal);

			item = new Item();
		}
	}
	
	// m�todo para remover o item da lista de itens no cadastro de nota fiscal
	public void removeItem() {
		notaFiscal.getItens().remove(item);
		Msg.addMsgWarn("Item removido");
		System.out.println("Item removido...");
		
		item = new Item();
	}
	
	/** validations **/

	// valida��o para n�o cadastrar n� de nota fiscal para o mesmo fornecedor
	public boolean validarNota() {
		Query q = dao.query("SELECT n FROM NotaFiscal n WHERE numero = ? and fornecedor = ?");
		q.setParameter(1, notaFiscal.getNumero());
		q.setParameter(2, notaFiscal.getFornecedor());

		if (!q.getResultList().isEmpty()) {
			Msg.addMsgError("Est� nota fiscal j� possui registro no sistema");
			return false;
		} else {
			resultValidarUK = "";
			return true;
		}
	}
	
	// vari�vel para exibir o total R$ dos produtos
		public Double getTotal() {
			if (item.getQuantidade() != null && item.getValorCusto() != null)
				return item.getQuantidade() * item.getValorCusto();
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
	
	/** get and set **/
	
	public NotaFiscal getNotaFiscal() {
		return notaFiscal;
	}

	public void setNotaFiscal(NotaFiscal notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Long getIdProduto() {
		return idProduto;
	}

	public void setIdProduto(Long idProduto) {
		this.idProduto = idProduto;
	}

	public DAO<NotaFiscal> getDao() {
		return dao;
	}

	public void setDao(DAO<NotaFiscal> dao) {
		this.dao = dao;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getResultValidarUK() {
		return resultValidarUK;
	}

	public void setResultValidarUK(String resultValidarUK) {
		this.resultValidarUK = resultValidarUK;
	}

	public Double getTotalValor() {
		return totalValor;
	}

	public void setTotalValor(Double totalValor) {
		this.totalValor = totalValor;
	}
	
	
	
}
