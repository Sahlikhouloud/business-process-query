/** Copyright (c) 2011
 * new plug in for querying similar activities
 * created by Nattawat Nonsung 
 * armmer1@gmail.com
*/

if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.Query = Clazz.extend({
	
	// Defines the facade
    facade		: undefined,
    
	// Defines the undo/redo Stack
	undoStack	: [],
	
	// Constructor 
    construct: function(facade){
    
        this.facade = facade;     
		
		// Offers the functionality of undo                
        this.facade.offer({
			name			: ORYX.I18N.Query.query,
			description		: ORYX.I18N.Query.queryDesc,
			icon			: ORYX.PATH + "images/query.png",
//			keyCodes: [{
//					metaKeys: [ORYX.CONFIG.META_KEY_META_CTRL],
//					keyCode: 90,
//					keyAction: ORYX.CONFIG.KEY_ACTION_DOWN
//				}
//		 	],
			functionality	: this.querying.bind(this),
			group			: ORYX.I18N.Query.group,
			isEnabled		: function(){ return true }.bind(this),
			index			: 1
		}); 

		// Register on event for key down --> store all commands in a stack		 
//		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEDOWN, this.handleExecuteCommands.bind(this) );
    	
	},
	
	handleExecuteCommands: function( evt ){
		
	},
	
	sendSaveRequest: function(method, url, params, forceNew, success, failure){
		
		// Send the request to the server.
		Ext.Ajax.request({
			url				: url,
			method			: method,
			timeout			: 1800000,
			disableCaching	: true,
			headers			: {'Accept':"application/json", 'Content-Type':'charset=UTF-8'},
			params			: params,
			success			: success,
			failure			: failure
		});
	},
	
	/**
	 * Does the querying
	 * 
	 */
	querying: function(){
		
		var modelMeta = this.facade.getModelMetaData();
		var reqURI = modelMeta.modelHandler;

		// Get the stencilset
		var ss = this.facade.getStencilSets().values()[0]
		
		var typeTitle = ss.title();
		
		// Define Default values
		var defaultData = {title:Signavio.Utils.escapeHTML(name||""), summary:Signavio.Utils.escapeHTML(modelMeta.description||""), type:typeTitle, url: reqURI, namespace: modelMeta.model.stencilset.namespace, comment: '' }
		
		// Create a Template
		var dialog = new Ext.XTemplate(		
			// TODO find some nice words here -- copy from above ;)
			'<form class="oryx_repository_edit_model" action="#" id="query_model" onsubmit="return false;">',
							
				'<fieldset>',
					'<p class="description">' + ORYX.I18N.Query.dialogDesciption + '</p>',
					'<input type="hidden" name="namespace" value="{namespace}" />',
//					'<p><label for="edit_model_title">' + ORYX.I18N.Save.dialogLabelTitle + '</label><input type="text" class="text" name="title" value="{title}" id="edit_model_title" onfocus="this.className = \'text activated\'" onblur="this.className = \'text\'"/></p>',
//					'<p><label for="edit_model_summary">' + ORYX.I18N.Save.dialogLabelDesc + '</label><textarea rows="5" name="summary" id="edit_model_summary" onfocus="this.className = \'activated\'" onblur="this.className = \'\'">{summary}</textarea></p>',
//					(modelMeta.versioning) ? '<p><label for="edit_model_comment">' + ORYX.I18N.Save.dialogLabelComment + '</label><textarea rows="5" name="comment" id="edit_model_comment" onfocus="this.className = \'activated\'" onblur="this.className = \'\'">{comment}</textarea></p>' : '',
					'<p><label for="edit_model_type">' + ORYX.I18N.Save.dialogLabelType + '</label><input type="text" name="type" class="text disabled" value="{type}" disabled="disabled" id="edit_model_type" /></p>',
					
				'</fieldset>',
			
			'</form>')
	
		// Create a new window				
		var win = new Ext.Window({
			id		: 'Query_Window',
	        width	: 'auto',
	        height	: 'auto',
		    title	: ORYX.I18N.Query.queryDesc,
	        modal	: true,
	        resize	: false,
			bodyStyle: 'background:#FFFFFF',
	        html	: dialog.apply( defaultData ),
	        defaultButton: 0,
			buttons:[{
				text: ORYX.I18N.Query.queryBtn,
				handler: function(){
				
					win.body.mask(ORYX.I18N.Query.pleaseWait, "x-waiting-box");
					
					window.setTimeout(function(){
						
						callback($('query_model'));
						
					}.bind(this), 10);			
				},
				listeners:{
					render:function(){
						this.focus();
					}
				}
			},{
            	text: ORYX.I18N.Save.close,
            	handler: function(){
	               win.close();
            	}.bind(this)
			}],
			listeners: {
				close: function(){					
                	win.destroy();
					delete this.saving;
				}.bind(this)
			}
	    });
				      
		win.show();
		
		// Create the callback for the template
		callback = function(form){

//				var title 		= form.elements["title"].value.strip();
//				title 			= title.length == 0 ? defaultData.title : title;
//				
//				var summary 	= form.elements["summary"].value.strip();	
//				summary 		= summary.length == 0 ? defaultData.summary : summary;
//				
				var namespace	= form.elements["namespace"].value.strip();
//				namespace		= namespace.length == 0 ? defaultData.namespace : namespace;
//				
//				var comment 	= form .elements["comment"].value.strip();
//				comment			= comment.length == 0 ? defaultData.comment : comment;
				
//				modelMeta.name = title;
//				modelMeta.description = summary;
//				modelMeta.parent = modelInfo.parent;
				modelMeta.namespace = namespace;
				Ext.Msg.alert(ORYX.I18N.Oryx.title, reqURI).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
//				this.sendSaveRequest('GET', reqURI, params, forceNew, successFn, failure);
				new Ajax.Request('/signaviocore/p/query/', {
		            method: 'get',
		            asynchronous: true,
					requestHeaders: {
						"Accept":"application/json"
					},
					encoding: 'UTF-8',
					onSuccess: (function(transport) {
						var resJSON = transport.responseText.evalJSON();
						Ext.Msg.alert(ORYX.I18N.Oryx.title, resJSON[0].token).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
					}).bind(this),
					onException: function(){
					
					}.bind(this),
					onFailure: (function(transport) {
						
					}).bind(this),
					on401: (function(transport) {
						
					}).bind(this),
					on403: (function(transport) {
						
					}).bind(this)
				});
				win.close();
		}.bind(this);
	}
	
});
