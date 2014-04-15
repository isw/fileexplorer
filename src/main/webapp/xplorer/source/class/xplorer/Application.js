/* ************************************************************************

   Copyright:

   License:

   Authors:

************************************************************************ */

/* ************************************************************************

#asset(xplorer/*)
#asset(qx/icon/${qx.icontheme}/22/actions/*)
#asset(qx/icon/${qx.icontheme}/16/actions/*)
#asset(qx/icon/${qx.icontheme}/16/places/*)
#asset(qx/icon/${qx.icontheme}/16/categories/*)
#asset(qx/icon/${qx.icontheme}/22/places/*)
#asset(qx/icon/${qx.icontheme}/22/apps/*)
#asset(qx/icon/${qx.icontheme}/22/mimetypes/*)

************************************************************************ */

/**
 * This is the main application class of your custom application "xplorer"
 */
qx.Class.define("xplorer.Application",
{
  extend : qx.application.Standalone,



  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    /**
     * This method contains the initial application code and gets called 
     * during startup of the application
     * 
     * @lint ignoreDeprecated(alert)
     */
    main : function()
    {
      // Call super class
      this.base(arguments);

      this.CONTEXTROOT = "/"+window.location.pathname.split("/")[1];
      if (this.CONTEXTROOT === "/xplorer")
    	  this.CONTEXTROOT = "";
      
      // Enable logging in debug variant
      if (qx.core.Environment.get("qx.debug"))
      {
        // support native logging capabilities, e.g. Firebug for Firefox
        qx.log.appender.Native;
        // support additional cross-browser console. Press F7 to toggle visibility
        qx.log.appender.Console;
      }
      this._previousFilters = new Array ();
      
      var self = this;
      this.checkLogin (function (res) {
        if (res == null) {
        	self.showLoginScreen();
        }
        else {
        	self.user = res;
        	self.setupMainScreen ();
        }
      }, function (err) {
    	  qx.log.Logger.error (self, "Error while 'checkLogin'", err);
      });
      
      var sessionTimer = qx.util.TimerManager.getInstance();
      sessionTimer.start (function (userdata, timerid) {
        this.invokeService("core.Ping",[], function(e){});
      },
      1000*60*5,
      this,
      null,
      1000*60*5);
    },
    showLoginScreen : function () {
      var self = this;
  	  this.loadAuthSchemes(function (res) {
		self.showLogin (res);
	  });    
    },
    setupMainScreen : function () {
    	var self = this;
    	var mainContent = new qx.ui.container.Composite (new qx.ui.layout.VBox());
    	mainContent.setPadding(5);
      mainContent.add (this.createMenubar());
      this.getRoot().add (mainContent, {left: 0, right: 0, bottom:0, top:0});
      this.prepareAboutDialog();
      var pluginTab = new qx.ui.tabview.TabView ();
      pluginTab.setBarPosition ("left");
      var fstab = new qx.ui.tabview.Page("Files", "icon/22/apps/internet-transfer.png");
      fstab.setLayout (new qx.ui.layout.VBox());
      pluginTab.add (fstab);
      //var servertab = new qx.ui.tabview.Page ("Tomcat", "icon/22/apps/utilities-network-manager.png");
      //servertab.setLayout (new qx.ui.layout.VBox());
      //servertab.add (new xplorer.tomcat(this), {flex:1});
      //pluginTab.add(servertab);
      mainContent.add (pluginTab, {flex:1});
      
    	this.loadFilesystems(function (succ) {
    		// eine schleife mit tabs!
    	  var fsView = new qx.ui.tabview.TabView();
    		for (var i=0; i<succ.length; i++) {
    		  var fs = succ[i];
    			var tab = new qx.ui.tabview.Page(fs.label,"icon/16/places/folder.png");
    			tab.setLayout (new qx.ui.layout.VBox());
    			tab.add (new xplorer.fbrowser(fs, self), {flex:1});
    			//self.setupFilebrowserFS(succ[0], mainContent);
    			fsView.add (tab);
    		}
    		fstab.add (fsView, {flex:1});
    	}, function (err) {
    		if (err.message) {
    			alert("Error loading FS: "+err.message);
    		}
    	});
    },
    prepareAboutDialog : function () {
      var ad = this.__aboutdialog = new qx.ui.groupbox.GroupBox().set({
        contentPadding: [16, 16, 16, 16],
        zIndex : -1,
        visibility: "hidden"
      });
      var blk = this.__aboutblocker = new qx.ui.core.Blocker (this.getRoot());
      ad.setLayout(new qx.ui.layout.VBox(30));
      var content = new qx.ui.container.Composite();
      content.setDecorator("popup");
      content.setPadding(10);
      content.setLayout (new qx.ui.layout.VBox(5));
      var msg = new qx.ui.basic.Label ("&copy; by Innuendo Software Technology GmbH<p>").set({rich:true});
      var logo = new qx.ui.basic.Image ("xplorer/logo.gif");
      var msg2 = new qx.ui.basic.Label ("<p>.... und a bisserl FITS .... :-)</p>").set({rich:true});
      var btn = new qx.ui.form.Button("Ok", "icon/16/actions/dialog-ok.png");        
      btn.addListener("execute", function(e) {
    	//this.__showabout.setMode("in");
    	//this.__showabout.start();
    	this.__aboutdialog.hide();
        blk.unblock ();
      }, this);
      var layout = new qx.ui.layout.Flow();
      layout.setAlignX("center");
      var btnCont = new qx.ui.container.Composite(layout);
      btnCont.add (btn);
      content.add (logo);
      content.add (msg);
      content.add (msg2);
      content.add (btnCont);
      ad.add (content);
      this.getRoot().add (ad, {left:"40%",top:"30%"});
      /*ad.addListenerOnce("appear", function () {
    	this.__showabout = new qx.fx.effect.combination.Fold (
    		this.__aboutdialog.getContainerElement().getDomElement());
      }, this);*/
      
    },
    showAboutDialog : function () {
      this.__aboutdialog.setZIndex(101);
      this.__aboutdialog.show();
      this.__aboutblocker.blockContent(100);
  	  //this.__showabout.setMode("out");
	  //this.__showabout.start();
      //this.__aboutblocker.blockContent(100);
    },
    createMenubar : function () {
        var menubar = new qx.ui.menubar.MenuBar;
        var fmenu = new qx.ui.menu.Menu;
        var hmenu = new qx.ui.menu.Menu;
    	var exitButton = new qx.ui.menu.Button("Exit", "icon/16/actions/application-exit.png");
    	var aboutButton = new qx.ui.menu.Button("About", "icon/16/actions/help-about.png");	

    	exitButton.addListener ("execute", function (e) {
    		this.logout (qx.lang.Function.bind(this.showLoginScreen, this));
    	}, this);
    	aboutButton.addListener ("execute", function (e) {
    		this.showAboutDialog ();
    	}, this);
    	fmenu.add (exitButton);
    	hmenu.add (aboutButton);
        var fileMenu = new qx.ui.menubar.Button("File", null, fmenu);        
        menubar.add (fileMenu);
        menubar.addSpacer();
        var helpMenu = new qx.ui.menubar.Button ("Help", null, hmenu);
        menubar.add (helpMenu);
        return menubar;
    },
    
    showLogin : function (schemes) {
    	this.getRoot().removeAll();
    	/* Container layout */
        var layout = new qx.ui.layout.Grid(9, 5);
        layout.setColumnAlign(0, "right", "top");
        layout.setColumnAlign(1, "center", "top");
        layout.setColumnAlign(3, "right", "top");
        layout.setColumnFlex (1, 1);
        layout.setColumnFlex (2, 2);
        
        /* Container widget */
        var loginForm = new qx.ui.groupbox.GroupBox().set({
          contentPadding: [16, 16, 16, 16]
        });
        loginForm.setLayout(layout);

        loginForm.addListener("resize", function(e) {
          var bounds = loginForm.getBounds();
          loginForm.set({
            marginTop: Math.round(-bounds.height / 2),
            marginLeft : Math.round(-bounds.width / 2)
          });
        }, this);

        this.getRoot().add(loginForm, {left: "50%", top: "30%", width:"30%"});
        loginForm.resetUserBounds();
        //var logo = new qx.ui.basic.Image ("xplorer/logo.gif").set({opacity:0.1});
        var logo = new qx.ui.basic.Label ("&copy; by Innuendo Software Technology GmbH").set({rich:true,opacity:0.4});
        this.getRoot().add (logo, {right:20,bottom:20});
        /* Labels */
        var labels = ["Host-ID", "Password"];
        for (var i=0; i<labels.length; i++) {
          loginForm.add(new qx.ui.basic.Label(labels[i]).set({
            allowShrinkX: false,
            paddingTop: 3
          }), {row: i, column : 1});
        }
        var schemebox = new qx.ui.form.SelectBox();
        for (var i=0; i<schemes.length; i++) {
        	var li = new qx.ui.form.ListItem (schemes[i].description,null,schemes[i].componentName);
        	schemebox.add(li);
        }
        loginForm.add (schemebox, {row:2,column:2});
        
        var msg = new qx.ui.basic.Label().set ({allowShrinkX:false, paddingTop:3});
        loginForm.add (msg,{row:3,column:1,colSpan:3});
        
        /* Text fields */
        var field1 = new qx.ui.form.TextField();
        var field2 = new qx.ui.form.PasswordField();
        loginForm.add(field1.set({
          allowShrinkX: false,
          paddingTop: 3
        }), {row: 0, column : 2});

        loginForm.add(field2.set({
          allowShrinkX: false,
          paddingTop: 3
        }), {row: 1, column : 2});

        /* Button */
        var button1 = this.__okButton =  new qx.ui.form.Button("Login");
        button1.setAllowStretchX(false);

        loginForm.add(
          button1,
          {
            row : 5,
            column : 2
          }
        ); 
        var olduser = qx.bom.Cookie.get("fileexplorer.userid");
        var oldschem = qx.bom.Cookie.get("fileexplorer.scheme");
        if (olduser != null) {
        	field1.setValue (olduser);
        	field2.focus ();
        }
        else
        	field1.focus ();
        if (oldschem != null)
        	schemebox.setModelSelection (new Array(oldschem));
        
        var self = this;
        var trylogin = function (e) {
            var u = field1.getValue();
            var p = field2.getValue();
            var s = schemebox.getSelection()[0].getModel();
            self.doRemoteLogin (u, p, s, function (res) {
            	qx.bom.Cookie.set("fileexplorer.userid", u, 30); // 30 tage im cookie speichern
            	qx.bom.Cookie.set("fileexplorer.scheme", s, 30);
            	self.user = res;
            	self.getRoot().remove (loginForm);
            	self.setupMainScreen();
            }, 
            function (res) {
            	var shake = {duration: 1000, keyFrames : {
                    0 : {translate: "0px"},
                    10 : {translate: "-10px"},
                    20 : {translate: "10px"},
                    30 : {translate: "-10px"},
                    40 : {translate: "10px"},
                    50 : {translate: "-10px"},
                    60 : {translate: "10px"},
                    70 : {translate: "-10px"},
                    80 : {translate: "10px"},
                    90 : {translate: "-10px"},
                    100 : {translate: "0px"}
                  }};
            	qx.bom.element.Animation.animate(loginForm.getContainerElement().getDomElement(), shake);
            	msg.setValue (res.message);
            	field2.setTextSelection (0);
            });        	
        };
        
        button1.addListener ("execute", trylogin, this);
        field1.addListener ("keyup", function (e) {
        	if (e.getKeyIdentifier() == "Enter")
        		trylogin (e);
        }, this);
        field2.addListener ("keyup", function (e) {
        	if (e.getKeyIdentifier() == "Enter")
        		trylogin (e);
        }, this);
    },
    
    getUser : function () {
    	return this.user;
    },
    doRemoteLogin : function (user, pwd, scheme, succ, err) {
      this.invokeService ("login.Login", [user,pwd, scheme], succ, err);
    },
    
    checkLogin : function (succ, err) {
      this.invokeService ("login.GetUser", [], succ, err);
    },

    loadAuthSchemes : function (succ, err) {
      this.invokeService ("login.GetAuthenticators",[], succ, err);
    },
    
    loadFilesystems : function (succ, err) {
      this.invokeService ("fs.GetFilesystems",[], succ, err);
    },
    logout : function (succ, err) {
        this.invokeService ("login.Logout", [], succ, err);
      },

    
    invokeService : function (service, params, successcb, errcb) {
  	  var rq = new qx.io.remote.Request (this.CONTEXTROOT+"/json","POST","application/json");
  	  rq.setParseJson (true);
  	  rq.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  	  rq.setParameter ("calldata",qx.util.Serializer.toJson({"service":service, "parameter":params}));
  	  rq.setTimeout(30000);
  	  rq.addListener ("completed", function (e) {
  		  var res = e.getContent();
  		  if (res.status == "OK")
  			  successcb (res.result);
  		  else {
  			  if (errcb != null)
  				  errcb (res);
  			  else
  				  qx.log.Logger.error (res);
  		  }
  	  });
  	  rq.send ();
    },
    
    streamService : function (service, parameters) {
      var data = qx.util.Serializer.toJson({"service":service, "parameter":parameters});
      var downloaderForm = document.getElementById ("downloaderForm");
      downloaderForm.calldata.value = data;
      downloaderForm.submit();      
    }
  }
});
