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
    // ��������ʽ��ǰ��ո�  
    // �ÿ��ַ������
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
 * �ж��Ƿ�Ϊ�Ǹ�����
 * @param data ����������
 * @returns
 */
var isInteger = function(data){
	var regex = /^\d+$/g;
	return regex.test(data);
};

/**
 * �滻URL�е������ַ�
 * @param data ���滻���ַ���
 * @returns �ɹ�URL�����ַ���
 */
var filterUrlSpecialChar = function(data){
	// �滻Url�е������ַ�
	/*
	Url�����ַ��������
	1. +		%2B
	2. �ո�		+����%20
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
 * ��JS�ļ��Ǹ�ʽ��JS������ʱ��Ĺ����࣬���а����˴������ڶ���Date����ʽ������Ҫ�ĸ�ʽ��<br>
 * ���ߴ����ַ�����ʽ��ʱ��������ַ������ڶ�Ӧ�ĸ�ʽ����ת��Ϊ��Ӧ�����ڶ���<br>
 * ���Լ�����������֮��Ĳ�ֵ
 * 
 * y: ��ʾ��
 * M����ʾһ���е��·� 1~12
 * d: ��ʾ�·��е����� 1~31
 * H����ʾһ���е�Сʱ�� 00~23
 * m: ��ʾСʱ�еķ����� 00~59
 * s: ��ʾ�����е�����   00~59
 */
  
  var DateFormat = function(bDebug){
      this.isDebug = bDebug || false;
      this.curDate = new Date();
  };
 
 DateFormat.prototype = {
    //����һЩ���õ����ڸ�ʽ�ĳ���
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
	 * ���ݸ����ĸ�ʽ�Ը������ַ�������ʱ����н�����
	 * @params strDate Ҫ���������ڵ��ַ�����ʾ,�˲���ֻ�����ַ�����ʽ�����ڣ����򷵻ص���ϵͳ����
	 * @params strFormat �����������ڵ�˳��, ��������strDate�ĸ�ʽΪ{Date.parse()}����֧�ֵĸ�ʽ��<br>
	 *         ����Բ����룬����һ��Ҫ������strDate��Ӧ�ĸ�ʽ, ���������ʽ�򷵻ص���ϵͳ���ڡ�
	 * @return ���ؽ������Date���͵�ʱ��<br>
	 *        �����ܽ����򷵻ص�ǰ����<br>
	 *        ������Ϊʱ���ʽ �򷵻ص�����Ϊ 1970��1��1�յ�����
	 *
	 * bug: �˷���Ŀǰֻ��ʵ������'yyyy-MM-dd'��ʽ�����ڵ�ת����<br>
	 *       ��'yyyyMMdd'��ʽ�����ڣ�����ʵ��
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
	 * ���ݸ�����ʱ�������ͼ����ֵ���Ը����ĸ�ʽ�Ը�����ʱ����м��㲢��ʽ������
	 * @params date Ҫ����������ʱ�����Ϊʱ����ַ�������{@see Date}���Ƶ�ʱ�����
	 * @params interval ʱ���������磺"YEAR"��"MONTH"�� "DATE", �����ִ�Сд
	 * @params amount ʱ����ֵ�����������͸���, ����Ϊ��date�����ڼ�ȥ��Ӧ����ֵ������Ϊ��date�������ϼ�����Ӧ����ֵ
	 * @params strFormat ������˵�date�ĸ�ʽΪ�ַ����ǣ�����������롣��date����Ϊ{@see Date}�����Ǵ������Ϊ��������ĸ�ʽ��
	 * @params targetFormat �������������ʱ��ĸ�ʽ����û��������ʹ��strFormat����Ĭ�ϸ�ʽ'yyyy-MM-dd HH:mm:ss'
	 * @return ���ؼ��㲢��ʽ�����ʱ����ַ���
	 */
	changeDate: function(date, interval, amount, strFormat, targetFormat){
		var tmpdate = new Date();
	    if(date == undefined){
		   this.debug('�����ʱ�䲻��Ϊ��!');
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
		   this.debug('�������[amount=' + amount + ']����ת��Ϊ����');
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
		      this.debug('�������[interval:' + field + '] ����������!');		
		}
		
	    this.curDate = tmpdate;
		return this.formatCurrentDate(targetFormat == undefined? strFormat: targetFormat);
	},
	
	/**
	 * �Ƚ��������ڵĲ��
	 * @param date1 Date���͵�ʱ��
	 * @param date2 Dete ���͵�ʱ�䣬Ĭ��date1����date2
	 * @return ������������֮��ĺ�����,
	 * 			��date1����date2, ����0
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
	    this.debug('�Ƚ�ʱ������쳣' + e.message);
	  }
	},
	
	/**
	 * ���ݸ��������ڵõ����ڵ��£��գ�ʱ���ֺ���Ķ���
	 * @params date ���������� dateΪ��Date���ͣ� ���ȡ��ǰ����
	 * @return �и������ڵ��¡��ա�ʱ���ֺ�����ɵĶ���
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
	 *�ڿ���̨�����־
	 *@params message Ҫ�������־��Ϣ
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
* JavaScriptʵ�ֵ�ArrayList�� 
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
				//ֻȡ��ǰֵ�������Ĳ���
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




//�Զ���ʾ��
$(function(){ 
	//ȡ��div�� 
	var $search = $('#search'); 
	//ȡ�������JQuery���� 
	var $searchInput = $search.find('#search_keyword'); 
	//�ر�������ṩ���������Զ���� 
	$searchInput.attr('autocomplete','off'); 
	//�����Զ���ɵ������б�������ʾ���������ص�����,�����������ĺ��棬����ʾ��ʱ���ٵ���λ�� 
	var $autocomplete = $('<div class="autocomplete"></div>') 
	.hide() 
	.insertAfter('#input_div'); 
	//��������б�����ݲ������������б��� 
	var clear = function(){ 
	$autocomplete.empty().hide(); 
	}; 
	//ע���¼����������ʧȥ�����ʱ����������б����� 
	$searchInput.blur(function(){ 
	setTimeout(clear,500); 
	}); 
	//�����б��и�������Ŀ������������ʾ�����б����ʱ���ƶ������߼��̵����¼��ͻ��ƶ���������Ŀ����ٶ��������� 
	var selectedItem = null; 
	//timeout��ID 
	var timeoutid = null; 
	//����������ĸ������� 
	var setSelectedItem = function(item){ 
	//������������ 
	selectedItem = item ; 
	//�����¼���ѭ����ʾ�ģ�С��0���ó�����ֵ���������ֵ���ó�0 
	if(selectedItem < 0){ 
	selectedItem = $autocomplete.find('li').length - 1; 
	} 
	else if(selectedItem > $autocomplete.find('li').length-1 ) { 
	selectedItem = 0; 
	} 
	//�����Ƴ������б���ĸ���������Ȼ���ٸ�����ǰ�����ı��� 
	$autocomplete.find('li').removeClass('highlight') 
	.eq(selectedItem).addClass('highlight'); 
	}; 
	var ajax_request = function(){ 
	//ajax�����ͨ�� 
	$.ajax({ 
	'url':'Query', //�������ĵ�ַ 
	'data':{'search-text':$searchInput.val()}, //���� 
	'dataType':'json', //������������ 
	'type':'POST', //�������� 
	'success':function(data){ 
	if(data.length) { 
	//����data����ӵ��Զ������ 
	$.each(data, function(index,term) { 
	//����li��ǩ,��ӵ������б��� 
	$('<li></li>').text(term).appendTo($autocomplete) 
	.addClass('clickable') 
	.hover(function(){ 
	//�����б�ÿһ����¼�������ƽ�ȥ�Ĳ��� 
	$(this).siblings().removeClass('highlight'); 
	$(this).addClass('highlight'); 
	selectedItem = index; 
	},function(){ 
	//�����б�ÿһ����¼�������뿪�Ĳ��� 
	$(this).removeClass('highlight'); 
	//������뿪ʱ������-1��������� 
	selectedItem = -1; 
	}) 
	.click(function(){ 
	//��굥�������б����һ��Ļ����ͽ���һ���ֵ��ӵ�������� 
	$searchInput.val(term); 
	//��ղ����������б� 
	$autocomplete.empty().hide(); 
	}); 
	});//�¼�ע����� 
	//���������б��λ�ã�Ȼ����ʾ�����б� 
	var ypos = $searchInput.position().top; 
	var xpos = $searchInput.position().left; 
	$autocomplete.css('width',$searchInput.css('width')); 
	$autocomplete.css({'position':'relative','left':xpos + "px",'top':ypos +"px", 'index-z':'10000'}); 
	setSelectedItem(0); 
	//��ʾ�����б� 
	$autocomplete.show(); 
	} 
	} 
	}); 
	}; 
	//�����������¼�ע�� 
	$searchInput 
	.keyup(function(event) { 
	//��ĸ���֣��˸񣬿ո� 
	if(event.keyCode > 40 || event.keyCode == 8 || event.keyCode ==32) { 
	//����ɾ�������б��е���Ϣ 
	$autocomplete.empty().hide(); 
	clearTimeout(timeoutid); 
	timeoutid = setTimeout(ajax_request,100); 
	} 
	else if(event.keyCode == 38){ 
	//�� 
	//selectedItem = -1 ��������뿪 
	if(selectedItem == -1){ 
	setSelectedItem($autocomplete.find('li').length-1); 
	} 
	else { 
	//������1 
	setSelectedItem(selectedItem - 1); 
	} 
	event.preventDefault(); 
	} 
	else if(event.keyCode == 40) { 
	//�� 
	//selectedItem = -1 ��������뿪 
	if(selectedItem == -1){ 
	setSelectedItem(0); 
	} 
	else { 
	//������1 
	setSelectedItem(selectedItem + 1); 
	} 
	event.preventDefault(); 
	} 
	}) 
	.keypress(function(event){ 
	//enter�� 
	if(event.keyCode == 13) { 
	//�б�Ϊ�ջ�������뿪���µ�ǰû������ֵ 
	if($autocomplete.find('li').length == 0 || selectedItem == -1) { 
	return; 
	} 
	$searchInput.val($autocomplete.find('li').eq(selectedItem).text()); 
	$autocomplete.empty().hide(); 
	event.preventDefault(); 
	} 
	}) 
	.keydown(function(event){ 
	//esc�� 
	if(event.keyCode == 27 ) { 
	$autocomplete.empty().hide(); 
	event.preventDefault(); 
	} 
	}); 
	//ע�ᴰ�ڴ�С�ı���¼������µ��������б��λ�� 
	$(window).resize(function() { 
	var ypos = $searchInput.position().top; 
	var xpos = $searchInput.position().left; 
	$autocomplete.css('width',$searchInput.css('width')); 
	$autocomplete.css({'position':'relative','left':xpos + "px",'top':ypos +"px"}); 
	}); 
	}); 