#ifndef __DATA_TYPE__
#define __DATA_TYPE__



typdef enum {
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
char *getMIME(mime_t mime);


#endif