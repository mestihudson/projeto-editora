package ufrr.editora.mb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import ufrr.editora.dao.DAO;
import ufrr.editora.entity.Endereco;
import ufrr.editora.entity.Fornecedor;
import ufrr.editora.util.Msg;
import ufrr.editora.validator.Validator;

@ManagedBean
@ViewScoped
public class FornecedorBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	private Fornecedor fornecedor = new Fornecedor();
	private Endereco endereco = new Endereco();
	private List<Fornecedor> fornecedores;
	private List<Fornecedor> fornecedoresD;
	private DAO<Fornecedor> dao = new DAO<Fornecedor>(Fornecedor.class);
	public Boolean cadastro = true;
	private Validator<Fornecedor> validator;
	private String search;
	private String box4Search;

	@PostConstruct
	public void init() {

		fornecedor = new Fornecedor();
		endereco = new Endereco();
		validator = new Validator<Fornecedor>(Fornecedor.class);
		search = "";
		box4Search = "cnpj";
		box4Search = "nome";
	}

	/** Lista de Fornecedores **/

	public List<Fornecedor> getFornecedores() {
		if (fornecedores == null) {
			System.out.println("Carregando Fornecedores...");
			fornecedores = new DAO<Fornecedor>(Fornecedor.class).getAllOrder("nome");
		}
		return fornecedores;
	}

	// Lista de fornecedores com CPF
	public List<Fornecedor> getCpfs() {
		fornecedoresD = new ArrayList<Fornecedor>();
		List<Fornecedor> fs = new ArrayList<Fornecedor>();
		fs = this.getFornecedores();
		for (int i = 0; i < fs.size(); i++) {
			if (fs.get(i).getCnpj().length() == 14) {
				fornecedoresD.add(fs.get(i));
			}
		}
		return fornecedoresD;
	}

	// Lista de fornecedores com CNPJ
	public List<Fornecedor> getCnpjs() {
		fornecedoresD = new ArrayList<Fornecedor>();
		List<Fornecedor> fs = new ArrayList<Fornecedor>();
		fs = this.getFornecedores();
		for (int i = 0; i < fs.size(); i++) {
			if (fs.get(i).getCnpj().length() == 18) {
				fornecedoresD.add(fs.get(i));
			}
		}
		return fornecedoresD;
	}

	// Lista de todos os fornecedores
	public List<Fornecedor> getListaFornecedores() {
		fornecedoresD = new ArrayList<Fornecedor>();
		List<Fornecedor> fs = new ArrayList<Fornecedor>();
		fs = this.getFornecedores();
		for (int i = 0; i < fs.size(); i++) {
			if (fs.get(i).getId() != null) {
				fornecedoresD.add(fs.get(i));
			}
		}
		return fornecedoresD;
	}

	// Pesquisa Fornecedor pelo nome e cnpj/cpf
	public String getListaFornecedorByName() {

		if (box4Search.equals("cnpj")) {
			if (search.contains("'") || search.contains("@")
					|| search.contains("*")) {
				init();
				Msg.addMsgError("Cont�m caract�r(es) inv�lido(s)");
				return null;
			} else {
				if (search.length() <= 4) {
					init();
					Msg.addMsgError("Informe pelo menos 5 caracters");
					return null;
				} else {
					fornecedores = dao.getAllByName("obj.cnpj", search);
					if (fornecedores.isEmpty()) {
						init();
						Msg.addMsgError("Nenhum registro encontrado");
					} else {
						return null;
					}
				}
			}
		} else if (box4Search.equals("nome")) {
			if (search.contains("'") || search.contains("@")
					|| search.contains("/") || search.contains("*")) {
				init();
				Msg.addMsgError("Cont�m caract�r(es) inv�lido(s)");
				return null;
			} else {
				if (search.length() <= 4) {
					init();
					Msg.addMsgError("Informe pelo menos 5 caracteres");
					return null;
				} else {
					fornecedores = dao.getAllByName("obj.nome", search);
					if (fornecedores.isEmpty()) {
						init();
						Msg.addMsgError("Nenhum registro encontrado");
					} else {
						return null;
					}
				}
			}
		}
		return null;
	}

	/** Autocompletes **/

	// AutoComplete de nome e cnpj

	public List<String> autocomplete(String nome) {
		ArrayList<String> nomes = new ArrayList<String>();
		if (box4Search.equals("nome")) {
			if (!search.contains("'")) {
				List<Fornecedor> array = dao.getAllByName("nome", nome);
				for (int i = 0; i < array.size(); i++) {
					nomes.add(array.get(i).getNome());
				}
			}
		} else if (box4Search.equals("cnpj")) {
			if (!search.contains("'")) {
				List<Fornecedor> array = dao.getAllByName("cnpj_cpf", nome);
				for (int i = 0; i < array.size(); i++) {
					nomes.add(array.get(i).getCnpj());
				}
			}
		}
		return nomes;
	}

	/** actions **/

	// Cadastra fornecedor
	public String addFornecedor() {
		try {
			boolean all = true;
			if (!validarNomeUK_nome()) {
				all = false;
				Msg.addMsgError("O nome do fornecedor j� tem registro no sistema");
			}
			if (!validarNomeUK_cnpj()) {
				all = false;
				Msg.addMsgError("CPF ou CNPJ j� tem registro no sistema");
			}
			if (!validarNome_banco()) {
				all = false;
				Msg.addMsgError("Nome do banco n�o pode ser vazio");
			}
			if (!validarNome_agencia()) {
				all = false;
				Msg.addMsgError("Agencia n�o pode ser vazio");
			}
			if (!validarNome_conta()) {
				all = false;
				Msg.addMsgError("N�mero da conta n�o pode ser vazio");
			}
			if (!validarNome_titular()) {
				all = false;
				Msg.addMsgError("Titular da conta n�o pode ser vazio");
			}
			if (!all) {
				System.out.println("...Erro ao cadastrar fornecedor: dados faltam ser preenchidos ou fornecedor j� existe");
				return "/pages/forncedor/cadastrarFornecedor.jsf";
			} else {					
				if (fornecedor.getBanco().equalsIgnoreCase("caixa") && fornecedor.getOperacao()==0
						|| fornecedor.getBanco().equalsIgnoreCase("caixa") && fornecedor.getOperacao()==null) {
					Msg.addMsgError("Campo Op n�o pode ser vazio");
					return null;	
				}	
					if (fornecedor.getBanco().equalsIgnoreCase("bb") && fornecedor.getOperacao()>=1
							|| fornecedor.getBanco().equalsIgnoreCase("bradesco") && fornecedor.getOperacao()>=1
							|| fornecedor.getBanco().equalsIgnoreCase("itau") && fornecedor.getOperacao()>=1
							|| fornecedor.getBanco().equalsIgnoreCase("santander") && fornecedor.getOperacao()>=1) {
						Msg.addMsgError("Campo Op serve somente para a op��o de banco Caixa");
						return null;	
				}else
				fornecedor.setEndereco(endereco);
				dao.adiciona(fornecedor);
				this.endereco = new Endereco();
				this.fornecedor = new Fornecedor();
				init();
				Msg.addMsgInfo("Cadastro efetuado com sucesso");
				System.out.println("...cadastro efetuado com sucesso!");
			}
		} catch (Exception e) {
			init();
			e.printStackTrace();
			System.out
					.println("...Alguma coisa deu errada ao cadastrar fornecedor");
		}
		return null;
	}

	// m�todo para alterar o fornecedor
	public String alterFornecedor() {
		try {
			boolean all = true;
			if (!validarNome_banco()) {
				all = false;
				Msg.addMsgError("Nome do banco n�o pode ser vazio");
			}
			if (!validarNome_agencia()) {
				all = false;
				Msg.addMsgError("Agencia n�o pode ser vazio");
			}
			if (!validarNome_conta()) {
				all = false;
				Msg.addMsgError("N�mero da conta n�o pode ser vazio");
			}
			if (!validarNome_titular()) {
				all = false;
				Msg.addMsgError("Titular da conta n�o pode ser vazio");
			}
			if (!all) {
				System.out.println("...Erro ao cadastrar fornecedor: dados faltam ser preenchidos ou fornecedor j� existe");
				return "/pages/forncedor/cadastrarFornecedor.jsf";
			} else {					
				if (fornecedor.getBanco().equalsIgnoreCase("caixa") && fornecedor.getOperacao()==0
						|| fornecedor.getBanco().equalsIgnoreCase("caixa") && fornecedor.getOperacao()==null) {
					Msg.addMsgError("Campo Op n�o pode ser vazio");
					return null;	
				}	
					if (fornecedor.getBanco().equalsIgnoreCase("bb") && fornecedor.getOperacao()>=1
							|| fornecedor.getBanco().equalsIgnoreCase("bradesco") && fornecedor.getOperacao()>=1
							|| fornecedor.getBanco().equalsIgnoreCase("itau") && fornecedor.getOperacao()>=1
							|| fornecedor.getBanco().equalsIgnoreCase("santander") && fornecedor.getOperacao()>=1) {
						Msg.addMsgError("Campo Op serve somente para a op��o de banco Caixa");
						return null;	
				}else
				dao.atualiza(fornecedor);
				this.fornecedor = new Fornecedor();
				init();
				Msg.addMsgInfo("Atualiza��o efetuada com sucesso");
				System.out.println("...altera��o de fornecedor efetuada com sucesso!");
			}
		} catch (Exception e) {
			init();
			e.printStackTrace();
			System.out
					.println("...Alguma coisa deu errada ao cadastrar fornecedor");
		}
		return null;
	}

	/** ajax */

	public void checkBox4Search(AjaxBehaviorEvent event) {

	}

	public void checkNomeComercial(AjaxBehaviorEvent event) {
		validarNome();
		if (fornecedor.getNome().isEmpty()) {
			validator.setResultNome("");
		}
	}

	/** valida��o UK Nome */

	public boolean validarNomeUK_nome() {
		return validator.validarNomeUK("nome", fornecedor.getNome());
	}

	public void checkNomeUK_nome(AjaxBehaviorEvent event) {
		if (validarNomeUK_nome()) {
			if (fornecedor.getNome().isEmpty()) {
				validator.setResultNome("");
			}
		}
	}

	/** valida��o UK CPF / CNPJ */

	public boolean validarNome() {
		return validator.validarNome(fornecedor.getNome());
	}

	public boolean validarNomeUK_cnpj() {
		return validator.validarNomeUK("cnpj_cpf", fornecedor.getCnpj());
	}

	public void checkNomeUK_cpf(AjaxBehaviorEvent event) {
		if (validarNomeUK_cnpj()) {
			if (fornecedor.getCnpj().isEmpty()) {
				validator.setResultNome("");
			}
		}
	}

	/** Get and Set **/

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public DAO<Fornecedor> getDao() {
		return dao;
	}

	public void setDao(DAO<Fornecedor> dao) {
		this.dao = dao;
	}

	public Boolean getCadastro() {
		return cadastro;
	}

	public void setCadastro(Boolean cadastro) {
		this.cadastro = cadastro;
	}

	public void setFornecedores(List<Fornecedor> fornecedores) {
		this.fornecedores = fornecedores;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public List<Fornecedor> getFornecedoresD() {
		return fornecedoresD;
	}

	public void setFornecedoresD(List<Fornecedor> fornecedoresD) {
		this.fornecedoresD = fornecedoresD;
	}

	public Validator<Fornecedor> getValidator() {
		return validator;
	}

	public void setValidator(Validator<Fornecedor> validator) {
		this.validator = validator;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getBox4Search() {
		return box4Search;
	}

	public void setBox4Search(String box4Search) {
		this.box4Search = box4Search;
	}

	public void checkNome_banco(AjaxBehaviorEvent event) {
		validarNome_banco();
	}

	public boolean validarNome_banco() {
		return validator.validarNome(fornecedor.getBanco());
	}

	public void checkNome_agencia(AjaxBehaviorEvent event) {
		validarNome_agencia();
	}

	public boolean validarNome_agencia() {
		return validator.validarNome(fornecedor.getAgencia());
	}

	public void checkNome_conta(AjaxBehaviorEvent event) {
		validarNome_conta();
	}

	public boolean validarNome_conta() {
		return validator.validarNome(fornecedor.getConta());
	}

	public void checkNome_titular(AjaxBehaviorEvent event) {
		validarNome_titular();
	}

	public boolean validarNome_titular() {
		return validator.validarNome(fornecedor.getTitularConta());
	}

}