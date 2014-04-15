qx.Class.define("xplorer.tomcat",
{
  extend : qx.ui.container.Composite,

  construct : function (app) {
   this.base(arguments, new qx.ui.layout.VBox(5));
   this.application = app;  
   this.servers = new qx.ui.form.SelectBox ();
   this.serverChangeEventid = this.servers.addListener ("changeSelection", this.onServerSelect, this);
   
   this.refreshButton = new qx.ui.form.Button ("Refresh","icon/16/actions/view-refresh.png");
   this.refreshButton.addListener ("execute", this.refresh, this);
   this.heapdumpButton = new qx.ui.form.Button ("Heapdump","icon/16/actions/system-search.png");
   this.heapdumpButton.addListener ("execute", this.heapdump, this);
   var line = new qx.ui.container.Composite (new qx.ui.layout.HBox ());
   line.add (this.servers, {flex:1});
   line.add (this.refreshButton);
   line.add (this.heapdumpButton);
   
   this.add (line);
   
   this.webappsModel = new qx.ui.table.model.Simple ();
   this.webappsModel.setColumns (["Context","State","Sessions", "Timeout (sec)"]);
   
   this.webappsTable = new qx.ui.table.Table (this.webappsModel);
   this.webappsTable.setStatusBarVisible (false);
   this.webappsTable.setHeight (200);
   this.webappsTable.getSelectionModel().addListener ("changeSelection", this.fillWebappDescriptor, this);
   this.webappsTable.setContextMenu (this.getTomcatContextMenu());
   this.deploymentDescriptor = new qx.ui.form.TextArea ();
   this.deploymentDescriptor.set ({wrap:true,readOnly:true,font:"monospace", enabled:true});
   
   //this.webapps = new qx.ui.form.List ();
   //this.add (this.webapps);
   this.add (this.webappsTable);
   this.add (this.deploymentDescriptor, {flex:1});
   this.refresh ();
  },
  
  members :
  {
    heapdump : function () {
      this.application.streamService ("tomcat.GenerateHeapDump", [this.servers.getSelection()[0].getModel().pid]);
    },
    
    refresh : function () {
      var self = this;
      this.webappsModel.setData ([]);
      var serv = this.servers.getSelection();
      if (serv != null && serv.length>0)
    	  serv = serv[0].getModel();
      else
    	  serv = null;
      this.servers.removeAll ();
      this.deploymentDescriptor.setValue ("");
      this.application.invokeService ("tomcat.ListServers", [], function (res) {
        self.fillServerList(res, serv);
        }, function (err) {
          alert(err.message);
        });      
    },
    
    onServerSelect : function (e) {
      if (e.getData()==null || e.getData().length <1) return;
      var srv = e.getData()[0].getModel();
      var self = this;
      this.deploymentDescriptor.setValue ("");
      this.application.invokeService ("tomcat.ListApplications", [srv], function(res) {
        self.fillWebappList(res);
      }, function (err) {
        alert(err.message);
      });
      
    },
   
    fillWebappList : function (webapplist) {
      var data = [];
      this.webappsTable.getSelectionModel().resetSelection();
      for (var i=0; i<webapplist.length; i++) {
        var wa = webapplist[i];
        data.push ([wa.context,wa.state,wa.numSessions, wa.sessionTimeout, wa.deploymentDescriptor,wa.oname]);
      }
      this.webappsModel.setData (data);
    },
    
    fillServerList : function (serversList, sel) {
      this.servers.removeListenerById (this.serverChangeEventid);	
      this.servers.removeAll ();
      var selection = null;
      for (var i=0; i<serversList.length; i++) {
    	var srv = serversList[i];
        var lbl = srv.serverInfo;
        if (srv.instanceName != null) {
          lbl = lbl + " ["+srv.instanceName+"]";
        }
        else {
          lbl = lbl + " ["+srv.pid+"]";
        }
        var li = new qx.ui.form.ListItem(lbl,null, srv);
        this.servers.add (li);
        if (sel != null && (sel.pid == srv.pid))
        	selection = li;
      }      
      if (selection != null)
    	  this.servers.setSelection ([selection]);
      this.serverChangeEventid = this.servers.addListener ("changeSelection", this.onServerSelect, this);
      this.servers.fireDataEvent ("changeSelection",this.servers.getSelection());
    },
    fillWebappDescriptor : function (e) {
      var selectedRowData = [];
      this.webappsTable.getSelectionModel().iterateSelection(function(index) {
        selectedRowData.push(this.webappsModel.getRowData(index));
      }, this);      
      if (selectedRowData.length > 0)
        this.deploymentDescriptor.setValue (vkbeautify.xml(selectedRowData[0][selectedRowData[0].length-2],2));
    },
    startApp : function (e) {
      var wm = this.webappsModel;
      this.webappsTable.getSelectionModel().iterateSelection(function(index) {
        var data = this.webappsModel.getRowData(index);
        var serv = this.servers.getSelection()[0].getModel();
        if (!serv.canExecute) {
        	alert("Not Allowed!");
        	return;
        }
        this.application.invokeService ("tomcat.StartApplication", [serv.pid, data[data.length-1]], function (res) {
          wm.setValue (1, index, res.state);
        }, function (err) {
          alert (err.message);
        });
      }, this);            
    },
    stopApp : function (e) {
      var wm = this.webappsModel;
      this.webappsTable.getSelectionModel().iterateSelection(function(index) {
        var data = this.webappsModel.getRowData(index);
        var serv = this.servers.getSelection()[0].getModel();
        if (!serv.canExecute) {
        	alert("Not Allowed!");
        	return;
        }
        this.application.invokeService ("tomcat.StopApplication", [serv.pid, data[data.length-1]], function (res) {
          wm.setValue (1, index, res.state);
        }, function (err) {
          alert (err.message);
        });
      }, this);            
      
    },
    browseApp : function (e) {
      this.webappsTable.getSelectionModel().iterateSelection(function(index) {
        var data = this.webappsModel.getRowData(index);
        window.open (this.servers.getSelection()[0].getModel().baseUrl+data[0]);
      }, this);
    },
    getTomcatContextMenu : function()  {
      var menu = new qx.ui.menu.Menu;
      
      var start = new qx.ui.menu.Button ("Start","icon/16/actions/media-playback-start.png");
      start.addListener ("execute", this.startApp, this);
      var stop = new qx.ui.menu.Button ("Stop","icon/16/actions/media-playback-stop.png");
      stop.addListener ("execute", this.stopApp, this);
      var browse = new qx.ui.menu.Button ("Browse","icon/16/categories/internet.png");
      browse.addListener ("execute", this.browseApp, this);

      menu.add(start);
      menu.add(stop);
      menu.add(browse);

      return menu;
    } 
  }
});