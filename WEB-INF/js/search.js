var keyword;
var sortby;
var page;

function GetQueryString(name) {
	reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)","i");
	var r = window.location.search.substr(1).match(reg);
	if (r!=null) return unescape(r[2]); return null;
}

$(document).ready(function(){
	keyword = GetQueryString("keyword");
	
	if(keyword != null)
		keyword = unescape(keyword);	//中文解码
	
	$("#search_keyword").attr("value",keyword);
	$.ajax({
		type:"GET",
		url:"/QUERY",
		dataType:"text",
		data:"keyWords="+encodeURIComponent(keyword,'UTF-8')+"&random="+(new Date()).getTime(),
		success:Handle,
	});
});


var Handle = function(data){
	data = data.trim();
//	alert(data);
	
	var obj = JSON.parse(data);
	var totalSize = obj.totalSize;
	var resultSize = obj.resultSize;
	var rsHtml = "找到"+ totalSize +"条结果";
	$("#result_num").html(rsHtml);

	rsHtml = "";
	if(resultSize == 0){
		rsHtml = "<div class='list_item'>抱歉！没有找到与"+keyword+"相关的内容，请您输入其他关键字。</div>";
		$("#content_list").html(rsHtml);
	}
	else {
		for(var i=0; i<resultSize; i++){
			var res = obj.resultList[i];
			rsHtml += reultItemHtml(i+1, res.docID, res.file, res.directory, res.recommendSize, res.recommends);
		}
		$("#content_list").html(rsHtml);
		
		pagination(pageCount, curPage);
	}

};

$(function(){
	$("#submit_query").click(function(){
		//搜索并刷新页面
		var kw = $("#search_keyword").val();
		if(kw.trim()==""){
			alert("请填写关键词");
			return;
		}
		refresh(kw, 1, 1, 'y');
	});
	
	$("#by_rel").click(function(){
		//搜索并刷新页面
		var kw = $("#search_keyword").val();
		refresh(kw, 1, 1, 'n');
	});
	
	$("#by_time").click(function(){
		//搜索并刷新页面
		var kw = $("#search_keyword").val();
		refresh(kw, 2, 1, 'n');
	});
	
	$(".open_destination_link").live("click", function(){
//		alert($(this).parent().next().html());
		$.ajax({
			type:"GET",
			url:"/OPEN",
			dataType:"text",
			data:"directory="+encodeURIComponent($(this).parent().next().html(),'UTF-8')+"&random="+(new Date()).getTime(),
			success:function(data){
				if(data.equals("false"))
					alert("目录不存在!");
			},
		});
	});
});


var reultItemHtml = function(i, docID, docName, directory, recommendSize, recommendList){
	var html =	"<div class='list_item' id='doc_" + docID + "'>";
	html += "<div class='item_body'>";
	html += "<div class='file_name'>" + i + ".<a class='open_destination_link' href='javascript:;'>" + docName + "</a></div>";
	html += "<div class='file_path'>" + directory + "</div>";
	html += "<div class='recommend_part'>";
	html += "<ol class='recommend_list'>";
	for(var i=0; i<recommendSize; i++)
		html += "<li><div class='re_file_name'><a class='open_destination_link' href='javascript:;'>"+recommendList[i].file+"</a></div><div class='re_file_path'>" + recommendList[i].directory + "</div></li>";
		
	html += "</ol>";
	html += "</div>";
	html += "</div>";
	html += "</div>";
	return html;
};

var search = function(kw){
	refresh(kw);
};

var refresh = function(keywords){
	var url="search.jsp?keyword="+encodeURI(escape(keywords));	//中文编码
	url += "&random="+(new Date()).getTime();
	//刷新页面
	window.location.href=url;
};

	




