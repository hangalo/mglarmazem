package skylinkmglarmazem.dao;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import skylink.mglarmazem.bdutil.ConnectionDB;
import skylink.mglarmazem.modelo.Armazem;

/**
 *
 * @Henriques
 */

public class ArmazemDAO {

    private static final Logger LOGGER = Logger.getLogger(ArmazemDAO.class.getName());

    private static final String INSERT = "INSERT INTO armazem(data_registo, preco_produto, data_compra, quantidade_produto, id_produto) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE armazem SET data_registo=?, preco_produto=?, data_compra=?, quantidade_produto=?, id_produto=? WHERE id_armazem=?";
    private static final String DELETE = "DELETE FROM armazem WHERE id_armazem=?";
    private static final String LISTAR = "SELECT * FROM armazem";

    public boolean save(Armazem a) {
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT)) {

            preencherCampos(ps, a, false);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao salvar", e);
            throw new RuntimeException(e); 
        }
    }

    public boolean update(Armazem a) {
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            preencherCampos(ps, a, true);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar", e);
            throw new RuntimeException(e);
        }
    }

    public boolean delete(int id) {
        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao eliminar", e);
            throw new RuntimeException(e);
        }
    }

    public List<Armazem> listarTudo() {
        List<Armazem> lista = new ArrayList<>();

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(LISTAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(map(rs));
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar", e);
            throw new RuntimeException(e); 
        }

        return lista;
    }

    private void preencherCampos(PreparedStatement ps, Armazem a, boolean isUpdate) throws SQLException {

        if (a.getDataRegisto() != null)
            ps.setDate(1, new Date(a.getDataRegisto().getTime()));
        else
            ps.setNull(1, Types.DATE);

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
        a.setDataRegisto(rs.getDate("data_registo"));
        a.setPrecoProduto(rs.getDouble("preco_produto"));
        a.setDataCompra(rs.getDate("data_compra"));
        a.setQuantidadeProduto(rs.getInt("quantidade_produto"));
        a.setIdProduto(rs.getInt("id_produto"));

        return a;
    }
}