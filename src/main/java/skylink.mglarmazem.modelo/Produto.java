package skylink.mglarmazem.modelo;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Henriques
 */
public class Produto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idProduto;
    private String descricaoProduto;
    private Integer idCategoria;
    private Integer quantidadeExistente;

    public Produto() {
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

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Integer getQuantidadeExistente() {
        return quantidadeExistente;
    }

    public void setQuantidadeExistente(Integer quantidadeExistente) {
        this.quantidadeExistente = quantidadeExistente;
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
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Produto other = (Produto) obj;

        return Objects.equals(this.idProduto, other.idProduto);
    }

    
    
    @Override
    public String toString() {
        return String.format("%s[idProduto=%d]", getClass().getSimpleName(), getIdProduto());
    }

}
