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
			icon			: ORYX.PATH + "images/arrow_undo.png",
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
		alert("Get recommendation...");
	}
	
});
