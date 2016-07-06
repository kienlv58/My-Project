package vn.k2t.traficjam.user;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import vn.k2t.traficjam.R;

public class LoginUserActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks{

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
    //login google
    GoogleApiClient mGoogleApiClient;



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

        //login google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_app))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, (GoogleApiClient.OnConnectionFailedListener) this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i){
            case R.id.btn_loginFB:
                LoginManager.getInstance().logInWithReadPermissions(LoginUserActivity.this, Arrays.asList("public_profile", "email", "user_friends"));
                break;
            case R.id.btn_loginGoogle:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, 100);
                break;
        }

    }
//fb + firebase
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

    //google + firebase
    public void firebaseAuthWithGoogle(GoogleSignInAccount account){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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
        if (mAuthStateListener != null )
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 100){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
