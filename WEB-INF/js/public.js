/*********** browser judgment ************/
var Sys = {};  
var ua = navigator.userAgent.toLowerCase();  
var s;  
(s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :  
(s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :  
(s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :  
(s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :  
(s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;  

/*********** implement String trim function ************/
String.prototype.trim= function(){  
    // 用正则表达式将前后空格  
    // 用空字符串替代
    return this.replace(/(^\s*)|(\s*$)/g, "");  
};

/*********** implement String replaceAll function ************/
String.prototype.replaceAll = function(regex,replacement) { 
    return this.replace(new RegExp(regex,"gm"),replacement); 
};
        
/*********** <input> tag gray-input-tip ************/
        
function inputTipDisplay(obj){
	if(obj.value == ""){
		obj.value = "-- " + obj.title + " --";
		$(obj).css("color","#BBB");
	}
}
function inputTipDestroy(obj){
	if(obj.value == ("-- " + obj.title + " --")){
		obj.value = "";
		$(obj).css("color","#000");
	}
}

/**
 * 判断是否为非负整数
 * @param data 待检验数据
 * @returns
 */
var isInteger = function(data){
	var regex = /^\d+$/g;
	return regex.test(data);
};

/**
 * 替换URL中的特殊字符
 * @param data 带替换的字符串
 * @returns 可供URL传输字符串
 */
var filterUrlSpecialChar = function(data){
	// 替换Url中的特殊字符
	/*
	Url特殊字符及其编码
	1. +		%2B
	2. 空格		+或者%20
	3. / 		%2F
	4. ?		%3F
	5. %		%25
	6. #		%23
	7. &		%26
	8. =		%3D
	*/ 
	data = data.replace(/\%/g,"%25");
	data = data.replace(/\+/g,"%2B");
	data = data.replace(/\ /g,"%20");
	data = data.replace(/\//g,"%2F");
	data = data.replace(/\?/g,"%3F");
	data = data.replace(/\#/g,"%23");
	data = data.replace(/\&/g,"%26");
	data = data.replace(/\=/g,"%3D");
	return data;
};

/*
function isValidURL(URL){
	if(!isUrl(URL)){
		alert("illegal format");
		return false;
	}
	alert(getUrl(URL));
	res = $.ajax({
		type:'GET',
		dataType:'html',
		data:"<html></html>",
		cache:false,
		timeout:10000,
		url:getUrl(URL),
		success:function(){
			alert("OK");
			return true;
		},
		error:function(){
			alert("False");
			return false;
		}
        });
	 alert(res);
	 return res;
}
*/

/**
 * 此JS文件是格式化JS中日期时间的工具类，其中包含了传入日期对象Date，格式化成想要的格式，<br>
 * 或者传入字符串格式的时间个，次字符串日期对应的格式可以转换为相应的日期对象，<br>
 * 可以计算两个日期之间的差值
 * 
 * y: 表示年
 * M：表示一年中的月份 1~12
 * d: 表示月份中的天数 1~31
 * H：表示一天中的小时数 00~23
 * m: 表示小时中的分钟数 00~59
 * s: 表示分钟中的秒数   00~59
 */
  
  var DateFormat = function(bDebug){
      this.isDebug = bDebug || false;
      this.curDate = new Date();
  };
 
 DateFormat.prototype = {
    //定义一些常用的日期格式的常量
    DEFAULT_DATE_FORMAT: 'yyyy-MM-dd',
	DEFAULT_MONTH_FORMAT: 'yyyy-MM',
	DEFAULT_YEAR_FORMAT: 'yyyy',
	DEFAULT_TIME_FORMAT: 'HH:mm:ss',
	DEFAULT_DATETIME_FORMAT: 'yyyy-MM-dd HH:mm:ss',
    DEFAULT_YEAR: 'YEAR',
	DEFAULT_MONTH: 'MONTH',
	DEFAULT_DATE: 'DATE',
	DEFAULT_HOUR: 'HOUR',
	DEFAULT_MINUTE: 'MINUTE',
	DEFAULT_SECOND: 'SECOND',
	
	
	/**
	 * 根据给定的格式对给定的字符串日期时间进行解析，
	 * @params strDate 要解析的日期的字符串表示,此参数只能是字符串形式的日期，否则返回当期系统日期
	 * @params strFormat 解析给定日期的顺序, 如果输入的strDate的格式为{Date.parse()}方法支持的格式，<br>
	 *         则可以不传入，否则一定要传入与strDate对应的格式, 若不传入格式则返回当期系统日期。
	 * @return 返回解析后的Date类型的时间<br>
	 *        若不能解析则返回当前日期<br>
	 *        若给定为时间格式 则返回的日期为 1970年1月1日的日期
	 *
	 * bug: 此方法目前只能实现类似'yyyy-MM-dd'格式的日期的转换，<br>
	 *       而'yyyyMMdd'形式的日期，则不能实现
	 */
	 
	parseDate: function(strDate, strFormat){
		
		var regexDate = /(\d{4})[-](\d{2})[-](\d{2})\s+(\d{2})[:](\d{2})[:](\d{2})\s*/g;
		if(!regexDate.test(strDate))return null;
		if(typeof strDate != 'string'){
			return new Date();
		}
		var longTime = Date.parse(strDate);
		if(isNaN(longTime)){
			var tmpDate = new Date();
			var regFormat = /(\w{4})|(\w{2})|(\w{1})/g;
			var regDate = /(\d{4})|(\d{2})|(\d{1})/g;
			var formats = strFormat.match(regFormat);
			var dates = strDate.match(regDate);
			if( formats != undefined &&  dates != undefined && formats.length == dates.length){
				for(var i = 0; i < formats.length; i++){
					var format = formats[i];
					if(format == 'yyyy'){
						tmpDate.setFullYear(parseInt(dates[i], 10));
					}else if(format == 'MM' || format == 'M'){
						tmpDate.setMonth(parseInt(dates[i], 10) - 1);
					}else if(format == 'dd' || format == 'd'){
						tmpDate.setDate(parseInt(dates[i], 10));
					}else if(format == 'HH' || format == 'H'){
						tmpDate.setHours(parseInt(dates[i], 10));
					}else if(format == 'mm' || format == 'm'){
						tmpDate.setMinutes(parseInt(dates[i], 10));
					}else if(format == 'ss' || format == 's'){
						tmpDate.setSeconds(parseInt(dates[i], 10));
					}
				}
				return tmpDate;
			}
			return null;
	    }else{
	    	return new Date(longTime);
	    }
    },
	
	
	/**
	 * 根据给定的时间间隔类型及间隔值，以给定的格式对给定的时间进行计算并格式化返回
	 * @params date 要操作的日期时间可以为时间的字符串或者{@see Date}类似的时间对象，
	 * @params interval 时间间隔类型如："YEAR"、"MONTH"、 "DATE", 不区分大小写
	 * @params amount 时间间隔值，可以正数和负数, 负数为在date的日期减去相应的数值，正数为在date的日期上加上相应的数值
	 * @params strFormat 当输入端的date的格式为字符串是，此项必须输入。若date参数为{@see Date}类型是此项会作为最终输出的格式。
	 * @params targetFormat 最终输出的日期时间的格式，若没有输入则使用strFormat或者默认格式'yyyy-MM-dd HH:mm:ss'
	 * @return 返回计算并格式化后的时间的字符串
	 */
	changeDate: function(date, interval, amount, strFormat, targetFormat){
		var tmpdate = new Date();
	    if(date == undefined){
		   this.debug('输入的时间不能为空!');
		   return new Date();
		}else if(typeof date == 'string'){
			tmpdate = this.parseDate(date, strFormat);
		}else if(date instanceof Date){
		  tmpdate = date;
		}
		var field  =  (typeof interval == 'string')? interval.toUpperCase(): 'DATE';
		
		try{
		  amount = parseInt(amount + '', 10);
		  if(isNaN(amount)){
		     amount = 0;
		  }
		}catch(e){
		   this.debug('你输入的[amount=' + amount + ']不能转换为整数');
		   amount = 0;
		}
		switch(field){
		   case this.DEFAULT_YEAR:
		     tmpdate.setFullYear(tmpdate.getFullYear() + amount);
		     break;
		   case this.DEFAULT_MONTH:
		     tmpdate.setMonth(tmpdate.getMonth() + amount);
		     break;
		   case this.DEFAULT_DATE:
		     tmpdate.setDate(tmpdate.getDate() + amount);
		     break;
		   case this.DEFAULT_HOUR:
		     tmpdate.setHours(tmpdate.getHours() + amount);
		     break;
		   case this.DEFAULT_MINUTE:
		     tmpdate.setMinutes(tmpdate.getMinutes() + amount);
		     break;
		   case this.DEFAULT_SECOND:
		      tmpdate.setSeconds(tmpdate.getSeconds() + amount);
		     break;
		   default:
		      this.debug('你输入的[interval:' + field + '] 不符合条件!');		
		}
		
	    this.curDate = tmpdate;
		return this.formatCurrentDate(targetFormat == undefined? strFormat: targetFormat);
	},
	
	/**
	 * 比较两个日期的差距
	 * @param date1 Date类型的时间
	 * @param date2 Dete 类型的时间，默认date1晚于date2
	 * @return 返回两个日期之间的毫秒数,
	 * 			若date1早于date2, 返回0
	 */
	compareTo: function(date1, date2){
	  try{
			tmpdate1 = date1;
			tmpdate2 = date2;
			if(!(tmpdate1 instanceof Date) || !(tmpdate2 instanceof Date)){
				return 0;
			}else{
				var time1 = tmpdate1.getTime(); 
				var time2 = tmpdate2.getTime();
				var time = time1 - time2;
				if(!isNaN(time) && time > 0){
					return time;
				}else{
					return 0;
				}
			}
	  }catch(e){
	    this.debug('比较时间出现异常' + e.message);
	  }
	},
	
	/**
	 * 根据给定的日期得到日期的月，日，时，分和秒的对象
	 * @params date 给定的日期 date为非Date类型， 则获取当前日期
	 * @return 有给定日期的月、日、时、分和秒组成的对象
	 */
	getDateObject: function(date){
		 if(!(date instanceof Date)){
		   date = new Date();
		 }
		return {
			'M+' : date.getMonth() + 1, 
			'd+' : date.getDate(),   
			'H+' : date.getHours(),   
			'm+' : date.getMinutes(), 
			's+' : date.getSeconds()
		 };
	},
	
	/**
	 *在控制台输出日志
	 *@params message 要输出的日志信息
	 */
	debug: function(message){
	   try{
	       if(!this.isDebug){
		     return;
		   }
	       if(!window.console){
		       window.console = {};
		       window.console.log = function(){
		          return;
		       };
		   }
	       window.console.log(message + ' ');
	   }catch(e){
	   }
	}
};



/**********************************************************
* JavaScript实现的ArrayList类 
* 
* @author {yangl}
* @version $Revision: 0.5 $ $Date: 2008/04/02 15:00:00 $
* @description
* Method:
* add(element);
* addElementAt(index, element);
* contains(element);
* get(index);
* isEmpty(index);
* indexOf(element);
* lastIndexOf(element);
* remove()
* setElementAt(index, element);
* size();
* toString();
* @example
* var arrList = new ArrayList();
* //var arrList = new ArrayList(10);
* arrList.add("000");
* arrList.add("001");
* arrList.add("002");
*
*********************************************************/
// JavaScript ArrayList
/*
Method:
        add(element);
        addElementAt(index, element);
        contains(element);
        get(index);
        isEmpty(index);
        indexOf(element);
        lastIndexOf(element);
        remove(index);
        setElementAt(index, element);
        size();
        toString();
*/
/*
Example:
        var arrList = new ArrayList();
        //var arrList = new ArrayList(10);
        arrList.add("000");
        arrList.add("001");
        arrList.add("002");
*/
var ArrayList = function () {
    var args = ArrayList.arguments;
    var initialCapacity = 10;
    
    if (args != null && args.length > 0) {
        initialCapacity = args[0];
    }
    
    var elementData = new Array(initialCapacity);
    var elementCount = 0;
    
    this.size = function () {
        return elementCount;
    };
    
    this.add = function (element) {
        //alert("add");
        ensureCapacity(elementCount + 1);
        elementData[elementCount++] = element;
        return true;
    };
    
    this.addElementAt = function (index, element) {
        //alert("addElementAt");
        if (index > elementCount || index < 0) {
            alert("IndexOutOfBoundsException, Index: " + index + ", Size: " + elementCount);
            return;
            //throw (new Error(-1,"IndexOutOfBoundsException, Index: "+index+", Size: " + elementCount));
        }
        ensureCapacity(elementCount + 1);
        for (var i = elementCount + 1; i > index; i--) {
            elementData[i] = elementData[i - 1];
        }
        elementData[index] = element;
        elementCount++;
    };
    
    this.setElementAt = function (index, element) {
        //alert("setElementAt");
        if (index > elementCount || index < 0) {
            alert("IndexOutOfBoundsException, Index: " + index + ", Size: " + elementCount);
            return;
            //throw (new Error(-1,"IndexOutOfBoundsException, Index: "+index+", Size: " + elementCount));
        }
        elementData[index] = element;
    };
    
    this.toString = function () {
        //alert("toString()");
        var str = "{";
        for (var i = 0; i < elementCount; i++) {
            if (i > 0) {
                str += ",";
            }
            str += elementData[i];
        }
        str += "}";
        return str;
    };
    
    this.get = function (index) {
        //alert("elementAt");
        if (index >= elementCount) {
            alert("ArrayIndexOutOfBoundsException, " + index + " >= " + elementCount);
            return;
            //throw ( new Error( -1,"ArrayIndexOutOfBoundsException, " + index + " >= " + elementCount ) );
        }
        return elementData[index];
    };
    
    this.remove = function (index) {
        if (index >= elementCount) {
            alert("ArrayIndexOutOfBoundsException, " + index + " >= " + elementCount);
            //return;
            throw (new Error(-1, "ArrayIndexOutOfBoundsException, " + index + " >= " + elementCount));
        }
        var oldData = elementData[index];
        for (var i = index; i < elementCount - 1; i++) {
            elementData[i] = elementData[i + 1];
        }
        elementData[elementCount - 1] = null;
        elementCount--;
        return oldData;
    };
    
    this.removeAll = function(){
    	for (var i = 0; i < elementCount; i++) {
            elementData[i] = null;
        }
        elementCount = 0;
    };
    
    this.isEmpty = function () {
        return elementCount == 0;
    };
    
    this.indexOf = function (elem) {
        //alert("indexOf");
        for (var i = 0; i < elementCount; i++) {
            if (elementData[i] == elem) {
                return i;
            }
        }
        return -1;
    };
    
    this.lastIndexOf = function (elem) {
        for (var i = elementCount - 1; i >= 0; i--) {
            if (elementData[i] == elem) {
                return i;
            }
        }
        return -1;
    };
    
    this.contains = function (elem) {
        return this.indexOf(elem) >= 0;
    };
    
    function ensureCapacity(minCapacity) {
        var oldCapacity = elementData.length;
        if (minCapacity > oldCapacity) {
            var oldData = elementData;
            var newCapacity = parseInt((oldCapacity * 3) / 2 + 1);
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            elementData = new Array(newCapacity);
            for (var i = 0; i < oldCapacity; i++) {
                elementData[i] = oldData[i];
            }
        }
    }
};




/**********************************************************
*	UrlTools 
* 
* @author {HarryHuang}
* @version $Revision: 0.1 $ $Date: 2012/04/02 16:10:00 $
* @description
* Method:
* isUrl(url)
* getUrl(string)
* getArguments(url, urlNo)
* getQueryString(name)
*
*********************************************************/
var URLTools = {
	isUrl : function(url){
		var b="^((https|http)?://)?(([0-9]{1,3}.){3}[0-9]{1,3}|([0-9a-z_!~*'()-]+.)*([0-9a-z][0-9a-z-]{0,61})?[0-9a-z].[a-z]{2,6})(:[0-9]{1,4})?(|/|\\?|(/\\?)(.*?))?$";
		var d=new RegExp(b);
		return d.test(url);
	},
	getUrl : function(str){
		var b="^((https|http)?://){1}";
		var d=new RegExp(b);
		return d.test(str)?string:"http://"+str;
	},
	getArguments : function(url, urlNo){
		var argList = new ArrayList();
		var s1 = url.indexOf("?");
		if(s1 != -1){
			var req = url.substr(s1 + 1);
			var args = req.split("&");
			for(var i=0; i<args.length; i++){
				var strs = args[i].split("=");
				//只取当前值是整数的参数
				if(isInteger(strs[1])){
					var bean = new Object();
					bean["name"] = unescape(strs[0]);
					bean["value"] = unescape(strs[1]);
					bean["lineNo"] = urlNo;
					argList.add(bean);
				}
			}
		}
		return argList;
			
	},
	getQueryString : function(name){
		var svalue = location.search.match(new
							RegExp('[\?\&]' + name + '=([^\&]*)(\&?)','i'));
		return svalue ? unescape(svalue[1]) : svalue;
	},
	parseDate : function(date){
		date = date.trim();
		var y=0,M=0,d=1,H=0,m=0,s=0;
		var parts = date.split(" ");
		if(parts.length == 1){
			var part1 = parts[0].trim();
			var params1 = part1.split("-");
			for(var i=0; i<params1.length; i++){
				if(i==0){
					y = parseInt(params1[i].trim(),10);
				}
				else if(i==1){
					M = parseInt(params1[i].trim(),10);
				}
				else if(i==2){
					d = parseInt(params1[i].trim(),10);
				}
			}
		}
		else if(parts.length == 2){
			var part1 = parts[0].trim();
			var part2 = parts[1].trim();
			var params1 = part1.split("-");
			var params2 = part2.split(":");
			y = parseInt(params1[0].trim(),10);
			M = parseInt(params1[1].trim(),10)-1;
			d = parseInt(params1[2].trim(),10);
			H = parseInt(params2[0].trim(),10);
		}
		
		return {
			year:y,
			mon:M,
			day:d,
			hour:H,
			min:m,
			sec:s
		};
	}
};	




//自动提示框
$(function(){ 
	//取得div层 
	var $search = $('#search'); 
	//取得输入框JQuery对象 
	var $searchInput = $search.find('#search_keyword'); 
	//关闭浏览器提供给输入框的自动完成 
	$searchInput.attr('autocomplete','off'); 
	//创建自动完成的下拉列表，用于显示服务器返回的数据,插入在输入框的后面，等显示的时候再调整位置 
	var $autocomplete = $('<div class="autocomplete"></div>') 
	.hide() 
	.insertAfter('#input_div'); 
	//清空下拉列表的内容并且隐藏下拉列表区 
	var clear = function(){ 
	$autocomplete.empty().hide(); 
	}; 
	//注册事件，当输入框失去焦点的时候清空下拉列表并隐藏 
	$searchInput.blur(function(){ 
	setTimeout(clear,500); 
	}); 
	//下拉列表中高亮的项目的索引，当显示下拉列表项的时候，移动鼠标或者键盘的上下键就会移动高亮的项目，想百度搜索那样 
	var selectedItem = null; 
	//timeout的ID 
	var timeoutid = null; 
	//设置下拉项的高亮背景 
	var setSelectedItem = function(item){ 
	//更新索引变量 
	selectedItem = item ; 
	//按上下键是循环显示的，小于0就置成最大的值，大于最大值就置成0 
	if(selectedItem < 0){ 
	selectedItem = $autocomplete.find('li').length - 1; 
	} 
	else if(selectedItem > $autocomplete.find('li').length-1 ) { 
	selectedItem = 0; 
	} 
	//首先移除其他列表项的高亮背景，然后再高亮当前索引的背景 
	$autocomplete.find('li').removeClass('highlight') 
	.eq(selectedItem).addClass('highlight'); 
	}; 
	var ajax_request = function(){ 
	//ajax服务端通信 
	$.ajax({ 
	'url':'Query', //服务器的地址 
	'data':{'search-text':$searchInput.val()}, //参数 
	'dataType':'json', //返回数据类型 
	'type':'POST', //请求类型 
	'success':function(data){ 
	if(data.length) { 
	//遍历data，添加到自动完成区 
	$.each(data, function(index,term) { 
	//创建li标签,添加到下拉列表中 
	$('<li></li>').text(term).appendTo($autocomplete) 
	.addClass('clickable') 
	.hover(function(){ 
	//下拉列表每一项的事件，鼠标移进去的操作 
	$(this).siblings().removeClass('highlight'); 
	$(this).addClass('highlight'); 
	selectedItem = index; 
	},function(){ 
	//下拉列表每一项的事件，鼠标离开的操作 
	$(this).removeClass('highlight'); 
	//当鼠标离开时索引置-1，当作标记 
	selectedItem = -1; 
	}) 
	.click(function(){ 
	//鼠标单击下拉列表的这一项的话，就将这一项的值添加到输入框中 
	$searchInput.val(term); 
	//清空并隐藏下拉列表 
	$autocomplete.empty().hide(); 
	}); 
	});//事件注册完毕 
	//设置下拉列表的位置，然后显示下拉列表 
	var ypos = $searchInput.position().top; 
	var xpos = $searchInput.position().left; 
	$autocomplete.css('width',$searchInput.css('width')); 
	$autocomplete.css({'position':'relative','left':xpos + "px",'top':ypos +"px", 'index-z':'10000'}); 
	setSelectedItem(0); 
	//显示下拉列表 
	$autocomplete.show(); 
	} 
	} 
	}); 
	}; 
	//对输入框进行事件注册 
	$searchInput 
	.keyup(function(event) { 
	//字母数字，退格，空格 
	if(event.keyCode > 40 || event.keyCode == 8 || event.keyCode ==32) { 
	//首先删除下拉列表中的信息 
	$autocomplete.empty().hide(); 
	clearTimeout(timeoutid); 
	timeoutid = setTimeout(ajax_request,100); 
	} 
	else if(event.keyCode == 38){ 
	//上 
	//selectedItem = -1 代表鼠标离开 
	if(selectedItem == -1){ 
	setSelectedItem($autocomplete.find('li').length-1); 
	} 
	else { 
	//索引减1 
	setSelectedItem(selectedItem - 1); 
	} 
	event.preventDefault(); 
	} 
	else if(event.keyCode == 40) { 
	//下 
	//selectedItem = -1 代表鼠标离开 
	if(selectedItem == -1){ 
	setSelectedItem(0); 
	} 
	else { 
	//索引加1 
	setSelectedItem(selectedItem + 1); 
	} 
	event.preventDefault(); 
	} 
	}) 
	.keypress(function(event){ 
	//enter键 
	if(event.keyCode == 13) { 
	//列表为空或者鼠标离开导致当前没有索引值 
	if($autocomplete.find('li').length == 0 || selectedItem == -1) { 
	return; 
	} 
	$searchInput.val($autocomplete.find('li').eq(selectedItem).text()); 
	$autocomplete.empty().hide(); 
	event.preventDefault(); 
	} 
	}) 
	.keydown(function(event){ 
	//esc键 
	if(event.keyCode == 27 ) { 
	$autocomplete.empty().hide(); 
	event.preventDefault(); 
	} 
	}); 
	//注册窗口大小改变的事件，重新调整下拉列表的位置 
	$(window).resize(function() { 
	var ypos = $searchInput.position().top; 
	var xpos = $searchInput.position().left; 
	$autocomplete.css('width',$searchInput.css('width')); 
	$autocomplete.css({'position':'relative','left':xpos + "px",'top':ypos +"px"}); 
	}); 
	}); 