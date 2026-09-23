package skylink.mglarmazem.mb;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import skylink.armazem.modelo.CategoriaProduto;
import skylink.armazem.modelo.Produto;
import skylinkmglarmazem.dao.ProdutoDAO;

@Named("produtoBean")
@ViewScoped
public class ProdutoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Produto produto;
    private List<Produto> listaProdutos;
    private List<Produto> listaProdutosCombo;
    private List<Produto> listaQuantidadeExistente;

    private Integer filtroIdCategoria;
    private String filtroDescricaoProduto;
    private List<CategoriaProduto> listaCategorias;

    private final ProdutoDAO dao = new ProdutoDAO();

    @PostConstruct
    public void init() {
        novo();
        carregarCategorias();
        carregarProdutosCombo();
        carregarQuantidadeExistente();   
        this.listaProdutos = new ArrayList<>();
    }

    public void novo() {
        produto = new Produto();
        produto.setQuantidadeExistente(0);
    }

    public void listar() {
        try {
            this.listaProdutos = dao.listarTudo();
        } catch (SQLException e) {
            this.listaProdutos = new ArrayList<>();
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro",
                    "Não foi possível listar os produtos.");
        }
    }

    public void carregarProdutosCombo() {
        try {
            this.listaProdutosCombo = dao.listarTudo();
        } catch (SQLException e) {
            this.listaProdutosCombo = new ArrayList<>();
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro",
                    "Não foi possível carregar os produtos para os combos.");
        }
    }

    public void carregarQuantidadeExistente() {
        try {
            this.listaQuantidadeExistente = dao.listarQuantidadeExistente();
        } catch (SQLException e) {
            this.listaQuantidadeExistente = new ArrayList<>();
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro",
                    "Não foi possível carregar os produtos em stock.");
        }
    }

    public void carregarCategorias() {
        try {
            this.listaCategorias = dao.listarCategorias();
        } catch (SQLException e) {
            this.listaCategorias = new ArrayList<>();
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro",
                    "Não foi possível carregar as categorias.");
        }
    }

    public void filtrar() {
        try {
            this.listaQuantidadeExistente =
                    dao.listarQuantidadeExistenteFiltrado(filtroIdCategoria,
                                                          filtroDescricaoProduto);
        } catch (SQLException e) {
            this.listaQuantidadeExistente = new ArrayList<>();
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro",
                    "Não foi possível filtrar os produtos em stock.");
        }
    }

    public void pesquisarPorCategoria() {
        try {
            if (filtroIdCategoria == null) {
                this.listaProdutos = dao.listarTudo();
            } else {
                this.listaProdutos = dao.listarPorCategoria(filtroIdCategoria);
            }
        } catch (SQLException e) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro",
                    "Não foi possível pesquisar os produtos.");
        }
    }

    public void verTodos() {
        this.filtroIdCategoria = null;
        this.filtroDescricaoProduto = null;
        listar();
    }

    public void limparFiltros() {
        this.filtroIdCategoria = null;
        this.filtroDescricaoProduto = null;
        carregarQuantidadeExistente();     // recarrega tudo
    }

    public void salvar() {
        try {
            boolean sucesso = (produto.getIdProduto() == null
                    || produto.getIdProduto() == 0)
                    ? dao.save(produto)
                    : dao.update(produto);

            if (sucesso) {
                adicionarMensagem(FacesMessage.SEVERITY_INFO, "Sucesso",
                        "Produto guardado com sucesso!");
                novo();
                listar();
                carregarQuantidadeExistente();
                carregarProdutosCombo();
            } else {
                adicionarMensagem(FacesMessage.SEVERITY_WARN, "Aviso",
                        "Nenhum registo foi afectado.");
            }
        } catch (SQLException e) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro",
                    "Erro ao guardar o produto.");
        }
    }

    public void prepararEditar(Produto p) {
        this.produto = p;
    }

    public void eliminar(Integer id) {
        if (id == null) {
            adicionarMensagem(FacesMessage.SEVERITY_WARN, "Aviso",
                    "ID do produto inválido.");
            return;
        }
        try {
            if (dao.delete(id)) {
                adicionarMensagem(FacesMessage.SEVERITY_INFO, "Sucesso",
                        "Produto removido com sucesso.");
                listar();
                carregarQuantidadeExistente();
                carregarProdutosCombo();
            } else {
                adicionarMensagem(FacesMessage.SEVERITY_WARN, "Aviso",
                        "Produto não encontrado.");
            }
        } catch (SQLException e) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro",
                    "Erro ao remover o produto.");
        }
    }

    private void adicionarMensagem(FacesMessage.Severity severidade,
                                   String resumo, String detalhe) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severidade, resumo, detalhe));
    }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public List<Produto> getListaProdutos() { return listaProdutos; }
    public void setListaProdutos(List<Produto> l) { this.listaProdutos = l; }

    public List<Produto> getListaProdutosCombo() { return listaProdutosCombo; }
    public void setListaProdutosCombo(List<Produto> l) { this.listaProdutosCombo = l; }

    public List<Produto> getListaQuantidadeExistente() { return listaQuantidadeExistente; }
    public void setListaQuantidadeExistente(List<Produto> l) { this.listaQuantidadeExistente = l; }

    public Integer getFiltroIdCategoria() { return filtroIdCategoria; }
    public void setFiltroIdCategoria(Integer filtroIdCategoria) { this.filtroIdCategoria = filtroIdCategoria; }

    public String getFiltroDescricaoProduto() { return filtroDescricaoProduto; }
    public void setFiltroDescricaoProduto(String filtroDescricaoProduto) { this.filtroDescricaoProduto = filtroDescricaoProduto; }

    public List<CategoriaProduto> getListaCategorias() { return listaCategorias; }
    public void setListaCategorias(List<CategoriaProduto> listaCategorias) { this.listaCategorias = listaCategorias; }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.produto);
        hash = 83 * hash + Objects.hashCode(this.listaProdutos);
        hash = 83 * hash + Objects.hashCode(this.listaProdutosCombo);
        hash = 83 * hash + Objects.hashCode(this.listaQuantidadeExistente);
        hash = 83 * hash + Objects.hashCode(this.filtroIdCategoria);
        hash = 83 * hash + Objects.hashCode(this.filtroDescricaoProduto);
        hash = 83 * hash + Objects.hashCode(this.listaCategorias);
        hash = 83 * hash + Objects.hashCode(this.dao);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProdutoBean other = (ProdutoBean) obj;
        if (!Objects.equals(this.filtroDescricaoProduto, other.filtroDescricaoProduto)) {
            return false;
        }
        if (!Objects.equals(this.produto, other.produto)) {
            return false;
        }
        if (!Objects.equals(this.listaProdutos, other.listaProdutos)) {
            return false;
        }
        if (!Objects.equals(this.listaProdutosCombo, other.listaProdutosCombo)) {
            return false;
        }
        if (!Objects.equals(this.listaQuantidadeExistente, other.listaQuantidadeExistente)) {
            return false;
        }
        if (!Objects.equals(this.filtroIdCategoria, other.filtroIdCategoria)) {
            return false;
        }
        if (!Objects.equals(this.listaCategorias, other.listaCategorias)) {
            return false;
        }
        return Objects.equals(this.dao, other.dao);
    }


}