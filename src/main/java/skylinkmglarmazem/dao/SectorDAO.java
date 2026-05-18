package skylinkmglarmazem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import skylink.mglarmazem.bdutil.ConnectionDB;
import skylink.armazem.modelo.Sector;

/**
 * @author Henriques
 */
public class SectorDAO {

    public List<Sector> listarTudo() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Sector> lista = new ArrayList<>();
        

        String sql = "SELECT id_sector, descricao_sector FROM sector ORDER BY descricao_sector";
        
        try {
            conn = ConnectionDB.getConnection();
            ps = conn.prepareStatement(sql); 
            rs = ps.executeQuery();

            while (rs.next()) {
                Sector set = new Sector();
                set.setIdSector(rs.getInt("id_sector"));
                
                set.setDescricaoSector(rs.getString("descricao_sector")); 
                
                lista.add(set);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar sectores: " + e.getMessage());
        } finally {
            ConnectionDB.closeConnection(conn, ps, rs);
        }
        return lista;
    }
}