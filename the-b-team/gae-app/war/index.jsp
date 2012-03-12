<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<c:import url="/common/layout.jsp">
<c:param name="title"></c:param>
<c:param name="body">
<div data-role="page" class="type-index">
	<div data-role="header" data-theme="f">
		<h1><img src="${f:url('/image/logo.png')}" alt="スタ☆me"/></h1>
		<%--
		<a href="../../" data-icon="home" data-iconpos="notext" data-direction="reverse">Home</a>
		<a href="../nav.html" data-icon="search" data-iconpos="notext" data-rel="dialog" data-transition="fade">Search</a>
		 --%>
	</div>

	<div data-role="content">
		<ul data-role="listview" data-inset="true">
			<li data-role="list-divider">みんなのスタ☆me</li>
			<li><a href="${f:url('/list')}" data-ajax="false">見る</a></li>
			<%--
			<li data-role="list-divider">開発用</li>
			<li><a href="${f:url('/devel')}" data-ajax="false">開発用ページ</a></li>
			<li><a href="${f:url('/jsonList')}" data-ajax="false">アップロードされたデータ</a><li>
			 --%>
		</ul>
	</div>
</div>

</c:param>
</c:import>

