package com.ayon.testnow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.ayon.testnow.Login.user;
import static com.ayon.testnow.Login.uid;

public class SignUpActivity extends AppCompatActivity {

    EditText email, name, dept, phn;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ImageView imageView = findViewById(R.id.imageView);
        Glide.with(this).load(user.getPhotoUrl()).into(imageView);
        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        dept = findViewById(R.id.dept);
        phn = findViewById(R.id.phn);
        email.setText(user.getEmail());
        name.setText(user.getDisplayName());
    }

    public void signUp(View view) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(getString(R.string.db_key_users));
        Map<String, Object> dataMap = new HashMap<>();
        String nameString, deptString, reg_number;
        nameString = name.getText().toString();
        deptString = dept.getText().toString();
        reg_number = phn.getText().toString();
        if (nameString.equalsIgnoreCase("") || deptString.equalsIgnoreCase("") || reg_number.equalsIgnoreCase(""))
            return;
        dataMap.put(getString(R.string.name), nameString);
        dataMap.put(getString(R.string.email), user.getEmail());
        dataMap.put(getString(R.string.dept), deptString);
        dataMap.put(getString(R.string.admin), false);
        dataMap.put(getString(R.string.reg), reg_number);
        dataMap.put(getString(R.string.photo_url), user.getPhotoUrl().toString());
        Log.d("Tag", "signUp: " + user.getPhotoUrl());

//        myRef.child(uid).child(getString(R.string.name)).setValue(name.getText().toString());
//        myRef.child(uid).child(getString(R.string.email)).setValue(user.getEmail());
//        myRef.child(uid).child(getString(R.string.dept)).setValue(dept.getText().toString());
//        myRef.child(uid).child(getString(R.string.admin)).setValue(false);
//        myRef.child(uid).child(getString(R.string.active)).setValue(false);
        myRef.child(uid).updateChildren(dataMap);
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
