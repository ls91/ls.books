package ls.books.domain;

public class Book {

    protected int bookId;
    protected String isbn;
    protected String title;
    protected int seriesId;
    protected int noSeries;
    protected int formatId;
    protected int statusId;
    protected int noPages;
    protected String notes;

    public Book() {}
    
    public Book(final int bookId, final String isbn, final String title, final int seriesId, final int noSeries, final int formatId, final int statusId, final int noPages, final String notes) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.seriesId = seriesId;
        this.noSeries = noSeries;
        this.formatId = formatId;
        this.statusId = statusId;
        this.noPages = noPages;
        this.notes = notes;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(final int bookId) {
        this.bookId = bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(final String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public int getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(final int seriesId) {
        this.seriesId = seriesId;
    }
    
    public int getNoSeries() {
        return noSeries;
    }

    public void setNoSeries(final int noSeries) {
        this.noSeries = noSeries;
    }

    public int getFormatId() {
        return formatId;
    }

    public void setFormatId(final int formatId) {
        this.formatId = formatId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(final int statusId) {
        this.statusId = statusId;
    }

    public int getNoPages() {
        return noPages;
    }

    public void setNoPages(final int noPages) {
        this.noPages = noPages;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(final String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Book ID: ");
        builder.append(bookId);
        builder.append("\nISBN: ");
        builder.append(isbn);
        builder.append("\nTitle: ");
        builder.append(title);
        builder.append("\nSeries ID: ");
        builder.append(seriesId);
        builder.append("\nNo. series: ");
        builder.append(noSeries);
        builder.append("\nFormat ID: ");
        builder.append(formatId);
        builder.append("\nStatus ID: ");
        builder.append(statusId);
        builder.append("\nNo. pages: ");
        builder.append(noPages);
        builder.append("\nNotes: ");
        builder.append(notes);
        return builder.toString();
    }

    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof Book) {
            Book that = (Book) other;
            result = (bookId == that.getBookId() && isbn.equals(that.getIsbn()) && title.equals(that.getTitle()) && seriesId == that.getSeriesId() && noSeries == that.getNoSeries() && formatId == that.getFormatId() && statusId == that.getStatusId() && noPages == that.getNoPages() && notes.equals(that.getNotes()));
        }
        return result;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
