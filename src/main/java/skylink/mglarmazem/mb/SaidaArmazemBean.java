package skylink.mglarmazem.mb;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import skylink.armazem.modelo.SaidaArmazem;
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
     private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final SaidaArmazemDAO dao = new SaidaArmazemDAO();

    @PostConstruct
    public void init() {
        limpar();
    }

    
    public void limpar() {
        saida = new SaidaArmazem();
        saida.setDataSaidaArmazem(new Date()); 
    }

    
public String registrar() {
    if (dao.registrarSaida(saida)) {
        produtoDAO.updateDiminuirQuantidade(saida.getQuantidadeSaidaArmazem(), saida.getIdProduto());
        saida = new SaidaArmazem();        
        adicionarMensagem(FacesMessage.SEVERITY_WARN, "Guardar", "Dados guardados com sucesso");
        return "saida_armazem?faces-redirect=true";       
    } else {
        adicionarMensagem(FacesMessage.SEVERITY_WARN, "Erro", "erro ao guardar dados");
        return null;
    }
}
   
    private void adicionarMensagem(FacesMessage.Severity severidade, String resumo, String detalhe) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severidade, resumo, detalhe));
    }

    public SaidaArmazem getSaida() {
        return saida;
    }

    public void setSaida(SaidaArmazem saida) {
        this.saida = saida;
    }
}