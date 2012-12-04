package fi.helsinki.cs.nero.data;

import fi.helsinki.cs.nero.logic.Session;
import java.util.ArrayList;


/*
 * Created on Oct 22, 2004
 *
 */
/**
 * @author Johannes Kuusela
 *
 *
 */
public class Room {

    /**
     * Huoneen tunnus
     */
    private final String roomID;
    /**
     * Rakennus, johon huone kuuluu
     */
    private String buildingName;
    /**
     * Kerros. jossa huone sijaitsee
     */
    private String floor;
    /**
     * Huoneen numero
     */
    private String roomNumber;
    /**
     * Huoneen nimi
     */
    private String roomName;
    /**
     * Huoneen pinta-ala
     */
    private double roomSize;
    /**
     * Huoneen ty�pisteet
     */
    private Post[] posts;
    /**
     * Huoneen vapaamuotoinen kuvaus
     */
    private String description;
    /**
     * Huoneeseen kohdistuvat avainvaraukset
     */
    private ArrayList<RoomKeyReservation> roomKeyReservations;
    /**
     * Vakio, joka kertoo ett? huoneessa ei ole yht??n ty?pistett?
     */
    public static final int NO_POSTS = 0;
    private Session session;
    private String wing;

    /**
     * Konstruktori. Saa parametrinaan session, johon huone liittyy, huoneen
     * tunnuksen, rakennuksen nimen, huoneen kerroksen, huoneen numeron, huoneen
     * nimen, huoneen koon ja huoneen kuvauksen. Huoneen ty�pisteet on
     * asetettava erikseen setPosts-metodilla ennen kuin huonetta voi k�ytt��.
     *
     * @param session Sessio <code>Session</code> oliona.
     * @param roomID Huoneelle asetettava tunnus Stringin�.
     * @param buildingName Rakennus, jossa huone sijaitsee Stringin�.
     * @param floor Kerros, jossa huone sijaitsee Stringin�.
     * @param roomNumber Huoneelle asetettava numero Stringin�
     * @param roomName Huoneen nimi Stringin�.
     * @param roomSize Huoneen pinta-ala (neli�metri�) liukulukuna.
     * @param description Huoneen kuvaus Stringin�.
     * @throws IllegalArgumentException Jos annettu Session tai roomID null.
     */
    public Room(Session session, String roomID, String buildingName, String floor, String roomNumber,
            String roomName, double roomSize, String description) {


        if (session == null || roomID == null) {
            throw new IllegalArgumentException();
        }
        this.session = session;
        this.roomID = roomID;
        this.buildingName = buildingName;
        this.floor = floor;
        this.roomNumber = roomNumber;
        this.roomName = roomName;
        this.roomSize = roomSize;
        this.description = description;
        this.roomKeyReservations = new ArrayList<RoomKeyReservation>();
        if (buildingName.equals("Exactum")) {
            this.wing = roomNumber.substring(0, 1);
        } else {
            this.wing = "muu";
        }
        System.out.println(this.roomNumber);
        System.out.println(this.wing);
    }

    /**
     * Asettaa huoneen ty�pisteet. Metodia on kutsuttava t�sm�lleen kerran.
     *
     * @param posts taulukko huoneen ty�pisteist�
     * @throws IllegalArgumentException jos annettu taulukko on null
     * @throws IllegalStateException jos metodia kutsutaan toistamiseen
     */
    public void setPosts(Post[] posts) {
        if (posts == null) {
            throw new IllegalArgumentException();
        }
        if (this.posts != null) {
            throw new IllegalStateException();
        }
        this.posts = posts;
    }

    /**
     * Nerompi Lis�� huoneelle annetun avainvarauksen
     *
     * @param roomKeyReservation lis�tt�v� avainvaraus
     */
    public void addRoomKeyReservation(RoomKeyReservation roomKeyReservation) {
        this.roomKeyReservations.add(roomKeyReservation);
    }

