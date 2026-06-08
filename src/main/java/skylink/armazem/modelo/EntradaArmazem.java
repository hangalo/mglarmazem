package skylink.mglarmazem.modelo;

import skylink.armazem.modelo.Produto;
import skylink.armazem.modelo.Sector;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @author Henriques
 */
public class EntradaArmazem {

    private Integer idArmazem;
    private Date dataRegisto;
    private Double precoProduto;
    private Date dataCompra;
    private Integer quantidadeProduto;
    private String unidadeMedida;
    private Produto produto;
    private Sector sector;

    private BigDecimal totalValorRelatorio;

    public EntradaArmazem() {
    }

    public EntradaArmazem(String descricaoProd, int totalQuantidade, BigDecimal totalValor) {
        this.quantidadeProduto = totalQuantidade;
        this.totalValorRelatorio = totalValor;

        this.produto = new Produto();
        this.produto.setDescricaoProduto(descricaoProd);
    }

    public EntradaArmazem(Integer idArmazem, Date dataRegisto, Double precoProduto, Date dataCompra, Integer quantidadeProduto, String unidadeMedida, Produto produto, Sector sector, BigDecimal totalValorRelatorio) {
        this.idArmazem = idArmazem;
        this.dataRegisto = dataRegisto;
        this.precoProduto = precoProduto;
        this.dataCompra = dataCompra;
        this.quantidadeProduto = quantidadeProduto;
        this.unidadeMedida = unidadeMedida;
        this.produto = produto;
        this.sector = sector;
        this.totalValorRelatorio = totalValorRelatorio;
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

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
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

    public Produto getProduto() {
        return produto;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public BigDecimal getTotalValorRelatorio() {
        return totalValorRelatorio;
    }

    public void setTotalValorRelatorio(BigDecimal totalValorRelatorio) {
        this.totalValorRelatorio = totalValorRelatorio;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.idArmazem);
        hash = 41 * hash + Objects.hashCode(this.dataRegisto);
        hash = 41 * hash + Objects.hashCode(this.precoProduto);
        hash = 41 * hash + Objects.hashCode(this.dataCompra);
        hash = 41 * hash + Objects.hashCode(this.quantidadeProduto);
        hash = 41 * hash + Objects.hashCode(this.unidadeMedida);
        hash = 41 * hash + Objects.hashCode(this.produto);
        hash = 41 * hash + Objects.hashCode(this.sector);
        hash = 41 * hash + Objects.hashCode(this.totalValorRelatorio);
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
        final EntradaArmazem other = (EntradaArmazem) obj;
        if (!Objects.equals(this.unidadeMedida, other.unidadeMedida)) {
            return false;
        }
        if (!Objects.equals(this.idArmazem, other.idArmazem)) {
            return false;
        }
        if (!Objects.equals(this.dataRegisto, other.dataRegisto)) {
            return false;
        }
        if (!Objects.equals(this.precoProduto, other.precoProduto)) {
            return false;
        }
        if (!Objects.equals(this.dataCompra, other.dataCompra)) {
            return false;
        }
        if (!Objects.equals(this.quantidadeProduto, other.quantidadeProduto)) {
            return false;
        }
        if (!Objects.equals(this.produto, other.produto)) {
            return false;
        }
        if (!Objects.equals(this.sector, other.sector)) {
            return false;
        }
        return Objects.equals(this.totalValorRelatorio, other.totalValorRelatorio);
    }

    @Override
    public String toString() {
        return "EntradaArmazem{" + "idArmazem=" + idArmazem + ", dataRegisto=" + dataRegisto + ", precoProduto=" + precoProduto + ", dataCompra=" + dataCompra + ", quantidadeProduto=" + quantidadeProduto + ", unidadeMedida=" + unidadeMedida + ", produto=" + produto + ", sector=" + sector + ", totalValorRelatorio=" + totalValorRelatorio + '}';
    }

}
