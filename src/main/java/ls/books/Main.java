package ls.books;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;

public class Main {
        
        public static void main(String[] args) throws Exception {
            DataSource dataSource = new JdbcDataSource();
            ((JdbcDataSource) dataSource).setURL("jdbc:h2:mem:ls-books;DB_CLOSE_DELAY=-1");
            ((JdbcDataSource) dataSource).setUser("books");
            ((JdbcDataSource) dataSource).setPassword("books");
            
            Connection connection = dataSource.getConnection();
            Statement createTable = connection.createStatement();
            createTable.execute(
                    "CREATE TABLE AUTHORS ("
                    + "ID NUMBER,"
                    + "LAST_NAME VARCHAR(500),"
                    + "FIRST_NAME VARCHAR(500))");
            createTable.close();
            connection.close();
            
            
            connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO AUTHORS VALUES (?, ?, ?)");
            ps.setInt(1, 1);
            ps.setString(2, "SOUTH");
            ps.setString(3, "LUKE");
            ps.execute();
            ps.close();
            connection.close();
            
            connection = dataSource.getConnection();
            ps = connection.prepareStatement("INSERT INTO AUTHORS VALUES (?, ?, ?)");
            ps.setInt(1, 2);
            ps.setString(2, "SOUTH");
            ps.setString(3, "JO");
            ps.execute();
            ps.close();
            connection.close();
            
            connection = dataSource.getConnection();
            ps = connection.prepareStatement("INSERT INTO AUTHORS VALUES (?, ?, ?)");
            ps.setInt(1, 3);
            ps.setString(2, "CUSSLER");
            ps.setString(3, "CLIVE");
            ps.execute();
            ps.close();
            connection.close();
            
            
            Component comp = new Component();
            Server server = comp.getServers().add(Protocol.HTTP, 8182);

            WebServicesApplication application = new WebServicesApplication(comp.getContext(), dataSource);

            comp.getDefaultHost().attach(application);
            comp.start();

            System.out.println("Server started on port " + server.getPort());
            System.out.println("Press key to stop server");
            System.in.read();
            System.out.println("Stopping server");
            comp.stop();
            System.out.println("Server stopped");
        }
}
