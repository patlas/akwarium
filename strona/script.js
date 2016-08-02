function httpGetAsync(theUrl, callback)
{
    /*var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() { 
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
		{
			if(callback != null)
			{
				callback(xmlHttp.responseText);
				return null;
			}
				
			else
				return xmlHttp.responseText;
		}
    }
    xmlHttp.open("GET", theUrl, true); // true for asynchronous 
    xmlHttp.send(null);*/
	var str = theUrl;
	str = str.replace('?','');
	return getDemo(str);
}

function getDemo(parameter)
{
	txt = "<note><temp>27.7</temp> <temp_term>12.0</temp_term> <ph>6.5</ph></note>"
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