<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>秒杀详情页</title>
    <%@include file="common/head.jsp" %>
</head>
<body>
<div class="container">
    <div class="panel panel-default text-center">
        <div class="panel-heading">
            <h1>${secKill.name}</h1>
        </div>
    </div>
    <div class="panel-body">
        <h2 class="text-danger text-center">
            <%--                显示time图标--%>
            <span class="glyphicon glyphicon-time"></span>
            <%--                显示倒计时--%>
            <span class="glyphicon" id="seckill-box"></span>
        </h2>
    </div>
</div>
<%--    登陆弹出框，输入电话--%>
<div id="killPhoneModal" class="modal fade">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title text-center">
                    <span class="glyphicon glyphicon-phone">秒杀电话</span>
                </h3>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-xs-8 col-xs-offset-2">
                        <input type="text" name="killPhone" id="killPhoneKey"
                               placeholder="填手机号" class="from-control"/>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <%--验证信息--%>
                <span id="killPhoneMessage" class="glyphicon"></span>
                <button type="button" id="killPhoneBtn" class="btn btn-success">
                    <span class="glyphicon glyphicon-phone"></span>
                    submit
                </button>
            </div>
        </div>
    </div>

</div>
</body>
<!-- 新 Bootstrap 核心 CSS 文件 -->
<link href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://cdn.staticfile.org/twitter-bootstrap/3.4.0/js/bootstrap.min.js"></script>
<%--jQuery cookie操作插件--%>
<script src="https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<%--jQuery countDown倒计时插件--%>
<script src="https://cdn.bootcss.com/jquery.countdown/2.2.0/jquery.countdown.min.js"></script>
<%-- 交互逻辑 --%>
<script src="/seckill/resources/script/seckill.js" type="text/javascript"></script>
<script type="text/javascript">
    $(function () {

        // 使用EL表达式传入参数
        seckill.detail.init({
            // 使用EL表达式传入参数
            seckillId: ${secKill.secKillId},
            startTime: ${secKill.startTime.time},//毫秒时间
            endTime: ${secKill.endTime.time}
        });

    });
</script>
</html>
