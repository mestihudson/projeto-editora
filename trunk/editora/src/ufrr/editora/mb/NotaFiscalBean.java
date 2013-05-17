package ufrr.editora.mb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.Query;

import ufrr.editora.dao.DAO;
import ufrr.editora.entity.Item;
import ufrr.editora.entity.NotaFiscal;
import ufrr.editora.entity.Produto;
import ufrr.editora.util.Msg;
import ufrr.editora.validator.Validator;

@ManagedBean
@ViewScoped
public class NotaFiscalBean implements Serializable {
	
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	private static final long serialVersionUID = 1L;
	private NotaFiscal notaFiscal = new NotaFiscal();
	private Item item = new Item();
	private Long idProduto;
	private List<NotaFiscal> notasFiscais;
	private List<NotaFiscal> notasFiscais1;
	private DAO<NotaFiscal> dao = new DAO<NotaFiscal>(NotaFiscal.class);
	private String search, resultValidarUK;
	private Double totalValor;
	public Boolean cadastro = true;
	private Integer box4Search;
	private Validator<NotaFiscal> validator;

	@PostConstruct
	public void init() {

		notaFiscal = new NotaFiscal();
		validator = new Validator<NotaFiscal>(NotaFiscal.class);
		search = "";
		box4Search = 1;
		box4Search = 2;
	}
	
	// Pesquisa nota fiscal pelo numero e fornecedor
		public String getListaNotaFiscalByNumero() {

			if (box4Search.equals(1)) {
				if (search.contains("'") || search.contains("@")
						|| search.contains("/") || search.contains("*")) {
					init();
					Msg.addMsgError("Contém caractér(es) inválido(s)");
					return null;
				} else {
					if (search.length() <= 0) {
						init();
						Msg.addMsgError("Número inválido. Preencha corretamente o campo marcado para pesquisa");
						return null;
					} else {
						notasFiscais = dao.getAllByName("obj.numero", search);
						if (notasFiscais.isEmpty()) {
							init();
							Msg.addMsgError("Nenhum registro encontrado");
						} else {
							return null;
						}
					}
				}
			}
				else if (box4Search.equals(2)) {
					if (search.length() <= 4) {
						init();
						Msg.addMsgError("Informe 5 caracteres para pesquisa");
						return null;
					} else {
						notasFiscais = dao.getAllByName("obj.fornecedor.nome", search);
						if (notasFiscais.isEmpty()) {
							init();
							Msg.addMsgError("Nenhum registro encontrado");
						} else {
							return null;
						}
					}

			}
			return null;
		}
		
		/** Autocompletes **/

		
	
	/** List NF **/

	public List<NotaFiscal> getNotasFiscais() {
		if (notasFiscais == null) {
			System.out.println("Carregando notas fiscais...");
			notasFiscais = new DAO<NotaFiscal>(NotaFiscal.class).getAllOrder("fornecedor.nome, numero");
		}
		return notasFiscais;
	}
	
	// lista as notas ativadas
		public List<NotaFiscal> getAtivados() {
			notasFiscais1 = new ArrayList<NotaFiscal>();
			List<NotaFiscal> ps = new ArrayList<NotaFiscal>();
			ps = this.getNotasFiscais();
			for (int i = 0; i < ps.size(); i++) {
				if (ps.get(i).getStatus().equals(true)) {
					notasFiscais1.add(ps.get(i));
				}
			}
			return notasFiscais1;
		}
	
