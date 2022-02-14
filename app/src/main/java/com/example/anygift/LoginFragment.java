package com.example.anygift;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.anygift.model.Model;
import com.example.anygift.model.ModelFirebase;
import com.example.anygift.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth= FirebaseAuth.getInstance();

    View view;
    Snackbar mySnackbar;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressBar pb;

    Button signIn_btn;
    Button signUp_btn;
    Button forgotP_btn;
    TextInputEditText email;
    TextInputEditText password;

    String email_user;
    String password_user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle("AnyGift - Login");
        view= inflater.inflate(R.layout.fragment_login, container, false);
        signIn_btn=view.findViewById(R.id.Login_signIn_btn);
        signUp_btn=view.findViewById(R.id.Login_btn_signUp);
        forgotP_btn=view.findViewById(R.id.Login_btn_forgotp);
        email=view.findViewById(R.id.Login_email_input);
        password=view.findViewById(R.id.Login_password_input);
        pb=view.findViewById(R.id.login_prob);
        pb.setVisibility(View.INVISIBLE);

        signIn_btn.setTypeface(Typeface.SANS_SERIF);
        signUp_btn.setTypeface(Typeface.SANS_SERIF);
        forgotP_btn.setTypeface(Typeface.SANS_SERIF);

        signIn_btn.setOnClickListener(v -> {checkUser();});

        signUp_btn.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment());
        });

        forgotP_btn.setOnClickListener(v -> {forgotPassword();});
        return view;
    }

    public void checkUser() {


        email_user=email.getText().toString();
        password_user=password.getText().toString();
        if(TextUtils.isEmpty(email_user) && email_user.matches(emailPattern))
        {
            email.setError("please enter  correct   email");
            return;
        }
        if(TextUtils.isEmpty(password_user))
        {
            password.setError("please enter correct  password");
            return;
        }

        pb.setVisibility(View.VISIBLE);

        //authenticate the user
        mAuth.signInWithEmailAndPassword(email_user,password_user).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    signIn_btn.setEnabled(false);
                    signUp_btn.setEnabled(false);
                    forgotP_btn.setEnabled(false);
                    pb.setVisibility(View.INVISIBLE);
                    mySnackbar = Snackbar.make(view, "Login successful :)", BaseTransientBottomBar.LENGTH_LONG);
                    mySnackbar.show();
                    Log.d("TAG","login successful");
                    Navigation.findNavController(view).navigate(LoginFragmentDirections.actionGlobalFeedFragment());
                }
                else
                    //Log.d("TAG","Login failed");
                    Toast.makeText(getContext(),"Error! "+ task.getException().getMessage() ,Toast.LENGTH_LONG).show();
                pb.setVisibility(View.INVISIBLE);
            }
        });
    }


    public void forgotPassword(){
        email_user=email.getText().toString();
        mAuth.sendPasswordResetEmail(email_user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    mySnackbar = Snackbar.make(view, "Email sent, check your mailbox :)", BaseTransientBottomBar.LENGTH_LONG);
                    mySnackbar.show();

                }
                else
                    Log.d("TAG", "failed");
            }
        });
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
}

