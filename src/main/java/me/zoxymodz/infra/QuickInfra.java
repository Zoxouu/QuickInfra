package me.zoxymodz.infra;

import lombok.Getter;
import me.zoxymodz.infra.cmd.HostCMD;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public final class QuickInfra extends Plugin {
    @Getter
    public static QuickInfra instance;
    @Getter
    private static ProxyServer proxyServer;

    @Override
    public void onEnable() {
        instance = this;
        proxyServer = getProxy();

        this.getProxy().getPluginManager().registerCommand(this, new HostCMD());
    }

    @Override
    public void onDisable() {
    }
}
