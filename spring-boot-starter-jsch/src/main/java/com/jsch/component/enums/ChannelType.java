package com.jsch.component.enums;

/**
 * Created by 陈敏 on 2017/8/22.
 */
public enum ChannelType {
    CHANNEL_SESSION_TYPE("session"),
    CHANNEL_SHELL_TYPE("shell"),
    CHANNEL_EXEC_TYPE("exec"),
    CHANNEL_X11_TYPE("x11"),
    CHANNEL_AGENTFORWARDING_TYPE("auth-agent@openssh.com"),
    CHANNEL_DIRECTTCPIP_TYPE("direct-tcpip"),
    CHANNEL_FORWARDEDTCPIP_TYPE("forwarded-tcpip"),
    CHANNEL_SFTP_TYPE("sftp"),
    CHANNEL_SUBSYSTEM_TYPE("subsystem");
    private String type;

    ChannelType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
