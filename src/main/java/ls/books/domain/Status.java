package ls.books.domain;

public class Status {

    private int statusId;
    private String name;

    public Status() {}
    
    public Status(final int statusId, final String name) {
        this.statusId = statusId;
        this.name = name;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(final int statusId) {
        this.statusId = statusId;
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
        stringBuilder.append("Status ID: ");
        stringBuilder.append(statusId);
        stringBuilder.append("\nName: ");
        stringBuilder.append(name);
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof Status) {
            Status that = (Status) other;
            result = (statusId == that.getStatusId() && name.equals(that.getName()));
        }
        return result;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
