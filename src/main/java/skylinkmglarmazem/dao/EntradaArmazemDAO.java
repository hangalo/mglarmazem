package skylinkmglarmazem.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import skylink.mglarmazem.bdutil.ConnectionDB;
import skylink.mglarmazem.modelo.EntradaArmazem;
import skylink.armazem.modelo.Produto;

/**
 * @author Henriques
 */
public class EntradaArmazemDAO {

    private static final Logger LOGGER = Logger.getLogger(EntradaArmazemDAO.class.getName());

    private static final String INSERT = "INSERT INTO entrada_armazem(preco_produto, data_compra, quantidade_produto, id_produto, unidade_medida) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE entrada_armazem SET preco_produto=?, data_compra=?, quantidade_produto=?, id_produto=?, unidade_medida=? WHERE id_armazem=?";
    private static final String DELETE = "DELETE FROM entrada_armazem WHERE id_armazem=?";
    private static final String SELECT_ANTIGO = "SELECT id_produto, quantidade_produto FROM entrada_armazem WHERE id_armazem=? FOR UPDATE";

    private static final String AJUSTAR_ESTOQUE = "UPDATE produto SET quantidade_existente = quantidade_existente + ? WHERE id_produto = ? AND quantidade_existente + ? >= 0";

    private static final String LISTAR = "SELECT a.*, p.id_produto AS id_produto_prod, p.descricao_produto AS descricao_prod FROM entrada_armazem a INNER JOIN produto p ON a.id_produto = p.id_produto ORDER BY a.data_registo DESC";
    private static final String POR_DATAS = "SELECT a.*, p.id_produto AS id_produto_prod, p.descricao_produto AS descricao_prod FROM entrada_armazem a INNER JOIN produto p ON a.id_produto = p.id_produto WHERE a.data_compra BETWEEN ? AND ? ORDER BY a.data_compra DESC";
    private static final String LISTAR_POR_PRODUTO = "SELECT p.id_produto, p.descricao_produto AS descricao_prod, SUM(e.quantidade_produto) AS total_quantidade, SUM(e.quantidade_produto * e.preco_produto) AS total_valor FROM produto p INNER JOIN entrada_armazem e ON p.id_produto = e.id_produto WHERE e.data_compra BETWEEN ? AND ? GROUP BY p.id_produto, p.descricao_produto ORDER BY p.descricao_produto";

    public boolean salvar(EntradaArmazem e) {
        try (Connection conn = ConnectionDB.getConnection()) {
            conn.setAutoCommit(false);
            try {
                preencherInsertOuUpdate(conn, INSERT, e, false);
                ajustarEstoque(conn, e.getProduto().getIdProduto(), e.getQuantidadeProduto());
                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Erro ao inserir entrada e atualizar estoque. Transação revertida.", ex);
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erro de conexão ao salvar entrada.", ex);
            return false;
        }
    }

    public boolean atualizar(EntradaArmazem e) {
        try (Connection conn = ConnectionDB.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int idAntigo;
                int qtdAntiga;
                try (PreparedStatement ps = conn.prepareStatement(SELECT_ANTIGO)) {
                    ps.setInt(1, e.getIdArmazem());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                        idAntigo = rs.getInt("id_produto");
                        qtdAntiga = rs.getInt("quantidade_produto");
                    }
                }

                ajustarEstoque(conn, idAntigo, -qtdAntiga);
                ajustarEstoque(conn, e.getProduto().getIdProduto(), e.getQuantidadeProduto());

                boolean ok = preencherInsertOuUpdate(conn, UPDATE, e, true) > 0;
                if (!ok) {
                    conn.rollback();
                    return false;
                }
                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Erro ao atualizar entrada. Transação revertida.", ex);
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erro de conexão ao atualizar entrada.", ex);
            return false;
        }
    }

