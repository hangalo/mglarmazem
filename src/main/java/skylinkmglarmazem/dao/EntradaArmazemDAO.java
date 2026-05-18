package skylinkmglarmazem.dao;

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

    private static final String INSERT = "INSERT INTO entrada_armazem(preco_produto, data_compra, quantidade_produto, id_produto) VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE entrada_armazem SET data_registo=?, preco_produto=?, data_compra=?, quantidade_produto=?, id_produto=? WHERE id_armazem=?";
    private static final String DELETE = "DELETE FROM entrada_armazem WHERE id_armazem=?";

    private static final String LISTAR = "SELECT a.*, p.descricao_produto as descricao_prod FROM entrada_armazem a INNER JOIN produto p ON a.id_produto = p.id_produto ORDER BY a.data_registo DESC";
    private static final String POR_DATAS = "SELECT a.*, p.id_produto as id_produto_prod, p.descricao_produto as descricao_prod FROM entrada_armazem a INNER JOIN produto p ON a.id_produto = p.id_produto WHERE a.data_compra BETWEEN ? AND ? ORDER BY a.data_compra DESC";
    private static final String POR_PRODUTO = "SELECT a.*, p.descricao_produto as descricao_prod FROM entrada_armazem a INNER JOIN produto p ON a.id_produto = p.id_produto WHERE a.id_produto = ? ORDER BY a.data_registo DESC";

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

    public boolean salvar(EntradaArmazem entradaArmazem) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionDB.getConnection();
            ps = conn.prepareStatement(INSERT);
            ps.setDouble(1, entradaArmazem.getPrecoProduto());
            ps.setDate(2, new java.sql.Date(entradaArmazem.getDataCompra().getTime()));
            ps.setInt(3, entradaArmazem.getQuantidadeProduto());
            ps.setInt(4, entradaArmazem.getProduto().getIdProduto());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir produto: " + e.getMessage());
            return false;
        } finally {
            ConnectionDB.closeConnection(conn, ps);
        }
    }


    public List<EntradaArmazem> listarTudo() {
        List<EntradaArmazem> lista = new ArrayList<>();
        try (Connection conn = ConnectionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(LISTAR); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(map(rs));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar", e);
        }
        return lista;
    }

    public List<EntradaArmazem> pesquisarPorDatas(java.util.Date inicio, java.util.Date fim) {
        List<EntradaArmazem> lista = new ArrayList<>();
        try (Connection conn = ConnectionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(POR_DATAS)) {
            ps.setDate(1, new java.sql.Date(inicio.getTime()));
            ps.setDate(2, new java.sql.Date(fim.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(map(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro por datas", e);
        }
        return lista;
    }
    
    private EntradaArmazem map(ResultSet rs) throws SQLException {
    EntradaArmazem entrada = new EntradaArmazem();
    
    entrada.setIdArmazem(rs.getInt("id_armazem")); 
    entrada.setPrecoProduto(rs.getDouble("preco_produto")); 
    entrada.setQuantidadeProduto(rs.getInt("quantidade_produto"));
    entrada.setDataCompra(rs.getDate("data_compra"));
    
    if (rs.getTimestamp("data_registo") != null) {
        entrada.setDataRegisto(new java.util.Date(rs.getTimestamp("data_registo").getTime()));
    }

    Produto produto = new Produto();
    
    produto.setIdProduto(rs.getInt("id_produto_prod")); 
    produto.setDescricaoProduto(rs.getString("descricao_prod"));
    
    entrada.setProduto(produto);

    return entrada;
}
}
