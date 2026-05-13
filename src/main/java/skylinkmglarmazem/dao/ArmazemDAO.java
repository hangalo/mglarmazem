package skylinkmglarmazem.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import skylink.mglarmazem.bdutil.ConnectionDB;
import skylink.mglarmazem.modelo.Armazem;

/**
 * @author Henriques
 */
public class ArmazemDAO {

    private static final Logger LOGGER = Logger.getLogger(ArmazemDAO.class.getName());

    private static final String INSERT = "INSERT INTO armazem(data_registo, preco_produto, data_compra, quantidade_produto, id_produto) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE armazem SET data_registo=?, preco_produto=?, data_compra=?, quantidade_produto=?, id_produto=? WHERE id_armazem=?";
    private static final String DELETE = "DELETE FROM armazem WHERE id_armazem=?";
    
    private static final String LISTAR = "SELECT a.*, p.descricao_produto as descricao_prod FROM armazem a INNER JOIN produto p ON a.id_produto = p.id_produto ORDER BY a.data_registo DESC";
    private static final String POR_DATAS = "SELECT a.*, p.descricao_produto as descricao_prod FROM armazem a INNER JOIN produto p ON a.id_produto = p.id_produto WHERE a.data_compra BETWEEN ? AND ? ORDER BY a.data_compra DESC";
    private static final String POR_PRODUTO = "SELECT a.*, p.descricao_produto as descricao_prod FROM armazem a INNER JOIN produto p ON a.id_produto = p.id_produto WHERE a.id_produto = ? ORDER BY a.data_registo DESC";

    private static final String ATUALIZAR_ESTOQUE_PRODUTO = "UPDATE produto SET quantidade_existente = quantidade_existente + ? WHERE id_produto = ?";

    private boolean produtoExiste(Connection conn, int idProduto) throws SQLException {
        String sql = "SELECT 1 FROM produto WHERE id_produto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProduto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void validar(Armazem a, Connection conn) throws SQLException {
        if (a == null) throw new RuntimeException("Objeto Armazem está nulo!");
        if (a.getPrecoProduto() == null || a.getPrecoProduto() <= 0) throw new RuntimeException("Preço inválido!");
        if (a.getQuantidadeProduto() == null || a.getQuantidadeProduto() <= 0) throw new RuntimeException("Quantidade inválida!");
        if (a.getIdProduto() == null || a.getIdProduto() <= 0) throw new RuntimeException("Selecione um produto!");
        if (!produtoExiste(conn, a.getIdProduto())) throw new RuntimeException("Produto não cadastrado!");
    }

    public boolean salvar(Armazem a) {
        Connection conn = null;
        try {
            conn = ConnectionDB.getConnection();
            conn.setAutoCommit(false); 
            
            validar(a, conn);

            try (PreparedStatement ps = conn.prepareStatement(INSERT)) {
                ps.setTimestamp(1, a.getDataRegisto() == null ? new Timestamp(System.currentTimeMillis()) : new Timestamp(a.getDataRegisto().getTime()));
                ps.setDouble(2, a.getPrecoProduto());
                ps.setDate(3, a.getDataCompra() != null ? new java.sql.Date(a.getDataCompra().getTime()) : null);
                ps.setInt(4, a.getQuantidadeProduto());
                ps.setInt(5, a.getIdProduto());
                ps.executeUpdate();
            }

            try (PreparedStatement psEstoque = conn.prepareStatement(ATUALIZAR_ESTOQUE_PRODUTO)) {
                psEstoque.setInt(1, a.getQuantidadeProduto());
                psEstoque.setInt(2, a.getIdProduto());
                psEstoque.executeUpdate();
            }

            conn.commit(); 
            return true;
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { }
            LOGGER.log(Level.SEVERE, "Erro ao salvar", e);
            throw new RuntimeException(e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) { }
        }
    }

    private Armazem map(ResultSet rs) throws SQLException {
        Armazem a = new Armazem();
        a.setIdArmazem(rs.getInt("id_armazem"));
        a.setDataRegisto(rs.getTimestamp("data_registo"));
        a.setPrecoProduto(rs.getDouble("preco_produto"));
        a.setDataCompra(rs.getDate("data_compra"));
        a.setQuantidadeProduto(rs.getInt("quantidade_produto"));
        a.setIdProduto(rs.getInt("id_produto"));
        
        try {

            a.setDescricaoProduto(rs.getString("descricao_prod"));
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Coluna descricao_prod não encontrada no ResultSet");
        }
        return a;
    }

    public List<Armazem> listarTudo() {
        List<Armazem> lista = new ArrayList<>();
        try (Connection conn = ConnectionDB.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(LISTAR); 
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) { lista.add(map(rs)); }
        } catch (Exception e) { LOGGER.log(Level.SEVERE, "Erro ao listar", e); }
        return lista;
    }

    public List<Armazem> pesquisarPorDatas(java.util.Date inicio, java.util.Date fim) {
        List<Armazem> lista = new ArrayList<>();
        try (Connection conn = ConnectionDB.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(POR_DATAS)) {
            ps.setDate(1, new java.sql.Date(inicio.getTime()));
            ps.setDate(2, new java.sql.Date(fim.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { lista.add(map(rs)); }
            }
        } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Erro por datas", e); }
        return lista;
    }
}