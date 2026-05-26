package skylink.armazem.modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import skylink.mglarmazem.modelo.EntradaArmazem; 
import skylink.armazem.modelo.Sector;

public class SaidaArmazem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idSaidaArmazem;
    private Date dataSaidaArmazem;
    private Sector idSector;
    private Produto idProduto;
    private Integer quantidadeSaidaArmazem;

    private EntradaArmazem entradaArmazem; 
    private Sector sector;

    public SaidaArmazem(java.lang.Integer idSaidaArmazem, Date dataSaidaArmazem, Sector idSector, Produto idProduto, Integer quantidadeSaidaArmazem, EntradaArmazem entradaArmazem, Sector sector) {
        this.idSaidaArmazem = idSaidaArmazem;
        this.dataSaidaArmazem = dataSaidaArmazem;
        this.idSector = idSector;
        this.idProduto = idProduto;
        this.quantidadeSaidaArmazem = quantidadeSaidaArmazem;
        this.entradaArmazem = entradaArmazem;
        this.sector = sector;
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

    public Sector getIdSector() {
        return idSector;
    }

    public void setIdSector(Sector idSector) {
        this.idSector = idSector;
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

    public EntradaArmazem getEntradaArmazem() {
        return entradaArmazem;
    }

    public void setEntradaArmazem(EntradaArmazem entradaArmazem) {
        this.entradaArmazem = entradaArmazem;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.idSaidaArmazem);
        hash = 59 * hash + Objects.hashCode(this.dataSaidaArmazem);
        hash = 59 * hash + Objects.hashCode(this.idSector);
        hash = 59 * hash + Objects.hashCode(this.idProduto);
        hash = 59 * hash + Objects.hashCode(this.quantidadeSaidaArmazem);
        hash = 59 * hash + Objects.hashCode(this.entradaArmazem);
        hash = 59 * hash + Objects.hashCode(this.sector);
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
        final SaidaArmazem other = (SaidaArmazem) obj;
        if (!Objects.equals(this.idSaidaArmazem, other.idSaidaArmazem)) {
            return false;
        }
        if (!Objects.equals(this.dataSaidaArmazem, other.dataSaidaArmazem)) {
            return false;
        }
        if (!Objects.equals(this.idSector, other.idSector)) {
            return false;
        }
        if (!Objects.equals(this.idProduto, other.idProduto)) {
            return false;
        }
        if (!Objects.equals(this.quantidadeSaidaArmazem, other.quantidadeSaidaArmazem)) {
            return false;
        }
        if (!Objects.equals(this.entradaArmazem, other.entradaArmazem)) {
            return false;
        }
        return Objects.equals(this.sector, other.sector);
    }

    @Override
    public String toString() {
        return "SaidaArmazem{" + "idSaidaArmazem=" + idSaidaArmazem + ", dataSaidaArmazem=" + dataSaidaArmazem + ", idSector=" + idSector + ", idProduto=" + idProduto + ", quantidadeSaidaArmazem=" + quantidadeSaidaArmazem + ", entradaArmazem=" + entradaArmazem + ", sector=" + sector + '}';
    }

    
    
}