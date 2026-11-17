package skylink.mglarmazem.mb;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import skylink.armazem.modelo.Produto;
import skylink.mglarmazem.modelo.EntradaArmazem;
import skylinkmglarmazem.dao.EntradaArmazemDAO;
import skylinkmglarmazem.dao.EntradaArmazemDAO.ResumoProduto;
import skylinkmglarmazem.dao.ProdutoDAO;

/**
 * @author Henriques
 */
@Named("entradaArmazemBean")
@ViewScoped
public class EntradaArmazemBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(EntradaArmazemBean.class.getName());

    private EntradaArmazem armazem;
    private Produto produto;
    private List<EntradaArmazem> listaArmazens = new ArrayList<>();
    private List<ResumoProduto> listaResumo = new ArrayList<>();
    private List<Produto> listaProdutos = new ArrayList<>();

    private BigDecimal totalPreco = BigDecimal.ZERO;
    private Integer totalQuantidade = 0;

    private final EntradaArmazemDAO armazemDAO = new EntradaArmazemDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private String descricaoProduto;

    private Date dataInicio;
    private Date dataFim;
    private final Date hoje = new Date();

    @PostConstruct
    public void init() {
        novo();
        try {
            this.listaProdutos = produtoDAO.listarTudo();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erro ao carregar produtos", ex);
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Não foi possível carregar a lista de produtos.");
        }
    }

    public void calcularTotal() {
        this.totalPreco = BigDecimal.ZERO;
        this.totalQuantidade = 0;

        if (listaArmazens != null) {
            for (EntradaArmazem a : listaArmazens) {
                if (a.getQuantidadeProduto() != null) {
                    this.totalQuantidade += a.getQuantidadeProduto();
                    if (a.getPrecoProduto() != null) {
                        BigDecimal preco = BigDecimal.valueOf(a.getPrecoProduto());
                        BigDecimal qtd = BigDecimal.valueOf(a.getQuantidadeProduto());
                        this.totalPreco = this.totalPreco.add(preco.multiply(qtd));
                    }
                }
            }
        }
    }

    private void calcularTotalResumo() {
        this.totalPreco = BigDecimal.ZERO;
        this.totalQuantidade = 0;
        for (ResumoProduto r : listaResumo) {
            this.totalQuantidade += r.getTotalQuantidade();
            if (r.getTotalValor() != null) {
                this.totalPreco = this.totalPreco.add(r.getTotalValor());
            }
        }
    }

    public String salvar() {
        if (!validarEntrada()) {
            return null;
        }

        if (armazemDAO.salvar(armazem)) {
            adicionarMensagem(FacesMessage.SEVERITY_INFO, "Guardar", "Dados guardados com sucesso");
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            novo();
            return "/entrada_armazem/registar_entrada?faces-redirect=true";
        } else {
            adicionarMensagem(FacesMessage.SEVERITY_WARN, "Erro", "Erro ao guardar dados");
            return null;
        }
    }

    public void pesquisarPorDatas() {
        if (!validarDatas()) {
            return;
        }
        listaArmazens = armazemDAO.pesquisarPorDatas(dataInicio, dataFim);
        calcularTotal();
    }

    public void pesquisarProduto() {
        if (!validarDatas()) {
            return;
        }
        listaResumo = armazemDAO.pesquisarRelatorioPorProduto(dataInicio, dataFim);
        calcularTotalResumo();
    }

    public void limparFiltros() {
        this.dataInicio = null;
        this.dataFim = null;
        this.listaArmazens = new ArrayList<>();
        this.listaResumo = new ArrayList<>();
        this.totalPreco = BigDecimal.ZERO;
        this.totalQuantidade = 0;
    }

    public void novo() {
        this.armazem = new EntradaArmazem();
        this.armazem.setDataRegisto(new Date());
        this.armazem.setProduto(new Produto());
        this.produto = new Produto();
        this.totalPreco = BigDecimal.ZERO;
        this.totalQuantidade = 0;
    }

    private boolean validarEntrada() {
        if (armazem.getProduto() == null || armazem.getProduto().getIdProduto() <= 0) {
            adicionarMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Selecione um produto.");
            return false;
        }
        if (armazem.getQuantidadeProduto() == null || armazem.getQuantidadeProduto() <= 0) {
            adicionarMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "A quantidade deve ser maior que zero.");
            return false;
        }
        if (armazem.getPrecoProduto() == null || armazem.getPrecoProduto() < 0) {
            adicionarMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Informe um preço válido.");
            return false;
        }
        if (armazem.getDataCompra() == null) {
            adicionarMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Informe a data da compra.");
            return false;
        }
        return true;
    }

    private boolean validarDatas() {
        if (dataInicio == null || dataFim == null) {
            adicionarMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Selecione o intervalo de datas.");
            return false;
        }
        if (dataInicio.after(dataFim)) {
            adicionarMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "A data inicial não pode ser posterior à data final.");
            return false;
        }
        return true;
    }

    private void adicionarMensagem(FacesMessage.Severity severidade, String resumo, String detalhe) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidade, resumo, detalhe));
    }


    public String getDescricaoProduto() { return descricaoProduto; }
    public void setDescricaoProduto(String descricaoProduto) { this.descricaoProduto = descricaoProduto; }

    public BigDecimal getTotalPreco() { return totalPreco; }
    public void setTotalPreco(BigDecimal totalPreco) { this.totalPreco = totalPreco; }

    public Integer getTotalQuantidade() { return totalQuantidade; }
    public void setTotalQuantidade(Integer totalQuantidade) { this.totalQuantidade = totalQuantidade; }

    public EntradaArmazem getArmazem() { return armazem; }
    public void setArmazem(EntradaArmazem armazem) { this.armazem = armazem; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public List<EntradaArmazem> getListaArmazens() { return listaArmazens; }
    public void setListaArmazens(List<EntradaArmazem> listaArmazens) { this.listaArmazens = listaArmazens; }

    public List<ResumoProduto> getListaResumo() { return listaResumo; }
    public void setListaResumo(List<ResumoProduto> listaResumo) { this.listaResumo = listaResumo; }

    public List<Produto> getListaProdutos() { return listaProdutos; }
    public void setListaProdutos(List<Produto> listaProdutos) { this.listaProdutos = listaProdutos; }

    public Date getDataInicio() { return dataInicio; }
    public void setDataInicio(Date dataInicio) { this.dataInicio = dataInicio; }

    public Date getDataFim() { return dataFim; }
    public void setDataFim(Date dataFim) { this.dataFim = dataFim; }

    public Date getHoje() { return hoje; }
}