package command;

import api.RiotApi;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import object.Ingame;
import object.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.net.URL;
import java.util.LinkedList;

public class UserCommand extends ListenerAdapter {
    //private final static String RIOT_API_KEY = "라이엇 개인용 API 키";
    private final static String RIOT_API_KEY = "RGAPI-80841738-025f-47c3-a36b-0a950b945ccf";
    private final static RiotApi riotApi = new RiotApi();
    private final static String LOL_VERSION = "12.17.1";
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        String[] message = e.getMessage().getContentRaw().split(" ");

        // 도움말 이벤트
        if(message.length == 1 && message[0].equals("!도움말")) {
            EmbedBuilder eb = new EmbedBuilder();
            e.getChannel().sendMessage("");
        }

        // 롤전적 이벤트
        else if(message.length > 1 && message[0].equals("!롤전적")) {
            String nickname = "";
            for(int i=1; i<message.length; i++) {
                nickname += message[i];
                if(i == message.length-1) break;
                nickname += "%20";
            }

            User user = new User(nickname);
            user = getSummonerInfo(user);
            System.out.println(user);
            if(user == null) {
                e.getChannel().sendMessage("유저가 명확하지 않습니다.").queue();
                return;
            }
            LinkedList list = getPersonalGameRecord(user);

            EmbedBuilder eb = new EmbedBuilder();
            eb.setAuthor("롤 전적 검색");
            eb.setTitle(user.getNickname().replace("%20", " "), "https://www.op.gg/summoners/kr/" + nickname);
            eb.setThumbnail("https://ddragon.leagueoflegends.com/cdn/" + LOL_VERSION + "/img/profileicon/" + user.getProfileIconId() + ".png");
            eb.setDescription(String.valueOf(user.getSummonerLevel()) + " 레벨");
            eb.setColor(Color.yellow);


            for(int i=0; i< list.size(); i++) {
                User tempUser = (User) list.get(i);
                Double winRate = Double.valueOf(tempUser.getWins() + tempUser.getLosses());
                winRate = tempUser.getWins() / winRate * 100;
                winRate = Double.valueOf(Math.round(winRate));

                if(tempUser.getQueueType().equals("RANKED_SOLO_5x5")) {
                    eb.addField("솔로 랭크   " + tempUser.getTier() + "  " + tempUser.getRank() + "  " + tempUser.getLeaguePoints() + "LP", String.valueOf(tempUser.getWins()) + " 승 " + String.valueOf(tempUser.getLosses()) + " 패 / " + winRate + "%", false);
                }
                if(tempUser.getQueueType().equals("RANKED_FLEX_SR"))
                    eb.addField("자유 랭크   " +tempUser.getTier() + "  " + tempUser.getRank() + "  " + tempUser.getLeaguePoints() + "LP", String.valueOf(tempUser.getWins()) + " 승 " + String.valueOf(tempUser.getLosses()) + " 패 / " + winRate + "%", false);
            }

            // 최근 게임 기록 ID
            LinkedList recentGameMatchIdList = getPersonalRecentGameHistory(user, 0, 10);

            // 최근 게임 리스트 (최근 게임 기록 ID를 가져와서 정보 가져오기)
            LinkedList matchList = new LinkedList();
            for(int i=0; i<recentGameMatchIdList.size(); i++) {
                matchList = getPersonalGameHistory(user, (String) recentGameMatchIdList.get(i), matchList);
            }

            Ingame[] ingame = new Ingame[recentGameMatchIdList.size()];
            int winCount = 0;
            int loseCount = 0;
            String str = "";
            for(int i=0; i<matchList.size(); i++) {
                ingame[i] = (Ingame) matchList.get(i);
                str += ingame[i].getWin() + " " + ingame[i].getChampionName() + " " + String.valueOf(ingame[i].getKills()) +"/" + String.valueOf(ingame[i].getDeaths()) + "/" + String.valueOf(ingame[i].getAssists()) + "  평점: " + ingame[i].getKda() + "\n";
                if(ingame[i].getWin().equals("승리")) winCount += 1;
                else loseCount += 1;
            }

            String https = "https://ddragon.leagueoflegends.com/cdn/12.20.1/img/champion/" + ingame[0].getChampionName() + ".png";
            eb.addField("최근" + winCount + loseCount + "경기 기록",winCount + loseCount + "전 " + winCount+ "승 " + loseCount + "패" , false);
            eb.setFooter(str);

            e.getChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }

