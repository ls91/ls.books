package ls.books;

import static ls.books.Project.PROJECT_OPTIONS;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.restlet.Component;

import ls.books.dao.CsvReader;
import ls.books.db.SchemaBuilder;

public class Main {

    public static void main(String[] args) throws Exception {
        try {
            CommandLine line = new DefaultParser().parse(PROJECT_OPTIONS, args);
            
            int portNumber = determinePortNumber(line);
            
            if (line.hasOption(Project.HELP_SHORT_ARG)) {
                new Project().displayHelp();
                System.exit(0);
            } else if (line.hasOption(Project.PROJECT_SHORT_ARG)) {
                new Project().displayProjectInfo();
                System.exit(0);
            } else if (line.hasOption(Project.VERSION_SHORT_ARG)) {
                new Project().displayVersion();
                System.exit(0);
            } else if (line.hasOption(Project.BUILD_SHORT_ARG)) {
                buildDatabase(line.getOptionValue(Project.BUILD_SHORT_ARG));
                System.exit(0);
            } else if (line.hasOption(Project.LOAD_SHORT_ARG)) {
                String[] loadArgs = line.getOptionValues(Project.LOAD_SHORT_ARG);
                startup(portNumber, loadArgs[0], loadArgs[1]);
            } else if (line.hasOption(Project.RUN_SHORT_ARG)) {
                startup(portNumber, line.getOptionValue(Project.RUN_SHORT_ARG));
            } else {
                System.out.println("For Usage: java -jar ls.books.jar -h");
            }
            System.exit(0);
        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            System.exit(1);
        }
    }

    protected static int determinePortNumber(final CommandLine line) {
        int portNumber = 80;
        if (line.hasOption(Project.PORT_SHORT_ARG)) {
            portNumber = Integer.parseInt(line.getOptionValue(Project.PORT_SHORT_ARG));
        }
        return portNumber;
    }
    
    protected static void buildDatabase(final String databaseName) throws ClassNotFoundException, SQLException {
        DataSource dataSource = SchemaBuilder.getDataSource(databaseName, "password");
        SchemaBuilder.buildSchema(dataSource);
    }
    
    protected static void startup(final int portNumber, final String databaseName) throws Exception {
        startup(portNumber, databaseName, null);
    }
    
    protected static void startup(final int portNumber, final String databaseName, final String fileName) throws Exception {
        DataSource dataSource = SchemaBuilder.getDataSource(databaseName, "password");
        if (databaseName == null) {
            SchemaBuilder.buildSchema(dataSource);
            SchemaBuilder.populateWithSampleData(dataSource);
        }
        
        Component comp = new WebService(dataSource, portNumber);
        comp.start();
        
        if (fileName != null) {
            new CsvReader(portNumber).populateFromFile(fileName);
        }
        
        System.out.println("Press key to stop server");
        System.in.read();
        System.out.println("Stopping server");
        comp.stop();
        System.out.println("Server stopped");
    }
}
