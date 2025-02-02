package fr.iutrodez.tourneecommercial.modeles;

public class Client {
    private final String _id;
    private final String idUtilisateur;
    private final String nomEntreprise;
    private final Adresse adresse;
    private final String descriptif;
    private final Coordonnees coordonnees;
    private final Contact contact;
    private final boolean clientEffectif;

    public Client(String _id, String idUtilisateur, String nomEntreprise, Adresse adresse, String descriptif, Coordonnees coordonnees, Contact contact, boolean clientEffectif) {
        this._id = _id;
        this.idUtilisateur = idUtilisateur;
        this.nomEntreprise = nomEntreprise;
        this.adresse = adresse;
        this.descriptif = descriptif;
        this.coordonnees = coordonnees;
        this.contact = contact;
        this.clientEffectif = clientEffectif;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Client) {
            return ((Client) obj).get_id().equals(this.get_id());
        }
        return false;
    }


    public String get_id() {
        return this._id;
    }

    public String getIdUtilisateur() {
        return this.idUtilisateur;
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

    public Coordonnees getCoordonnees() {
        return coordonnees;
    }

    public boolean isClientEffectif() {
        return clientEffectif;
    }
}
