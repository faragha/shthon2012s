<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>開発用</title>
</head>
<body>
<h1>開発用</h1>
<h2>アップロード</h2>
<form action="${f:url('/m/upload')}" method="post" enctype="multipart/form-data"/>
	<table>
		<tr>
			<td>ファイル</td>
			<td><input type="file" name="file"/></td>
		</tr>
		<tr>
			<td></td>
			<td><input type="submit"/></td>
		</tr>
	</table>
</form>
</body>
</html>
