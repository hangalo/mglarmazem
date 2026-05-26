package skylink.mglarmazem.mb;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import skylink.armazem.modelo.Estoque;
import skylink.armazem.modelo.Sector;
import skylinkmglarmazem.dao.EstoqueDAO;
import skylinkmglarmazem.dao.SectorDAO;

/**
 * @author Henriques
 */
@Named("estoqueBean")
@ViewScoped
public class EstoqueBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(EstoqueBean.class.getName());

    private Sector idSector; 
    private Date dataInicio;
    private Date dataFim;
    private Date hoje = new Date();

    private List<Sector> listaSectores;
    private List<Estoque> listaEstoqueFiltrado;

    private final EstoqueDAO estoqueDAO = new EstoqueDAO();
    private final SectorDAO sectorDAO = new SectorDAO();

    @PostConstruct
    public void init() {
        carregarSectores();
        limparFiltros();
    }

    public void limparFiltros() {
        this.idSector = null;
        this.dataInicio = null;
        this.dataFim = null;
        if (this.listaEstoqueFiltrado != null) {
            this.listaEstoqueFiltrado.clear();
        } else {
            this.listaEstoqueFiltrado = new ArrayList<>();
        }
    }

    private void carregarSectores() {
        try {
            this.listaSectores = sectorDAO.listarTudo();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar os setores para o controle de estoque", e);
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Não foi possível carregar a lista de setores.");
        }
    }

    public void pesquisarEstoque() {

        if (idSector == null || idSector.getIdSector() == null || dataInicio == null || dataFim == null) {
            adicionarMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Seleccione o sector e o intervalo de datas corretamente.");
            return;
        }

        try {

            this.listaEstoqueFiltrado = estoqueDAO.obterControleEstoquePorSetor(idSector.getIdSector(), dataInicio, dataFim);

            if (this.listaEstoqueFiltrado == null || this.listaEstoqueFiltrado.isEmpty()) {
                adicionarMensagem(FacesMessage.SEVERITY_INFO, "Informação", "Nenhum registro de estoque encontrado para os filtros aplicados.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao pesquisar controle de estoque", e);
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao processar a consulta no banco de dados.");
        }
    }

    private void adicionarMensagem(FacesMessage.Severity severidade, String resumo, String detalhe) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidade, resumo, detalhe));
    }

    public Sector getIdSector() { return idSector; }
    public void setIdSector(Sector idSector) { this.idSector = idSector; }

    public Date getDataInicio() { return dataInicio; }
    public void setDataInicio(Date dataInicio) { this.dataInicio = dataInicio; }

    public Date getDataFim() { return dataFim; }
    public void setDataFim(Date dataFim) { this.dataFim = dataFim; }

    public Date getHoje() { return hoje; }
    public void setHoje(Date hoje) { this.hoje = hoje; }

    public List<Sector> getStaticListaSectores() { return listaSectores; } 
    public List<Sector> getListaSectores() { return listaSectores; }
    public void setListaSectores(List<Sector> listaSectores) { this.listaSectores = listaSectores; }

    public List<Estoque> getListaEstoqueFiltrado() { return listaEstoqueFiltrado; }
    public void setListaEstoqueFiltrado(List<Estoque> listaEstoqueFiltrado) { this.listaEstoqueFiltrado = listaEstoqueFiltrado; }
}