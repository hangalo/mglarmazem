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

    private static final String INSERT = "INSERT INTO produto(descricao_produto, categoria) VALUES (?, ?)";
    private static final String UPDATE = "UPDATE produto SET descricao_produto = ?, categoria = ? WHERE id_produto = ?";
    private static final String DELETE = "DELETE FROM produto WHERE id_produto = ?";
    private static final String BUSCAR_POR_CODIGO = "SELECT id_produto, descricao_produto, categoria FROM produto WHERE id_produto = ?";
    private static final String LISTAR_TUDO = "SELECT id_produto, descricao_produto, categoria FROM produto ORDER BY descricao_produto";

    public boolean save(Produto produto) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionDB.getConnection();
            ps = conn.prepareStatement(INSERT);
            ps.setString(1, produto.getDescricaoProduto());
            ps.setString(2, produto.getCategoria());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir: " + e.getMessage());
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
            ps.setString(2, produto.getCategoria());
            ps.setInt(3, produto.getIdProduto());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao actualizar: " + e.getMessage());
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
            System.out.println("Erro ao eliminar: " + e.getMessage());
            return false;
        } finally {
            ConnectionDB.closeConnection(conn, ps);
        }
    }

    public List<Produto> listarTudo() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Produto> lista = new ArrayList<Produto>();
        try {
            conn = ConnectionDB.getConnection();
            ps = conn.prepareStatement(LISTAR_TUDO);
            rs = ps.executeQuery();
            while (rs.next()) {
                Produto p = new Produto();
                p.setIdProduto(rs.getInt("id_produto"));
                p.setDescricaoProduto(rs.getString("descricao_produto"));
                p.setCategoria(rs.getString("categoria"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar: " + e.getMessage());
        } finally {
            ConnectionDB.closeConnection(conn, ps, rs);
        }
        return lista;
    }

    public List<Produto> listarPorCategoria(String categoriaFiltro) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Produto> lista = new ArrayList<Produto>();
        String sql = "SELECT id_produto, descricao_produto, categoria FROM produto WHERE categoria = ? ORDER BY descricao_produto";
        try {
            conn = ConnectionDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, categoriaFiltro);
            rs = ps.executeQuery();
            while (rs.next()) {
                Produto p = new Produto();
                p.setIdProduto(rs.getInt("id_produto"));
                p.setDescricaoProduto(rs.getString("descricao_produto"));
                p.setCategoria(rs.getString("categoria"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao filtrar: " + e.getMessage());
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
                produto = new Produto();
                produto.setIdProduto(rs.getInt("id_produto"));
                produto.setDescricaoProduto(rs.getString("descricao_produto"));
                produto.setCategoria(rs.getString("categoria"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar: " + e.getMessage());
        } finally {
            ConnectionDB.closeConnection(conn, ps, rs);
        }
        return produto;
    }
}