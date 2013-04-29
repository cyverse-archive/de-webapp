package org.iplantc.de.client.sysmsgs.view;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.sencha.gxt.core.client.XTemplates;

import org.iplantc.de.client.sysmsgs.model.MessageDTO;

/**
 * TODO document
 */
final class SystemMessageSummaryCell extends AbstractSafeHtmlCell<MessageDTO> {
	
	interface MessageSummaryTemplate extends XTemplates {
		@XTemplate(source="MessageSummary.html")
		SafeHtml make(MessageDTO msg);
	}
	
	private static final MessageSummaryTemplate msgSummaryTpl = GWT.create(
			MessageSummaryTemplate.class);

	SystemMessageSummaryCell() {
		super(new SafeHtmlRenderer<MessageDTO>() {

			@Override
			public SafeHtml render(final MessageDTO msg) {
				return msgSummaryTpl.make(msg);
			}

			@Override
			public void render(final MessageDTO msg, final SafeHtmlBuilder builder) {
				builder.append(render(msg));
			}});
	}

	@Override
	protected void render(final Cell.Context unused, final SafeHtml data, final SafeHtmlBuilder sb) 
			{
		sb.append(data);
	}	

}