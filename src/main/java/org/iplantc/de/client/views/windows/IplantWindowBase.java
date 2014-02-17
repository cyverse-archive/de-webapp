package org.iplantc.de.client.views.windows;

import org.iplantc.de.client.Constants;
import org.iplantc.de.client.DeResources;
import org.iplantc.de.client.desktop.layout.DesktopLayoutType;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.WindowLayoutRequestEvent;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.views.windows.configs.WindowConfig;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.theme.window.IPlantWindowAppearance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.event.shared.HandlerRegistration;

import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.core.client.util.Rectangle;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent.MaximizeHandler;
import com.sencha.gxt.widget.core.client.event.RestoreEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jstroot
 * 
 */
public abstract class IplantWindowBase extends Window implements IPlantWindowInterface {
    interface WindowStateFactory extends AutoBeanFactory {
        AutoBean<WindowState> windowState();
    }

    protected WindowConfig config;
    protected Status status;

    protected boolean isMaximizable;
    protected boolean maximized;
    protected boolean minimized;

    private ToolButton btnMinimize;
    private ToolButton btnMaximize;
    private ToolButton btnRestore;
    private ToolButton btnClose;

    /**
     * Used to store the <code>HandlerRegistration</code>s of widgets when needed.
     */
    private final Map<Widget, List<HandlerRegistration>> handlerRegMap = new HashMap<Widget, List<HandlerRegistration>>();

    private final DeResources res = GWT.create(DeResources.class);

    private final WindowStateFactory wsf = GWT.create(WindowStateFactory.class);
    private ToolButton maxBtn;

    /**
     * Constructs an instance of the window.
     * 
     * @param tag a unique identifier for the window.
     */
    protected IplantWindowBase(String tag) {
        this(tag, false, true, false, true);
    }

    public IplantWindowBase(IPlantWindowAppearance appearance) {
        super(appearance);
    }

    protected IplantWindowBase(final String tag, final WindowConfig config) {
        this(tag, false, true, true, true, config);
    }

    protected IplantWindowBase(String tag, boolean haveStatus, boolean isMinimizable,
            boolean isMaximizable, boolean isClosable, WindowConfig config) {
        this(tag, haveStatus, isMinimizable, isMaximizable, isClosable);
        this.config = config;
    }

    public IplantWindowBase(String tag, boolean haveStatus, boolean isMinimizable,
            boolean isMaximizable, boolean isClosable) {
        super(GWT.<IPlantWindowAppearance> create(IPlantWindowAppearance.class));
        res.css().ensureInjected();
        setStateId(tag);
        this.isMaximizable = isMaximizable;

        if (haveStatus) {
            status = new Status();
            getHeader().addTool(status);
            status.hide();
        }

        getHeader().addTool(createLayoutButton());

        if (isMinimizable) {
            btnMinimize = createMinimizeButton();
            getHeader().addTool(btnMinimize);
        }
        if (isMaximizable) {
            btnMaximize = createMaximizeButton();
            // SRI: if a window is maximizable, then it is restorable.
            createRestoreButton();
            getHeader().addTool(btnMaximize);
            getHeader().sinkEvents(Event.ONDBLCLICK);
            getHeader().addHandler(createHeaderDblClickHandler(), DoubleClickEvent.getType());
        }
        if (isClosable) {
            btnClose = createCloseButton();
            getHeader().addTool(btnClose);
        }

        // Turn off default window buttons.
        setMaximizable(false);
        setMinimizable(false);
        setClosable(false);

        getHeader().setIcon(IplantResources.RESOURCES.iplantTiny());

        setShadow(false);
        setBodyBorder(false);
        setBorders(false);

        addHandler(new RestoreEvent.RestoreHandler() {
            @Override
            public void onRestore(RestoreEvent event) {
                replaceRestoreIcon();
            }
        }, RestoreEvent.getType());

        addMaximizeHandler(new MaximizeHandler() {
            @Override
            public void onMaximize(MaximizeEvent event) {
                replaceMaximizeIcon();
            }
        });
    }

    protected void doHide() {
        hide();
    }

    @Override
    public Point adjustPositionForView(Point position) {
        // We need to parse the string width and height here, since the window's element may not be
        // rendered yet, so those values could be 0 or something very large.
        int offsetWidth = Util.parseInt(width, 0);
        int offsetHeight = Util.parseInt(height, 0) + Constants.CLIENT.deTaskBarHeight();

        // Calculate the maximum X and Y position for this window.
        int maxX = XDOM.getViewportWidth();
        int maxY = XDOM.getViewportHeight();

        XElement container = (XElement)getContainer();
        if (container != null) {
            maxX = container.getBounds().getWidth();
            maxY = container.getBounds().getHeight();
        }

        maxX = Math.max(maxX - offsetWidth, 0);
        maxY = Math.max(maxY - offsetHeight, 0);

        // Calculate the adjusted position.
        Point adjusted = new Point(position.getX(), position.getY());
        if (adjusted.getX() > maxX) {
            adjusted.setX(0);
        }
        if (adjusted.getY() > maxY) {
            adjusted.setY(Constants.CLIENT.deHeaderHeight());
        }

        return adjusted;
    }

