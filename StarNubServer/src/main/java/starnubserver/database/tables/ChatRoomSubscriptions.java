///*
// * Copyright (C) 2014 www.StarNub.org - Underbalanced
// *
// * This utilities.file is part of org.starnubserver a Java Wrapper for Starbound.
// *
// * This above mentioned StarNub software is free software:
// * you can redistribute it and/or modify it under the terms
// * of the GNU General Public License as published by the Free
// * Software Foundation, either version  3 of the License, or
// * any later version. This above mentioned CodeHome software
// * is distributed in the hope that it will be useful, but
// * WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
// * the GNU General Public License for more details. You should
// * have received a copy of the GNU General Public License in
// * this StarNub Software.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package starnubserver.database.tables;
//
//import com.j256.ormlite.stmt.PreparedQuery;
//import com.j256.ormlite.stmt.QueryBuilder;
//import com.j256.ormlite.stmt.Where;
//import com.j256.ormlite.support.ConnectionSource;
//import org.apache.commons.lang3.exception.ExceptionUtils;
//import starnubserver.StarNub;
//import starnubserver.connections.player.account.ChatRoomSubscription;
//import starnubserver.connections.player.account.Settings;
//import starnubserver.database.TableWrapper;
//import chatmanager.chat.ChatRoom;
//
//import java.sql.SQLException;
//
///**
// * Represents ChatRoomSubscriptions Table that extends the TableWrapper class
// *
// * @author Daniel (Underbalanced) (www.StarNub.org)
// * @since 1.0 Beta
// */
//public class ChatRoomSubscriptions extends TableWrapper<ChatRoomSubscription, Integer> {
//
//    public ChatRoomSubscriptions(Class<ChatRoomSubscription> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
//        super(typeParameterDBClass, typeParameterIDClass);
//    }
//
//    public ChatRoomSubscriptions(ConnectionSource connectionSource, int oldVersion, Class<ChatRoomSubscription> typeParameterDBClass, Class<Integer> typeParameterIDClass) {
//        super(connectionSource, oldVersion, typeParameterDBClass, typeParameterIDClass);
//    }
//
//    @Override
//    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {
//
//    }
//
//    public ChatRoomSubscription getChatRoomSubscription(Settings settings, ChatRoom chatRoom) {
//        ChatRoomSubscription chatRoomSubscription = null;
//        try {
//            QueryBuilder<ChatRoomSubscription, Integer> queryBuilder =
//                    getTableDao().queryBuilder();
//            Where<ChatRoomSubscription, Integer> where = queryBuilder.where();
//            queryBuilder.where()
//                    .eq("ACCOUNT_SETTINGS_ID", settings)
//                    .and()
//                    .eq("CHAT_ROOM_ID", chatRoom);
//            PreparedQuery<ChatRoomSubscription> preparedQuery = queryBuilder.prepare();
//            chatRoomSubscription = getTableDao().queryForFirst(preparedQuery);
//        } catch (Exception e) {
//            StarNub.getLogger().cFatPrint("StarNub", ExceptionUtils.getMessage(e));
//        }
//        return chatRoomSubscription;
//    }
//}
