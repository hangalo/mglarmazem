package skylink.armazem.modelo;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Henriques
 */
public class CategoriaProduto implements Serializable {

    private Integer idCategoria;
    private String descricaoCategoria;

    public CategoriaProduto() {
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getDescricaoCategoria() {
        return descricaoCategoria;
    }

    public void setDescricaoCategoria(String descricaoCategoria) {
        this.descricaoCategoria = descricaoCategoria;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CategoriaProduto other = (CategoriaProduto) obj;
        return Objects.equals(idCategoria, other.idCategoria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCategoria);
    }
}
