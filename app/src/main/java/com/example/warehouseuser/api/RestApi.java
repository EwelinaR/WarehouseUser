package com.example.warehouseuser.api;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;

import com.example.warehouseuser.AuthenticationInterceptor;
import com.example.warehouseuser.Instrument;
import com.example.warehouseuser.RequestResponseStatus;
import com.example.warehouseuser.SessionManager;
import com.example.warehouseuser.UpdateInstrument;
import com.example.warehouseuser.UserInfo;
import com.example.warehouseuser.fragment.update.FragmentUpdate;
import com.example.warehouseuser.fragment.update.FragmentUpdateList;
import com.example.warehouseuser.fragment.update.OnAuthenticationUpdate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApi implements FragmentUpdate {

    static final String BASE_URL = "http://192.168.1.132:8080/";
    private final SessionManager manager;
    private final String CLIENT_ID = "client";
    private final String CLIENT_SECRET = "secret";
    private final String BEARER_PREFIX = "Bearer ";

    private int updateAmount;
    private FragmentUpdate observer;

    public RestApi(Context context) {
        this.manager = new SessionManager(context);
    }


    private RetrofitApi createRetrofitApi(String token) {
        Log.i("TOKEN", token);
        AuthenticationInterceptor interceptor =
                new AuthenticationInterceptor(token);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(RetrofitApi.class);
    }

    public void getToken(OnAuthenticationUpdate update, String username, String password) {
        String authToken = Credentials.basic(CLIENT_ID, CLIENT_SECRET);

        RetrofitApi retrofitApi = createRetrofitApi(authToken);

        Call<TokenResponse> call = retrofitApi.getAccessToken(username, password, "password");
        call.enqueue(new SignInCallback(manager, update));
    }

    public void refreshToken(OnAuthenticationUpdate update) {
        String authToken = Credentials.basic(CLIENT_ID, CLIENT_SECRET);
        RetrofitApi retrofitApi = createRetrofitApi(authToken);

        Call<TokenResponse> call = retrofitApi.refreshToken("refresh_token", manager.getRefreshToken());
        call.enqueue(new SignInCallback(manager, update));
    }

    public void saveUserRole() {
        RetrofitApi retrofitApi = createRetrofitApi(BEARER_PREFIX + manager.getAccessToken());

        Call<UserInfo> call = retrofitApi.getUserInfo(manager.getAccessToken());
        call.enqueue(new UserInfoCallback(manager));
    }

    public void getInstruments(FragmentUpdateList fragmentView) {
        RetrofitApi retrofitApi = createRetrofitApi(BEARER_PREFIX + manager.getAccessToken());

        Call<List<Instrument>> call = retrofitApi.getInstruments();
        call.enqueue(new ListCallback(fragmentView));
    }

    public void sendUpdates(List<UpdateInstrument> updateInstruments, FragmentUpdate observer) {
        RetrofitApi retrofitApi = createRetrofitApi(BEARER_PREFIX + manager.getAccessToken());
        Call<ResponseBody> call;
        this.observer = observer;
        updateAmount = updateInstruments.size();
        for (UpdateInstrument updateInstrument: updateInstruments) {
            call = null;
            switch (updateInstrument.getRequestType()) {
                case "POST":
                    call = retrofitApi.addInstrument(updateInstrument.getInstrument());
                    break;
                case "PUT":
                    call = retrofitApi.updateInstrument(updateInstrument.getInstrument().getId(),
                            updateInstrument.getInstrument(), updateInstrument.getDate());
                    break;
                case "DELETE":
                    call = retrofitApi.deleteInstrument(updateInstrument.getInstrument().getId());
                    break;
                case "INCREASE":
                    call = retrofitApi.increaseQuantity(updateInstrument.getInstrument().getId(), updateInstrument.getAmount());
                    break;
                case "DECREASE":
                    call = retrofitApi.decreaseQuantity(updateInstrument.getInstrument().getId(), updateInstrument.getAmount());
                    break;
            }
            if (call != null)
                call.enqueue(new SimpleCallback(this));
        }
    }

    private ArrayMap<String, String> map = new ArrayMap<>();

    @Override
    public void updateView(RequestResponseStatus status, String message) {
        System.out.println(message);
        if (status == RequestResponseStatus.OK) {
            Map<String, String> mm = new HashMap<>();
            mm.put("0", "producent");
            mm.put("1", "model");
            mm.put("2", "cena");

            if (!message.isEmpty()) {
                String[] fields = message.split(";");
                String objectId = fields[0];
                // for all modified fields in object
                for (String field : fields) {
                    String[] values = field.split(":");
                    if (values.length != 3) continue;
                    map.put(objectId + ";" + values[0],
                            "Pole " + mm.get(values[0]) + " zostało nadpisane:\n'"
                                    + values[1] + "' -> '" + values[2] + "'");
                }
            }
        } else if (status == RequestResponseStatus.NOT_FOUND) {
         //   String[] fields = message.split(";");
          //  map.put(String.valueOf(id), "Nie można zmienić: '" + field2 + "' Instrument został usunięty.");
        }

        System.out.println(map.toString());
        updateAmount--;
        if (updateAmount == 0) {
            String m = "\tKonflikty!";
            for (String a: map.values()) {
                m += "\n\n"+a;
            }
            observer.updateView(status, m);
            map.clear();
        }
    }
}