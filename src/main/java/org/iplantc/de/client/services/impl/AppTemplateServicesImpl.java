package org.iplantc.de.client.services.impl;

import org.iplantc.core.uiapps.integration.client.services.AppTemplateServices;
import org.iplantc.core.uiapps.integration.client.services.impl.AppTemplateCallbackConverter;
import org.iplantc.core.uiapps.widgets.client.models.AppTemplate;
import org.iplantc.core.uicommons.client.DEServiceFacade;
import org.iplantc.core.uicommons.client.models.DEProperties;
import org.iplantc.core.uicommons.client.models.HasId;
import org.iplantc.de.shared.SharedServiceFacade;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

public class AppTemplateServicesImpl implements AppTemplateServices {

    @Override
    public void getAppTemplate(HasId appId, AsyncCallback<AppTemplate> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() 
                + "app/" + appId.getId(); //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        DEServiceFacade.getInstance().getServiceData(wrapper, new AppTemplateCallbackConverter(callback));
    }

    @Override
    public void getAppTemplateForEdit(HasId appId, AsyncCallback<AppTemplate> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl()
                + "edit-app/" + appId.getId(); //$NON-NLS-1$
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        DEServiceFacade.getInstance().getServiceData(wrapper, new AppTemplateCallbackConverter(callback));
    }

    @Override
    public void saveAndPublishAppTemplate(AppTemplate at, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getMuleServiceBaseUrl() 
                + "update-app"; //$NON-NLS-1$
        Splittable split = appTemplateToSplittable(at);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.PUT, 
                address, split.getPayload());
        callSecuredService(callback, wrapper);
    }
    
    @Override
    public void getAppTemplatePreview(AppTemplate at, AsyncCallback<AppTemplate> callback) {
        String address = DEProperties.getInstance().getUnproctedMuleServiceBaseUrl() 
                + "preview-template"; //$NON-NLS-1$
        Splittable split = appTemplateToSplittable(at);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, 
                address, split.getPayload());
        DEServiceFacade.getInstance().getServiceData(wrapper, new AppTemplateCallbackConverter(callback));
    }

    private Splittable appTemplateToSplittable(AppTemplate at){
        AutoBean<AppTemplate> ab = AutoBeanUtils.getAutoBean(at);
        return AutoBeanCodex.encode(ab);
    }

    private void callSecuredService(AsyncCallback<String> callback, ServiceCallWrapper wrapper) {
        SharedServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

    @Override
    public void rerunAnalysis(HasId analysisId, AsyncCallback<AppTemplate> callback) {
        String address = DEProperties.getInstance().getUnproctedMuleServiceBaseUrl() 
                + "app-rerun-info/" + analysisId.getId(); //$NON-NLS-1$

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);

        DEServiceFacade.getInstance().getServiceData(wrapper, new AppTemplateCallbackConverter(callback));
    }

    @Override
    public void cmdLinePreview(AppTemplate at, AsyncCallback<String> callback) {
        String address = DEProperties.getInstance().getUnproctedMuleServiceBaseUrl()
               + "arg-preview"; //$NON-NLS-1$
        Splittable split = appTemplateToSplittable(at);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, 
                address, split.getPayload());
        DEServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

}
