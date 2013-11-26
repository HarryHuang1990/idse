<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    
    <title>IDSE</title>
    <meta http-equiv="Content-Type" content="text/html; charset=GBK" />
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">

	<link rel="stylesheet" type="text/css" href="css/styles.css">
	
	<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
	<script type="text/javascript" src="js/json2.js"></script>
	<script type="text/javascript" src="js/public.js"></script>

	<script type="text/javascript" src="js/search.js"></script>

  </head>
  
  <body class="w_body">
	<div class="main">
		<div class="search_result_head">
			<div class="search_logo"></div>
		
			<div class="search_input">
				<div class="search_input_wrap">
					<div class="searchBtn_box">
						<a class="searchBtn" id="submit_query" href="javascript:;">search</a>
					</div>
					<div class="searchInp_box" id="input_div">
						<div class="searchInp_auto"  id="search">
							<input type="text" id="search_keyword" class="searchInp_form" maxlength="40">
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="s_wrap">
			<div class="search_top">
				<div class="top_num" id="result_num">灉</div>
			</div>
			<div class="search_content" id="search_content">
				<div id="content_list">
					<div class="list_item">
						<img src="pictures/loading.gif">
					</div>
 
					<div class="list_item" onmouseover="showWeibox(weiboid)" onmouseout="destoryWeibox()">
						<div class="item_body">
							<div class="file_name"><a class="open_destination_link" href="javascript:;">aaa</a></div>
							<div class="file_path">//aaa</div>
							<div class="recommend_part">
								<ol class="recommend_list">
									<li><div class="re_file_name"><a class="open_destination_link" href="javascript:;">aaa</a></div><div class="re_file_path">/aaa</div></li>
									<li><div class="re_file_name"><a class="open_destination_link" href="javascript:;">aa</a></div><div class="re_file_path">//aaa</div></li>
									<li><div class="re_file_name"><a class="open_destination_link" href="javascript:;">aaa</a></div><div class="re_file_path">aaa</div></li>
									<li><div class="re_file_name"><a class="open_destination_link" href="javascript:;">aaa</a></div><div class="re_file_path">aaa</div></li>
									<li><div class="re_file_name"><a class="open_destination_link" href="javascript:;">aaa</a></div><div class="re_file_path">aaa</div></li> 
								</ol>
							</div>
						</div>
					</div>
				
				</div>
			</div>
		</div>
	</div>

  </body>
</html>
