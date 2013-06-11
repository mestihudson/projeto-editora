package ufrr.editora.mb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ufrr.editora.dao.DAO;
import ufrr.editora.entity.Item;
import ufrr.editora.entity.ItemVenda;
import ufrr.editora.entity.Usuario;
import ufrr.editora.entity.Venda;
import ufrr.editora.report.Report;
import ufrr.editora.util.EmailUtils;
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
			Query query = dao.query("SELECT v FROM Venda v WHERE v.dataVenda=?");
			query.setParameter(1, venda.getDataVenda());
			vendas = query.getResultList();
			if (vendas.isEmpty()) {
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
			if (vs.get(i).getOperacao()==1) {
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
			if (vs.get(i).getOperacao()==2) {
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
			System.out.println("...Erro ao efetuar a saida: retire o nome do cliente");
		} else {
			
			if (loginBean.getUsuario().getPerfil().getId()==1) {
				
			this.getVenda().setVendedor(this.loginBean.getUsuario());
			DAO<Usuario> UDao = new DAO<Usuario>(Usuario.class);
			Usuario u = UDao.buscaPorId(this.loginBean.getUsuario().getId());
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
			
			}else {
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
			if (venda.getImprimeCupom().equals(true)) {
								
				this.getVenda().setVendedor(this.loginBean.getUsuario());
				DAO<Usuario> UDao = new DAO<Usuario>(Usuario.class);
				Usuario u = UDao.buscaPorId(this.loginBean.getUsuario().getId());
				u.getVendas().add(venda);
				DAO<Venda> dao = new DAO<Venda>(Venda.class);
				venda.setOperacao(1);
				venda.setValorTotalDesconto(getValorTotalComDesconto());
				venda.setValorTotal(getValorTotalProdutos());
								
				// para gerar relatorio
//				HashMap<String, Object> params = new HashMap<String, Object>();
//				Report report = new Report("report2", params);
//				report.pdfReport();
//				report = new Report(); 
				
				Msg.addMsgInfo("Venda efetuada com sucesso");
				System.out.println("...venda efetuada com sucesso!!");
				dao.adiciona(venda);
				itemVenda = new ItemVenda();
				venda = new Venda();
												
				return "/pages/venda/efetuarVenda.xhtml";
									
				
			}else {
				this.getVenda().setVendedor(this.loginBean.getUsuario());
				DAO<Usuario> UDao = new DAO<Usuario>(Usuario.class);
				Usuario u = UDao.buscaPorId(this.loginBean.getUsuario().getId());
				u.getVendas().add(venda);
				DAO<Venda> dao = new DAO<Venda>(Venda.class);
				Msg.addMsgInfo("Venda efetuada com sucesso");
				venda.setOperacao(1);
				venda.setValorTotalDesconto(getValorTotalComDesconto());
				venda.setValorTotal(getValorTotalProdutos());
				dao.adiciona(venda);
				itemVenda = new ItemVenda();
				venda = new Venda();
				return "/pages/venda/efetuarVenda.xhtml";
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
				if (venda.getImprimeCupom().equals(true)) {
									
					this.getVenda().setVendedor(this.loginBean.getUsuario());
					DAO<Usuario> UDao = new DAO<Usuario>(Usuario.class);
					Usuario u = UDao.buscaPorId(this.loginBean.getUsuario().getId());
					u.getVendas().add(venda);
					DAO<Venda> dao = new DAO<Venda>(Venda.class);
					venda.setOperacao(1);
					venda.setValorTotalDesconto(getValorTotalComDesconto());
					venda.setValorTotal(getValorTotalProdutos());
					
					// para gerar relatorio
//					HashMap<String, Object> params = new HashMap<String, Object>();
//					Report report = new Report("report2", params);
//					report.pdfReport();
//					report = new Report();
					
					Msg.addMsgInfo("Venda efetuada com sucesso");
					System.out.println("...venda efetuada com sucesso!!");
					dao.adiciona(venda);
					itemVenda = new ItemVenda();
					venda = new Venda();
													
					return "/pages/venda/efetuarVendaAdmin.xhtml";
					
				}else {
					this.getVenda().setVendedor(this.loginBean.getUsuario());
					DAO<Usuario> UDao = new DAO<Usuario>(Usuario.class);
					Usuario u = UDao.buscaPorId(this.loginBean.getUsuario().getId());
					u.getVendas().add(venda);
					DAO<Venda> dao = new DAO<Venda>(Venda.class);
					Msg.addMsgInfo("Venda efetuada com sucesso");					
					venda.setValorTotalDesconto(getValorTotalComDesconto());
					venda.setValorTotal(getValorTotalProdutos());
					dao.adiciona(venda);
					itemVenda = new ItemVenda();
					venda = new Venda();
					return "/pages/venda/efetuarVendaAdmin.xhtml";
				}
				
			}
			return null;
		}

	// método para adicionar itens a nota fiscal
	public void guardaItem() {		
		boolean all = true;
		if (!all) {
			System.out.println("...Erro ao efetuar venda: inconsistencia nos dados do item");
		} else {
			for (ItemVenda i : this.getVenda().getItensVendas()) {
				if (getIdItem().equals(i.getItem().getId())) {
					this.cadastro = false;
					break;
				}
			}
			if (this.cadastro == true) {
				
				if (itemVenda.getQuantidade().equals(item.getQuantidadeAtual())) {
					Msg.addMsgError("Quantidade indisponível no estoque"); // criar um método para não passar os produtos cuja quantidade de venda seja maiores que o nível de estoque
					
				}else {
					
//				if (!itemVenda.getQuantidade().equals(item.getQuantidadeAtual())) {
					
					if (itemVenda.getQuantidade() == null
							|| itemVenda.getQuantidade() == 0) {
						

							DAO<Item> dao = new DAO<Item>(Item.class);
							Item item = dao.buscaPorId(idItem);
							itemVenda.setItem(item);
							itemVenda.setQuantidade(1);
							
//							item.setQuantidadeSaida(itemVenda.getQuantidade() + item.getQuantidadeSaida());
//							dao2.atualiza(item); 
							
							// para fazer perfeito isso resolve, mas se o vendedor erra é preciso dar o rollback...
							// resolver isso!!

							venda.getItensVendas().add(itemVenda);
							itemVenda.setVenda(venda);

							itemVenda = new ItemVenda();
							System.out.println("...Produto adicionado a venda com qtd não informada !! (ok)");

					} else {
						DAO<Item> dao = new DAO<Item>(Item.class);
						Item item = dao.buscaPorId(idItem);
						itemVenda.setItem(item);

						// item.setQuantidadeSaida(itemVenda.getQuantidade() +
						// item.getQuantidadeSaida());
						// dao2.atualiza(item);

						// para fazer perfeito isso resolve, mas se o vendedor
						// erra é preciso dar o rollback...
						// resolver isso!!

						venda.getItensVendas().add(itemVenda);
						itemVenda.setVenda(venda);

						itemVenda = new ItemVenda();
						System.out
								.println("...Produto adicionado a venda com qtd superior a 1 !! (ok)");
					}
				}

			} else {
				Msg.addMsgError("Produto não pode ser adicionado novamente");
				System.out.println("...produto nao pode ser adicionado novamente");
				itemVenda = new ItemVenda();
				this.cadastro = true;
					}
				}
			}
		
	// relatório
	public void imprimeCupom() {
		  HashMap<String, Object> params = new HashMap<String, Object>();
		  Report report = new Report("clientes", params);
		  report.pdfReport();
	}
	
	// enviar por email comprovante de compra
		public void enviaComprovante() {
			try {
				EmailUtils.enviaComprovante(venda);
			} catch (EmailException ex) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								"Erro! Occoreu um erro ao tentar enviar o email",
								"Erro"));
				System.out.println("...Erro ao tentar enviar o email de comprovante");
				Logger.getLogger(EmailBean.class.getName()).log(Level.ERROR, null, ex);
			}
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

	// método para editar/alterar nota
//	public String alterNota() {
//		boolean all = true;
//		if (notaFiscal.getValor() == null || notaFiscal.getValor() == 0) {
//			Msg.addMsgError("Informe o valor total da nota fiscal");
//			all = false;
//		}
//		if (notaFiscal.getItens().isEmpty()) {
//			Msg.addMsgError("Não é possível cadastrar nota fiscal sem produto");
//			all = false;
//		}
//		if (!all) {
//			System.out
//					.println("...Erro ao cadastrar nota: nota fiscal já existe");
//		} else {
//			if (notaFiscal.getValor().equals(getValorTotalProdutos())) {
//				DAO<NotaFiscal> dao = new DAO<NotaFiscal>(NotaFiscal.class);
//				Msg.addMsgInfo("Nota Fiscal alterada com sucesso");
//				dao.atualiza(notaFiscal);
//				notaFiscal = new NotaFiscal();
//				return "/pages/notafiscal/cadastrarNotaFiscal.xhtml";
//			} else {
//				System.out
//						.println("...Erro: o valor da nota fiscal deve ser o mesmo ao total de itens");
//				Msg.addMsgError("O valor total da nota fiscal deve ser igual ao valor dos produtos somados");
//				return null;
//			}
//		}
//		return null;
//	}

	// método para remover o item da lista de itens
	public void removeItemVenda() {
		venda.getItensVendas().remove(itemVenda);
		Msg.addMsgWarn("Produto removido");
		
//		item.setQuantidadeSaida(itemVenda.getQuantidade() - item.getQuantidadeSaida());
		System.out.println("Produto removido...");
		
//		dao2.atualiza(item);
//		item = new Item();
		itemVenda = new ItemVenda();
	}

	// desativar/ativar nota fiscal
//	public String alterStatus() {
//		if (notaFiscal.getStatus().equals(true)) {
//			notaFiscal.setStatus(false);
//			dao.atualiza(notaFiscal);
//			Msg.addMsgInfo("Nota fiscal: " + getNotaFiscal().getNumero() + "\n"
//					+ "Fornecedor: "
//					+ getNotaFiscal().getFornecedor().getNome() + "\n"
//					+ "desativada com sucesso");
//			System.out.println("...Nota fiscal desativada");
//			return "/pages/notafiscal/consultarNotaFiscal.xhtml";
//		} else {
//			notaFiscal.setStatus(true);
//			dao.atualiza(notaFiscal);
//			Msg.addMsgInfo("Nota fiscal: " + getNotaFiscal().getNumero() + "\n"
//					+ "Fornecedor: "
//					+ getNotaFiscal().getFornecedor().getNome() + "\n"
//					+ "reativada com sucesso");
//			System.out.println("...Nota fiscal ativada");
//			return "/pages/notafiscal/consultarNotaFiscal.xhtml";
//		}
//	}

	/** validations **/

	// validação para não cadastrar nº de nota fiscal para o mesmo fornecedor
//	public boolean validarNota() {
//		Query q = dao
//				.query("SELECT n FROM NotaFiscal n WHERE numero = ? and fornecedor = ? and status = true");
//		q.setParameter(1, notaFiscal.getNumero());
//		q.setParameter(2, notaFiscal.getFornecedor());
//
//		if (!q.getResultList().isEmpty()) {
//			Msg.addMsgError("Está nota fiscal já possui registro no sistema");
//			return false;
//		} else {
//			resultValidarUK = "";
//			return true;
//		}
//	}

	// variável para exibir o total R$ dos produtos
	public Double getTotal() {
		if (itemVenda.getQuantidade() != null && itemVenda.getItem().getValorVenda() != null)
			return itemVenda.getQuantidade() * itemVenda.getItem().getValorVenda();
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
		if(getDataFinal().before(getDataInicial())) {
			Msg.addMsgError("Período inválido");
		} else {
			try {
				Query query = dao.query("SELECT v FROM Venda v WHERE v.dataVenda between ? and ? order by v.dataVenda");
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
			Query query = dao.query("SELECT v FROM Venda v WHERE v.dataVenda = ?");
			query.setParameter(1, venda.getDataVenda());
			vendas = query.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("... erro na consulta do caixa");
		}
		return null;
	}
	
//	select * from tb_venda v
//	where v.datavenda between '24-05-2013' and '27-05-2013' 
//	order by v.id

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

}
