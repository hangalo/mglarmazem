package skylink.armazem.modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import skylink.mglarmazem.modelo.EntradaArmazem; 

public class SaidaArmazem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idSaidaArmazem;
    private Date dataSaidaArmazem;
    private Sector sector; 
    private Produto idProduto;
    private Integer quantidadeSaidaArmazem;
    private String unidadeMedida;
    private EntradaArmazem entradaArmazem; 

    public SaidaArmazem() {
    }

    public SaidaArmazem(Integer idSaidaArmazem, Date dataSaidaArmazem, Sector sector, Produto idProduto, 
                        Integer quantidadeSaidaArmazem, String unidadeMedida, EntradaArmazem entradaArmazem) {
        this.idSaidaArmazem = idSaidaArmazem;
        this.dataSaidaArmazem = dataSaidaArmazem;
        this.sector = sector;
        this.idProduto = idProduto;
        this.quantidadeSaidaArmazem = quantidadeSaidaArmazem;
        this.unidadeMedida = unidadeMedida;
        this.entradaArmazem = entradaArmazem;
    }

    public Integer getIdSaidaArmazem() {
        return idSaidaArmazem;
    }

    public void setIdSaidaArmazem(Integer idSaidaArmazem) {
        this.idSaidaArmazem = idSaidaArmazem;
    }

    public Date getDataSaidaArmazem() {
        return dataSaidaArmazem;
    }

    public void setDataSaidaArmazem(Date dataSaidaArmazem) {
        this.dataSaidaArmazem = dataSaidaArmazem;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public Sector getIdSector() {
        return sector;
    }

    public void setIdSector(Sector sector) {
        this.sector = sector;
    }

    public Produto getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Produto idProduto) {
        this.idProduto = idProduto;
    }

    public Integer getQuantidadeSaidaArmazem() {
        return quantidadeSaidaArmazem;
    }

    public void setQuantidadeSaidaArmazem(Integer quantidadeSaidaArmazem) {
        this.quantidadeSaidaArmazem = quantidadeSaidaArmazem;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public EntradaArmazem getEntradaArmazem() {
        return entradaArmazem;
    }

    public void setEntradaArmazem(EntradaArmazem entradaArmazem) {
        this.entradaArmazem = entradaArmazem;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.idSaidaArmazem);
        hash = 31 * hash + Objects.hashCode(this.unidadeMedida);
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
        final SaidaArmazem other = (SaidaArmazem) obj;
        if (!Objects.equals(this.idSaidaArmazem, other.idSaidaArmazem)) {
            return false;
        }
        return Objects.equals(this.unidadeMedida, other.unidadeMedida);
    }

    @Override
    public String toString() {
        return "SaidaArmazem{" + "idSaidaArmazem=" + idSaidaArmazem + ", dataSaidaArmazem=" + dataSaidaArmazem + ", quantidadeSaidaArmazem=" + quantidadeSaidaArmazem + ", unidadeMedida=" + unidadeMedida + '}';
    }
}