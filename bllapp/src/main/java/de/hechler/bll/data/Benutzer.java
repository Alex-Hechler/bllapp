package de.hechler.bll.data;

public class Benutzer {
    private long benutzerID;
    private String name;
    private int iconAssetId;

    public Benutzer(long benutzerID, String name, int iconAssetId) {
        this.benutzerID = benutzerID;
        this.name = name;
        this.iconAssetId = iconAssetId;
    }

    public Benutzer(String name, int iconAssetId) {
        this.name = name;
        this.iconAssetId = iconAssetId;
    }


    //Getter
    public long getBenutzerID() {
        return benutzerID;
    }
    public String getName() {
        return name;
    }
    public int getIconAssetId() {
        return iconAssetId;
    }

    //Setter
    public void setBenutzerID(long benutzerID) {
        this.benutzerID = benutzerID;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setIconAssetId(int iconAssetId) {
        this.iconAssetId = iconAssetId;
    }
}
