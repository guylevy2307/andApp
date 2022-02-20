package com.example.anygift;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.anygift.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserViewModel extends ViewModel {

    public User user = null;

    public UserViewModel() {
    }

    public interface GetUserListener {
        void onComplete(User user);
    }
    public interface SetUserListener {
        void onComplete(User user);
    }
    public void getUser(final GetUserListener listener) {
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
            return;
        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc != null) {
                        user = new User();
                        user.fromMap(task.getResult().getData());
                    }
                }
                listener.onComplete(user);
            }
        });
    }

    public void getUserById(String email, final GetUserListener listener) {
        FirebaseFirestore.getInstance().collection("users").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc != null) {
                        user = new User();
                        user.fromMap(task.getResult().getData());
                    }
                }
                listener.onComplete(user);
            }
        });
    }

    String getUserName() {
        return (user != null) ? user.getName() : "User_Name";
    }

    String getUserEmail() {
        return (user != null) ? user.getId() : "User_Email";
    }

    String getUserPhone() {
        return (user != null) ? user.getPhone() : "User_Phone";
    }

    void logout(){
        FirebaseAuth.getInstance().signOut();
    }

}

