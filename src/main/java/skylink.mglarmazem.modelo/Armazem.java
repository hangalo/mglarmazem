package skylink.mglarmazem.modelo;

import java.util.Date;
import java.util.Objects;

/**
 * @author Henriques
 */
public class Armazem {

    private Integer idArmazem;
    private Date dataRegisto;
    private Double precoProduto;
    private Date dataCompra;
    private Integer quantidadeProduto;
    private Integer idProduto;
    
    private String descricaoProduto;

    public Armazem() {
    }

    public Armazem(Integer idArmazem, Date dataRegisto, Double precoProduto, Date dataCompra, Integer quantidadeProduto, Integer idProduto) {
        this.idArmazem = idArmazem;
        this.dataRegisto = dataRegisto;
        this.precoProduto = precoProduto;
        this.dataCompra = dataCompra;
        this.quantidadeProduto = quantidadeProduto;
        this.idProduto = idProduto;
    }


    public Integer getIdArmazem() {
        return idArmazem;
    }

    public void setIdArmazem(Integer idArmazem) {
        this.idArmazem = idArmazem;
    }

    public Date getDataRegisto() {
        return dataRegisto;
    }

    public void setDataRegisto(Date dataRegisto) {
        this.dataRegisto = dataRegisto;
    }

    public Double getPrecoProduto() {
        return precoProduto;
    }

    public void setPrecoProduto(Double precoProduto) {
        this.precoProduto = precoProduto;
    }

    public Date getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(Date dataCompra) {
        this.dataCompra = dataCompra;
    }

    public Integer getQuantidadeProduto() {
        return quantidadeProduto;
    }

    public void setQuantidadeProduto(Integer quantidadeProduto) {
        this.quantidadeProduto = quantidadeProduto;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.idArmazem);
        hash = 79 * hash + Objects.hashCode(this.dataRegisto);
        hash = 79 * hash + Objects.hashCode(this.precoProduto);
        hash = 79 * hash + Objects.hashCode(this.dataCompra);
        hash = 79 * hash + Objects.hashCode(this.quantidadeProduto);
        hash = 79 * hash + Objects.hashCode(this.idProduto);
        hash = 79 * hash + Objects.hashCode(this.descricaoProduto);
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
        final Armazem other = (Armazem) obj;
        return Objects.equals(this.idArmazem, other.idArmazem) &&
               Objects.equals(this.dataRegisto, other.dataRegisto) &&
               Objects.equals(this.precoProduto, other.precoProduto) &&
               Objects.equals(this.dataCompra, other.dataCompra) &&
               Objects.equals(this.quantidadeProduto, other.quantidadeProduto) &&
               Objects.equals(this.idProduto, other.idProduto) &&
               Objects.equals(this.descricaoProduto, other.descricaoProduto);
    }

    @Override
    public String toString() {
        return "Armazem{" + "idArmazem=" + idArmazem + ", descricaoProduto=" + descricaoProduto + '}';
    }
}