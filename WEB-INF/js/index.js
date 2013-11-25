$(function(){
	$("#submit_query").click(function(){
		//搜索并刷新页面
		var keyword = $("#search_keyword").val();
		refresh(keyword, 1, 1);
	});
	
});

var refresh = function(keywords, sortby, page){
	var url="search.jsp?keyword="+encodeURI(escape(keywords));	//中文编码
	url += "&sortby="+sortby+"&page="+page+"&first=y";
	//刷新页面
	window.location.href=url;
};