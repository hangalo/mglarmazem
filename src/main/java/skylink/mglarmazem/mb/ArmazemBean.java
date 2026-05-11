package skylink.mglarmazem.mb;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import skylinkmglarmazem.dao.ArmazemDAO;
import skylink.mglarmazem.modelo.Armazem;

@Named("armazem")
@ViewScoped
public class ArmazemBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Armazem armazem = new Armazem();
    private List<Armazem> listaArmazens = new ArrayList<>();
    private ArmazemDAO armazemDAO = new ArmazemDAO();
    
    private String categoriaFiltro;
    private Date dataInicio;
    private Date dataFim;
    private Integer idProdutoFiltro;
    private Date hoje = new Date();

    @PostConstruct
    public void init() {
        listaArmazens = new ArrayList<>();
    }


    public void carregarTudo() {
        listaArmazens = armazemDAO.listarTudo();
    }

    public void salvar() {
        try {
            validar();
            if (armazem.getDataRegisto() == null) {
                armazem.setDataRegisto(new Date());
            }
            armazemDAO.save(armazem);
            addMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Guardado com sucesso!");
            atualizarLista();
        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public void actualizar() {
        try {
            validar();
            armazemDAO.update(armazem);
            addMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Actualizado com sucesso!");
            atualizarLista();
        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage());
        }
    }

    public void eliminar() {
        try {
            armazemDAO.delete(armazem.getIdArmazem());
            addMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Eliminado com sucesso!");
            atualizarLista();
        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao eliminar!");
        }
    }


    public void pesquisarPorCategoria() {
        try {
            if (categoriaFiltro == null || categoriaFiltro.trim().isEmpty()) {
                addMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Seleccione ou digite uma categoria.");
                return;
            }
            listaArmazens = armazemDAO.listarPorCategoria(categoriaFiltro);
            if (listaArmazens.isEmpty()) {
                addMensagem(FacesMessage.SEVERITY_INFO, "Info", "Nenhum registo encontrado para esta categoria.");
            }
        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Falha na pesquisa por categoria!");
        }
    }

    public void pesquisarPorDatas() {
        try {
            if (dataInicio == null || dataFim == null) {
                addMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Seleccione as duas datas.");
                return;
            }
            listaArmazens = armazemDAO.pesquisarPorDatas(dataInicio, dataFim);
        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Falha na pesquisa por datas!");
        }
    }

    public void pesquisarPorProduto() {
        try {
            if (idProdutoFiltro == null || idProdutoFiltro <= 0) {
                addMensagem(FacesMessage.SEVERITY_WARN, "Atenção", "Informe um ID válido.");
                return;
            }
            listaArmazens = armazemDAO.pesquisarPorProduto(idProdutoFiltro);
        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Falha na pesquisa!");
        }
    }


    public void novo() {
        armazem = new Armazem();
    }

    public void limparFiltros() {
        this.dataInicio = null;
        this.dataFim = null;
        this.idProdutoFiltro = null;
        this.categoriaFiltro = null;
        this.listaArmazens = new ArrayList<>();
    }

    private void atualizarLista() {
        listaArmazens = armazemDAO.listarTudo();
        armazem = new Armazem();
    }

    private void validar() {
        if (armazem.getQuantidadeProduto() == null || armazem.getQuantidadeProduto() <= 0) 
            throw new RuntimeException("Quantidade inválida.");
        if (armazem.getPrecoProduto() == null || armazem.getPrecoProduto() <= 0) 
            throw new RuntimeException("Preço inválido.");
        if (armazem.getIdProduto() == null || armazem.getIdProduto() <= 0) 
            throw new RuntimeException("ID do produto inválido.");
    }

    private void addMensagem(FacesMessage.Severity s, String t, String d) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(s, t, d));
    }


    public Armazem getArmazem() { return armazem; }
    public void setArmazem(Armazem armazem) { this.armazem = armazem; }
    public List<Armazem> getListaArmazens() { return listaArmazens; }
    public void setListaArmazens(List<Armazem> listaArmazens) { this.listaArmazens = listaArmazens; }
    public String getCategoriaFiltro() { return categoriaFiltro; }
    public void setCategoriaFiltro(String categoriaFiltro) { this.categoriaFiltro = categoriaFiltro; }
    public Date getDataInicio() { return dataInicio; }
    public void setDataInicio(Date dataInicio) { this.dataInicio = dataInicio; }
    public Date getDataFim() { return dataFim; }
    public void setDataFim(Date dataFim) { this.dataFim = dataFim; }
    public Integer getIdProdutoFiltro() { return idProdutoFiltro; }
    public void setIdProdutoFiltro(Integer idProdutoFiltro) { this.idProdutoFiltro = idProdutoFiltro; }
    public Date getHoje() { return hoje; }
    
    public double getTotalPreco() {
        double total = 0;
        for (Armazem a : listaArmazens) {
            if (a.getPrecoProduto() != null) total += a.getPrecoProduto();
        }
        return total;
    }
}