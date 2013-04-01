package ufrr.editora.mb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import ufrr.editora.dao.DAO;
import ufrr.editora.entity.Produto;
import ufrr.editora.util.Msg;

@ManagedBean
@ViewScoped
public class ProdutoBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	private Produto produto = new Produto();
	private List<Produto> produtos;
	private List<Produto> produtos1;
	private DAO<Produto> dao = new DAO<Produto>(Produto.class);
	public Boolean cadastro = true;
	
	/** List Products **/
	
	public List<Produto> getProdutos() {
		if (produtos == null) {
			System.out.println("Carregando produtos...");
			produtos = new DAO<Produto>(Produto.class).listaTodos();
		}
		return produtos;
	}
	
	// Exibe uma lista com o id tipoProduto == 1
		public List<Produto> getTiposId1() {
			produtos1 = new ArrayList<Produto>();
			for (Produto p : this.getProdutos()) {
				if (p.getTipo().getId() == 1) {
					produtos1.add(p);
				}
			}
			return produtos1;
		}

		// Exibe uma lista com o id tipoProduto != 1
		public List<Produto> getTipoOutros() {
			produtos1 = new ArrayList<Produto>();
			for (Produto p : this.getProdutos()) {
				if (p.getTipo().getId() != 1) {
					produtos1.add(p);
				}
			}
			return produtos1;
		}
	
	
	/** actions **/
	
	/** add para produto do tipo livro **/
	public String addProduto() {
		for (Produto produtos : this.getProdutos()) {
			if(produtos.getNome().equalsIgnoreCase(this.getProduto().getNome()) &&
					produtos.getEditora().equalsIgnoreCase(this.getProduto().getEditora()) ||
					produtos.getIsbn().equals(this.getProduto().getIsbn())){ //uk: nome and editora ou ISBN 
				this.cadastro = false;
				break;
			}
		}
		if(this.cadastro == true){
			if(produto.getNome().length() <= 4 || produto.getNome().isEmpty()){
				Msg.addMsgError("Informe corretamente o título da obra");
				
			} else {
				
				if(produto.getAutor().isEmpty() || produto.getCategoria().isEmpty() ||
						produto.getEditora().isEmpty()){
				Msg.addMsgError("Informe corretamente os campos obrigatórios");
				}
				else {
					Msg.addMsgInfo("Obra cadastrada com sucesso");
					dao.adiciona(produto);
					this.produto = new Produto();
					return "/pages/produto/cadastrarProduto.xhtml";
				}
			}
		} else {
			Msg.addMsgError("Livro já registrado");
		}
		produtos = dao.getAllOrder("nome");
		this.cadastro = true;
		return null;
	}
	
	public void alterProduto() {
		if (produto.getId() != null) {
			Msg.addMsgInfo("Produto editado com sucesso");
			dao.atualiza(produto);
			this.produto = new Produto();
		}
	}
	
	/** add para produto do tipo outros **/
	public String addProdutoOutros() {
		for (Produto produtos : this.getProdutos()) {
			if(produtos.getNome().equalsIgnoreCase(this.getProduto().getNome()) &&
					produtos.getEditora().equalsIgnoreCase(this.getProduto().getEditora())){ //pk: nome and editora
				this.cadastro = false;
				break;
			}
		}
		if(this.cadastro == true){
			if(produto.getNome().length() <= 4 || produto.getNome().isEmpty()){
				Msg.addMsgError("Informe corretamente a descrição do produto");
				
			} else {
				
				Msg.addMsgInfo("Produto cadastrado com sucesso");
				dao.adiciona(produto);
				this.produto = new Produto();
				return "/pages/produto/cadastrarProdutoOutros.xhtml";
								
			}
		} else {
			Msg.addMsgError("Nome já registrado");
		}
		produtos = dao.getAllOrder("nome");
		this.cadastro = true;
		return null;
	}
	
	public List<String> autocomplete(String nome) {
		List<Produto> array = dao.getAllByName("nome", nome);
		ArrayList<String> nomes = new ArrayList<String>();
		for (int i = 0; i < array.size(); i++) {
			nomes.add(array.get(i).getNome());
		}
		return nomes;
	}
	
	public List<String> autocompleteautor(String nome) {
		List<Produto> array = dao.getAllByName("autor", nome);
		ArrayList<String> nomes = new ArrayList<String>();
		for (int i = 0; i < array.size(); i++) {
			nomes.add(array.get(i).getAutor());
		}
		return nomes;
	}
	
	public List<String> autocompleteeditora(String nome) {
		List<Produto> array = dao.getAllByName("editora", nome);
		ArrayList<String> nomes = new ArrayList<String>();
		for (int i = 0; i < array.size(); i++) {
			nomes.add(array.get(i).getEditora());
		}
		return nomes;
	}
	
	public List<String> autocompletecategoria(String nome) {
		List<Produto> array = dao.getAllByName("categoria", nome);
		ArrayList<String> nomes = new ArrayList<String>();
		for (int i = 0; i < array.size(); i++) {
			nomes.add(array.get(i).getCategoria());
		}
		return nomes;
	}
	

	public void check(AjaxBehaviorEvent event) {
		if (produto.getNome().equals("")) {
			
		} else {
			if (produto.getNome().contains("'")
					|| produto.getNome().contains("@")
					|| produto.getNome().contains("/")
					|| produto.getNome().contains("*")) {
				Msg.addMsgError("Contém caracter(es) inválido(s)");
			} else {
				if (produto.getNome().length() <= 2) {
					Msg.addMsgError("Informe pelo menos 3 caracteres");	
					
				} else {
					produtos = dao.getAllByName("nome", produto.getNome());
					if (produtos.isEmpty()) {
						Msg.addMsgError("Nenhum registro encontrado");	
					} else {
						Integer count = produtos.size();
						Msg.addMsgError(count + "registro(s) encontrado(s)");
					}
				}
			}
		}
	}

	
	/** Get and Set **/

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public void setProdutos(List<Produto> produtos) {
		this.produtos = produtos;
	}	

	public DAO<Produto> getDao() {
		return dao;
	}

	public void setDao(DAO<Produto> dao) {
		this.dao = dao;
	}

	public Boolean getCadastro() {
		return cadastro;
	}

	public void setCadastro(Boolean cadastro) {
		this.cadastro = cadastro;
	}

	public List<Produto> getProdutos1() {
		return produtos1;
	}

	public void setProdutos1(List<Produto> produtos1) {
		this.produtos1 = produtos1;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}
	

}
