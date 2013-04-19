package ufrr.editora.mb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.persistence.Query;

import ufrr.editora.dao.DAO;
import ufrr.editora.entity.Endereco;
import ufrr.editora.entity.Fornecedor;
import ufrr.editora.entity.Usuario;
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
	private Validator<Usuario> validator;
	
	@PostConstruct
	public void init() {
		
		fornecedor = new Fornecedor();
		endereco = new Endereco();
		validator = new Validator<Usuario>(Usuario.class);
	}
	
	/** List Products **/
	
	public List<Fornecedor> getFornecedores() {
		if (fornecedores == null) {
			System.out.println("Carregando Fornecedores...");
			fornecedores = new DAO<Fornecedor>(Fornecedor.class).listaTodos();
		}
		return fornecedores;
	}
	
	// Exibe uma lista com os fornecedores
		@SuppressWarnings("unchecked")
		public List<Fornecedor> getListaFornecedores() {
			Query query = dao.query("SELECT f FROM Fornecedor f WHERE f.id is not null");
			fornecedores = query.getResultList();
			System.out.println("Total de Fornecedor: " + getFornecedores().size());
			return query.getResultList();
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
	
	
	/** actions **/
	
//	Cadastra fornecedor
	public String addFornecedor() {
		try {
			boolean all = true;
			if (!validarNomeUK_nome()) {
				all = false;
			}
			if (!validarNomeUK_cnpj()) {
				all = false;
			}
			if (!validarNome_banco()){
				all = false;
			}
			if (!validarNome_agencia()){
				all = false;
			}
			if (!validarNome_conta()){
				all = false;
			}
			if (!validarNome_titular()){
				all = false;
			}
			if (!all) {
				System.out.println("...erro ao cadastrar, fornecedor já tem registro");
				Msg.addMsgError("Preencha corretamente os dados");
				return "/pages/forncedor/cadastrarFornecedor.jsf";
			} else {
				if (fornecedor.getId() == null)
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
			System.out.println("...Alguma coisa deu errada ao cadastrar fornecedor");
		}
		return null;
	}

	public void addFornecedor2() {
		for (Fornecedor fornecedores : this.getFornecedores()) {
			if(fornecedores.getCnpj().equalsIgnoreCase(this.getFornecedor().getCnpj()) ||
					fornecedores.getNome().equalsIgnoreCase(this.getFornecedor().getNome())) { 
				this.cadastro = false;
				break;
			}
		}
		if(this.cadastro == true){
			if(this.fornecedor.getAgencia().isEmpty() 
					|| this.fornecedor.getConta().isEmpty()
					|| this.fornecedor.getBanco().isEmpty()){
				Msg.addMsgError("Informe corretamente os dados bancários");
				
			} else {
				if (fornecedor.getId() == null) {
					Msg.addMsgInfo("Fornecedor cadastrado com sucesso");
					fornecedor.setEndereco(endereco);
					dao.adiciona(fornecedor);
					this.endereco = new Endereco();
					this.fornecedor = new Fornecedor();
				} else {
					Msg.addMsgInfo("Alteração realizada com sucesso");
					dao.atualiza(fornecedor);
				}
			}
		} else {
			Msg.addMsgError("Fornecedor já registrado");
		}
		fornecedores = dao.listaTodos();
		this.cadastro = true;
	}
	
	public void alterFornecedor() {
		if (fornecedor.getId() != null) {
			Msg.addMsgInfo("Fornecedor editado com sucesso");
			dao.atualiza(fornecedor);
			this.fornecedor = new Fornecedor();
		}
	}
	
	public List<String> autocomplete(String nome) {
		List<Fornecedor> array = dao.getAllByName("nome", nome);
		ArrayList<String> nomes = new ArrayList<String>();
		for (int i = 0; i < array.size(); i++) {
			nomes.add(array.get(i).getNome());
		}
		return nomes;
	}
	
	//Pesquisa Fornecedor
	@SuppressWarnings("unchecked")
	public String getListaFornecedorByName() {
		if (fornecedor.getNome().contains("'") || fornecedor.getNome().contains("@")
				|| fornecedor.getNome().contains("/")
				|| fornecedor.getNome().contains("*")
				|| fornecedor.getNome().contains("<")
				|| fornecedor.getNome().contains(">")
				|| fornecedor.getNome().contains("#")) {

			Msg.addMsgError("Contém caracter(es) inválido(s)");
			return null;
		}
		if (fornecedor.getNome().length() <= 2) {
			Msg.addMsgError("Informe pelo menos 3 caracteres");
			return null;

		} else {
			fornecedores = dao.getAllByName("nome", fornecedor.getNome());
			if (fornecedores.isEmpty()) {
				Msg.addMsgError("Nenhum registro encontrado");
				return null;

			} else {
				System.out.println("Chegou Aqui... Processando informações...");
				try {
					Query query = dao.query("SELECT f FROM Fornecedor f WHERE f.nome LIKE ?"); 
					query.setParameter(1, fornecedor.getNome() + "%");
					fornecedoresD = query.getResultList();
					System.out.println("Fornecedor encontrado com sucesso...");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		}
	}
	
/** validação UK Nome */
	
	public boolean validarNomeUK_nome() {
		return validator.validarNomeUK("nome", fornecedor.getNome());
	}
	
	public void checkNomeUK_login(AjaxBehaviorEvent event) {
		if(validarNomeUK_nome()){
			if (fornecedor.getNome().isEmpty()){
				validator.setResultNome("");
			}
		}
	}
	
	/** validação UK CPF / CNPJ */

	public boolean validarNomeUK_cnpj() {
		return validator.validarNomeUK("cnpj_cpf", fornecedor.getCnpj());
	}
	
	
	public void checkNomeUK_cpf(AjaxBehaviorEvent event) {
		if(validarNomeUK_cnpj()){
			if (fornecedor.getCnpj().isEmpty()){
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

	public Validator<Usuario> getValidator() {
		return validator;
	}

	public void setValidator(Validator<Usuario> validator) {
		this.validator = validator;
	}
	
	
		
}
