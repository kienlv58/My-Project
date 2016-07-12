package vn.k2t.traficjam.user;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.database.queries.SQLUser;
import vn.k2t.traficjam.model.UserTraffic;
import vn.k2t.traficjam.untilitis.AppConstants;

public class LoginUserActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

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
    String email, password;
    ProgressDialog progressDialog;
    SQLUser sqlUser;
    String tokenGG;
    String emailGG;
    String avatar;
    UserTraffic mUser;


    //firebase
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    DatabaseReference mDatabase;

    //login facebook
    CallbackManager callbackManager;
    //login google
    GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions googleSignInOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login_user);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        callbackManager = CallbackManager.Factory.create();
        btn_loginAccount.setOnClickListener(this);
        btn_loginFB.setOnClickListener(this);
        btn_loginGG.setOnClickListener(this);
        txtv_forgotpass.setOnClickListener(this);
        txtv_register.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Đang gửi...");
        progressDialog.setMessage(getString(R.string.is_send));

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    new AsyncTask<Void,Void,Void>(){
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            progressDialog.setMessage(getString(R.string.loging));
                            progressDialog.show();
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            sqlUser = new SQLUser(getApplicationContext());
                            sqlUser.insertUser(mUser);
                            Intent intent = new Intent();
                            setResult(200, intent);
                            progressDialog.dismiss();
                            Toast.makeText(LoginUserActivity.this, mUser.getEmail() + "====" + mUser.getName(), Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            String uid = firebaseUser.getUid();
                            String email = firebaseUser.getEmail();
                            String name = firebaseUser.getDisplayName();
                            //String avatar = String.valueOf(firebaseUser.getPhotoUrl());
                            String uidProvider = firebaseUser.getProviderId();

                            mUser = new UserTraffic(uid, name, avatar, email, uidProvider, "", "", "", 1, "");
                            mDatabase.child(AppConstants.USER).child(uid).setValue(mUser);

                            return null;
                        }
                    }.execute();

                } else
                    Toast.makeText(LoginUserActivity.this, "user null", Toast.LENGTH_SHORT).show();
            }
        };

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressDialog.setMessage(getString(R.string.loging));
                progressDialog.show();
                Profile profile = Profile.getCurrentProfile();
                String uid_fb = profile.getId();
                avatar = "http://graph.facebook.com/" + uid_fb + "/picture?type=large";
                //avatar = String.valueOf(profile.getProfilePictureUri(400,800));
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
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* FragmentActivity */, (GoogleApiClient.OnConnectionFailedListener) this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .addApi(Plus.API)
//                .build();
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.web_app)).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .addApi(Plus.API)
                .build();

    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        switch (i) {
            case R.id.btn_loginFB:
                LoginManager.getInstance().logInWithReadPermissions(LoginUserActivity.this, Arrays.asList("public_profile", "email", "user_friends"));
                break;
            case R.id.btn_loginGoogle:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, 1000);
                break;
            case R.id.btn_loginAccount:
                email = edt_email.getText().toString();
                password = edt_pass.getText().toString();
                if (email.isEmpty() || !isValidEmail(email) || password.isEmpty() || password.length() < 6) {
                    Toast.makeText(LoginUserActivity.this, "email hoac mat khau khong dung dinh dang", Toast.LENGTH_SHORT).show();
                } else {
                    singInAccount(email, password);
                }
                break;
            case R.id.txtv_register:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivityForResult(intent, 111);
                break;
            case R.id.txtv_forgotpass:
                forgotPass();
                break;
        }

    }

    //acount + firebase
    public void singInAccount(String email, String pass) {
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginUserActivity.this, task + "", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginUserActivity.this, "Tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    //fb + firebase
    public void handeFBaccesstoken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(LoginUserActivity.this, task + "", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginUserActivity.this, "login fail", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    //google + firebase
    public void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        avatar = String.valueOf(account.getPhotoUrl());
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginUserActivity.this, task + "", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginUserActivity.this, R.string.login_fail, Toast.LENGTH_SHORT).show();
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
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                progressDialog.setMessage(getString(R.string.loging));
                progressDialog.show();
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                }

            }

        if (requestCode == 111) {
            edt_email.setText(data.getStringExtra("email"));
            edt_pass.setText(data.getStringExtra("pass"));
        }
    }


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void forgotPass() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_forgotpass, null);
        final EditText edt_ForgotPass = (EditText) v.findViewById(R.id.edt_forgotpass);
        new AlertDialog.Builder(this).setTitle("Quên mật khẩu").setView(v).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String _email = edt_ForgotPass.getText().toString();
                if (_email.isEmpty() || !isValidEmail(_email)) {
                    Toast.makeText(LoginUserActivity.this, "email khong dung dinh dang", Toast.LENGTH_SHORT).show();
                    forgotPass();
                } else
                    aa(_email, dialog);
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();

    }


    public void aa(String _email, final DialogInterface dialogInterface) {
        progressDialog.show();
        mAuth.sendPasswordResetEmail(_email).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                dialogInterface.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(LoginUserActivity.this, "check email", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginUserActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
