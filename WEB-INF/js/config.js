$(document).ready(function(){
	$.ajax({
		type:"GET",
		url:"/CONFIG",
		dataType:"text",
		data:"op=load&random="+(new Date()).getTime(),
		success:Handle,
	});
});


var Handle = function(data){
//	alert(data);
	var obj = JSON.parse(data);
	$("#target_directory").attr("value", obj.target_directory);
	$("#pdf_size").attr("value", obj.pdf_size);
	$("#txt_size").attr("value", obj.txt_size);
	$("#directory_size").attr("value", obj.directory_size);
	$("#log_file").attr("value", obj.log_file);
	$("#duration").attr("value", obj.duration);
	$("#interval").attr("value", obj.interval);
	$("#task_similarity").attr("value", obj.task_similarity);
	$("#kl").attr("value", obj.kl);
	$("#transfer_length").attr("value", obj.transfer_length);
	$("#topic_factor").attr("value", obj.topic_factor);
	$("#task_factor").attr("value", obj.task_factor);
	$("#location_factor").attr("value", obj.location_factor);
	$("#result_number").attr("value", obj.result_number);
	$("#recommend_step").attr("value", obj.recommend_step);
	$("#recommend_size").attr("value", obj.recommend_size);
};

var save = function(){
	var postdata = "op=save&random="+(new Date()).getTime();
	postdata += "&target_directory="+encodeURIComponent($("#target_directory").attr("value"));
	postdata += "&pdf_size="+encodeURIComponent($("#pdf_size").attr("value"));
	postdata += "&txt_size="+encodeURIComponent($("#txt_size").attr("value"));
	postdata += "&directory_size="+encodeURIComponent($("#directory_size").attr("value"));
	postdata += "&log_file="+encodeURIComponent($("#log_file").attr("value"));
	postdata += "&duration="+encodeURIComponent($("#duration").attr("value"));
	postdata += "&interval="+encodeURIComponent($("#interval").attr("value"));
	postdata += "&task_similarity="+encodeURIComponent($("#task_similarity").attr("value"));
	postdata += "&kl="+encodeURIComponent($("#kl").attr("value"));
	postdata += "&transfer_length="+encodeURIComponent($("#transfer_length").attr("value"));
	postdata += "&topic_factor="+encodeURIComponent($("#topic_factor").attr("value"));
	postdata += "&task_factor="+encodeURIComponent($("#task_factor").attr("value"));
	postdata += "&location_factor="+encodeURIComponent($("#location_factor").attr("value"));
	postdata += "&result_number="+encodeURIComponent($("#result_number").attr("value"));
	postdata += "&recommend_step="+encodeURIComponent($("#recommend_step").attr("value"));
	postdata += "&recommend_size="+encodeURIComponent($("#recommend_size").attr("value"));
//	alert(postdata);
	$.ajax({
		type:"GET",
		url:"/CONFIG",
		dataType:"text",
		data:postdata,
		success:function(data){
			alert("Saved ! Please rebuild (update) the Index or the Knowledge Graph");
			window.location.href="config.jsp?random="+(new Date()).getTime();
		},
	});
	
}

	




