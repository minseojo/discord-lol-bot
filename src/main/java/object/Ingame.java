package object;

public class Ingame {
    private String championName;
    private String summonerId;

    private int championId;
    private int kills;
    private int deaths;
    private int assists;

    private double kda;
    private String win;


    public Ingame(String summonerId, String championName, int championId, int kills, int deaths, int assists, double kda, String win) {
        this.summonerId = summonerId;
        this.championName = championName;
        this.championId = championId;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.kda = kda;
        this.win = win;
    }

    public String getChampionName() {
        return championName;
    }

    public void setChampionName(String championName) {
        this.championName = championName;
    }

    public String getSummonerId() {
        return summonerId;
    }

    public void setSummonerId(String summonerId) {
        this.summonerId = summonerId;
    }

    public int getChampionId() {
        return championId;
    }

    public void setChampionId(int championId) {
        this.championId = championId;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public double getKda() {
        return kda;
    }

    public void setKda(double kda) {
        this.kda = kda;
    }

    public String getWin() {
        return win;
    }

    public void setWin(String win) {
        this.win = win;
    }

    @Override
    public String toString() {
        return "Ingame{" +
                "championName='" + championName + '\'' +
                ", summonerId='" + summonerId + '\'' +
                ", championId=" + championId +
                ", kills=" + kills +
                ", deaths=" + deaths +
                ", assists=" + assists +
                ", win=" + win +
                '}';
    }
}
