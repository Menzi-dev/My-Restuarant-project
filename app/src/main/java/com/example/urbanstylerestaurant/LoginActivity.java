package com.example.urbanstylerestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText emailLogin, passwordLogin;
    private RadioGroup loginRoleRadioGroup;
    private Button loginButtonSubmit;
    private TextView createAccountText;
    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        loginRoleRadioGroup = findViewById(R.id.loginRoleRadioGroup);
        loginButtonSubmit = findViewById(R.id.loginButtonSubmit);
        createAccountText = findViewById(R.id.createAccountText);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("users");

        // 🔑 Handle Login Button Click
        loginButtonSubmit.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_button));
            loginUser();
        });

        // 🔁 Navigate to Register Activity
        createAccountText.setOnClickListener(view -> {
            view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_button));
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = emailLogin.getText().toString().trim();
        String password = passwordLogin.getText().toString().trim();

        int selectedRoleId = loginRoleRadioGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Please select your role", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedRoleButton = findViewById(selectedRoleId);
        String selectedRole = selectedRoleButton.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = auth.getCurrentUser().getUid();
                database.child(userId).get().addOnCompleteListener(snapshotTask -> {
                    if (snapshotTask.isSuccessful()) {
                        DataSnapshot snapshot = snapshotTask.getResult();
                        if (snapshot.exists()) {
                            String storedRole = snapshot.child("role").getValue(String.class);
                            if (storedRole != null && storedRole.equals(selectedRole)) {
                                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                openDashboard(storedRole);
                            } else {
                                Toast.makeText(this, "Incorrect role selected!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "User not found in database!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDashboard(String role) {
        Intent intent = null;
        switch (role) {
            case "Customer":
                intent = new Intent(this, CustomerDashboardActivity.class);
                break;
            case "Waiter":
                intent = new Intent(this, WaiterDashboardActivity.class);
                break;
            case "Chef":
                intent = new Intent(this, ChefDashboardActivity.class);
                break;
            case "Manager":
                intent = new Intent(this, ManagerDashboardActivity.class);
                break;
        }

        if (intent != null) {
            startActivity(intent);
            finish();
        }
    }
}
