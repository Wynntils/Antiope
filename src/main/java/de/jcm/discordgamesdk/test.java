package de.jcm.discordgamesdk;

import de.jcm.discordgamesdk.activity.Activity;

import java.io.File;
import java.time.Instant;

public class test {
    public static void main(String[] args) {
        Core.init(new File("discord_game_sdk\\lib\\x86_64\\discord_game_sdk.dll"));

        Core core = null;

        try (CreateParams params = new CreateParams()) {
            System.out.println("setting client id");
            params.setClientID(1121410048996954192L);
            params.setFlags(CreateParams.getDefaultFlags());
            try {
                core = new Core(params);
                System.out.println("got new core");
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        Activity activity = new Activity();
        activity.setDetails("details field");
        activity.setState("state field oh hey the image showed up");

        activity.timestamps().setStart(Instant.now());

        activity.party().size().setMaxSize(16);
        activity.party().size().setCurrentSize(2);

        activity.assets().setLargeImage("test");

        core.activityManager().updateActivity(activity);
        while (true) {
            try {
                core.runCallbacks();
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
