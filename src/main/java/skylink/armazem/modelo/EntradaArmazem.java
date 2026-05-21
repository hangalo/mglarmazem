package skylink.mglarmazem.modelo;

import skylink.armazem.modelo.Produto;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @author Henriques
 */
public class EntradaArmazem {
    
    private Integer idArmazem;
    private Date dataRegisto;
    private Double precoProduto; // Mantido como Double para não quebrar o seu método salvar antigo, se preferir
    private Date dataCompra;
    private Integer quantidadeProduto;
    private Produto produto;
    
    // Atributo extra ou reutilizado para armazenar o valor total calculado do relatório agrupado
    private BigDecimal totalValorRelatorio;

    // Construtor padrão (Vazio) - CRUCIAL para o JSF / PrimeFaces instanciar no Bean
    public EntradaArmazem() {
    }

    // CORRIGIDO: Construtor específico para a pesquisa agrupada por produto (Relatório)
    public EntradaArmazem(String descricaoProd, int totalQuantidade, BigDecimal totalValor) {
        this.quantidadeProduto = totalQuantidade;
        this.totalValorRelatorio = totalValor;
        
        // Inicializa o objeto produto com a descrição para evitar NullPointerException na View
        this.produto = new Produto();
        this.produto.setDescricaoProduto(descricaoProd);
    }

    // Construtor completo para persistência/listagem normal
    public EntradaArmazem(Integer idArmazem, Date dataRegisto, Double precoProduto, Date dataCompra, Integer quantidadeProduto, Produto produto) {
        this.idArmazem = idArmazem;
        this.dataRegisto = dataRegisto;
        this.precoProduto = precoProduto;
        this.dataCompra = dataCompra;
        this.quantidadeProduto = quantidadeProduto;
        this.produto = produto;
    }

    // --- GETTERS E SETTERS ---
    public Integer getIdArmazem() {
        return idArmazem;
    }

    public void setIdArmazem(Integer idArmazem) {
        this.idArmazem = idArmazem;
    }

    public Date getDataRegisto() {
        return dataRegisto;
    }

    public void setDataRegisto(Date dataRegisto) {
        this.dataRegisto = dataRegisto;
    }

    public Double getPrecoProduto() {
        return precoProduto;
    }

    public void setPrecoProduto(Double precoProduto) {
        this.precoProduto = precoProduto;
    }

    public Date getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(Date dataCompra) {
        this.dataCompra = dataCompra;
    }

    public Integer getQuantidadeProduto() {
        return quantidadeProduto;
    }

    public void setQuantidadeProduto(Integer quantidadeProduto) {
        this.quantidadeProduto = quantidadeProduto;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public BigDecimal getTotalValorRelatorio() {
        return totalValorRelatorio;
    }

    public void setTotalValorRelatorio(BigDecimal totalValorRelatorio) {
        this.totalValorRelatorio = totalValorRelatorio;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.idArmazem);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EntradaArmazem other = (EntradaArmazem) obj;
        return Objects.equals(this.idArmazem, other.idArmazem);
    }

    @Override
    public String toString() {
        return "EntradaArmazem{" + "idArmazem=" + idArmazem + '}';
    }  
}
