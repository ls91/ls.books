package ls.books;

import static ls.books.Project.PROJECT_OPTIONS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ProjectTest {

    @Test
    public void helpOptionAttributes() {
        assertTrue(PROJECT_OPTIONS.hasOption("h"));
        assertTrue(PROJECT_OPTIONS.hasOption("help"));
        assertFalse(PROJECT_OPTIONS.getOption("help").hasArg());
        assertEquals("Print this message", PROJECT_OPTIONS.getOption("help").getDescription());
    }
    
    @Test
    public void projectHelpOptionAttributes() {
        assertTrue(PROJECT_OPTIONS.hasOption("p"));
        assertTrue(PROJECT_OPTIONS.hasOption("projecthelp"));
        assertFalse(PROJECT_OPTIONS.getOption("projecthelp").hasArg());
        assertEquals("Print project help information and exit", PROJECT_OPTIONS.getOption("projecthelp").getDescription());
    }
    
    @Test
    public void versionOptionAttributes() {
        assertTrue(PROJECT_OPTIONS.hasOption("v"));
        assertTrue(PROJECT_OPTIONS.hasOption("version"));
        assertFalse(PROJECT_OPTIONS.getOption("version").hasArg());
        assertEquals("Print the version information and exit", PROJECT_OPTIONS.getOption("version").getDescription());
    }
    
    @Test
    public void portOptionAttributes() {
        assertTrue(PROJECT_OPTIONS.hasOption("P"));
        assertTrue(PROJECT_OPTIONS.hasOption("port"));
        assertTrue(PROJECT_OPTIONS.getOption("port").hasOptionalArg());
        assertEquals("portNumber", PROJECT_OPTIONS.getOption("port").getArgName());
        assertEquals("The port to listen on, defaults to 80", PROJECT_OPTIONS.getOption("port").getDescription());
    }
    
    @Test
    public void buildDatabaseOptionAttributes() {
        assertTrue(PROJECT_OPTIONS.hasOption("b"));
        assertTrue(PROJECT_OPTIONS.hasOption("build-db"));
        assertTrue(PROJECT_OPTIONS.getOption("build-db").hasArg());
        assertEquals("databaseName", PROJECT_OPTIONS.getOption("build-db").getArgName());
        assertEquals("Builds a database using the name provided. This will not start the application but immediately after, the database can be used with the '" + Project.RUN_LONG_ARG + "' argument", PROJECT_OPTIONS.getOption("build-db").getDescription());
    }
    
    @Test
    public void runOptionAttributes() {
        assertTrue(PROJECT_OPTIONS.hasOption("r"));
        assertTrue(PROJECT_OPTIONS.hasOption("run"));
        assertTrue(PROJECT_OPTIONS.getOption("run").hasOptionalArg());
        assertEquals("databaseName", PROJECT_OPTIONS.getOption("run").getArgName());
        assertEquals("Runs the application, if no argument is supplied it will run in demo mode (i.e. no persistance), otherwise it will attempt to use the supplied database", PROJECT_OPTIONS.getOption("run").getDescription());
    }
    
    @Test
    public void loadOptionAttributes() {
        assertTrue(PROJECT_OPTIONS.hasOption("l"));
        assertTrue(PROJECT_OPTIONS.hasOption("load"));
        assertTrue(PROJECT_OPTIONS.getOption("load").hasArg());
        assertEquals("databaseName> <csvFile", PROJECT_OPTIONS.getOption("load").getArgName());
        assertEquals("Loads data into the database from a CSV file", PROJECT_OPTIONS.getOption("load").getDescription());
    }
    
}
