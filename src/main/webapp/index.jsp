<%@ page import="org.iplantc.de.server.DiscoveryEnvironmentMaintenance" %>
<%@ page import="org.iplantc.de.server.DiscoveryEnvironmentProperties" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"> 
<head> 
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /> 
<title>iPlant Collaborative Discovery Environment</title>

<link href="<%= request.getContextPath() %>/landing-page-style.css" rel="stylesheet" type="text/css" />
</head>

<body>

<img src="http://iplantc.org/sites/all/themes/iplant/images/iplant_logo.png" border="0" alt="Log in to iPlant" style="margin-left: 10px;" /> 

<div style="width: 60%; margin-left: 10px;">
<h2>Discovery Environment</h2>
The Discovery Environment integrates powerful, community-recommended software tools into a system that:
<ul type="square">
<li> Makes big data management easy. Upload, organize, edit, view and search with ease!</li>
<li> Has 500+ scientific apps that utilizes compute clusters and HPC resources as needed.</li>
<li> Hides the complexity needed to do these tasks.</li>
</ul>
</div>

<%
ServletContext ctx = getServletConfig().getServletContext();
DiscoveryEnvironmentProperties props = DiscoveryEnvironmentProperties.getDiscoveryEnvironmentProperties(ctx);
DiscoveryEnvironmentMaintenance deMaintenance = new DiscoveryEnvironmentMaintenance(props.getMaintenanceFile());
if (deMaintenance.isUnderMaintenance()) {
    if (deMaintenance.hasMaintenanceTimes()) {
        String startTime = deMaintenance.getStartTime();
        String endTime = deMaintenance.getEndTime();
%>
        <div class="maintenance_wrapper">
            Discovery Environment is under maintenance from <%=startTime%> to <%=endTime%>.
        </div>
<%
    } else {
%>
        <div class="maintenance_wrapper">
            Discovery Environment is under maintenance.
        </div>
<%
    }
} else {
%>
    <div class="login-wrapper">
        <form action="Discoveryenvironment.jsp">
            <input type="submit" value="Log in with your iPlant ID" class="submitButton">
        </form>
        <a href="https://user.iplantcollaborative.org/reset/request">Forgot Password?</a> <a href="https://user.iplantcollaborative.org/register/">Register Now</a>
        <div class="label"> Minimum screen resolution supported: 1024 x 768 </div>
    </div>
<%
}
%>

<div class="project_text">&copy;2014 iPlant Collaborative. The
iPlant Collaborative is funded by a grant from the National Science
Foundation (#DBI-0735191).</div>
<div class="footer">

<table align="center"><tr>
<td class="whiteline" width="20%" style="vertical-align: text-top"><a href="https://pods.iplantcollaborative.org/wiki/display/DEmanual/Table+of+Contents">The Discovery Environment Manual</a><br />
A useful resource for new users.</td>

<td class="whiteline" width="20%" style="vertical-align: text-top"><a href="https://pods.iplantcollaborative.org/wiki/display/start/Getting+Started+with+iPlant">Getting Started with iPlant</a><br />
Helps you discover what other iPlant services you can 
use and ways to collaborate with iPlant.</td>

<td class="whiteline" width="20%" style="vertical-align: text-top"><a href="http://www.iplantcollaborative.org/">The Main iPlant Homepage</a><br />
Learn more about iPlant.</td>

<td class="whiteline" width="20%" style="vertical-align: text-top"><a href="http://ask.iplantcollaborative.org">Ask iPlant</a><br />
Questions? Please visit our forums.</td>

<td class="noline" width="20%" style="vertical-align: text-top"><a href="http://www.iplantcollaborative.org/forms/support">Need Help?</a><br />
Contact support if you need assistance.</td>

</tr></table>

</div>

</body>
</html>
