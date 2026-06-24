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

    private static final String INSERT = "INSERT INTO saida_armazem (id_produto, id_sector, quantidade_saida_armazem, data_saida_armazem, unidade_medida) VALUES (?, ?, ?, ?, ?)";

    private static final String LISTAR_POR_SECTOR = "SELECT s.id_saida_armazem AS numero_operacao, p.descricao_produto AS produto, sec.descricao_sector AS sector_destino, s.quantidade_saida_armazem AS qtd_retirada, s.unidade_medida AS unidade, s.data_saida_armazem AS data FROM saida_armazem s INNER JOIN produto p ON s.id_produto = p.id_produto INNER JOIN sector sec ON s.id_sector = sec.id_sector WHERE sec.id_sector = ? AND s.data_saida_armazem BETWEEN ? AND ?";

    private static final String PESQUISAR_SAIDAS_PRODUTO = "SELECT p.descricao_produto AS descricao_prod, SUM(s.quantidade_saida_armazem) AS total_quantidade, s.unidade_medida AS unidade FROM produto p INNER JOIN saida_armazem s ON p.id_produto = s.id_produto WHERE s.data_saida_armazem BETWEEN ? AND ? GROUP BY p.descricao_produto, s.unidade_medida";

    private static final String LISTAR_POR_SECTOR_E_DATAS = "SELECT s.id_saida_armazem, s.data_saida_armazem, s.id_sector, s.quantidade_saida_armazem, s.unidade_medida, s.id_produto, p.descricao_produto, sec.descricao_sector FROM saida_armazem s INNER JOIN produto p ON s.id_produto = p.id_produto INNER JOIN sector sec ON s.id_sector = sec.id_sector WHERE s.id_sector = ? AND s.data_saida_armazem BETWEEN ? AND ? ORDER BY s.data_saida_armazem DESC";

    private static final String LISTAR_SAIDAS_DE_HOJE = "SELECT s.id_saida_armazem, s.data_saida_armazem, s.id_sector, s.quantidade_saida_armazem, s.unidade_medida, s.id_produto, p.descricao_produto, sec.descricao_sector FROM saida_armazem s INNER JOIN produto p ON s.id_produto = p.id_produto INNER JOIN sector sec ON s.id_sector = sec.id_sector WHERE DATE(s.data_saida_armazem) = CURDATE() ORDER BY s.data_saida_armazem DESC";

    public boolean registrarSaida(SaidaArmazem saida) throws SQLException {
        try (Connection conn = ConnectionDB.getConnection(); PreparedStatement psInsert = conn.prepareStatement(INSERT)) {

            psInsert.setInt(1, saida.getIdProduto().getIdProduto());
            psInsert.setInt(2, saida.getSector().getIdSector());
            psInsert.setInt(3, saida.getQuantidadeSaidaArmazem());
            psInsert.setTimestamp(4, new java.sql.Timestamp(saida.getDataSaidaArmazem().getTime()));
            psInsert.setString(5, saida.getUnidadeMedida());

            psInsert.executeUpdate();
            return true;
        }
    }

    public List<SaidaArmazem> listarSaidasDeHoje() throws SQLException {
        List<SaidaArmazem> lista = new ArrayList<>();

        try (Connection conn = ConnectionDB.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(LISTAR_SAIDAS_DE_HOJE);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sector sec = new Sector();
                sec.setIdSector(rs.getInt("id_sector"));
                sec.setDescricaoSector(rs.getString("descricao_sector"));

                Produto prod = new Produto();
                prod.setIdProduto(rs.getInt("id_produto"));
                prod.setDescricaoProduto(rs.getString("descricao_produto"));

                EntradaArmazem ent = new EntradaArmazem();
                ent.setProduto(prod);

                SaidaArmazem s = new SaidaArmazem(
                        rs.getInt("id_saida_armazem"),
                        rs.getTimestamp("data_saida_armazem"),
                        sec,
                        prod,
                        rs.getInt("quantidade_saida_armazem"),
                        rs.getString("unidade_medida"),
                        ent
                );
                lista.add(s);
            }
        }
        return lista;
    }

    public List<SaidaArmazem> listarPorSector(int idSector, java.util.Date inicio, java.util.Date fim) throws SQLException {
        List<SaidaArmazem> lista = new ArrayList<>();

        try (Connection conn = ConnectionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(LISTAR_POR_SECTOR)) {

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
        if (inicio == null || fim == null) return lista;

        try (Connection conn = ConnectionDB.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(PESQUISAR_SAIDAS_PRODUTO)) {

            ps.setTimestamp(1, new java.sql.Timestamp(inicio.getTime()));
            ps.setTimestamp(2, new java.sql.Timestamp(fim.getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EntradaArmazem item = new EntradaArmazem();

                    Produto p = new Produto();
                    p.setDescricaoProduto(rs.getString("descricao_prod")); 

                    item.setProduto(p);
                    item.setQuantidadeProduto(rs.getInt("total_quantidade")); 
                    item.setUnidadeMedida(rs.getString("unidade"));          
                    
                    item.setTotalValorRelatorio(java.math.BigDecimal.ZERO);

                    lista.add(item);
                }
            }
        }
        return lista;
    }

    public List<SaidaArmazem> listarEntreDatasPorSector(int idSector, java.util.Date inicio, java.util.Date fim) throws SQLException {
        List<SaidaArmazem> lista = new ArrayList<>();
        try (Connection conn = ConnectionDB.getConnection(); PreparedStatement ps = conn.prepareStatement(LISTAR_POR_SECTOR_E_DATAS)) {
            ps.setInt(1, idSector);
            ps.setTimestamp(2, new java.sql.Timestamp(inicio.getTime()));
            ps.setTimestamp(3, new java.sql.Timestamp(fim.getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sector sec = new Sector();
                    sec.setIdSector(rs.getInt("id_sector"));
                    sec.setDescricaoSector(rs.getString("descricao_sector"));

                    Produto prod = new Produto();
                    prod.setIdProduto(rs.getInt("id_produto"));
                    prod.setDescricaoProduto(rs.getString("descricao_produto"));

                    EntradaArmazem ent = new EntradaArmazem();
                    ent.setProduto(prod);

                    SaidaArmazem s = new SaidaArmazem(
                            rs.getInt("id_saida_armazem"),
                            rs.getTimestamp("data_saida_armazem"),
                            sec,
                            prod,
                            rs.getInt("quantidade_saida_armazem"),
                            rs.getString("unidade_medida"),
                            ent
                    );
                    lista.add(s);
                }
            }
        }
        return lista;
    }

    private SaidaArmazem mapRelatorioSector(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setDescricaoProduto(rs.getString("produto"));

        EntradaArmazem ent = new EntradaArmazem();
        ent.setProduto(p);

        Sector sec = new Sector();
        sec.setDescricaoSector(rs.getString("sector_destino"));

        int idSaida = rs.getInt("numero_operacao");
        java.util.Date dataSaida = rs.getTimestamp("data");
        int qtdRetirada = rs.getInt("qtd_retirada");
        String unidade = rs.getString("unidade");

        return new SaidaArmazem(idSaida, dataSaida, sec, p, qtdRetirada, unidade, ent);
    }
}