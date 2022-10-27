package commands;

import api.RiotApi;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import object.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.net.URL;
import java.util.LinkedList;

public class UserCommand extends ListenerAdapter {
    private final static String RIOT_API_KEY = "라이엇 개인용 API 키";

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
            if(user == null) {
                e.getChannel().sendMessage("유저가 명확하지 않습니다.").queue();
                return;
            }
            LinkedList list = getPersonalGameRecord(user);

            EmbedBuilder eb = new EmbedBuilder();
            eb.setAuthor("롤 전적 검색");
            eb.setTitle(user.nickname.replace("%20", " "), "https://www.op.gg/summoners/kr/" + nickname);
            eb.setThumbnail("https://ddragon.leagueoflegends.com/cdn/" + LOL_VERSION + "/img/profileicon/" + user.profileIconId + ".png");
            eb.setDescription(String.valueOf(user.summonerLevel) + " 레벨");
            eb.setColor(Color.yellow);

            for(int i=0; i< list.size(); i++) {
                User tempUser = (User) list.get(i);
                Double winRate = Double.valueOf(tempUser.wins + tempUser.losses);
                winRate = tempUser.wins / winRate * 100;
                winRate = Double.valueOf(Math.round(winRate));
                if(tempUser.queueType.equals("RANKED_SOLO_5x5")) {
                    eb.addField("솔로 랭크   " + tempUser.tier + "  " + tempUser.rank + "  " + tempUser.leaguePoints + "LP", String.valueOf(tempUser.wins) + " 승 " + String.valueOf(tempUser.losses) + " 패 / " + winRate + "%", false);
                }
                if(tempUser.queueType.equals("RANKED_FLEX_SR"))
                    eb.addField("자유 랭크   " +tempUser.tier + "  " + tempUser.rank + "  " + tempUser.leaguePoints + "LP", String.valueOf(tempUser.wins) + " 승 " + String.valueOf(tempUser.losses) + " 패 / " + winRate + "%", false);
            }

            e.getChannel().sendMessageEmbeds(eb.build()).queue();
        }

    }

    private User getSummonerInfo(User user) {
        JSONObject obj = null;
        String[] info = {"id", "accountId", "puuid", "profileIconId", "summonerLevel"};
        try {
            URL url = new URL("https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/"+ user.nickname+"?api_key="+RIOT_API_KEY);
            obj = riotApi.getRiotApiJsonObject(url);
            // 응답코드가 200이 아닌경우
            if(obj == null) {
                return null;
            }
            user.id = obj.getString("id");
            user.accountId = obj.getString("accountId");
            user.puuid = obj.getString("puuid");
            user.profileIconId = obj.getInt("profileIconId");
            user.summonerLevel = obj.getInt("summonerLevel");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(obj == null) return null;
            else return user;
        }
    }

    private LinkedList getPersonalGameRecord(User user) {
        LinkedList list = new LinkedList();
        JSONArray ary = null;
        String[] info = {"tier", "rank", "leaguePoints", "wins", "losses", "queueType"};
        try {
            URL url = new URL("https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/" + user.id +"?api_key=" + RIOT_API_KEY);
            ary = riotApi.getRiotApiJsonArray(url);
            // 응답코드가 200이 아닌경우
            if(ary == null) {
                return null;
            }

            for(int i=0; i<ary.length(); i++) {
                JSONObject obj = ary.getJSONObject(i);
                User tmpUser = new User(user.nickname);
                tmpUser.tier = obj.getString("tier");
                tmpUser.rank = obj.getString("rank");
                tmpUser.leaguePoints = obj.getInt("leaguePoints");
                tmpUser.wins = obj.getInt("wins");
                tmpUser.losses = obj.getInt("losses");
                tmpUser.queueType = obj.getString("queueType");
                list.add(tmpUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(list.size() == 0) return null;
            return list;
        }
    }
}
