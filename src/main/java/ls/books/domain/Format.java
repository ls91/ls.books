package ls.books.domain;

public class Format {

    private int formatId;
    private String name;

    public Format() {}
    
    public Format(final int formatId, final String name) {
        this.formatId = formatId;
        this.name = name;
    }

    public int getFormatId() {
        return formatId;
    }

    public void setFormatId(final int formatId) {
        this.formatId = formatId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Format ID: ");
        stringBuilder.append(formatId);
        stringBuilder.append("\nName: ");
        stringBuilder.append(name);
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof Format) {
            Format that = (Format) other;
            result = (formatId == that.getFormatId() && name.equals(that.getName()));
        }
        return result;
    }
}
