package com.example.r.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jaeger.library.StatusBarUtil;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog mProgress;
    private EditText username, password,emai;
    private Button loginButton, signin,regg;
    private String user, pass,email;
    private TextView txt;
    private ImageView phonelogin,googlelogin;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mdataref;
    private Toolbar mtoolbar;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    GoogleApiClient mGoogleApiClient;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarUtil.setTransparent(MainActivity.this);
        mtoolbar = (Toolbar) findViewById(R.id.chatbarq);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("My Chat App Register/Login");

        mAuth = FirebaseAuth.getInstance();

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_pass);

        phonelogin=(ImageView) findViewById(R.id.phonelogin);
        googlelogin=(ImageView) findViewById(R.id.btngogle);
        mProgress=new ProgressDialog(this);
        username = (EditText)findViewById(R.id.edtemail);
        emai = (EditText)findViewById(R.id.editText3);
        password = (EditText)findViewById(R.id.edtpass);
        loginButton = (Button)findViewById(R.id.btnlogin);
        signin = (Button)findViewById(R.id.btnsignin);
        regg = (Button)findViewById(R.id.btnreg);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signin.setVisibility(View.INVISIBLE);
        emai.setVisibility(View.INVISIBLE);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress.setTitle("Signing in");
                mProgress.setMessage("Please wait");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();
                user=username.getText().toString();
                pass=password.getText().toString();

                if(user.isEmpty()&&pass.isEmpty()) {
                }else{  setLoginButton(user, pass);}

            }
        });


        googlelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
signIn();
            }
        });

        regg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regg.setVisibility(View.INVISIBLE);
                emai.setVisibility(View.VISIBLE);
                signin.setVisibility(View.VISIBLE);

            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=emai.getText().toString();
                user=username.getText().toString();
                pass=password.getText().toString();
                if(!TextUtils.isEmpty(user)||!TextUtils.isEmpty(pass)) {
                    mProgress.setTitle("Registering");
                    mProgress.setMessage("Please wait");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                    if(email.isEmpty()&&user.isEmpty()&&pass.isEmpty()){

                }else{setSignin(email, user, pass);}}
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(MainActivity.this,"onAuthstatechange:sigin",Toast.LENGTH_SHORT).show();
                }
            }
        };
        phonelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneactivity=new Intent(MainActivity .this, PhoneVeri.class);
                startActivity(phoneactivity);
                finish();

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        FirebaseUser u=FirebaseAuth.getInstance().getCurrentUser();
        if(u!=null ){
            startActivity(new Intent(MainActivity.this,Firstpage.class ));
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void setLoginButton(final String user, String pass){
        mAuth.signInWithEmailAndPassword(user, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            mProgress.dismiss();
                            Toast.makeText(MainActivity.this, "failed",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            mProgress.hide();
                            FirebaseUser currentuser=FirebaseAuth.getInstance().getCurrentUser();
                            String uid=currentuser.getUid();
                            mdataref= FirebaseDatabase.getInstance().getReference().child("userrdata").child(uid);
                            String dev_token= FirebaseInstanceId.getInstance().getToken();
                            mdataref.child("dev_token").setValue(dev_token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Intent intent = new Intent(MainActivity .this, Firstpage.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                        }
                    }
                });
    }

    public void setSignin(final String ema, final String user, final String pass){
        mAuth.createUserWithEmailAndPassword(user, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "failed1",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            FirebaseUser currentuser=FirebaseAuth.getInstance().getCurrentUser();
                            String uid=currentuser.getUid();
                            mdataref= FirebaseDatabase.getInstance().getReference().child("userrdata").child(uid);
                            HashMap<String,String> userMap=new HashMap<String, String>();
                            userMap.put("Name",ema);
                            userMap.put("Status","Available");
                            userMap.put("Image","default");
                            userMap.put("thumb_img","default");
                            mdataref.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        setLoginButton(user,pass);
                                    }else{
                                        mProgress.hide();
                                        Toast.makeText(MainActivity.this, "failed2",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
    }




    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                 GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                         if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else{

                             startActivity(new Intent(MainActivity.this, adduserinfo.class));
                             finish();

                         }
                    }
                });
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
