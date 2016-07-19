package vn.k2t.traficjam.user;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import vn.k2t.traficjam.R;
import vn.k2t.traficjam.database.queries.SQLUser;
import vn.k2t.traficjam.model.UserTraffic;
import vn.k2t.traficjam.untilitis.AppConstants;

public class RegisterActivity extends AppCompatActivity {
    @Bind(R.id.edt_email)
    EditText edt_email;
    @Bind(R.id.edt_user_name)
    EditText edt_user_name;
    @Bind(R.id.edt_pass)
    EditText edt_pass;
    @Bind(R.id.edt_repass)
    EditText edt_repass;
    @Bind(R.id.btn_loginAccount)
    Button btn_register;
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    private String username, email, pass;
    DatabaseReference mDatabase;
    UserTraffic mUser;
    SQLUser sqlUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        this.setFinishOnTouchOutside(false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sqlUser = new SQLUser(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng ký...");
        progressDialog.setCancelable(false);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edt_email.getText().toString();
                pass = edt_pass.getText().toString();
                String repass = edt_repass.getText().toString();
                username = edt_user_name.getText().toString();
                if (email.isEmpty() || pass.isEmpty() || repass.isEmpty() || username.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Ban chua nhap du thong tin", Toast.LENGTH_SHORT).show();
                } else if (pass.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "mat khau tren 6 ky tu", Toast.LENGTH_SHORT).show();
                    edt_pass.setText("");
                    edt_repass.setText("");

                } else if (!pass.equals(repass)) {
                    Toast.makeText(RegisterActivity.this, "mat khau khong trung khop", Toast.LENGTH_SHORT).show();
                    edt_pass.setText("");
                    edt_repass.setText("");
                } else {
                    registerAccount(email, pass);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    public void registerAccount(final String email, final String pass) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {

                    String uid = task.getResult().getUser().getUid();
                    String provider = task.getResult().getUser().getProviderId();
                    mUser = new UserTraffic(uid, username, "", email, provider, "", "", "", 1, "");
                    mDatabase.child(AppConstants.USER).child(uid).setValue(mUser);

                    sqlUser.insertUser(mUser);

//                    SharedPreferences preferences=getApplicationContext().getSharedPreferences("my_data",MODE_PRIVATE);
//                    SharedPreferences.Editor editor=preferences.edit();
//                    editor.putString("username",username);
//                    Log.e("Register: ",username);
//                    editor.commit();

                    Toast.makeText(RegisterActivity.this, "dky thanh cong", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
//                    new AlertDialog.Builder(getApplicationContext()).setMessage("Đăng ký thành công. Vui lòng kiểm tra email để xác thực").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent();
//                            intent.putExtra("email", email);
//                            intent.putExtra("pass", pass);
//                            setResult(111, intent);
//                            finish();
//                        }
//                    }).show();
                } else {
//                    new AlertDialog.Builder(getApplicationContext()).setMessage("Đăng ký khong thành công. Vui lòng thu lai").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }).show();
                    Toast.makeText(RegisterActivity.this, "dang ky that bai", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
