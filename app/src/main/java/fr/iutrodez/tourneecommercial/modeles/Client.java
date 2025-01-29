package fr.iutrodez.tourneecommercial.modeles;

public class Client {
    private String _id;
    private int idUtilisateur;

    private String nomEntreprise;

    private Adresse adresse;
    private String descriptif;
    private Coordonnees coordonnees;
    private Contact contact;

    private boolean clientEffectif;

    public Client(int idUtilisateur, String nomEntreprise, Adresse adresse,
                  String description, double longitude, double latitude, Contact contact) {
        this.idUtilisateur = idUtilisateur;
        this.nomEntreprise = nomEntreprise;
        this.adresse = adresse;
        this.descriptif = description;
        this.coordonnees = new Coordonnees(latitude, longitude);
        this.contact = contact;
    }

    public String get_id() {
        return _id;
    }

    public String getNomEntreprise() {
        return nomEntreprise;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Client) {
            return ((Client) obj).get_id().equals(this.get_id());
        }
        return false;
    }
}
