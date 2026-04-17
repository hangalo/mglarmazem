
package skylink.mglarmazem.modelo;

import java.util.Objects;

/**
 *
 * @Henriques
 */
public class Produto {
    
    
    private Integer idProduto;
    private String descricaoProduto;
    
    
    public Produto(){
    
    }

    public Integer getIdProduto() {
        return idProduto;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setIdProduto(Integer idProduto) {
        this.idProduto = idProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.idProduto);
        hash = 89 * hash + Objects.hashCode(this.descricaoProduto);
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
        final Produto other = (Produto) obj;
        return true;
    }

    @Override
    public String toString() {
        return "Produto{" + "idProduto=" + idProduto + ", descricaoProduto=" + descricaoProduto + '}';
    }
    
}

   