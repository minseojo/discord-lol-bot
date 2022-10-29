package object;

public class Ingame {
    public String championName;
    public String summonerId;

    public int championId;
    public int kills;
    public int deaths;
    public int assists;
    public String win;
    public Ingame(String summonerId, String championName, int championId, int kills, int deaths, int assists, String win) {
        this.summonerId = summonerId;
        this.championName = championName;
        this.championId = championId;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
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
