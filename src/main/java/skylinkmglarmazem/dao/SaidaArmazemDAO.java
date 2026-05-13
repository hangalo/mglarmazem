package skylinkmglarmazem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import skylink.mglarmazem.bdutil.ConnectionDB;
import skylink.mglarmazem.modelo.SaidaArmazem;

/**
 * @author Henriques
 */
public class SaidaArmazemDAO {

    private static final Logger LOGGER = Logger.getLogger(SaidaArmazemDAO.class.getName());

    private static final String INSERT = "INSERT INTO saida_armazem (id_produto, id_sector, quantidade_saida_armazem, data_saida_armazem) VALUES (?, ?, ?, ?)";
    
    private static final String UPDATE_ESTOQUE = "UPDATE produto SET quantidade_existente = quantidade_existente - ? WHERE id_produto = ? AND quantidade_existente >= ?";

    public boolean registrarSaida(SaidaArmazem saida) {
        Connection conn = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psInsert = null;

        try {
            conn = ConnectionDB.getConnection();
            conn.setAutoCommit(false); 

            psUpdate = conn.prepareStatement(UPDATE_ESTOQUE);
            psUpdate.setInt(1, saida.getQuantidadeSaidaArmazem());
            psUpdate.setInt(2, saida.getIdProduto());
            psUpdate.setInt(3, saida.getQuantidadeSaidaArmazem()); 
            
            int linhasAfetadasEstoque = psUpdate.executeUpdate();

            if (linhasAfetadasEstoque == 0) {

                throw new SQLException("Estoque insuficiente ou produto não encontrado!");
            }

            psInsert = conn.prepareStatement(INSERT);
            psInsert.setInt(1, saida.getIdProduto());
            psInsert.setInt(2, saida.getIdSector());
            psInsert.setInt(3, saida.getQuantidadeSaidaArmazem());
            psInsert.setDate(4, new java.sql.Date(saida.getDataSaidaArmazem().getTime()));
            
            psInsert.executeUpdate();

            conn.commit(); 
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); 
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Erro ao fazer rollback", ex);
                }
            }
            LOGGER.log(Level.SEVERE, "Erro ao registrar saída: " + e.getMessage());
            return false;
        } finally {

            fecharRecursos(psUpdate, psInsert, conn);
        }
    }

    
    private void fecharRecursos(PreparedStatement ps1, PreparedStatement ps2, Connection conn) {
        try {
            if (ps1 != null) ps1.close();
            if (ps2 != null) ps2.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao fechar recursos", e);
        }
    }
}