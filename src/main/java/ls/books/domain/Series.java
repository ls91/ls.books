package ls.books.domain;

public class Series {

    private int seriesId;
    private int authorId;
    private String seriesName;
    private String description;

    public Series(final int seriesId, final int authorId, final String seriesName, final String description) {
        this.seriesId = seriesId;
        this.authorId = authorId;
        this.seriesName = seriesName;
        this.description = description;
    }

    public int getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(final int seriesId) {
        this.seriesId = seriesId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(final int authorId) {
        this.authorId = authorId;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(final String seriesName) {
        this.seriesName = seriesName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Series ID: ");
        builder.append(seriesId);
        builder.append("\nAuthor ID: ");
        builder.append(authorId);
        builder.append("\nSeries Name: ");
        builder.append(seriesName);
        builder.append("\nDescription: ");
        builder.append(description);
        return builder.toString();
    }

    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof Series) {
            Series that = (Series) other;
            result = (seriesId == that.getSeriesId() && authorId == that.getAuthorId() && seriesName.equals(that.getSeriesName()) && description.equals(that.getDescription()));
        }
        return result;
    }
}
