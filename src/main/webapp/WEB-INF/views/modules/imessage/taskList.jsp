<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>发送任务管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {

        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/imessage/task/">发送任务列表</a></li>
    <li><a href="${ctx}/imessage/task/form">发送任务添加</a></li>
</ul>
<form:form id="searchForm" modelAttribute="hcTask" action="${ctx}/imessage/task/" method="post"
           class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>标题：</label>
            <form:input path="title" htmlEscape="false" maxlength="255" class="input-medium"/>
        </li>
        <li><label>类型：</label>
            <form:select path="type" class="input-medium">
                <form:option value="" label=""/>
                <form:options items="${fns:getDictList('task_type')}" itemLabel="label" itemValue="value"
                              htmlEscape="false"/>
            </form:select>
        </li>
        <li><label>状态：</label>
            <form:select path="taskStatus" class="input-medium">
                <form:option value="" label=""/>
                <form:options items="${fns:getDictList('task_state')}" itemLabel="label" itemValue="value"
                              htmlEscape="false"/>
            </form:select>
        </li>
        <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
        <li class="clearfix"></li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>标题</th>
        <th>任务类型</th>
        <th>手机号数量</th>
        <th>短信内容</th>
        <th>任务状态</th>
        <th>成功发送的个数</th>
        <th>创建人</th>
        <th>创建时间</th>
        <th>更新时间</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="hcTask">
        <tr>
            <td><a href="${ctx}/imessage/task/form?id=${hcTask.id}">
                    ${hcTask.title}
            </a></td>
            <td>
                    ${fns:getDictLabel(hcTask.type, 'task_type', '')}
            </td>
            <td>
                    ${hcTask.count}
            </td>
            <td>
                    ${hcTask.content}
            </td>
            <td>
                    ${fns:getDictLabel(hcTask.taskStatus, 'task_state', '')}
            </td>
            <td>
                    ${hcTask.successNumber}
            </td>
            <td>
                    ${hcTask.createBy.name}
            </td>
            <td>
                <fmt:formatDate value="${hcTask.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
            <td>
                <fmt:formatDate value="${hcTask.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
            <td>
                <a href="${ctx}/imessage/task/form?id=${hcTask.id}">修改</a>
                <a href="${ctx}/imessage/task/delete?id=${hcTask.id}"
                   onclick="return confirmx('确认要删除该发送任务吗？', this.href)">删除</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>