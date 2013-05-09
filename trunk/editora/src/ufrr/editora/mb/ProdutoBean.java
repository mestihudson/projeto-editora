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
import ufrr.editora.entity.Produto;
import ufrr.editora.util.Msg;
import ufrr.editora.validator.Validator;

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
	private Validator<Produto> validator;
	private String search;
	private String box4Search;

	@PostConstruct
	public void init() {

		produto = new Produto();
		validator = new Validator<Produto>(Produto.class);
		search = "";
		box4Search = "isbn";
	}

	/** List Products **/

	public List<Produto> getProdutos() {
		if (produtos == null) {
			System.out.println("Carregando produtos...");
			produtos = new DAO<Produto>(Produto.class).getAllOrder("nome");
		}
		return produtos;
	}

	// lista todos os produtos ativados
	public List<Produto> getAtivados() {
		produtos1 = new ArrayList<Produto>();
		List<Produto> ps = new ArrayList<Produto>();
		ps = this.getProdutos();
		for (int i = 0; i < ps.size(); i++) {
			if (ps.get(i).getAtivado().equals(true)) {
				produtos1.add(ps.get(i));
			}
		}
		return produtos1;
	}

	// Lista de livros ativados
	public List<Produto> getLivrosAtivados() {
		produtos1 = new ArrayList<Produto>();
		List<Produto> ps = new ArrayList<Produto>();
		ps = this.getProdutos();
		for (int i = 0; i < ps.size(); i++) {
			if (ps.get(i).getAtivado().equals(true)
					&& ps.get(i).getTipo().getId() == 1) {
				produtos1.add(ps.get(i));
			}
		}
		return produtos1;
	}

	// Lista de produtos ativados (sem livros)
	public List<Produto> getProdutosAtivados() {
		produtos1 = new ArrayList<Produto>();
		List<Produto> ps = new ArrayList<Produto>();
		ps = this.getProdutos();
		for (int i = 0; i < ps.size(); i++) {
			if (ps.get(i).getAtivado().equals(true)
					&& ps.get(i).getTipo().getId() != 1) {
				produtos1.add(ps.get(i));
			}
		}
		return produtos1;
	}

	// Lista de livros desativados
	public List<Produto> getLivrosDesativados() {
		produtos1 = new ArrayList<Produto>();
		List<Produto> ps = new ArrayList<Produto>();
		ps = this.getProdutos();
		for (int i = 0; i < ps.size(); i++) {
			if (ps.get(i).getAtivado().equals(false)
					&& ps.get(i).getTipo().getId() == 1) {
				produtos1.add(ps.get(i));
			}
		}
		return produtos1;
	}

	// Lista de produtos desativados (sem livros)
	public List<Produto> getProdutosDesativados() {
		produtos1 = new ArrayList<Produto>();
		List<Produto> ps = new ArrayList<Produto>();
		ps = this.getProdutos();
		for (int i = 0; i < ps.size(); i++) {
			if (ps.get(i).getAtivado().equals(false)
					&& ps.get(i).getTipo().getId() != 1) {
				produtos1.add(ps.get(i));
			}
		}
		return produtos1;
	}

	// Exibe uma lista com o id tipoProduto == 1
	public List<Produto> getTiposId1() {
		produtos1 = new ArrayList<Produto>();
		for (Produto p : this.getProdutos()) {
			if (p.getTipo().getId() == 1 && p.getAtivado().equals(true)) {
				produtos1.add(p);
			}
		}
		return produtos1;
	}

	// Exibe uma lista com o id tipoProduto != 1
	public List<Produto> getTipoOutros() {
		produtos1 = new ArrayList<Produto>();
		for (Produto p : this.getProdutos()) {
			if (p.getTipo().getId() != 1 && p.getAtivado().equals(true)) {
				produtos1.add(p);
			}
		}
		return produtos1;
	}

	// Pesquisar
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

	/** actions **/

	// Cadastrar livro
	public String addProduto() {
		try {
			boolean all = true;
			if (!validarNome_editora()) {
				all = false;
				Msg.addMsgError("Editora não pode ser vazio");
			}
			if (!validarNome_nome()) {
				all = false;
				Msg.addMsgError("Nome não pode ser vazio");
			}
			if (!validarIntegerUK_isbn()) {
				all = false;
				Msg.addMsgError("Este produto já existe");
			}
			if (!all) {
				System.out
						.println("...Erro ao cadastrar produto: produto já existe");
				return null;
			} else {
				produto.setAtivado(true);
				dao.adiciona(produto);
				this.produto = new Produto();
				init();
				Msg.addMsgInfo("Cadastro efetuado com sucesso");
				System.out
						.println("...cadastro de produto efetuado com sucesso!");
			}
		} catch (Exception e) {
			init();
			e.printStackTrace();
			System.out
					.println("...Alguma coisa deu errada ao cadastrar produto");
		}
		return null;
	}

	// Cadastrar produto do tipo Outros
	public String addOutros() {
		try {
			boolean all = true;
			if (!validarNameUK_nome()) {
				all = false;
				Msg.addMsgError("Outro produto com a mesma descrição já foi registrado no sistema");
				return null;
			}
			if (!all) {
				System.out.println("...Erro ao cadastrar produto: produto já existe");
				return null;
			} else {
				produto.setAtivado(true);
				dao.adiciona(produto);
				this.produto = new Produto();
				init();
				Msg.addMsgInfo("Cadastro efetuado com sucesso");
				System.out.println("...cadastro de produto efetuado com sucesso!");
			}
		} catch (Exception e) {
			init();
			e.printStackTrace();
			System.out
					.println("...Alguma coisa deu errada ao cadastrar produto");
		}
		return null;
	}

	// Edita dados do produto tipo livro
	public String alterProduto() {
		try {
			boolean all = true;
			if (!validarNome_editora()) {
				all = false;
				Msg.addMsgError("Editora não pode ser vazio");
			}
			if (!validarNome_nome()) {
				all = false;
				Msg.addMsgError("Nome não pode ser vazio");
			}
			if (!all) {
				System.out
						.println("...Erro ao cadastrar produto: este produto já existe");
				return null;
			} else {
				dao.atualiza(produto);
				this.produto = new Produto();
				init();
				Msg.addMsgInfo("Produto editado com sucesso");
				System.out.println("...produto editado com sucesso!");
			}
		} catch (Exception e) {
			init();
			e.printStackTrace();
			System.out.println("...Alguma coisa deu errada ao editar produto");
		}
		return null;
	}

	// Edita dados do produto tipo livro
	public String alterOutros() {
		try {
			boolean all = true;
			if (!validarNome_nome()) {
				all = false;
				Msg.addMsgError("Nome do produto não pode ser vazio");
			}
			if (!all) {
				System.out
						.println("...Erro ao cadastrar produto: nome não pode ser vazio");
				return null;
			} else {
				dao.atualiza(produto);
				this.produto = new Produto();
				init();
				Msg.addMsgInfo("Produto editado com sucesso");
				System.out.println("...produto editado com sucesso!");
			}
		} catch (Exception e) {
			init();
			e.printStackTrace();
			System.out.println("...Alguma coisa deu errada ao editar produto");
		}
		return null;
	}

	// Desativa qualquer tipo de produto
	public void desativar() {
		if (produto.getId() != null) {
			Msg.addMsgInfo("Produto desativado");
			produto.setAtivado(false);
			dao.atualiza(produto);
		}
	}

	// Reativa qualquer tipo de produto
	public void ativar() {
		if (produto.getId() != null) {
			Msg.addMsgInfo("Produto reativado");
			produto.setAtivado(true);
			dao.atualiza(produto);
		}
	}

	/** ajax */

	public void checkBox4Search(AjaxBehaviorEvent event) {

	}

	public void checkEditora(AjaxBehaviorEvent event) {
		validarEditora();
		if (produto.getEditora().isEmpty()) {
			validator.setResultNome("");
		}
	}

	/** validação UK ISBN */

	public boolean validarIntegerUK_isbn() {
		return validator.validarIntegerUK("isbn", produto.getIsbn());
	}

	public void checkNomeUK_nome(AjaxBehaviorEvent event) {
		if (validarNameUK_nome()) {
			if (produto.getNome().isEmpty()) {
				validator.setResultNome("");
			}
		}
	}

	/** validação UK ISBN */

	public boolean validarNameUK_nome() {
		return validator.validarNomeUK("nome", produto.getNome());
	}

	public void checkNomeUK_descricao(AjaxBehaviorEvent event) {
		if (validarNameUK_nome()) {
			if (produto.getEditora().isEmpty()) {
				validator.setResultNome("");
			}
		}
	}

	/** validação EMPTY */

	public boolean validarNome() {
		return validator.validarNome(produto.getNome());
	}

	public boolean validarEditora() {
		return validator.validarNome(produto.getEditora());
	}

	/** autocomplete **/

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

	public Validator<Produto> getValidator() {
		return validator;
	}

	public void setValidator(Validator<Produto> validator) {
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

	public void checkNome_editora(AjaxBehaviorEvent event) {
		validarNome_editora();
	}

	public boolean validarNome_editora() {
		return validator.validarNome(produto.getEditora());
	}

	public void checkNome_nome(AjaxBehaviorEvent event) {
		validarNome_nome();
	}

	public boolean validarNome_nome() {
		return validator.validarNome(produto.getNome());
	}

}
