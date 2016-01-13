package ls.books.domain;

public class Author {

    private int authorId;
    private String lastName;
    private String firstName;

    public Author() {}

    public Author(final int authorId, final String lastName, final String firstName) {
        this.authorId = authorId;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public int getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(int id) {
        this.authorId = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ID: ");
        builder.append(authorId);
        builder.append("\nLast Name: ");
        builder.append(lastName);
        builder.append("\nFirst Name: ");
        builder.append(firstName);
        return builder.toString();
    }

    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof Author) {
            Author that = (Author) other;
            result = (authorId == that.getAuthorId() && lastName.equals(that.getLastName()) && firstName.equals(that.getFirstName()));
        }
        return result;
        
    }
}
