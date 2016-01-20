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
            PreparedStatement ps = connection.prepareStatement("INSERT INTO AUTHOR (LAST_NAME, FIRST_NAME) VALUES (?, ?)");
            ps.setString(1, "South");
            ps.setString(2, "Luke");
            ps.execute();
            ps.close();
            connection.close();
            
            connection = dataSource.getConnection();
            ps = connection.prepareStatement("INSERT INTO AUTHOR (LAST_NAME, FIRST_NAME) VALUES (?, ?)");
            ps.setString(1, "South");
            ps.setString(2, "Jo");
            ps.execute();
            ps.close();
            connection.close();
            
            connection = dataSource.getConnection();
            ps = connection.prepareStatement("INSERT INTO AUTHOR (LAST_NAME, FIRST_NAME) VALUES (?, ?)");
            ps.setString(1, "Cussler");
            ps.setString(2, "Clive");
            ps.execute();
            ps.close();
            connection.close();
            
            //Series
            connection = dataSource.getConnection();
            ps = connection.prepareStatement("INSERT INTO SERIES (AUTHOR_ID, SERIES_NAME, DESCRIPTION) VALUES (?, ?, ?)");
            ps.setInt(1, 3);
            ps.setString(2, "Dirk Pitt");
            ps.setString(3, "A bunch of novels");
            ps.execute();
            ps.close();
            connection.close();
            
            connection = dataSource.getConnection();
            ps = connection.prepareStatement("INSERT INTO SERIES (AUTHOR_ID, SERIES_NAME, DESCRIPTION) VALUES (?, ?, ?)");
            ps.setInt(1, 3);
            ps.setString(2, "Isaac Bell");
            ps.setString(3, "A few more novels");
            ps.execute();
            ps.close();
            connection.close();
            
            connection = dataSource.getConnection();
            ps = connection.prepareStatement("INSERT INTO SERIES (AUTHOR_ID, SERIES_NAME, DESCRIPTION) VALUES (?, ?, ?)");
            ps.setInt(1, 1);
            ps.setString(2, "Luke's Books");
            ps.setString(3, "");
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
