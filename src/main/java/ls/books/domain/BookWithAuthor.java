package ls.books.domain;

public class BookWithAuthor extends Book {

    private int authorId;

    public BookWithAuthor() {}
    
    public BookWithAuthor(final int bookId, final String isbn, final String title, final int seriesId, final int noSeries, final int formatId, final int statusId, final int noPages, final String notes, final int authorId) {
        super(bookId, isbn, title, seriesId, noSeries, formatId, statusId, noPages, notes);
        this.authorId = authorId;
    }
    
    public BookWithAuthor(final Book book, final int authorId) {
        super(book.getBookId(), book.getIsbn(), book.getTitle(), book.getSeriesId(), book.getNoSeries(), book.getFormatId(), book.getStatusId(), book.getNoPages(), book.getNotes());
        this.authorId = authorId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(final int authorId) {
        this.authorId = authorId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append("\nAuthor ID: ");
        builder.append(authorId);
        return builder.toString();
    }

    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof BookWithAuthor) {
            BookWithAuthor that = (BookWithAuthor) other;
            result = (bookId == that.getBookId() && isbn.equals(that.getIsbn()) && title.equals(that.getTitle()) && authorId == that.getAuthorId() && seriesId == that.getSeriesId() && noSeries == that.getNoSeries() && formatId == that.getFormatId() && statusId == that.getStatusId() && noPages == that.getNoPages() && notes.equals(that.getNotes()));
        }
        return result;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
