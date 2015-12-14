package ls.books.domain;

public class Author {

    private int id;
    private String lastName;
    private String firstName;

    public Author(final int id, final String lastName, final String firstName) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
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
        builder.append(id);
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
            result = (id == that.getId() && lastName == that.getLastName() && firstName == that.getFirstName());
        }
        return result;
        
    }
}
