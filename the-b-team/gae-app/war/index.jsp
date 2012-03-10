<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>スタ☆me</title>
</head>
<body>
<h1>スタ☆me</h1>

<h2><a href="${f:url('/devel')}">開発用ページ</a></h2>
<h2><a href="${f:url('/jsonList')}">アップロードされたデータ</a></h2>

<h2>参照方法</h2>
http://******/i/[imageId]<br/>
で画像が取れます。
</body>
</html>
