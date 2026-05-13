package skylinkmglarmazem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import skylink.mglarmazem.bdutil.ConnectionDB;
import skylink.mglarmazem.modelo.Produto;

public class ProdutoDAO {

    private static final String INSERT = "INSERT INTO produto(descricao_produto, quantidade_existente, id_categoria) VALUES (?, ?, ?)";
    private static final String UPDATE = "UPDATE produto SET descricao_produto = ?, quantidade_existente = ?, id_categoria = ? WHERE id_produto = ?";
    private static final String DELETE = "DELETE FROM produto WHERE id_produto = ?";
    private static final String BUSCAR_POR_CODIGO = "SELECT * FROM produto WHERE id_produto = ?";
    private static final String LISTAR_TUDO = "SELECT * FROM produto ORDER BY descricao_produto";
    private static final String LISTAR_POR_CATEGORIA = "SELECT * FROM produto WHERE id_categoria = ? ORDER BY descricao_produto";

    public boolean save(Produto produto) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionDB.getConnection();
            ps = conn.prepareStatement(INSERT);
            ps.setString(1, produto.getDescricaoProduto());
            ps.setInt(2, produto.getQuantidadeExistente());
            ps.setInt(3, produto.getIdCategoria());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir produto: " + e.getMessage());
            return false;
        } finally {
            ConnectionDB.closeConnection(conn, ps);
        }
    }

    public boolean update(Produto produto) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionDB.getConnection();
            ps = conn.prepareStatement(UPDATE);
            ps.setString(1, produto.getDescricaoProduto());
            ps.setInt(2, produto.getQuantidadeExistente());
            ps.setInt(3, produto.getIdCategoria());
            ps.setInt(4, produto.getIdProduto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao actualizar produto: " + e.getMessage());
            return false;
        } finally {
            ConnectionDB.closeConnection(conn, ps);
        }
    }

    public boolean delete(int idProduto) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionDB.getConnection();
            ps = conn.prepareStatement(DELETE);
            ps.setInt(1, idProduto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao eliminar produto: " + e.getMessage());
            return false;
        } finally {
            ConnectionDB.closeConnection(conn, ps);
        }
    }

    public List<Produto> listarTudo() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Produto> lista = new ArrayList<>();
        try {
            conn = ConnectionDB.getConnection();
            ps = conn.prepareStatement(LISTAR_TUDO);
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
        } finally {
            ConnectionDB.closeConnection(conn, ps, rs);
        }
        return lista;
    }

    public List<Produto> listarPorCategoria(int idCategoriaFiltro) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Produto> lista = new ArrayList<>();
        try {
            conn = ConnectionDB.getConnection();
            ps = conn.prepareStatement(LISTAR_POR_CATEGORIA);
            ps.setInt(1, idCategoriaFiltro);
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao filtrar por categoria: " + e.getMessage());
        } finally {
            ConnectionDB.closeConnection(conn, ps, rs);
        }
        return lista;
    }

    public Produto buscarPorCodigo(int idProduto) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Produto produto = null;
        try {
            conn = ConnectionDB.getConnection();
            ps = conn.prepareStatement(BUSCAR_POR_CODIGO);
            ps.setInt(1, idProduto);
            rs = ps.executeQuery();
            if (rs.next()) {
                produto = mapearResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produto: " + e.getMessage());
        } finally {
            ConnectionDB.closeConnection(conn, ps, rs);
        }
        return produto;
    }

    private Produto mapearResultSet(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setIdProduto(rs.getInt("id_produto"));
        p.setDescricaoProduto(rs.getString("descricao_produto"));
        p.setQuantidadeExistente(rs.getInt("quantidade_existente"));
        p.setIdCategoria(rs.getInt("id_categoria"));
        return p;
    }

    
}