    /**
     * Nerompi Poistaa huoneelta annetun avainvarauksen
     *
     * @param roomKeyReservation poistettava avainvaraus
     */
    public boolean deleteRoomKeyReservation(RoomKeyReservation roomKeyReservation) {
        if (this.roomKeyReservations.contains(roomKeyReservation)) {
            this.roomKeyReservations.remove(roomKeyReservation);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Palauttaa huoneen tunnuksen.
     *
     * @return roomID Tunnus Stringin�.
     */
    public String getRoomID() {
        return this.roomID;
    }

    /**
     * Palauttaa rakennuksen, jossa huone sijaitsee.
     *
     * @return buildingName Rakennuksen nimi Stringin�.
     */
    public String getBuildingName() {
        return buildingName;
    }

    /**
     * Palauttaa kerroksen, jossa huone sijaitsee.
     *
     * @return floor Kerros stringin�.
     */
    public String getFloor() {
        return floor;
    }

    /**
     * Palauttaa huoneen numeron.
     *
     * @return roomNumber Huoneen numero Stringin�.
     */
    public String getRoomNumber() {
        return roomNumber;
    }

    /**
     * Palauttaa huoneen nimen.
     *
     * @return roomName Huoneen nimi Stringin�.
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * Palauttaa huoneen koon neli�metreiss�.
     *
     * @return huoneen koko
     */
    public double getRoomSize() {
        return roomSize;
    }

    /**
     * Palauttaa huoneen ty�pisteet.
     *
     * @return posts Huoneen ty�pisteet <code>Post[]</code> oliona.
     * @throws IllegalStateException jos ty�pisteit� ei ole asetettu
     * setPosts-metodilla
     */
    public Post[] getPosts() {
        if (posts == null) {
            throw new IllegalStateException();
        }
        return posts;
    }

    /**
     * Palauttaa huoneen kuvauksen.
     *
     * @return description Huoneen kuvaus Stringin�.
     */
    public String getDescription() {
        if (description == null) {
            return "Ei kuvausta.";
        } else {
            return description;
        }
    }

    public ArrayList getRoomKeyReservations() {
        return this.roomKeyReservations;
    }

    /**
     * Palauttaa huoneen vapaustilan tarkasteltavalla osa-aikav�lill�. Huoneen
     * vapaustila m��rittyy huoneiden ty�pisteiden vapaustilan perusteella.
     * Huone on vapaa (FREE), jos se sis�lt�� v�hint��n
     * Session.getFilterFreePosts() (yleens� 1, mutta voi olla enemm�n) vapaata
     * ty�pistett�. Huone on varattu (OCCUPIED), jos kaikki sen sis�lt�m�t
     * ty�pisteet ovat varattuja. Muussa tapauksessa huone on osittain vapaa
     * (PARTLY_FREE).
     *
     * @return varaustila, joka on jokin vakioista Post.OCCUPIED,
     * Post.PARTLY_FREE tai Post.FREE
     */
    public int getStatus() {
        int minFree = session.getFilterFreePosts();
        int freePostCount = 0;
        boolean partlyFreePosts = false;
        Post[] posts = getPosts();

        if (posts.length == 0) {
            return NO_POSTS;
        }

        for (int i = 0; i < posts.length; i++) {
            switch (posts[i].getStatus()) {
                case Post.FREE:
                    freePostCount++;
                    break;
                case Post.PARTLY_FREE:
                    partlyFreePosts = true;
                    break;
                default:
                // ei tehd? mit??n jos varattu
            }
        }

        if (freePostCount >= minFree) {
            return Post.FREE;
        }

        if (partlyFreePosts || freePostCount > 0) {
            return Post.PARTLY_FREE;
        } else {
            return Post.OCCUPIED;
        }

    }

    public String toString() {
        if (this.roomNumber == null) {
            return "(XXX)";
        }
        return roomNumber;
    }
}
