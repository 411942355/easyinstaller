package com.jianglibo.vaadin.dashboard.ssh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jianglibo.vaadin.dashboard.util.ThrowableUtil;
import com.jianglibo.vaadin.dashboard.vo.JschExecuteResult;

public class JschSession {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(JschSession.class);
	
	private Session session;
	
	private JschSession(Session session) {
		this.setSession(session);
	}
	
	public JschExecuteResult exec(String cmd) {
		return execs(cmd).get(0);
	}
	
	public ChannelSftp getSftpCh() throws JSchException {
		return (ChannelSftp) getSession().openChannel("sftp");
	}
	
	public List<JschExecuteResult> execs(String...cmds) {
		List<JschExecuteResult> results = Lists.newArrayList();
		for(String cmd: cmds) {
			try {
				ChannelExec chExec = (ChannelExec) getSession().openChannel("exec");
				chExec.setCommand(cmd);
				chExec.setInputStream(null);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				chExec.setErrStream(baos);
				chExec.connect();
				String out = new String(ByteStreams.toByteArray(chExec.getInputStream()));
				String err = baos.toString();
				int ext = chExec.getExitStatus();
				results.add(new JschExecuteResult(out , err , ext));
				chExec.disconnect();
			} catch (JSchException | IOException e) {
				results.add(new JschExecuteResult(ThrowableUtil.printToString(e), "", 1));
			}
		}
		return results;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public static class JschSessionBuilder {
		private String sshUser;
		private String host;
		private int port = 22;
		
		private String keyFile;
		
		private String knownHosts;
		
		public JschSessionBuilder setSshUser(String sshUser) {
			this.sshUser = sshUser == null ? "root" : sshUser; 
			if (this.sshUser.trim().isEmpty()) {
				this.sshUser = "root";
			}
			return this;
		}

		public JschSessionBuilder  setHost(String host) {
			this.host = host;
			return this;
		}

		public JschSessionBuilder  setPort(int port) {
			this.port = port == 0 ? 22 : port;
			return this;
		}
		
		public JschSessionBuilder  setKeyFile(String keyFile) {
			this.keyFile = keyFile;
			return this;
		}
		
		public JschSessionBuilder  setKnownHosts(String knownHosts) {
			this.knownHosts = knownHosts;
			return this;
		}

		public JschSession build() throws JSchException {
			JSch jsch = new JSch();
			jsch.addIdentity(keyFile);
			LOGGER.info("connection to server with keyfile \"{}\", user: {}, host: {} port: {}", keyFile, sshUser, host,port);
			if (knownHosts != null) {
				jsch.setKnownHosts(knownHosts);
			}
			Session session = jsch.getSession(sshUser, host, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			return new JschSession(session);
		}
	}
}
