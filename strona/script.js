var hour;
var mins;
var secs;
var day;
var month;
var year;


// server side function name with ist args
function _call_function(func_name, args_list, response_callback)
{

    var xhtml_request = new XMLHttpRequest();
    xhtml_request.open("POST", "index.html", true);
    xhtml_request.setRequestHeader("Content-type", "application/json");

    var data = JSON.stringify({"method": func_name, "args": args_list}); //if args count>0 than list with args

    xhtml_request.onreadystatechange = function() {

        if (xhtml_request.readyState == 4 && xhtml_request.status == 200)
		{
			if(response_callback != null)
			{
//			    window.alert(xhtml_request.responseText);
			    resp = JSON.parse(xhtml_request.responseText);
//			    window.alert(resp.time);
				response_callback(resp); // add parser function
				return null;
			}

			else
				return null;
		}
    }

    xhtml_request.send(data);
}

function updateValue(id, val)
{
	document.getElementById(id).innerHTML = val;
}


function send_get_time()
{
    _call_function("get_time", [], function(arg) {
//        window.alert(arg.date);

        hour = arg.time['hour'];
        mins = arg.time['mins'];
        secs = arg.time['secs'];

        day = arg.date['day'];
        month = arg.date['month'];
        year = arg.date['year'];

//        updateValue("ctl_time", ""+hour+":"+mins+":"+secs);
//        updateValue("ctl_date", ""+day+"-"+month+"-"+year);
////
//        document.getElementById("ctl_time").innerHTML = ""+arg.time['hour'];//arg.time;
//        document.getElementById("ctl_date").innerHTML = ""+arg.data['year'];
    })
}



function on_page_load()
{
    send_get_time();
}


function getDemo(parameter)
{
	txt = "<note><temp>27.5</temp> <temp_term>12.0</temp_term> <ph>6.5</ph></note>"
	parser = new DOMParser();
	xmlDoc = parser.parseFromString(txt, "text/xml");
	return xmlDoc.getElementsByTagName(parameter)[0].childNodes[0].nodeValue;
}

function httpSetAsync(parameter, val)
{
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open("GET", "/"+parameter+"="+val, true); // true for asynchronous 
    xmlHttp.send(null);
}




function date_time_refresher()
{
    secs++;
    if(secs >= 60)
    {
        secs = 0;
        mins++;
    }

    if(mins >= 60)
    {
        send_get_time();
    }
    var shour = ("0" + hour).slice(-2);
    var smins = ("0" + mins).slice(-2);
    var ssecs = ("0" + secs).slice(-2);

    updateValue("ctl_time", ""+shour+":"+smins+":"+ssecs);
    updateValue("ctl_date", ""+day+"-"+month+"-"+year);
}



function settingsTemp(temp,enable)
{
	var t = temp.split('.');
	var temph = parseInt(t[0]);
	var templ = 0;
	if(t.length > 1)
		templ = parseInt(t[1]);
	
	var indexh = temph-20;
	var indexl = templ;
	var indexe = enable;
	
	var temp_h = document.getElementById("temp_h");
	var temp_l = document.getElementById("temp_l");
	temp_h.options[indexh].setAttribute("selected","selected");
	temp_l.options[indexl].setAttribute("selected","selected");
	
	var temp_en = document.getElementById("temp_en");
	temp_en.options[indexe].setAttribute("selected","selected");
}

function settingsPh(ph,enable)
{
	var p = ph.split('.');
	var phh = parseInt(p[0]);
	var phl = 0;
	if(p.length > 1)
	{
		if(parseInt(p[1]) > 4)
			phl = 1;
		else
			phl = 0;
	}
	
	var indexh = phh-5;
	var indexl = phl;
	var indexe = enable;
	
	var ph_h = document.getElementById("ph_h");
	var ph_l = document.getElementById("ph_l");
	ph_h.options[indexh].setAttribute("selected","selected");
	ph_l.options[indexl].setAttribute("selected","selected");
	
	var ph_en = document.getElementById("ph_en");
	ph_en.options[indexe].setAttribute("selected","selected");
}