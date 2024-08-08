webpackJsonp([6],{"5Xle":function(e,r,t){"use strict";Object.defineProperty(r,"__esModule",{value:!0});var s=t("Xxa5"),a=t.n(s),o=t("exGp"),l=t.n(o),n={name:"UserModal",props:{visible:Boolean,addOrEdit:String,userInfo:Object},data:function(){return{title:"",form:{userName:"",password:"",userTrueName:"",role:""},rules:{userName:[{required:!0,message:"用户名不能为空",trigger:"blur"}],password:[{required:!0,message:"密码不能为空",trigger:"blur"}],userTrueName:[{required:!0,message:"用户真实姓名不能为空",trigger:"blur"}],role:[{required:!0,message:"用户权限不能为空",trigger:"change"}]}}},watch:{addOrEdit:{handler:function(e){this.title="add"==e?"新建用户":"编辑用户"}},visible:{handler:function(e){var r=this;e&&"edit"==this.addOrEdit&&this.$nextTick(function(){r.form.userName=r.userInfo.userName,r.form.password=r.userInfo.password,r.form.userTrueName=r.userInfo.userTrueName,r.form.role=r.userInfo.role.value})},immediate:!0}},methods:{updateVisible:function(e){this.$refs.form.resetFields(),this.$emit("update:visible",e)},submitForm:function(){var e=this;this.$refs.form.validate(function(r){if(!r)return console.log("error submit!!"),!1;"add"==e.addOrEdit?e.insertUser():e.updateUser()})},insertUser:function(){var e=this;return l()(a.a.mark(function r(){var t;return a.a.wrap(function(r){for(;;)switch(r.prev=r.next){case 0:return r.prev=0,r.next=3,e.$http.post("/user/insert",{userName:e.form.userName,password:e.form.password,userTrueName:e.form.userTrueName,role:e.form.role});case 3:200==(t=r.sent).data.code?(e.$message.success("添加成功"),e.updateVisible(!1),e.$parent.queryUserAll()):e.$message.error(t.data.msg),r.next=10;break;case 7:r.prev=7,r.t0=r.catch(0),console.log(r.t0);case 10:case"end":return r.stop()}},r,e,[[0,7]])}))()},updateUser:function(){var e=this;return l()(a.a.mark(function r(){var t;return a.a.wrap(function(r){for(;;)switch(r.prev=r.next){case 0:return r.prev=0,r.next=3,e.$http.post("/user/update",{userId:e.userInfo.userId,userName:e.form.userName,password:e.form.password,userTrueName:e.form.userTrueName,role:e.form.role});case 3:200==(t=r.sent).data.code?(e.$message.success("编辑成功"),e.updateVisible(!1),e.$parent.queryUserAll()):e.$message.error(t.data.msg),r.next=10;break;case 7:r.prev=7,r.t0=r.catch(0),console.log(r.t0);case 10:case"end":return r.stop()}},r,e,[[0,7]])}))()}}},i={render:function(){var e=this,r=e.$createElement,t=e._self._c||r;return t("el-dialog",{staticClass:"modal",attrs:{title:e.title,visible:e.visible,width:"30%","close-on-click-modal":!1},on:{"update:visible":e.updateVisible}},[t("el-form",{ref:"form",attrs:{model:e.form,rules:e.rules,"label-position":"right","label-width":"80px"}},[t("el-form-item",{attrs:{label:"用户名",prop:"userName"}},[t("el-input",{attrs:{clearable:"",placeholder:"请输入用户名"},model:{value:e.form.userName,callback:function(r){e.$set(e.form,"userName",r)},expression:"form.userName"}})],1),e._v(" "),t("el-form-item",{attrs:{label:"密码",prop:"password"}},[t("el-input",{attrs:{"show-password":"",placeholder:"请输入密码"},model:{value:e.form.password,callback:function(r){e.$set(e.form,"password",r)},expression:"form.password"}})],1),e._v(" "),t("el-form-item",{attrs:{label:"真实姓名",prop:"userTrueName"}},[t("el-input",{attrs:{clearable:"",placeholder:"请输入用户真实姓名"},model:{value:e.form.userTrueName,callback:function(r){e.$set(e.form,"userTrueName",r)},expression:"form.userTrueName"}})],1),e._v(" "),t("el-form-item",{attrs:{label:"用户权限",prop:"role"}},[t("el-select",{staticStyle:{width:"100%"},attrs:{placeholder:"请选择用户权限"},model:{value:e.form.role,callback:function(r){e.$set(e.form,"role",r)},expression:"form.role"}},[t("el-option",{attrs:{label:"管理员",value:1}}),e._v(" "),t("el-option",{attrs:{label:"用户",value:0}})],1)],1)],1),e._v(" "),t("span",{attrs:{slot:"footer"},slot:"footer"},[t("el-button",{on:{click:function(r){return e.updateVisible(!1)}}},[e._v("取 消")]),e._v(" "),t("el-button",{attrs:{type:"primary"},on:{click:e.submitForm}},[e._v("确 定")])],1)],1)},staticRenderFns:[]};var u={name:"UserManage",components:{UserModal:t("VU/8")(n,i,!1,function(e){t("LTEO")},"data-v-3a08b7e2",null).exports},data:function(){return{tableData:[],userVisible:!1,addOrEdit:"",userInfo:{}}},created:function(){this.queryUserAll()},methods:{handleEdit:function(e){this.addOrEdit="edit",this.userInfo=e,this.userVisible=!0},addUser:function(){this.addOrEdit="add",this.userVisible=!0},queryUserAll:function(){var e=this;return l()(a.a.mark(function r(){var t;return a.a.wrap(function(r){for(;;)switch(r.prev=r.next){case 0:return r.prev=0,r.next=3,e.$http.get("/user/queryAll");case 3:200==(t=r.sent).data.code?e.tableData=t.data.data:e.$message.error(t.data.msg),r.next=10;break;case 7:r.prev=7,r.t0=r.catch(0),console.log(r.t0);case 10:case"end":return r.stop()}},r,e,[[0,7]])}))()},deleteUser:function(e){var r=this;return l()(a.a.mark(function t(){var s;return a.a.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.prev=0,t.next=3,r.$http.post("/user/delete",{userId:e.userId});case 3:200==(s=t.sent).data.code?(r.$message.success("删除用户成功"),r.queryUserAll()):r.$message.error(s.data.msg),t.next=10;break;case 7:t.prev=7,t.t0=t.catch(0),console.log(t.t0);case 10:case"end":return t.stop()}},t,r,[[0,7]])}))()}}},c={render:function(){var e=this,r=e.$createElement,t=e._self._c||r;return t("div",{staticStyle:{"text-align":"left"}},[t("el-card",{attrs:{shadow:"hover"}},[t("el-button",{staticClass:"space-button",attrs:{type:"primary",size:"medium",icon:"el-icon-plus"},on:{click:e.addUser}},[e._v("新建")]),e._v(" "),t("el-table",{attrs:{data:e.tableData,border:!0,stripe:!0}},[t("el-table-column",{attrs:{type:"index",width:"80",align:"center"}}),e._v(" "),t("el-table-column",{attrs:{prop:"userName",label:"用户名",align:"center"}}),e._v(" "),t("el-table-column",{attrs:{prop:"password",label:"密码",align:"center"}}),e._v(" "),t("el-table-column",{attrs:{prop:"userTrueName",label:"真实姓名",align:"center"}}),e._v(" "),t("el-table-column",{attrs:{prop:"role.name",label:"用户权限",align:"center"}}),e._v(" "),t("el-table-column",{attrs:{label:"操作",align:"center"},scopedSlots:e._u([{key:"default",fn:function(r){return[t("el-button",{attrs:{type:"text",size:"small"},on:{click:function(t){return e.handleEdit(r.row)}}},[e._v("编辑")]),e._v(" "),t("el-popconfirm",{attrs:{title:"确定删除吗？"},on:{confirm:function(t){return e.deleteUser(r.row)}}},[t("el-button",{attrs:{slot:"reference",type:"text",size:"small"},slot:"reference"},[e._v("删除")])],1)]}}])})],1)],1),e._v(" "),t("user-modal",{attrs:{visible:e.userVisible,addOrEdit:e.addOrEdit,userInfo:e.userInfo},on:{"update:visible":function(r){e.userVisible=r}}})],1)},staticRenderFns:[]};var d=t("VU/8")(u,c,!1,function(e){t("JgaV")},"data-v-3c872e98",null);r.default=d.exports},JgaV:function(e,r){},LTEO:function(e,r){}});