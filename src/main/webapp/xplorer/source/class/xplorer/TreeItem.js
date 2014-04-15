/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2011 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Sebastian Werner (wpbasti)
     * Fabian Jakobs (fjakobs)
     * Christian Hagendorn (chris_schmidt)

************************************************************************ */

qx.Class.define("xplorer.TreeItem",
{
  extend : qx.ui.tree.VirtualTreeItem,

  properties :
  {
    size :
    {
      check : "Integer",
      event: "changeSize",
      nullable : true
    },

    owner : {
      check : "String",
      event : "changeOwner",
      nullable : true
    },
    group : {
      check : "String",
      event : "changeGroup",
      nullable : true
    },
    permission: {
      check : "String",
      event : "changePermission",
      nullable : true
    },    
    lastaccess: {
      check : "String",
      event: "changeLastaccess",
      nullable : true
    }
  },

  members :
  {
    __size : null,
    __owner: null,
    __group: null,
    __permission: null,
    __lastaccess: null,

    _addWidgets : function()
    {
      this.addSpacer();
      this.addOpenButton();

      // The standard tree icon follows
      this.addIcon();

      // The label
      this.addLabel();

      // All else should be right justified
      this.addWidget(new qx.ui.core.Spacer(), {flex: 1});

      // Add a file size, date and mode
      var text = this.__size = new qx.ui.basic.Label();
      text.setTextAlign("right");
      this.bind("size", text, "value");
      text.setWidth(150);
      this.addWidget(text);

      text = this.__owner = new qx.ui.basic.Label();
      text.setTextAlign("right");
      this.bind("owner", text, "value");
      text.setWidth(100);
      this.addWidget(text);

      this.addWidget(new qx.ui.basic.Label(":"));
      text = this.__group= new qx.ui.basic.Label();
      text.setTextAlign("left");
      this.bind("group", text, "value");
      text.setWidth(100);
      this.addWidget(text);

      text = this.__permission= new qx.ui.basic.Label();
      this.bind("permission", text, "value");
      text.setWidth(150);
      this.addWidget(text);

      text = this.__lastaccess = new qx.ui.basic.Label();
      this.bind("lastaccess", text, "value");
      text.setWidth(180);
      this.addWidget(text);
    }
  }
});
