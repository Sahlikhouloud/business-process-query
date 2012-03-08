if (!ORYX.Plugins) 
    ORYX.Plugins = new Object();

ORYX.Plugins.SearchActivity = Clazz.extend({
	// Defines the facade
    facade		: undefined,
    
 // Constructor 
    construct: function(facade){
    
        this.facade = facade;
		
		// Offers the functionality of undo                
        this.facade.offer({
			name			: ORYX.I18N.Query.searchActivity,
			description		: ORYX.I18N.Query.searchActivityAltDesc,
			icon			: ORYX.PATH + "images/search_activity.png",
			keyCodes: [{
					metaKeys: [ORYX.CONFIG.META_KEY_META_CTRL,ORYX.CONFIG.META_KEY_ALT],
					keyCode: 69, //e key-code
					keyAction: ORYX.CONFIG.KEY_ACTION_DOWN
				}
		 	],
			functionality	: this.searching.bind(this),
			group			: ORYX.I18N.Query.group,
			isEnabled		: function(){ 
									return true;
							  }.bind(this),
			index			: 1
		}); 

	},
	
	searching: function(){
		//Check selected obj, in case one task is selected then put task's name into form
		var selectedObjs = this.facade.getSelection();
		if(selectedObjs.length==1){
			var selectedObj = selectedObjs[0].toJSON();
			if(selectedObj.stencil.id == 'Task' || selectedObj.stencil.id == 'CollapsedSubprocess'){
				var obj = selectedObjs.first();
				var evt = undefined;
				var modelMeta = this.facade.getModelMetaData();
				var reqURI = modelMeta.modelHandler;
				var reqURIs = reqURI.split("/");
				var prefix = "/";
			    for(i=1; i<reqURIs.length-1; i++){
				    prefix+=reqURIs[i]+"/";
			    }
			    var facadeObj = this.facade;
				Ext.getBody().mask(ORYX.I18N.Query.pleaseWait, "x-waiting-box");
				Ext.Ajax.request({
		   			url				: prefix+'query/',
		   			method			: "GET",
		   			timeout			: 1800000,
		   			disableCaching	: true,
		   			headers			: {'Accept':"application/json", 'Content-Type':'charset=UTF-8'},
		   			params			: {
										id: 'getAllTasks'
						              },
		   			success			: function(transport) {
		   								Ext.getBody().unmask();
		   								var tasks = transport.responseText.evalJSON();
		   								var zoneCmp = Ext.getCmp('zone');
		   								var rt = Ext.data.Record.create([
		   								    {name: 'taskName'},
		   								    {name: 'noOfTime'}
		   								]);
		   								var store = new Ext.data.Store ({
		   									isAutoLoad: true,
		   								    reader: new Ext.data.JsonReader({
		   								    	root: 'tasks',
			   								    fields: [
			   								        {name: 'taskName', mapping: 'taskName'},
			   								        {name: 'noOfTime', mapping: 'noOfTime'}
			   								    ]},rt)
		   								});
		   								store.loadData(tasks);
		   								var cb = new Ext.form.ComboBox({
				   			                    	id: 'searchTasks',
				   			                     	store: store,
				   			                     	allowBlank: true,
				   			                     	hideLabel: true,
				   			                     	hideTrigger:true,
				   			                     	typeAhead: true,
				   			                     	emptyText: 'Search activites ... ',
				   			   					    valueField:'taskName',
				   			 					    displayField:'noOfTime',
				   			 					    anchor: '100%',
				   			 					    mode:'local',
//				   			 					    triggerAction: 'all',
					   			 					listeners:{
					   							       'select': function(combo, selection){
					   							    	   
					   							    	    // copy the name of a task to the selected shape in the canvas
					   							    	    // copy from renameShapes.js 
					   							    	   
					   							    	   
						   							    	// Get all properties which where at least one ref to view is set
						   									var props = obj.getStencil().properties().findAll(function(item){ 
						   										return (item.refToView() 
						   												&&  item.refToView().length > 0
						   												&&	item.directlyEditable()); 
						   									});
						   									// from these, get all properties where write access are and the type is String
						   									props = props.findAll(function(item){ return !item.readonly() &&  item.type() == ORYX.CONFIG.TYPE_STRING });
						   									
						   									// Get all ref ids
						   									var allRefToViews	= props.collect(function(prop){ return prop.refToView() }).flatten().compact();
						   									// Get all labels from the shape with the ref ids
						   									var labels			= obj.getLabels().findAll(function(label){ return allRefToViews.any(function(toView){ return label.id.endsWith(toView) }); })
						   									
						   									// If there are no referenced labels --> return
						   									if( labels.length == 0 ){ return } 
						   									
						   									// Define the nearest label
						   									var nearestLabel 	= labels.length <= 1 ? labels[0] : null;	
						   									if( !nearestLabel ){
						   										
						   										nearestLabel = labels.find(function(label){ return label.node == evt.target || label.node == evt.target.parentNode })
						   										if( !nearestLabel ){
						   											
						   											var evtCoord 	= this.facade.eventCoordinates(evt);

						   											var trans		= this.facade.getCanvas().rootNode.lastChild.getScreenCTM();
						   											evtCoord.x		*= trans.a;
						   											evtCoord.y		*= trans.d;

						   											var diff = labels.collect(function(label){ 
						   														var center 	= this.getCenterPosition( label.node ); 
						   														var len 	= Math.sqrt( Math.pow(center.x - evtCoord.x, 2) + Math.pow(center.y - evtCoord.y, 2));
						   														return {diff: len, label: label} 
						   													}.bind(this));
						   											
						   											diff.sort(function(a, b){ return a.diff > b.diff })	
						   											
						   											nearestLabel = 	diff[0].label;

						   										}
						   									}
						   									// Get the particular property for the label
						   									var prop 			= props.find(function(item){ return item.refToView().any(function(toView){ return nearestLabel.id == obj.id + toView })});
	
	
						   									// Get the center position from the nearest label
						   									var propId		= prop.prefix() + "-" + prop.id();
	
						   									// Define event handler
					   										var currentEl 	= obj;
					   										var oldValue	= currentEl.properties[propId]; 
					   										var newValue	= selection.get('taskName');
					   										
					   										if (oldValue != newValue) {
					   											// Implement the specific command for property change
					   											var commandClass = ORYX.Core.Command.extend({
					   												construct: function(){
					   													this.el = currentEl;
					   													this.propId = propId;
					   													this.oldValue = oldValue;
					   													this.newValue = newValue;
					   													this.facade = facadeObj;
					   												},
					   												execute: function(){
					   													this.el.setProperty(this.propId, this.newValue);
					   													//this.el.update();
					   													this.facade.setSelection([this.el]);
					   													this.facade.getCanvas().update();
					   													this.facade.updateSelection();
					   												},
					   												rollback: function(){
					   													this.el.setProperty(this.propId, this.oldValue);
					   													//this.el.update();
					   													this.facade.setSelection([this.el]);
					   													this.facade.getCanvas().update();
					   													this.facade.updateSelection();
					   												}
					   											})
					   											// Instanciated the class
					   											var command = new commandClass();
					   											
					   											// Execute the command
					   											facadeObj.executeCommands([command]);
					   										}
//					   							    	   	obj.properties.name = selection.get('taskName');
						   									Ext.WindowMgr.get('Search_Activities_Window').close();  
					   						    	   	}
					   							    }
			   			                     	});
		   								
			   			        		var panel = new Ext.Panel({
			   			        			border	:false,
			   			        			bodyStyle:   'background-color:#FFFFFE; padding:10px;',
			   			        			width: 600,
			   			        	        layout: 'anchor',
				   					        width	: 'auto',
				   					        height	: 'auto',
			   			        			items: [{
			   			        				border	:false,
			   			        	            bodyStyle: 'margin-bottom:10px',
			   			        	            html: ORYX.I18N.Query.searchActivityDesc
			   			        	        }, cb]
			   			        		});
			   			        		
		   							   // Create new window and attach form into it
		   								var win = new Ext.Window({
		   									id: 'Search_Activities_Window',
		   							        width	: 'auto',
		   							        height	: 'auto',
		   								    title	: ORYX.I18N.Query.searchActivity,
		   							        modal	: true,
		   							        resizable	: false,
		   									bodyStyle: 'background:#FFFFFF',
		   							        items  : [panel],
		   									listeners: {
		   										close: function(){					
		   							            	win.destroy();
		   											delete this.saving;
		   										}.bind(this)
		   									}  
		   							    });
		   								win.show();
									  },
		   			failure			: function(transport) {
		   								Ext.getBody().unmask();
		   								Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.exception).setIcon(Ext.Msg.WARNING).getDialog().setWidth(260).center().syncSize();
									  }
		   		});
			}else{
				Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.noSelectedTaskMsg).setIcon(Ext.Msg.INFO).getDialog().setWidth(260).center().syncSize();
			}
		}else{
			Ext.Msg.alert(ORYX.I18N.Oryx.title, ORYX.I18N.Query.noSelectedTaskMsg).setIcon(Ext.Msg.INFO).getDialog().setWidth(260).center().syncSize();
		}
	}
	
});