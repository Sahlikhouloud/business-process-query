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
		var reqURIs = reqURI.split("/");
		var prefix = "/";
	    for(i=1; i<reqURIs.length-1; i++){
		    prefix+=reqURIs[i]+"/";
	    }
		var modelJSON = this.facade.getJSON();
		var canvasChilds  = modelJSON.childShapes;
		var tasks = [];
		for(i=0; i<canvasChilds.length; i++){
		    if(canvasChilds[i].stencil.id == 'Task' || canvasChilds[i].stencil.id == 'CollapsedSubprocess'){
		    	tasks.push(canvasChilds[i].properties.name);
		    }
	    }
		
		var optionTxt = "";
		for(i=0; i<tasks.length; i++){
			optionTxt+='<option value="'+tasks[i]+'">'+tasks[i]+'</option>';
	    }
		
		// Get the stencilset
		var ss = this.facade.getStencilSets().values()[0]
		
		var typeTitle = ss.title();
		
		// Define Default values
		var defaultData = {title:Signavio.Utils.escapeHTML(name||""), summary:Signavio.Utils.escapeHTML(modelMeta.description||""), type:typeTitle, url: reqURI, namespace: modelMeta.model.stencilset.namespace, comment: '' }
		
		//Create components in template
//		var slider = new Ext.form.SliderField({
////		    renderTo: dialog,
//		    width: 200,
//		    value: 50,
//		    increment: 10,
//		    minValue: 0,
//		    maxValue: 100
//		});
		
//		Test = {}; 
//	    Test.slideZone1 = new Ext.ux.SlideZone('slider1', {   
//	        type: 'horizontal', 
//	        size: 500,  
//	        sliderWidth: 18, 
//	        sliderHeight: 21, 
//	        maxValue: 1000, 
//	        minValue: 0, 
//	        sliderSnap: 1, 
//	        sliders: [{ value: 500,   
//	                    name: 'start1_1' 
//	                    }] 
//	         }); 
//	     
//	    Test.slideZone1.getSlider('start1_1').on('drag', 
//	        function() { 
//	                $('slider_1_1_value').innerHTML = parseInt(this.value); 
//	                $('slider_1_1_percent').innerHTML = this.percent.toFixed(2); 
//	                $('slider_1_1_position').innerHTML = this.el.getX() + 
//	                        1/2 * Test.slideZone1.sliderWidth;     
//	                }  
		
//		'new Ajax.Request('+prefix'+"query", {})'+
//        'method: \'get\',' +
//        'asynchronous: true,' +
//		'requestHeaders: {' +
//			'"Accept":"application/json"' +
//		'},' +
//		'parameters: {' +
//			'id: \'getMaxZone\'' +
//        '},' +
//		'encoding: \'UTF-8\',' +
//		'onSuccess: (function(transport) {' +
//			
//		'}).bind(this),' +
//		'onException: function(){' +
//			
//		'}.bind(this)' +
//	'});' +
		// Create a Template
		var dialog = new Ext.XTemplate(		
			// TODO find some nice words here -- copy from above ;)
			'<form class="oryx_repository_edit_model" action="#" id="query_model" onsubmit="return false;">',
							
				'<fieldset>',
					'<p class="description">' + ORYX.I18N.Query.dialogDesciption + '</p>',
					'<input type="hidden" name="namespace" value="{namespace}" />',
					'<p><label for="query_model_task">' + ORYX.I18N.Query.targetTask + '</label>' +
						'<select class="select" name="task" id="query_model_task" onchange="' +
//							'new Ajax.Request('+prefix+'query/, {'+
//							'method: \'get\',' +
//							'asynchronous: true,' +
//							'parameters: {' +
//								'id: "getMaxZone"' +
//					        '},' +
//							'encoding: \'UTF-8\',' +
//							'onSuccess: (function(transport) {' +
//								
//							'}).bind(this),' +
//							'onException: function(){' +
//								
//							'}.bind(this)' +
//							'});' +
						'">'+ optionTxt + '</select>' +
					'</p>',
					'<p><label for="query_model_zone">' + ORYX.I18N.Query.zone + '</label>' +
						'<select class="select" name="zone" id="query_model_zone" >' +
							'<option value="1">1</option>' +
							'<option value="2">2</option>' +
							'<option value="3">3</option>' +
							'<option value="4">4</option>' +
							'<option value="5">5</option>' +
						'</select>' +
					'</p>',
					'<p><label for="query_model_method">' + ORYX.I18N.Query.zone + '</label>' +
						'<select class="select" name="method" id="query_model_method">' +
							'<option value="2">Levenstein</option>' +
							'<option value="4">Improved weight</option>' +
						'</select>' +
					'</p>',
//					'<span class="x-editable">{slider}</span></div>',
					'<p><label for="edit_model_type">' + ORYX.I18N.Save.dialogLabelType + '</label><input type="text" name="type" class="text disabled" value="{type}" disabled="disabled" id="edit_model_type" /></p>',
				'</fieldset>',
			
			'</form>');
		
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
			var task = form.elements["task"].value;
			var zone = form.elements["zone"].value;
			var method = form.elements["method"].value;
			var namespace	= form.elements["namespace"].value.strip();
			modelMeta.namespace = namespace;
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
