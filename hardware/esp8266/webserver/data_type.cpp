#include "data_type.h"



static char *mime_str[] = {
	"text/plain",
	"text/html",
	"text/xml",
	"text/css",
	"text/javascript",
	"image/jpeg",
  "image/jpeg",
	"image/bmp",
	"image/png"
};


//organizacja katalogów: scripts, styles, images, data, other

enum f_ext {
	D_SCRIPT = 0,
	D_STYLE,
	D_DATA,
	D_HTML,
	D_IMG,
	D_OTHER
};

#define EX_TAB_SIZE	9
static String file_ex[EX_TAB_SIZE] = { 
	"dat",
	"html",
  "xml",
	"css",
	"js",
	"jpeg",
  "jpg",
  "bmp",
	"png"
	//"gif"
};

#define FILE_DIR_SIZE	5
static char *file_dir[] = {
	"/scripts/",
	"/styles/",
	"/images/",
	"/data/",
	"/other/"
};


String getFileExtensioen(String fname)
{
  fname.toLowerCase();
  uint8_t index = 0;
  for(index; index<fname.length(); index++)
  {
    if(fname.charAt(index) == '.')
      break;
  }
  
  return fname.substring(index+1);
}

char *getDirByName(String fname)
{
	String extension = getFileExtensioen(fname);
	
	for(uint8_t i = 0; i<EX_TAB_SIZE; i++)
	{
		if(file_ex[i] == extension)
		{
			if(i >= 3 && i < 9)
				return file_dir[D_IMG];
			
			return file_dir[i];
		}
		
		if(i>= FILE_DIR_SIZE)
			return file_dir[D_OTHER];
	}
}

String getMIME(String fname)
{
  for(int i=0; i<EX_TAB_SIZE; i++)
  {
    if(fname.endsWith(file_ex[i]))
      return mime_str[i];
  }
	return mime_str[0];
}


String nameLongToShort(String long_name)
{
  String ex = getFileExtensioen(long_name);
  ex.toUpperCase();
  if(ex.length() > 2)
    ex = ex.substring(0,3);
  if(long_name.length() > 9)
  {
    long_name.toUpperCase();
    String fname = long_name;
    fname = fname.substring(0,5);
    fname.trim();
    fname += "~1.";
    fname += ex;
    return fname;
  }
  
  long_name.toUpperCase();
  return long_name;
  
}


