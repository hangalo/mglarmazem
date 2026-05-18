package skylink.mglarmazem.mb;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.util.List;
import skylink.armazem.modelo.CategoriaProduto;
import skylinkmglarmazem.dao.CategoriaProdutoDAO;

/**
 * @author Henriques
 */
@Named("categoriaBean")
@RequestScoped
public class CategoriaBean {

    private List<CategoriaProduto> listaCategorias;
    private final CategoriaProdutoDAO dao = new CategoriaProdutoDAO();

    @PostConstruct
    public void init() {
        listaCategorias = dao.listarTudo();
    }

    public List<CategoriaProduto> getListaCategorias() {
        return listaCategorias;
    }
}
