package skylink.mglarmazem.mb;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import skylink.armazem.modelo.Produto;
import skylink.mglarmazem.modelo.EntradaArmazem;
import skylinkmglarmazem.dao.EntradaArmazemDAO;
import skylinkmglarmazem.dao.ProdutoDAO;

/**
 * @author Henriques
 */
@Named("entradaArmazemBean")
@ViewScoped
public class EntradaArmazemBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private EntradaArmazem armazem;
    private Produto produto;
    private List<EntradaArmazem> listaArmazens;
    private List<Produto> listaProdutos;

    private BigDecimal totalPreco = BigDecimal.ZERO;
    private Integer totalQuantidade = 0;

    private final EntradaArmazemDAO armazemDAO = new EntradaArmazemDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private String descricaoProduto;

    private Date dataInicio;
    private Date dataFim;
    private Date hoje = new Date();

    @PostConstruct
    public void init() {
        novo();
        this.listaProdutos = produtoDAO.listarTudo();
    }

    public void calcularTotal() {
        this.totalPreco = BigDecimal.ZERO;
        this.totalQuantidade = 0;

        if (listaArmazens != null) {
            for (EntradaArmazem a : listaArmazens) {
                if (a.getQuantidadeProduto() != null) {
                    this.totalQuantidade += a.getQuantidadeProduto();
                }

                if (a.getQuantidadeProduto() != null && a.getPrecoProduto() != null) {
                    BigDecimal preco = BigDecimal.valueOf(a.getPrecoProduto());
                    BigDecimal qtd = BigDecimal.valueOf(a.getQuantidadeProduto());
                    this.totalPreco = this.totalPreco.add(preco.multiply(qtd));
                }
            }
        }
    }

    public String salvar() {
        if (armazemDAO.salvar(armazem)) {
            produtoDAO.updateAumentarQuantidade(armazem.getQuantidadeProduto(), armazem.getProduto().getIdProduto());
            armazem = new EntradaArmazem();
            adicionarMensagem(FacesMessage.SEVERITY_WARN, "Guardar", "Dados guardados com sucesso");
            return "/entrada_armazem/registar_entrada?faces-redirect=true";
        } else {
            adicionarMensagem(FacesMessage.SEVERITY_WARN, "Erro", "erro ao guardar dados");
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

    public void pesquisarProduto() throws SQLException {
        if (!validarDatas()) {
            return;
        }
        listaArmazens = armazemDAO.pesquisarProduto(dataInicio, dataFim);
        calcularTotal();
    }

    public void limparFiltros() {
        this.dataInicio = null;
        this.dataFim = null;
        if (listaArmazens != null) {
            listaArmazens.clear();
        }
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

    private boolean validarDatas() {
        if (dataInicio == null || dataFim == null) {
            adicionarMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Selecione o intervalo de datas.");
            return false;
        }
        return true;
    }

    private void adicionarMensagem(FacesMessage.Severity severidade, String resumo, String detalhe) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidade, resumo, detalhe));
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public BigDecimal getTotalPreco() {
        return totalPreco;
    }

    public void setTotalPreco(BigDecimal totalPreco) {
        this.totalPreco = totalPreco;
    }

    public Integer getTotalQuantidade() {
        return totalQuantidade;
    }

    public void setTotalQuantidade(Integer totalQuantidade) {
        this.totalQuantidade = totalQuantidade;
    }

    public EntradaArmazem getArmazem() {
        return armazem;
    }

    public void setArmazem(EntradaArmazem armazem) {
        this.armazem = armazem;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public List<EntradaArmazem> getListaArmazens() {
        return listaArmazens;
    }

    public void setListaArmazens(List<EntradaArmazem> listaArmazens) {
        this.listaArmazens = listaArmazens;
    }

    public List<Produto> getListaProdutos() {
        return listaProdutos;
    }

    public void setListaProdutos(List<Produto> listaProdutos) {
        this.listaProdutos = listaProdutos;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Date getHoje() {
        return hoje;
    }
}
