package ls.books;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Manifest;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class Project {
    public static final String HELP_SHORT_ARG = "h";
    public static final String HELP_LONG_ARG = "help";
    
    public static final String PROJECT_SHORT_ARG = "p";
    public static final String PROJECT_LONG_ARG = "projecthelp";
    
    public static final String VERSION_SHORT_ARG = "v";
    public static final String VERSION_LONG_ARG = "version";
    
    public static final String PORT_SHORT_ARG = "P";
    public static final String PORT_LONG_ARG = "port";

    public static final String BUILD_SHORT_ARG = "b";
    public static final String BUILD_LONG_ARG = "build-db";
    
    public static final String RUN_SHORT_ARG = "r";
    public static final String RUN_LONG_ARG = "run";
    
    public static final String LOAD_SHORT_ARG = "l";
    public static final String LOAD_LONG_ARG = "load";
    
    public static final Options PROJECT_OPTIONS = new Options(); 

    static {
        PROJECT_OPTIONS.addOption(HELP_SHORT_ARG, HELP_LONG_ARG, false, "Print this message");
        PROJECT_OPTIONS.addOption(PROJECT_SHORT_ARG, PROJECT_LONG_ARG, false, "Print project help information and exit");
        PROJECT_OPTIONS.addOption(VERSION_SHORT_ARG, VERSION_LONG_ARG, false, "Print the version information and exit");
        PROJECT_OPTIONS.addOption(BUILD_SHORT_ARG, BUILD_LONG_ARG, true, "Builds a database using the name provided. This will not start the application but immediately after, the database can be used with the '" + RUN_LONG_ARG + "' argument");
        PROJECT_OPTIONS.getOption(BUILD_SHORT_ARG).setArgName("databaseName");
        
        PROJECT_OPTIONS.addOption(LOAD_SHORT_ARG, LOAD_LONG_ARG, true, "Loads data into the database from a CSV file");
        PROJECT_OPTIONS.getOption(LOAD_SHORT_ARG).setArgs(2);
        PROJECT_OPTIONS.getOption(LOAD_SHORT_ARG).setArgName("databaseName> <csvFile");
        
        PROJECT_OPTIONS.addOption(PORT_SHORT_ARG, PORT_LONG_ARG, true, "The port to listen on, defaults to 80");
        PROJECT_OPTIONS.getOption(PORT_SHORT_ARG).setOptionalArg(true);
        PROJECT_OPTIONS.getOption(PORT_SHORT_ARG).setArgName("portNumber");
        
        PROJECT_OPTIONS.addOption(RUN_SHORT_ARG, RUN_LONG_ARG, true, "Runs the application, if no argument is supplied it will run in demo mode (i.e. no persistance), otherwise it will attempt to use the supplied database");
        PROJECT_OPTIONS.getOption(RUN_SHORT_ARG).setOptionalArg(true);
        PROJECT_OPTIONS.getOption(RUN_SHORT_ARG).setArgName("databaseName");
    }

    private Manifest manifest;

    public Project() {
        URLClassLoader urlClassLoader = (URLClassLoader) getClass().getClassLoader();
        try {
            URL manifestUrl = urlClassLoader.findResource("META-INF/MANIFEST.MF");
            manifest = new Manifest(manifestUrl.openStream());
          } catch (IOException e) {
              e.printStackTrace();
          }
    }

    public void displayHelp() {
        new HelpFormatter().printHelp("ls.books.jar", PROJECT_OPTIONS, true);
    }

    public void displayVersion() {
        System.out.println(getVersion());
    }

    public String getVersion() {
        StringBuilder versionBuilder = new StringBuilder("ls.books - Version: ");
        versionBuilder.append(manifest.getMainAttributes().getValue("Implementation-Version"));
        return versionBuilder.toString();
    }

    public void displayProjectInfo() {
        System.out.println(getProjectInfo());
    }

    public String getProjectInfo() {
        StringBuilder projectInfoBuilder = new StringBuilder("ls.books - Project Info\n\n");
        projectInfoBuilder.append("URL: ");
        projectInfoBuilder.append(manifest.getMainAttributes().getValue("Project-URL"));
        return projectInfoBuilder.toString();
    }
}
