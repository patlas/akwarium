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

char *getDirByName(String fname)
{
	fname.toLowerCase();
	uint8_t index = 0;
	for(index; index<fname.length(); index++)
	{
		if(fname.charAt(index) == '.')
			break;
	}
	
	String extension = fname.substring(index+1);
	
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

int fileIDbyName(const char* dir, const char* fname)
{
  SdFile dirFile;
  SdFile file;
  char lname[20];
  if(!dirFile.open(dir, O_READ))
  {
    Serial.print("ERROR OPENING DIR: ");
    Serial.println(dir);
    return -1;
  }
  while(file.openNext(&dirFile, O_READ))
  {
    file.getName(lname,20);
    if(strcmp(fname, lname) == 0)
    {
      file.close();
      return file.dirIndex();
    }
    file.close();
  }
  return -2;
}

