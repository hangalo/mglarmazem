package skylinkmglarmazem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import skylink.mglarmazem.bdutil.ConnectionDB;
import skylink.armazem.modelo.SaidaArmazem;
import skylink.mglarmazem.modelo.EntradaArmazem;

/**
 * @author Henriques
 */
public class SaidaArmazemDAO {

    private static final Logger LOGGER = Logger.getLogger(SaidaArmazemDAO.class.getName());

    private static final String INSERT = "INSERT INTO saida_armazem (id_produto, id_sector, quantidade_saida_armazem, data_saida_armazem) VALUES (?, ?, ?, ?)";

    private static final String PESQUISAR_SAIDAS_PRODUTO = "SELECT p.descricao_produto AS descricao_prod, SUM(s.quantidade_saida_armazem) AS total_quantidade, SUM(s.quantidade_saida_armazem * e.preco_produto) AS total_valor FROM produto p INNER JOIN saida_armazem s ON p.id_produto = s.id_produto INNER JOIN entrada_armazem e ON p.id_produto = e.id_produto WHERE s.data_saida_armazem BETWEEN ? AND ? GROUP BY p.descricao_produto";
    private static final String LISTAR_TUDO = "SELECT s.id_saida_armazem, s.data_saida_armazem, s.id_sector, s.quantidade_saida_armazem, s.id_produto, p.descricao_produto, sec.descricao_sector, (SELECT e.preco_produto FROM entrada_armazem e WHERE e.id_produto = s.id_produto ORDER BY e.id_armazem DESC LIMIT 1) AS preco_produto FROM saida_armazem s INNER JOIN produto p ON s.id_produto = p.id_produto INNER JOIN sector sec ON s.id_sector = sec.id_sector ORDER BY s.data_saida_armazem DESC";

    public boolean registrarSaida(SaidaArmazem saida) {
        Connection conn = null;
        PreparedStatement psInsert = null;
        try {
            conn = ConnectionDB.getConnection();
            conn.setAutoCommit(false);
            psInsert = conn.prepareStatement(INSERT);
            psInsert.setInt(1, saida.getIdProduto());
            psInsert.setInt(2, saida.getIdSector());
            psInsert.setInt(3, saida.getQuantidadeSaidaArmazem());
            psInsert.setDate(4, new java.sql.Date(saida.getDataSaidaArmazem().getTime()));
            psInsert.executeUpdate();
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Erro ao fazer rollback", ex);
                }
            }
            LOGGER.log(Level.SEVERE, "Erro ao registrar saída: " + e.getMessage());
            return false;
        } finally {
            fecharRecursos(null, psInsert, conn);
        }
    }

    public List<EntradaArmazem> pesquisarSaidasPorProduto(java.util.Date inicio, java.util.Date fim) throws SQLException {
        List<EntradaArmazem> lista = new ArrayList<>();

        try (Connection conn = ConnectionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(PESQUISAR_SAIDAS_PRODUTO)) {

            ps.setTimestamp(1, new java.sql.Timestamp(inicio.getTime()));
            ps.setTimestamp(2, new java.sql.Timestamp(fim.getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EntradaArmazem saida = new EntradaArmazem(
                            rs.getString("descricao_prod"),
                            rs.getInt("total_quantidade"),
                            rs.getBigDecimal("total_valor")
                    );
                    lista.add(saida);
                }
            }
        }
        return lista;
    }

    public List<SaidaArmazem> listarTudo() throws SQLException {
        List<SaidaArmazem> lista = new ArrayList<>();
        try (Connection conn = ConnectionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(LISTAR_TUDO); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SaidaArmazem s = new SaidaArmazem();
                s.setIdSaidaArmazem(rs.getInt("id_saida_armazem"));
                s.setIdProduto(rs.getInt("id_produto"));
                s.setIdSector(rs.getInt("id_sector"));
                s.setQuantidadeSaidaArmazem(rs.getInt("quantidade_saida_armazem"));
                s.setDataSaidaArmazem(rs.getDate("data_saida_armazem"));
                skylink.armazem.modelo.Produto p = new skylink.armazem.modelo.Produto();
                p.setDescricaoProduto(rs.getString("descricao_produto"));
                s.getEntradaArmazem().setProduto(p);
                s.getEntradaArmazem().setPrecoProduto(rs.getDouble("preco_produto"));
                s.getSector().setDescricaoSector(rs.getString("descricao_sector"));
                lista.add(s);
            }
        }
        return lista;
    }

    private void fecharRecursos(PreparedStatement ps1, PreparedStatement ps2, Connection conn) {
        try {
            if (ps1 != null) {
                ps1.close();
            }
            if (ps2 != null) {
                ps2.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao fechar recursos", e);
        }
    }
}
