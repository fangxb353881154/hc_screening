<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出客户信息吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/crm/crmInfo/export");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});

			$("#importBtn").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true},
					bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"});
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
<div id="importBox" class="hide">
	<form id="importForm" action="${ctx}/crm/crmInfo/import" method="post" enctype="multipart/form-data"
		  class="form-search" style="padding-left:20px;text-align:center;" onsubmit="loading('正在导入，请稍等...');"><br/>
		<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
		<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
		<a href="${ctx}/crm/crmInfo/import/template">下载模板</a>
	</form>
</div>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/crm/crmInfo/">客户信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="crmInfo" action="${ctx}/crm/crmInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>客户名称：</label>
				<form:input path="custname" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>手机号：</label>
				<form:input path="mobile" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="clearfix"></li>
			<li><label>身份证号：</label>
				<form:input path="idCard" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>查询结果：</label>
				<form:input path="results" htmlEscape="false" maxlength="2000" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				&nbsp;&nbsp;
				<input id="importBtn" class="btn btn-primary" type="button" value="导  入"/>
				&nbsp;&nbsp;
				<input id="btnExport" class="btn btn-primary" type="button" value="导  出"/>
				&nbsp;&nbsp;
				<input class="btn btn-success" type="button" value="开始扫描"
					onclick="return confirmx('每次扫描最高1W条数据，扫描期间请勿重复提交！<br> <br>确定开始扫描数据？','${ctx}/crm/crmInfo/validateAndSave');" />
				&nbsp;&nbsp;
				<input type="button" class="btn btn-warning" value="一键清空" id="btnDeleteAll" onclick="return confirmx('确认要清空所有客户信息吗？', '${ctx}/crm/crmInfo/deleteAll');"/>
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>客户名称</th>
				<th>手机号</th>
				<th>身份证号</th>
				<th>查询结果</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>备注</th>
				<shiro:hasPermission name="crm:crmInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="crmInfo">
			<tr>
				<td><a href="${ctx}/crm/crmInfo/form?id=${crmInfo.id}">
					${crmInfo.custname}
				</a></td>
				<td>
					${crmInfo.mobile}
				</td>
				<td>
					${crmInfo.idCard}
				</td>
				<td>
					${crmInfo.results}
				</td>
				<td>
					${crmInfo.createBy.id}
				</td>
				<td>
					<fmt:formatDate value="${crmInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${crmInfo.remark}
				</td>
				<shiro:hasPermission name="crm:crmInfo:edit"><td>
					<a href="${ctx}/crm/crmInfo/delete?id=${crmInfo.id}" onclick="return confirmx('确认要删除该客户信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>