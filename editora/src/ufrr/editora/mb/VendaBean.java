package ufrr.editora.mb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ufrr.editora.dao.DAO;
import ufrr.editora.entity.Item;
import ufrr.editora.entity.ItemVenda;
import ufrr.editora.entity.Usuario;
import ufrr.editora.entity.Venda;
import ufrr.editora.report.Report;
import ufrr.editora.util.Msg;
import ufrr.editora.validator.Validator;

@ManagedBean
@ViewScoped
public class VendaBean implements Serializable {

	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	private static final long serialVersionUID = 1L;
	private Venda venda = new Venda();
	private ItemVenda itemVenda = new ItemVenda();
	private Item item = new Item();
	private Long idItem;
	private List<ItemVenda> itensVendas;
	private List<Item> itens;
	private List<Venda> vendas;
	private List<Venda> vendas1;
	private List<Venda> caixaEntrada;
	private List<Venda> caixaSaida;
	private DAO<Venda> dao = new DAO<Venda>(Venda.class);
	private DAO<Item> dao2 = new DAO<Item>(Item.class);
	private String search, resultValidarUK;
	private Double totalValor;
	private Double totalValorDesconto;
	private Double desconto;
	public Boolean cadastro = true;
	private Integer box4Search;
	private Validator<Venda> validator;
	private Boolean quantidadeN;

	@Temporal(TemporalType.DATE)
	private Calendar dataInicial = Calendar.getInstance();

	@Temporal(TemporalType.DATE)
	private Calendar dataFinal = Calendar.getInstance();

	@PostConstruct
	public void init() {

		venda = new Venda();
		validator = new Validator<Venda>(Venda.class);
		search = "";
		box4Search = 1;
		box4Search = 2;
		box4Search = 3;
		box4Search = 4;
	}

