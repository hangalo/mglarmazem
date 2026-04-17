package skylink.mglarmazem.mb;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import skylinkmglarmazem.dao.ProdutoDAO;
import skylink.mglarmazem.modelo.Produto;

/**
 *
 * @Henriques
 */

@Named("produto")
@ViewScoped
public class ProdutoBean implements Serializable {

    private Produto produto;
    private List<Produto> listaProdutos;

    private ProdutoDAO dao = new ProdutoDAO();

    
    @PostConstruct
    public void init() {
        produto = new Produto();
        listar();
    }

 
    public void salvar() {
        if (dao.save(produto)) {
            produto = new Produto(); 
            listar();
            System.out.println("Produto salvo com sucesso!");
        } else {
            System.out.println("Erro ao salvar produto!");
        }
    }

   
    public void atualizar() {
        if (dao.update(produto)) {
            produto = new Produto();
            listar();
            System.out.println("Produto atualizado com sucesso!");
        } else {
            System.out.println("Erro ao atualizar produto!");
        }
    }

   
    public void eliminar(int id) {
        if (dao.delete(id)) {
            listar();
            System.out.println("Produto eliminado com sucesso!");
        } else {
            System.out.println("Erro ao eliminar produto!");
        }
    }

   
    public void buscar(int id) {
        produto = dao.buscarPorCodigo(id);
    }

 
    public void listar() {
        listaProdutos = dao.listarTudo();
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
}
