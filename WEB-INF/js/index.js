$(function(){
	$("#submit_query").click(function(){
		//������ˢ��ҳ��
		var keyword = $("#search_keyword").val();
		refresh(keyword, 1, 1);
	});
	
});

var refresh = function(keywords, sortby, page){
	var url="search.jsp?keyword="+encodeURI(escape(keywords));	//���ı���
	url += "&sortby="+sortby+"&page="+page+"&first=y";
	//ˢ��ҳ��
	window.location.href=url;
};