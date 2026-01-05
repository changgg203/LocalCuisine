package com.example.localcuisine.data.remote.notification;

public class NotificationTextBuilder {

    public static String buildTitle(FirestoreNotification n) {
        if (n == null) return "Thông báo";

        String type = n.getType();
        if (type == null) return "Thông báo mới";

        switch (type) {
            case "REPLY":
                return "Phản hồi mới";
            case "REVIEW":
                return "Đánh giá mới";
            case "FAVORITE":
                return "Món ăn được yêu thích";
            default:
                return "Thông báo mới";
        }
    }

    public static String buildContent(FirestoreNotification n) {
        if (n == null) return "";

        String type = n.getType();
        if (type == null) return "Có người đã phản hồi về món ăn bạn quan tâm: " + n.getContent();

        switch (type) {
            case "REPLY":
                return "Có người đã phản hồi về món ăn bạn quan tâm";

            case "REVIEW":
                return "Có một đánh giá mới cho món ăn của bạn";

            case "FAVORITE":
                return "Một món ăn bạn theo dõi vừa được yêu thích";

            default:
                return n.getContent();
        }
    }
}
