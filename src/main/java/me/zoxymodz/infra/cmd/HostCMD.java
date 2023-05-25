package me.zoxymodz.infra.cmd;

import me.zoxymodz.infra.manager.Server;
import me.zoxymodz.infra.provider.ServerType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class HostCMD extends Command {

    public HostCMD() {
        super("host",null,"h");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        new Server(sender.getName(), ServerType.HOST);

    }
}
