package com.example.zzu.huzhucommunity.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.zzu.huzhucommunity.R;
import com.example.zzu.huzhucommunity.asynchttp.AsyncHttpCallback;
import com.example.zzu.huzhucommunity.commonclass.MyApplication;

/**
 * 登录界面
 */
public class LoginActivity extends BaseActivity implements AsyncHttpCallback {
    private EditText passwordEditText;
    private ImageButton cancelButton;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            if(TextUtils.isEmpty(s)) cancelButton.setVisibility(View.GONE);
            else cancelButton.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) actionBar.hide();

        cancelButton = findViewById(R.id.LoginActivity_password_cancel_button);
        passwordEditText = findViewById(R.id.LoginActivity_password_edit_text);
        passwordEditText.addTextChangedListener(textWatcher);

        addListener(R.id.LoginActivity_login_button);
        addListener(R.id.LoginActivity_register_text_view);
        addListener(R.id.LoginActivity_password_cancel_button);
    }

    /**
     * 添加监听器
     * @param res 控件ID
     */
    public void addListener(final int res){
        findViewById(res).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (res){
                    case R.id.LoginActivity_login_button:
                        onSuccess(1);
//                        String account = accountEditText.getText().toString();
//                        String password = passwordEditText.getText().toString();
//                        if(TextUtils.isEmpty(account) || TextUtils.isEmpty(password)){
//                            Toast.makeText(LoginActivity.this, "请输入账号和密码", Toast.LENGTH_SHORT).show();
//                            break;
//                        }
//                        LoginRegister.getOurInstance().login(account, password,LoginActivity.this);
                        break;
                    case R.id.LoginActivity_register_text_view:
                        RegisterActivity.startMe(LoginActivity.this);
                        break;
                    case R.id.LoginActivity_password_cancel_button:
                        passwordEditText.setText("");
                        break;
                }
            }
        });
    }

    /**
     * 回调处理HTTP请求成功
     * @param code 返回状态
     */
    @Override
    public void onSuccess(int code) {
        Toast.makeText(MyApplication.getContext(), "Success login", Toast.LENGTH_SHORT).show();
        MainActivity.startMe(this);
        finish();
    }

    /**
     * 回调处理HTTP请求异常
     * @param code 返回状态
     */
    @Override
    public void onError(int code) {
        Toast.makeText(MyApplication.getContext(), "Error on login", Toast.LENGTH_SHORT).show();
    }
    public static void startMe(Context context){
        context.startActivity(new Intent(context, LoginActivity.class));
    }
}
