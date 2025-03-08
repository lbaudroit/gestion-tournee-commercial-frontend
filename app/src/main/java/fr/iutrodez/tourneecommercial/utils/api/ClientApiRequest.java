package fr.iutrodez.tourneecommercial.utils.api;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import fr.iutrodez.tourneecommercial.model.Adresse;
import fr.iutrodez.tourneecommercial.model.Client;
import fr.iutrodez.tourneecommercial.model.Contact;
import fr.iutrodez.tourneecommercial.model.dto.ClientDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Class to make requests to the API for the Client resource
 */
public class ClientApiRequest extends ApiRessource {
    private static final String RESOURCE_NAME = "client";

    /**
     * Constructeur de la classe ClientApiRequest.
     *
     * @param requestQueue La file d'attente des requêtes.
     */
    public ClientApiRequest(RequestQueue requestQueue) {
        super(requestQueue);
    }

    /**
     * Récupère tous les clients.
     *
     * @param context         Le contexte de l'application.
     * @param successCallback La fonction de rappel en cas de succès.
     * @param errorCallback   La fonction de rappel en cas d'erreur.
     */
    public void getAll(Context context, SuccessCallback<List<Client>> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/";
        super.getWithTokenAsArray(context, url, response -> successCallback.onSuccess(extractClients(response)), errorCallback::onError);
    }

    /**
     * Crée un nouveau client.
     *
     * @param context          Le contexte de l'application.
     * @param businessName     Le nom de l'entreprise.
     * @param addressLabel     L'adresse.
     * @param postalCode       Le code postal.
     * @param city             La ville.
     * @param description      La description.
     * @param contactName      Le nom du contact.
     * @param contactFirstname Le prénom du contact.
     * @param contactPhone     Le téléphone du contact.
     * @param isClient         Indique si c'est un client.
     * @param successCallback  La fonction de rappel en cas de succès.
     * @param errorCallback    La fonction de rappel en cas d'erreur.
     */
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

    /**
     * Récupère le nombre de pages de clients.
     *
     * @param context         Le contexte de l'application.
     * @param successCallback La fonction de rappel en cas de succès.
     * @param errorCallback   La fonction de rappel en cas d'erreur.
     */
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

    /**
     * Récupère une page de clients.
     *
     * @param context         Le contexte de l'application.
     * @param page            Le numéro de la page.
     * @param successCallback La fonction de rappel en cas de succès.
     * @param errorCallback   La fonction de rappel en cas d'erreur.
     */
    public void getPage(Context context, int page, SuccessCallback<List<Client>> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/lazy?page=" + page;
        super.getWithTokenAsArray(context, url, response -> successCallback.onSuccess(extractClients(response)), errorCallback::onError);
    }

    /**
     * Récupère un client par son identifiant.
     *
     * @param context         Le contexte de l'application.
     * @param id              L'identifiant du client.
     * @param successCallback La fonction de rappel en cas de succès.
     * @param errorCallback   La fonction de rappel en cas d'erreur.
     */
    public void getOne(Context context, String id, SuccessCallback<Client> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/" + id;
        super.getWithToken(context, url, response -> {
            Gson gson = new Gson();
            successCallback.onSuccess(gson.fromJson(response.toString(), Client.class));
        }, errorCallback::onError);
    }

    /**
     * Met à jour un client.
     *
     * @param context          Le contexte de l'application.
     * @param id               L'identifiant du client.
     * @param businessname     Le nom de l'entreprise.
     * @param adressLabel      L'adresse.
     * @param postalCode       Le code postal.
     * @param city             La ville.
     * @param description      La description.
     * @param contactName      Le nom du contact.
     * @param contactFirstname Le prénom du contact.
     * @param contactPhone     Le téléphone du contact.
     * @param isClient         Indique si c'est un client.
     * @param successCallback  La fonction de rappel en cas de succès.
     * @param errorCallback    La fonction de rappel en cas d'erreur.
     */
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

    /**
     * Supprime un client.
     *
     * @param context         Le contexte de l'application.
     * @param id              L'identifiant du client.
     * @param successCallback La fonction de rappel en cas de succès.
     * @param errorCallback   La fonction de rappel en cas d'erreur.
     */
    public void delete(Context context, String id, SuccessCallback<String> successCallback, ErrorCallback errorCallback) {
        String url = RESOURCE_NAME + "/" + id;
        super.deleteWithToken(context, url, response -> {
            String message = extractMessage(response);
            successCallback.onSuccess(message);
        }, errorCallback::onError);
    }

    /**
     * Extrait une liste de clients à partir d'un JSONArray.
     *
     * @param response La réponse JSON contenant les clients.
     * @return La liste des clients.
     */
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

    /**
     * Crée le corps de la requête pour la création d'un client.
     *
     * @param businessname     Le nom de l'entreprise.
     * @param adressLabel      L'adresse.
     * @param postalCode       Le code postal.
     * @param city             La ville.
     * @param description      La description.
     * @param contactName      Le nom du contact.
     * @param contactFirstname Le prénom du contact.
     * @param contactPhone     Le téléphone du contact.
     * @param isClient         Indique si c'est un client.
     * @return Le corps de la requête en JSON.
     */
    private JSONObject createClientCreationBody(String businessname, String adressLabel, String postalCode, String city,
                                                String description, String contactName, String contactFirstname,
                                                String contactPhone, boolean isClient) {
        Adresse adress = new Adresse(adressLabel, postalCode, city);
        Contact contact = new Contact(contactName, contactFirstname, contactPhone);
        ClientDTO clientDTO = new ClientDTO(businessname, adress, description, contact, isClient);
        Gson gson = new Gson();
        String json = gson.toJson(clientDTO);
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Extrait le message d'une réponse JSON.
     *
     * @param response La réponse JSON.
     * @return Le message extrait.
     */
    private String extractMessage(JSONObject response) {
        try {
            return response.getString("message");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
