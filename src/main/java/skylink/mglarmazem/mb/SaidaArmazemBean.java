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
import skylink.armazem.modelo.SaidaArmazem;
import skylink.mglarmazem.modelo.EntradaArmazem; 
import skylinkmglarmazem.dao.ProdutoDAO;
import skylinkmglarmazem.dao.SaidaArmazemDAO;

/**
 * @author Henriques
 */
@Named("saidaArmazemBean")
@ViewScoped
public class SaidaArmazemBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private SaidaArmazem saida;
    private List<SaidaArmazem> listaSaidas; 
    private List<EntradaArmazem> listaSaidasPorProduto; 

    private Date dataInicio;
    private Date dataFim;
    private Date hoje = new Date();

    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final SaidaArmazemDAO dao = new SaidaArmazemDAO();

    @PostConstruct
    public void init() {
        limpar();
        carregarHistoricoUnsafe();
    }

    private void carregarHistoricoUnsafe() {
        try {
            carregarHistorico();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void limpar() {
        this.saida = new SaidaArmazem();
        this.saida.setDataSaidaArmazem(new Date()); 
    }

    public void limparFiltros() {
        this.dataInicio = null;
        this.dataFim = null;
        if (this.listaSaidasPorProduto != null) {
            this.listaSaidasPorProduto.clear();
        }
    }
    
    public void carregarHistorico() throws SQLException {
        this.listaSaidas = dao.listarTudo();
    }

    public void pesquisarSaidasProduto() throws SQLException {
        if (dataInicio == null || dataFim == null) {
            adicionarMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Seleccione o intervalo de datas.");
            return;
        }
        this.listaSaidasPorProduto = dao.pesquisarSaidasPorProduto(dataInicio, dataFim);
    }

    public String registrar() {
        if (dao.registrarSaida(saida)) {
            produtoDAO.updateDiminuirQuantidade(saida.getQuantidadeSaidaArmazem(), saida.getIdProduto());
            
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Dados guardados com sucesso");
            
            return "saida_armazem?faces-redirect=true";       
        } else {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao guardar dados. Verifique os limites de estoque.");
            return null;
        }
    }

    public Double calcularValorTotal(SaidaArmazem s) {
        if (s == null || s.getQuantidadeSaidaArmazem() == null || s.getEntradaArmazem() == null) {
            return 0.0;
        }
        Double preco = s.getEntradaArmazem().getPrecoProduto(); 
        return s.getQuantidadeSaidaArmazem() * (preco != null ? preco : 0.0);
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

    public Date getDataInicio() { return dataInicio; }
    public void setDataInicio(Date dataInicio) { this.dataInicio = dataInicio; }

    public Date getDataFim() { return dataFim; }
    public void setDataFim(Date dataFim) { this.dataFim = dataFim; }

    public Date getHoje() { return hoje; }
    public void setHoje(Date hoje) { this.hoje = hoje; }
}