package ufrr.editora.mb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import ufrr.editora.dao.DAO;
import ufrr.editora.entity.Endereco;
import ufrr.editora.entity.Fornecedor;
import ufrr.editora.util.Msg;

@ManagedBean
@ViewScoped
public class FornecedorBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	private Fornecedor fornecedor = new Fornecedor();
	private Endereco endereco = new Endereco();
	private List<Fornecedor> fornecedores;
	private DAO<Fornecedor> dao = new DAO<Fornecedor>(Fornecedor.class);
	public Boolean cadastro = true;
	
	/** List Products **/
	
	public List<Fornecedor> getFornecedores() {
		if (fornecedores == null) {
			System.out.println("Carregando Fornecedores...");
			fornecedores = new DAO<Fornecedor>(Fornecedor.class).listaTodos();
		}
		return fornecedores;
	}
	
	
	/** actions **/
	
	public void addFornecedor() {
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
	public String getListaFornecedoresByName() {
		if (fornecedor.getNome().contains("'")
				|| fornecedor.getNome().contains("@")
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
				Msg.addMsgInfo("Nenhum registro encontrado");
				return null;

			} else {
				System.out.println("Chegou Aqui...");
				return "/pages/fornecedor/consultarFornecedor.jsf";
			}
		}
	}
	
	public void check(AjaxBehaviorEvent event) {
		if (fornecedor.getNome().equals("")) {
			
		} else {
			if (fornecedor.getNome().contains("'")
					|| fornecedor.getNome().contains("@")
					|| fornecedor.getNome().contains("/")
					|| fornecedor.getNome().contains("*")) {
				Msg.addMsgError("Contém caracter(es) inválido(s)");
			} else {
				if (fornecedor.getNome().length() <= 2) {
					Msg.addMsgError("Informe pelo menos 3 caracteres");	
					
				} else {
					fornecedores = dao.getAllByName("nome", fornecedor.getNome());
					if (fornecedores.isEmpty()) {
						Msg.addMsgError("Nenhum registro encontrado");	
					} else {
						Integer count = fornecedores.size();
						Msg.addMsgError(count + "registro(s) encontrado(s)");
					}
				}
			}
		}
	}
	
	public void checkCnpj(AjaxBehaviorEvent event) {
		if (fornecedor.getNome().equals("")) {
			
		} else {
			if (fornecedor.getNome().contains("'")
					|| fornecedor.getNome().contains("@")
					|| fornecedor.getNome().contains("/")
					|| fornecedor.getNome().contains("*")) {
				Msg.addMsgError("Contém caracter(es) inválido(s)");
			} else {
				if (fornecedor.getNome().length() <= 2) {
					Msg.addMsgError("Informe pelo menos 3 caracteres");	
					
				} else {
					fornecedores = dao.getAllByName("nome", fornecedor.getNome());
					if (fornecedores.isEmpty()) {
						Msg.addMsgError("Nenhum registro encontrado");	
					} else {
						Integer count = fornecedores.size();
						Msg.addMsgError(count + "registro(s) encontrado(s)");
					}
				}
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
	
	
		
}
