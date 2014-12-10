package starnubserver.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import starnubserver.StarNub;
import starnubserver.logger.MultiOutputLogger;

import java.sql.SQLException;
import java.util.List;

public abstract class TableWrapper<T1, T2> {

    private Dao<T1, T2> tableDao;
    final Class<T1> typeParameterDBClass;
    final Class<T2> typeParameterIDClass;

    public TableWrapper(Class<T1> typeParameterDBClass, Class<T2> typeParameterIDClass) {
        this.typeParameterDBClass = typeParameterDBClass;
        this.typeParameterIDClass = typeParameterIDClass;
    }

    public TableWrapper(ConnectionSource connectionSource, int oldVersion, Class<T1> typeParameterDBClass, Class<T2> typeParameterIDClass) {
        this.typeParameterDBClass = typeParameterDBClass;
        this.typeParameterIDClass = typeParameterIDClass;
        setTableWrapper(connectionSource, oldVersion);
    }

    public Dao<T1, T2> getTableDao() {
        return tableDao;
    }

    public void setTableDao(Dao<T1, T2> tableDao) {
        this.tableDao = tableDao;
    }

    public Class<T1> getTypeParameterDBClass() {
        return typeParameterDBClass;
    }

    public Class<T2> getTypeParameterIDClass() {
        return typeParameterIDClass;
    }

    @SuppressWarnings("unchecked")
    public void setTableWrapper(ConnectionSource connection, int oldVersion){
        if (tableDao == null) {
            try {
                tableDao = DaoManager.createDao(connection, typeParameterDBClass);
            } catch (SQLException e) {
                MultiOutputLogger logger = StarNub.getLogger();
                if (logger != null) {
                    logger.cFatPrint("StarNub", ExceptionUtils.getMessage(e));
                } else {
                    e.printStackTrace();
                }
            }
        }
        try {
            if (!tableDao.isTableExists()) {
                TableUtils.createTableIfNotExists(connection, typeParameterDBClass);
            } else {
                tableUpdater(connection, oldVersion);
            }
        } catch (SQLException e){
            MultiOutputLogger logger = StarNub.getLogger();
            if (logger != null) {
                logger.cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            } else {
                e.printStackTrace();
            }
        }
    }

    public abstract void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException;

    public void updateTable(String query){
        try {
            getTableDao().executeRaw(query);
        } catch (SQLException e) {
            if (ExceptionUtils.getRootCauseMessage(e).contains("Duplicate column name")){
                /*  We don't care if it was a duplicate column so we will ignore this StackTrace */
                return;
            } else {
                MultiOutputLogger logger = StarNub.getLogger();
                if (logger != null) {
                    logger.cFatPrint("StarNub", ExceptionUtils.getMessage(e));
                } else {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean createIfNotExist(T1 persistedClass){
        try {
            tableDao.createIfNotExists(persistedClass);
            return true;
        } catch (SQLException e) {
            MultiOutputLogger logger = StarNub.getLogger();
            if (logger != null) {
                logger.cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean create(T1 persistedClass){
        try {
            tableDao.create(persistedClass);
            return true;
        } catch (SQLException e) {
            MultiOutputLogger logger = StarNub.getLogger();
            if (logger != null) {
                logger.cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean createOrUpdate(T1 persistedClass){
        try {
            tableDao.createOrUpdate(persistedClass);
            return true;
        } catch (SQLException e) {
            MultiOutputLogger logger = StarNub.getLogger();
            if (logger != null) {
                logger.cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean update(T1 persistedClass){
        try {
            tableDao.update(persistedClass);
            return true;
        } catch (SQLException e) {
            MultiOutputLogger logger = StarNub.getLogger();
            if (logger != null) {
                logger.cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean refresh(T1 persistedClass){
        try {
            tableDao.refresh(persistedClass);
            return true;
        } catch (SQLException e) {
            MultiOutputLogger logger = StarNub.getLogger();
            if (logger != null) {
                logger.cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean delete(T1 persistedClass){
        try {
            tableDao.delete(persistedClass);
            return true;
        } catch (SQLException e) {
            MultiOutputLogger logger = StarNub.getLogger();
            if (logger != null) {
                logger.cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    public T1 getById(T2 id) {
        try {
            return getTableDao().queryForId(id);
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }

    public T1 getFirstExact(String columnName, Object searchTerm) {
        try {
            QueryBuilder<T1, T2> queryBuilder =
                    getTableDao().queryBuilder();
            queryBuilder.where()
                    .eq(columnName, searchTerm);
            PreparedQuery<T1> preparedQuery = queryBuilder.prepare();
            return getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }

    public T1 getFirstSimilar(String columnName, Object searchTerm) {
        try {
            QueryBuilder<T1, T2> queryBuilder =
                    getTableDao().queryBuilder();
            queryBuilder.where()
                    .like(columnName, searchTerm);
            PreparedQuery<T1> preparedQuery = queryBuilder.prepare();
            return getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }

    public List<T1> getAll(){
        try {
            return getTableDao().queryForAll();
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }

    public List<T1> getAllExact(String columnName, Object searchTerm){
        try {
            return getTableDao().queryBuilder().where()
                    .eq(columnName, searchTerm)
                    .query();
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }

    public T1 getMatchingColumn1FirstSimilarColumn2(String columnName1, Object searchTerm1, String columnName2, Object searchTerm2) {
        try {
            QueryBuilder<T1, T2> queryBuilder =
                    getTableDao().queryBuilder();
            queryBuilder.where()
                    .eq(columnName1, searchTerm1)
                    .and()
                    .like(columnName2, searchTerm2);
            PreparedQuery<T1> preparedQuery = queryBuilder.prepare();
            return getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }

    public List<T1> getMatchingColumn1AllSimilarColumn2(String columnName1, Object searchTerm1, String columnName2, Object searchTerm2) {
        try {
            QueryBuilder<T1, T2> queryBuilder =
                    getTableDao().queryBuilder();
            queryBuilder.where()
                    .eq(columnName1, searchTerm1)
                    .and()
                    .like(columnName2, searchTerm2);
            PreparedQuery<T1> preparedQuery = queryBuilder.prepare();
            return getTableDao().query(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }

    public List<T1> getAllSimilar(String columnName, Object searchTerm){
        try {
            return getTableDao().queryBuilder().where()
                    .like(columnName, searchTerm)
                    .query();
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return null;
    }
}
