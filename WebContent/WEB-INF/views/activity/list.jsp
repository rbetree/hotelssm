<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@include file="../common/header.jsp"%>
<div class="easyui-layout" data-options="fit:true">
    <!-- Begin of toolbar -->
    <div id="wu-toolbar">
        <div class="wu-toolbar-button">
            <%@include file="../common/menus.jsp"%>
        </div>
        <div class="wu-toolbar-search">
            <label>活动标题:</label><input id="search-title" class="wu-text" style="width:150px">
            <label>状态:</label>
            <select id="search-status" class="easyui-combobox" panelHeight="auto" style="width:120px">
            	<option value="-1">全部</option>
            	<option value="1">上架</option>
            	<option value="0">下架</option>
            </select>
            <a href="#" id="search-btn" class="easyui-linkbutton" iconCls="icon-search">搜索</a>
        </div>
    </div>
    <!-- End of toolbar -->
    <table id="data-datagrid" class="easyui-datagrid" toolbar="#wu-toolbar"></table>
</div>
<!-- 添加弹框 -->
<div id="add-dialog" class="easyui-dialog" data-options="closed:true,iconCls:'icon-save'" style="width:500px; padding:10px;">
	<form id="add-form" method="post">
        <table>
            <tr>
                <td align="right">活动标题:</td>
                <td><input type="text" id="add-title" name="title" class="wu-text easyui-validatebox" data-options="required:true, missingMessage:'请输入活动标题'" style="width:350px" /></td>
            </tr>
            <tr>
                <td align="right">活动内容:</td>
                <td><textarea id="add-content" name="content" rows="8" class="wu-textarea" style="width:350px" data-options="required:true"></textarea></td>
            </tr>
            <tr>
                <td align="right">开始时间:</td>
                <td><input type="text" id="add-startTime" name="startTime" class="wu-text easyui-datetimebox" style="width:350px" /></td>
            </tr>
            <tr>
                <td align="right">结束时间:</td>
                <td><input type="text" id="add-endTime" name="endTime" class="wu-text easyui-datetimebox" style="width:350px" /></td>
            </tr>
            <tr>
                <td align="right">状态:</td>
                <td>
	                <select id="add-status" name="status" class="easyui-combobox" panelHeight="auto" style="width:350px" data-options="required:true, missingMessage:'请选择状态'">
		            	<option value="1">上架</option>
		            	<option value="0">下架</option>
	            	</select>
                </td>
            </tr>
        </table>
    </form>
</div>
<!-- 修改窗口 -->
<div id="edit-dialog" class="easyui-dialog" data-options="closed:true,iconCls:'icon-save'" style="width:500px; padding:10px;">
	<form id="edit-form" method="post">
        <input type="hidden" name="id" id="edit-id">
        <table>
            <tr>
                <td align="right">活动标题:</td>
                <td><input type="text" id="edit-title" name="title" class="wu-text easyui-validatebox" data-options="required:true, missingMessage:'请输入活动标题'" style="width:350px" /></td>
            </tr>
            <tr>
                <td align="right">活动内容:</td>
                <td><textarea id="edit-content" name="content" rows="8" class="wu-textarea" style="width:350px"></textarea></td>
            </tr>
            <tr>
                <td align="right">开始时间:</td>
                <td><input type="text" id="edit-startTime" name="startTime" class="wu-text easyui-datetimebox" style="width:350px" /></td>
            </tr>
            <tr>
                <td align="right">结束时间:</td>
                <td><input type="text" id="edit-endTime" name="endTime" class="wu-text easyui-datetimebox" style="width:350px" /></td>
            </tr>
            <tr>
                <td align="right">状态:</td>
                <td>
	                <select id="edit-status" name="status" class="easyui-combobox" panelHeight="auto" style="width:350px" data-options="required:true, missingMessage:'请选择状态'">
		            	<option value="1">上架</option>
		            	<option value="0">下架</option>
	            	</select>
                </td>
            </tr>
        </table>
    </form>
</div>
<%@include file="../common/footer.jsp"%>

