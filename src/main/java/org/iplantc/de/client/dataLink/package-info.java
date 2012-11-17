/**
 * 
 * Welcome to the Data Link package!!
 * 
 * I'll be your tourguide.

 * <h1>Summary of what IS done</h1> 
 * The creation and deletion of data links is complete.
 * 
 * When you create tickets, they should appear dynamically in the tree. When you delete, they should dissappear.
 * 
 * The normal tree node close/open icons have been nullified (see the DataLinkPanel constructor). 
 * The images displayed come from the DataLinkPanelCell
 * 
 * <h1>What Needs to be done</h1>
 * 
 * 1. I did not get to the selection or copying of the links. 
 *  a. I think this can be accomplished in the DataLinkPanelCell. 
 *  b. Looking around online, many people reference the following project; 
 *     http://code.google.com/p/zeroclipboard/
 *      I suspect it has been migrated to github
 *     https://github.com/jonrohan/ZeroClipboard
 *  c. Yesterday, someone mentioned that John had used clippy
 *     https://github.com/mojombo/clippy
 * 2. White background in panel.
 * 
 * ------------------------------------------------
 * There are two sub-packages; models and view
 * 
 * <h1>Models Package</h1> Models contains the interface definitions for the <code>DataLink</code>
 * object.
 * 
 * Both <code>DataLink</code> and <code>DiskResource</code> extend a new interface,
 * <code>IDiskResource</code>.
 * This was done so that both objects can be displayed in the same
 * {@link com.sencha.gxt.widget.core.client.tree.Tree}.
 * 
 * The <code>DataLinkList</code> object is a convenience object for de-serializing JSON arrays of
 * <code>DataLink</code> objects.
 * 
 * All deserialization of <code>DataLink</code>s is done in <code>DataLinkPanel</code> callback private
 * classes (there is no presenter).
 * 
 * <h1>View Package</h2>
 * 
 * TODO JDS Insert some copy-to-clipboard button in {@link DataLinkPanelCell}
 * 
 * TODO JDS Error messages which were created are generic standins, they should be revisited.
 * 
 * FIXME? JDS Currently, json response is not indicating to which Disk resource each ticket belongs. This
 * is happening in the create data links callback in the DataLinkPanel class
 * 
 * 
 * 
 * 
 * 
 * @author jstroot
 * 
 */
package org.iplantc.de.client.dataLink;