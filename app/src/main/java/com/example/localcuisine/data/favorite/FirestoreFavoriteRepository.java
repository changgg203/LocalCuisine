package com.example.localcuisine.data.favorite;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FirestoreFavoriteRepository
 * <p>
 * Implementation của {@link FavoriteRepository} sử dụng Firebase Firestore
 * để lưu trữ danh sách món ăn yêu thích của người dùng.
 *
 * <p>
 * Cấu trúc dữ liệu trên Firestore:
 * <pre>
 * users/{uid}/favorites/{foodId}
 * </pre>
 *
 * <p>
 * Mỗi document đại diện cho một món ăn yêu thích, với document ID
 * chính là {@code foodId}.
 *
 * <p>
 * Tất cả các thao tác đều bất đồng bộ và trả kết quả thông qua callback.
 */
public class FirestoreFavoriteRepository implements FavoriteRepository {

    /**
     * Firestore instance
     */
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Tạo đường dẫn document favorite tương ứng với người dùng và món ăn
     *
     * @param uid    UID người dùng
     * @param foodId ID món ăn
     * @return đường dẫn document Firestore
     */
    private String favDoc(String uid, int foodId) {
        return "users/" + uid + "/favorites/" + foodId;
    }

    /**
     * Kiểm tra một món ăn có nằm trong danh sách yêu thích của người dùng hay không
     *
     * @param uid    UID người dùng
     * @param foodId ID món ăn
     * @param cb     Callback trả về kết quả boolean
     */
    @Override
    public void isFavorite(
            @NonNull String uid,
            int foodId,
            @NonNull BooleanCallback cb
    ) {
        db.document(favDoc(uid, foodId))
                .get()
                .addOnSuccessListener(snap -> cb.onResult(snap.exists()))
                .addOnFailureListener(cb::onError);
    }

    /**
     * Thêm một món ăn vào danh sách yêu thích của người dùng
     *
     * @param uid    UID người dùng
     * @param foodId ID món ăn
     * @param cb     Callback báo kết quả thao tác
     */
    @Override
    public void addFavorite(
            @NonNull String uid,
            int foodId,
            @NonNull ActionCallback cb
    ) {
        Map<String, Object> data = new HashMap<>();
        data.put("foodId", foodId);
        data.put("createdAt", Timestamp.now());

        db.document(favDoc(uid, foodId))
                .set(data)
                .addOnSuccessListener(v -> cb.onSuccess())
                .addOnFailureListener(cb::onError);
    }

    /**
     * Xoá một món ăn khỏi danh sách yêu thích của người dùng
     *
     * @param uid    UID người dùng
     * @param foodId ID món ăn
     * @param cb     Callback báo kết quả thao tác
     */
    @Override
    public void removeFavorite(
            @NonNull String uid,
            int foodId,
            @NonNull ActionCallback cb
    ) {
        db.document(favDoc(uid, foodId))
                .delete()
                .addOnSuccessListener(v -> cb.onSuccess())
                .addOnFailureListener(cb::onError);
    }

    /**
     * Lấy danh sách ID các món ăn yêu thích của người dùng
     *
     * <p>
     * Document ID trong collection {@code favorites} được sử dụng
     * trực tiếp làm {@code foodId}.
     *
     * @param uid UID người dùng
     * @param cb  Callback trả về danh sách ID món ăn
     */
    @Override
    public void getAllFavoriteIds(
            @NonNull String uid,
            @NonNull ListCallback cb
    ) {
        db.collection("users")
                .document(uid)
                .collection("favorites")
                .get()
                .addOnSuccessListener(qs -> {
                    List<Integer> ids = new ArrayList<>();
                    qs.getDocuments().forEach(doc -> {
                        try {
                            ids.add(Integer.parseInt(doc.getId()));
                        } catch (NumberFormatException ignored) {
                        }
                    });
                    cb.onResult(ids);
                })
                .addOnFailureListener(cb::onError);
    }
}
