<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>${param.title}</title>
	<link rel="stylesheet" href="${f:url('/css/jquery.mobile-1.0.1.min.css')}" />
	<link rel="stylesheet" href="${f:url('/css/jquery.mobile.structure-1.0.1.min.css')}" />
	<link rel="stylesheet" href="${f:url('/css/global.css')}" />
	<script src="${f:url('/js/jquery-1.7.1.min.js')}"></script>
	<script src="${f:url('/js/jquery.mobile-1.0.1.js')}"></script>
	${param.head}
</head>
<body>
${param.body}
</body>
</html>
