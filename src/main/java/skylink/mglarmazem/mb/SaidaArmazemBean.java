package skylink.mglarmazem.mb;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Date;
import skylink.mglarmazem.modelo.SaidaArmazem;
import skylinkmglarmazem.dao.SaidaArmazemDAO;

/** 
 * @author Henriques
 */
@Named("saidaArmazemBean")
@ViewScoped
public class SaidaArmazemBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private SaidaArmazem saida;
    private final SaidaArmazemDAO dao = new SaidaArmazemDAO();

    @PostConstruct
    public void init() {
        limpar();
    }

    
    public void limpar() {
        saida = new SaidaArmazem();
        saida.setDataSaidaArmazem(new Date()); 
    }

    
    public void registrar() {
        try {
            
            if (saida.getIdProduto() <= 0 || saida.getIdSector() <= 0) {
                adicionarMensagem(FacesMessage.SEVERITY_WARN, "Aviso", "Selecione um produto e um sector válido.");
                return;
            }

            if (saida.getQuantidadeSaidaArmazem() <= 0) {
                adicionarMensagem(FacesMessage.SEVERITY_WARN, "Aviso", "A quantidade deve ser maior que zero.");
                return;
            }

            
            boolean sucesso = dao.registrarSaida(saida);

            if (sucesso) {
                adicionarMensagem(FacesMessage.SEVERITY_INFO, "Sucesso", "Saída registada e stock atualizado!");
                limpar(); 
            } else {
                adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro", "Não foi possível processar a operação. Verifique os dados.");
            }

        } catch (Exception e) {
            
            adicionarMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao processar", e.getMessage());
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