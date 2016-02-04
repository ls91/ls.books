package ls.books;

import javax.sql.DataSource;

import ls.books.db.SchemaBuilder;

import org.restlet.Component;

public class Main {

    public static void main(String[] args) throws Exception {
        DataSource dataSource = SchemaBuilder.buildSchema("jdbc:h2:mem:ls-books;DB_CLOSE_DELAY=-1", "password");
        SchemaBuilder.populateWithSampleData(dataSource);

        Component comp = new WebService(dataSource, 8182);
        comp.start();

        System.out.println("Press key to stop server");
        System.in.read();
        System.out.println("Stopping server");
        comp.stop();
        System.out.println("Server stopped");
    }
}
