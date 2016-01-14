package ls.books;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import ls.books.db.SchemaBuilder;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;

public class Main {
        
        public static void main(String[] args) throws Exception {
            DataSource dataSource = SchemaBuilder.buildSchema("jdbc:h2:mem:ls-books;DB_CLOSE_DELAY=-1", "password");
            
            Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO AUTHORS (LAST_NAME, FIRST_NAME) VALUES (?, ?)");
            ps.setString(1, "SOUTH");
            ps.setString(2, "LUKE");
            ps.execute();
            ps.close();
            connection.close();
            
            connection = dataSource.getConnection();
            ps = connection.prepareStatement("INSERT INTO AUTHORS (LAST_NAME, FIRST_NAME) VALUES (?, ?)");
            ps.setString(1, "SOUTH");
            ps.setString(2, "JO");
            ps.execute();
            ps.close();
            connection.close();
            
            connection = dataSource.getConnection();
            ps = connection.prepareStatement("INSERT INTO AUTHORS (LAST_NAME, FIRST_NAME) VALUES (?, ?)");
            ps.setString(1, "CUSSLER");
            ps.setString(2, "CLIVE");
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
