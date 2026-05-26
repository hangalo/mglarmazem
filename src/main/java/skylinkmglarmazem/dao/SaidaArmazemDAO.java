package skylinkmglarmazem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import skylink.armazem.modelo.Produto;
import skylink.mglarmazem.bdutil.ConnectionDB;
import skylink.armazem.modelo.SaidaArmazem;
import skylink.armazem.modelo.Sector;
import skylink.mglarmazem.modelo.EntradaArmazem;

/**
 * @author Henriques
 */
public class SaidaArmazemDAO {

    private static final String INSERT = "INSERT INTO saida_armazem (id_produto, id_sector, quantidade_saida_armazem, data_saida_armazem) VALUES (?, ?, ?, ?)";
    
    private static final String LISTAR_POR_SECTOR = "SELECT sec.descricao_sector, p.descricao_produto, p.quantidade_existente FROM saida_armazem s INNER JOIN produto p ON s.id_produto = p.id_produto INNER JOIN sector sec ON s.id_sector = sec.id_sector WHERE sec.id_sector = ? AND s.data_saida_armazem BETWEEN ? AND ? GROUP BY sec.descricao_sector, p.descricao_produto, p.quantidade_existente";

    private static final String PESQUISAR_SAIDAS_PRODUTO = "SELECT p.descricao_produto AS descricao_prod, SUM(s.quantidade_saida_armazem) AS total_quantidade FROM produto p INNER JOIN saida_armazem s ON p.id_produto = s.id_produto WHERE s.data_saida_armazem BETWEEN ? AND ? GROUP BY p.descricao_produto";

    private static final String LISTAR_TUDO = "SELECT s.id_saida_armazem, s.data_saida_armazem, s.id_sector, s.quantidade_saida_armazem, s.id_produto, p.descricao_produto, sec.descricao_sector, (SELECT e.preco_produto FROM entrada_armazem e WHERE e.id_produto = s.id_produto ORDER BY e.id_armazem DESC LIMIT 1) AS preco_produto FROM saida_armazem s INNER JOIN produto p ON s.id_produto = p.id_produto INNER JOIN sector sec ON s.id_sector = sec.id_sector ORDER BY s.data_saida_armazem DESC";

    public boolean registrarSaida(SaidaArmazem saida) throws SQLException {

        try (Connection conn = ConnectionDB.getConnection(); 
             PreparedStatement psInsert = conn.prepareStatement(INSERT)) {
            
            psInsert.setInt(1, saida.getIdProduto().getIdProduto()); 
            psInsert.setInt(2, saida.getIdSector().getIdSector());   
            psInsert.setInt(3, saida.getQuantidadeSaidaArmazem());
            psInsert.setTimestamp(4, new java.sql.Timestamp(saida.getDataSaidaArmazem().getTime()));
            
            psInsert.executeUpdate();
            return true;
        }
    }

    public List<SaidaArmazem> listarPorSector(int idSector, java.util.Date inicio, java.util.Date fim) throws SQLException {
        List<SaidaArmazem> lista = new ArrayList<>();

        try (Connection conn = ConnectionDB.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(LISTAR_POR_SECTOR)) {

            ps.setInt(1, idSector);
            ps.setTimestamp(2, new java.sql.Timestamp(inicio.getTime()));
            ps.setTimestamp(3, new java.sql.Timestamp(fim.getTime()));    

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRelatorioSector(rs));
                }
            }
        }
        return lista;
    }

    public List<EntradaArmazem> pesquisarSaidasPorProduto(java.util.Date inicio, java.util.Date fim) throws SQLException {
        List<EntradaArmazem> lista = new ArrayList<>();

        try (Connection conn = ConnectionDB.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(PESQUISAR_SAIDAS_PRODUTO)) {

            ps.setTimestamp(1, new java.sql.Timestamp(inicio.getTime()));
            ps.setTimestamp(2, new java.sql.Timestamp(fim.getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EntradaArmazem entrada = new EntradaArmazem(
                            rs.getString("descricao_prod"),
                            rs.getInt("total_quantidade"),
                            java.math.BigDecimal.ZERO
                    );
                    lista.add(entrada);
                }
            }
        }
        return lista;
    }

    public List<SaidaArmazem> listarTudo() throws SQLException {
        List<SaidaArmazem> lista = new ArrayList<>();
        
        try (Connection conn = ConnectionDB.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(LISTAR_TUDO); 
             ResultSet rs = ps.executeQuery()) {
                 
            while (rs.next()) {

                Sector secId = new Sector();
                secId.setIdSector(rs.getInt("id_sector"));
                
                Produto prodId = new Produto();
                prodId.setIdProduto(rs.getInt("id_produto"));

                EntradaArmazem ent = new EntradaArmazem();
                Produto pRelatorio = new Produto();
                pRelatorio.setDescricaoProduto(rs.getString("descricao_produto"));
                ent.setProduto(pRelatorio);
                ent.setPrecoProduto(rs.getDouble("preco_produto"));

                Sector secRelatorio = new Sector();
                secRelatorio.setDescricaoSector(rs.getString("descricao_sector"));

                SaidaArmazem s = new SaidaArmazem(
                    rs.getInt("id_saida_armazem"),
                    rs.getTimestamp("data_saida_armazem"),
                    secId,
                    prodId,
                    rs.getInt("quantidade_saida_armazem"),
                    ent,
                    secRelatorio
                );

                lista.add(s);
            }
        }
        return lista;
    }

    private SaidaArmazem mapRelatorioSector(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setDescricaoProduto(rs.getString("descricao_produto"));
        p.setQuantidadeExistente(rs.getInt("quantidade_existente"));

        EntradaArmazem ent = new EntradaArmazem();
        ent.setProduto(p);

        Sector sec = new Sector();
        sec.setDescricaoSector(rs.getString("descricao_sector"));

        return new SaidaArmazem(null, null, null, null, null, ent, sec);
    }
}