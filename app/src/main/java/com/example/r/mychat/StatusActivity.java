package com.example.r.mychat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Button updatestat;
    private EditText edtstat;
    private FirebaseUser mcurr;
    private DatabaseReference mstatref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        edtstat=(EditText) findViewById(R.id.edtstat);
        updatestat=(Button) findViewById(R.id.btnupdatestat);
        String statval=getIntent().getStringExtra("statvalue");
        edtstat.setText(statval);

        mcurr=FirebaseAuth.getInstance().getCurrentUser();
        String cuid=mcurr.getUid();
        mstatref= FirebaseDatabase.getInstance().getReference().child("userrdata").child(cuid);

        updatestat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stat=edtstat.getText().toString();
                mstatref.child("Status").setValue(stat).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(StatusActivity.this, "Sucess",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}
