package controller;

import commands.UserCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;


public class Main  extends ListenerAdapter {
    private final static String discordBotToken = "디스코드 봇 토큰 값";
    public static JDA jda;

    public static void main(String[] args) throws LoginException {
        jda = JDABuilder.createDefault(discordBotToken, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT).build();
        jda.addEventListener(new UserCommand());
    }
}