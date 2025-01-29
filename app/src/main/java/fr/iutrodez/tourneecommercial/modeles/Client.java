package fr.iutrodez.tourneecommercial.modeles;

public class Client {
    private String _id;

    private String idUtilisateur;
    private String nomEntreprise;

    private Adresse adresse;
    private String descriptif;
    private Coordonnees coordonnees;
    private Contact contact;

    private boolean clientEffectif;

    public Client(String nomEntreprise, Adresse adresse) {
        this.nomEntreprise = nomEntreprise;
        this.adresse = adresse;
    }

    public Client(String _id, String nomEntreprise, Adresse adresse, String descriptif, Coordonnees coordonnees, Contact contact) {
        this.nomEntreprise = nomEntreprise;
        this.adresse = adresse;
        this.descriptif = descriptif;
        this.coordonnees = coordonnees;
        this.contact = contact;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Client) {
            return ((Client) obj).get_id().equals(this.get_id());
        }
        return false;
    }

    public Client(String _id, String idUtilisateur, String nomEntreprise, Adresse adresse, Contact contact) {
        this.adresse = adresse;
        this._id = _id;
        this.contact = contact;
        this.nomEntreprise = nomEntreprise;
    }


    public String get_id() {
        return this._id;
    }

    public Adresse getAdresse() {
        return this.adresse;
    }


    public String getNomEntreprise() {
        return this.nomEntreprise;
    }

    public Contact getContact() {
        return contact;
    }

    public String getDescriptif() {
        return descriptif;
    }
}
