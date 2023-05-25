package me.zoxymodz.infra.manager;

import lombok.Getter;
import me.zoxymodz.infra.QuickInfra;
import me.zoxymodz.infra.provider.ServerType;
import net.md_5.bungee.api.config.ServerInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Server {
    @Getter
    private int port;
    private static int ports = 25570;
    @Getter
    private String name;
    @Getter
    private ServerType st;
    @Getter
    private ServerInfo si;
    private static final String SPIGOT_VERSION = "1.8.8-R0.1-SNAPSHOT-latest";
    @Getter
    private static List<Server> servs = new ArrayList<>();

    public Server(String name,ServerType st) {
        this.name = name;
        this.si = createServer(st);
        this.port = ports;
        QuickInfra.getProxyServer().getServers().put(st.getName(), si);
        servs.add(this);
    }
    public static Server getServerWithName(String name){
        return servs.stream().filter(server -> server.getName().equals(name)).findAny().orElse(null);
                    }
    public static Server getServerWithPort(int port){
        return servs.stream().filter(server -> server.getPort() == port).findAny().orElse(null);
    }
    public static List<Server> getServersWithType(ServerType st){
        return servs.stream().filter(server -> server.getSt().equals(st)).collect(Collectors.toList());
    }
    public void removeServer() {
        try {
            // Récupère le serveur Spigot avec le nom spécifié
            ServerInfo server = si;
            if (server == null) {
                System.out.println("Le serveur " + getName() + " n'existe pas.");
                return;
            }

            // Arrête le serveur Spigot
            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "stop.sh");
            pb.directory(new File(getName()));
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            pb.start().waitFor();

            // Supprime le dossier du serveur Spigot
            FileUtils.deleteDirectory(new File(getName()));

            // Retire le serveur de la liste des serveurs disponibles
            QuickInfra.getProxyServer().getServers().remove(getName());
            servs.remove(this);
            System.out.println("Le serveur " + getName() + " a été supprimé.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    private ServerInfo createServer(ServerType serverName) {
        try {
            ports++;
            // Crée un nouveau dossier pour le serveur Spigot
            File serverDir = new File(QuickInfra.getInstance().getDataFolder(), getName());
            serverDir.mkdir();
            // Télécharge Spigot
            File spigotFile = new File(serverDir, "spigot.jar");
            String spigotUrl = "https://cdn.getbukkit.org/spigot/spigot-" + SPIGOT_VERSION + ".jar";
            FileUtils.copyURLToFile(new URL(spigotUrl), spigotFile);

            // Crée le fichier de configuration Spigot
            File configFile = new File(serverDir, "server.properties");
            FileUtils.writeStringToFile(configFile, "server-port="+ports+"\n", Charset.defaultCharset());

            // Crée le script de démarrage du serveur
            File startScript = new File(serverDir, "start.sh");
            String command = "#!/bin/bash\nhere=$(basename `pwd`)\nscreen -dmS $here java "+ serverName.getRam() + " -jar spigot.jar";
            FileUtils.writeStringToFile(startScript, command, Charset.defaultCharset());
            startScript.setExecutable(true);
            Thread.sleep(10000);

            // Lance le script de démarrage du serveur
            ProcessBuilder pb = new ProcessBuilder("/bin/bash", startScript.getAbsolutePath());
            pb.start();

            System.out.println("Le server a bien été cree et c'est lancer");
            // Crée un nouveau serveur Spigot avec le nom spécifié
           return QuickInfra.getProxyServer().constructServerInfo(
                   getName(),
                    new InetSocketAddress("localhost", ports),
                    "Un serveur Spigot à la demande",
                    false
            );
            // Ajoute le serveur à la liste des serveurs disponibles
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
