package ls.books.dao;

import java.util.List;

import ls.books.dao.mapper.StatusMapper;
import ls.books.domain.Status;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface StatusDao {

    public enum ColumnName {
        STATUS_ID,
        NAME
    }

    //Create
    @SqlUpdate("insert into status (name)"
            + " values (:status.name)")
    @GetGeneratedKeys
    int createStatus(@BindBean("status") Status status);

    //Read
    @SqlQuery("select   status_id, "
            + "         name "
            + "from     status "
            + "order by name")
    @Mapper(StatusMapper.class)
    List<Status> getStatuses();

    @SqlQuery("select   status_id, "
            + "         name "
            + "from     status "
            + "where    status_id = :statusId")
    @Mapper(StatusMapper.class)
    Status findStatusById(@Bind("statusId") int statusId);

    //Update
    @SqlUpdate("update  status"
            + " set     name        =   :status.name"
            + " where   status_id   =   :status.statusId")
    void updateStatus(@BindBean("status") Status status);

    //Delete
    @SqlUpdate("delete from status"
            + " where status_id = :statusId")
    void deleteStatusById(@Bind("statusId") int statusId);

    void close();
}
