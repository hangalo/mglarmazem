package skylink.mglarmazem.mb;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import skylinkmglarmazem.dao.ArmazemDAO;
import skylink.mglarmazem.modelo.Armazem;
/**
 *
 * @Henriques
 */

@Named("armazem")
@ViewScoped
public class ArmazemBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Armazem armazem;
    private List<Armazem> listaArmazem;

    @Inject
    private ArmazemDAO armazemDAO;

    @PostConstruct
    public void init() {
        armazem = new Armazem();

        try {
            listaArmazem = armazemDAO.listarTudo();
        } catch (Exception e) {
            listaArmazem = new ArrayList<>();
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao carregar dados!");
            e.printStackTrace(); 
        }
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
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao salvar!");
            e.printStackTrace();
        }
    }

    public void actualizar() {
        try {
            armazemDAO.update(armazem);

            addMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Atualizado!");
            atualizarLista();

        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao atualizar!");
        }
    }

    public void eliminar() {
        try {
            armazemDAO.delete(armazem.getIdArmazem());

            addMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Eliminado!");
            atualizarLista();

        } catch (Exception e) {
            addMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao eliminar!");
        }
    }

    public void seleccionar(Armazem a) {
        this.armazem = a;
    }

    public void novo() {
        armazem = new Armazem();
    }

    private void atualizarLista() {
        listaArmazem = armazemDAO.listarTudo();
        armazem = new Armazem();
    }

    private void validar() {
        if (armazem.getQuantidadeProduto() <= 0)
            throw new RuntimeException("Quantidade inválida");

        if (armazem.getPrecoProduto() <= 0)
            throw new RuntimeException("Preço inválido");
    }

    private void addMensagem(FacesMessage.Severity s, String t, String d) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(s, t, d));
    }

    public List<Armazem> getListaArmazem() { return listaArmazem; }
    public Armazem getArmazem() { return armazem; }
    public void setArmazem(Armazem armazem) { this.armazem = armazem; }
}