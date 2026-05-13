package skylink.mglarmazem.mb;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import skylinkmglarmazem.dao.ArmazemDAO;
import skylink.mglarmazem.modelo.Armazem;
import skylink.mglarmazem.modelo.Produto;
import skylinkmglarmazem.dao.ProdutoDAO;

/**
 * @author Henriques
 */
@Named("armazemBean")
@ViewScoped
public class ArmazemBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Armazem armazem;
    private Produto produto;
    private List<Armazem> listaArmazens;
    private List<Produto> listaProdutos;

    private Double totalPreco = 0.0;

    private final ArmazemDAO armazemDAO = new ArmazemDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private String descricaoProduto;

    private Date dataInicio;
    private Date dataFim;
    private Date hoje = new Date();

    @PostConstruct
    public void init() {
        novo();
        try {
            this.listaProdutos = produtoDAO.listarTudo();
        } catch (Exception e) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Não foi possível carregar os produtos.");
        }
    }

    public void calcularTotal() {
        this.totalPreco = 0.0;
        if (listaArmazens != null) {
            for (Armazem a : listaArmazens) {
                if (a.getPrecoProduto() != null) {

                    this.totalPreco += a.getPrecoProduto();
                }
            }
        }
    }

    public void salvar() {
        try {
            if (this.produto == null || this.produto.getIdProduto() == null || this.produto.getIdProduto() <= 0) {
                adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Selecione um produto válido.");
                return;
            }

            this.armazem.setIdProduto(this.produto.getIdProduto());

            if (armazemDAO.salvar(this.armazem)) {
                adicionarMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Entrada registada e stock atualizado!");
                novo();
            }
        } catch (Exception e) {
            adicionarMensagem(FacesMessage.SEVERITY_FATAL, "Erro", "Falha ao processar: " + e.getMessage());
        }
    }

    public void pesquisarPorDatas() {
        try {
            if (dataInicio == null || dataFim == null) {
                adicionarMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Selecione o intervalo de datas.");
                return;
            }
            listaArmazens = armazemDAO.pesquisarPorDatas(dataInicio, dataFim);

            calcularTotal();

        } catch (Exception e) {
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Erro na pesquisa.");
        }
    }

    public void novo() {
        this.armazem = new Armazem();
        this.armazem.setDataRegisto(new Date());
        this.produto = new Produto();
        this.totalPreco = 0.0;
    }

    private void adicionarMensagem(FacesMessage.Severity severidade, String resumo, String detalhe) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidade, resumo, detalhe));
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public Double getTotalPreco() {
        return totalPreco;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public void setTotalPreco(Double totalPreco) {
        this.totalPreco = totalPreco;
    }

    public Armazem getArmazem() {
        return armazem;
    }

    public void setArmazem(Armazem armazem) {
        this.armazem = armazem;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public List<Armazem> getListaArmazens() {
        return listaArmazens;
    }

    public void setListaArmazens(List<Armazem> listaArmazens) {
        this.listaArmazens = listaArmazens;
    }

    public List<Produto> getListaProdutos() {
        return listaProdutos;
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
