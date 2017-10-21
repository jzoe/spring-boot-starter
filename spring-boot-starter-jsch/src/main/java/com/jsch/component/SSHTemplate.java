package com.jsch.component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jsch.component.enums.ChannelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

/**
 * Created by 陈敏 on 2017/8/22.
 */
public class SSHTemplate implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(SSHTemplate.class);
    private SSHSource           sshSource;
    private int                 returnCode;
    public SSHTemplate() {
    }

    public SSHTemplate(SSHSource sshSource) {
        this.sshSource = sshSource;
        afterPropertiesSet();
    }

    public void close(Session session) {
        if (session == null) {
            return;
        }
        try {
            sshSource.close(session);
        } catch (Exception e) {
            logger.info("close Session error," + e.getMessage());
        }
    }

    public int close(Channel channel) {
        int returnCode = 0;
        if (channel == null) {
            return returnCode;
        }
        if (channel.isClosed()) {
            returnCode = channel.getExitStatus();
        }
        channel.disconnect();
        return returnCode;
    }

    /**
     * 下载文件
     * @param downloadFile 下载文件
     */
    public byte[] downLoad(String downloadFile){
        ChannelSftp sftp;
        try{
            sftp = (ChannelSftp) getSession().openChannel(ChannelType.CHANNEL_SFTP_TYPE.getType());
            sftp.connect();
            InputStream inputStream = sftp.get(downloadFile);
            byte[] data = new byte[inputStream.available()];
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            bis.read(data);
            bis.close();
            inputStream.close();
            return data;
        }catch(Exception e){
            logger.error(downloadFile + "文件下载失败：", e);
        }
        return null;
    }

    /**
     * 下载文件
     * @param downloadFile 下载文件
     */
    public void downLoad(String downloadFile, String dst){
        ChannelSftp sftp = null;
        Session session = getSession();
        try{
            sftp = (ChannelSftp) session.openChannel(ChannelType.CHANNEL_SFTP_TYPE.getType());
            sftp.connect();
            File dstFile = new File(dst);
            File file = new File(dstFile.getParent());
            if(!file.exists()) {
                file.mkdirs();
                if (dstFile.exists()) {
                    dstFile.delete();
                    dstFile.createNewFile();
                } else {
                    dstFile.createNewFile();
                }
            }
            sftp.get(downloadFile, dst);
        }catch(Exception e){
            logger.error(downloadFile + "文件下载失败：", e);
        } finally {
            if (sftp != null) {
                close(sftp);
            }
            close(session);
        }
    }

    /**
     * 下载文件
     * @param directory 下载目录
     * @param downloadFile 下载文件
     */
    public void downLoad(String directory, String downloadFile, String dst){
        ChannelSftp sftp = null;
        Session session = getSession();
        try{
            sftp = (ChannelSftp) session.openChannel(ChannelType.CHANNEL_SFTP_TYPE.getType());
            sftp.connect();
            sftp.cd(directory);
            File dstFile = new File(dst);
            File file = new File(dstFile.getParent());
            if(!file.exists()) {
                file.mkdirs();
                if (dstFile.exists()) {
                    dstFile.delete();
                    dstFile.createNewFile();
                } else {
                    dstFile.createNewFile();
                }
            }
            sftp.get(downloadFile, dst);
        }catch(Exception e){
            logger.error(downloadFile + "文件下载失败：", e);
        } finally {
            if (sftp != null) {
                close(sftp);
            }
            close(session);
        }
    }

    public void upLoad(String src, String dst) {
        ChannelSftp sftp = null;
        Session session = getSession();
        try{
            sftp = (ChannelSftp) session.openChannel(ChannelType.CHANNEL_SFTP_TYPE.getType());
            sftp.connect();
            sftp.cd(dst);
            sftp.put(src, dst);
        }catch(Exception e){
            logger.error(src + "文件上传失败：", e);
        } finally {
            if (sftp != null) {
                close(sftp);
            }
            close(session);
        }
    }

    public Session getSession() {
        afterPropertiesSet();
        Session session = sshSource.getSession();
        try {
            session.connect();
        } catch (JSchException e) {
            logger.error("open jsch session error", e);
        }
        return session;
    }

    public SSHSource getSshSource() {
        return sshSource;
    }

    @Override
    public void afterPropertiesSet() {
        if (getSshSource() == null) {
            throw new IllegalArgumentException("Property 'sshSource' is required");
        }
    }

    public int getReturnCode() {
        return this.returnCode;
    }

    public void setSshSource(SSHSource sshSource) {
        this.sshSource = sshSource;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }
}
