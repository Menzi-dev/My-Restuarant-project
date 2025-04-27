package com.example.urbanstylerestaurant;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameRegister, emailRegister, passwordRegister ,confirmPassword ,surnameRegister;
    private RadioGroup roleRadioGroup;
    private Button createAccountButton;
    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameRegister = findViewById(R.id.nameRegister);
        surnameRegister = findViewById(R.id.surnameRegister);
        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        confirmPassword = findViewById(R.id.confirmPassword);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        createAccountButton = findViewById(R.id.createAccountButton);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("users");

        createAccountButton.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_button));
            registerUser();
        });
    }

    private void registerUser() {
        String name = nameRegister.getText().toString();
        String surname = surnameRegister.getText().toString();
        String email = emailRegister.getText().toString();
        String password = passwordRegister.getText().toString();
        String PasswordConfimation = confirmPassword.getText().toString();

        int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();
        if (selectedRoleId == -1) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedRoleButton = findViewById(selectedRoleId);
        String role = selectedRoleButton.getText().toString();

        if (name.isEmpty() || email.isEmpty() || PasswordConfimation.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, PasswordConfimation).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = auth.getCurrentUser().getUid();
                User user = new User(name, surname , email, role); // Surname not in sketch, passing empty string
                database.child(userId).setValue(user);
                Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Go back to login after registration
            } else {
                Toast.makeText(this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}