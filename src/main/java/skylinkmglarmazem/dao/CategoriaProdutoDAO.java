package skylinkmglarmazem.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import skylink.mglarmazem.bdutil.ConnectionDB;
import skylink.mglarmazem.modelo.CategoriaProduto;

/**
 * @author Henriques
 */
public class CategoriaProdutoDAO {

    public List<CategoriaProduto> listarTudo() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<CategoriaProduto> lista = new ArrayList<>();

        try {
            conn = ConnectionDB.getConnection();
            ps = conn.prepareStatement("SELECT * FROM categoria_produto ORDER BY descricao_categoria");
            rs = ps.executeQuery();

            while (rs.next()) {
                CategoriaProduto cat = new CategoriaProduto();
                cat.setIdCategoria(rs.getInt("id_categoria"));
                cat.setDescricaoCategoria(rs.getString("descricao_categoria"));
                lista.add(cat);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar categorias: " + e.getMessage());
        } finally {
            ConnectionDB.closeConnection(conn, ps, rs);
        }
        return lista;
    }
}
