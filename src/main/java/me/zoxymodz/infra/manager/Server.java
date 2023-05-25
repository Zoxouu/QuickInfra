package me.zoxymodz.infra.manager;

import lombok.Getter;
import lombok.NonNull;
import me.zoxymodz.infra.QuickInfra;
import me.zoxymodz.infra.provider.ServerTypes;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public class Server {
    private String name;
    private ServerTypes serverType;
    private ServerInfo serverInfo;
    private static int port = 25570;
    private static final String SPIGOT_VERSION = "1.8.8-R0.1-SNAPSHOT-latest";
    private static List<Server> servs = new ArrayList<>();
    @Getter
    private static int serversNumbers;

    public Server(@NonNull String name, ServerTypes st) {
        this.name = name;
        this.serverType = st;
        this.serverInfo = createServer();
        QuickInfra.instance.getProxy().getServers().put(serverType.getName(), serverInfo);
        servs.add(this);
    }

    public static Server getServerWithName(String name) {
        return servs.stream().filter(server -> server.name.equals(name)).findAny().orElse(null);
    }

    public static List<Server> getServersWithType(@NonNull ServerTypes st) {
        return servs.stream().filter(server -> server.serverType.equals(st)).collect(Collectors.toList());
    }

    public void removeServer() {
        try {
            // Récupère le serveur Spigot avec le nom spécifié
            if (serverInfo == null) {
                System.out.println("Le serveur " + name + " n'existe pas.");
                return;
            }

            // Arrête le serveur Spigot
            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "stop.sh");
            pb.directory(new File(name));
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            pb.start().waitFor();

            // Supprime le dossier du serveur Spigot
            Files.deleteIfExists(Paths.get(name));

            // Retire le serveur de la liste des serveurs disponibles
            QuickInfra.instance.getProxy().getServers().remove(name);
            servs.remove(this);
            System.out.println("Le serveur " + name + " a été supprimé.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private ServerInfo createServer() {
        try {
            port++;
            serversNumbers ++;
            // Crée un nouveau dossier pour le serveur Spigot
            Path serverDir = QuickInfra.instance.getDataFolder().toPath().resolve("servers").resolve(name);
            if(Files.notExists(serverDir)) Files.createDirectories(serverDir);
            // Télécharge Spigot
            Path spigotFile = serverDir.resolve("spigot.jar");
            String spigotUrl = "https://cdn.getbukkit.org/spigot/spigot-" + SPIGOT_VERSION + ".jar";
            try(InputStream is = URI.create(spigotUrl).toURL().openStream()) {
                Files.copy(is, spigotFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Crée le fichier de configuration Spigot
            Path configFile = serverDir.resolve("server.properties");
            writeString(configFile, "server-port=" + port + "\n");
            writeString(serverDir.resolve("eula.txt"), "eula=true");
            // Crée le script de démarrage du serveur
            Path startScript = serverDir.resolve("start.sh");
            String command = "#!/bin/bash\nhere=$(basename `pwd`)\nscreen -dmS $here java " + serverType.getRam() + " -jar spigot.jar";
            writeString(startScript, command);
            startScript.toFile().setExecutable(true);
            // Thread.sleep(10000);

            // Lance le script de démarrage du serveur
            new ProcessBuilder("/bin/bash", "start.sh").directory(serverDir.toFile()).start();

            System.out.println("Le server a bien été cree et c'est lancer");
            // Crée un nouveau serveur Spigot avec le nom spécifié
            return QuickInfra.instance.getProxy().constructServerInfo(
                    name,
                    new InetSocketAddress("localhost", port),
                    "Un serveur Spigot à la demande",
                    false);
            // Ajoute le serveur à la liste des serveurs disponibles
        } catch (IOException /* | InterruptedException */ e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeString(Path path, String str) throws IOException {
        writeString(path, str, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    public static void writeString(Path path, String str, StandardOpenOption... options) throws IOException {
        Files.write(path, str.getBytes(StandardCharsets.UTF_8), options);
    }
}
