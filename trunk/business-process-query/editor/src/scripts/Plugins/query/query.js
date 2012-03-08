/** Copyright (c) 2011
 * new plug in for querying similar activities based on only current design
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
			description		: ORYX.I18N.Query.queryAltDesc,
			icon			: ORYX.PATH + "images/query.png",
			keyCodes: [{
					metaKeys: [ORYX.CONFIG.META_KEY_META_CTRL,ORYX.CONFIG.META_KEY_ALT],
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
			index			: 3
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
			
			// Get the stencilset
			var ss = this.facade.getStencilSets().values()[0]
			
			var typeTitle = ss.title();
			
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
//				            		metaData.attr = 'ext:qtip="' + value + '"';
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
							'<input type="hidden" id="input_query_task" value="{taskName}"/>',
							'<input type="hidden" id="input_query_process" value="{processID}"/>',
							'<input type="hidden" id="input_query_zone" value="{zone}"/>',
							'<input type="hidden" id="input_query_method" value="{method}"/>',
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
   								//input panel
				   				var defaultDataInput = {taskName:Ext.get('input_query_task').getValue(),
				   							processID:Ext.get('input_query_process').getValue(),
					   						zone:Ext.get('input_query_zone').getValue(),
					   						method:Ext.get('input_query_method').getValue()
				   						};
				   				//get input SVG
				   				Ext.Ajax.request({
				   		   			url				: prefix+'query/',
				   		   			method			: "GET",
				   		   			timeout			: 1800000,
				   		   			disableCaching	: true,
				   		   			headers			: {'Accept':"application/json", 'Content-Type':'charset=UTF-8'},
				   		   			params			: {
				   										id: 'getSVG',
				   										task: defaultDataInput.taskName.strip(),
				   										processID: defaultDataInput.processID.strip(),
				   										parent: modelMeta.parent
				   						              },
				   		   			success			: function(transport) {
					   		   			Ext.WindowMgr.get('Query_Result_Window').body.unmask();
						   				Ext.WindowMgr.get('Query_Result_Window').hide();
						   				
						   				//input panel
						   				// Create a Template
						   				var dialogInput = new Ext.XTemplate(	
											'<div style="width: 900px; height: 250px; overflow:scroll;" onmouseover="this.childNodes[1].style.display=\'inline\';" onmouseout="this.childNodes[1].style.display=\'none\';">',
												'<p id="input_details_box2" ',
												' style="display:none; border-style:solid; border-width:1px; padding:5px; margin:2px; position:absolute; top:3px; left:10px; border-color: #C3C3C3; background-color: #F0F0F0; color:#383838;" ',
												' onmouseover="this.childNodes[0].style.display=\'inline\';" onmouseout="this.childNodes[0].style.display=\'none\';">',
													'<span style="float:right; display:none;"><img src="../explorer/src/img/famfamfam/delete_icon.png" onmouseover="this.style.cursor=\'pointer\';" onclick="this.parentNode.parentNode.style.display=\'none\';"/></span>',
													'<b>Query process</b><br/>',	
													'Task : {taskName} <br/>',
													'Process : {processID} <br/>',
													'Zone : {zone} <br/>',
													'Method : {method} <br/>',
												'</p>',
												'<span style="display:none; position:absolute; top:6px; right:20px;">',
													'<img src="../explorer/src/img/famfamfam/zoom_in.png" onmouseover="this.style.cursor=\'pointer\';" ',
												 		' onclick= "',
												 		'this.nextSibling.nextSibling.value = parseFloat(this.nextSibling.nextSibling.value) + 0.1;',
														'var size = this.nextSibling.nextSibling.value;',
														'var sizeTxt = \'scale(\'+size+\')\';',
														'this.parentNode.nextSibling.firstChild.childNodes[3].setAttribute(\'transform\',sizeTxt);',
														'"',
													'/>',
													'<img src="../explorer/src/img/famfamfam/zoom_out.png" onmouseover="this.style.cursor=\'pointer\';" ',
												 		' onclick= "',
												 		'this.nextSibling.value = parseFloat(this.nextSibling.value) - 0.1;',
														'var size = this.nextSibling.value;',
														'var sizeTxt = \'scale(\'+size+\')\';',
														'this.parentNode.nextSibling.firstChild.childNodes[3].setAttribute(\'transform\',sizeTxt);',
													'"',
													'/>',
													'<input type="hidden" value="1.0"/>',
												'</span>',
												'<div id="svg_input_box" style="display:none; text-align: center">' + transport.responseText + '</div>',
											'</div>'
						   				)
						   				
						   				//output panel 
						   				var defaultDataOutput = {processID:record.get('comparedProcessID').strip(), taskName:record.get('comparedTask').strip(), simValue:record.get('matchingValue') }
						   				// Create a Template
						   				var dialogOutput = new Ext.XTemplate(	
					   						'<div style="width: 900px; height: 250px; overflow:scroll;" onmouseover="this.childNodes[1].style.display=\'inline\';" onmouseout="this.childNodes[1].style.display=\'none\';">',
						   						'<p id="details_box2" ',
													' style="display:none; border-style:solid; border-width:1px; padding:5px; margin:2px; position:absolute; top:3px; left:10px; border-color: #C3C3C3; background-color: #F0F0F0; color:#383838;" ',
													' onmouseover="this.childNodes[0].style.display=\'inline\';" onmouseout="this.childNodes[0].style.display=\'none\';">',
														'<span style="float:right; display:none;"><img src="../explorer/src/img/famfamfam/delete_icon.png" onmouseover="this.style.cursor=\'pointer\';" onclick="this.parentNode.parentNode.style.display=\'none\';"/></span>',
						   							'<b>Similar process</b><br/>',
						   							'Task : {taskName} <br/>',
						   							'Process : {processID} <br/>',
						   							'Sim. value : {simValue} <br/>',
						   						'</p>',
						   						'<span style="display:none; position:absolute; top:6px; right:20px;">',
													'<img src="../explorer/src/img/famfamfam/zoom_in.png" onmouseover="this.style.cursor=\'pointer\';" ',
												 		' onclick= "',
												 		'this.nextSibling.nextSibling.value = parseFloat(this.nextSibling.nextSibling.value) + 0.1;',
														'var size = this.nextSibling.nextSibling.value;',
														'var sizeTxt = \'scale(\'+size+\')\';',
														'this.parentNode.nextSibling.firstChild.childNodes[3].setAttribute(\'transform\',sizeTxt);',
														'"',
													'/>',
													'<img src="../explorer/src/img/famfamfam/zoom_out.png" onmouseover="this.style.cursor=\'pointer\';" ',
												 		' onclick= "',
												 		'this.nextSibling.value = parseFloat(this.nextSibling.value) - 0.1;',
														'var size = this.nextSibling.value;',
														'var sizeTxt = \'scale(\'+size+\')\';',
														'this.parentNode.nextSibling.firstChild.childNodes[3].setAttribute(\'transform\',sizeTxt);',
													'"',
													'/>',
													'<input type="hidden" value="1.0"/>',
												'</span>',
						   						'<div id="svg_box" style="display:none; text-align: center">' + SVGTag + '</div>',
					   						'</div>'
						   				)
						   				var panel = new Ext.Panel({
						   					type: 'vbox',
						   					align : 'stretch',
										    width: 'auto',
										    height: 'auto',
										    items: [{
										        margins: '5 0 0 0',
										        html: dialogInput.apply(defaultDataInput)
										    },{
										        margins: '5 0 0 0',
										        html: dialogOutput.apply(defaultDataOutput)
										    }]
										});
						   				// Create new window and SVG tag into it
						   				var winSVG = new Ext.Window({
						   			        id		: 'svg_Window',
						   			        width	: 'auto',
						   			        height	: 'auto',
						   				    title	: ORYX.I18N.Query.queryResultsSVGDesc,
//						   				    maximizable: true,
						   			        modal	: true,
						   			        resizable	: false,
						   					bodyStyle: 'background:#FFFFFF',
						   					frame: true,
						   					items: [panel],
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
													   			 	   			Ext.getCmp('recommendation_tab_panel').setActiveTab(1);
													   			        		var previousSVG = Ext.getCmp('svg_recommendation_panel');
													   			        		if(previousSVG){
													   			        			Ext.getCmp('recommendation_output_panel').remove(previousSVG);
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
						   				Ext.get("svg_input_box").fadeIn({ endOpacity: 1, duration: 1});
						   				Ext.get("input_details_box2").slideIn('l', { duration: 1 });
						   				Ext.get("svg_box").fadeIn({ endOpacity: 1, duration: 1});
						   				Ext.get("details_box2").slideIn('l', { duration: 1 });
				   		   			},failure			: function(transport) {
		   								Ext.WindowMgr.get('Query_Result_Window').body.unmask();
		   								Ext.WindowMgr.get('Query_Result_Window').hide();
		   								Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getSVGFailure+' "'+record.get('comparedProcessID').strip()+'"').setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
		   								Ext.WindowMgr.get('Query_Result_Window').show();
									  }
				   					});
							  },
   			failure			: function(transport) {
   								Ext.WindowMgr.get('Query_Result_Window').body.unmask();
   								Ext.WindowMgr.get('Query_Result_Window').hide();
   								Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.getSVGFailure+' "'+record.get('comparedProcessID').strip()+'"').setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
   								Ext.WindowMgr.get('Query_Result_Window').show();
							  }
   		});
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
	   							Ext.getCmp('recommendation_tab_panel').setActiveTab(1);
	   			        		var previousSVG = Ext.getCmp('svg_recommendation_panel');
	   			        		if(previousSVG){
	   			        			Ext.getCmp('recommendation_tab_panel').getItem('recommendation_output_panel').remove(previousSVG);
	   			        		}
	   			        		var dataDefault = {task: task, processID: processID};
	   			        		var dialogIn = new Ext.XTemplate(	
	   			        				'<div onmouseover="this.childNodes[1].style.display=\'inline\';" onmouseout="this.childNodes[1].style.display=\'none\';">',
			   			        			'<div>',
			   			        				'<input type="hidden" id="selectedSVGCmp" value=""/>',
			   			        				'<input type="hidden" id="tagetTaskInSvgRecommendationPanel" value="{task}"/>',
			   			        				'<input type="hidden" id="tagetProcessIdInSvgRecommendationPanel" value="{processID}"/>',
			   			        			'</div>',
			   			        			'<span style="display:none; position:absolute; top:6px; right:20px;">',
												'<img src="../explorer/src/img/famfamfam/zoom_in.png" onmouseover="this.style.cursor=\'pointer\';" ',
											 		' onclick= "',
											 		'this.nextSibling.nextSibling.value = parseFloat(this.nextSibling.nextSibling.value) + 0.1;',
													'var size = this.nextSibling.nextSibling.value;',
													'var sizeTxt = \'scale(\'+size+\')\';',
													'this.parentNode.nextSibling.firstChild.childNodes[3].setAttribute(\'transform\',sizeTxt);',
													'"',
												'/>',
												'<img src="../explorer/src/img/famfamfam/zoom_out.png" onmouseover="this.style.cursor=\'pointer\';" ',
											 		' onclick= "',
											 		'this.nextSibling.value = parseFloat(this.nextSibling.value) - 0.1;',
													'var size = this.nextSibling.value;',
													'var sizeTxt = \'scale(\'+size+\')\';',
													'this.parentNode.nextSibling.firstChild.childNodes[3].setAttribute(\'transform\',sizeTxt);',
												'"',
												'/>',
												'<input type="hidden" value="1.0"/>',
											'</span>',
							   				'<div id="svg_recommendation_canvas" style="text-align: center; align: center; margin: 0 auto;">' + transport.responseText + '</div>',
						   				'</div>'
				   				)
	   			        		var panel2 = new Ext.Panel({
	   			        			id: 'svg_recommendation_panel',
	   			        			autoScroll: true,
	   			        			border	:false,
	   			        			html: dialogIn.apply(dataDefault),
	   			        			bodyStyle:    'background-color:#FFFFFE'
	   			        		});
	   			        		
	   			        		Ext.getCmp('recommendation_tab_panel').setActiveTab(1);
	   			        		Ext.getCmp('recommendation_output_panel').add(panel2);
	   			        		Ext.getCmp('recommendation_output_panel').doLayout();
	   			        		Ext.getCmp('recommendation_panel').expand(true);
	   			        		
	   			},failure			: function(transport) {
	   				Ext.WindowMgr.get('svg_Window').close();
	   				Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.exception+' "'+record.get('comparedProcessID').strip()+'"').setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
			}
   		});
	},
	
	clone: function(obj) {
	    if (null == obj || "object" != typeof obj) return obj;
	    var copy = obj.constructor();
	    for (var attr in obj) {
	        if (obj.hasOwnProperty(attr)) copy[attr] = obj[attr];
	    }
	    return copy;
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
    	
    },
});
