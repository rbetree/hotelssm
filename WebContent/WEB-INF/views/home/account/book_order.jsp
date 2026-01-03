<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
  <title>山卡拉 | 酒店管理系统预定房间</title>
  <meta name="Author" content="山卡拉">
    <meta name="Keywords" content="山卡拉">
    <meta name="Description" content="山卡拉酒店管理系统">
    <link rel="stylesheet" href="../../resources/home/css/index.css">
    <link rel="stylesheet" href="../../resources/home/css/order.css">
    <link rel="stylesheet" href="../../resources/home/css/jquery-ui.min.css">
    <link rel="stylesheet" href="../../resources/home/css/sankala-theme.css">
</head>
<body>
<!--- 页头--->
<div id="c_header"></div>
<!----主体-->
<div id="section">
    <!--客房信息-->
    <div class="hotel_inf_w">

        <div class="hotel_roominfobox">
            <a href="#"><img src="${roomType.photo }" alt=""/></a>
            <div class="info">
            <h2>${roomType.name }</h2>
            <!--豪华双人房&#45;&#45;&#45;&#45;预定-->
            </div>
            <ul class="hotel_detail">
            <li><span>预定数:</span>${roomType.bookNum }</li>
            <li><span>房价:</span>${roomType.price }</li>
            <li><span>床位数:</span>${roomType.bedNum }</li>
            <li><span>可住:</span>${roomType.liveNum }人</li>
            <li><span>其他:</span>${roomType.remark }</li>
            </ul>
        </div>
        <div class="jump">
         
            <a href="../index">更多房型</a>
        </div>
    </div>
    <!--预定信息-->
    <div class="book_info">
        <form id="order_info">
            <ul>
                <input type="hidden" name="rid" value=""/>
                <li>
                    <h3>预定信息</h3>

                    <div class="info_group">
                        <label>入住时间</label><input type="text" name="arriveDate" id="arriveDate" class="datepicker"/>
						<label>离店时间</label><input type="text" name="leaveDate" id="leaveDate" class="datepicker"/>
                    </div>

                    <div class="info_group">
                        <label>房型价格</label><span class="price">￥${roomType.price }</span>
                    </div>
                    <c:if test="${not empty account}">
                    <div class="info_group">
                        <label>会员等级</label><span class="level">
                        	<c:if test="${account.level == 1}">普通会员（九折优惠）</c:if>
                        	<c:if test="${account.level == 2}">高级会员（八折优惠）</c:if>
                        	<c:if test="${account.level != 1 && account.level != 2}">普通会员</c:if>
                        </span>
                    </div>
                    <div class="info_group">
                        <label>折扣价格</label><span class="discount-price" style="color:#f00;font-weight:bold;">
                        	<c:if test="${account.level == 1}">￥${roomType.price * 0.9}</c:if>
                        	<c:if test="${account.level == 2}">￥${roomType.price * 0.8}</c:if>
                        	<c:if test="${account.level != 1 && account.level != 2}">￥${roomType.price}</c:if>
                        </span>
                    </div>
                    </c:if>
                    <c:if test="${empty account}">
                    <div class="info_group">
                        <label>会员折扣</label><span style="color:#999;">登录后享受会员折扣</span>
                    </div>
                    </c:if>
                </li>
                <li>
                    <h3>入住信息</h3>
					<div style="color:#666;margin:8px 0;font-size:12px;line-height:20px;">
						提示：未登录也可以直接预订；提交订单时系统会以手机号为用户名自动创建账号（初始密码为身份证后6位），方便在“我的订单”中取消/修改。
					</div>

                    <div class="info_group">
                        <label>姓&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;名</label><input type="text" name="name" id="name" value="${account.name}"/><span class="msg"></span>
                    </div>
                    <div class="info_group">
                        <label>电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话</label><input type="text" maxlength="11" name="mobile" id="mobile" value="${account.mobile}"/><span class="msg"></span>
                    </div>
					<div class="info_group">
                        <label>身份证号</label><input type="text" name="idCard" id="idCard" value="${account.idCard}"/><span class="msg"></span>
                    </div>
                    <div class="info_group">
                        <label for="massage">留&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;言</label>
                         <textarea id="remark" name="remark" style="width:200px;"></textarea>
                    </div>

                </li>
                <li>
                    <div class="msg">
                        预定须知:请携带好本人的身份证，办理入住手续，本店办理入住需要在前台缴费押金 ￥500

                    </div>
                    <div id="btn_booking">确认预定</div>

                </li>
            </ul>
        </form>
    </div>

    <div class="malog">
        <div class="message">
            <p class="msgs"></p>
            <p>您可以在 <a href="index#order">我的订单</a>查看详情</p>
            <p>系统会在<span class="num">5</span>秒后跳转会 <a href="../index">菜单列表</a></p>
        </div>
    </div>

</div>
<!----页底--->
<div id="c_footer"></div>
<script src="../../resources/home/js/jquery-1.11.3.js"></script>
<script src="../../resources/home/js/jquery-ui.min.js"></script>
</body>
<script>
  $(function() {
    $(".datepicker").datepicker({"dateFormat":"yy-mm-dd"});
    $("#btn_booking").click(function(){
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
    	var remark = $("#remark").val();
    	$.ajax({
    		url:'book_order',
    		type:'post',
    		dataType:'json',
    		data:{roomTypeId:'${roomType.id }',name:name,mobile:mobile,idCard:idCard,remark:remark,arriveDate:arriveDate,leaveDate:leaveDate},
    		success:function(data){
    			if(data.type == 'success'){
    				$(".malog").show();
    				setTimeout(function(){
    					window.location.href = 'index';
    				},1000)
    			}else{
    				alert(data.msg);
    			}
    		}
    	});
    })
  });
  </script>
</html>
