package ls.books.dao;

import java.util.List;

import ls.books.dao.mapper.FormatMapper;
import ls.books.domain.Format;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface FormatDao {

    public enum ColumnName {
        FORMAT_ID,
        NAME
    }

    //Create
    @SqlUpdate("insert into format (name)"
            + " values (:format.name)")
    @GetGeneratedKeys
    int createFormat(@BindBean("format") Format format);

    //Read
    @SqlQuery("select   format_id, "
            + "         name "
            + "from     format "
            + "order by name")
    @Mapper(FormatMapper.class)
    List<Format> getFormats();

    @SqlQuery("select   format_id, "
            + "         name "
            + "from     format "
            + "where    format_id = :formatId")
    @Mapper(FormatMapper.class)
    Format findFormatById(@Bind("formatId") int formatId);

    //Update
    @SqlUpdate("update  format"
            + " set     name        =   :format.name"
            + " where   format_id   =   :format.formatId")
    void updateFormat(@BindBean("format") Format format);

    //Delete
    @SqlUpdate("delete from format"
            + " where format_id = :formatId")
    void deleteFormatById(@Bind("formatId") int formatId);

    void close();
}
