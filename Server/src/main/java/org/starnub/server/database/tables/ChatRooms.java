package org.starnub.server.database.tables;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.starnub.server.StarNub;
import org.starnub.server.database.TableWrapper;
import org.starnub.server.server.chat.ChatRoom;

import java.sql.SQLException;

public class ChatRooms extends TableWrapper<ChatRoom, Integer> {

    public ChatRooms(Class<ChatRoom> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(typeParameterDBClass, typeParameterIDClass);
    }

    public ChatRooms(ConnectionSource connectionSource, int oldVersion, Class<ChatRoom> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(connectionSource, oldVersion, typeParameterDBClass, typeParameterIDClass);
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }

    public ChatRoom getChatRoomByName (String chatRoomName) {
        try {
            QueryBuilder<ChatRoom, Integer> queryBuilder =
                    getTableDao().queryBuilder();
            Where<ChatRoom, Integer> where = queryBuilder.where();
            queryBuilder.where()
                    .like("CHAT_ROOM_NAME", chatRoomName);
            PreparedQuery<ChatRoom> preparedQuery = queryBuilder.prepare();
            return getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
            return null;
        }
    }
}
