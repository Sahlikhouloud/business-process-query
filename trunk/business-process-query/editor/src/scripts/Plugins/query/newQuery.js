/** Copyright (c) 2011
 * new plug in for create a new query for finding similar activities
 * created by Nattawat Nonsung 
 * armmer1@gmail.com
*/

if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.NewQuery = Clazz.extend({
	// Defines the facade
    facade		: undefined,
    
 // Constructor 
    construct: function(facade){
    
        this.facade = facade;
		
		// Offers the functionality of undo                
        this.facade.offer({
			name			: ORYX.I18N.Query.newQuery,
			description		: ORYX.I18N.Query.newQueryAltDesc,
			icon			: ORYX.PATH + "images/newQuery.png",
			keyCodes: [{
					metaKeys: [ORYX.CONFIG.META_KEY_META_CTRL,ORYX.CONFIG.META_KEY_ALT,ORYX.CONFIG.META_KEY_SHIFT],
					keyCode: 81, //q key-code
					keyAction: ORYX.CONFIG.KEY_ACTION_DOWN
				}
		 	],
			functionality	: this.querying.bind(this),
			group			: ORYX.I18N.Query.group,
			isEnabled		: function(){ 
								// disable in case of query process
								if(this.facade.getModelMetaData().name !="" ){
									var nameFragments = this.facade.getModelMetaData().name.split(".");
						        	if(nameFragments.length>1 && nameFragments[1] == 'query'){
						        		return false;
						        	}else{
						        		return true;
						        	}
								}else{
									return true;
								}
							  }.bind(this),
			index			: 2
		}); 

		// Register on event for key down --> store all commands in a stack		 
//		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_MOUSEDOWN, this.handleExecuteCommands.bind(this) );
    	
	},
	
	/**
	 * Does the querying
	 * 
	 */
	querying: function(){
		if(!this.isQueryProcess(this.facade.getModelMetaData().name)){
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
			
			//create form
			var formPanel = new Ext.form.FormPanel({
				id		: 'new_query_model',
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
						    	   formPanel.body.mask(ORYX.I18N.Query.pleaseWait, "x-waiting-box");
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
							   								formPanel.body.unmask();
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
							   									formPanel.body.unmask();
												   				Ext.getCmp('zone').reset();
								   								var zoneCmp = Ext.getCmp('zone');
							   									var zoneStore = new Ext.data.SimpleStore({
										   					    	fields:['myId', 'myText'],
										   					        data:[['1','1'],['2','2'],['3','3'],['4','4'],['5','5']]
										   					    })
							   									zoneCmp.bindStore(zoneStore);
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
	                      })
	            ] 
			});
			
			// Create new window and attach form into it
			var win = new Ext.Window({
		        id		: 'New_Query_Window',
		        width	: 'auto',
		        height	: 'auto',
			    title	: ORYX.I18N.Query.newQueryDesc,
		        modal	: true,
		        resizable	: false,
				bodyStyle: 'background:#FFFFFF',
		        items  : [formPanel],
	            defaultButton: 0,
					buttons:[{
						text: ORYX.I18N.Query.modifyBtn,
						handler: function(){
								win.body.mask(ORYX.I18N.Query.pleaseWait, "x-waiting-box");
								
								window.setTimeout(function(){
									duplicateProcess(formPanel.getForm());
								}.bind(this), 10);
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
			
			// for modifying query ---> create new .query file (refer to save as function - in file.js)
			duplicateProcess = function(form){
				var queryName;
				var json = modelJSON;
				var glossary = [];
				var sendSaveRequest = this.sendSaveRequest.bind(this);
				//Support for glossary
				if (this.facade.hasGlossaryExtension) {
					
					Ext.apply(json, ORYX.Core.AbstractShape.JSONHelper);
					var allNodes = json.getChildShapes(true);
					
					var orders = {};
					
					this.facade.getGlossary().each(function(entry){
						if ("undefined" == typeof orders[entry.shape.resourceId+"-"+entry.property.prefix()+"-"+entry.property.id()]){
							orders[entry.shape.resourceId+"-"+entry.property.prefix()+"-"+entry.property.id()] = 0;
						}
						// Add entry
						glossary.push({
							itemId		: entry.glossary,
			            	elementId	: entry.shape.resourceId,
			            	propertyId	: entry.property.prefix()+"-"+entry.property.id(),
				            order		: orders[entry.shape.resourceId+"-"+entry.property.prefix()+"-"+entry.property.id()]++
						});
						
						// Replace SVG
						if (entry.property.refToView() && entry.property.refToView().length > 0) {
							entry.property.refToView().each(function(ref){
								var node = $(entry.shape.id+""+ref);
								if (node)
									node.setAttribute("oryx:glossaryIds", entry.glossary + ";")
							})
						}
					}.bind(this))

					// Set the json as string
					json = json.serialize();

				} else {
					json = Ext.encode(json);
				}
				// Set the glossaries as string
				glossary = Ext.encode(glossary);
				
				var selection = this.facade.getSelection();
				this.facade.setSelection([]);

				// Get the serialized svg image source
		        var svgClone 	= this.facade.getCanvas().getSVGRepresentation(true);
				this.facade.setSelection(selection);
		        if (this.facade.getCanvas().properties["oryx-showstripableelements"] === false) {
		        	var stripOutArray = svgClone.getElementsByClassName("stripable-element");
		        	for (var i=stripOutArray.length-1; i>=0; i--) {
		        		stripOutArray[i].parentNode.removeChild(stripOutArray[i]);
		        	}
		        }
				  
				// Remove all forced stripable elements 
	        	var stripOutArray = svgClone.getElementsByClassName("stripable-element-force");
	        	for (var i=stripOutArray.length-1; i>=0; i--) {
	        		stripOutArray[i].parentNode.removeChild(stripOutArray[i]);
	        	}
				          
				// Parse dom to string
		        var svgDOM 	= DataManager.serialize(svgClone);
		        var ss = this.facade.getStencilSets().values()[0];
		        
		        var successFn = function(transport) {
		        	//insert into query description table
		        	var task = form.findField('task').getValue();
					var zone = form.findField('zone').getValue();
		        	new Ajax.Request(prefix+'query/', {
			            method: 'post',
			            asynchronous: true,
						requestHeaders: {
							"Accept":"application/json"
						},
						parameters: {
							jobId: 'newQuery',
							processID: queryName,
							zone: zone,
							targetProcess: modelMeta.name,
							targetTask: task,
							queryDesc: "",
							parent: modelMeta.parent
			            },
						encoding: 'UTF-8',
						onSuccess: function(transport){
						},
						onException: function(){
							Ext.WindowMgr.get('New_Query_Window').close();  
							Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.saveQeuryexception).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
						}.bind(this),
						onFailure: (function(transport) {
							Ext.WindowMgr.get('New_Query_Window').close();  
							Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.saveQeuryexception).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
						}).bind(this)
		        	});
		        	
		        	var loc = transport.getResponseHeader.location;
					if (!this.processURI && loc) {
						this.processURI = loc;
					}
					
					// create new window
					var resJSON = transport.responseText.evalJSON();
					
					var modelURL = location.href.substring(0, location.href.indexOf(location.search)) + '?id=' + resJSON.href.substring(7);
//					var newURLWin = new Ext.Window({
//						title:		ORYX.I18N.Save.savedAs, 
//						bodyStyle:	"background:white;padding:10px", 
//						width:		'auto', 
//						height:		'auto',
//						html:"<div style='font-weight:bold;margin-bottom:10px'>"+ORYX.I18N.Query.createdNewQueryDesc+":</div><span><a href='" + modelURL +"' target='_blank'>" + modelURL + "</a></span>",
//						buttons:[{text:'Ok',handler:function(){newURLWin.destroy()}}]
//					});
//					newURLWin.show();
					
					//open new window
					window.open(modelURL);

					Ext.WindowMgr.get('New_Query_Window').close();
					
					delete this.saving;
					
				}.bind(this);
				
				var failure = function(transport) {
					// raise loading disable event.
	                this.facade.raiseEvent({
	                    type: ORYX.CONFIG.EVENT_LOADING_DISABLE
	                });
	                	
	                Ext.WindowMgr.get('New_Query_Window').close();                	
					
					if(transport.status && transport.status === 401) {
						Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.exception).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
					} else if(transport.status && transport.status === 403) {
						Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.exception).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
					} else if(transport.statusText === "transaction aborted") {
						Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.exception).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
					} else if(transport.statusText === "communication failure") {
						Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.exception).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
					} else {
						Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.exception).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
					}
					
					delete this.saving;
					
				}.bind(this);
				
				new Ajax.Request(prefix+'query/', {
		            method: 'get',
		            asynchronous: true,
					requestHeaders: {
						"Accept":"application/json"
					},
					parameters: {
						id: 'getNoOfQuery',
						processID: modelMeta.name,
						parent: modelMeta.parent
		            },
					encoding: 'UTF-8',
					onSuccess: function(transport){
						queryName = modelMeta.name+".query"+"."+(parseInt(transport.responseText)+1);
				        var params = {
				        		json_xml: json,
				        		svg_xml: svgDOM,
				        		name: queryName,
				        		type: ss.title(),
				        		parent: modelMeta.parent,
				        		description: "",
				        		comment: "",
				        		glossary_xml: glossary,
				        		namespace: modelMeta.namespace,
				        		views: Ext.util.JSON.encode(modelMeta.views || [])
				        };
				        
				        //create a query as a new process + insert query desc table
						sendSaveRequest('POST', reqURI, params, successFn, failure);
					},
					onException: function(){
						Ext.WindowMgr.get('New_Query_Window').close();  
						Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getFileException).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
					}.bind(this),
					onFailure: (function(transport) {
						Ext.WindowMgr.get('New_Query_Window').close();  
						Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getFileException).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
					}).bind(this),
					on401: (function(transport) {
						Ext.WindowMgr.get('New_Query_Window').close();  
						Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getFileException).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
					}).bind(this),
					on403: (function(transport) {
						Ext.WindowMgr.get('New_Query_Window').close();  
						Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getFileException).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
					}).bind(this)
				});
				
			}.bind(this);
			
			win.show();
			
			//Check selected obj, in case one task is selected then put task's name into form
			var selectedObjs = this.facade.getSelection();
			if(selectedObjs.length==1){
				var obj = selectedObjs[0].toJSON();
				if(obj.stencil.id == 'Task' || obj.stencil.id == 'CollapsedSubprocess'){
					var taskCmp = Ext.getCmp('task');
					taskCmp.setValue(obj.properties.name.strip());
					taskCmp.fireEvent('select');
				}
			}
		}
	},
	
	sendSaveRequest: function(method, url, params, success, failure){
		
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
     * Check file name format whether it follow query process format
     * ex/ name.query.queryNo
     * */
    isQueryProcess: function(processId){
    	if(processId!=""){
    		var nameFragments = processId.split(".");
        	if(nameFragments.length>1 && nameFragments[1] == 'query'){
        		return true;
        	}else{
        		return false;
        	}
    	}else{
    		return false;
    	}
    	
    }
});