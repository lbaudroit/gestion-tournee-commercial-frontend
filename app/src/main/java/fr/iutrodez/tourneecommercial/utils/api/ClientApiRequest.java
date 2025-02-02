package fr.iutrodez.tourneecommercial.utils.api;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import fr.iutrodez.tourneecommercial.modeles.Client;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class ClientApiRequest extends ApiRessource {
    private static final String RESOURCE_NAME = "client";
    private static final String TAG = "ClientApiRequest";

    public ClientApiRequest(RequestQueue requestQueue) {
        super(requestQueue);
    }

    public void getAll(Context context, SuccessCallback<List<Client>> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/";
        super.getWithTokenAsArray(context, url, response -> {
            successCallback.onSuccess(extractClients(response));
        }, errorCallback::onError);
    }


    public void create() {
        System.out.println("NOT IMPLEMETED");
    }

    public void getNumberOfPages() {
        System.out.println("NOT IMPLEMETED");
    }

    public void getPage(int page) {
        System.out.println("NOT IMPLEMETED");
    }

    public void getOne(int id) {
        System.out.println("NOT IMPLEMETED");
    }

    public void update(int id) {
        System.out.println("NOT IMPLEMETED");
    }

    public void delete(int id) {
        System.out.println("NOT IMPLEMETED");
    }

    private List<Client> extractClients(JSONArray response) {
        List<Client> clients = new java.util.ArrayList<>();
        Gson gson = new Gson();
        for (int i = 0; i < response.length(); i++) {
            try {
                clients.add(gson.fromJson(response.getJSONObject(i).toString(), Client.class));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return clients;
    }
}
