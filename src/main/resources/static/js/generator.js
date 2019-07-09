$(function () {
    $("#jqGrid").jqGrid({
        url: 'sys/generator/list',
        datatype: "json",
        colModel: [			
			{ label: '表名', name: 'tableName', width: 100, key: true },
			{ label: 'Engine', name: 'engine', width: 70},
			{ label: '表备注', name: 'tableComment', width: 100 },
			{ label: '创建时间', name: 'createTime', width: 100 }
        ],
		viewrecords: true,
        height: 385,
        rowNum: 50,
		rowList : [10,30,50,100,200],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order",
            dbHost: vm.dbHost,
            dbPort: vm.dbPort,
            dbUserName: vm.dbUserName,
            dbPassword: vm.dbPassword,
            dbName: vm.dbName
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
});

var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
			tableName: null
		},
		subSysName : "",
		basePackage : "",
		simpleName : "",
		port : "",
		dbHost : "",
		dbPort : "",
		dbUserName : "",
		dbPassword : "",
		dbName : ""
	},
	methods: {
		query: function () {
			$("#jqGrid").jqGrid('setGridParam',{ 
                postData:{
                	'tableName': vm.q.tableName,
                	'dbHost': vm.dbHost,
                	'dbPort': vm.dbPort,
                	'dbUserName': vm.dbUserName,
                	'dbPassword': vm.dbPassword,
                	'dbName': vm.dbName
                },
                page:1 
            }).trigger("reloadGrid");
		},
		generator: function() {
			var tableNames = getSelectedRows();
			if(tableNames == null){
				return ;
			}
			location.href = "sys/generator/code?tables=" + JSON.stringify(tableNames)
			+"&subSysName="+vm.subSysName 
			+ "&basePackage=" + vm.basePackage
			+ "&simpleName=" + vm.simpleName
			+ "&port=" + vm.port
			+ "&dbHost=" + vm.dbHost
			+ "&dbPort=" + vm.dbPort
			+ "&dbUserName=" + vm.dbUserName
			+ "&dbPassword=" + vm.dbPassword
			+ "&dbName=" + vm.dbName;
		}
	}
});

