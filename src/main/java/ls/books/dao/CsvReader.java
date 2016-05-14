package ls.books.dao;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ls.books.domain.Author;
import ls.books.domain.Book;
import ls.books.domain.Format;
import ls.books.domain.Series;
import ls.books.domain.Status;

public class CsvReader {
    private static int ISBN_POSITION = 0;
    private static int TITLE_POSITION = 1;
    private static int FIRSTN_POSITION = 2;
    private static int MIDN_POSITION = 3;
    private static int LASTN_POSITION = 4;
    private static int SERIES_POSITION = 5;
    private static int SERIES_NO_POSITION = 6;
    private static int FORMAT_POSITION = 7;
    private static int PAGES_POSITION = 8;
    private static int STATUS_POSITION = 9;
    private static int NOTES_POSITION = 10;
    
    private static String FORMAT_URL = "http://localhost:%d/rest/format";
    private static String STATUS_URL = "http://localhost:%d/rest/status";
    private static String AUTHOR_URL = "http://localhost:%d/rest/author";
    private static String AUTHOR_SERIES_URL = "http://localhost:%d/rest/author/%d/series";
    private static String SERIES_URL = "http://localhost:%d/rest/series";
    private static String BOOK_URL = "http://localhost:%d/rest/book";
    
    private int port;
    protected Map<Format, Integer> formats = new HashMap<Format, Integer>();
    protected Map<Status, Integer> statuses = new HashMap<Status, Integer>();
    protected Map<Author, Integer> authors = new HashMap<Author, Integer>();
    protected Map<Series, Integer> series = new HashMap<Series, Integer>();
    protected Map<Book, Integer> books = new HashMap<Book, Integer>();
    

