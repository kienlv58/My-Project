package vn.k2t.traficjam.user;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import vn.k2t.traficjam.R;

public class LoginUserActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.edt_email)
    EditText edt_email;
    @Bind(R.id.edt_pass)
    EditText edt_pass;
    @Bind(R.id.btn_loginAccount)
    Button btn_loginAccount;
    @Bind(R.id.btn_loginFB)
    Button btn_loginFB;
    @Bind(R.id.btn_loginGoogle)
    Button btn_loginGG;
    @Bind(R.id.txtv_forgotpass)
    TextView txtv_forgotpass;
    @Bind(R.id.txtv_register)
    TextView txtv_register;
    //firebase
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    //login facebook
    CallbackManager callbackManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login_user);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();


        callbackManager = CallbackManager.Factory.create();
        btn_loginAccount.setOnClickListener(this);
        btn_loginFB.setOnClickListener(this);
        btn_loginGG.setOnClickListener(this);
        txtv_forgotpass.setOnClickListener(this);
        txtv_register.setOnClickListener(this);

        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    String uid = firebaseUser.getUid();
                    String email = firebaseUser.getEmail();
                    String name = firebaseUser.getDisplayName();
                    String avatar = String.valueOf(firebaseUser.getPhotoUrl());
                    Toast.makeText(LoginUserActivity.this,email+"===="+name,Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(LoginUserActivity.this,"user null",Toast.LENGTH_SHORT).show();
            }
        };

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handeFBaccesstoken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.btn_loginFB:
                LoginManager.getInstance().logInWithReadPermissions(LoginUserActivity.this, Arrays.asList("public_profile", "email", "user_friends"));
                break;
        }

    }

    public void handeFBaccesstoken(AccessToken accessToken){
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginUserActivity.this,task+"",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(LoginUserActivity.this,"login fail",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }
}
