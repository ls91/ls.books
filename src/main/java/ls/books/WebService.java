package ls.books;

import javax.sql.DataSource;

import org.restlet.Component;
import org.restlet.data.Protocol;

public class WebService extends Component {

    public WebService(final DataSource dataSource, final int portNumber) {
        super();
        getServers().add(Protocol.HTTP, portNumber);
        getDefaultHost().attach(new WebServicesApplication(getContext(), dataSource));
    }
}
