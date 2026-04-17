
package skylink.mglarmazem.bdutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @Henriques
 */
public class ConnectionDB {
    
     private static final String URL = "jdbc:mysql://127.0.0.1:3306/mglarmazem?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DRIVERMYSQL8 = "com.mysql.cj.jdbc.Driver";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    
    
   public static Connection getConnection() {
        Connection con;
      
        try {
            Class.forName(DRIVERMYSQL8);
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println("Erro na conexao com a base de dados: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
        return con;
    }

    public static void closeConnection(Connection conn){
        close(conn, null, null);    
    }
    
     public static void closeConnection(Connection conn, PreparedStatement ps){
        close(conn, ps, null);    
    }
     
      public static void closeConnection(Connection conn, PreparedStatement ps, ResultSet rs){
        close(conn, ps, rs);    
    }
    
    
    private static void close(Connection conn, PreparedStatement ps, ResultSet rs) {

        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }

        } catch (SQLException ex) {
            System.err.println("Erro ao desalocar recurso: "+ex.getMessage());
        }

    }

    
    
    
}