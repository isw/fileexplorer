qx.Class.define("xplorer.fbrowser",
{
  extend : qx.ui.container.Composite,

  construct : function (filesystem, app) {
	this.base(arguments, new qx.ui.layout.VBox());
	this.filesystem = filesystem;
	this.application = app;
	
    var filecontainer = new qx.ui.container.Composite(new qx.ui.layout.Grow).set({
        decorator : "main"
      });
    var filepane = new qx.ui.splitpane.Pane("vertical");
    var fileslist = new qx.ui.container.Composite (new qx.ui.layout.Grow).set ({decorator:"main", height:400});
    var filescontent = new qx.ui.container.Composite (new qx.ui.layout.Grow).set ({decorator:"main"});
    var contentContainer = new qx.ui.container.Composite(new qx.ui.layout.VBox());
    var toolbar = new qx.ui.toolbar.ToolBar();
    toolbar.setSpacing(5);
    var part1 = new qx.ui.toolbar.Part();
    
    var downloadMenu = new qx.ui.menu.Menu ();
    this.downloadButton = new qx.ui.menu.Button("Download Raw");
    this.downloadButtonGZ = new qx.ui.menu.Button("Download Zip");//, "icon/22/actions/document-save.png");
    downloadMenu.add (this.downloadButton);
    downloadMenu.add (this.downloadButtonGZ);
    this.downloadMenuButton = new qx.ui.toolbar.MenuButton ("Download","icon/22/actions/document-save.png", downloadMenu);
    
    //this.downloadButton = new qx.ui.toolbar.Button("Download", "icon/22/actions/document-save.png");
    this.gotop = new qx.ui.toolbar.Button ("Top", "icon/22/actions/go-top.png");
    this.gobottom = new qx.ui.toolbar.Button ("Bottom", "icon/22/actions/go-bottom.png");
    this.tail = new qx.ui.toolbar.Button ("Tail");
    this.showfileMenu = new qx.ui.menu.Menu ();
    this.showfileplain = new qx.ui.menu.Button ("Open Plain");
    this.showfilehex = new qx.ui.menu.Button ("Open Hex");
    this.showfilefiltered = new qx.ui.menu.Button ("Open Filtered ...");
    this.showfileMenu.add (this.showfileplain);
    this.showfileMenu.add (this.showfilehex);
    this.showfileMenu.add (this.showfilefiltered);
    this.showfileButton = new qx.ui.toolbar.MenuButton ("Open", "icon/22/places/user-desktop.png", this.showfileMenu);
    
    this.downloadMenuButton.setEnabled (false);
    this.gotop.setEnabled (false);
    this.gobottom.setEnabled (false);
    this.tail.setEnabled (false);
    this.showfileButton.setEnabled (false);
    
    part1.add(this.downloadMenuButton);
    part1.add(this.showfileButton);
    part1.add(this.gotop);
    part1.add(this.gobottom);
    //part1.add(this.tail);
    toolbar.add(part1);
    toolbar.addSpacer();
    
    var partWait = new qx.ui.container.Composite(new qx.ui.layout.VBox(0,"middle"));
    this.waitIndicator = new qx.ui.basic.Atom ("Loading ...","xplorer/ajax-loader.gif");
    this.waitIndicator.setVisibility ("hidden");
    partWait.add (this.waitIndicator);
    toolbar.add (partWait);
    
    contentContainer.add(toolbar);
    
    this.content = new qx.ui.form.TextArea("");
    this.content.set ({wrap:false,readOnly:true,font:"monospace"});
    var contentCanvas = new qx.ui.container.Composite(new qx.ui.layout.Canvas);
    contentCanvas.add (this.content, {left:0,right:0,top:0,bottom:0});
    contentContainer.add (contentCanvas, {flex:1});
    
    this.status = new qx.ui.basic.Label ("");
    contentContainer.add (this.status);      
    filescontent.add(contentContainer);
    
    filepane.add (fileslist, 1);
    filepane.add (filescontent, 1);
    filecontainer.add (filepane);
    
    this.gobottom.addListener ("execute", function (e) {
  	  var de = this.content.getContentElement().getDomElement();
  	  de.scrollTop = de.scrollHeight;
    }, this); 
    this.gotop.addListener ("execute", function (e) {
  	  var de = this.content.getContentElement().getDomElement();
  	  de.scrollTop = 0;
    }, this);
    
    this.tail.addListener ("execute", function (e) {
      this.func_tail (e);
    }, this);
    this.downloadButton.addListener ("execute", function (e) {
    	this.func_download (e);
    }, this);
    this.downloadButtonGZ.addListener ("execute", function (e) {
    	this.func_downloadGZ (e);
    }, this);        
    this.showfileplain.addListener ("execute", function (e) {
      this.func_showfileplain (e);
    }, this);
    this.showfilehex.addListener ("execute", function (e) {
      this.func_showfilehex(e);
    }, this);
    this.showfilefiltered.addListener ("click", function (e) {
    	this.func_showfilefiltered(e);
    }, this);
    var self = this;
    this.fetchFolderContent ("fs.ReadFolderContent", null, function (cnt) {
  	  fileslist.add (self.createFiletrees(cnt));  
    });
    
    this.add (filepane, {flex:1}); //, {left: 0, top: 0, bottom:0, right:0});
    
    this.downloadForm = new qx.ui.embed.Html("<form target='downloadframe' id='downloaderForm' action='"+this.application.CONTEXTROOT+"/json' method='post'><input type='hidden' name='calldata'/></form>");
    toolbar.add (this.downloadForm);
    this.setStatus (null);
	
  },
  
  members :
  {
    setStatus : function (fl) {
      this.content.setValue ("");
      var usr = this.application.getUser ();
		
    	if (fl == null) {
    		this.currentFile = fl;
    		this.status.setValue ("[USER: "+usr.name+"]");
    		this.downloadMenuButton.setEnabled (false);
    		this.gotop.setEnabled (false);
    		this.gobottom.setEnabled (false);
    		this.tail.setEnabled (false);
    		this.showfileButton.setEnabled (false);    		
    	} else {
      	if (fl.length>1 || (fl.length==1 && fl[0].getDirectory())) {
      		this.currentFile = fl;
      		this.status.setValue ("[USER: "+usr.name+"]");
      		this.downloadMenuButton.setEnabled (true); 
      		this.gotop.setEnabled (false); 
      		this.gobottom.setEnabled (false);
      		this.tail.setEnabled (false);
      		this.showfileButton.setEnabled (false);
          this.downloadButton.setEnabled(false);
          this.downloadButtonGZ.setEnabled(true);   
              
      		this.contextMenu.downloadGZ.setEnabled(true);
      		//this.contextMenu.tail.setEnabled(false);
      		this.contextMenu.download.setEnabled(false);
      		this.contextMenu.openplain.setEnabled(false);
      		this.contextMenu.openhex.setEnabled(false);
      		this.contextMenu.openfiltered.setEnabled(false);
      	}
      	else {
      		this.currentFile = fl;
      		var f = fl[0].getPath().join("/");
      		var size = fl[0].getSize();
      		this.status.setValue ("[USER: "+usr.name+"], "+"File: "+f+", Size: "+size);
      		this.downloadMenuButton.setEnabled (true);
      		this.gotop.setEnabled (true);
      		this.gobottom.setEnabled (true);
      		this.tail.setEnabled (true);
      		this.showfileButton.setEnabled (true);
          this.downloadButton.setEnabled(true);
          this.downloadButtonGZ.setEnabled(true);        		
  
      		this.contextMenu.downloadGZ.setEnabled(true);
      		//this.contextMenu.tail.setEnabled(true);
      		this.contextMenu.download.setEnabled(true);
      		this.contextMenu.openplain.setEnabled(true);
      		this.contextMenu.openhex.setEnabled(true);
      		this.contextMenu.openfiltered.setEnabled(true);
      	}
    	}
    },
    openTailViewer : function (pt) {
      var win = new qx.ui.window.Window(
          "Tail: "+pt,
          "icon/16/categories/internet.png"
        );
      var layout = new qx.ui.layout.VBox();
      layout.setSeparator("separator-vertical");

      win.setLayout(layout);
      win.setContentPadding(5);
      var iframe = new qx.ui.embed.Iframe().set({
        width: 400,
        height: 300,
        minWidth: 200,
        minHeight: 150,
        source: this.application.CONTEXTROOT+"/tail.html?pt="+pt,
        decorator : null
      });
      win.add(iframe, {flex: 1});
      win.open();
      this.application.getRoot().add (win, {left:100, top:100});
    },
    isSingleFileSelected : function () {
    	if (this.currentFile != null && this.currentFile.length==1 && !this.currentFile[0].getDirectory()) return true;
    	return false;
    },
    func_tail : function (e) {
      if (this.isSingleFileSelected()) {
        var self = this;
        this.application.invokeService ("fs.CheckTail",[this.filesystem.id, this.currentFile[0].getPath().join("/")], 
            function (succ) {
              self.openTailViewer(succ);
            },
            function (err) {
        		if (err.message) {
        			alert("Error : "+err.message);
        		}
        	});
      }
    },
    func_download : function (e) {
      if (this.isSingleFileSelected())
        this.fetchFileContent (this.currentFile[0].getPath().slice(0).join("/"), "utf-8", true, false, function (cnt) {});    	
    },
    func_downloadGZ : function (e) {
      if (this.isSingleFileSelected())
        this.fetchFileContent (this.currentFile[0].getPath().slice(0).join("/"), "utf-8", true, true, function (cnt) {});
      else {
    	var files = [];
    	for (var i=0; i<this.currentFile.length; i++) {
    		var f = this.currentFile[i];
    		files.push (f.getPath().join("/"));
    	}
    	this.zipFiles (files);  
      }
    },
    func_showfileplain : function (e) {
      if (this.isSingleFileSelected())
  	    this.streamFileContent (this.currentFile[0].getPath().join("/"), "utf-8", this.content, false, null);    	
    },
    func_showfilehex : function (e) {
      if (this.isSingleFileSelected())
    	this.streamFileContent (this.currentFile[0].getPath().join("/"), "utf-8", this.content, true, null);    	
    },
    func_showfilefiltered : function (e) {
      if (this.isSingleFileSelected())
  	    this.showFilterDialog (e);    	
    },
    showFilterDialog : function (e) {
      var dlg = new qx.ui.popup.Popup(new qx.ui.layout.VBox(10)).set({
        padding : 10
      });
      try {
        dlg.placeToMouse (e);
      }
      catch (err) {
        dlg.placeToWidget (e.getTarget(), false);
      }
	    var hinweis = new qx.ui.basic.Label ("RegExp for serverside line filtering:");
	    dlg.add (hinweis);
	    var regexp = new qx.ui.form.TextField ();
	    dlg.add (regexp);
	    regexp.focus();
	    /*
	    if (this._previousFilters.length > 0) {
	    	for (var i=this._previousFilters.length-1; i>=0; i--) {
	    		var li = new qx.ui.form.ListItem (this._previousFilters[i]);
	    		regexp.add(li);
	    	}
	    }*/
	    var box = new qx.ui.container.Composite;
	    box.setLayout(new qx.ui.layout.HBox(10, "right"));
	    dlg.add(box);
	    regexp.addListener ("keyup", function (e) {
	    	if (e.getKeyIdentifier() == "Enter") {
	    	  var val = regexp.getValue();
	    	  if (val == null || val == "") {
	    		  dlg.hide();
	    		  return
	    	  }
	    	  //this._previousFilters.push (val);
	          this.streamFileContent (this.currentFile[0].getPath().join("/"), "utf-8", this.content, false, val);
	          dlg.hide();
	    	}
	    }, this);
	            
	    this.application.getRoot().add(dlg);
	    dlg.show ();
    },
    streamFileContent : function (fl, charset, targetfield, hex, filter) {
    	var timer = qx.util.TimerManager.getInstance();
    	this.stopStreamer ();
    	
		var rq = new qx.io.request.Xhr (this.application.CONTEXTROOT+"/json","POST");
		var params = [this.filesystem.id, fl, false, charset, hex, filter];
		rq.setRequestData ({"calldata":qx.util.Serializer.toJson({"service":"fs.ReadFileContents", "parameter":params})});
		rq.setTimeout (1000*60*15); // 15 minuten
		rq.send ();
		this.filestreamerrequest = rq;	
		this.waitIndicator.setVisibility ("visible");
    	this.filestreamerid = timer.start (
    	  function (userdata, timerid) {
    		var phase = this.filestreamerrequest.getPhase();
    		//qx.log.Logger.debug (phase);
    	    if (phase=="abort" || phase=="timeout" || phase=="statusError") {
    	    	this.stopStreamer ();
    	    	return;
    	    }
    	    if (phase == "loading") {
    	    	if (this.filestreamerrequest.getResponseText() != null)
    	    		userdata.setValue (this.filestreamerrequest.getResponseText());
    	    }
    	    if (phase == "load") {
    	    	if (this.filestreamerrequest.getResponseText() != null)
    	    		userdata.setValue (this.filestreamerrequest.getResponseText());
    	    	
    	    }
    	    if (phase == "sent") {
    	    	if (this.filestreamerrequest.getResponseText() != null)
    	    		userdata.setValue (this.filestreamerrequest.getResponseText());    	    	
    	    }
    	    if (phase == "success") {
    	    	if (this.filestreamerrequest.getResponseText() != null)
    	    		userdata.setValue (this.filestreamerrequest.getResponseText());
    	    	this.filestreamerequest = null;
    	    	this.stopStreamer ();
    	    }
    	  }, 200, this, targetfield, 100);    			
    },
    
    stopStreamer : function () {
    	this.waitIndicator.setVisibility ("hidden");
    	var timer = qx.util.TimerManager.getInstance();
    	if (this.filestreamerid != null)
    		timer.stop (this.filestreamerid);
    	if (this.filestreamerrequest != null)
    		this.filestreamerrequest.abort();
    },
    
    zipFiles : function (files) {
        var data = qx.util.Serializer.toJson({"service":"fs.ZipFiles", "parameter":[this.filesystem.id,files]});
    	var downloaderForm = document.getElementById ("downloaderForm");
    	downloaderForm.calldata.value = data;
    	downloaderForm.submit();    	
    },
    
    fetchFileContent : function (fl, charset, stream, zipped, successcb) {
      if (!stream) {
    	this.invokeService ("fs.ReadFileContents", [this.filesystem.id,fl, stream, charset,zipped, null], successcb, function (err) {
    		if (err.message) {
    			alert("Error loading FS: "+err.message);
    		}
    	});
      }
      else {
        var data = qx.util.Serializer.toJson({"service":"fs.ReadFileContents", "parameter":[this.filesystem.id,fl,stream, charset,zipped,null]});
    	var downloaderForm = document.getElementById ("downloaderForm");
    	downloaderForm.calldata.value = data;
    	downloaderForm.submit();
      }
    	
    },
    invokeService : function (service, params, successcb, errcb) {
    	this.application.invokeService (service, params, successcb, errcb);
    },
    fetchFolderContent : function (service, parent, successcb) {
      this.invokeService (service, [this.filesystem.id,parent], successcb, function (err) {
  		if (err.message) {
			alert("Error loading folder content: "+err.message);
		}
      });
    },
    createFiletrees : function (rootdir) {
      var self = this;
      var scroller = new qx.ui.container.Scroll();
      var container = new qx.ui.container.Composite(new qx.ui.layout.Dock());
      scroller.add(container);
      var tree = new qx.ui.tree.VirtualTree(null, "label","children");
      tree.setModel (qx.data.marshal.Json.createModel(rootdir, true));
      container.add (tree, {width:"100%",height:"100%"});
      
      tree.setIconPath("directory");
      tree.setHideRoot (true);
      tree.setSelectionMode("multi");
      tree.setOpenMode("click");
      tree.setDragSelection(true);
      tree.setIconOptions({
        converter : function(value, model, source, target)
        {
          if (value) {
            if (target.isOpen())
              return "icon/22/places/folder-open.png";
            else
              return "icon/22/places/folder.png";
          } else {
            return "icon/22/mimetypes/office-document.png";
          }
        }
      });
      var delegate = {
          bindItem : function(controller, item, id) {
            controller.bindDefaultProperties(item, id);
            controller.bindProperty("size", "size", null, item, id);
            controller.bindProperty("user", "owner", null, item, id);
            controller.bindProperty("group", "group", null, item, id);
            controller.bindProperty("rights", "permission", null, item, id);
            controller.bindProperty("lastaccess", "lastaccess", null, item, id);
          },
          createItem : function () {
            return new xplorer.TreeItem();
          }
        };
      tree.setDelegate (delegate);
      tree.addListener ("open", function (e) {
        var node = e.getData();
        if (node.getDirectory ()) {
          this.fetchFolderContent ("fs.ReadFolderContent", node.getPath().join("/"), function (cnt) {
            node.setChildren(qx.data.marshal.Json.createModel(cnt.children,true));
          });          
        }
      }, this);
      tree.getSelection().addListener("change", function(e) {
        var sel = tree.getSelection();
        if (sel.getLength() < 1) {
          self.setStatus (null);
          return;
        }
        var fnodes = [];
        for (var i=0; i<sel.getLength(); i++) {
          fnodes.push (sel.getItem(i));
        }
        self.setStatus (fnodes);          
        if (self.isSingleFileSelected())
          if (fnodes[0].getSize() < 500000) 
            self.func_showfileplain();
          else
            self.content.setValue ("[Filesize too big for automatic display, please select manually]");
      }, this);
      tree.setContextMenu (this.getFileContextMenu());
      this._filetree = tree;
      return scroller;
    },
    xcreateFiletrees : function (rootdir) {
        var scroller = new qx.ui.container.Scroll();
        var container = new qx.ui.container.Composite(new qx.ui.layout.VBox());
        //container.setAllowGrowX(false);
        //container.setAllowStretchX(false);
        scroller.add(container);

        var tree = new qx.ui.treevirtual.TreeVirtual(
                [
                  "Path",
                  "Size",
                  "Owner",
                  "Group",
                  "Permissions",
                  "Last modified"
                ]);
        tree.setAlwaysShowOpenCloseSymbol (true);
        tree.setSelectionMode(
                qx.ui.treevirtual.TreeVirtual.SelectionMode.MULTIPLE_INTERVAL);
        
        // der Pfad darf nie ganz wegminimiert werden ...
        var resizeBehavior = tree.getTableColumnModel().getBehavior();
        resizeBehavior.set(0, { width:"3*", minWidth:200  });
        resizeBehavior.set(1, { width:"1*", minWidth:30  });
        resizeBehavior.set(2, { width:"2*", minWidth:40  });
        resizeBehavior.set(3, { width:"1*", minWidth:30  });
        resizeBehavior.set(4, { width:"1*", minWidth:30  });
        resizeBehavior.set(5, { width:"2*", minWidth:40  });
        //tree.set ({width:"100%"});
        
        //container.add(tree, {left: 0, top: 0, right:0, bottom:0});
        container.add(tree, {flex:1});
        var dataModel = tree.getDataModel();

        var rootnode = dataModel.addBranch (null, "/", true);
        dataModel.setColumnData (rootnode, 10, rootdir);
        this.addNodesToModel (dataModel, rootnode, rootdir);
        
        var self = this;
        tree.addListener("treeOpenWithContent",
                function(e)
                {
                  var node = e.getData();
                  var n = dataModel.getColumnData(node.nodeId, 10);
                  if (n != null && n.path != null && n.path.length > 0)
  	                self.fetchFolderContent ("fs.ReadFolderContent", n.path.slice(0).join("/"), function (cnt) {
  	                  dataModel.prune (node.nodeId, false);
  	                  self.addNodesToModel(dataModel, node.nodeId, cnt);
  	                });
                });

        tree.addListener("treeClose",
                function(e) {
            		var node = e.getData();
            		dataModel.prune (node.nodeId, false);
            		dataModel.addLeaf (node.nodeId, "Loading ...");
                });      
        tree.addListener("treeOpenWhileEmpty",
                function(e) {
  		        var node = e.getData();
  		        var n = dataModel.getColumnData(node.nodeId, 10);
  		        self.fetchFolderContent ("fs.ReadFolderContent", n.path.slice(0).join("/"), function (cnt) {
  		          dataModel.prune (node.nodeId, false);
  		          self.addNodesToModel(dataModel, node.nodeId, cnt);
  		        });
                });


        tree.addListener("changeSelection",
                function(e) {
      	  		var nodes = e.getData();
      	  		if (nodes == null || nodes.length == 0) {
      	  			self.setStatus (null);
      	  			return;
      	  		}
      	  		var fnodes = self.getSelectedNodesFromTree(nodes, dataModel);
      	  		
//        	  		if (fnodes.length>1 || fnodes[0].directory) {
//        	  			self.setStatus (fnodes);
//        	  			return;
//        	  		}    	  		
      	  		self.setStatus (fnodes);    	  	
      	  		if (self.isSingleFileSelected())
      	  			if (fnodes[0].size < 500000) 
      	  				self.func_showfileplain();
      	  			else
      	  				self.content.setValue ("[Filesize too big for automatic display, please select manually]")
                });

        tree.setContextMenu (this.getFileContextMenu());
        this._filetree = tree;
        return scroller;
      },
      getSelectedNodesFromTree : function () {
        var res = [];
        var selection = this._filetree.getSelection();
        for (var i=0; i<selection.getLength(); i++) {
          res.push (selection.getItem(i));
        }
        return res;
      },
      contextMenuHandler : function (target) {
      	return qx.lang.Function.bind (function (e) {
      		this.contextMenuSelected(target, e);
      	}, this);
      },
      contextMenuSelected : function (target, e) {        
      	var nodes = this.getSelectedNodesFromTree();
      	this.setStatus (nodes);
      	target.call(this, e);
      },
      getFileContextMenu : function()  {
        var menu = new qx.ui.menu.Menu;
        
        var download = new qx.ui.menu.Button ("Download Raw");
        //download.addListener ("execute", this.func_download);
        download.addListener ("execute", this.contextMenuHandler(this.func_download), this);
        var downloadGZ = new qx.ui.menu.Button ("Download ZIP");
        downloadGZ.addListener ("execute", this.contextMenuHandler(this.func_downloadGZ), this);
        var oplain = new qx.ui.menu.Button ("Open Plain");
        oplain.addListener ("execute", this.contextMenuHandler(this.func_showfileplain), this);
        var ohex = new qx.ui.menu.Button ("Open Hex");
        ohex.addListener ("execute", this.contextMenuHandler(this.func_showfilehex), this);
        var ofilt = new qx.ui.menu.Button ("Open Filtered");
        ofilt.addListener ("execute", this.contextMenuHandler(this.func_showfilefiltered), this);
        var tail = new qx.ui.menu.Button ("Tail");
        tail.addListener ("execute", this.contextMenuHandler(this.func_tail), this);
        this.contextMenu = {
          "download" : download,
          "downloadGZ" : downloadGZ,
          "openplain":oplain,
          "openhex":ohex,
          "openfiltered":ofilt
          //"tail":tail
        };
        
        //var cutButton = new qx.ui.menu.Button("Cut", "icon/16/actions/edit-cut.png", this.__cutCommand);
        //var copyButton = new qx.ui.menu.Button("Copy", "icon/16/actions/edit-copy.png", this.__copyCommand);
        //var pasteButton = new qx.ui.menu.Button("Paste", "icon/16/actions/edit-paste.png", this.__pasteCommand);
  
        //cutButton.addListener("execute", this.debugButton);
        //copyButton.addListener("execute", this.debugButton);
        //pasteButton.addListener("execute", this.debugButton);
  
        menu.add(download);
        menu.add(downloadGZ);
        menu.add(oplain);
        menu.add(ohex);
        menu.add(ofilt);
        //menu.add(tail);
        return menu;
      },
      addNodesToModel : function (datamodel, parent, data) {
  	  for (var i=0; i<data.children.length; i++) {
  	    var c = data.children[i];
  	  	var node = null;
  	  	var name = c.path[c.path.length-1];
  	  	if (c.directory) {
  	  	  node = datamodel.addBranch (parent, name, false); 
  	  	  datamodel.addLeaf (node, "Loading ...");
  	  	}
  	  	else {
  	      node = datamodel.addLeaf (parent, name);    		  
  	  	}
  	    datamodel.setColumnData (node, 1, c.size);
  	    datamodel.setColumnData (node, 2, c.user);
  	    datamodel.setColumnData (node, 3, c.group);
  	    datamodel.setColumnData (node, 4, c.rights);
  	    datamodel.setColumnData (node, 5, c.lastaccess);
  	    datamodel.setColumnData (node, 10, c);
  	  }    	
      datamodel.setData();
    }
    
  }
});
