package skylinkmglarmazem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import skylink.mglarmazem.bdutil.ConnectionDB;
import skylink.mglarmazem.modelo.Produto;

/**
 *
 * @Henriques
 */

public class ProdutoDAO {

    private static final String INSERT = "INSERT INTO produto(descricao_produto) VALUES (?)";
    private static final String UPDATE = "UPDATE produto SET descricao_produto = ? WHERE id_produto = ?";
    private static final String DELETE = "DELETE FROM produto WHERE id_produto = ?";
    private static final String BUSCAR_POR_CODIGO = "SELECT id_produto, descricao_produto FROM produto WHERE id_produto = ?";
    private static final String LISTAR_TUDO = "SELECT id_produto, descricao_produto FROM produto";

    public boolean save(Produto produto) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean flagControlo = false;
        try {
            conn = ConnectionDB.getConnection();
            ps = conn.prepareStatement(INSERT);
            ps.setString(1, produto.getDescricaoProduto());
            int retorno = ps.executeUpdate();
            if (retorno > 0) {
                flagControlo = true;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao inserir dados: " + e.getMessage());
        } finally {
            ConnectionDB.closeConnection(conn, ps);
        }
        return flagControlo;
    }

    public boolean update(Produto produto) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean flagControlo = false;
        try {
            conn = ConnectionDB.getConnection();
            ps = conn.prepareStatement(UPDATE);
            ps.setString(1, produto.getDescricaoProduto());
            ps.setInt(2, produto.getIdProduto());
            int retorno = ps.executeUpdate();
            if (retorno > 0) {
                flagControlo = true;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao actualizar dados: " + e.getMessage());
        } finally {
            ConnectionDB.closeConnection(conn, ps);
        }
        return flagControlo;
    }

    public boolean delete(int idProduto) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean flagControlo = false;
        try {
            conn = ConnectionDB.getConnection();
            ps = conn.prepareStatement(DELETE);
            ps.setInt(1, idProduto);
            int retorno = ps.executeUpdate();
            if (retorno > 0) {
                flagControlo = true;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao eliminar dados: " + e.getMessage());
        } finally {
            ConnectionDB.closeConnection(conn, ps);
        }
        return flagControlo;
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
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar produto: " + e.getMessage());
        } finally {
            ConnectionDB.closeConnection(conn, ps, rs);
        }
        return produto;
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
                Produto produto = new Produto();
                produto.setIdProduto(rs.getInt("id_produto"));
                produto.setDescricaoProduto(rs.getString("descricao_produto"));
                lista.add(produto);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar produtos: " + e.getMessage());
        } finally {
            ConnectionDB.closeConnection(conn, ps, rs);
        }
        return lista;
    }
}
