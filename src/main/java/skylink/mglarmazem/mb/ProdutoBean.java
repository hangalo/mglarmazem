package skylink.mglarmazem.mb;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import skylinkmglarmazem.dao.ProdutoDAO;
import skylink.mglarmazem.modelo.Produto;

/**
 * @author Henriques
 */
@Named("produtoBean")
@ViewScoped
public class ProdutoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Produto produto;
    private List<Produto> listaProdutos;
    private String categoriaFiltro; 

    private ProdutoDAO dao = new ProdutoDAO();

    @PostConstruct
    public void init() {
        novo(); 
    }

    public void novo() {
        produto = new Produto();
    }

    
    public void prepararEditar(Produto p) {
        this.produto = p;
        adicionarMensagem(FacesMessage.SEVERITY_INFO, "Edição", "Produto selecionado para alteração.");
    }

    public void limpar() {
        this.categoriaFiltro = null;
        novo();
        
        adicionarMensagem(FacesMessage.SEVERITY_INFO, "Limpo", "Filtros e campos resetados.");
    }

    public void salvar() {
        boolean sucesso;

        if (produto.getIdProduto() == null || produto.getIdProduto() == 0) {
            sucesso = dao.save(produto);
        } else {
            sucesso = dao.update(produto);
        }

        if (sucesso) {
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Operação realizada com sucesso!");
            limpar();
        } else {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao processar produto!");
        }
    }

    public void pesquisarPorCategoria() {
        try {
            if (categoriaFiltro != null && !categoriaFiltro.trim().isEmpty()) {
                this.listaProdutos = dao.listarPorCategoria(categoriaFiltro);
                
                if (listaProdutos == null || listaProdutos.isEmpty()) {
                    adicionarMensagem(FacesMessage.SEVERITY_INFO, "Informação", "Nenhum produto encontrado nesta categoria.");
                }
            } else {
                adicionarMensagem(FacesMessage.SEVERITY_WARN, "Aviso", "Por favor, selecione uma categoria.");
                listar(); 
            }
        } catch (Exception e) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao filtrar categoria: " + e.getMessage());
        }
    }

    public void eliminar(int id) {
        if (dao.delete(id)) {
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Produto eliminado!");
            listar();
        } else {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao eliminar produto!");
        }
    }

    public void listar() {
        listaProdutos = dao.listarTudo();
    }

    private void adicionarMensagem(FacesMessage.Severity severidade, String resumo, String detalhe) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidade, resumo, detalhe));
    }

    public String getCategoriaFiltro() { return categoriaFiltro; }
    public void setCategoriaFiltro(String categoriaFiltro) { this.categoriaFiltro = categoriaFiltro; }
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public List<Produto> getListaProdutos() { return listaProdutos; }
    public void setListaProdutos(List<Produto> listaProdutos) { this.listaProdutos = listaProdutos; }
}