    @Override
    /**
     * SRI -
     * fit container for maximize by off setting for header and taskbar
     * 
     */
    protected void fitContainer() {
        // Offset for header height and real taskbar height.
        int maxHeightOffset = Constants.CLIENT.deHeaderHeight() + Constants.CLIENT.deTaskBarHeight() - 5;
        if (getContainer() != null) {
            Rectangle bounds = ((XElement)getContainer()).getBounds();
            setPagePosition(bounds.getX(), bounds.getY());
            setPixelSize(bounds.getWidth(), bounds.getHeight() - maxHeightOffset);
        } else {
            setPosition(0, 0);
            setPixelSize(XDOM.getViewportWidth(), XDOM.getViewportHeight() - maxHeightOffset);
            center();
        }
    }

    private ToolButton createLayoutButton() {
        final ToolButton layoutBtn = new ToolButton(res.css().xToolLayoutwindow());
        layoutBtn.setId("idLayout"); //$NON-NLS-1$
        layoutBtn.sinkEvents(Event.ONMOUSEOUT);
        layoutBtn.setToolTip("Layout");
        final Menu m = new Menu();
        m.add(buildCascadeLayoutMenuItem());
        m.add(buildTileLayoutMenuItem());

        ArrayList<HandlerRegistration> hrList = new ArrayList<HandlerRegistration>();
        HandlerRegistration reg;
        reg = layoutBtn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                m.showAt(layoutBtn.getAbsoluteLeft() + 10, layoutBtn.getAbsoluteTop() + 15);
            }
        });
        hrList.add(reg);

        reg = layoutBtn.addHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                layoutBtn.addStyleName(res.css().xToolLayoutwindowHover());
            }
        }, MouseOverEvent.getType());
        hrList.add(reg);

        reg = layoutBtn.addHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                layoutBtn.removeStyleName(res.css().xToolLayoutwindowHover());
            }
        }, MouseOutEvent.getType());
        hrList.add(reg);

        handlerRegMap.put(layoutBtn, hrList);

        return layoutBtn;
    }

    private MenuItem buildCascadeLayoutMenuItem() {
        MenuItem item = new MenuItem(DesktopLayoutType.CASCADE.toString());
        item.addSelectionHandler(new SelectionHandler<Item>() {

            @Override
            public void onSelection(SelectionEvent<Item> event) {
                fireLayoutRequest(DesktopLayoutType.CASCADE);
            }
        });

        return item;
    }

    private MenuItem buildTileLayoutMenuItem() {
        MenuItem item = new MenuItem(DesktopLayoutType.TILE.toString());
        item.addSelectionHandler(new SelectionHandler<Item>() {

            @Override
            public void onSelection(SelectionEvent<Item> event) {
                fireLayoutRequest(DesktopLayoutType.TILE);
            }
        });

        return item;
    }

    private void fireLayoutRequest(DesktopLayoutType type) {
        WindowLayoutRequestEvent event = new WindowLayoutRequestEvent(type);
        EventBus.getInstance().fireEvent(event);
    }

    private ToolButton createMaximizeButton() {
        maxBtn = new ToolButton(res.css().xToolMaximizewindow());
        maxBtn.setId("idmaximize"); //$NON-NLS-1$
        maxBtn.sinkEvents(Event.ONMOUSEOUT);
        maxBtn.setToolTip(org.iplantc.de.resources.client.messages.I18N.DISPLAY.maximize());

        ArrayList<HandlerRegistration> hrList = new ArrayList<HandlerRegistration>();
        HandlerRegistration reg;
        reg = maxBtn.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                setMaximized(true);
            }
        });
        hrList.add(reg);

        reg = maxBtn.addHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                maxBtn.addStyleName(res.css().xToolMaximizewindowHover());
            }
        }, MouseOverEvent.getType());
        hrList.add(reg);

        reg = maxBtn.addHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                maxBtn.removeStyleName(res.css().xToolMaximizewindowHover());
            }
        }, MouseOutEvent.getType());
        hrList.add(reg);

        handlerRegMap.put(maxBtn, hrList);
        return maxBtn;
    }

    private ToolButton createRestoreButton() {
        btnRestore = new ToolButton(res.css().xToolRestorewindow());
        btnRestore.setId("idrestore"); //$NON-NLS-1$
        btnRestore.sinkEvents(Event.ONMOUSEOUT);
        btnRestore.setToolTip(org.iplantc.de.resources.client.messages.I18N.DISPLAY.restore());

        ArrayList<HandlerRegistration> hrList = new ArrayList<HandlerRegistration>();
        HandlerRegistration reg;
        reg = btnRestore.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                setMaximized(false);
            }
        });
        hrList.add(reg);

        reg = btnRestore.addHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                btnRestore.addStyleName(res.css().xToolRestorewindowHover());
            }
        }, MouseOverEvent.getType());
        hrList.add(reg);

        reg = btnRestore.addHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                btnRestore.removeStyleName(res.css().xToolRestorewindowHover());
            }
        }, MouseOutEvent.getType());
        hrList.add(reg);

        handlerRegMap.put(btnRestore, hrList);
        return btnRestore;
    }

    private ToolButton createMinimizeButton() {
        final ToolButton newMinBtn = new ToolButton(res.css().xToolMinimizewindow());
        newMinBtn.setId("idminimize"); //$NON-NLS-1$
        newMinBtn.sinkEvents(Event.ONMOUSEOUT);
        newMinBtn.setToolTip(org.iplantc.de.resources.client.messages.I18N.DISPLAY.minimize());

        newMinBtn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                minimized = true;
                minimize();
                newMinBtn.removeStyleName(res.css().xToolMinimizewindowHover());
            }
        });

        newMinBtn.addHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                newMinBtn.addStyleName(res.css().xToolMinimizewindowHover());
            }
        }, MouseOverEvent.getType());

        newMinBtn.addHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                newMinBtn.removeStyleName(res.css().xToolMinimizewindowHover());
            }
        }, MouseOutEvent.getType());

        return newMinBtn;
    }

    private ToolButton createCloseButton() {
        final ToolButton newCloseBtn = new ToolButton(res.css().xToolClosewindow());
        newCloseBtn.setId("idclose"); //$NON-NLS-1$
        newCloseBtn.sinkEvents(Event.ONMOUSEOUT);
        newCloseBtn.setToolTip(org.iplantc.de.resources.client.messages.I18N.DISPLAY.close());

        newCloseBtn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                doHide();
            }
        });

        newCloseBtn.addHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                newCloseBtn.removeStyleName(res.css().xToolClosewindowHover());
            }
        }, MouseOutEvent.getType());

        newCloseBtn.addHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                newCloseBtn.addStyleName(res.css().xToolClosewindowHover());
            }
        }, MouseOverEvent.getType());

        return newCloseBtn;
    }

    private DoubleClickHandler createHeaderDblClickHandler() {
        DoubleClickHandler handler = new DoubleClickHandler() {

            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                if (!isMaximized()) {
                    maximize();
                } else {
                    restore();
                }
            }
        };

        return handler;
    }

    /**
     * Replaces the maximize icon with the restore icon.
     * 
     * The restore icon is only visible to the user when a window is in maximized state.
     */
    private void replaceMaximizeIcon() {
        int index = findToolButtonIndex(res.css().xToolMaximizewindow());
        if (index > -1) {
            getHeader().removeTool(btnMaximize);
            btnMaximize.removeFromParent();
            // re-insert restore button at same index of maximize button
            getHeader().insertTool(btnRestore, index);
        }
    }

    /**
     * Replaces the restore icon with the maximize icon.
     */
    private void replaceRestoreIcon() {
        int index = findToolButtonIndex(res.css().xToolRestorewindow());
        if (index > -1) {
            getHeader().removeTool(btnRestore);
            // re-insert maximize button at same index as restore button
            getHeader().insertTool(btnMaximize, index);
        }
    }

    private int findToolButtonIndex(String btnToolName) {
        int toolCount = getHeader().getToolCount();
        int index = -1;

        for (int i = 0; i < toolCount; i++) {
            Widget tool = getHeader().getTool(i);
            String fullStyle = tool.getStyleName();

            if (fullStyle.contains(btnToolName)) {
                index = i;
                break;
            }
        }

        return index;
    }

    protected <C extends WindowConfig> WindowState createWindowState(C config) {
        WindowState ws = wsf.windowState().as();
        ws.setConfigType(config.getWindowType());
        ws.setMaximized(isMaximized());
        ws.setMinimized(!isVisible());
        ws.setWinLeft(getAbsoluteLeft());
        ws.setWinTop(getAbsoluteTop());
        ws.setWidth(getElement().getWidth(true));
        ws.setHeight(getElement().getHeight(true));
        Splittable configSplittable = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(config));
        ws.setWindowConfig(configSplittable);
        return ws;
    }

    @Override
    public Point getPosition3(boolean b) {
        return getElement().getPosition(b);
    }

    @Override
    public boolean isMaximized() {
        return maximized;
    }

    @Override
    public void setMaximized(boolean maximize) {
        if (isMaximizable) {
            this.maximized = maximize;

            if (maximize) {
                maximize();
                btnRestore.removeStyleName(res.css().xToolRestorewindowHover());
            } else {
                restore();
                btnMaximize.removeStyleName(res.css().xToolMaximizewindowHover());
            }
        }
    }

    @Override
    public boolean isMinimized() {
        return minimized;
    }

    @Override
    public void setMinimized(boolean min) {
        minimized = min;
    }

    @Override
    public void setTitle(String wintitle) {
        setHeadingText(wintitle);
    }

    @Override
    public String getTitle() {
        return getHeader().getText();
    }

    @Override
    public void setPixelSize(int width, int height) {
        super.setPixelSize(width, height);
    }

    @Override
    public int getHeaderOffSetHeight() {
        return getHeader().getOffsetHeight();
    }

    @Override
    public <C extends WindowConfig> void update(C config) {
    }

}
