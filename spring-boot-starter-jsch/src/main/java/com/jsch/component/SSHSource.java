package com.jsch.component;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import com.jsch.exception.BusinessException;
import com.jsch.utils.ConfigTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ObjectUtils;

import java.util.LinkedList;
import java.util.Properties;

/**
 * Created by 陈敏 on 2017/8/21.
 * 远程连接服务器连接池
 */
public class SSHSource implements InitializingBean{

    private static final Logger logger = LoggerFactory.getLogger(SSHSource.class);

    private static final int DEFAULT_MAX_SIZE = 10;  // 默认连接池最大连接数
    private static final int DEFAULT_SSH_PORT = 22;  // 默认端口
    private static final int DEFAULT_TIME_OUT = 0;  // 默认超时时间（秒）

    private JSch jSch = new JSch();

    private String              ipAddress   =   "";
    private String              username    =   "";
    private String              password    =   "";
    private String              publicKey   =   "";
    private int                 maxSize = DEFAULT_MAX_SIZE;  // 连接池最大连接数
    private int                 sshPort = DEFAULT_SSH_PORT;  // 端口
    private int                 timeout = DEFAULT_TIME_OUT;

    private LinkedList<Session> sources = new LinkedList<Session>();
    public SSHSource() {
    }

    private SSHSource init() {
        for (int i = 0; i < maxSize; i++) {
            Session session;
            try {
                session = buildSession(username, ipAddress, sshPort);
                session.setUserInfo(new SSHUserInfo());
                String passwd = buildPasswd(password, publicKey);
                session.setPassword(passwd);
//                session.connect();
                sources.add(session);
            } catch (Exception e) {
                throw new BusinessException("Session Source Inited error");
            }
        }
        return this;
    }

    public Session buildSession(String username, String ipAddress, int sshPort) throws JSchException {
        Session session = jSch.getSession(username, ipAddress, sshPort);
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking","no");
        session.setConfig(sshConfig);
        return session;
    }

    public String buildPasswd(String password, String publicKey){
        String passwd = password;
        if (!ObjectUtils.isEmpty(publicKey)) {
            try {
                passwd = ConfigTools.decrypt(publicKey, password);
            } catch (Exception e) {
                throw new BusinessException("The Server password decryption failure");
            }
        }
        return passwd;
    }

    public Session getSession() {
        if(sources.isEmpty()) {
            throw new BusinessException("The session of Sources is Empty!");
        }
        return sources.removeFirst();
    }

    public void close(Session session) {
        sources.addLast(session);
        session.disconnect();
    }

    public void destroy() {
        sources.clear();
    }

    public JSch getJSch() {
        return this.jSch;
    }

    public LinkedList<Session> getSources() {
        return this.sources;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public int getSshPort() {
        return this.sshPort;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public SSHSource setSources(LinkedList<Session> sources) {
        this.sources = sources;
        return this;
    }

    public SSHSource setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public SSHSource setUsername(String username) {
        this.username = username;
        return this;
    }

    public SSHSource setPassword(String password) {
        this.password = password;
        return this;
    }

    public SSHSource setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public SSHSource setSshPort(int sshPort) {
        this.sshPort = sshPort;
        return this;
    }

    public SSHSource setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    private static class SSHUserInfo implements UserInfo {
        private String password;
        private String passphrase;

        public SSHUserInfo() {
        }

        @Override
        public String getPassphrase() {
            System.out.println("MyUserInfo.getPassphrase");
            return null;
        }

        @Override
        public String getPassword() {
            System.out.println("MyUserInfo.getPassword");
            return null;
        }

        @Override
        public boolean promptPassword(String s) {
            System.out.println("MyUserInfo.promptPassword");
            System.out.println(s);
            return false;
        }

        @Override
        public boolean promptPassphrase(String s) {
            System.out.println("MyUserInfo.promptPassphrase");
            System.out.println(s);
            return false;
        }

        @Override
        public boolean promptYesNo(String s) {
            System.out.println("MyUserInfo.promptYesNo");
            System.out.println(s);
            if(s.contains("The authenticity of host")) {
                return true;
            }
            return false;
        }

        @Override
        public void showMessage(String s) {
            System.out.println("MyUserInfo.showMessage");
            System.out.println(s);
        }

        public SSHUserInfo setPassword(String password) {
            this.password = password;
            return this;
        }

        public SSHUserInfo setPassphrase(String passphrase) {
            this.passphrase = passphrase;
            return this;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.sources.size() == 0) {
            init();
        }
        logger.info("The SSHSource inited successfully!");
    }
}
