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
						queryDesc: ""
		            },
					encoding: 'UTF-8',
					onSuccess: function(transport){
					},
					onException: function(){
						Ext.WindowMgr.get('Query_Window').close();  
						Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.saveQeuryexception).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
					}.bind(this),
					onFailure: (function(transport) {
						Ext.WindowMgr.get('Query_Window').close();  
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
				var newURLWin = new Ext.Window({
					title:		ORYX.I18N.Save.savedAs, 
					bodyStyle:	"background:white;padding:10px", 
					width:		'auto', 
					height:		'auto',
					html:"<div style='font-weight:bold;margin-bottom:10px'>"+ORYX.I18N.Save.savedDescription+":</div><span><a href='" + modelURL +"' target='_blank'>" + modelURL + "</a></span>",
					buttons:[{text:'Ok',handler:function(){newURLWin.destroy()}}]
				});
				newURLWin.show();
				
				window.open(modelURL);

				Ext.WindowMgr.get('Query_Window').close();
				
				delete this.saving;
				
			}.bind(this);
			
			var failure = function(transport) {
				// raise loading disable event.
                this.facade.raiseEvent({
                    type: ORYX.CONFIG.EVENT_LOADING_DISABLE
                });
                	
                Ext.WindowMgr.get('Query_Window').close();                	
				
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
					Ext.WindowMgr.get('Query_Window').close();  
					Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getFileException).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
				}.bind(this),
				onFailure: (function(transport) {
					Ext.WindowMgr.get('Query_Window').close();  
					Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getFileException).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
				}).bind(this),
				on401: (function(transport) {
					Ext.WindowMgr.get('Query_Window').close();  
					Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getFileException).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
				}).bind(this),
				on403: (function(transport) {
					Ext.WindowMgr.get('Query_Window').close();  
					Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getFileException).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
				}).bind(this)
			});
			
		}.bind(this);
		
		// Create the callback for the template
		callback = function(form){
			//remove results window
			var previousResultsWin = Ext.getCmp('Query_Result_Window');
       		if(previousResultsWin){
       			Ext.getCmp('Query_Result_Window').destroy();
       		}
			
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
				onSuccess: successQuery,
				onException: function(){
					Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getFailure).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
					win.close();
				}.bind(this),
				onFailure: (function(transport) {
					Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getFailure).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
					win.close();
				}).bind(this),
				on401: (function(transport) {
					Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getFailure).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
					win.close();
				}).bind(this),
				on403: (function(transport) {
					Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getFailure).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
					win.close();
				}).bind(this)
			});
			
		}.bind(this);
		
		win.show();
		
		var showProcessImg = this.showProcessImg.bind(this);
		
		var successQuery = function(transport) {
			var resJSON = transport.responseText.evalJSON();
			var rt = Ext.data.Record.create([
			    {name: 'comparedTask'},
			    {name: 'comparedProcessID'},
			    {name: 'matchingValue'}
			])
			var resultStore = new Ext.data.Store ({
				isAutoLoad: true,
			    reader: new Ext.data.JsonReader({
			    	root: 'results',
				    fields: [
				        {name: 'comparedTask', mapping: 'comparedTask'},
				        {name: 'comparedProcessID', mapping: 'comparedProcessID'},
				        {name: 'matchingValue', mapping: 'matchingValue'}
				    ]},rt)
			})
			
			resultStore.loadData(resJSON);
			
			var grid = new Ext.grid.GridPanel({
				id:	'grid_results',
			    store: resultStore,
			    autoScroll: true,
			    colModel: new Ext.grid.ColumnModel({
			    	defaultSortable: true,
			    	defaults: {
			            sortable: true
			        },
			        columns: [
			            {id: 'similarTask', width: 10, header: 'Similar tasks', dataIndex: 'comparedTask', type:'string', 
			            	renderer: function(value, metaData, record, rowIndex, colIndex, store, view) {
//			            		metaData.attr = 'ext:qtip="' + value + '"';
			            		return value;
			            	}
			            },
			            {id: 'processID', width: 10, header: 'ProcessID', dataIndex: 'comparedProcessID', type:'string'},
			            {id: 'similarValue', width: 10, header: 'Similarity value', dataIndex: 'matchingValue', type:'float'},
		            ]
			    }),
			    viewConfig: {
			        forceFit: true,
			    },
			    sm: new Ext.grid.RowSelectionModel({
	                singleSelect: true,
	                listeners: {
	                     rowselect: showProcessImg
	               }
	            }),
			    width: 800,
			    height: 400,
			    frame: true,
			    layout: 'fit',
			    iconCls: 'icon-grid'
			});
			
			var defaultData1 = {processID:resJSON.processID, taskName:resJSON.task, zone:resJSON.zone, method:resJSON.method}
				// Create a Template
			var dialog1 = new Ext.XTemplate(		
					'<div style="height: 370px; width:250; background-color: #F0F0F0;">',
						'<p id="details_box1" style="display:none; padding:5px; margin:2px; color:#383838;">',
							'Task : {taskName} <br/>',
							'Process : {processID} <br/>',
							'Zone : {zone} <br/>',
							'Method : {method} <br/>',
						'</p>',
					'</div>'
			)
			var panel = new Ext.Panel({
			    layout:'border',
			    defaults: {
			        collapsible: true,
			        split: true
			    },
			    width: 800,
			    height: 400,
			    items: [{
			        title: ORYX.I18N.Query.queryDetailsDesc,
			        collapseTitle : ORYX.I18N.Query.queryDetailsDesc,
			        region:'west',
			        margins: '5 0 0 0',
			        cmargins: '5 5 0 0',
			        width: 175,
			        minSize: 100,
			        maxSize: 250,
			        html: dialog1.apply(defaultData1)
			    },{
			    	collapsible: false,
			        region:'center',
			        margins: '5 0 0 0',
			        items: [grid]
			    }]
			});
			
			// Create new window and attach grid results into it
			var winResults = new Ext.Window({
				id		: 'Query_Result_Window',
		        width	: 'auto',
		        height	: 'auto',
			    title	: ORYX.I18N.Query.queryResultsDesc,
		        modal	: true,
		        resizable	: false,
				bodyStyle: 'background:#FFFFFF',
		        items  : [panel],
	            defaultButton: 0,
		        buttons:[{
	            	text: ORYX.I18N.Save.close,
	            	handler: function(){
	            		winResults.close();
	            	}.bind(this)
				}],
				listeners: {
					close: function(){					
						winResults.destroy();
						delete this.saving;
					}.bind(this)
				}
		    });
			win.close();
			winResults.show();
			Ext.get("grid_results").fadeIn({ endOpacity: 1, duration: 1});
			Ext.get("details_box1").fadeIn({ endOpacity: 1, duration: 1});
		}.bind(this);
	},
	
	/**
	 *	Correct partial SVG  
	 */
	verifyPartialSVG: function(jsonProcess){
   		for(var i=0; i<jsonProcess.childShapes.length; i++){
   			for(var j=0; j<jsonProcess.childShapes[i].outgoing.length; j++){
				var resourceId = jsonProcess.childShapes[i].outgoing[j].resourceId;
				//check that is there any reference for this resourceId
				var exist = false;
				for(var k=0; k<jsonProcess.childShapes.length; k++){
					if(jsonProcess.childShapes[k].resourceId == resourceId){
						exist = true;
						break;
					}
				}
				if(!exist){
					jsonProcess.childShapes[i].outgoing.splice(j,1);
				}
			}
   			if(jsonProcess.childShapes[i].stencil.id == 'SequenceFlow'){
   				//	for sequence, we have to check target as well
   				var targetId = jsonProcess.childShapes[i].target.resourceId;
   				//check that is there any referrence for this target resourceId
				var exist = false;
				for(var k=0; k<jsonProcess.childShapes.length; k++){
					if(jsonProcess.childShapes[k].resourceId == targetId){
						exist = true;
						break;
					}
				}
				if(!exist){
					jsonProcess.childShapes[i].target = null;
				}
   			}
    	}
	},
	
	//View SVG process when it is selected from similar process in grid
	showProcessImg: function(smObj, rowIndex, record) {
		//bind global object to be local one
		var modelMeta = this.facade.getModelMetaData();
		var reqURI = modelMeta.modelHandler;
		var reqURIs = reqURI.split("/");
		var prefix = "/";
	    for(i=1; i<reqURIs.length-1; i++){
		    prefix+=reqURIs[i]+"/";
	    }
		var verifyPartialSVG = this.verifyPartialSVG.bind(this);
		var createInteractiveSVG = this.createInteractiveSVG.bind(this);
		var facadeObj = this.facade;
		
		Ext.WindowMgr.get('Query_Result_Window').body.mask(ORYX.I18N.Query.pleaseWait, "x-waiting-box");
		
		//get SVG from Signavio file
		Ext.Ajax.request({
   			url				: prefix+'query/',
   			method			: "GET",
   			timeout			: 1800000,
   			disableCaching	: true,
   			headers			: {'Accept':"application/json", 'Content-Type':'charset=UTF-8'},
   			params			: {
								id: 'getSVG',
								task: record.get('comparedTask').strip(),
								processID: record.get('comparedProcessID').strip(),
								parent: modelMeta.parent
				              },
   			success			: function(transport) {
   								var SVGTag = transport.responseText;
   								Ext.WindowMgr.get('Query_Result_Window').body.unmask();
				   				Ext.WindowMgr.get('Query_Result_Window').hide();
				   				var defaultData = {processID:record.get('comparedProcessID').strip(), taskName:record.get('comparedTask').strip(), simValue:record.get('matchingValue') }
				   				// Create a Template
				   				var dialog = new Ext.XTemplate(	
				   						'<div style="width: 900px; height: 400px; overflow:scroll;">',
					   						'<p id="details_box2" style="display:none; border-style:solid; border-width:1px; padding:5px; margin:2px; position:absolute; top:3px; left:10px; border-color: #C3C3C3; background-color: #F0F0F0; color:#383838;">',
					   							'Task : {taskName} <br/>',
					   							'Process : {processID} <br/>',
					   							'Sim. value : {simValue} <br/>',
					   						'</p>',
					   						'<div id="svg_box" style="display:none;">' + SVGTag + '</div>',
				   						'</div>'
				   				)
				   				
				   				// Create new window and SVG tag into it
				   				var winSVG = new Ext.Window({
				   			        id		: 'svg_Window',
				   			        width	: 'auto',
				   			        height	: 'auto',
				   				    title	: ORYX.I18N.Query.queryResultsSVGDesc,
//				   				    maximizable: true,
				   			        modal	: true,
				   			        resizable	: false,
				   					bodyStyle: 'background:#FFFFFF',
				   					html: dialog.apply(defaultData),
				   					frame: true,
				   		            defaultButton: 0,
				   						buttons:[{
					   			        	text: ORYX.I18N.Query.copyAllBtn,
					   			        	handler: function(){
					   			        		winSVG.body.mask(ORYX.I18N.Query.pleaseWait, "x-waiting-box");
					   			        		Ext.Ajax.request({
						   			 	   			url				: prefix+'query/',
						   			 	   			method			: "GET",
						   			 	   			timeout			: 1800000,
						   			 	   			disableCaching	: true,
						   			 	   			headers			: {'Accept':"application/json", 'Content-Type':'charset=UTF-8'},
						   			 	   			params			: {
						   			 									id: 'getJSON',
						   			 									task: record.get('comparedTask').strip(),
						   			 									processID: record.get('comparedProcessID').strip(),
						   			 									parent: modelMeta.parent
						   			 					              },
						   			 	   			success			: function(transport) {
											   			 	   			winSVG.close();
											   			        		
											   			        		//remove old recommended process
											   			        		var previousSVG = Ext.getCmp('svg_recommendation_panel');
											   			        		if(previousSVG){
											   			        			Ext.getCmp('recommendation_panel').remove(previousSVG);
											   			        		}
											   			        		
											   			        		Ext.getCmp('recommendation_panel').collapse(true);
											   			        		
											   			        		facadeObj.importJSON(transport.responseText.evalJSON());
						   			 	   			},failure			: function(transport) {
						   			 	   				winSVG.close();
						   			 	   				Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.exception+' "'+record.get('comparedProcessID').strip()+'"').setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
													}
					   			        		})
					   			        	}.bind(this)
					   					},{
					   			        	text: ORYX.I18N.Query.selectBtn,
					   			        	handler: function(){
					   			        		createInteractiveSVG(record.get('comparedTask').strip(), record.get('comparedProcessID').strip());
					   			        	}.bind(this)
					   					},{
					   						text: ORYX.I18N.Query.backBtn,
					   						handler: function(){
					   							winSVG.close();
					   							Ext.WindowMgr.get('Query_Result_Window').show();
					   							Ext.getCmp("grid_results").getSelectionModel().deselectRow(rowIndex);
					   							Ext.get("grid_results").fadeIn({ endOpacity: 1, duration: 1});
					   							Ext.get("details_box1").fadeIn({ endOpacity: 1, duration: 1});
					   						},
					   						listeners:{
					   							render:function(){
					   								this.focus();
					   							}
					   						}
					   					},{
					   			        	text: ORYX.I18N.Save.close,
					   			        	handler: function(){
					   			        		winSVG.close();
					   			        	}.bind(this)
					   					}],
				   					listeners: {
				   						close: function(){					
				   							winSVG.destroy();
				   							delete this.saving;
				   						}.bind(this)
				   					}  
				   			    });
				   				winSVG.show();
				   				Ext.get("svg_box").fadeIn({ endOpacity: 1, duration: 1});
				   				Ext.get("details_box2").slideIn('l', { duration: 1 });
							  },
   			failure			: function(transport) {
   								Ext.WindowMgr.get('Query_Result_Window').body.unmask();
   								Ext.WindowMgr.get('Query_Result_Window').hide();
   								Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getSVGFailure+' "'+record.get('comparedProcessID').strip()+'"').setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
   								Ext.WindowMgr.get('Query_Result_Window').show();
							  }
   		})
	},
	
	//create interactive SVG from a ordinary one
	createInteractiveSVG: function(task, processID){
		//bind global object to be local one
		var modelMeta = this.facade.getModelMetaData();
		var reqURI = modelMeta.modelHandler;
		var reqURIs = reqURI.split("/");
		var prefix = "/";
	    for(i=1; i<reqURIs.length-1; i++){
		    prefix+=reqURIs[i]+"/";
	    }
		var verifyPartialSVG = this.verifyPartialSVG.bind(this);
		var extractSelectedSVGFromJSON = this.extractSelectedSVGFromJSON.bind(this);
		var clone = this.clone.bind(this);
		var facadeObj = this.facade;
		
		Ext.WindowMgr.get('svg_Window').body.mask(ORYX.I18N.Query.pleaseWait, "x-waiting-box");
		Ext.Ajax.request({
	   			url				: prefix+'query/',
	   			method			: "GET",
	   			timeout			: 1800000,
	   			disableCaching	: true,
	   			headers			: {'Accept':"application/json", 'Content-Type':'charset=UTF-8'},
	   			params			: {
									id: 'getInteractiveSVG',
									task: task,
									processID: processID,
									parent: modelMeta.parent
					              },
	   			success			: function(transport) {
	   							Ext.WindowMgr.get('svg_Window').close();
	   			        		
	   			        		//remove old recommended process
	   			        		var previousSVG = Ext.getCmp('svg_recommendation_panel');
	   			        		if(previousSVG){
	   			        			Ext.getCmp('recommendation_panel').remove(previousSVG);
	   			        		}
	   			        		
	   			        		var dialogIn = new Ext.XTemplate(	
	   			        			'<div>',
	   			        				'<input type="hidden" id="selectedSVGCmp" value=""/>',
	   			        			'</div>',
					   				'<div id="svg_recommendation_canvas" style="text-align: center; align: center; margin: 0 auto;">' + transport.responseText + '</div>'
				   				)
	   			        		var panel2 = new Ext.Panel({
	   			        			id: 'svg_recommendation_panel',
	   			        			autoScroll: true,
	   			        			html: dialogIn.apply(),
	   			        			bodyStyle:    'background-color:#FFFFFE',
	   			        			defaultButton: 0,
			   						buttons:[{
				   							text: ORYX.I18N.Query.copyAllBtn,
					   			        	handler: function(){
					   			        		Ext.getBody().mask(ORYX.I18N.Query.pleaseWait, "x-waiting-box");
					   			        		//remove old recommended process
					   			        		var previousSVG = Ext.getCmp('svg_recommendation_panel');
					   			        		if(previousSVG){
					   			        			Ext.getCmp('recommendation_panel').remove(previousSVG);
					   			        		}
					   			        		Ext.getCmp('recommendation_panel').collapse(true);
								        		Ext.Ajax.request({
									 	   			url				: prefix+'query/',
									 	   			method			: "GET",
									 	   			timeout			: 1800000,
									 	   			disableCaching	: true,
									 	   			headers			: {'Accept':"application/json", 'Content-Type':'charset=UTF-8'},
									 	   			params			: {
									 									id: 'getJSON',
									 									task: task,
									 									processID: processID,
									 									parent: modelMeta.parent
									 					              },
									 	   			success			: function(transport) {
									 	   							facadeObj.importJSON(transport.responseText.evalJSON());
									 	   							Ext.getBody().unmask();
									 	   			},failure			: function(transport) {
									 	   				Ext.getBody().unmask();
									 	   				Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.exception+' "'+record.get('comparedProcessID').strip()+'"').setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
									 	   			}
								        		})
					   			        	}.bind(this)
			   						},{
			   							text: ORYX.I18N.Query.copyCmpBtn,
				   			        	handler: function(){
				   			        		Ext.getBody().mask(ORYX.I18N.Query.pleaseWait, "x-waiting-box");
				   			        		Ext.Ajax.request({
								 	   			url				: prefix+'query/',
								 	   			method			: "GET",
								 	   			timeout			: 1800000,
								 	   			disableCaching	: true,
								 	   			headers			: {'Accept':"application/json", 'Content-Type':'charset=UTF-8'},
								 	   			params			: {
								 									id: 'getJSON',
								 									task: task,
								 									processID: processID,
								 									parent: modelMeta.parent
								 					              },
								 	   			success			: function(transport) {
								 	   							var selectedIDs = Ext.get('selectedSVGCmp').getValue().split(',');
								 	   							var processJson = transport.responseText.evalJSON();
									 	   						extractSelectedSVGFromJSON(processJson,selectedIDs);
									   			        		verifyPartialSVG(processJson);
									   			        		facadeObj.importJSON(processJson);
									   			        		Ext.getCmp('recommendation_panel').collapse(true);
								 	   							Ext.getBody().unmask();
								 	   			},failure			: function(transport) {
								 	   				Ext.getBody().unmask();
								 	   				Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.exception+' "'+record.get('comparedProcessID').strip()+'"').setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
								 	   			}
							        		})
				   			        	}.bind(this)
			   						}]
	   			        		});
	   			        		
	   			        		Ext.getCmp('recommendation_panel').collapse(false);
	   			        		Ext.getCmp('recommendation_panel').add(panel2);
	   			        		Ext.getCmp('recommendation_panel').doLayout();
	   			        		Ext.getCmp('recommendation_panel').expand(true);
	   			},failure			: function(transport) {
	   				Ext.WindowMgr.get('svg_Window').close();
	   				Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.exception+' "'+record.get('comparedProcessID').strip()+'"').setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
			}
   		})
	},
	
	extractSelectedSVGFromJSON: function(jsonProcess, selectedIDs){
		var noOfChild = 0;
		noOfChild = jsonProcess.childShapes.length;
		var deleteIds = [];
		//find unselected components
   		for(var i=0; i<noOfChild; i++){
   			var exist = false;
   			//loop only length-1 bz, the last cmp is empty
       		for(var j=0; j<selectedIDs.length-1; j++){
       			if(selectedIDs[j].substring(4,selectedIDs[j].length) == jsonProcess.childShapes[i].resourceId){
       				exist = true;
       				break;
       			}
       		}
       		if(!exist){
       			deleteIds.push(jsonProcess.childShapes[i].resourceId);
       		}
   		}
   		//remove unselected components
   		for(var i=0; i<deleteIds.length; i++){
   			for(var j=0; j<jsonProcess.childShapes.length; j++){
   				if(jsonProcess.childShapes[j].resourceId == deleteIds[i]){
   					jsonProcess.childShapes.splice(j,1);
   					break;
   				}
   			}
   		}
	},
	
	clone: function(obj) {
	    if (null == obj || "object" != typeof obj) return obj;
	    var copy = obj.constructor();
	    for (var attr in obj) {
	        if (obj.hasOwnProperty(attr)) copy[attr] = obj[attr];
	    }
	    return copy;
	}
});
