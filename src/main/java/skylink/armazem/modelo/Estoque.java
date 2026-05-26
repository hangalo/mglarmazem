package skylink.armazem.modelo;

import java.io.Serializable;
import java.util.Objects;

public class Estoque implements Serializable {

    private static final long serialVersionUID = 1L;

    private Sector descricaoSector;
    private Produto descricaoProduto;
    private int quantidadeExistente;
    private int totalSaidas;
    private int totalEntradas;

    public Estoque(Sector descricaoSector, Produto descricaoProduto, int quantidadeExistente, int totalSaidas, int totalEntradas) {
        this.descricaoSector = descricaoSector;
        this.descricaoProduto = descricaoProduto;
        this.quantidadeExistente = quantidadeExistente;
        this.totalSaidas = totalSaidas;
        this.totalEntradas = totalEntradas;
    }

    public Sector getDescricaoSector() {
        return descricaoSector;
    }

    public void setDescricaoSector(Sector descricaoSector) {
        this.descricaoSector = descricaoSector;
    }

    public Produto getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(Produto descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public int getQuantidadeExistente() {
        return quantidadeExistente;
    }

    public void setQuantidadeExistente(int quantidadeExistente) {
        this.quantidadeExistente = quantidadeExistente;
    }

    public int getTotalSaidas() {
        return totalSaidas;
    }

    public void setTotalSaidas(int totalSaidas) {
        this.totalSaidas = totalSaidas;
    }

    public int getTotalEntradas() {
        return totalEntradas;
    }

    public void setTotalEntradas(int totalEntradas) {
        this.totalEntradas = totalEntradas;
    }

}
