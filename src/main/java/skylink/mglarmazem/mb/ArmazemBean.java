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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    public void eliminar() {
        try {
            armazemDAO.delete(armazem.getIdArmazem());
            addMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Eliminado com sucesso!");
            atualizarLista();
        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao eliminar!");
            e.printStackTrace();
        }
    }

    public void pesquisarPorDatas() {
        try {
            if (dataInicio == null || dataFim == null) {
                addMensagem(FacesMessage.SEVERITY_WARN,
                        "Atenção", "Seleccione as duas datas.");
                return;
            }
            if (dataInicio.after(dataFim)) {
                addMensagem(FacesMessage.SEVERITY_WARN,
                        "Atenção", "Data início não pode ser maior que data fim.");
                return;
            }
            listaArmazens = armazemDAO.pesquisarPorDatas(dataInicio, dataFim);
            if (listaArmazens.isEmpty()) {
                addMensagem(FacesMessage.SEVERITY_INFO,
                        "Info", "Nenhum registo encontrado no período.");
            }
        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Falha na pesquisa!");
            e.printStackTrace();
        }
    }

    public void pesquisarPorProduto() {
        try {
            if (idProdutoFiltro == null || idProdutoFiltro <= 0) {
                addMensagem(FacesMessage.SEVERITY_WARN,
                        "Atenção", "Informe um ID de produto válido.");
                return;
            }
            listaArmazens = armazemDAO.pesquisarPorProduto(idProdutoFiltro);
            if (listaArmazens.isEmpty()) {
                addMensagem(FacesMessage.SEVERITY_INFO,
                        "Info", "Nenhum registo encontrado para este produto.");
            }
        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Falha na pesquisa!");
            e.printStackTrace();
        }
    }

    public void seleccionar(Armazem a) {
        this.armazem = a;
    }

    public void novo() {
        armazem = new Armazem();
    }

    public void limparFiltros() {
        dataInicio = null;
        dataFim = null;
        idProdutoFiltro = null;
        listaArmazens = new ArrayList<>();
    }

    private void atualizarLista() {
        listaArmazens = armazemDAO.listarTudo();
        armazem = new Armazem();
    }

    private void validar() {
        if (armazem.getQuantidadeProduto() == null || armazem.getQuantidadeProduto() <= 0) {
            throw new RuntimeException("Quantidade inválida. Deve ser maior que zero.");
        }
        if (armazem.getPrecoProduto() == null || armazem.getPrecoProduto() <= 0) {
            throw new RuntimeException("Preço inválido. Deve ser maior que zero.");
        }
        if (armazem.getIdProduto() == null || armazem.getIdProduto() <= 0) {
            throw new RuntimeException("ID do produto inválido.");
        }
    }

    private void addMensagem(FacesMessage.Severity s, String t, String d) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(s, t, d));
    }

    public Armazem getArmazem() {
        return armazem;
    }

    public void setArmazem(Armazem armazem) {
        this.armazem = armazem;
    }

    public List<Armazem> getListaArmazens() {
        return listaArmazens;
    }

    public void setListaArmazens(List<Armazem> listaArmazens) {
        this.listaArmazens = listaArmazens;
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

    public Integer getIdProdutoFiltro() {
        return idProdutoFiltro;
    }

    public void setIdProdutoFiltro(Integer idProdutoFiltro) {
        this.idProdutoFiltro = idProdutoFiltro;
    }

    public double getTotalPreco() {
        return listaArmazens.stream()
                .mapToDouble(a -> a.getPrecoProduto() != null ? a.getPrecoProduto() : 0)
                .sum();
    }

    public Date getHoje() {
        return hoje;
    }

}
