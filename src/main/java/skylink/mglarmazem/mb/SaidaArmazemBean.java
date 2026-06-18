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

    private Date dataInicio;
    private Date dataFim;
    private Date hoje = new Date();

    private List<SaidaArmazem>   listaSaidasPorSector   = new ArrayList<>();
    private List<EntradaArmazem> listaSaidasPorProduto  = new ArrayList<>();
    private List<SaidaArmazem>   listaDetalhadaPorSector = new ArrayList<>();
    private List<SaidaArmazem>   listaSaidas            = new ArrayList<>();

    private List<Sector>  listaSectores  = new ArrayList<>();
    private List<Produto> listaProdutos  = new ArrayList<>();

    private transient SaidaArmazemDAO dao;
    private transient ProdutoDAO      produtoDAO;
    private transient SectorDAO       sectorDAO;

    @PostConstruct
    public void init() {
        inicializarDaos();
        limpar();
        try {
            carregarDropdowns();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro na inicialização do bean", e);
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro crítico",
                    "Falha ao carregar dados iniciais. Tente recarregar a página.");
        }
    }

    private void inicializarDaos() {
        if (dao        == null) dao        = new SaidaArmazemDAO();
        if (produtoDAO == null) produtoDAO = new ProdutoDAO();
        if (sectorDAO  == null) sectorDAO  = new SectorDAO();
    }

   
    public String registrar() {
        inicializarDaos();

        if (saida.getIdProduto() == null || saida.getIdProduto().getIdProduto() == 0) {
            addMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
                    "Seleccione um produto válido para realizar a saída.");
            return null;
        }
        if (saida.getSector() == null || saida.getSector().getIdSector() == 0) {
            addMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
                    "Seleccione o sector de destino.");
            return null;
        }
        if (saida.getQuantidadeSaidaArmazem() <= 0) {
            addMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
                    "A quantidade de saída deve ser maior que zero.");
            return null;
        }

        int idProd    = saida.getIdProduto().getIdProduto();
        int qtdeSaida = saida.getQuantidadeSaidaArmazem();

        try {
            int disponivel = produtoDAO.buscarQuantidadeAtual(idProd);

            if (qtdeSaida > disponivel) {
                addMensagem(FacesMessage.SEVERITY_WARN, "Estoque Insuficiente",
                        "Solicitado: " + qtdeSaida + " | Disponível: " + disponivel + " unidades.");
                return null;
            }

            if (dao.registrarSaida(saida)) {
                produtoDAO.updateDiminuirQuantidade(qtdeSaida, idProd);
                FacesContext.getCurrentInstance()
                        .getExternalContext().getFlash().setKeepMessages(true);
                addMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Saída registada com sucesso!");
                limpar();
                return "/saida_armazem/registar_saida?faces-redirect=true";
            } else {
                addMensagem(FacesMessage.SEVERITY_ERROR, "Erro",
                        "Não foi possível guardar o registo.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao registrar saída", e);
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro de Base de Dados",
                    "Falha ao comunicar com a base de dados.");
        }
        return null;
    }

    
    public void pesquisarPorSector() {
        inicializarDaos();

        if (saida.getSector() == null || saida.getSector().getIdSector() == 0) {
            addMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Seleccione um sector.");
            return;
        }
        if (!validarDatas()) return;

        try {
            listaSaidasPorSector = dao.listarEntreDatasPorSector(
                    saida.getSector().getIdSector(), dataInicio, dataFim);

            if (listaSaidasPorSector.isEmpty()) {
                addMensagem(FacesMessage.SEVERITY_INFO, "Sem resultados",
                        "Nenhuma saída encontrada para o sector e período indicados.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro em pesquisarPorSector", e);
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro",
                    "Falha ao listar saídas por sector.");
        }
    }

   public void pesquisarSaidasProduto() {
    inicializarDaos();

    if (!validarDatas()) return;

    try {
        listaSaidasPorProduto = dao.pesquisarSaidasPorProduto(dataInicio, dataFim);

        if (listaSaidasPorProduto.isEmpty()) {
            addMensagem(FacesMessage.SEVERITY_INFO, "Sem resultados",
                    "Nenhuma saída encontrada para o período indicado.");
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Erro em pesquisarSaidasProduto", e);
        addMensagem(FacesMessage.SEVERITY_ERROR, "Erro",
                "Falha ao pesquisar saídas por produto.");
    }
}

public void carregarHistorico() {
    inicializarDaos();
    if (!validarDatas()) return;
    try {
        listaSaidas = dao.listarEntreDatasPorSector(
                saida.getSector().getIdSector(), dataInicio, dataFim);
        if (listaSaidas.isEmpty()) {
            addMensagem(FacesMessage.SEVERITY_INFO, "Sem resultados",
                    "Nenhum registo de saída encontrado para o período seleccionado.");
        }
    } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, "Erro em carregarHistorico", e);
        addMensagem(FacesMessage.SEVERITY_ERROR, "Erro",
                "Falha ao carregar histórico de saídas.");
    }
}

    public void limpar() {
        saida = new SaidaArmazem();
        saida.setSector(new Sector());
        saida.setIdProduto(new Produto());
        saida.setEntradaArmazem(new EntradaArmazem());
        saida.setDataSaidaArmazem(new Date());
        saida.setQuantidadeSaidaArmazem(0);
    }

    public void limparFiltros() {
        dataInicio              = null;
        dataFim                 = null;
        listaSaidasPorSector    = new ArrayList<>();
        listaSaidasPorProduto   = new ArrayList<>();
        listaDetalhadaPorSector = new ArrayList<>();
        listaSaidas             = new ArrayList<>();
        if (saida != null) saida.setSector(new Sector());
    }

   
    private void carregarDropdowns() throws Exception {
        listaSectores = sectorDAO.listarTudo();
        listaProdutos = produtoDAO.listarTudo();
    }

    private boolean validarDatas() {
        if (dataInicio == null || dataFim == null) {
            addMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
                    "Seleccione o intervalo de datas.");
            return false;
        }
        if (dataFim.before(dataInicio)) {
            addMensagem(FacesMessage.SEVERITY_WARN, "Atenção",
                    "A data de fim não pode ser anterior à data de início.");
            return false;
        }
        return true;
    }

    private void addMensagem(FacesMessage.Severity sev, String resumo, String detalhe) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(sev, resumo, detalhe));
    }

   
    public SaidaArmazem getSaida() { return saida; }
    public void setSaida(SaidaArmazem saida) { this.saida = saida; }

    public Date getDataInicio() { return dataInicio; }
    public void setDataInicio(Date dataInicio) { this.dataInicio = dataInicio; }

    public Date getDataFim() { return dataFim; }
    public void setDataFim(Date dataFim) { this.dataFim = dataFim; }

    public Date getHoje() { return hoje; }

    public List<SaidaArmazem> getListaSaidasPorSector() { return listaSaidasPorSector; }
    public void setListaSaidasPorSector(List<SaidaArmazem> l) { this.listaSaidasPorSector = l; }

    public List<EntradaArmazem> getListaSaidasPorProduto() { return listaSaidasPorProduto; }
    public void setListaSaidasPorProduto(List<EntradaArmazem> l) { this.listaSaidasPorProduto = l; }

    public List<SaidaArmazem> getListaDetalhadaPorSector() { return listaDetalhadaPorSector; }
    public void setListaDetalhadaPorSector(List<SaidaArmazem> l) { this.listaDetalhadaPorSector = l; }

    public List<SaidaArmazem> getListaSaidas() { return listaSaidas; }
    public void setListaSaidas(List<SaidaArmazem> l) { this.listaSaidas = l; }

    public List<Sector> getListaSectores() { return listaSectores; }
    public void setListaSectores(List<Sector> l) { this.listaSectores = l; }

    public List<Produto> getListaProdutos() { return listaProdutos; }
    public void setListaProdutos(List<Produto> l) { this.listaProdutos = l; }
}