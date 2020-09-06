package mvc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import socketCommunication.http.server.session.Session;

public class StoragedSession {

	private final Session session;
	
	private String lang;
	
	private FlashMessages messages;
	
	private Map<String, Object> params;
	
	@SuppressWarnings("unchecked")
	protected StoragedSession(Session session) throws IOException {
		this.session = session;
		if (session.getContent().isEmpty()) {
			this.lang = "cs"; // TODO default
			this.messages = new FlashMessages();
			this.params = new HashMap<>();
		} else {
			byte [] data = Base64.getDecoder().decode(session.getContent());
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
		    ObjectInputStream os = new ObjectInputStream(bis);
		    try {
		    	this.lang = (String)os.readObject();
		    } catch (ClassNotFoundException e) {
				this.lang = "cs"; // TODO default
			}
		    try {
		    	this.messages = (FlashMessages)os.readObject();
		    } catch (ClassNotFoundException e) {
				this.messages = new FlashMessages();
			}
		    try {
		    	this.params = (Map<String, Object>)os.readObject();
		    } catch (ClassNotFoundException e) {
				this.params = new HashMap<>();
			}
		    os.close();
		}
	}
	
	public Object getSession(String session) {
		return params.get(session);
	}
	
	public void addSession(String name, Object session) {
		params.put(name, session);
	}
	
	public void removeSession(String name) {
		params.remove(name);
	}
	
	public String getLang() {
		return lang;
	}
	
	public void setLang(String lang) {
		this.lang = lang;
	}
	
	protected FlashMessages getFlash() {
		return messages;
	}
	
	protected void flush() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    ObjectOutputStream os = new ObjectOutputStream(bos);
	    os.writeObject(lang);
	    os.writeObject(messages);
	    os.writeObject(params);
		session.setContent(Base64.getEncoder().encodeToString(bos.toByteArray()));
	    os.close();
	}

}
