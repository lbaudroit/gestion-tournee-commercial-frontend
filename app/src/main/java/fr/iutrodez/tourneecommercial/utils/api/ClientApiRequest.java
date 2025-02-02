package fr.iutrodez.tourneecommercial.utils.api;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import fr.iutrodez.tourneecommercial.modeles.Adresse;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.modeles.Contact;
import fr.iutrodez.tourneecommercial.modeles.dto.ClientCreation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


    public void create(Context context, String businessName, String addressLabel, String postalCode, String city,
                       String description, String contactName, String contactFirstname, String contactPhone,
                       boolean isClient, SuccessCallback<String> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/";
        JSONObject body = createClientCreationBody(businessName, addressLabel, postalCode, city, description, contactName,
                contactFirstname, contactPhone, isClient);
        super.postWithToken(context, url, body, response -> {
            String message = extractMessage(response);
            successCallback.onSuccess(message);
        }, errorCallback::onError);
    }

    public void getNumberOfPages(Context context, SuccessCallback<Integer> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/count";
        super.getWithToken(context, url, response -> {
            try {
                successCallback.onSuccess(response.getInt("nombre"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, errorCallback::onError);

    }

    public void getPage(Context context, int page, SuccessCallback<List<Client>> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/lazy?page=" + page;
        super.getWithTokenAsArray(context, url, response -> {
            successCallback.onSuccess(extractClients(response));
        }, errorCallback::onError);
    }

    public void getOne(Context context, String id, SuccessCallback<Client> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/" + id;
        super.getWithToken(context, url, response -> {
            Gson gson = new Gson();
            successCallback.onSuccess(gson.fromJson(response.toString(), Client.class));
        }, errorCallback::onError);
    }

    public void update(Context context, String id, String businessname, String adressLabel, String postalCode, String city,
                       String description, String contactName, String contactFirstname, String contactPhone,
                       boolean isClient, SuccessCallback<String> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/" + id;
        JSONObject body = createClientCreationBody(businessname, adressLabel, postalCode, city, description, contactName,
                contactFirstname, contactPhone, isClient);
        super.putWithToken(context, url, body, response -> {
            String message = extractMessage(response);
            successCallback.onSuccess(message);
        }, errorCallback::onError);

    }

    public void delete(Context context, String id, SuccessCallback<String> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/" + id;
        super.deleteWithToken(context, url, response -> {
            String message = extractMessage(response);
            successCallback.onSuccess(message);
        }, errorCallback::onError);

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

    private JSONObject createClientCreationBody(String businessname, String adressLabel, String postalCode, String city,
                                                String description, String contactName, String contactFirstname,
                                                String contactPhone, boolean isClient) {
        Adresse adress = new Adresse(adressLabel, postalCode, city);
        Contact contact = new Contact(contactName, contactFirstname, contactPhone);
        ClientCreation clientCreation = new ClientCreation(businessname, adress, description, contact, isClient);
        Gson gson = new Gson();
        String json = gson.toJson(clientCreation);
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractMessage(JSONObject response) {
        try {
            return response.getString("message");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
