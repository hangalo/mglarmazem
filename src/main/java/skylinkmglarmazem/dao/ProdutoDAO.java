package skylinkmglarmazem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import skylink.mglarmazem.bdutil.ConnectionDB;
import skylink.armazem.modelo.CategoriaProduto;
import skylink.armazem.modelo.Produto;

public class ProdutoDAO {

    private static final String INSERT = "INSERT INTO produto(descricao_produto, id_categoria) VALUES (?, ?)";
    private static final String UPDATE = "UPDATE produto SET descricao_produto = ?, quantidade_existente = ?, id_categoria = ? WHERE id_produto = ?";
    private static final String DELETE = "DELETE FROM produto WHERE id_produto = ?";
    private static final String BUSCAR_POR_CODIGO = "SELECT * FROM produto WHERE id_produto = ?";
    private static final String LISTAR_TUDO = "SELECT id_produto, descricao_produto, quantidade_existente,  descricao_categoria FROM produto p INNER JOIN categoria_produto c ON p.id_categoria=c.id_categoria";
    private static final String LISTAR_POR_CATEGORIA = "SELECT id_produto, descricao_produto, quantidade_existente, c.id_categoria, descricao_categoria FROM produto p INNER JOIN categoria_produto c ON p.id_categoria=c.id_categoria WHERE c.id_categoria = ? ORDER BY descricao_produto";

    private static final String ATUALIZAR_ESTOQUE_PRODUTO = "UPDATE produto SET quantidade_existente = quantidade_existente + ? WHERE id_produto = ?";
    private static final String DIMINUIR_ESTOQUE_PRODUTO = "UPDATE produto SET quantidade_existente = quantidade_existente - ? WHERE id_produto = ?";
    
    public boolean save(Produto produto) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = ConnectionDB.getConnection();
            ps = conn.prepareStatement(INSERT);
            ps.setString(1, produto.getDescricaoProduto());
            ps.setInt(2, produto.getCategoriaProduto().getIdCategoria());
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
            ps.setInt(3, produto.getCategoriaProduto().getIdCategoria());
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
    
    public boolean updateAumentarQuantidade(Integer quantidade, Integer idProduto) {
        boolean bl;
        PreparedStatement ps = null;
        Connection conn = null;
        boolean flagControlo = false;
        try {
            System.out.println("Quantidade 1>>>>>>>>>>" + quantidade);
            conn = ConnectionDB.getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(ATUALIZAR_ESTOQUE_PRODUTO);
            ps.setInt(1, quantidade);
            ps.setInt(2, idProduto);
            int retorno = ps.executeUpdate();
            conn.commit();
            if (retorno > 0) {
                System.out.println("StockProdutoDAO:update AumentarQuantidade Dados quantidade aumentada com sucesso com sucesso: " + ps.getUpdateCount());
                flagControlo = true;
            }
            bl = flagControlo;
        }
        catch (SQLException e) {
            boolean bl2;
            try {
                System.out.println("Erro ao inserir dados: " + e.getMessage());
                bl2 = false;
            }
            catch (Throwable throwable) {
                ConnectionDB.closeConnection(conn, ps);
                throw throwable;
            }
            ConnectionDB.closeConnection((Connection)conn, (PreparedStatement)ps);
            return bl2;
        }
        ConnectionDB.closeConnection((Connection)conn, (PreparedStatement)ps);
        return bl;
    }
    
    public boolean updateDiminuirQuantidade(Integer quantidade, Integer idProduto) {
        boolean bl;
        PreparedStatement ps = null;
        Connection conn = null;
        boolean flagControlo = false;
        try {
            System.out.println("Quantidade a diminuir >>>>>>>>>>" + quantidade);
            conn = ConnectionDB.getConnection();
            conn.setAutoCommit(false);
            
            ps = conn.prepareStatement(DIMINUIR_ESTOQUE_PRODUTO); 
            
            ps.setInt(1, quantidade);
            ps.setInt(2, idProduto);
            int retorno = ps.executeUpdate();
            conn.commit();
            
            if (retorno > 0) {
                System.out.println("StockProdutoDAO:updateDiminuirQuantidade Dados quantidade diminuída com sucesso: " + ps.getUpdateCount());
                flagControlo = true;
            }
            bl = flagControlo;
        }
        catch (SQLException e) {
            boolean bl2;
            try {
                System.out.println("Erro ao diminuir dados no estoque: " + e.getMessage());
                bl2 = false;
            }
            catch (Throwable throwable) {
                ConnectionDB.closeConnection(conn, ps);
                throw throwable;
            }
            ConnectionDB.closeConnection((Connection)conn, (PreparedStatement)ps);
            return bl2;
        }
        ConnectionDB.closeConnection((Connection)conn, (PreparedStatement)ps);
        return bl;
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
       
        CategoriaProduto categoriaProduto = new CategoriaProduto();
        categoriaProduto.setDescricaoCategoria(rs.getString("descricao_categoria"));
        p.setCategoriaProduto(categoriaProduto);
        return p;
    }
    
    
    public int buscarQuantidadeAtual(int idProduto) throws SQLException {
        String sql = "SELECT quantidade_existente FROM produto WHERE id_produto = ?";
        
        try (Connection conn = ConnectionDB.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idProduto);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantidade_existente"); 
                }
            }
        }
        return 0; 
    }
}