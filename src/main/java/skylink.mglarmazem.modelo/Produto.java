package skylink.mglarmazem.modelo;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Henriques
 */
public class Produto implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer idProduto;
    private String descricaoProduto;
    private String categoria; 
    
    public Produto(){
    }

    public Integer getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Integer idProduto) {
        this.idProduto = idProduto;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.idProduto);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Produto other = (Produto) obj;
        
        return Objects.equals(this.idProduto, other.idProduto);
    }

    @Override
    public String toString() {
        return descricaoProduto; 
    }
}