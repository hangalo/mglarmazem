package skylink.mglarmazem.modelo;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Henriques
 */
public class Sector implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idSector;
    private String descricaoSector;

    public Sector() {
    }

    public Sector(Integer idSector, String descricaoSector) {
        this.idSector = idSector;
        this.descricaoSector = descricaoSector;
    }

    public Integer getIdSector() {
        return idSector;
    }

    public void setIdSector(Integer idSector) {
        this.idSector = idSector;
    }

    public String getDescricaoSector() {
        return descricaoSector;
    }

    public void setDescricaoSector(String descricaoSector) {
        this.descricaoSector = descricaoSector;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.idSector);
        hash = 67 * hash + Objects.hashCode(this.descricaoSector);
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
        final Sector other = (Sector) obj;
        if (!Objects.equals(this.descricaoSector, other.descricaoSector)) {
            return false;
        }
        return Objects.equals(this.idSector, other.idSector);
    }

  

    
    @Override
    public String toString() {
        return String.format("%s[idSector=%d]", getClass().getSimpleName(), getIdSector());
    }
}
