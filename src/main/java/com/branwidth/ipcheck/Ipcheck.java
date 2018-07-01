package com.branwidth.ipcheck;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.logging.Logger;

public final class Ipcheck extends JavaPlugin implements Listener {

    private Logger logger = getLogger();

    @Override
    public void onEnable() {
        logger.info("IPLogger is starting up!");
        createFiles();
        getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onDisable() {
        logger.info("IPLogger is shutting down!");
    }

    @EventHandler
    private void IpDetect(AsyncPlayerPreLoginEvent event) throws Exception {
        String ipAddress = event.getAddress().getHostAddress();
        String apiKey = this.getConfig().getString("APIKey");
        int blockValue = 0;
        boolean blockLevel0 = this.getConfig().getBoolean("BlockLevel_0");
        boolean blockLevel1 = this.getConfig().getBoolean("BlockLevel_1");
        boolean blockLevel2 = this.getConfig().getBoolean("BlockLevel_2");

        StringBuilder result = new StringBuilder();
        URL url = new URL("http://v2.api.iphub.info/ip/" + ipAddress);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("X-Key", apiKey);
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        String resultJSON = result.toString().replace("}","").replace("{", "");
        String[] resultGroup = resultJSON.split(",");
        for (String value: resultGroup) {
            String[] valueSplit = value.split(":");
            if (valueSplit[0] == "block") {
                blockValue = Integer.valueOf(valueSplit[1]);
            }
        }

        if (blockLevel0 && blockValue == 0) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "We do not allow IP Proxies or VPNs on this server");
        }

        if (blockLevel1 && blockValue == 1) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "We do not allow IP Proxies or VPNs on this server");
        }

        if (blockLevel2 && blockValue == 2) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "We do not allow IP Proxies or VPNs on this server");
        }

    }

    private void createFiles() {

        File configf = new File(getDataFolder(), "config.yml");

        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
    }
}
