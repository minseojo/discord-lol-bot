package Object;

public class User {
    public int profileIconId;
    public String id;
    public String accountId;
    public String puuid;
    public String nickname;
    public int summonerLevel;
    public String tier;
    public String rank;
    public int leaguePoints;
    public int wins;
    public int losses;
 //"queueType": "RANKED_SOLO_5x5",
    public String queueType;
    public User(String nickname) {
        this.nickname = nickname;
    }

}
