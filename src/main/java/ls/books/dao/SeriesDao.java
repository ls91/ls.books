package ls.books.dao;

import java.util.List;

import ls.books.dao.mapper.SeriesMapper;
import ls.books.domain.Series;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface SeriesDao {

    public enum ColumnName {
        SERIES_ID,
        AUTHOR_ID,
        SERIES_NAME,
        DESCRIPTION
    }

    //Create
    @SqlUpdate("insert into series (author_id, series_name, description)"
            + " values (:series.authorId, :series.seriesName, :series.description)")
    @GetGeneratedKeys
    int createSeries(@BindBean("series") Series series);

    //Read
    @SqlQuery("select   series_id, "
            + "         author_id, "
            + "         series_name, "
            + "         description "
            + "from     series "
            + "order by series_name")
    @Mapper(SeriesMapper.class)
    List<Series> getSeries();

    @SqlQuery("select   series_id, "
            + "         author_id, "
            + "         series_name, "
            + "         description "
            + "from     series "
            + "where    series_id = :seriesId")
    @Mapper(SeriesMapper.class)
    Series findSeriesBySeriesId(@Bind("seriesId") int seriesId);

    @SqlQuery("select   series_id, "
            + "         author_id, "
            + "         series_name, "
            + "         description "
            + "from     series "
            + "where    author_id = :authorId "
            + "order by series_name")
    @Mapper(SeriesMapper.class)
    List<Series> findSeriesByAuthorId(@Bind("authorId") int authorId);

    @SqlQuery("select   series_id, "
            + "         author_id, "
            + "         series_name, "
            + "         description "
            + "from     series "
            + "where    lower(series_name)  like    concat('%', lower(:query), '%') "
            + "or       lower(description)  like    concat('%', lower(:query), '%') ")
    @Mapper(SeriesMapper.class)
    List<Series> findSeriesByNameAndDescriptionLike(@Bind("query") String query);

    //Update
    @SqlUpdate("update  series"
            + " set     author_id   =   :series.authorId"
            + " ,       series_name =   :series.seriesName"
            + " ,       description =   :series.description"
            + " where   series_id   =   :series.seriesId")
    void updateSeries(@BindBean("series") Series series);

    //Delete
    @SqlUpdate("delete from series"
            + " where series_id = :seriesId")
    void deleteSeriesById(@Bind("seriesId") int seriesId);

    void close();
}
