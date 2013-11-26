<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    
    <title>IDSE Settings</title>
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
	<script type="text/javascript" src="js/config.js"></script>
  </head>
  
<style type="text/css">
	table th, table td{
		text-align:left;
		padding : 5px 0px;
		font: 12px/1.125 Arial,Helvetica,sans-serif;
	}
	table th{
		width: 40%;
	}
	#log_file, #target_directory{
		width: 300px;
		length:600px;
		
	}
	.title th{
		border-bottom: solid 2px #E5E5E5;
		font-size:14px;
		font-weight:bold;
	}
	#gxszButton{
		overflow:auto;
		clear:both;
	}
	#submit{
		float:right;
	}
	#setting-title{
		color: #0078B6;
		font-family: arial,sans-serif;
		font-size: 20px;
		margin-top:35px;
		border:none;
	}
</style>
  
  <body class="w_body">
	<div class="main">
		<div class="search_result_head">
			<div class="search_logo"></div>
		
			<div id="setting-title" class="search_input">Settings</div>
		</div>
		
		<div class="s_wrap">
			<div class="search_top">
				
			</div>
			<div class="search_content" id="search_content">
				<div id="content_list">
					<form name="f2">
						<table width="98%" border="0" cellpadding="2" cellspacing="0" id="gxsz">
							<tbody>
								<tr class="title">
    								<th colspan=2>Index</th>
								</tr>
								<tr class="params" id="row-target-directory">
    								<th>Directories to index</th>
    								<td><input type="text" name="taraget_directory" id="target_directory"/></td>
								</tr>
								<tr class="params" id="row_pdf_size">
    								<th>Max size of PDF to index (KB)</th>
    								<td><input type="text" name="pdf_size" id="pdf_size"/></td>
								</tr>
								<tr class="params" id="row_txt_size">
    								<th>Max size of Txt to index (KB)</th>
    								<td><input type="text" name="txt_size" id="txt_size"/></td>
								</tr>
								<tr class="params" id="row_directory_size">
    								<th>Max size of directory to index (files)</th>
    								<td><input type="text" name="directory_size" id="directory_size"/></td>
								</tr>
								<tr class="title">
    								<th colspan=2>Knowledge Mining</th>
								</tr>
								<tr class="params" id="row_log_file">
    								<th>User activity log file</th>
    								<td><input type="text" name="log_file" id="log_file"/></td>
								</tr>
								<tr class="params" id="row_duration">
    								<th>Min duration to divide user log (s)</th>
    								<td><input type="text" name="duration" id="duration"/></td>
								</tr>
								<tr class="params" id="row_interval">
    								<th>Min interval to divide user log (s)</th>
    								<td><input type="text" name="interval" id="interval"/></td>
								</tr>
								<tr class="params" id="row_task_similarity">
    								<th>Similarity threshold to merge raw log cluster after dividing</th>
    								<td><input type="text" name="task_similarity" id="task_similarity"/></td>
								</tr>
								<tr class="params" id="row_kl">
    								<th>KL threshold for topic pruning</th>
    								<td><input type="text" name="kl" id="kl"/></td>
								</tr>
								<tr class="params" id="row_transfer_length">
    								<th>Transfer_length threshold between two files</th>
    								<td><input type="text" name="transfer_length" id="transfer_length"/></td>
								</tr>
								<tr class="params" id="row_topic_factor">
    								<th>Contribution factor of Topic Relation</th>
    								<td><input type="text" name="topic_factor" id="topic_factor"/></td>
								</tr>
								<tr class="params" id="row_task_factor">
    								<th>Contribution factor of Task Relation</th>
    								<td><input type="text" name="task_factor" id="task_factor"/></td>
								</tr>
								<tr class="params" id="row_Location_factor">
    								<th>Contribution factor of Location Relation</th>
    								<td><input type="text" name="location_factor" id="location_factor"/></td>
								</tr>
								<tr class="title">
    								<th colspan=2>Search Result</th>
								</tr>
								<tr class="params" id="row_result_number">
    								<th>Number of results returned</th>
    								<td><input type="text" name="result_number" id="result_number"/></td>
								</tr>
								<tr class="params" id="row_recommend_step">
    								<th>Recommendation step</th>
    								<td><input type="text" name="recommend_step" id="recommend_step"/></td>
								</tr>
								<tr class="params" id="row_recommend_size">
    								<th>Number of recommendation</th>
    								<td><input type="text" name="recommend_size" id="recommend_size"/></td>
								</tr>
							</tbody>
						</table>
						<div id="gxszButton"><input id="submit" type="button" onclick="save()" value="Saving"></div>
					</form>
				</div>
			</div>
		</div>
	</div>

  </body>
</html>
