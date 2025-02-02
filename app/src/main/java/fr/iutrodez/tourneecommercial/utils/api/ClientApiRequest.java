package fr.iutrodez.tourneecommercial.utils.api;

import com.android.volley.RequestQueue;

public class ClientApiRequest extends ApiRessource {
    public ClientApiRequest(RequestQueue requestQueue) {
        super(requestQueue);
    }

    public void getAll() {
        System.out.println("NOT IMPLEMETED");
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
}
