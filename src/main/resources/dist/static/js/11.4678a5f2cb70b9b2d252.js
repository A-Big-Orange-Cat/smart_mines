webpackJsonp([11],{AsEy:function(e,t){},SLDh:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r=a("Xxa5"),n=a.n(r),o=a("exGp"),l=a.n(o),c={name:"DeviceTypeManage",data:function(){return{formInfo:{name:"",type:""},typeList:[{label:"OPC",value:1},{label:"Modbus TCP",value:2},{label:"S7",value:3}],tableData:[],curPage:1}},created:function(){this.getDeviceTypeAll()},methods:{submitQuery:function(){this.queryByNameOrProtocol()},resetForm:function(){this.$refs.form.resetFields()},handleCurrentChange:function(e){console.log("当前页: "+e)},handleParam:function(e){this.$router.push({name:"device_param_manage",params:{deviceTypeId:e.deviceTypeId,deviceTypeName:e.deviceTypeName}})},getDeviceTypeAll:function(){var e=this;return l()(n.a.mark(function t(){var a;return n.a.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.prev=0,t.next=3,e.$http.get("/deviceType/queryAll");case 3:200==(a=t.sent).data.code?e.tableData=a.data.data:e.$message.error(a.data.msg),t.next=10;break;case 7:t.prev=7,t.t0=t.catch(0),console.log(t.t0);case 10:case"end":return t.stop()}},t,e,[[0,7]])}))()},queryByNameOrProtocol:function(){var e=this;return l()(n.a.mark(function t(){var a;return n.a.wrap(function(t){for(;;)switch(t.prev=t.next){case 0:return t.prev=0,t.next=3,e.$http.post("/deviceType/queryByNameOrProtocol",{deviceTypeName:e.formInfo.name,communicationProtocol:e.formInfo.type});case 3:a=t.sent,e.tableData=a.data.data,t.next=10;break;case 7:t.prev=7,t.t0=t.catch(0),console.log(t.t0);case 10:case"end":return t.stop()}},t,e,[[0,7]])}))()}}},s={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",[a("div",{staticClass:"box-card"},[a("el-form",{ref:"form",staticClass:"form-inline",attrs:{inline:!0,model:e.formInfo}},[a("el-form-item",{attrs:{label:"设备类型名称",prop:"name"}},[a("el-input",{attrs:{placeholder:"请输入设备类型名称"},model:{value:e.formInfo.name,callback:function(t){e.$set(e.formInfo,"name",t)},expression:"formInfo.name"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"通讯协议",prop:"type"}},[a("el-select",{attrs:{clearable:"",placeholder:"请选择通讯协议"},model:{value:e.formInfo.type,callback:function(t){e.$set(e.formInfo,"type",t)},expression:"formInfo.type"}},e._l(e.typeList,function(e,t){return a("el-option",{key:t,attrs:{label:e.label,value:e.value}})}),1)],1),e._v(" "),a("el-form-item",[a("el-button",{attrs:{type:"primary",size:"medium"},on:{click:e.submitQuery}},[e._v("查询")]),e._v(" "),a("el-button",{attrs:{size:"medium"},on:{click:e.resetForm}},[e._v("重置")])],1)],1)],1),e._v(" "),a("div",{staticClass:"box-table"},[a("el-table",{attrs:{data:e.tableData,border:!0,stripe:!0}},[a("el-table-column",{attrs:{type:"index",width:"80",align:"center"}}),e._v(" "),a("el-table-column",{attrs:{prop:"deviceTypeName",label:"设备类型名称",align:"center"}}),e._v(" "),a("el-table-column",{attrs:{prop:"communicationProtocol.name",label:"通讯协议",align:"center"}}),e._v(" "),a("el-table-column",{attrs:{label:"设备类型参数",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[7==t.row.deviceTypeId?a("el-button",{attrs:{type:"primary",plain:"",size:"small"},on:{click:function(a){return e.handleParam(t.row)}}},[e._v("设置")]):a("span",[e._v("-")])]}}])}),e._v(" "),a("el-table-column",{attrs:{prop:"sortNumber",label:"排序序号",align:"center"}})],1)],1)])},staticRenderFns:[]};var i=a("VU/8")(c,s,!1,function(e){a("AsEy")},"data-v-340d75eb",null);t.default=i.exports}});