    // 소환사 정보 가져오기
    private User getSummonerInfo(User user) {
        JSONObject obj = null;
        String[] info = {"id", "accountId", "puuid", "profileIconId", "summonerLevel"};
        try {
            URL url = new URL("https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/"+ user.getNickname()+"?api_key="+RIOT_API_KEY);

            obj = riotApi.getRiotApiJsonObject(url);

            // 응답코드가 200이 아닌경우 -> 존재하는 닉네임이 아닌경우
            if(obj == null) {
                return null;
            }
            
            // 유저 정보 등록
            user.setId(obj.getString("id"));;
            user.setAccountId(obj.getString("accountId"));
            user.setPuuid(obj.getString("puuid"));
            user.setProfileIconId(obj.getInt("profileIconId"));
            user.setSummonerLevel(obj.getInt("summonerLevel"));
           // System.out.println("id: " + user.id);
        //    System.out.println("puuid: " + user.puuid);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(obj == null) return null;
            else return user;
        }
    }

    // 소환사 전적 가져오기
    private LinkedList getPersonalGameRecord(User user) {
        LinkedList list = new LinkedList();
        JSONArray ary = null;
        String[] info = {"tier", "rank", "leaguePoints", "wins", "losses", "queueType"};
        try {
            URL url = new URL("https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/" + user.getId() +"?api_key=" + RIOT_API_KEY);
            ary = riotApi.getRiotApiJsonArray(url);

            // 응답코드가 200이 아닌경우 -> 존재하는 닉네임이 아닌경우
            if(ary == null) {
                return null;
            }

            // 유저 정보 등록하고 게임종류(솔랭, 자유랭, 더블업 등)별로 리스트에 전적 담아서 리턴
            for(int i=0; i<ary.length(); i++) {
                JSONObject obj = ary.getJSONObject(i);
                User tmpUser = new User(user.getNickname());
                tmpUser.setTier(obj.getString("tier"));
                tmpUser.setRank(obj.getString("rank"));
                tmpUser.setLeaguePoints(obj.getInt("leaguePoints"));
                tmpUser.setWins(obj.getInt("wins"));
                tmpUser.setLosses(obj.getInt("losses"));
                tmpUser.setQueueType(obj.getString("queueType"));
                list.add(tmpUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(list.size() == 0) return null;
            return list;
        }
    }

    // 최근 전적 count 개수 만큼 가져오기
    private LinkedList getPersonalRecentGameHistory(User user, int start, int count) { // 유저정보,  start = 가장 최근 경기 = 0, count = 가장 최근 경기부터 게임기록 개수
        LinkedList list = new LinkedList();
        JSONArray ary = null;
        try {
            URL url = new URL("https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/" + user.getPuuid() + "/ids?start=" + start + "&count=" + count + "&api_key=" + RIOT_API_KEY);
            ary = riotApi.getRiotApiJsonArray(url);
            // 응답코드가 200이 아닌경우 -> 존재하는 puuid가 아닌경우
            if(ary == null) {
                return null;
            }

            // 리스트에 최근 전적 start부터 count개수 만큼 담아서 리턴
            for(int i=0; i<ary.length(); i++) {
                System.out.println(ary.getString(i));
                list.add(ary.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(list.size() == 0) return null;
            return list;
        }
    }

    // matchId에 따른 최근 경기 기록 가져오기
    private LinkedList getPersonalGameHistory(User user, String matchId, LinkedList list) { // 유저정보, 유저정보에 따른 matchId가져와서 최근전적 list에 추가, 최근 전적 리스트
        JSONObject obj = null;
        JSONArray participants = null;
        try {
            URL url = new URL("https://asia.api.riotgames.com/lol/match/v5/matches/" + matchId +  "?api_key=" + RIOT_API_KEY);
            obj = riotApi.getRiotApiJsonObject(url);

            // 응답코드가 200이 아닌경우 -> 존재하는 matchId가 아닌경우
            if(obj == null) {
                return null;
            }

            participants = obj.getJSONObject("info").getJSONArray("participants");
            String summonerId = user.getId(), championName = "";
            int championId = -1, kills = 0, deaths = 0, assists = 0;
            double kda = 0.0;
            String win = "";
            boolean isWin;
            
            for(int i=0; i<participants.length(); i++) {
                    if(participants.getJSONObject(i).get("summonerId").equals(user.getId())) {
                        championId = (int) participants.getJSONObject(i).get("championId");
                        championName = (String) participants.getJSONObject(i).get("championName");
                        kills = (int) participants.getJSONObject(i).get("kills");
                        deaths = (int) participants.getJSONObject(i).get("deaths");
                        assists = (int) participants.getJSONObject(i).get("assists");
                        isWin = (boolean) participants.getJSONObject(i).get("win");
                        kda = Math.round(Double.valueOf(kills + assists) / Double.valueOf(deaths) * 10) / 10.0;
                        if(isWin) win = "승리";
                        else win = "패배";
                        break;
                    }
            }
            Ingame ingame = new Ingame(user.getId(), championName, championId, kills, deaths, assists, kda, win);
            list.add(ingame);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if(list.size() == 0) return null;
            return list;
        }
    }
}
