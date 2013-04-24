package org.iplantc.de.client.sysmsgs.model;

import java.util.Date;

public final class SystemMessage {

	private final String id;
	private final String type;
	private final String body;
	private final Date startTime;
	private final Date endTime;
	
	private boolean acknowledged;
	
	public SystemMessage(final String id, final String type, final String body, final Date startTime,
			final Date endTime, final boolean acknowledged) {
		this.id = id;
		this.type = type;
		this.body = body;
		this.startTime = startTime;
		this.endTime = endTime;
		this.acknowledged = acknowledged;
	}
	
	public String getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}
	
	public String getBody() {
		return body;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public Date getEndTime() {
		return endTime;
	}
	
	public boolean isAcknowledged() {
		return acknowledged;
	}
	
	public void acknowledge() {
		acknowledged = true;
	}
	
}
