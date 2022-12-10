package com.gps.payroll.profileAndAbout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gps.payroll.authentication.LoginActivity;
import com.gps.payroll.backPageListener.BackListenerFragment;
import com.gps.payroll.splashAndDashboard.MainActivity;
import com.gps.payroll.splashAndDashboard.Dashboard;
import com.gps.payroll.modelClasses.StoreUserImageUrlData;
import com.gps.payroll.R;
import com.squareup.picasso.Picasso;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements BackListenerFragment, View.OnClickListener {

    private View views;
    private Fragment fragment;
    private NetworkInfo netInfo;
    private ConnectivityManager cm;
    private StorageReference storageReference;
    private ProgressBar progressBar;
    private int PERMISSION_REQUEST_CODE = 101;
    private ProgressDialog dialog;
    private CircleImageView profilePic;
    private static Uri uriProfileImage;
    private FragmentTransaction fragmentTransaction;
    public static BackListenerFragment backBtnListener;
    private DatabaseReference databaseReference, imageReference;
    private TextView phone, email, name, nid, address;
    private CardView resetPass, logout, backFromProfile, editNid, editAddress, changeImage;
    private String messageRole, userPhone, userEmailText, profileImageUrl="", userNid, userAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        views = inflater.inflate(R.layout.fragment_profile, container, false);

        dialog = new ProgressDialog(getActivity());
        progressBar = views.findViewById(R.id.profileProgressId);

        resetPass = views.findViewById(R.id.resetPassId);
        resetPass.setOnClickListener(this);
        logout = views.findViewById(R.id.logoutId);
        logout.setOnClickListener(this);
        backFromProfile = views.findViewById(R.id.backFromProfileId);
        backFromProfile.setOnClickListener(this);
        profilePic = views.findViewById(R.id.profilePicId);
        profilePic.setOnClickListener(this);
        editNid = views.findViewById(R.id.uploadNidId);
        editNid.setOnClickListener(this);
        editAddress = views.findViewById(R.id.uploadAddressId);
        editAddress.setOnClickListener(this);
        changeImage = views.findViewById(R.id.uploadProfilePicId);
        changeImage.setOnClickListener(this);

        phone = views.findViewById(R.id.userPhoneId);
        email = views.findViewById(R.id.userEmailId);
        name = views.findViewById(R.id.userNameId);
        nid = views.findViewById(R.id.userNidId);
        address = views.findViewById(R.id.userAddressId);

        try{
            messageRole = requireArguments().getString("messageRole");

            if(messageRole.equals("adminS")){
                userPhone = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
                databaseReference = FirebaseDatabase.getInstance().getReference("Admin Info");
                editNid.setVisibility(View.GONE);
                editAddress.setVisibility(View.GONE);

            } else if(messageRole.equals("employeeS")){
                userPhone = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
                databaseReference = FirebaseDatabase.getInstance().getReference("Employee Info");
                editNid.setVisibility(View.VISIBLE);
                editAddress.setVisibility(View.VISIBLE);
            }
        } catch (Exception e){
            Log.i("Exception", e.getMessage());
        }

        imageReference = FirebaseDatabase.getInstance().getReference("User Images");
        getUserInfo();

        return views;
    }

    private void getUserInfo() {
        cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            try {
                databaseReference.child(userPhone).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if(messageRole.equals("adminS")){
                                name.setText(Objects.requireNonNull(snapshot.child("username").getValue()).toString());
                                userEmailText = Objects.requireNonNull(snapshot.child("userEmail").getValue()).toString();

                                phone.setText(userPhone);
                                email.setText(userEmailText);

                            } else if(messageRole.equals("employeeS")){
                                name.setText(Objects.requireNonNull(snapshot.child("username").getValue()).toString());
                                userEmailText = Objects.requireNonNull(snapshot.child("userEmail").getValue()).toString();
                                userNid = Objects.requireNonNull(snapshot.child("userNid").getValue()).toString();
                                userAddress = Objects.requireNonNull(snapshot.child("userAddress").getValue()).toString();

                                phone.setText(userPhone);
                                email.setText(userEmailText);
                                nid.setText(userNid);
                                address.setText(userAddress);
                            }

                            try {
                                imageReference.child(userPhone).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        try {
                                            String imageUrl = Objects.requireNonNull(snapshot.child("avatar").getValue()).toString();
                                            Picasso.get().load(imageUrl).into(profilePic);

                                        } catch (Exception e) {
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {}
                                });
                            } catch (Exception e) {
                                progressBar.setVisibility(View.GONE);
                            }

                            progressBar.setVisibility(View.GONE);

                        } catch (Exception exception){
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e){
                Toast.makeText(getActivity(), "No Such User", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }

        } else {
            Toast.makeText(getActivity(), "Turn On Internet Connection", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.resetPassId) {
            Bundle armgs = new Bundle();
            String emailText = email.getText().toString();
            armgs.putString("email_key", emailText);

            ResetPassword resetPassword = new ResetPassword();
            resetPassword.setArguments(armgs);
            resetPassword.show(requireActivity().getSupportFragmentManager(), "Sample dialog");
        }

        if(v.getId()==R.id.uploadNidId) {
            Bundle armgs = new Bundle();
            armgs.putString("email_key", email.getText().toString());
            armgs.putString("phone_key", phone.getText().toString());
            armgs.putString("username_key", name.getText().toString());
            armgs.putString("nid_key", nid.getText().toString());
            armgs.putString("address_key", address.getText().toString());

            EditNid editNid = new EditNid();
            editNid.setArguments(armgs);
            editNid.show(requireActivity().getSupportFragmentManager(), "Sample dialog");
        }

        if(v.getId()==R.id.uploadAddressId) {
            Bundle armgs = new Bundle();
            armgs.putString("email_key", email.getText().toString());
            armgs.putString("phone_key", phone.getText().toString());
            armgs.putString("username_key", name.getText().toString());
            armgs.putString("nid_key", nid.getText().toString());
            armgs.putString("address_key", address.getText().toString());

            EditAddress editAddress = new EditAddress();
            editAddress.setArguments(armgs);
            editAddress.show(requireActivity().getSupportFragmentManager(), "Sample dialog");
        }

        if(v.getId()==R.id.logoutId) {
            logoutApp();
        }

        if(v.getId()==R.id.backFromProfileId) {
            ((MainActivity) requireActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

            fragment = new Dashboard();
            fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentID, fragment, "EMPLOYEE_FRAGMENT");
            fragmentTransaction.commit();
        }

        if(v.getId()==R.id.uploadProfilePicId) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(requireActivity(), new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);

                someActivityResultLauncher.launch("image/*");
            }

            else {
                someActivityResultLauncher.launch("image/*");
            }
        }
    }

    ActivityResultLauncher<String> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        uriProfileImage = result;

                        profilePic.setImageURI(uriProfileImage);
                        Picasso.get().load(uriProfileImage).into(profilePic);

                        uploadImageToFirebase();
                    }
                }
            });

    private void uploadImageToFirebase() {
        dialog.setMessage("Uploading.....");
        dialog.show();

        storageReference = FirebaseStorage.getInstance()
                .getReference("profile images/" + userPhone + ".jpg");

        if(uriProfileImage!=null){
            storageReference.putFile(uriProfileImage).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                profileImageUrl = uri.toString();
                saveUserInfo();
            }).addOnFailureListener(e -> {
                dialog.dismiss();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            })).addOnFailureListener(e -> {});
        }
    }

    private void saveUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null && profileImageUrl!=null){
            UserProfileChangeRequest profile;
            profile= new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(profileImageUrl)).build();

            user.updateProfile(profile).addOnCompleteListener(task -> {});

            storeImageMethod(profileImageUrl);

            dialog.dismiss();
            Toast.makeText(getActivity(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
        }
    }

    public void storeImageMethod(String profileImageUrl){
        StoreUserImageUrlData storeUserImageUrlData = new StoreUserImageUrlData(profileImageUrl);
        imageReference.child(userPhone).setValue(storeUserImageUrlData);
    }

    private void logoutApp(){
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(requireActivity());
        alertDialogBuilder.setTitle("Logout !");
        alertDialogBuilder.setMessage("Are you sure you want to logout ?");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
            String nullValue = "";
            setNullMethod(nullValue);

            requireActivity().finish();
            requireActivity().finishAffinity();
        });

        alertDialogBuilder.setNeutralButton("No", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void setNullMethod(String passedString){
        try {
            FileOutputStream fileOutputStream = requireActivity().openFileOutput("Users_Info.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(passedString.getBytes());
            fileOutputStream.close();

            FileOutputStream fileOutputStream2 = requireActivity().openFileOutput("Admin_Phone.txt", Context.MODE_PRIVATE);
            fileOutputStream2.write(passedString.getBytes());
            fileOutputStream2.close();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        ((MainActivity) requireActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

        fragment = new Dashboard();
        fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentID, fragment, "EMPLOYEE_FRAGMENT");
        fragmentTransaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        backBtnListener = this;
    }

    @Override
    public void onPause() {
        backBtnListener = null;
        super.onPause();
    }
}
