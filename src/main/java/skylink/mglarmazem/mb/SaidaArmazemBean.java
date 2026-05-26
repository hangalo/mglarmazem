package skylink.mglarmazem.mb;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import skylink.armazem.modelo.Produto;
import skylink.armazem.modelo.SaidaArmazem;
import skylink.armazem.modelo.Sector;
import skylink.mglarmazem.modelo.EntradaArmazem;
import skylinkmglarmazem.dao.ProdutoDAO;
import skylinkmglarmazem.dao.SaidaArmazemDAO;
import skylinkmglarmazem.dao.SectorDAO;

/**
 * @author Henriques
 */
@Named("saidaArmazemBean")
@ViewScoped
public class SaidaArmazemBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(SaidaArmazemBean.class.getName());

    private SaidaArmazem saida;
    private List<SaidaArmazem> listaSaidas; 
    private List<EntradaArmazem> listaSaidasPorProduto; 
    
    private Sector idSector; 
    private List<Sector> listaSectores; 
    private List<SaidaArmazem> listaSaidasPorSector; 

    private Date dataInicio;
    private Date dataFim;
    private Date hoje = new Date();

    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final SaidaArmazemDAO dao = new SaidaArmazemDAO();
    private final SectorDAO sectorDAO = new SectorDAO(); 

    @PostConstruct
    public void init() {
        limpar();
        carregarHistorico();
        carregarSectores(); 
    }

    public void limpar() {
        this.saida = new SaidaArmazem(null, null, new Sector(), new Produto(), null, new EntradaArmazem(), new Sector());
        this.saida.setDataSaidaArmazem(new Date()); 
        this.idSector = null; 
    }

    public void limparFiltros() {
        this.dataInicio = null;
        this.dataFim = null;
        this.idSector = null;
        if (this.listaSaidasPorProduto != null) {
            this.listaSaidasPorProduto.clear();
        }
        if (this.listaSaidasPorSector != null) {
            this.listaSaidasPorSector.clear();
        }
    }
    
    private void carregarSectores() {
        try {
            this.listaSectores = sectorDAO.listarTudo();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar setores no banco de dados", e);
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Não foi possível carregar a lista de setores.");
        }
    }
    
    public void carregarHistorico() {
        try {
            this.listaSaidas = dao.listarTudo();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar histórico de saídas", e);
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Não foi possível carregar o histórico.");
        }
    }

    public void pesquisarPorSector() {
        if (idSector == null || idSector.getIdSector() == null || idSector.getIdSector() == 0 || dataInicio == null || dataFim == null) {
            adicionarMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Seleccione o sector e o intervalo de datas corretamente.");
            return;
        }

        try {
            this.listaSaidasPorSector = dao.listarPorSector(idSector.getIdSector(), dataInicio, dataFim);
            if (this.listaSaidasPorSector == null || this.listaSaidasPorSector.isEmpty()) {
                adicionarMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registo encontrado nesta data ", "Nenhum registo encontrado .");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao pesquisar saídas por setor", e);
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao buscar dados do setor especificado.");
        }
    }

    public void pesquisarSaidasProduto() {
        if (dataInicio == null || dataFim == null) {
            adicionarMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Seleccione o intervalo de datas.");
            return;
        }
        try {
            this.listaSaidasPorProduto = dao.pesquisarSaidasPorProduto(dataInicio, dataFim);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao pesquisar saídas por produto", e);
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao processar a pesquisa no banco.");
        }
    }

    public String registrar() {
        try {
            if (dao.registrarSaida(saida)) {
                int qtdeSaida = saida.getQuantidadeSaidaArmazem();
                int idProd = saida.getIdProduto().getIdProduto(); 
                
                produtoDAO.updateDiminuirQuantidade(qtdeSaida, idProd);
                
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                adicionarMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Dados guardados com sucesso!");
                limpar(); 
                return "/saida_armazem/registar_saida?faces-redirect=true";        
            } else {
                adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao guardar dados no sistema.");
                return null;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro catastrófico ao registrar saída no banco de dados", e);
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Falha de comunicação com o banco de dados.");
            return null;
        }
    }
   
    private void adicionarMensagem(FacesMessage.Severity severidade, String resumo, String detalhe) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidade, resumo, detalhe));
    }

    public SaidaArmazem getSaida() { return saida; }
    public void setSaida(SaidaArmazem saida) { this.saida = saida; }

    public List<SaidaArmazem> getListaSaidas() { return listaSaidas; }
    public void setListaSaidas(List<SaidaArmazem> listaSaidas) { this.listaSaidas = listaSaidas; }

    public List<EntradaArmazem> getListaSaidasPorProduto() { return listaSaidasPorProduto; }
    public void setListaSaidasPorProduto(List<EntradaArmazem> listaSaidasPorProduto) { this.listaSaidasPorProduto = listaSaidasPorProduto; }

    public Sector getIdSector() { return idSector; }
    public void setIdSector(Sector idSector) { this.idSector = idSector; }

    public List<Sector> getListaSectores() { return listaSectores; }
    public void setListaSectores(List<Sector> listaSectores) { this.listaSectores = listaSectores; }

    public List<SaidaArmazem> getListaSaidasPorSector() { return listaSaidasPorSector; }
    public void setListaSaidasPorSector(List<SaidaArmazem> listaSaidasPorSector) { this.listaSaidasPorSector = listaSaidasPorSector; }

    public Date getDataInicio() { return dataInicio; }
    public void setDataInicio(Date dataInicio) { this.dataInicio = dataInicio; }

    public Date getDataFim() { return dataFim; }
    public void setDataFim(Date dataFim) { this.dataFim = dataFim; }

    public Date getHoje() { return hoje; }
    public void setHoje(Date hoje) { this.hoje = hoje; }
}