    public CsvReader(final int port) {
        this.port = port;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public int getPort() {
        return port;
    }
    
    protected int createFormat(Format format) throws JSONException, ResourceException, IOException {
        if (!formats.containsKey(format)) {
            JSONObject object = new JSONObject();
            object.put("formatId", "0");
            object.put("name", format.getName());
            
            StringRepresentation objectJson = new StringRepresentation(object.toString());
            objectJson.setMediaType(MediaType.APPLICATION_JSON);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            ClientResource resource = new ClientResource(String.format(FORMAT_URL, port));
            resource.post(objectJson).write(baos);
            resource.release();
            
            String result = baos.toString().replaceAll("\"", "");
            baos.reset();
            formats.put(format, Integer.parseInt(result));
        }
        
        return formats.get(format);
    }
    
    protected int createStatus(Status status) throws JSONException, ResourceException, IOException {
        if (!statuses.containsKey(status)) {
            JSONObject object = new JSONObject();
            object.put("statusId", "0");
            object.put("name", status.getName());
            
            StringRepresentation objectJson = new StringRepresentation(object.toString());
            objectJson.setMediaType(MediaType.APPLICATION_JSON);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            ClientResource resource = new ClientResource(String.format(STATUS_URL, port));
            resource.post(objectJson).write(baos);
            resource.release();
            
            String result = baos.toString().replaceAll("\"", "");
            baos.reset();
            statuses.put(status, Integer.parseInt(result));
        }
        
        return statuses.get(status);
    }
    
    protected int createAuthor(Author author) throws JSONException, ResourceException, IOException {
        if (!authors.containsKey(author)) {
            JSONObject object = new JSONObject();
            object.put("authorId", "0");
            object.put("lastName", author.getLastName());
            object.put("firstName", author.getFirstName());
            
            StringRepresentation objectJson = new StringRepresentation(object.toString());
            objectJson.setMediaType(MediaType.APPLICATION_JSON);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            ClientResource resource = new ClientResource(String.format(AUTHOR_URL, port));
            resource.post(objectJson).write(baos);
            resource.release();
            
            String result = baos.toString().replaceAll("\"", "");
            baos.reset();
            int id = Integer.parseInt(result);
            
            
            resource = new ClientResource(String.format(AUTHOR_SERIES_URL, port, id));
            resource.get().write(baos);
            resource.release();
            
            List<Series> listOfSeries = new Gson().fromJson(baos.toString(), new TypeToken<List<Series>>() {}.getType());
            baos.reset();
            Series blankSeries = listOfSeries.get(0);
            int seriesId = blankSeries.getSeriesId();
            blankSeries.setSeriesId(0);
            
            series.put(blankSeries, seriesId);
            authors.put(author, id);
        }
        
        return authors.get(author);
    }
    
    protected int createSeries(Series newSeries) throws JSONException, ResourceException, IOException {
        if (!series.containsKey(newSeries)) {
            JSONObject object = new JSONObject();
            object.put("seriesId", "0");
            object.put("authorId", newSeries.getAuthorId());
            object.put("seriesName", newSeries.getSeriesName());
            object.put("description", newSeries.getDescription());
            
            StringRepresentation objectJson = new StringRepresentation(object.toString());
            objectJson.setMediaType(MediaType.APPLICATION_JSON);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            ClientResource resource = new ClientResource(String.format(SERIES_URL, port));
            resource.post(objectJson).write(baos);
            resource.release();
            
            String result = baos.toString().replaceAll("\"", "");
            baos.reset();
            int id = Integer.parseInt(result);
            
            series.put(newSeries, id);
        }
        
        return series.get(newSeries);
    }
    
    protected int createBook(Book book) throws JSONException, ResourceException, IOException {
        if (!books.containsKey(book)) {
            JSONObject object = new JSONObject();
            object.put("bookId", "0");
            object.put("isbn", book.getIsbn());
            object.put("title", book.getTitle());
            object.put("seriesId", Integer.toString(book.getSeriesId()));
            object.put("noSeries", Integer.toString(book.getNoSeries()));
            object.put("formatId", Integer.toString(book.getFormatId()));
            object.put("statusId", Integer.toString(book.getStatusId()));
            object.put("noPages", Integer.toString(book.getNoPages()));
            object.put("notes", book.getNotes());
            
            StringRepresentation objectJson = new StringRepresentation(object.toString());
            objectJson.setMediaType(MediaType.APPLICATION_JSON);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            ClientResource resource = new ClientResource(String.format(BOOK_URL, port));
            resource.post(objectJson).write(baos);
            resource.release();
            
            String result = baos.toString().replaceAll("\"", "");
            baos.reset();
            int id = Integer.parseInt(result);
            
            books.put(book, id);
        }
        
        return books.get(book);
    }
    
    public void populateFromFile(String fileName) throws ResourceException, JSONException, IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {
            for(String line; (line = br.readLine()) != null; ) {
                String[] lineParts = line.split(",");
                
                int formatId = createFormat(new Format(0, lineParts[FORMAT_POSITION].replaceAll("\"", "")));
                int statusId = createStatus(new Status(0, lineParts[STATUS_POSITION].replaceAll("\"", "")));
                int authorId = createAuthor(new Author(0, (lineParts[MIDN_POSITION].replaceAll("\"", "") + " " + lineParts[LASTN_POSITION].replaceAll("\"", "")).trim(), lineParts[FIRSTN_POSITION].replaceAll("\"", "")));
                int seriesId = createSeries(new Series(0, authorId, lineParts[SERIES_POSITION].replaceAll("\"", ""), ""));
                createBook(new Book(0
                                   ,lineParts[ISBN_POSITION].replaceAll("\"", "")
                                   ,lineParts[TITLE_POSITION].replaceAll("\"", "")
                                   ,seriesId
                                   ,Integer.parseInt(lineParts[SERIES_NO_POSITION].replaceAll("\"", ""))
                                   ,formatId
                                   ,statusId
                                   ,Integer.parseInt(lineParts[PAGES_POSITION].replaceAll("\"", ""))
                                   ,lineParts[NOTES_POSITION].replaceAll("\"", "")));
            }
        }
    }
}
