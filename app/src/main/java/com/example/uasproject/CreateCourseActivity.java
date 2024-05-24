package com.example.uasproject;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.graphics.Bitmap;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;

public class CreateCourseActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imageView;
    private StorageReference storageReference;
    private EditText edtNama, edtTentang, edtPengajar, edtHarga;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private Button buttonSelectImage, buttonSimpan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            finish();
        });

        imageView = findViewById(R.id.imagePreview);
        edtNama = findViewById(R.id.edt_nama);
        edtTentang = findViewById(R.id.edt_tentang);
        edtPengajar = findViewById(R.id.edt_pengajar);
        edtHarga = findViewById(R.id.edt_harga);
        buttonSelectImage = findViewById(R.id.select_img);
        buttonSimpan = findViewById(R.id.btn_simpan);
        progressBar = findViewById(R.id.progressBar);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        buttonSelectImage.setOnClickListener(v -> openFileChooser());
        buttonSimpan.setOnClickListener(v -> uploadCourse());

    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            }catch(Exception e){
                Log.e("Image Error", String.valueOf(e));
            }
        }
    }

    private void uploadCourse(){
        String nama = edtNama.getText().toString();
        String tentang = edtTentang.getText().toString();
        String harga = edtHarga.getText().toString();
        String pengajar = edtPengajar.getText().toString();
        FirebaseUser user = mAuth.getInstance().getCurrentUser();
        String user_id = user.getUid();

        try {
            DBFirebase db = new DBFirebase();

            buttonSimpan.setBackgroundResource(R.drawable.button_shape_off);
            buttonSimpan.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(getResources().getColor(R.color.bluePrimary)));

            if (imageUri != null) {
                StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
                fileReference.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            fileReference.getDownloadUrl().addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    Uri downloadUri = task.getResult();
                                    String image = downloadUri.toString();

                                    db.createCourse(user_id, nama, pengajar, tentang, image, Integer.valueOf(harga));

                                    buttonSimpan.setBackgroundResource(R.drawable.button_shape);
                                    buttonSimpan.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    progressBar.setIndeterminate(false);

                                    Toast.makeText(CreateCourseActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            buttonSimpan.setBackgroundResource(R.drawable.button_shape);
                            buttonSimpan.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            progressBar.setIndeterminate(false);
                            Toast.makeText(CreateCourseActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                buttonSimpan.setBackgroundResource(R.drawable.button_shape);
                buttonSimpan.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                progressBar.setIndeterminate(false);
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }

        }catch(Exception e){
            buttonSimpan.setBackgroundResource(R.drawable.button_shape);
            buttonSimpan.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            progressBar.setIndeterminate(false);
            Log.e("Input Course", String.valueOf(e));
        }
    }
}