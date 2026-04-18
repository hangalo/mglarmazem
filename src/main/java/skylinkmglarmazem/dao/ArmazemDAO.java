package skylinkmglarmazem.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import skylink.mglarmazem.bdutil.ConnectionDB;
import skylink.mglarmazem.modelo.Armazem;

public class ArmazemDAO {

    private static final Logger LOGGER = Logger.getLogger(ArmazemDAO.class.getName());

    private static final String INSERT  = "INSERT INTO armazem(data_registo, preco_produto, data_compra, quantidade_produto, id_produto) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE  = "UPDATE armazem SET data_registo=?, preco_produto=?, data_compra=?, quantidade_produto=?, id_produto=? WHERE id_armazem=?";
    private static final String DELETE  = "DELETE FROM armazem WHERE id_armazem=?";
    private static final String LISTAR  = "SELECT * FROM armazem ORDER BY data_registo DESC";

    private static final String POR_DATAS   =
        "SELECT * FROM armazem WHERE data_compra BETWEEN ? AND ? ORDER BY data_compra DESC";
    private static final String POR_PRODUTO =
        "SELECT * FROM armazem WHERE id_produto = ? ORDER BY data_registo DESC";

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
        if (a == null)
            throw new RuntimeException("Objecto Armazem está nulo!");
        if (a.getPrecoProduto() == null || a.getPrecoProduto() <= 0)
            throw new RuntimeException("Preço do produto inválido!");
        if (a.getQuantidadeProduto() == null || a.getQuantidadeProduto() <= 0)
            throw new RuntimeException("Quantidade inválida!");
        if (a.getIdProduto() == null || a.getIdProduto() <= 0)
            throw new RuntimeException("Seleccione um produto válido!");
        if (!produtoExiste(conn, a.getIdProduto()))
            throw new RuntimeException("Produto não existe na base de dados!");
    }

    public boolean save(Armazem a) {
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT)) {

            validar(a, conn);
            preencherCampos(ps, a, false);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao salvar", e);
            throw new RuntimeException("Erro ao salvar: " + e.getMessage());
        }
    }

    public boolean update(Armazem a) {
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            validar(a, conn);
            if (a.getIdArmazem() == null || a.getIdArmazem() <= 0)
                throw new RuntimeException("ID do armazém inválido!");

            preencherCampos(ps, a, true);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao actualizar", e);
            throw new RuntimeException("Erro ao actualizar: " + e.getMessage());
        }
    }

    public boolean delete(int id) {
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {

            if (id <= 0)
                throw new RuntimeException("ID inválido para eliminação!");

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao eliminar", e);
            throw new RuntimeException("Erro ao eliminar: " + e.getMessage());
        }
    }

    public List<Armazem> listarTudo() {
        List<Armazem> lista = new ArrayList<>();
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(LISTAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(map(rs));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar", e);
            throw new RuntimeException("Erro ao listar: " + e.getMessage());
        }
        return lista;
    }

    public List<Armazem> pesquisarPorDatas(java.util.Date dataInicio, java.util.Date dataFim) {
        List<Armazem> lista = new ArrayList<>();
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(POR_DATAS)) {

            ps.setDate(1, new Date(dataInicio.getTime()));
            ps.setDate(2, new Date(dataFim.getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao pesquisar por datas", e);
            throw new RuntimeException("Erro ao pesquisar por datas: " + e.getMessage());
        }
        return lista;
    }

    public List<Armazem> pesquisarPorProduto(int idProduto) {
        List<Armazem> lista = new ArrayList<>();
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(POR_PRODUTO)) {

            ps.setInt(1, idProduto);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao pesquisar por produto", e);
            throw new RuntimeException("Erro ao pesquisar por produto: " + e.getMessage());
        }
        return lista;
    }

    private void preencherCampos(PreparedStatement ps, Armazem a, boolean isUpdate) throws SQLException {
        if (a.getDataRegisto() != null)
            ps.setTimestamp(1, new Timestamp(a.getDataRegisto().getTime()));
        else
            ps.setNull(1, Types.TIMESTAMP);

        ps.setDouble(2, a.getPrecoProduto());

        if (a.getDataCompra() != null)
            ps.setDate(3, new Date(a.getDataCompra().getTime()));
        else
            ps.setNull(3, Types.DATE);

        ps.setInt(4, a.getQuantidadeProduto());
        ps.setInt(5, a.getIdProduto());

        if (isUpdate)
            ps.setInt(6, a.getIdArmazem());
    }

    private Armazem map(ResultSet rs) throws SQLException {
        Armazem a = new Armazem();
        a.setIdArmazem(rs.getInt("id_armazem"));
        a.setDataRegisto(rs.getTimestamp("data_registo"));
        a.setPrecoProduto(rs.getDouble("preco_produto"));
        a.setDataCompra(rs.getDate("data_compra"));
        a.setQuantidadeProduto(rs.getInt("quantidade_produto"));
        a.setIdProduto(rs.getInt("id_produto"));
        return a;
    }
}