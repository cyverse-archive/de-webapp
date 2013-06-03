package org.iplantc.de.client.sysmsgs.presenter;

import java.io.IOException;
import java.util.Date;

import org.iplantc.de.client.I18N;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * This class renders an activation time.
 */
final class ActivationTimeRenderer implements Renderer<Date> {

    private static boolean withinPreviousWeek(final Date successor, final Date predecessor) {
        if (predecessor.after(successor)) {
            return false;
        }
        return CalendarUtil.getDaysBetween(predecessor, successor) < 7;
    }

    private final ProvidesTime clock;

    ActivationTimeRenderer(final ProvidesTime clock) {
        this.clock = clock;
    }

    /**
     * @see Renderer<T>#render(T)
     */
    @Override
    public String render(final Date activationTime) {
        final Date now = clock.now();
        String actMsg = "";
        if (CalendarUtil.isSameDate(now, activationTime)) {
            actMsg = I18N.DISPLAY.today();
        } else if (withinPreviousWeek(now, activationTime)) {
            actMsg = DateTimeFormat.getFormat("cccc").format(activationTime);
        } else {
            actMsg = DateTimeFormat.getFormat("dd MMMM yyyy").format(activationTime);
        }
        return actMsg;
    }

    /**
     * @see Renderer<T>#render(T, Appendable)
     */
    @Override
    public void render(final Date activationTime, final Appendable appendable) throws IOException {
        appendable.append(render(activationTime));
    }

}