	// método para cadastrar nota
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
			Msg.addMsgError("Não é possível cadastrar nota fiscal sem produto");
			all = false;
		}
		if (!all) {
			System.out.println("...Erro ao cadastrar nota: nota fiscal já existe");
		} else {
			if (notaFiscal.getValor().equals(getValorTotalProdutos())) {
				DAO<NotaFiscal> dao = new DAO<NotaFiscal>(NotaFiscal.class);
				Msg.addMsgInfo("Nota Fiscal cadastrada com sucesso");
				notaFiscal.setStatus(true);
				dao.adiciona(notaFiscal);
				item = new Item();
				notaFiscal = new NotaFiscal();
				return "/pages/notafiscal/cadastrarNotaFiscal.xhtml";	
		} else {
			System.out.println("...Erro: o valor da nota fiscal deve ser o mesmo ao total de itens");
			Msg.addMsgError("O valor total da nota fiscal deve ser igual ao valor dos produtos somados");
			return null;
			}
		}
		return null;
	}
		
	// método para adicionar itens a nota fiscal
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
		} else {
			for (Item i : this.getNotaFiscal().getItens()) {
				if(getIdProduto().equals(i.getProduto().getId())){
					this.cadastro = false;
					break;
				}
			}
			if(this.cadastro == true){

			DAO<Produto> dao = new DAO<Produto>(Produto.class);
			Produto produto = dao.buscaPorId(idProduto);
			item.setProduto(produto);

			notaFiscal.getItens().add(item);
			item.setNotaFiscal(notaFiscal);

			item = new Item();
			System.out.println("...Item adicionado com sucesso");
			
			}else {
				Msg.addMsgError("Este produto já foi adicionado");
				System.out.println("...Este produto já foi adicionado");
				item = new Item();
				this.cadastro = true;
		} 
		}
	}
	
	// método para editar/alterar nota
		public String alterNota() {
			boolean all = true;
			if (notaFiscal.getValor() == null || notaFiscal.getValor() == 0) {
				Msg.addMsgError("Informe o valor total da nota fiscal");
				all = false;
			}
			if (notaFiscal.getItens().isEmpty()) {
				Msg.addMsgError("Não é possível cadastrar nota fiscal sem produto");
				all = false;
			}
			if (!all) {
				System.out.println("...Erro ao cadastrar nota: nota fiscal já existe");
			} else {
				if (notaFiscal.getValor().equals(getValorTotalProdutos())) {
					DAO<NotaFiscal> dao = new DAO<NotaFiscal>(NotaFiscal.class);
					Msg.addMsgInfo("Nota Fiscal alterada com sucesso");
					dao.atualiza(notaFiscal);
					notaFiscal = new NotaFiscal();
					return "/pages/notafiscal/cadastrarNotaFiscal.xhtml";	
			} else {
				System.out.println("...Erro: o valor da nota fiscal deve ser o mesmo ao total de itens");
				Msg.addMsgError("O valor total da nota fiscal deve ser igual ao valor dos produtos somados");
				return null;
				}
			}
			return null;
		}
	
	// método para remover o item da lista de itens no cadastro de nota fiscal
	public void removeItem() {
		notaFiscal.getItens().remove(item);
		Msg.addMsgWarn("Item removido");
		System.out.println("Item removido...");
		
		item = new Item();
	}
	
	// desativar/ativar nota fiscal
	public String alterStatus() {
		if (notaFiscal.getStatus().equals(true)) {
			notaFiscal.setStatus(false);
			dao.atualiza(notaFiscal);
			Msg.addMsgInfo("Nota fiscal: " + getNotaFiscal().getNumero() + "\n"
					+ "Fornecedor: " + getNotaFiscal().getFornecedor().getNome() + "\n"
					+ "desativada com sucesso");
			System.out.println("...Nota fiscal desativada");
			return "/pages/notafiscal/consultarNotaFiscal.xhtml";
		} else {
			notaFiscal.setStatus(true);
			dao.atualiza(notaFiscal);
			Msg.addMsgInfo("Nota fiscal: " + getNotaFiscal().getNumero() + "\n"
					+ "Fornecedor: " + getNotaFiscal().getFornecedor().getNome() + "\n"
					+ "reativada com sucesso");
			System.out.println("...Nota fiscal ativada");
			return "/pages/notafiscal/consultarNotaFiscal.xhtml";
		}
	}
	
	/** validations **/

	// validação para não cadastrar nº de nota fiscal para o mesmo fornecedor
	public boolean validarNota() {
		Query q = dao.query("SELECT n FROM NotaFiscal n WHERE numero = ? and fornecedor = ? and status = true");
		q.setParameter(1, notaFiscal.getNumero());
		q.setParameter(2, notaFiscal.getFornecedor());

		if (!q.getResultList().isEmpty()) {
			Msg.addMsgError("Está nota fiscal já possui registro no sistema");
			return false;
		} else {
			resultValidarUK = "";
			return true;
		}
	}
	
	// variável para exibir o total R$ dos produtos
		public Double getTotal() {
			if (item.getQuantidade() != null && item.getValorCusto() != null)
				return item.getQuantidade() * item.getValorCusto();
			else
				return null;	
		}
		
		// variável para exibir a soma do total dos produtos
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

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
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

	public Boolean getCadastro() {
		return cadastro;
	}

	public void setCadastro(Boolean cadastro) {
		this.cadastro = cadastro;
	}

	public void setTotalValor(Double totalValor) {
		this.totalValor = totalValor;
	}

	public void setNotasFiscais(List<NotaFiscal> notasFiscais) {
		this.notasFiscais = notasFiscais;
	}

	public List<NotaFiscal> getNotasFiscais1() {
		return notasFiscais1;
	}

	public void setNotasFiscais1(List<NotaFiscal> notasFiscais1) {
		this.notasFiscais1 = notasFiscais1;
	}

	public Integer getBox4Search() {
		return box4Search;
	}

	public void setBox4Search(Integer box4Search) {
		this.box4Search = box4Search;
	}

	public Validator<NotaFiscal> getValidator() {
		return validator;
	}

	public void setValidator(Validator<NotaFiscal> validator) {
		this.validator = validator;
	}
	
}
