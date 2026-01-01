package com.example.localcuisine.data.favorite;

import java.util.List;

/**
 * FavoriteRepository
 * <p>
 * Interface định nghĩa các thao tác quản lý danh sách món ăn yêu thích
 * của người dùng.
 *
 * <p>
 * Repository này chịu trách nhiệm:
 * <ul>
 *     <li>Kiểm tra trạng thái yêu thích của một món ăn</li>
 *     <li>Thêm / xoá món ăn khỏi danh sách yêu thích</li>
 *     <li>Lấy toàn bộ danh sách món ăn yêu thích của người dùng</li>
 * </ul>
 *
 * <p>
 * Tất cả các thao tác đều là bất đồng bộ và trả kết quả thông qua callback.
 */
public interface FavoriteRepository {

    /**
     * Kiểm tra một món ăn có nằm trong danh sách yêu thích của người dùng hay không
     *
     * @param uid    UID của người dùng
     * @param foodId ID của món ăn
     * @param cb     Callback trả về kết quả boolean
     */
    void isFavorite(String uid, int foodId, BooleanCallback cb);

    /**
     * Thêm một món ăn vào danh sách yêu thích của người dùng
     *
     * @param uid    UID của người dùng
     * @param foodId ID của món ăn
     * @param cb     Callback báo kết quả thao tác
     */
    void addFavorite(String uid, int foodId, ActionCallback cb);

    /**
     * Xoá một món ăn khỏi danh sách yêu thích của người dùng
     *
     * @param uid    UID của người dùng
     * @param foodId ID của món ăn
     * @param cb     Callback báo kết quả thao tác
     */
    void removeFavorite(String uid, int foodId, ActionCallback cb);

    /**
     * Lấy danh sách ID các món ăn yêu thích của người dùng
     *
     * @param uid UID của người dùng
     * @param cb  Callback trả về danh sách foodId
     */
    void getAllFavoriteIds(String uid, ListCallback cb);

    /**
     * Callback trả về kết quả boolean
     */
    interface BooleanCallback {

        /**
         * Gọi khi có kết quả
         *
         * @param value Kết quả boolean
         */
        void onResult(boolean value);

        /**
         * Gọi khi xảy ra lỗi
         *
         * @param e Exception lỗi
         */
        void onError(Exception e);
    }

    /**
     * Callback cho các thao tác thêm / xoá
     */
    interface ActionCallback {

        /**
         * Gọi khi thao tác thành công
         */
        void onSuccess();

        /**
         * Gọi khi thao tác thất bại
         *
         * @param e Exception lỗi
         */
        void onError(Exception e);
    }

    /**
     * Callback trả về danh sách ID món ăn
     */
    interface ListCallback {

        /**
         * Gọi khi lấy dữ liệu thành công
         *
         * @param foodIds Danh sách ID món ăn yêu thích
         */
        void onResult(List<Integer> foodIds);

        /**
         * Gọi khi xảy ra lỗi
         *
         * @param e Exception lỗi
         */
        void onError(Exception e);
    }
}