	// pesquisa venda pela data
	@SuppressWarnings("unchecked")
	public String getVendaByData() {
		try {
			Query query = dao
					.query("SELECT v FROM Venda v WHERE v.dataVenda=?");
			query.setParameter(1, venda.getDataVenda());
			vendas = query.getResultList();
			if (getCaixaEntrada().isEmpty()) {
				init();
				Msg.addMsgError("Nenhuma venda efetuada na data informada");
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Pesquisa venda pelo cliente, data e vendedor
	public String getListaVendaByCDV() {

		if (box4Search.equals(3)) {
			if (search.contains("'") || search.contains("@")
					|| search.contains("*")) {
				init();
				Msg.addMsgError("Contém caractér(es) inválido(s)");
				return null;
			} else {
				if (search.length() <= 4) {
					init();
					Msg.addMsgError("Informe 5 caracteres para pesquisa");
					return null;
				} else {
					vendas = dao.getAllByName("obj.vendedor.nome", search);
					if (vendas.isEmpty()) {
						init();
						Msg.addMsgError("Nenhuma efetuada para este vendedor");
						return null;
					} else {
						return null;
					}
				}
			}
		} else if (box4Search.equals(2)) {
			if (search.length() <= 4) {
				init();
				Msg.addMsgError("Informe 5 caracteres para pesquisa");
				return null;
			} else {
				vendas = dao.getAllByName("obj.cliente.nome", search);
				if (vendas.isEmpty()) {
					init();
					Msg.addMsgError("Nenhuma venda efetuada para este cliente");
					return null;
				} else {
					return null;
				}
			}

		}
		return null;
	}

	/** List NF **/

	public List<Venda> getVendas() {
		if (vendas == null) {
			System.out.println("Carregando caixa...");
			vendas = new DAO<Venda>(Venda.class).getAllOrder("id");
		}
		return vendas;
	}

	//
	// entrada de dinheiro no caixa
	public List<Venda> getCaixaEntrada() {
		caixaEntrada = new ArrayList<Venda>();
		List<Venda> vs = new ArrayList<Venda>();
		vs = this.getVendas();
		for (int i = 0; i < vs.size(); i++) {
			if (vs.get(i).getOperacao() == 1) {
				caixaEntrada.add(vs.get(i));
			}
		}
		return caixaEntrada;
	}

	// saída de dinheiro no caixa
	public List<Venda> getCaixaSaida() {
		caixaSaida = new ArrayList<Venda>();
		List<Venda> vs = new ArrayList<Venda>();
		vs = this.getVendas();
		for (int i = 0; i < vs.size(); i++) {
			if (vs.get(i).getOperacao() == 2) {
				caixaSaida.add(vs.get(i));
			}
		}
		return caixaSaida;
	}

	/** Actions **/

	// saída de dinheiro
	public String addSaida() {
		boolean all = true;

		if (venda.getCliente() != null) {
			Msg.addMsgFatal("Erro: operação cancelada. Não identifique o cliente ao retirar dinheiro.");
			all = false;
			return "/pages/venda/efetuarVendaAdmin.xhtml";
		}
		if (!all) {
			System.out
					.println("...Erro ao efetuar a saida: retire o nome do cliente");
		} else {

			if (loginBean.getUsuario().getPerfil().getId() == 1) {

				this.getVenda().setVendedor(this.loginBean.getUsuario());
				DAO<Usuario> UDao = new DAO<Usuario>(Usuario.class);
				Usuario u = UDao
						.buscaPorId(this.loginBean.getUsuario().getId());
				u.getVendas().add(venda);
				DAO<Venda> dao = new DAO<Venda>(Venda.class);
				venda.setOperacao(2);
				venda.setTituloObs(1);
				venda.setValorTotalDesconto(getVenda().getValorTotal());

				Msg.addMsgInfo("Saída efetuada com sucesso");
				System.out.println("...saida efetuada com sucesso!!");
				dao.adiciona(venda);
				venda = new Venda();

				return "/pages/venda/efetuarVendaAdmin.xhtml";

			} else {
				Msg.addMsgFatal("Retirada não permitida");
			}

		}
		return null;
	}

	// método para adicionar venda (entrada de dinheiro)
	public String addVenda() {
		boolean all = true;

		if (venda.getCliente() == null) {
			Msg.addMsgError("Informe o cliente");
			all = false;
		}
		if (venda.getItensVendas().isEmpty()) {
			Msg.addMsgError("Não é possível efetuar a venda sem produto");
			all = false;
		}
		if (!all) {
			System.out.println("...Erro ao efetuar a venda: dados incompletos");
		} else {
			for (ItemVenda i : this.getVenda().getItensVendas()) { // verifica se a quantidade de produto informado ultrapasse o disponível em estoque
				if (i.getItem().getQuantidadeAtual() - i.getQuantidade() <= -1) {
					this.cadastro = false;
					break;
				}
			}
			if (this.cadastro == true) {
				if (venda.getImprimeCupom().equals(true)) {

					this.getVenda().setVendedor(this.loginBean.getUsuario());
					DAO<Usuario> UDao = new DAO<Usuario>(Usuario.class);
					Usuario u = UDao.buscaPorId(this.loginBean.getUsuario().getId());
					u.getVendas().add(venda);
					DAO<Venda> dao = new DAO<Venda>(Venda.class);
					venda.setOperacao(1);
					venda.setValorTotalDesconto(getValorTotalComDesconto());
					venda.setValorTotal(getValorTotalProdutos());

					// para gerar o cupom
					// HashMap<String, Object> params = new HashMap<String,
					// Object>();
					// Report report = new Report("report2", params);
					// report.pdfReport();
					// report = new Report();

					// atualiza a lista de item (quantidade de saida)					
					for (ItemVenda iv : this.getVenda().getItensVendas()) {
						DAO<Item> iDAO = new DAO<Item>(Item.class);
						Item i = iv.getItem();
												
						i.setQuantidadeSaida(i.getQuantidadeSaida()+iv.getQuantidade());
						iDAO.atualiza(i);
						System.out.println("...atualizou quantidade de saída do item");
					}	
					
					Msg.addMsgInfo("Venda efetuada com sucesso");
					System.out.println("...venda efetuada com sucesso!!");
					dao.adiciona(venda);
					itemVenda = new ItemVenda();
					venda = new Venda();

					return "/pages/venda/efetuarVenda.xhtml";

				} else {
					this.getVenda().setVendedor(this.loginBean.getUsuario());
					DAO<Usuario> UDao = new DAO<Usuario>(Usuario.class);
					Usuario u = UDao.buscaPorId(this.loginBean.getUsuario().getId());
					u.getVendas().add(venda);
					DAO<Venda> dao = new DAO<Venda>(Venda.class);
					venda.setOperacao(1);
					Msg.addMsgInfo("Venda efetuada com sucesso");
					venda.setValorTotalDesconto(getValorTotalComDesconto());
					venda.setValorTotal(getValorTotalProdutos());
					
					
					// atualiza a lista de item (quantidade de saida)					
					for (ItemVenda iv : this.getVenda().getItensVendas()) {
						DAO<Item> iDAO = new DAO<Item>(Item.class);
						Item i = iv.getItem();
						
						i.setQuantidadeSaida(i.getQuantidadeSaida()+iv.getQuantidade());
						iDAO.atualiza(i);
						System.out.println("...atualizou quantidade de saída do item");
					}	
					
					dao.adiciona(venda);
					itemVenda = new ItemVenda();
					venda = new Venda();
					return "/pages/venda/efetuarVenda.xhtml";
				}
			} else {
				Msg.addMsgFatal("Há na lista um produto com quantidade indisponível no estoque, " +
						"com isso não será permitida nenhuma venda. Verifique a quantidade disponível no estoque!");
				System.out.println("...quantidade não disponível no estoque");
				itemVenda = new ItemVenda();
				this.cadastro = true;
			}
			
		}
		return null;
	}

	// método para adicionar venda somente Admin
	public String addVendaAdmin() {
		boolean all = true;

		if (venda.getCliente() == null) {
			Msg.addMsgError("Informe o cliente");
			all = false;
		}
		if (venda.getItensVendas().isEmpty()) {
			Msg.addMsgError("Não é possível efetuar a venda sem produto");
			all = false;
		}
		if (!all) {
			System.out.println("...Erro ao efetuar a venda: dados incompletos");
		} else {
			for (ItemVenda i : this.getVenda().getItensVendas()) { // verifica se a quantidade de produto informado ultrapasse o disponível em estoque
				if (i.getItem().getQuantidadeAtual() - i.getQuantidade() <= -1) {
					this.cadastro = false;
					break;
				}
			}
			if (this.cadastro == true) {
				if (venda.getImprimeCupom().equals(true)) {

					this.getVenda().setVendedor(this.loginBean.getUsuario());
					DAO<Usuario> UDao = new DAO<Usuario>(Usuario.class);
					Usuario u = UDao.buscaPorId(this.loginBean.getUsuario().getId());
					u.getVendas().add(venda);
					DAO<Venda> dao = new DAO<Venda>(Venda.class);
					venda.setOperacao(1);
					venda.setValorTotalDesconto(getValorTotalComDesconto());
					venda.setValorTotal(getValorTotalProdutos());

					// para gerar o cupom
					// HashMap<String, Object> params = new HashMap<String,
					// Object>();
					// Report report = new Report("report2", params);
					// report.pdfReport();
					// report = new Report();

					// atualiza a lista de item (quantidade de saida)					
					for (ItemVenda iv : this.getVenda().getItensVendas()) {
						DAO<Item> iDAO = new DAO<Item>(Item.class);
						Item i = iv.getItem();
												
						i.setQuantidadeSaida(i.getQuantidadeSaida()+iv.getQuantidade());
						iDAO.atualiza(i);
						System.out.println("...atualizou quantidade de saída do item");
					}	
					
					Msg.addMsgInfo("Venda efetuada com sucesso");
					System.out.println("...venda efetuada com sucesso!!");
					dao.adiciona(venda);
					itemVenda = new ItemVenda();
					venda = new Venda();

					return "/pages/venda/efetuarVendaAdmin.xhtml";

				} else {
					this.getVenda().setVendedor(this.loginBean.getUsuario());
					DAO<Usuario> UDao = new DAO<Usuario>(Usuario.class);
					Usuario u = UDao.buscaPorId(this.loginBean.getUsuario().getId());
					u.getVendas().add(venda);
					DAO<Venda> dao = new DAO<Venda>(Venda.class);
					Msg.addMsgInfo("Venda efetuada com sucesso");
					venda.setValorTotalDesconto(getValorTotalComDesconto());
					venda.setValorTotal(getValorTotalProdutos());
					
					
					// atualiza a lista de item (quantidade de saida)					
					for (ItemVenda iv : this.getVenda().getItensVendas()) {
						DAO<Item> iDAO = new DAO<Item>(Item.class);
						Item i = iv.getItem();
						
						i.setQuantidadeSaida(i.getQuantidadeSaida()+iv.getQuantidade());
						iDAO.atualiza(i);
						System.out.println("...atualizou quantidade de saída do item");
					}	
					
					dao.adiciona(venda);
					itemVenda = new ItemVenda();
					venda = new Venda();
					return "/pages/venda/efetuarVendaAdmin.xhtml";
				}
			} else {
				Msg.addMsgFatal("Há na lista um produto com quantidade indisponível no estoque, " +
						"com isso não será permitida nenhuma venda. Verifique a quantidade disponível no estoque!");
				System.out.println("...quantidade não disponível no estoque");
				itemVenda = new ItemVenda();
				this.cadastro = true;
			}
			
		}
		return null;
	}

	// método para adicionar itens a nota fiscal
	public void guardaItem() {
		boolean all = true;
		if (!all) {
			System.out
					.println("...Erro ao efetuar venda: inconsistencia nos dados do item");
		} else {
			for (ItemVenda i : this.getVenda().getItensVendas()) {
				if (getIdItem().equals(i.getItem().getId())) {
					this.cadastro = false;
					break;
				}
			}
			if (this.cadastro == true) {
				
//				ItemVenda i : this.getVenda().getItensVendas()
				for (ItemVenda i : this.getVenda().getItensVendas()) {
					if (i.getItem().getQuantidadeAtual() - i.getQuantidade() <= -1) {
						this.cadastro = false;
						break;
					}
				}
				if (this.cadastro == true) {
					
					// criar um método para não passar os produtos cuja quantidade de venda seja maior que o nível de estoque
					
					if (itemVenda.getQuantidade() == null || itemVenda.getQuantidade() == 0) {

						DAO<Item> dao = new DAO<Item>(Item.class);
						Item item = dao.buscaPorId(idItem);
						itemVenda.setItem(item);
						itemVenda.setQuantidade(1);

						venda.getItensVendas().add(itemVenda);
						itemVenda.setVenda(venda);

						itemVenda = new ItemVenda();
						System.out.println("...Produto adicionado a venda com qtd não informada !! (ok)");

					} else {
							
							DAO<Item> dao = new DAO<Item>(Item.class);
							Item item = dao.buscaPorId(idItem);
							itemVenda.setItem(item);

							venda.getItensVendas().add(itemVenda);
							itemVenda.setVenda(venda);

							itemVenda = new ItemVenda();
							System.out.println("...Produto adicionado a venda com qtd superior a 1 !! (ok)");
								
							}

				} else {
					Msg.addMsgFatal("Há na lista um produto com quantidade indisponível no estoque, " +
							"com isso não será permitida nenhuma venda. Verifique a quantidade disponível no estoque!");
					System.out.println("...quantidade não disponível no estoque");
					itemVenda = new ItemVenda();
					this.cadastro = true;
				}

			} else {
				Msg.addMsgError("Produto não pode ser adicionado novamente");
				System.out.println("...produto nao pode ser adicionado novamente");
				itemVenda = new ItemVenda();
				this.cadastro = true;
			}
		}
	}
	
	public Boolean quantidadeN() {
		if (venda.getItensVendas().get(0).getItem().getQuantidadeSaida() - venda.getItensVendas().get(1).getItem().getQuantidadeSaida() - itemVenda.getQuantidade() < 0) {
			System.out.println("...quantidade negativa");
			setQuantidadeN(true);
			return true;
		}else {
			System.out.println("...quantidade normal");
			setQuantidadeN(false);
			return false;
		}
	}
	
	// método para remover o item da lista de itens
		public void removeItemVenda() {

//			getItemVenda().getItem().setQuantidadeSaida(getItemVenda().getQuantidade() - getItem().getQuantidadeSaida());
//			dao2.atualiza(item);
			// item.setQuantidadeSaida(itemVenda.getQuantidade() + item.getQuantidadeSaida());
			// dao2.atualiza(item);

			venda.getItensVendas().remove(itemVenda);
			Msg.addMsgWarn("Produto removido");
			System.out.println("...Item removido da venda");

			itemVenda = new ItemVenda();
		}

	// relatório
	public void imprimeCupom() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		Report report = new Report("clientes", params);
		report.pdfReport();
	}


	public List<ItemVenda> getItensVendas() {
		return itensVendas;
	}

	public void setItensVendas(List<ItemVenda> itensVendas) {
		this.itensVendas = itensVendas;
	}

	// cancelar cadastro de nota fiscal
	public String cancelarVenda() {
		if (venda.getCliente().getNome() != null
				|| !venda.getItensVendas().isEmpty()) {
			Msg.addMsgFatal("Venda cancelada");
			return "efetuarVenda.xhtml";
		} else {
			return "efetuarVenda.xhtml?faces-redirect=true";
		}

	}

	// cancelar cadastro de nota fiscal
	public String cancelarVenda2() {
		if (!venda.getItensVendas().isEmpty()) {
			Msg.addMsgFatal("Venda cancelada");
			return "efetuarVenda.xhtml";
		} else {
			return "efetuarVenda.xhtml?faces-redirect=true";
		}

	}

	// cancelar cadastro de nota fiscal
	public String cancelarVenda3() {
		if (venda.getCliente().getNome() != null
				|| !venda.getItensVendas().isEmpty()) {
			Msg.addMsgFatal("Operacao cancelada");
			return "efetuarVendaAdmin.xhtml";
		} else {
			return "efetuarVendaAdmin.xhtml?faces-redirect=true";
		}

	}

	// cancelar venda/saida
	public String cancelarVenda4() {
		Msg.addMsgFatal("Operacao cancelada");
		return "efetuarVendaAdmin.xhtml";
	}

	// variável para exibir o total R$ dos produtos
	public Double getTotal() {
		if (itemVenda.getQuantidade() != null
				&& itemVenda.getItem().getValorVenda() != null)
			return itemVenda.getQuantidade()
					* itemVenda.getItem().getValorVenda();
		else
			return null;
	}

	// variável para exibir a soma do total dos produtos
	public Double getValorTotalProdutos() {
		setTotalValor(00.00);
		for (ItemVenda i : getVenda().getItensVendas()) {
			setTotalValor(getTotalValor() + i.getTotal());
		}
		return totalValor;
	}

	public Double getValorTotalComDesconto() {
		setTotalValor(00.00);
		if (getValorTotalProdutos() != null && getDesconto1() != null)
			return getValorTotalProdutos() - getDesconto1();
		else
			return null;
	}

	public Double getDesconto1() {
		if (totalValor != null && desconto != null)
			return totalValor * desconto / 100;
		else
			return null;
	}

	// método para adicionar itens a nota fiscal
	public void gerarDesconto() {

		itemVenda = new ItemVenda();

	}

	// Fluxo de caixa
	public Double getValorTotalEntrada() {
		setTotalValor(00.00);
		for (Venda v : getCaixaEntrada()) {
			setTotalValor(getTotalValor() + v.getValorTotalDesconto());
		}
		return totalValor;
	}

	public Double getValorTotalSaida() {
		setTotalValor(00.00);
		for (Venda v : getCaixaSaida()) {
			setTotalValor(getTotalValor() + v.getValorTotalDesconto());
		}
		return totalValor;
	}

	public Double getLucro() {
		setTotalValor(00.00);
		if (getValorTotalSaida() != null && getValorTotalEntrada() != null)
			return getValorTotalEntrada() - getValorTotalSaida();
		else
			return null;
	}

	public Boolean getResult() {
		if (getLucro() >= 0.00)
			return true;
		else
			return false;
	}

	@SuppressWarnings("unchecked")
	public String getCaixaByDate() {
		if (getDataFinal().before(getDataInicial())) {
			Msg.addMsgError("Período inválido");
		} else {
			try {
				Query query = dao
						.query("SELECT v FROM Venda v WHERE v.dataVenda between ? and ? order by v.dataVenda");
				query.setParameter(1, getDataInicial());
				query.setParameter(2, getDataFinal());
				vendas = query.getResultList();

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("... erro na consulta do caixa");
			}
			return null;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public String getCaixaByDate2() {

		try {
			Query query = dao
					.query("SELECT v FROM Venda v WHERE v.dataVenda = ?");
			query.setParameter(1, venda.getDataVenda());
			vendas = query.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("... erro na consulta do caixa");
		}
		return null;
	}

	/** get and set **/

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public Venda getVenda() {
		return venda;
	}

	public void setVenda(Venda venda) {
		this.venda = venda;
	}

	public ItemVenda getItemVenda() {
		return itemVenda;
	}

	public void setItemVenda(ItemVenda itemVenda) {
		this.itemVenda = itemVenda;
	}

	public Long getIdItem() {
		return idItem;
	}

	public void setIdItem(Long idItem) {
		this.idItem = idItem;
	}

	public void setVendas(List<Venda> vendas) {
		this.vendas = vendas;
	}

	public List<Venda> getVendas1() {
		return vendas1;
	}

	public void setVendas1(List<Venda> vendas1) {
		this.vendas1 = vendas1;
	}

	public DAO<Venda> getDao() {
		return dao;
	}

	public List<Item> getItens() {
		return itens;
	}

	public void setItens(List<Item> itens) {
		this.itens = itens;
	}

	public void setDao(DAO<Venda> dao) {
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

	public Boolean getCadastro() {
		return cadastro;
	}

	public void setCadastro(Boolean cadastro) {
		this.cadastro = cadastro;
	}

	public Integer getBox4Search() {
		return box4Search;
	}

	public void setBox4Search(Integer box4Search) {
		this.box4Search = box4Search;
	}

	public Validator<Venda> getValidator() {
		return validator;
	}

	public void setValidator(Validator<Venda> validator) {
		this.validator = validator;
	}

	public Double getTotalValorDesconto() {
		return totalValorDesconto;
	}

	public void setTotalValorDesconto(Double totalValorDesconto) {
		this.totalValorDesconto = totalValorDesconto;
	}

	public void setDesconto(Double desconto) {
		this.desconto = desconto;
	}

	public Double getDesconto() {
		return desconto;
	}

	public Calendar getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Calendar dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Calendar getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Calendar dataFinal) {
		this.dataFinal = dataFinal;
	}

	public void setCaixaEntrada(List<Venda> caixaEntrada) {
		this.caixaEntrada = caixaEntrada;
	}

	public void setCaixaSaida(List<Venda> caixaSaida) {
		this.caixaSaida = caixaSaida;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public DAO<Item> getDao2() {
		return dao2;
	}

	public void setDao2(DAO<Item> dao2) {
		this.dao2 = dao2;
	}

	public Boolean getQuantidadeN() {
		return quantidadeN;
	}

	public void setQuantidadeN(Boolean quantidadeN) {
		this.quantidadeN = quantidadeN;
	}
	
	
}
