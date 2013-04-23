package org.iplantc.de.client.utils;

import java.util.LinkedList;
import java.util.Queue;

import com.extjs.gxt.ui.client.util.Params;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.InfoConfig;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.core.client.util.Size;

/**
 * Display info to the user in the lower-right corner.
 * 
 * @author amuir
 * @deprecated Class needs to be ported to GXT3
 */
@Deprecated
public class DEInfo extends ContentPanel {
    private static Queue<DEInfo> infoQueue = new LinkedList<DEInfo>();

    /**
     * Displays a message using the specified config.
     * 
     * @param config the info config
     */
    public static void display(InfoConfig config) {
        if (config != null) {
            add(new DEInfo(config));
        }

        if (infoQueue.size() == 1) {
            peek().onShowInfo();
        }
    }

    /**
     * Displays a message with the given title and text.
     * 
     * @param title the title
     * @param text the text
     */
    public static void display(String title, String text) {
        display(new InfoConfig(title, text));
    }

    /**
     * Displays a message with the given title and text. The passed parameters will be applied to both
     * the title and text before being displayed.
     * 
     * @param title the info title
     * @param text the info text
     * @param params the parameters to be applied to the title and text
     */
    public static void display(String title, String text, Params params) {
        InfoConfig config = new InfoConfig(title, text, params);
        display(config);
    }

    private static DEInfo peek() {
        return infoQueue.peek();
    }

    private static DEInfo remove() {
        return infoQueue.isEmpty() ? null : infoQueue.remove();
    }

    private static void add(DEInfo info) {
        if (info != null) {
            infoQueue.add(info);
        }
    }

    protected InfoConfig config;

    /**
     * Creates a new info instance.
     */
    public DEInfo(InfoConfig config) {
        this.config = config;
        baseStyle = "x-info"; //$NON-NLS-1$
        frame = true;
        setShadow(true);
        setLayoutOnChange(true);
    }

    /**
     * Hides the pop-up.
     */
    @Override
    public void hide() {
        super.hide();
        afterHide();
    }

    /**
     * Called after an info is hidden.
     */
    protected void afterHide() {
        RootPanel.get().remove(this);

        remove();

        // give the last item time to be removed before showing the next one.
        Timer t = new Timer() {
            @Override
            public void run() {
                DEInfo info = peek();

                if (info != null) {
                    info.onShowInfo();
                }
            }
        };

        t.schedule(500);
    }

    /**
     * Called after an info is shown.
     */
    protected void afterShow() {
        Timer t = new Timer() {
            @Override
            public void run() {
                afterHide();
            }
        };

        t.schedule(config.display);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onRender(Element parent, int pos) {
        super.onRender(parent, pos);
    }

    private void onShowInfo() {
        RootPanel.get().add(this);
        el().makePositionable(true);

        setTitle();
        setText();

        Point p = position();
        el().setLeftTop(p.getX(), p.getY());
        setSize(config.width, config.height);

        afterShow();
    }

    private Point position() {
        Size s = XDOM.getViewportSize();
        int left = s.getWidth() - config.width - 10 + XDOM.getBodyScrollLeft();
        int top = s.getHeight() - config.height - 10 + XDOM.getBodyScrollTop();

        return new Point(left, top);
    }

    private void setText() {
        if (config.text != null) {
            if (config.params != null) {
                config.text = Format.substitute(config.text, config.params);
            }

            removeAll();
            addText(config.text);
        }
    }

    private void setTitle() {
        if (config.title != null) {
            head.setVisible(true);

            if (config.params != null) {
                config.title = Format.substitute(config.title, config.params);
            }

            setHeading(config.title);
        } else {
            head.setVisible(false);
        }
    }
}
