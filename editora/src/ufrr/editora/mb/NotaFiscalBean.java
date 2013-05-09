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
	private String search, resultValidarHCM;
	
	// método para cadastrar nota
	public void addNota() {
		DAO<NotaFiscal> dao = new DAO<NotaFiscal>(NotaFiscal.class);
		Msg.addMsgInfo("Nota Fiscal cadastrada com sucesso");
		dao.adiciona(notaFiscal);
		item = new Item();
		notaFiscal = new NotaFiscal();
	}
	
	// método para adicionar itens a nota fiscal
	public void guardaItem() {

		DAO<Produto> dao = new DAO<Produto>(Produto.class);

		Produto produto = dao.buscaPorId(idProduto);
		item.setProduto(produto);

		notaFiscal.getItens().add(item);
		item.setNotaFiscal(notaFiscal);

		item = new Item();
	}
	
	// método para remover o item da lista de itens no cadastro de nota fiscal
	public void removeItem() {
		DAO<Item> dao = new DAO<Item>(Item.class);
		dao.remove(item);
	}
	
	/** validations **/

	//validação para não cadastrar nº de nota fiscal para o mesmo fornecedor
	public boolean validarHcmAndCecor() {
			Query q = dao.query("SELECT n FROM NotaFiscal n WHERE numero = ? and fornecedor = ?");
			q.setParameter(1, notaFiscal.getNumero());
			q.setParameter(2, notaFiscal.getFornecedor());

			if (!q.getResultList().isEmpty()) {
				resultValidarHCM = "Está nota fiscal já possui registro no sistema";
				return false;
			} else {
				resultValidarHCM = "";
				return true;
			}
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

	public String getResultValidarHCM() {
		return resultValidarHCM;
	}

	public void setResultValidarHCM(String resultValidarHCM) {
		this.resultValidarHCM = resultValidarHCM;
	}
	
	

}
