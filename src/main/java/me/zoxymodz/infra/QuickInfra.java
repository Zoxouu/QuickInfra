package me.zoxymodz.infra;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.zoxymodz.infra.cmd.HostCMD;
import me.zoxymodz.infra.manager.Server;
import me.zoxymodz.infra.provider.ServerTypes;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class QuickInfra extends Plugin {
    @Getter
    public static QuickInfra instance;

    @Override
    public void onEnable() {
        instance = this;
        try {
            Path path = this.getDataFolder().toPath().resolve("types.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            if (Files.notExists(path)) {
                Files.createDirectories(path.getParent());
                Server.writeString(path, gson.toJson(new ServerTypes[]{new ServerTypes(1024,1024,"LOBBY-")}), StandardOpenOption.CREATE);
            }
            ServerTypes[] serverTypes = new Gson().fromJson(new String(Files.readAllBytes(path)), ServerTypes[].class);
            this.getProxy().getPluginManager().registerCommand(this, new HostCMD(serverTypes));
            new Server(ServerTypes.class.getName()+Server.getServersNumbers(),serverTypes[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {

    }
}
