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
	
	/**
	 * Does the querying
	 * 
	 */
	querying: function(){
		
		var modelMeta = this.facade.getModelMetaData();
		var reqURI = modelMeta.modelHandler;
		var reqURIs = reqURI.split("/");
		var prefix = "/";
	    for(i=1; i<reqURIs.length-1; i++){
		    prefix+=reqURIs[i]+"/";
	    }
		var modelJSON = this.facade.getJSON();
		var canvasChilds  = modelJSON.childShapes;
		var tasks = [];
		for(var i=0; i<canvasChilds.length; i++){
		    if(canvasChilds[i].stencil.id == 'Task' || canvasChilds[i].stencil.id == 'CollapsedSubprocess'){
		    	tasks.push(canvasChilds[i].properties.name);
		    }
	    }
		
		var taskTxt = "[";
		for(i=0; i<tasks.length; i++){
			taskTxt+="['"+tasks[i].strip()+"', '"+tasks[i].strip()+"'],";
	    }
		taskTxt = taskTxt.substring(0,taskTxt.length-1)+"]";
		
		// Get the stencilset
		var ss = this.facade.getStencilSets().values()[0]
		
		var typeTitle = ss.title();
		
		// Define Default values
		var defaultData = {title:Signavio.Utils.escapeHTML(name||""), summary:Signavio.Utils.escapeHTML(modelMeta.description||""), type:typeTitle, url: reqURI, namespace: modelMeta.model.stencilset.namespace, comment: '' }
		
		// Create form
		var methods = [
		                [2, 'Levenstein']
		               ,[4, 'Improved weight']
		           ];
		var formPanel = new Ext.form.FormPanel({
			id		: 'query_model',
			bodyStyle:'padding:10px',
	        width	: 'auto',
	        height	: 'auto',
            items:[ 
                     new Ext.form.ComboBox({
                    	fieldLabel: ORYX.I18N.Query.targetTask,
                    	name: 'task',
                    	id: 'task',
                    	store: new Ext.data.SimpleStore({
   					    	fields:['myId', 'myText'],
   					        data:eval(taskTxt)
   					    })
                     	,allowBlank:false
                     	,autoWidth:true
                     	,emptyText: '-- select --'
   					    ,valueField:'myId'
					    ,displayField:'myText'
					    ,mode:'local'
					    ,triggerAction: 'all'
					    ,listeners:{
					       'select': function(){
			    	   			// get max zone
					    	   	Ext.Ajax.request({
						   			url				: prefix+'query/',
						   			method			: "GET",
						   			timeout			: 1800000,
						   			disableCaching	: true,
						   			headers			: {'Accept':"application/json", 'Content-Type':'charset=UTF-8'},
						   			params			: {
														id: 'getMaxZone',
														task: this.getValue().strip(),
														processID: modelMeta.name
										              },
						   			success			: function(transport) {
						   								var zoneJson = transport.responseText.evalJSON();
						   								Ext.getCmp('zone').reset();
						   								var zoneCmp = Ext.getCmp('zone');
						   								var rt = Ext.data.Record.create([
						   								    {name: 'myId'},
						   								    {name: 'myText'}
						   								]);
						   								var zoneStore = new Ext.data.Store ({
						   									isAutoLoad: true,
						   								    reader: new Ext.data.JsonReader({
						   								    	root: 'zone',
							   								    fields: [
							   								        {name: 'myId', mapping: 'myId'},
							   								        {name: 'myText', mapping: 'myText'}
							   								    ]},rt)
						   								})
						   								zoneStore.loadData(zoneJson);
						   								zoneCmp.bindStore(zoneStore);
													  },
						   			failure			: function(transport) {
													  }
						   		})
				    	   	}
					    }
                     }),
                     new Ext.form.ComboBox({
                    	fieldLabel: ORYX.I18N.Query.zone,
                    	name: 'zone',
                    	id: 'zone',
                     	store: new Ext.data.SimpleStore({
   					    	fields:['myId', 'myText'],
   					        data:[]
   					    })
                     	,allowBlank:false
                     	,emptyText: '-- select --'
   					    ,valueField:'myId'
 					    ,displayField:'myText'
 					    ,mode:'local'
 					    ,triggerAction: 'all'
                      }),
                      new Ext.form.ComboBox({
                    	fieldLabel: ORYX.I18N.Query.method,
                    	name: 'method',
                    	id: 'method',
   					    store: new Ext.data.SimpleStore({
   					    	fields:['myId', 'myText'],
   					        data:methods
   					    })
                      	,allowBlank:false
                      	,emptyText: '-- select --'
   					    ,valueField:'myId'
   					    ,displayField:'myText'
   					    ,mode:'local'
   					    ,triggerAction: 'all'
                      })
            ] 
		});
		
		// Create new window and attach form into it
		var win = new Ext.Window({
	        id		: 'Query_Window',
	        width	: 'auto',
	        height	: 'auto',
		    title	: ORYX.I18N.Query.queryDesc,
	        modal	: true,
	        resize	: false,
			bodyStyle: 'background:#FFFFFF',
	        items  : [formPanel],
            defaultButton: 0,
				buttons:[{
				text: ORYX.I18N.Query.queryBtn,
				handler: function(){
				
					win.body.mask(ORYX.I18N.Query.pleaseWait, "x-waiting-box");
					
					window.setTimeout(function(){
						
						callback(formPanel.getForm());
						
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
			var task = form.findField('task').getValue();
			var zone = form.findField('zone').getValue();
			var method = form.findField('method').getValue();
			new Ajax.Request(prefix+'query/', {
	            method: 'get',
	            asynchronous: true,
				requestHeaders: {
					"Accept":"application/json"
				},
				parameters: {
					id: 'getRecommendation',
					task: task,
					zone: zone,
					method: method,
					processID: modelMeta.name
	            },
				encoding: 'UTF-8',
				onSuccess: (function(transport) {
					var resJSON = transport.responseText.evalJSON();
				}).bind(this),
				onException: function(){
					Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getFailure).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
				}.bind(this),
				onFailure: (function(transport) {
					Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getFailure).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
				}).bind(this),
				on401: (function(transport) {
					Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getFailure).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
				}).bind(this),
				on403: (function(transport) {
					Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getFailure).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
				}).bind(this)
			});
			win.close();
		}.bind(this);
	}
	
});
