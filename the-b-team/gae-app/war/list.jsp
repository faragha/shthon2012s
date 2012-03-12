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
	startLoad();
});
function loadImages(datas, hasMore, requestCode) {
	var imagesGrid = $("#imagesGrid");
	var lastData;
	for (var i=0;i<datas.length;i++) {
		var data = datas[i];
		lastData = data;
		var tag = $("<div>")
			.addClass("ui-block-c");
		var img = $("<div>");
		{
			var href = $("<a>")
				.attr("data-ajax", "false")
				.attr("href","${f:url('/i/')}" + data.imageId);
			$("<img>")
				.css("width", "95%")
				.css("height", "95%")
				.attr("src","${f:url('/t/')}" + data.imageId)
				.appendTo(href);
			href.appendTo(img);
			img.appendTo(tag);
		}
		var text = $("<div>");
		{
			text.addClass("imageTitle");
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
	if (lastData && hasMore) {
		var tag = $("<div>")
			.addClass("ui-block-c");
		var button = $("<button>")
			.css("width", "95%")
			.css("height", "95%")
			.text("続きを見る")
			.click(loadMore)
			.appendTo(tag);
		button.attr("updatedAt", lastData.updatedAt);
		tag.appendTo(imagesGrid);
		tag.height(tag.width());
	}
}
function loadMore() {
	$(this).parent().remove();
	var updatedAt = $(this).attr("updatedAt");
	if (updatedAt) {
		startLoad(updatedAt);
	}
}
function startLoad(lastUpdatedAt) {
	var url = "${f:url('/jsonpList?func=loadImages')}";
	if (lastUpdatedAt) {
		url = url + "&lastUpdatedAt=" + lastUpdatedAt;
	}
	$("<script>")
    .attr("type", "text/javascript")
    .attr("src", url)
    .appendTo($("head"));
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
		<ul data-role="listview" data-inset="true">
			<li data-role="list-divider">みんなのスタ☆me</li>
			<li>
				<div class="ui-grid-c" id="imagesGrid">
				</div><!-- /grid-a -->
			</li>
		</ul>
	</div>
</div>

</c:param>
</c:import>

