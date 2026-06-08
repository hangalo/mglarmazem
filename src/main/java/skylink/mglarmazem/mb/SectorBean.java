package skylink.mglarmazem.mb;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped; 
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import skylink.armazem.modelo.Sector;
import skylinkmglarmazem.dao.SectorDAO;

/**
 * @author Henriques
 */
@Named("sectorBean")
@ViewScoped
public class SectorBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Sector> listaSectores;
    private final SectorDAO dao = new SectorDAO();

    @PostConstruct
    public void init() {

        this.listaSectores = dao.listarTudo();
    }

    public List<Sector> getListaSectores() {
        return listaSectores;
    }
}
