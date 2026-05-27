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
 * @author Henriques
 */
public class EstoqueDAO {

    // QUERY DEFINITIVA: Filtra o setor dentro da subconsulta de saídas e faz CROSS JOIN com setores
    private static final String CONTROLE_ESTOQUE = "SELECT sec.descricao_sector, p.descricao_produto, p.quantidade_existente, COALESCE(ent.total_entrada, 0) AS total_entrada, COALESCE(sai.total_saida, 0) AS total_saida FROM produto p LEFT JOIN (SELECT id_produto, SUM(quantidade_saida_armazem) AS total_saida FROM saida_armazem WHERE id_sector = ? AND data_saida_armazem BETWEEN ? AND ? GROUP BY id_produto) sai ON p.id_produto = sai.id_produto LEFT JOIN (SELECT id_produto, SUM(quantidade_produto) AS total_entrada FROM entrada_armazem WHERE data_registo BETWEEN ? AND ? GROUP BY id_produto) ent ON p.id_produto = ent.id_produto CROSS JOIN sector sec WHERE sec.id_sector = ?";

    public List<Estoque> obterControleEstoquePorSetor(int idSector, java.util.Date dataInicio, java.util.Date dataFim) throws SQLException {
        List<Estoque> lista = new ArrayList<>();

        try (Connection conn = ConnectionDB.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(CONTROLE_ESTOQUE)) {

            // 1º ? -> ID do Setor (Filtro interno de Saídas)
            ps.setInt(1, idSector);

            // 2º e 3º ? -> Período da tabela de Saídas (sai)
            ps.setTimestamp(2, new java.sql.Timestamp(dataInicio.getTime()));
            ps.setTimestamp(3, new java.sql.Timestamp(dataFim.getTime()));

            // 4º e 5º ? -> Período da tabela de Entradas (ent)
            ps.setTimestamp(4, new java.sql.Timestamp(dataInicio.getTime()));
            ps.setTimestamp(5, new java.sql.Timestamp(dataFim.getTime()));

            // 6º ? -> ID do Setor no WHERE final (sec.id_sector = ?)
            ps.setInt(6, idSector);

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