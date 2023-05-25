package me.zoxymodz.infra.provider;

import lombok.Getter;

@Getter
public enum ServerType {

    LOBBY("LOBBY-","-Xmx1024M -Xms4096M"),
    GAME("GAME-","-Xmx1024M -Xms8192"),
    HOST("HOST-","-Xmx1024M -Xms8192M" );

    private String name;
    private String ram;

    ServerType(String name, String ram) {
        this.name = name;
        this.ram = ram;
   }
}
