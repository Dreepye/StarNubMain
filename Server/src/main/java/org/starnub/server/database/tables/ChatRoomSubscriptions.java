package org.starnub.server.database.tables;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.starnub.server.StarNub;
import org.starnub.server.connectedentities.player.account.ChatRoomSubscription;
import org.starnub.server.connectedentities.player.account.Settings;
import org.starnub.server.database.TableWrapper;
import org.starnub.server.server.chat.ChatRoom;

import java.sql.SQLException;

public class ChatRoomSubscriptions extends TableWrapper<ChatRoomSubscription, Integer> {

    public ChatRoomSubscriptions(Class<ChatRoomSubscription> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(typeParameterDBClass, typeParameterIDClass);
    }

    public ChatRoomSubscriptions(ConnectionSource connectionSource, int oldVersion, Class<ChatRoomSubscription> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
        super(connectionSource, oldVersion, typeParameterDBClass, typeParameterIDClass);
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }

    public ChatRoomSubscription getChatRoomSubscription(Settings settings, ChatRoom chatRoom) {
        ChatRoomSubscription chatRoomSubscription = null;
        try {
            QueryBuilder<ChatRoomSubscription, Integer> queryBuilder =
                    getTableDao().queryBuilder();
            Where<ChatRoomSubscription, Integer> where = queryBuilder.where();
            queryBuilder.where()
                    .eq("ACCOUNT_SETTINGS_ID", settings)
                    .and()
                    .eq("CHAT_ROOM_ID", chatRoom);
            PreparedQuery<ChatRoomSubscription> preparedQuery = queryBuilder.prepare();
            chatRoomSubscription = getTableDao().queryForFirst(preparedQuery);
        } catch (Exception e) {
            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
        }
        return chatRoomSubscription;
    }
}
