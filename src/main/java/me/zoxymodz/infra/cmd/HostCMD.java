package me.zoxymodz.infra.cmd;

import me.zoxymodz.infra.manager.Server;
import me.zoxymodz.infra.provider.ServerTypes;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class HostCMD extends Command {

    private final ServerTypes[] types;
    public HostCMD(ServerTypes[] serverTypes) {
        super("host", null, "h");
        this.types = serverTypes;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        new Server(sender.getName(), types[0]);
    }
}
