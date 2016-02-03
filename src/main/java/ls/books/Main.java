package ls.books;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import ls.books.db.SchemaBuilder;

import org.restlet.Component;

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
        
        connection = dataSource.getConnection();
        ps = connection.prepareStatement("INSERT INTO FORMAT (FORMAT_ID, NAME) VALUES (?, ?)");
        ps.setInt(1, 1);
        ps.setString(2, "Paperback");
        ps.execute();
        ps.close();
        connection.close();
        
        connection = dataSource.getConnection();
        ps = connection.prepareStatement("INSERT INTO FORMAT (FORMAT_ID, NAME) VALUES (?, ?)");
        ps.setInt(1, 2);
        ps.setString(2, "Hardback");
        ps.execute();
        ps.close();
        connection.close();
        
        connection = dataSource.getConnection();
        ps = connection.prepareStatement("INSERT INTO BOOK (ISBN, TITLE, SERIES_ID, NO_SERIES, FORMAT_ID, NO_PAGES, NOTES) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)");
        ps.setInt(1, 14353452);
        ps.setString(2, "Havana Storm");
        ps.setInt(3, 1);
        ps.setInt(4, 23);
        ps.setInt(5, 2);
        ps.setInt(6, 125);
        ps.setString(7, "hiijisadasd");
        ps.execute();
        ps.close();
        connection.close();
        
        connection = dataSource.getConnection();
        ps = connection.prepareStatement("INSERT INTO BOOK (ISBN, TITLE, SERIES_ID, NO_SERIES, FORMAT_ID, NO_PAGES, NOTES) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)");
        ps.setInt(1, 87879234);
        ps.setString(2, "Arctic Drift");
        ps.setInt(3, 1);
        ps.setInt(4, 22);
        ps.setInt(5, 1);
        ps.setInt(6, 300);
        ps.setString(7, "hiijisadasd");
        ps.execute();
        ps.close();
        connection.close();
        
        connection = dataSource.getConnection();
        ps = connection.prepareStatement("INSERT INTO BOOK (ISBN, TITLE, SERIES_ID, NO_SERIES, FORMAT_ID, NO_PAGES, NOTES) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)");
        ps.setInt(1, 234324);
        ps.setString(2, "Bioshock: Rapture");
        ps.setInt(3, 2);
        ps.setInt(4, 0);
        ps.setInt(5, 1);
        ps.setInt(6, 125);
        ps.setString(7, "hiijisadasd");
        ps.execute();
        ps.close();
        connection.close();
        
        
        Component comp = new WebService(dataSource, 8182);
        comp.start();

        System.out.println("Press key to stop server");
        System.in.read();
        System.out.println("Stopping server");
        comp.stop();
        System.out.println("Server stopped");
    }
}
