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
import skylink.armazem.modelo.Sector;
import skylink.armazem.modelo.SaidaArmazem;

public class ProdutoDAO {

    private static final String INSERT = "INSERT INTO produto(descricao_produto, id_categoria) VALUES (?, ?)";
    private static final String UPDATE = "UPDATE produto SET descricao_produto = ?, quantidade_existente = ?, id_categoria = ? WHERE id_produto = ?";
    private static final String DELETE = "DELETE FROM produto WHERE id_produto = ?";
    private static final String BUSCAR_POR_CODIGO = "SELECT * FROM produto WHERE id_produto = ?";
    private static final String LISTAR_TUDO = "SELECT id_produto, descricao_produto, quantidade_existente,  descricao_categoria FROM produto p INNER JOIN categoria_produto c ON p.id_categoria=c.id_categoria";
    private static final String LISTAR_POR_CATEGORIA = "SELECT id_produto, descricao_produto, quantidade_existente, c.id_categoria, descricao_categoria FROM produto p INNER JOIN categoria_produto c ON p.id_categoria=c.id_categoria WHERE c.id_categoria = ? ORDER BY descricao_produto";

    private static final String ATUALIZAR_ESTOQUE_PRODUTO = "UPDATE produto SET quantidade_existente = quantidade_existente + ? WHERE id_produto = ?";
    private static final String DIMINUIR_ESTOQUE_PRODUTO = "UPDATE produto SET quantidade_existente = quantidade_existente - ? WHERE id_produto = ?";
    
    private static final String LISTAR_STOCK_POR_SECTOR = 
        "SELECT sec.id_sector, sec.descricao_sector, p.id_produto, p.descricao_produto, " +
        "SUM(s.quantidade_saida_armazem) AS quantidade_existente, s.unidade_medida " +
        "FROM saida_armazem s " +
        "INNER JOIN sector sec ON s.id_sector = sec.id_sector " +
        "INNER JOIN produto p ON s.id_produto = p.id_produto " +
        "GROUP BY sec.id_sector, sec.descricao_sector, p.id_produto, p.descricao_produto, s.unidade_medida " +
        "ORDER BY sec.descricao_sector ASC, quantidade_existente DESC";

    public List<SaidaArmazem> listarStockPorSector() throws SQLException {
        List<SaidaArmazem> lista = new ArrayList<>();
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(LISTAR_STOCK_POR_SECTOR);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                Sector sec = new Sector();
                sec.setIdSector(rs.getInt("id_sector"));
                sec.setDescricaoSector(rs.getString("descricao_sector"));

                Produto prod = new Produto();
                prod.setIdProduto(rs.getInt("id_produto"));
                prod.setDescricaoProduto(rs.getString("descricao_produto"));

                SaidaArmazem stock = new SaidaArmazem();
                stock.setSector(sec);
                stock.setIdProduto(prod); 
                stock.setQuantidadeSaidaArmazem(rs.getInt("quantidade_existente"));
                
                String unidade = rs.getString("unidade_medida");
                stock.setUnidadeMedida(unidade != null ? unidade : "Unidade");

                lista.add(stock);
            }
        }
        return lista;
    }

    public boolean save(Produto produto) throws SQLException {
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT)) {
             
            ps.setString(1, produto.getDescricaoProduto());
            ps.setInt(2, produto.getCategoriaProduto().getIdCategoria());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Produto produto) throws SQLException {
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {
             
            ps.setString(1, produto.getDescricaoProduto());
            ps.setInt(2, produto.getQuantidadeExistente());
            ps.setInt(3, produto.getCategoriaProduto().getIdCategoria());
            ps.setInt(4, produto.getIdProduto());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int idProduto) throws SQLException {
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
             
            ps.setInt(1, idProduto);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Produto> listarTudo() throws SQLException {
        List<Produto> lista = new ArrayList<>();
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(LISTAR_TUDO);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
        }
        return lista;
    }

    public List<Produto> listarPorCategoria(int idCategoriaFiltro) throws SQLException {
        List<Produto> lista = new ArrayList<>();
        
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(LISTAR_POR_CATEGORIA)) {
             
            ps.setInt(1, idCategoriaFiltro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResultSet(rs));
                }
            }
        }
        return lista;
    }
    
    public boolean updateAumentarQuantidade(Integer quantidade, Integer idProduto) throws SQLException {
        try (Connection conn = ConnectionDB.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(ATUALIZAR_ESTOQUE_PRODUTO)) {
                ps.setInt(1, quantidade);
                ps.setInt(2, idProduto);
                int retorno = ps.executeUpdate();
                conn.commit();
                return retorno > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
    
    public boolean updateDiminuirQuantidade(Integer quantidade, Integer idProduto) throws SQLException {
        try (Connection conn = ConnectionDB.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(DIMINUIR_ESTOQUE_PRODUTO)) {
                ps.setInt(1, quantidade);
                ps.setInt(2, idProduto);
                int retorno = ps.executeUpdate();
                conn.commit();
                return retorno > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public Produto buscarPorCodigo(int idProduto) throws SQLException {
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(BUSCAR_POR_CODIGO)) {
             
            ps.setInt(1, idProduto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }
        }
        return null;
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