package com.wynntils.antiope;


import com.wynntils.antiope.core.DiscordGameSDKCore;
import com.wynntils.antiope.core.type.CreateParams;
import com.wynntils.antiope.manager.activity.type.Activity;

import java.time.Instant;

public class test {
    public static void main(String[] args) {
        DiscordGameSDKCore.initDiscordNative("discord_game_sdk\\lib\\x86_64\\discord_game_sdk.dll");

        DiscordGameSDKCore core = null;

        try (CreateParams params = new CreateParams()) {
            System.out.println("setting client id");
            params.setClientID(1121410048996954192L);
            params.setFlags(CreateParams.getDefaultFlags());
            try {
                core = new DiscordGameSDKCore(params);
                System.out.println("got new core");
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        Activity activity = new Activity();
        activity.setDetails("details field");
        activity.setState("state field");

        activity.timestamps().setStart(Instant.now());

        activity.party().size().setMaxSize(16);
        activity.party().size().setCurrentSize(2);

        activity.assets().setLargeImage("test");
        activity.assets().setLargeText("this is a big image");
        activity.assets().setSmallImage("test2");
        activity.assets().setSmallText("this is a less big image");

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