<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<c:import url="/common/layout.jsp">
<c:param name="title"></c:param>
<c:param name="head">
<script type="text/javascript">
<!--
$(function() {
	$("<script>")
    .attr("type", "text/javascript")
    .attr("src", "${f:url('/jsonpList?func=loadImages')}")
    .appendTo($("head"));
});
function loadImages(datas, requestCode) {
	var imagesGrid = $("#imagesGrid");
	for (var i=0;i<datas.length;i++) {
		var data = datas[i];
		var tag = $("<div>")
			.addClass("ui-block-c");
		var img = $("<div>");
		{
			$("<img>")
				.css("width", "95%")
				.attr("src","${f:url('/i/')}" + data.imageId)
				.appendTo(img);
			img.appendTo(tag);
		}
		var text = $("<div>");
		{
			text.css("position", "relative");
			if (data.title != null) {
				text.text(data.title);
			} else {
				text.text("");
			}
			text.appendTo(tag);
		}
		
		tag.appendTo(imagesGrid);
		tag.height(tag.width());
		text.css("left", 0);
		text.css("top", -text.height());
		
	}
}
//-->
</script>
</c:param>

<c:param name="body">
<div data-role="page" class="type-index">
	<div data-role="header" data-theme="f">
		<h1><img src="${f:url('/image/logo.png')}" alt="スタ☆me"/></h1>
		<a href="${f:url('/')}" data-icon="home" data-iconpos="notext" data-direction="reverse">Home</a>
		<%--
		<a href="../nav.html" data-icon="search" data-iconpos="notext" data-rel="dialog" data-transition="fade">Search</a>
		 --%>
	</div>

	<div data-role="content">
		<div class="ui-grid-c" id="imagesGrid">
		</div><!-- /grid-a -->
	</div>
</div>

</c:param>
</c:import>

