package com.proyectointegrado.Login;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.proyecto.appproyectointegrado.R;
import com.proyectointegrado.Database_manager.Users;
import com.proyectointegrado.Database_manager.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import rx.Subscriber;
import io.reactivex.*;

public class Registre_activity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "PRUEBA";
    private static CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static Intent i;
    private static Button btn_register;
    private static EditText et_name, et_surname, et_pass, et_email, et_type;
    private static String name, surname, email, insert_pass, pass, type;
    private static  DefaultHttpClient client;
    private static  JSONObject respJSONObject;
    private static Users user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registre);
        btn_register = findViewById(R.id.btn_register);
        et_name = findViewById(R.id.et_user_registre);
        et_surname = findViewById(R.id.et_surname_registre);
        et_email = findViewById(R.id.et_mail_registre);
        et_pass = findViewById(R.id.et_pass_registre);
        et_type = findViewById(R.id.et_type_registre);
        et_name.setText("Adolfo");
        et_surname.setText("Prado");
        et_email.setText("adolf@gmail.com");
        et_pass.setText("123");
        et_type.setText("Admin");


        //in_api_service = Utils.getAPIService();
        btn_register.setOnClickListener(this);

    }

    /*private void registerUser(String name, String surname, String pass, String email, String type) {
       in_api_service.registerUser(name,surname,email,pass,type).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Users>()
        {

        });
    }*/

    private void mostrarToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:

                name = et_name.getText().toString().trim();
                surname = et_surname.getText().toString().trim();
                email = et_email.getText().toString().trim();
                insert_pass = et_pass.getText().toString().trim();
                type = et_type.getText().toString().trim();

                pass = Utils.encrypt_pass_sha512(insert_pass);

                log(insert_pass);
                log(pass);

                new ExecuteTaskRegisterUser().execute();
        }
    }

    private void montarUser(Users users) {
        users.setName(et_name.getText().toString().trim());
        users.setSurname(et_surname.getText().toString().trim());
        users.setEmail(et_email.getText().toString().trim());
        users.setPass(et_pass.getText().toString().trim());
        users.setType(et_type.getText().toString().trim());
    }

    private void log(String mensaje) {
        Log.i(TAG, mensaje);
    }

    private class ExecuteTaskRegisterUser extends AsyncTask<String, String, Users> {
        @Override
        protected Users doInBackground(String... params) {


             user = new Users();

             client = new DefaultHttpClient();
             respJSONObject = null;
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 1000);

            try {
                HttpPost post = new HttpPost("https://proyecto-studium.herokuapp.com/api/user/setuser");
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                nameValuePairs.add(new BasicNameValuePair("Nombre", name));
                nameValuePairs.add(new BasicNameValuePair("Surname", surname));
                nameValuePairs.add(new BasicNameValuePair("Email", email));
                nameValuePairs.add(new BasicNameValuePair("Pass", pass));
                nameValuePairs.add(new BasicNameValuePair("Rol", type));

                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = client.execute(post);
                String responseStr = String.valueOf(EntityUtils.toString(response.getEntity()));
                respJSONObject = new JSONObject(responseStr);

                if (respJSONObject != null) {
                    log(responseStr);
                    log("SI");
                }

            } catch (Exception e) {
                log(e.getMessage());
            }
            return user;
        }

        @Override
        protected void onPostExecute(Users users) {
            super.onPostExecute(users);

            log("TODO OK");
        }
    }
}
