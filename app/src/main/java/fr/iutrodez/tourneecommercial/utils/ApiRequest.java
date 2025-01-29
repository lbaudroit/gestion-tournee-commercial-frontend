package fr.iutrodez.tourneecommercial.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.iutrodez.tourneecommercial.R;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.modeles.dto.ItineraireDTO;
import java.util.HashMap;
import java.util.Map;
import fr.iutrodez.tourneecommercial.R;

/**
 * Classe utilitaire pour gérer les requêtes API de l'application.
 * Fournit des méthodes pour interagir avec le backend et des API externes.
 */
public class ApiRequest {
    private static RequestQueue requestQueue;

    private static final String API_URL = "http://10.0.2.2:9090/";

    /**
     * Interface de callback pour gérer les réponses des requêtes API.
     * @param <T> Le type de données attendu dans la réponse (JSONObject ou JSONArray)
     */
    public interface ApiResponseCallback<T> {
        /**
         * Appelé lorsque la requête est réussie.
         * @param response La réponse du serveur
         */
        void onSuccess(T response);

        /**
         * Appelé lorsque la requête échoue.
         * @param error L'erreur retournée
         */
        void onError(VolleyError error);
    }

    /**
     * Effectue une requête de connexion au serveur.
     * @param context Le contexte de l'application
     * @param url L'URL relative de l'endpoint de connexion
     * @param postData Les données de connexion (email, mot de passe)
     * @param callback Le callback pour gérer la réponse
     */
    public static void connexion(Context context, String url, JSONObject postData, ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                API_URL + url,
                postData,
                callback::onSuccess,
                callback::onError
        );
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Envoie une requête d'inscription au serveur.
     * @param context Le contexte de l'application
     * @param url L'URL relative de l'endpoint d'inscription
     * @param postData Les données d'inscription de l'utilisateur
     * @param callback Le callback pour gérer la réponse
     */
    public static void inscription(Context context, String url, JSONObject postData, ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                API_URL + url,
                postData,
                callback::onSuccess,
                callback::onError
        );
        requestQueue.add(jsonObjectRequest);
    }


    /**
     * Modifie les informations d'un client existant.
     * @param context Le contexte de l'application
     * @param id L'identifiant du client à modifier
     * @param postData Les nouvelles données du client
     * @param callback Le callback pour gérer la réponse
     */
    public static void modifierClient(Context context,String id,JSONObject postData,ApiResponseCallback callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("token", "");
        String url = API_URL + "client/modifier/?id="+id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                postData,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders()  {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);

                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
    /**
     * Crée un nouveau client dans le système.
     * @param context Le contexte de l'application
     * @param url L'URL relative de l'endpoint de création
     * @param postData Les données du nouveau client
     * @param callback Le callback pour gérer la réponse
     */
    public static void creationClient(Context context, String url, JSONObject postData, ApiResponseCallback callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("token", "");
        url = API_URL + url;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                 url,
                postData,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders()  {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }


    /**
     * Récupère les informations d'un client spécifique.
     * @param context Le contexte de l'application
     * @param id L'identifiant du client à récupérer
     * @param callback Le callback pour gérer la réponse
     */
    public static void recupererClient(Context context,String id,ApiResponseCallback callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("token", "");

        String url = API_URL + "client/recuperer/?id="+id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Valide une adresse en utilisant l'API gouvernementale française.
     * @param context Le contexte de l'application
     * @param libelleAdresse L'adresse à valider
     * @param codePostal Le code postal
     * @param ville La ville
     * @param callback Le callback pour gérer la réponse
     */
    public static void validationAdresse(Context context, String libelleAdresse, String codePostal, String ville, ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String url = "https://api-adresse.data.gouv.fr/search/?q=" + libelleAdresse + "&postcode=" + codePostal + "&city=" + ville + "&limit=1" + "&type=housenumber" + "&autocomplete=0";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                callback::onSuccess,
                callback::onError
        );
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Récupère des suggestions d'adresses basées sur une recherche.
     * @param context Le contexte de l'application
     * @param query Le texte de recherche
     * @param callback Le callback pour gérer la réponse
     */
    public static void fetchAddressSuggestions(Context context, String query, ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String url = "https://api-adresse.data.gouv.fr/search/?q=" + query + "&limit=20" + "&type=housenumber";
        System.out.println(url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                callback::onSuccess,
                callback::onError
        );
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Récupère le nombre total d'itinéraires.
     * @param context Le contexte de l'application
     * @param url L'URL relative de l'endpoint
     * @param callback Le callback pour gérer la réponse
     */
    public static void fetchNombresItineraires(Context context, String url, ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                API_URL + url,
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    /**
     * Récupère la liste des itinéraires.
     * @param context Le contexte de l'application
     * @param url L'URL relative de l'endpoint
     * @param callback Le callback pour gérer la réponse
     */
    public static void fetchItineraires(Context context, String url, ApiResponseCallback<JSONArray> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API_URL + url,
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }


    public static void recupererClients(Context context, ApiResponseCallback<JSONArray> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        String url = API_URL + "client/";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    public static void deleteItineraire(Context context, long itineraireId, ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        System.out.println(API_URL + "itineraire/supprimer/?id=" + itineraireId);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                API_URL + "itineraire/supprimer/?id=" + itineraireId,
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
    /**
     * Méthode d'appel API pour supprimer un client
     * @param context
     * @param idClient client à supprimer
     * @param callback les méthodes de callbacks
     */
    public static void removeClient(Context context,String idClient,ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("token", "");

        String url = API_URL + "client/supprimer/?id="+idClient;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public static void getParametres(Context context, ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                API_URL + "utilisateur/",
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Supprime un itinéraire spécifique.
     * @param context Le contexte de l'application
     * @param itineraireId L'identifiant de l'itinéraire à supprimer
     * @param callback Le callback pour gérer la réponse
     */
    public static void deleteItineraire(Context context, long itineraireId, ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        String url = API_URL + "itineraire/recuperer/?id=" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                API_URL + "itineraire/supprimer/?id=" + itineraireId,
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public static void modifierParametres(Context context, JSONObject postData, ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                API_URL + "utilisateur/modifier/",
                postData,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public static void genererItineraire(Context context, List<Client> clients, ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        String ids = clients.stream()
                .map(Client::get_id)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String url = API_URL + "itineraire/generer/?clients=" + ids;
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    /**
     * Récupère ou renouvelle le token d'API.
     * Si le token est expiré, effectue une nouvelle connexion pour en obtenir un nouveau.
     * @param context Le contexte de l'application
     * @return Le token d'API valide
     */
    public static String getAPI_KEY(Context context) {
        final String[] token = {context.getSharedPreferences("user", MODE_PRIVATE).getString("token", "")};
        long expirationTime = context.getSharedPreferences("user", MODE_PRIVATE).getLong("expiration", 0);
        if (expirationTime < System.currentTimeMillis()) {
            JSONObject postData = new JSONObject();
            try {
                postData.put("email", context.getSharedPreferences("user", MODE_PRIVATE).getString("email", ""));
                postData.put("motDePasse", context.getSharedPreferences("user", MODE_PRIVATE).getString("password", ""));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            ApiRequest.connexion(context, "auth/authentifier", postData, new ApiRequest.ApiResponseCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    // Récupération du token
                    try {
                        token[0] = response.getString("token");
                        // Current time in milliseconde
                        long expirationTime = System.currentTimeMillis() + response.getLong("expiration");
                        // Enregistrement du token dans les SharedPreferences
                        context.getSharedPreferences("user", MODE_PRIVATE).edit().putLong("expiration", expirationTime).apply();
                        context.getSharedPreferences("user", MODE_PRIVATE).edit().putString("token", token[0]).apply();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    Toast.makeText(context, R.string.invalid_params_connexion_error, Toast.LENGTH_LONG).show();
                }
            });
        }
        return token[0];
    }

    private static JSONObject creationDTOCreationItineraire(String nom, List<Client> clients, int distance) throws JSONException {
        // Création de l'objet avec les données
        JSONObject itineraireData = new JSONObject();
        List<String> clientIds = clients.stream()
                .map(Client::get_id)
                .collect(Collectors.toList());
        itineraireData.put("nom", nom);
        itineraireData.put("idClients", new JSONArray(clientIds));
        itineraireData.put("distance", distance);

        return itineraireData;
    }

    public static void creationItineraire(Context context, String nom, List<Client> clients, int distance, ApiResponseCallback<JSONObject> callback) throws JSONException {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        JSONObject donneesAEnvoyer = creationDTOCreationItineraire(nom, clients, distance);

        String url = API_URL + "itineraire/creer/";
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                donneesAEnvoyer,
        return token[0];
    }


    /**
     * Récupère la liste complète des clients.
     * @param context Le contexte de l'application
     * @param callback Le callback pour gérer la réponse
     */
    public static void getClients(Context context, ApiResponseCallback<JSONArray> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API_URL + "client/",
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }

    /**
     * Récupère le nombre total de clients.
     * @param context Le contexte de l'application
     * @param callback Le callback pour gérer la réponse contenant le nombre total de clients
     */
    public static void getNombreClient(Context context,  ApiResponseCallback<JSONObject> callback) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                API_URL + "client/number/",
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("token", "");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonArrayRequest);
    }
          
    public static void modificationItineraire(Context context, long id, String nom, List<Client> clients, int distance, ApiResponseCallback<JSONObject> callback) throws JSONException {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        JSONObject donneesAEnvoyer = creationDTOCreationItineraire(nom, clients, distance);

        String url = API_URL + "itineraire/modifier/?id=" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                donneesAEnvoyer,
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("token", "");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
          
    /**
     * Récupère une page de 30 clients pour le lazy loading.
     * @param context Le contexte de l'application
     * @param page Le numéro de la page à récupérer (commence à 0)
     * @param callback Le callback pour gérer la réponse
     */
    public static void getClientsBy30(Context context, int page, ApiResponseCallback<JSONArray> callback){
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        String token = getAPI_KEY(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API_URL + "client/lazy/?page="+page,
                null,
                callback::onSuccess,
                callback::onError
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String token = context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("token", "");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public static Client jsonToClient(JSONObject json) {
        Gson gson = new Gson();

        // Définir le type pour une liste de clients
        Type clientType = TypeToken.get(Client.class).getType();

        // Convertir le JSON en liste d'objets Client
        return gson.fromJson(json.toString(), clientType);
    }

    public static ItineraireDTO itineraireDTOToClient(JSONObject json) {
        Gson gson = new Gson();

        // Définir le type pour une liste de clients
        Type dtoType = TypeToken.get(ItineraireDTO.class).getType();

        // Convertir le JSON en liste d'objets Client
        return gson.fromJson(json.toString(), dtoType);
        requestQueue.add(jsonArrayRequest);
    }
}
