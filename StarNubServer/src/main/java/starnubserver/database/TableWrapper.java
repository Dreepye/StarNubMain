package starnubserver.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import starnubserver.StarNub;

import java.sql.SQLException;

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
                e.printStackTrace();
            }
        }
        try {
            if (!tableDao.isTableExists()) {
                TableUtils.createTableIfNotExists(connection, typeParameterDBClass);
            } else {
                tableUpdater(connection, oldVersion);
            }
        } catch (SQLException e){
            e.printStackTrace();
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
                e.printStackTrace();
            }
        }
    }

    public boolean createIfNotExist(T1 persistedClass){
        try {
            tableDao.createIfNotExists(persistedClass);
            return true;
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return false;
        }
    }

    public boolean create(T1 persistedClass){
        try {
            tableDao.create(persistedClass);
            return true;
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return false;
        }
    }

    public boolean createOrUpdate(T1 persistedClass){
        try {
            tableDao.createOrUpdate(persistedClass);
            return true;
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return false;
        }
    }

    public boolean update(T1 persistedClass){
        try {
            tableDao.update(persistedClass);
            return true;
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return false;
        }
    }

    public boolean refresh(T1 persistedClass){
        try {
            tableDao.refresh(persistedClass);
            return true;
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return false;
        }
    }

    public boolean delete(T1 persistedClass){
        try {
            tableDao.delete(persistedClass);
            return true;
        } catch (SQLException e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return false;
        }
    }

    /** //TODO
     * setDao(starbounddata.packets.connection);
     * try {
     * if (oldVersion < 2) {
     * // we added the age column in version 2
     * tableDao.executeRaw("ALTER TABLE `account` ADD COLUMN age INTEGER;");
     * }
     * if (oldVersion < 3) {
     * // we added the weight column in version 3
     * tableDao.executeRaw("ALTER TABLE `account` ADD COLUMN weight INTEGER;");
     * }
     * } catch (SQLException e){
     * e.printStackTrace();
     * }
     * @param starbounddata.packets.connection Connection starbounddata.packets.connection source, should be provided
     * @param oldVersion int older starbounddata.packets.starbounddata.packets.starnubserver version should be provided
     */

}
