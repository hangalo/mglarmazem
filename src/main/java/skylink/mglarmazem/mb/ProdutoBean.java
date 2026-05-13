package skylink.mglarmazem.mb;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import skylink.mglarmazem.modelo.Produto;
import skylinkmglarmazem.dao.ProdutoDAO;

/**
 * @author Henriques
 */
@Named("produtoBean")
@ViewScoped
public class ProdutoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Produto produto;
    private List<Produto> listaProdutos;
    private Integer filtroIdCategoria; 

    private final ProdutoDAO dao = new ProdutoDAO();

    @PostConstruct
    public void init() {
        novo();
        listar();
    }

   
    public void novo() {
        produto = new Produto();
        produto.setQuantidadeExistente(0); 
    }

    
    public void salvar() {
        try {
            boolean sucesso;
            if (produto.getIdProduto() == null || produto.getIdProduto() == 0) {
                sucesso = dao.save(produto);
            } else {
                sucesso = dao.update(produto);
            }

            if (sucesso) {
                adicionarMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Produto guardado com sucesso!");
                novo();   
                listar(); 
            } else {
                adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Não foi possível guardar o produto.");
            }
        } catch (Exception e) {
            adicionarMensagem(FacesMessage.SEVERITY_FATAL, "Erro Crítico", e.getMessage());
        }
    }

    
    public void listar() {
        listaProdutos = dao.listarTudo();
    }

    
    public void pesquisarPorCategoria() {
        if (filtroIdCategoria != null && filtroIdCategoria > 0) {
            listaProdutos = dao.listarPorCategoria(filtroIdCategoria);
        } else {
            listar(); 
        }
    }

    
    public void prepararEditar(Produto p) {
        this.produto = p;
    }

   
    public void eliminar(Integer id) {
        if (dao.delete(id)) {
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Produto removido.");
            listar();
        } else {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao eliminar produto.");
        }
    }

    
    private void adicionarMensagem(FacesMessage.Severity severidade, String resumo, String detalhe) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidade, resumo, detalhe));
    }


    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public List<Produto> getListaProdutos() {
        return listaProdutos;
    }

    public void setListaProdutos(List<Produto> listaProdutos) {
        this.listaProdutos = listaProdutos;
    }

    public Integer getFiltroIdCategoria() {
        return filtroIdCategoria;
    }

    public void setFiltroIdCategoria(Integer filtroIdCategoria) {
        this.filtroIdCategoria = filtroIdCategoria;
    }
}