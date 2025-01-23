package fr.iutrodez.tourneecommercial.consommateurapi;

import java.util.Arrays;
import java.util.List;

import fr.iutrodez.tourneecommercial.modeles.Adresse;
import fr.iutrodez.tourneecommercial.modeles.Client;
import fr.iutrodez.tourneecommercial.modeles.Contact;
import fr.iutrodez.tourneecommercial.modeles.Itineraire;

public class FakeData {
    private static final Client[] clients = {
            new Client(1,
                    "Client 1",
                    new Adresse("4 ruelle de l'escudelou", "12000", "RODEZ"),
                    "Description 1",
                    2,
                    43,
                    new Contact("DUPONT", "Jean", "06 12 34 56 78")),
            new Client(2,
                    "Client 2",
                    new Adresse("4 ruelle de l'escudelou", "12000", "RODEZ"),
                    "Description 2",
                    2,
                    43,
                    new Contact("DUPONT", "Jean", "06 12 34 56 78"))};

    private static final Itineraire[] itineraires = {
            new Itineraire("Soupe", 50),
            new Itineraire("Risotto", 48),
            new Itineraire("Patate", 103)
    };

    public static List<Client> getClients() {
        return Arrays.asList(clients);
    }

    public static List<Itineraire> getItineraires() {
        return Arrays.asList(itineraires);
    }
}
