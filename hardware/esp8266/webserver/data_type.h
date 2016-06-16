#ifndef __DATA_TYPE__
#define __DATA_TYPE__

#include <string.h>
#include <WString.h>

typedef enum {
	text_plain = 0,
	text_html,
	text_xml,
	text_css,
	text_javascript,
	image_jpeg,
	image_bmp,
	image_png,
} mime_t;


char *getDirByName(char *fname);
String getMIME(String fname);


#endif
