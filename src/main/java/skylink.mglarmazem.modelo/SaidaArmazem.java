package skylink.mglarmazem.modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author Henriques
 */

public class SaidaArmazem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idSaidaArmazem;
    private Date dataSaidaArmazem;
    private Integer idSector;
    private Integer idProduto;
    private Integer quantidadeSaidaArmazem;

    public SaidaArmazem() {
        this.dataSaidaArmazem = new Date(); 
    }

    public Integer getIdSaidaArmazem() { return idSaidaArmazem; }
    public void setIdSaidaArmazem(Integer idSaidaArmazem) { this.idSaidaArmazem = idSaidaArmazem; }

    public Date getDataSaidaArmazem() { return dataSaidaArmazem; }
    public void setDataSaidaArmazem(Date dataSaidaArmazem) { this.dataSaidaArmazem = dataSaidaArmazem; }

    public Integer getIdSector() { return idSector; }
    public void setIdSector(Integer idSector) { this.idSector = idSector; }

    public Integer getIdProduto() { return idProduto; }
    public void setIdProduto(Integer idProduto) { this.idProduto = idProduto; }

    public Integer getQuantidadeSaidaArmazem() { return quantidadeSaidaArmazem; }
    public void setQuantidadeSaidaArmazem(Integer quantidadeSaidaArmazem) { this.quantidadeSaidaArmazem = quantidadeSaidaArmazem; }

    @Override
    public int hashCode() {
        return 7 * 31 + Objects.hashCode(this.idSaidaArmazem);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final SaidaArmazem other = (SaidaArmazem) obj;
        return Objects.equals(this.idSaidaArmazem, other.idSaidaArmazem);
    }
}