    public boolean deletar(int idArmazem) {
        try (Connection conn = ConnectionDB.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int idProduto;
                int qtd;
                try (PreparedStatement ps = conn.prepareStatement(SELECT_ANTIGO)) {
                    ps.setInt(1, idArmazem);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                        idProduto = rs.getInt("id_produto");
                        qtd = rs.getInt("quantidade_produto");
                    }
                }

                ajustarEstoque(conn, idProduto, -qtd);

                try (PreparedStatement ps = conn.prepareStatement(DELETE)) {
                    ps.setInt(1, idArmazem);
                    if (ps.executeUpdate() == 0) {
                        conn.rollback();
                        return false;
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                LOGGER.log(Level.SEVERE, "Erro ao deletar entrada. Transação revertida.", ex);
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Erro de conexão ao deletar entrada.", ex);
            return false;
        }
    }

    public List<EntradaArmazem> listarTudo() {
        List<EntradaArmazem> lista = new ArrayList<>();
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(LISTAR);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapEntrada(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar tudo", e);
        }
        return lista;
    }

    public List<EntradaArmazem> pesquisarPorDatas(java.util.Date inicio, java.util.Date fim) {
        List<EntradaArmazem> lista = new ArrayList<>();
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(POR_DATAS)) {
            ps.setDate(1, new java.sql.Date(inicio.getTime()));
            ps.setDate(2, new java.sql.Date(fim.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapEntrada(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao pesquisar por datas", e);
        }
        return lista;
    }

    public List<ResumoProduto> pesquisarRelatorioPorProduto(java.util.Date inicio, java.util.Date fim) {
        List<ResumoProduto> lista = new ArrayList<>();
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(LISTAR_POR_PRODUTO)) {
            ps.setDate(1, new java.sql.Date(inicio.getTime()));
            ps.setDate(2, new java.sql.Date(fim.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRelatorio(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao pesquisar relatório por produto", e);
        }
        return lista;
    }


    private int preencherInsertOuUpdate(Connection conn, String sql, EntradaArmazem e, boolean update) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, e.getPrecoProduto());
            ps.setDate(2, new java.sql.Date(e.getDataCompra().getTime()));
            ps.setInt(3, e.getQuantidadeProduto());
            ps.setInt(4, e.getProduto().getIdProduto());
            ps.setString(5, e.getUnidadeMedida());
            if (update) {
                ps.setInt(6, e.getIdArmazem());
            }
            return ps.executeUpdate();
        }
    }

    private void ajustarEstoque(Connection conn, int idProduto, int delta) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(AJUSTAR_ESTOQUE)) {
            ps.setInt(1, delta);
            ps.setInt(2, idProduto);
            ps.setInt(3, delta);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Produto " + idProduto + " inexistente ou estoque insuficiente para o ajuste de " + delta);
            }
        }
    }

    private EntradaArmazem mapEntrada(ResultSet rs) throws SQLException {
        EntradaArmazem entrada = new EntradaArmazem();
        entrada.setIdArmazem(rs.getInt("id_armazem"));
        entrada.setPrecoProduto(rs.getDouble("preco_produto"));
        entrada.setDataCompra(rs.getDate("data_compra"));
        entrada.setQuantidadeProduto(rs.getInt("quantidade_produto"));
        entrada.setDataRegisto(rs.getTimestamp("data_registo"));
        entrada.setUnidadeMedida(rs.getString("unidade_medida"));

        Produto produto = new Produto();
        produto.setIdProduto(rs.getInt("id_produto_prod"));
        produto.setDescricaoProduto(rs.getString("descricao_prod"));
        entrada.setProduto(produto);

        return entrada;
    }

    private ResumoProduto mapRelatorio(ResultSet rs) throws SQLException {
        return new ResumoProduto(
                rs.getInt("id_produto"),
                rs.getString("descricao_prod"),
                rs.getInt("total_quantidade"),
                rs.getBigDecimal("total_valor"));
    }

    public static class ResumoProduto implements java.io.Serializable {
        private static final long serialVersionUID = 1L;

        private final int idProduto;
        private final String descricaoProduto;
        private final int totalQuantidade;
        private final BigDecimal totalValor;

        public ResumoProduto(int idProduto, String descricaoProduto, int totalQuantidade, BigDecimal totalValor) {
            this.idProduto = idProduto;
            this.descricaoProduto = descricaoProduto;
            this.totalQuantidade = totalQuantidade;
            this.totalValor = totalValor;
        }

        public int getIdProduto() { return idProduto; }
        public String getDescricaoProduto() { return descricaoProduto; }
        public int getTotalQuantidade() { return totalQuantidade; }
        public BigDecimal getTotalValor() { return totalValor; }
    }
}