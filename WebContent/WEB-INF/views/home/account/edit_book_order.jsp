<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>灵悟|酒店管理系统修改预定</title>
    <meta name="Author" content="灵悟">
    <meta name="Keywords" content="灵悟">
    <meta name="Description" content="灵悟酒店管理系统">
    <link rel="stylesheet" href="../../resources/home/css/index.css">
    <link rel="stylesheet" href="../../resources/home/css/order.css">
    <link rel="stylesheet" href="../../resources/home/css/jquery-ui.min.css">
    <style>
        .edit-wrap{max-width:900px;margin:0 auto;}
        .edit-wrap h2{margin:15px 0;}
        .edit-actions{margin-top:10px;}
        .btn-primary{display:inline-block;padding:8px 18px;background:#5cb85c;color:#fff;border-radius:4px;cursor:pointer;}
        .btn-link{margin-left:12px;color:#337ab7;cursor:pointer;}
        .form-select{height:30px;min-width:220px;}
        .hint{color:#666;font-size:12px;line-height:20px;margin:8px 0;}
    </style>
</head>
<body>
<div id="c_header"></div>

<div id="section" class="edit-wrap">
    <h2>修改预定订单</h2>
    <div class="hint">
        提示：仅“预定中”的订单支持修改；若更换房型，需该房型仍有可用房间。
    </div>
    <form id="edit_order_form">
        <input type="hidden" name="id" value="${bookOrder.id}">
        <ul>
            <li>
                <h3>预定信息</h3>
                <div class="info_group">
                    <label>房型</label>
                    <select class="form-select" name="roomTypeId" id="roomTypeId">
                        <c:forEach items="${roomTypeList}" var="roomType">
                            <option value="${roomType.id}" <c:if test="${roomType.id == bookOrder.roomTypeId}">selected</c:if>>${roomType.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="info_group">
                    <label>入住时间</label><input type="text" name="arriveDate" id="arriveDate" class="datepicker" value="${bookOrder.arriveDate}"/>
                    <label>离店时间</label><input type="text" name="leaveDate" id="leaveDate" class="datepicker" value="${bookOrder.leaveDate}"/>
                </div>
            </li>
            <li>
                <h3>入住信息</h3>
                <div class="info_group">
                    <label>姓&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;名</label><input type="text" name="name" id="name" value="${bookOrder.name}"/><span class="msg"></span>
                </div>
                <div class="info_group">
                    <label>电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话</label><input type="text" maxlength="11" name="mobile" id="mobile" value="${bookOrder.mobile}"/><span class="msg"></span>
                </div>
                <div class="info_group">
                    <label>身份证号</label><input type="text" name="idCard" id="idCard" value="${bookOrder.idCard}"/><span class="msg"></span>
                </div>
                <div class="info_group">
                    <label for="remark">留&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;言</label>
                    <textarea id="remark" name="remark" style="width:200px;">${bookOrder.remark}</textarea>
                </div>
                <div class="edit-actions">
                    <span id="btn_submit" class="btn-primary">保存修改</span>
                    <span class="btn-link" onclick="window.location.href='index#order'">返回我的订单</span>
                </div>
            </li>
        </ul>
    </form>
</div>

<div id="c_footer"></div>
<script src="../../resources/home/js/jquery-1.11.3.js"></script>
<script src="../../resources/home/js/jquery-ui.min.js"></script>
<script>
    $(function() {
        $(".datepicker").datepicker({"dateFormat":"yy-mm-dd"});

        $("#btn_submit").click(function(){
            var arriveDate = $("#arriveDate").val();
            var leaveDate = $("#leaveDate").val();
            if(arriveDate == '' || leaveDate == ''){
                alert('请选择时间!');
                return;
            }
            var name = $("#name").val();
            if(name == ''){
                $("#name").next("span.msg").text('请填写入住人!');
                return;
            }
            $("#name").next("span.msg").text('');
            var mobile = $("#mobile").val();
            if(mobile == ''){
                $("#mobile").next("span.msg").text('请填写手机号!');
                return;
            }
            $("#mobile").next("span.msg").text('');
            var idCard = $("#idCard").val();
            if(idCard == ''){
                $("#idCard").next("span.msg").text('请填写身份证号!');
                return;
            }
            $("#idCard").next("span.msg").text('');

            $.ajax({
                url:'edit_book_order',
                type:'post',
                dataType:'json',
                data:$("#edit_order_form").serialize(),
                success:function(data){
                    alert(data.msg);
                    if(data.type == 'success'){
                        window.location.href = 'index#order';
                    }
                }
            });
        });
    });
</script>
</body>
</html>
