package com.example.doancuoiky.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.widget.Toast;

import com.example.doancuoiky.Models.Product;
import com.example.doancuoiky.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FirestoreHelper {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    String uid ;

    public FirestoreHelper() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = auth.getCurrentUser().getUid();
    }

    public void saveUserData(Context context, String username, String phone, String birthday, Bitmap imgProfile) {
        // Chuyển đổi ảnh Bitmap thành chuỗi Base64
        String imgProfileBase64 = encodeImageToBase64(imgProfile);

        // Tạo đối tượng User
        User user = new User(username, phone, birthday, imgProfileBase64);

        // Lưu vào Firestore
        db.collection("users")
                .document(uid)  // Sử dụng UID làm Document ID
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    // Lưu thành công
                    Toast.makeText(context, "Lưu profile thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Lỗi khi lưu
                    Toast.makeText(context, "Lỗi khi lưu profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    public void updateUserData(Context context, String username, String phone, String birthday, Bitmap imgProfile) {

        // Lấy dữ liệu hiện tại của user từ Firestore
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Chuyển documentSnapshot thành đối tượng User
                        User currentUser = documentSnapshot.toObject(User.class);

                        if (currentUser != null) {
                            // Giữ nguyên habits cũ
                            Map<String, Object> currentHabits = currentUser.getHabits();

                            // Chuyển đổi ảnh Bitmap thành chuỗi Base64
                            String imgProfileBase64 = encodeImageToBase64(imgProfile);

                            // Cập nhật thông tin user
                            currentUser.setUsername(username);
                            currentUser.setPhone(phone);
                            currentUser.setBirthday(birthday);
                            currentUser.setImgProfileUrl(imgProfileBase64);
                            currentUser.setHabits(currentHabits); // Đảm bảo giữ nguyên habits

                            // Lưu lại vào Firestore
                            db.collection("users")
                                    .document(uid)
                                    .set(currentUser)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Lỗi khi cập nhật: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        }
                    } else {
                        Toast.makeText(context, "Không tìm thấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi khi lấy dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    // Chuyển ảnh Bitmap thành chuỗi Base64
    private String encodeImageToBase64(Bitmap imgProfile) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imgProfile.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public void getUserData(String userID ,UserDataCallback callback) {

        // Truy vấn Document "profile" trong Collection của UID
        db.collection("users")
                .document(userID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Chuyển dữ liệu từ DocumentSnapshot sang đối tượng User
                        User user = documentSnapshot.toObject(User.class);
                        callback.onSuccess(user); // Trả về đối tượng User thông qua callback
                    } else {
                        callback.onFailure("User profile does not exist.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi khi lấy dữ liệu
                    callback.onFailure("Error retrieving user data: " + e.getMessage());
                });
    }



    // Interface để xử lý callback
    public interface UserDataCallback {
        void onSuccess(User user);

        void onFailure(String errorMessage);
    }

    public void saveProductData(Context context,
                                String userID,
                                Bitmap productImage,
                                String productState,
                                String productName,
                                int productPrice,
                                String location,
                                String category,
                                String brandName,
                                String guarantee,
                                String description) {
//        // Lấy UID của người dùng hiện tại
//        String uid = product.getUserID();

        // Chuyển đổi ảnh Bitmap thành chuỗi Base64
        String imgProfileBase64 = encodeImageToBase64(productImage);

        // Tạo đối tượng Product
        Product product = new Product(userID, imgProfileBase64, productState, productName,productPrice,location,category,brandName,guarantee,description);
        String productId = UUID.randomUUID().toString();
        product.setId(productId);
        // Lưu vào Firestore
        db.collection("products")
                .document(productId)  // random id
                .set(product)
                .addOnSuccessListener(aVoid -> {
                    // Lưu thành công
                    Toast.makeText(context, "Lưu profile thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Lỗi khi lưu
                    Toast.makeText(context, "Lỗi khi lưu profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // Callback interface để trả về danh sách Product
    public interface ProductListCallback {
        void onSuccess(List<Product> productList);
        void onFailure(String errorMessage);
    }

    // Hàm lấy tất cả sản phẩm từ collection "products"
    public void getAllProducts(ProductListCallback callback) {
        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> productList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Product product = document.toObject(Product.class);
                        if (product != null) {
                            productList.add(product);
                        }
                    }
                    callback.onSuccess(productList); // Trả về danh sách product
                })
                .addOnFailureListener(e -> {
                    callback.onFailure("Lỗi khi tải sản phẩm: " + e.getMessage());
                });
    }
    public interface FavoriteProductCallback {
        void onSuccess(String message);

        void onFailure(String errorMessage);
    }

    // Hàm thêm sản phẩm vào "favorite"
    public void addProductToFavorites(String productId, FavoriteProductCallback callback) {
        Map<String, Object> favoriteData = new HashMap<>();
        favoriteData.put("productId", productId);

        db.collection("users")
                .document(uid)
                .collection("favorite")
                .document(productId)
                .set(favoriteData)
                .addOnSuccessListener(aVoid -> callback.onSuccess("Đã thêm vào yêu thích."))
                .addOnFailureListener(e -> callback.onFailure("Lỗi khi thêm sản phẩm: " + e.getMessage()));
    }

    // Fetch danh sách productId từ collection "favorite" của người dùng
    public void getFavoriteProductIds(String uid, FavoriteFetchCallback callback) {
        CollectionReference favoriteRef = db.collection("users")
                .document(uid)
                .collection("favorite");

        favoriteRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> favoriteIds = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String productId = document.getString("productId");
                        if (productId != null) {
                            favoriteIds.add(productId);
                        }
                    }
                    callback.onSuccess(favoriteIds);
                })
                .addOnFailureListener(e -> callback.onFailure("Lỗi khi lấy sản phẩm yêu thích: " + e.getMessage()));
    }

    // Callback khi lấy danh sách sản phẩm yêu thích
    public interface FavoriteFetchCallback {
        void onSuccess(List<String> favoriteIds);
        void onFailure(String errorMessage);
    }

    // Hàm xóa sản phẩm khỏi "favorite"
    public void removeProductFromFavorites(String productId, FavoriteProductCallback callback) {
        db.collection("users")
                .document(uid)
                .collection("favorite")
                .document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess("Đã xóa khỏi yêu thích."))
                .addOnFailureListener(e -> callback.onFailure("Lỗi khi xóa sản phẩm: " + e.getMessage()));
    }
    public void incrementFavorite(String productId, FavoriteProductCallback callback) {
        DocumentReference productRef = db.collection("products").document(productId);

        db.runTransaction(transaction -> {
                    Long currentFavorite = transaction.get(productRef).getLong("favorite");
                    if (currentFavorite == null) {
                        currentFavorite = 0L;
                    }
                    transaction.update(productRef, "favorite", currentFavorite + 1);
                    return null;
                }).addOnSuccessListener(aVoid -> callback.onSuccess("Đã thêm vào yêu thích."))
                .addOnFailureListener(e -> callback.onFailure("Lỗi khi thêm yêu thích: " + e.getMessage()));
    }

    public void decrementFavorite(String productId, FavoriteProductCallback callback) {
        DocumentReference productRef = db.collection("products").document(productId);

        db.runTransaction(transaction -> {
                    Long currentFavorite = transaction.get(productRef).getLong("favorite");
                    if (currentFavorite == null || currentFavorite <= 0) {
                        currentFavorite = 0L;
                    } else {
                        currentFavorite -= 1;
                    }
                    transaction.update(productRef, "favorite", currentFavorite);
                    return null;
                }).addOnSuccessListener(aVoid -> callback.onSuccess("Đã xóa khỏi yêu thích."))
                .addOnFailureListener(e -> callback.onFailure("Lỗi khi xóa yêu thích: " + e.getMessage()));
    }
    public void updateUserHabits( String category, String priceRange, HabitUpdateCallback callback) {
        DocumentReference userRef = db.collection("users").document(uid);

        db.runTransaction(transaction -> {
                    DocumentSnapshot snapshot = transaction.get(userRef);

                    // Lấy current habits
                    Map<String, Object> habits = (Map<String, Object>) snapshot.get("habits");
                    if (habits == null) {
                        habits = new HashMap<>();
                    }

                    // Cập nhật categories
                    Map<String, Long> categories = (Map<String, Long>) habits.get("categories");
                    if (categories == null) {
                        categories = new HashMap<>();
                    }
                    categories.put(category, categories.getOrDefault(category, 0L) + 1);

                    // Cập nhật priceRanges
                    Map<String, Long> priceRanges = (Map<String, Long>) habits.get("priceRanges");
                    if (priceRanges == null) {
                        priceRanges = new HashMap<>();
                    }
                    priceRanges.put(priceRange, priceRanges.getOrDefault(priceRange, 0L) + 1);

                    // Ghi lại vào habits
                    habits.put("categories", categories);
                    habits.put("priceRanges", priceRanges);

                    transaction.update(userRef, "habits", habits);
                    return null;
                }).addOnSuccessListener(aVoid -> callback.onSuccess("Habit updated successfully."))
                .addOnFailureListener(e -> callback.onFailure("Error updating habit: " + e.getMessage()));
    }
    public interface HabitUpdateCallback {
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }
    public void getUserHabits(String userId, HabitCallback callback) {
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Lấy dữ liệu habits
                Map<String, Object> habits = (Map<String, Object>) documentSnapshot.get("habits");

                if (habits != null) {
                    // Tách categories và priceRanges
                    Map<String, Long> categories = (Map<String, Long>) habits.get("categories");
                    Map<String, Long> priceRanges = (Map<String, Long>) habits.get("priceRanges");

                    // Gọi callback thành công
                    callback.onSuccess(categories != null ? categories : new HashMap<>(),
                            priceRanges != null ? priceRanges : new HashMap<>());
                } else {
                    callback.onFailure("Habits not found.");
                }
            } else {
                callback.onFailure("User not found.");
            }
        }).addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface HabitCallback {
        void onSuccess(Map<String, Long> categories, Map<String, Long> priceRanges);
        void onFailure(String errorMessage);
    }


}

