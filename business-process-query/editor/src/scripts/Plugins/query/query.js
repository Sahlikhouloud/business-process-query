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
			
//			// Create a Template
//			var dialog = new Ext.XTemplate(		
//					'<div>',
//						'<svg xmlns:oryx="http://www.b3mn.org/oryx" xmlns:svg="http://www.w3.org/2000/svg" xmlns="http://www.w3.org/2000/svg" version="1.0" width="800" height="450">',
//							//Gateways
//							'<g> ',
//							  '<oryx:magnets>',
//							    '<oryx:magnet ',
//							       'oryx:default="yes" ',
//							       'oryx:cy="16" ',
//							       'oryx:cx="16" /> ',
//							  '</oryx:magnets>',
//							  //Parallel  
//							  '<g>',
//							    '<defs>',
//									'<radialGradient id="background" cx="10%" cy="10%" r="100%" fx="10%" fy="10%"> ',
//										'<stop offset="0%" stop-color="#ffffff" stop-opacity="1"/> ',
//										'<stop id="fill_el" offset="100%" stop-color="#ffffff" stop-opacity="1"/> ',
//									'</radialGradient> ',
//								'</defs> ',
//							    '<path ',
//							       'd="M -4.5,16 L 16,-4.5 L 35.5,16 L 16,35.5z" ',
//							       'id="bg_frame" ',
//							       'fill="url(#background) white" ',
//							       'stroke="black" ',
//							       'style="stroke-width:1" /> ',
//							    '<path ',
//							       'd="M 6.75,16 L 25.75,16 M 16,6.75 L 16,25.75" ',
//							       'id="path9" ',
//							       'stroke="black" ',
//							       'style="fill:none;stroke-width:3" /> ',
//								'<text id="text_name" x="26" y="26" oryx:align="left top"/> ',
//							  '</g> ',
//							  
//							  //Inclusive
//							  '<g>',
//							    '<defs>',
//									'<radialGradient id="background" cx="10%" cy="10%" r="100%" fx="10%" fy="10%">',
//										'<stop offset="0%" stop-color="#ffffff" stop-opacity="1"/>',
//										'<stop id="fill_el" offset="100%" stop-color="#ffffff" stop-opacity="1"/>',
//									'</radialGradient>',
//								'</defs> ' ,
//							    '<path ',
//							       'd="M -4.5,16 L 16,-4.5 L 35.5,16 L 16,35.5z" ',
//							       'id="bg_frame" ',
//							       'fill="url(#background) white" ',
//							       'stroke="black" ',
//							       'style="stroke-width:1" /> ',
//							    '<circle ',
//							    	'id="circle" ',
//							    	'stroke="black" ',
//									'cx="16" ',
//									'cy="16" ',
//									'r="9.75" ',
//									'style="fill:none;stroke-width:2.5" />',
//								'<text id="text_name" x="26" y="26" oryx:align="left top"/>',
//							  '</g> ',
//							  //Exclusive
//							  '<g> ',
//							    '<defs> ',
//									'<radialGradient id="background" cx="10%" cy="10%" r="100%" fx="10%" fy="10%"> ',
//										'<stop offset="0%" stop-color="#ffffff" stop-opacity="1"/> ',
//										'<stop id="fill_el" offset="100%" stop-color="#ffffff" stop-opacity="1"/> ',
//									'</radialGradient> ',
//								'</defs> ',
//							    '<path ',
//							       'd="M -4.5,16 L 16,-4.5 L 35.5,16 L 16,35.5z" ',
//							       'id="bg_frame" ',
//							       'fill="url(#background) white" ',
//							       'stroke="black" ',
//							       'style="stroke-width:1" /> ',
//							    '<g id="cross"> ',
//							      '<path ',
//							      	'id="crosspath" ',
//							      	'stroke="black" ',
//							      	'fill="black" ',
//							        'd="M 8.75,7.55 L 12.75,7.55 L 23.15,24.45 L 19.25,24.45 z" ',
//							        'style="stroke-width:1" /> ',
//							      '<path ',
//							      	'id="crosspath2" ',
//							      	'stroke="black" ',
//							      	'fill="black" ',
//							        'd="M 8.75,24.45 L 19.25,7.55 L 23.15,7.55 L 12.75,24.45 z" ',
//							        'style="stroke-width:1" /> ',
//							    '</g> ',
//								'<text id="text_name" x="26" y="26" oryx:align="left top"/> ',
//							  '</g> ',
//							'</g> ', // end gateways
//							
//							//Tasks
//							'<g>',
//							  '<oryx:magnets>',
//							  	'<oryx:magnet oryx:cx="1" oryx:cy="20" oryx:anchors="left" />',
//							  	'<oryx:magnet oryx:cx="1" oryx:cy="40" oryx:anchors="left" />',
//							  	'<oryx:magnet oryx:cx="1" oryx:cy="60" oryx:anchors="left" />',
//							  	'<oryx:magnet oryx:cx="25" oryx:cy="79" oryx:anchors="bottom" />',
//							  	'<oryx:magnet oryx:cx="50" oryx:cy="79" oryx:anchors="bottom" />',
//							  	'<oryx:magnet oryx:cx="75" oryx:cy="79" oryx:anchors="bottom" />',
//							  	'<oryx:magnet oryx:cx="99" oryx:cy="20" oryx:anchors="right" />',
//							  	'<oryx:magnet oryx:cx="99" oryx:cy="40" oryx:anchors="right" />',
//							  	'<oryx:magnet oryx:cx="99" oryx:cy="60" oryx:anchors="right" />',
//							  	'<oryx:magnet oryx:cx="25" oryx:cy="1" oryx:anchors="top" />',
//							  	'<oryx:magnet oryx:cx="50" oryx:cy="1" oryx:anchors="top" />',
//							  	'<oryx:magnet oryx:cx="75" oryx:cy="1" oryx:anchors="top" />',
//							  	'<oryx:magnet oryx:cx="50" oryx:cy="40" oryx:default="yes" />',
//							  '</oryx:magnets>',
//							  // normal task
//							  '<g pointer-events="fill" oryx:minimumSize="50 40">',
//							  	'<defs>',
//									'<radialGradient id="background" cx="10%" cy="10%" r="100%" fx="10%" fy="10%">',
//										'<stop offset="0%" stop-color="#ffffff" stop-opacity="1"/>',
//										'<stop id="fill_el" offset="100%" stop-color="#ffffcc" stop-opacity="1"/>',
//									'</radialGradient>',
//								'</defs>',
//								'<rect id="text_frame" oryx:anchors="bottom top right left" x="1" y="1" width="94" height="79" rx="10" ry="10" stroke="none" stroke-width="0" fill="none" />',
//							    '<rect id="callActivity" oryx:resize="vertical horizontal" oryx:anchors="bottom top right left" x="0" y="0" width="100" height="80" rx="10" ry="10" stroke="black" stroke-width="4" fill="none" />',
//								'<rect id="bg_frame" oryx:resize="vertical horizontal" x="0" y="0" width="100" height="80" rx="10" ry="10" stroke="black" stroke-width="1" fill="url(#background) #ffffcc" />',
//									'<text ', 
//										'font-size="12" ', 
//										'id="text_name" ', 
//										'x="50" ', 
//										'y="40" ', 
//										'oryx:align="middle center" ',
//										'oryx:fittoelem="text_frame" ',
//										'stroke="black"> ',
//									'</text> ',
//							  '</g>',
//							  
//							  //sub process
//							  '<g pointer-events="fill" oryx:minimumSize="80 60">',
//							  	'<defs>',
//									'<radialGradient id="background" cx="10%" cy="10%" r="100%" fx="10%" fy="10%">',
//										'<stop offset="0%" stop-color="#ffffff" stop-opacity="1"/>',
//										'<stop id="fill_el" offset="100%" stop-color="#ffffcc" stop-opacity="1"/>',
//									'</radialGradient>',
//								'</defs>',
//								'<rect id="text_frame" oryx:anchors="bottom top right left" x="1" y="1" width="94" height="79" rx="10" ry="10" stroke="none" stroke-width="0" fill="none" />',
//								'<rect id="bg_frame" oryx:anchors="bottom top right left" x="0" y="0" width="100" height="80" rx="10" ry="10" stroke="black" stroke-width="1" fill="url(#background) #ffffcc" />',
//							    '<rect id="callActivity" oryx:resize="vertical horizontal" oryx:anchors="bottom top right left" x="0" y="0" width="100" height="80" rx="10" ry="10" stroke="black" stroke-width="4" fill="none" />',
//								'<rect id="border" oryx:anchors="top bottom left right" oryx:resize="vertical horizontal" x="2.5" y="2.5" width="95" height="75" rx="8" ry="8" stroke="black" stroke-width="1" fill="none" />',
//							    	'<text ',
//										'font-size="12" ',
//										'id="text_name" ', 
//										'x="50" ',
//										'y="40" ',
//										'oryx:align="middle center" ',
//										'oryx:fittoelem="text_frame" ',
//										'stroke="black">',
//									'</text>',
//								 	'<g 	id="b" ',
//										'oryx:anchors="bottom" ',
//										'transform="translate(1)">',
//									 	'<rect id="plusborder" oryx:anchors="bottom" x="43" y="66" width="14" height="14" fill="none" stroke="black" stroke-width="1" />',
//										'<path id="plus" oryx:anchors="bottom" fill="none" stroke="black" d="M50 68 v10 M 45 73 h10" stroke-width="1"/>',
//									'</g>',
//							  '</g>',
//							'</g>',//end tasks
//							
//							//Connectors
//							'<g class="edge">',
//								'<defs>',
//								  	'<marker id="start" refX="1" refY="5" markerUnits="userSpaceOnUse" markerWidth="17" markerHeight="11" orient="auto">',
//								  		'<path id="conditional"   d="M 0 6 L 8 1 L 15 5 L 8 9 L 1 5" fill="white" stroke="black" stroke-width="1" />',
//										'<path id="default" d="M 5 0 L 11 10" fill="white" stroke="black" stroke-width="1" />',
//								  	'</marker>',
//								  	'<marker id="end" refX="15" refY="6" markerUnits="userSpaceOnUse" markerWidth="15" markerHeight="12" orient="auto">',
//								  		'<path id="arrowhead" d="M 0 1 L 15 6 L 0 11z" fill="black" stroke="black" stroke-linejoin="round" stroke-width="2" />',
//								  	'</marker>',
//								'</defs>',
//								'<g id="edge">',
//									'<path id="bg_frame" d="M10 50 L210 50" stroke="black" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" marker-start="url(#start)" marker-end="url(#end)" />',
//									'<text id="text_name" x="0" y="0" oryx:edgePosition="startTop"/>',
//								'</g>',
//							'</g>',
//						'</svg>',
//					'</div>'
//			)
//			
//			// Create a new window for results				
//			var winResults = new Ext.Window({
//				id		: 'results_window',
//		        width	: '800',
//		        height	: '450',
//			    title	: ORYX.I18N.Query.queryResultsDesc,
//		        modal	: true,
//		        resize	: true,
//				bodyStyle: 'background:#FFFFFF',
//		        html	: dialog.apply(),
//		        defaultButton: 0,
//		        buttons:[{
//	            	text: ORYX.I18N.Save.close,
//	            	handler: function(){
//	            		winResults.close();
//	            	}.bind(this)
//				}],
//				listeners: {
//					close: function(){					
//						winResults.destroy();
//						delete this.saving;
//					}.bind(this)
//				}
//		    });
//			winResults.show();
			
		}.bind(this);
		
		win.show();
		
		var showProcessImg = function(smObj, rowIndex, record) {
			
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
	   								Ext.WindowMgr.get('Query_Result_Window').body.unmask();
					   				Ext.WindowMgr.get('Query_Result_Window').hide();
					   				var defaultData = {processID:record.get('comparedProcessID').strip(), taskName:record.get('comparedTask').strip(), simValue:record.get('matchingValue') }
					   				// Create a Template
					   				var dialog = new Ext.XTemplate(	
					   						'<div style="width: 900px; height: 400px; overflow:scroll;">',
						   						'<p style="border-style:solid; border-width:1px; padding:5px; margin:2px; position:absolute; top:3px; left:10px; border-color: #C3C3C3; background-color: #F0F0F0; color:#383838;">',
						   							'Task : {taskName} <br/>',
						   							'Process : {processID} <br/>',
						   							'Sim. value : {simValue} <br/>',
						   						'</p>',
						   						'<div>' + transport.responseText + '</div>',
					   						'</div>'
					   				)
					   				
					   				// Create new window and SVG tag into it
					   				var winSVG = new Ext.Window({
					   			        id		: 'svg_Window',
					   			        width	: 'auto',
					   			        height	: 'auto',
					   				    title	: ORYX.I18N.Query.queryResultsSVGDesc,
//					   				    maximizable: true,
					   			        modal	: true,
					   			        resizable	: false,
					   					bodyStyle: 'background:#FFFFFF',
					   					html: dialog.apply(defaultData),
					   					frame: true,
					   		            defaultButton: 0,
					   						buttons:[{
					   						text: ORYX.I18N.Query.backBtn,
					   						handler: function(){
					   							winSVG.close();
					   							Ext.WindowMgr.get('Query_Result_Window').show();
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
								  },
	   			failure			: function(transport) {
	   								Ext.WindowMgr.get('Query_Result_Window').body.unmask();
	   								Ext.WindowMgr.get('Query_Result_Window').hide();
	   								Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getSVGFailure+' "'+record.get('comparedProcessID').strip()+'"').setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
	   								Ext.WindowMgr.get('Query_Result_Window').show();
								  }
	   		})
	   		
		}.bind(this);
		
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
			
			var defaultData1 = {processID:resJSON.task, taskName:resJSON.processID, zone:resJSON.zone, method:resJSON.method}
				// Create a Template
			var dialog1 = new Ext.XTemplate(		
					'<div style="height: 370px; width:250; background-color: #F0F0F0;">',
						'<p style="padding:5px; margin:2px; color:#383838;">',
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
		}.bind(this);
	}
	
});
