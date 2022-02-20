package com.example.anygift;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.room.PrimaryKey;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.anygift.model.Model;
import com.example.anygift.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SignUpFragment extends Fragment {

    User user;
    View view;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @NonNull
    TextInputEditText firstName;
    @NonNull
    TextInputEditText lastName;
    @PrimaryKey
    @NonNull
    TextInputEditText email;
    @NonNull
    TextInputEditText phone;
    @NonNull
    TextInputEditText password;
    @NonNull
    TextInputEditText address;

    Button signIn_btn;
    Button continue_btn, find_btn;
    CheckBox terms;

    String Fname;
    String Lname;
    String phone_usr;
    String email_usr;
    String address_usr;
    String password_usr;
    FusedLocationProviderClient client;
    String latAndLong;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("AnyGift - SignUp");
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        firstName = view.findViewById(R.id.SignUp_firstName_input);
        lastName = view.findViewById(R.id.SignUp_lastName_input);
        phone = view.findViewById(R.id.SignUp_phone_input);
        email = view.findViewById(R.id.SignUp_email_input);
        password = view.findViewById(R.id.SignUp_password_input);
        signIn_btn = view.findViewById(R.id.SignUp_signIn_btn);
        continue_btn = view.findViewById(R.id.SignUp_continue_btn);
        address = view.findViewById(R.id.SignUp_address_input);
        find_btn = view.findViewById(R.id.SignUp_btn_find_addres);

        terms = view.findViewById(R.id.SignUp_check_box);
        signIn_btn.setTypeface(Typeface.SANS_SERIF);
        continue_btn.setTypeface(Typeface.SANS_SERIF);
        continue_btn.setOnClickListener(v -> {
            save();
        });
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        find_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

                }

            }
        });

        signIn_btn.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment());
        });


        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && (grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)
        ) {
            getCurrentLocation();
        } else {
            Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = (Location) task.getResult();
                    if (location != null) {
                        String cityName = null;
                        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses.size() > 0) {
                                System.out.println(addresses.get(0).getLocality());
                                cityName = addresses.get(0).getCountryName() + ", " +
                                        addresses.get(0).getAdminArea() + ", " +
                                        addresses.get(0).getPostalCode() + ".";
                                latAndLong = String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude());
                            }
                            address.setText(cityName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }
            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }


    public void save() {
        continue_btn.setEnabled(false);
        signIn_btn.setEnabled(false);
        Fname = firstName.getText().toString();
        Lname = lastName.getText().toString();
        phone_usr = phone.getText().toString();
        email_usr = email.getText().toString();
        address_usr = address.getText().toString();
        password_usr = password.getText().toString();
        boolean flag = terms.isChecked();


        if (Fname == null || Lname == null || address_usr == null) {
            firstName.setError("You must enter your first name");
            lastName.setError("You must ennte your last name");
            address.setError(" You must enter your address");
            continue_btn.setEnabled(true);
            return;
        }

        if (password_usr.length() < 6) {
            password.setError("Password must be at least 6 characters");
            continue_btn.setEnabled(true);
            return;
        }

        if (flag == false) {
            terms.setError("You must agree our terms :)");
            continue_btn.setEnabled(true);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email_usr, password_usr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    updateProfile();
                }
            }
        });
        user = new User(Fname, Lname, phone_usr, email_usr, address_usr, password_usr, latAndLong);
        Snackbar mySnackbar = Snackbar.make(view, "signUp succeed, Nice to meet you :)", BaseTransientBottomBar.LENGTH_LONG);
        mySnackbar.show();
        Model.instance.addUser(user, () -> {
           // Navigation.findNavController(view).navigate(SignUpFragmentDirections.actionSignUpFragmentToUserProfileFragment());
             Navigation.findNavController(view).navigate(R.id.action_global_feedFragment);

        });
    }


    public void updateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(Fname)
                .build();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }


}