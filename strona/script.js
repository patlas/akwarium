
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
				response_callback(response_callback.responseText); // add parser function
				return null;
			}

//			else
//				return xmlHttp.responseText;
		}
    }

    xhtml_request.send(data);

}

function send_get_time()
{
    var xhtml_request = new XMLHttpRequest();
    xhtml_request.open("POST", "index.html", true);
    xhtml_request.setRequestHeader("Content-type", "application/json");
    var data = JSON.stringify({"method": "current_time", "args": ""}); //if args count>0 than list with args
    xhtml_request.send(data);
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

function updateValue(id, val)
{
	document.getElementById(id).innerHTML = val;
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