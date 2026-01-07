package com.example.localcuisine.data.remote.notification;

import com.example.localcuisine.ui.i18n.UiText;
import com.example.localcuisine.ui.i18n.UiTextKey;

public class NotificationTextBuilder {

    public static String buildTitle(FirestoreNotification n) {
        if (n == null) {
            return UiText.t(UiTextKey.NOTIF_DEFAULT_TITLE);
        }

        String type = n.getType();
        if (type == null) {
            return UiText.t(UiTextKey.NOTIF_NEW);
        }

        switch (type) {
            case "REPLY":
                return UiText.t(UiTextKey.NOTIF_REPLY_TITLE);

            case "REVIEW":
                return UiText.t(UiTextKey.NOTIF_REVIEW_TITLE);

            case "FAVORITE":
                return UiText.t(UiTextKey.NOTIF_FAVORITE_TITLE);

            default:
                return UiText.t(UiTextKey.NOTIF_NEW);
        }
    }

    public static String buildContent(FirestoreNotification n) {
        if (n == null) return "";

        String type = n.getType();
        if (type == null) {
            return n.getContent();
        }

        switch (type) {
            case "REPLY":
                return UiText.t(UiTextKey.NOTIF_REPLY_CONTENT);

            case "REVIEW":
                return UiText.t(UiTextKey.NOTIF_REVIEW_CONTENT);

            case "FAVORITE":
                return UiText.t(UiTextKey.NOTIF_FAVORITE_CONTENT);

            default:
                return n.getContent();
        }
    }
}