<!-- End of easyui-dialog -->
<script type="text/javascript">

	/**
	*  添加记录
	*/
	function add(){
		var validate = $("#add-form").form("validate");
		if(!validate){
			$.messager.alert("消息提醒","请检查你输入的数据!","warning");
			return;
		}
		var data = $("#add-form").serialize();
		$.ajax({
			url:'add',
			dataType:'json',
			type:'post',
			data:data,
			success:function(data){
				if(data.type == 'success'){
					$.messager.alert('信息提示','添加成功！','info');
					$('#add-dialog').dialog('close');
					$('#data-datagrid').datagrid('reload');
				}else{
					$.messager.alert('信息提示',data.msg,'warning');
				}
			}
		});
	}

	/**
	* 编辑记录
	*/
	function edit(){
		var validate = $("#edit-form").form("validate");
		if(!validate){
			$.messager.alert("消息提醒","请检查你输入的数据!","warning");
			return;
		}
		var data = $("#edit-form").serialize();
		$.ajax({
			url:'edit',
			dataType:'json',
			type:'post',
			data:data,
			success:function(data){
				if(data.type == 'success'){
					$.messager.alert('信息提示','修改成功！','info');
					$('#edit-dialog').dialog('close');
					$('#data-datagrid').datagrid('reload');
				}else{
					$.messager.alert('信息提示',data.msg,'warning');
				}
			}
		});
	}

	/**
	* 删除记录
	*/
	function remove(){
		$.messager.confirm('信息提示','确定要删除该记录？', function(result){
			if(result){
				var item = $('#data-datagrid').datagrid('getSelected');
				if(item == null || item.length == 0){
					$.messager.alert('信息提示','请选择要删除的数据！','info');
					return;
				}

				$.ajax({
					url:'delete',
					dataType:'json',
					type:'post',
					data:{id:item.id},
					success:function(data){
						if(data.type == 'success'){
							$.messager.alert('信息提示','删除成功！','info');
							$('#data-datagrid').datagrid('reload');
						}else{
							$.messager.alert('信息提示',data.msg,'warning');
						}
					}
				});
			}
		});
	}

	/**
	* Name 打开编辑窗口
	*/
	function openEdit(){
		var item = $('#data-datagrid').datagrid('getSelected');
		if(item == null || item.length == 0){
			$.messager.alert('信息提示','请选择要编辑的数据！','info');
			return;
		}
		$('#edit-dialog').dialog({
			closed: false,
			modal:true,
            title: "编辑活动信息",
            buttons: [{
                text: '确定',
                iconCls: 'icon-ok',
                handler: edit
            }, {
                text: '取消',
                iconCls: 'icon-cancel',
                handler: function () {
                    $('#edit-dialog').dialog('close');
                }
            }],
            onBeforeOpen:function(){
            	$("#edit-id").val(item.id);
            	$("#edit-title").val(item.title);
            	$("#edit-content").val(item.content);
            	$("#edit-startTime").datetimebox('setValue',formatDateTime(item.startTime));
            	$("#edit-endTime").datetimebox('setValue',formatDateTime(item.endTime));
            	$("#edit-status").combobox('setValue',item.status);
            }
        });
	}

	/**
	* Name 打开添加窗口
	*/
	function openAdd(){
		$('#add-dialog').dialog({
			closed: false,
			modal:true,
            title: "添加活动信息",
            buttons: [{
                text: '确定',
                iconCls: 'icon-ok',
                handler: add
            }, {
                text: '取消',
                iconCls: 'icon-cancel',
                handler: function () {
                    $('#add-dialog').dialog('close');
                }
            }],
            onBeforeOpen:function(){
            	$("#add-form input").val('');
            	$("#add-content").val('');
            }
        });
	}

	//搜索按钮监听
	$("#search-btn").click(function(){
		var option = {title:$("#search-title").val()};
		var status = $("#search-status").combobox('getValue');
		if(status != -1){
			option.status = status;
		}
		$('#data-datagrid').datagrid('reload',option);
	});

	function add0(m){return m<10?'0'+m:m }
	function format(shijianchuo){
		var time = new Date(shijianchuo);
		var y = time.getFullYear();
		var m = time.getMonth()+1;
		var d = time.getDate();
		var h = time.getHours();
		var mm = time.getMinutes();
		var s = time.getSeconds();
		return y+'-'+add0(m)+'-'+add0(d)+' '+add0(h)+':'+add0(mm)+':'+add0(s);
	}

	function formatDateTime(timestamp){
		if(!timestamp) return '';
		var time = new Date(timestamp);
		var y = time.getFullYear();
		var m = time.getMonth()+1;
		var d = time.getDate();
		var h = time.getHours();
		var mm = time.getMinutes();
		var s = time.getSeconds();
		return y+'-'+add0(m)+'-'+add0(d)+' '+add0(h)+':'+add0(mm)+':'+add0(s);
	}

	/**
	* 载入数据
	*/
	$('#data-datagrid').datagrid({
		url:'list',
		rownumbers:true,
		singleSelect:true,
		pageSize:20,
		pagination:true,
		multiSort:true,
		fitColumns:true,
		idField:'id',
	    treeField:'title',
		fit:true,
		columns:[[
			{ field:'chk',checkbox:true},
			{ field:'id',title:'ID',width:50,sortable:true},
			{ field:'title',title:'活动标题',width:200,sortable:true},
			{ field:'content',title:'活动内容',width:300},
			{ field:'startTime',title:'开始时间',width:150,formatter:function(value,row,index){
				return value ? format(value) : '-';
			}},
			{ field:'endTime',title:'结束时间',width:150,formatter:function(value,row,index){
				return value ? format(value) : '-';
			}},
			{ field:'status',title:'状态',width:80,formatter:function(value,row,index){
				return value == 1 ? '<font color="green">上架</font>' : '<font color="red">下架</font>';
			}},
			{ field:'createTime',title:'创建时间',width:150,formatter:function(value,row,index){
				return format(value);
			}}
		]]
	});

</script>
