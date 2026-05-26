package skylinkmglarmazem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import skylink.armazem.modelo.Estoque;
import skylink.armazem.modelo.Produto;
import skylink.armazem.modelo.Sector;
import skylink.mglarmazem.bdutil.ConnectionDB;

/**
 *
 * @Henriques
 */
public class EstoqueDAO {

    private static final String CONTROLE_ESTOQUE = "SELECT sec.descricao_sector, p.descricao_produto, p.quantidade_existente, SUM(s.quantidade_saida_armazem) AS total_saida, sum(e.quantidade_produto) AS total_entrada FROM produto p INNER JOIN saida_armazem s ON s.id_produto = p.id_produto inner join entrada_armazem e ON p.id_produto=e.id_produto INNER JOIN sector sec ON s.id_sector = sec.id_sector where sec.id_sector=? AND s.data_saida_armazem between ? and ? GROUP BY sec.descricao_sector, p.descricao_produto, p.quantidade_existente";

    public List<Estoque> obterControleEstoquePorSetor(int idSector, java.util.Date dataInicio, java.util.Date dataFim) throws SQLException {
        List<Estoque> lista = new ArrayList<>();

        try (Connection conn = ConnectionDB.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(CONTROLE_ESTOQUE)) {

            ps.setInt(1, idSector);
            ps.setTimestamp(2, new java.sql.Timestamp(dataInicio.getTime()));
            ps.setTimestamp(3, new java.sql.Timestamp(dataFim.getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sector sec = new Sector();
                    sec.setDescricaoSector(rs.getString("descricao_sector"));
                    
                    Produto prod = new Produto();
                    prod.setDescricaoProduto(rs.getString("descricao_produto"));
                    
                    Estoque item = new Estoque(
                        sec,
                        prod,
                        rs.getInt("quantidade_existente"),
                        rs.getInt("total_saida"),
                        rs.getInt("total_entrada")
                    );
                    
                    lista.add(item);
                }
            }
        }
        return lista;
    }
}