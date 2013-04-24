package org.iplantc.de.client.sysmsgs.view;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.sencha.gxt.core.client.XTemplates;

import org.iplantc.de.client.sysmsgs.model.SystemMessage;

/**
 * TODO document
 */
final class SystemMessageSummaryCell extends AbstractSafeHtmlCell<SystemMessage> {
	
	interface MessageSummaryTemplate extends XTemplates {
		@XTemplate(source="MessageSummary.html")
		SafeHtml make(SystemMessage msg);
	}
	
	private static final MessageSummaryTemplate msgSummaryTpl = GWT.create(
			MessageSummaryTemplate.class);

	SystemMessageSummaryCell() {
		super(new SafeHtmlRenderer<SystemMessage>() {

			@Override
			public SafeHtml render(final SystemMessage msg) {
				return msgSummaryTpl.make(msg);
			}

			@Override
			public void render(final SystemMessage msg, final SafeHtmlBuilder builder) {
				builder.append(render(msg));
			}});
	}

	@Override
	protected void render(final Cell.Context unused, final SafeHtml data, final SafeHtmlBuilder sb) 
			{
		sb.append(data);
	}	

}