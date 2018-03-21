package com.example.r.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class adduserinfo extends AppCompatActivity {
    private DatabaseReference userdata;
    private FirebaseUser cuurentu;
    private CircleImageView dispic;
    private EditText namme, status;
    private Button btnimg,btnupdate;
    private StorageReference mStorageRef;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adduserinfo);


        namme = (EditText) findViewById(R.id.usname);
        status = (EditText) findViewById(R.id.userstat);
        btnimg = (Button) findViewById(R.id.ChooseDp);
        btnupdate = (Button) findViewById(R.id.btngo);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        dispic = (CircleImageView) findViewById(R.id.circleImageView);
        cuurentu = FirebaseAuth.getInstance().getCurrentUser();





        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent move=new Intent(adduserinfo.this,Firstpage.class);
                startActivity(move);
            }
        });



        btnimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galler = new Intent();
                galler.setType("image/");
                galler.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galler, "Choose ur dP"), 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {

            Uri imageuri = data.getData();
            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                progressDialog=new ProgressDialog(adduserinfo.this);
                progressDialog.setMessage("Please wait");
                progressDialog.setTitle("Uploading img");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                cuurentu = FirebaseAuth.getInstance().getCurrentUser();

                File thumbfilepath=new File(resultUri.getPath());
                String cuid = cuurentu.getUid();


                try {
                    Bitmap thum_bitmap=new Compressor(this).setMaxHeight(200).setMaxWidth(200).setQuality(50).compressToBitmap(thumbfilepath);
                    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                    thum_bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                    final byte[] th_byte=byteArrayOutputStream.toByteArray();
                    StorageReference filepath = mStorageRef.child("profile_img").child(cuid+".jpg");
                    final StorageReference thum_filepath = mStorageRef.child("profile_img").child("thumb_img").child(cuid+".jpg");

                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                final String down_url_img=task.getResult().getDownloadUrl().toString();
                                UploadTask uploadTask= thum_filepath.putBytes(th_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> th_task) {

                                        String thum_downUrl=th_task.getResult().getDownloadUrl().toString();

                                        if(th_task.isSuccessful()){
                                            String ema=namme.getText().toString();
                                            String sat=status.getText().toString();

                                            Map updatehm=new HashMap();
                                            updatehm.put("Name",ema);
                                            updatehm.put("Status",sat);
                                            updatehm.put("Image",down_url_img);
                                            updatehm.put("thumb_img",thum_downUrl);
                                            updatehm.put("location","Private");
                                            String cuid = cuurentu.getUid();
                                            userdata = FirebaseDatabase.getInstance().getReference().child("userrdata").child(cuid);
                                            userdata.keepSynced(true);
                                            userdata.setValue(updatehm).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    Toast.makeText(adduserinfo.this, "Sucess",
                                                            Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            });


                                        }
                                    }
                                });


                            } else {
                                Toast.makeText(adduserinfo.this, "failed to upload img",
                                        Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                            }



                        }

                    });} catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
