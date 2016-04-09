#include "stm32f4xx_hal.h"

#include <stdint.h>
#include "FreeRTOS.h"
#include "task.h"
#include "semphr.h"
#include "queue.h"

#include "delay_timer.h"
#include "ds18b20.h"
#include "adc.h"
#include "tlv.h"

#define USB_QUEUE_LENGTH 10

#define PRIORITY_BASIC 1
#define PRIORITY_MAX configMAX_PRIORITIES

//typedef struct _high_power high_power_t;

/* start and stop time hold as minutes = 60*hour[24-format]+mins */
typedef struct{
  uint16_t mStart1;
	uint16_t mStop1;
	uint16_t mStart2;
	uint16_t mStop2;
} high_power_t;

/* Avaliable TYPEs in TLV frame (first byte)
	*
	* 0x01 - SET_TEMP,	data: 0x03,0x02,0x04 //(each uint8_t); 32,4 [st.C]	(AUTO_TERM)
	* 0x02 - SET_PH,		data: 0x00,0x07,0x08 //(each uint8_t); pH 7,8 			(AUTO_CO2)
	* 0x03 - SET_OUT1,	data: 0x0000,0x0000,0x0000, 0x0000 //(each uint16_t - check endian); as in high_power_t -> make cast
	* 0x04 - SET_OUT2,	data: 0x0000,0x0000,0x0000, 0x0000 //(each uint16_t - check endian); as in high_power_t -> make cast
	* 0x05 - SET_OUT3,	data: 0x0000,0x0000,0x0000, 0x0000 //(each uint16_t - check endian); as in high_power_t -> make cast
	* 0x06 - SET_OUT4,	data: 0x0000,0x0000,0x0000, 0x0000 //(each uint16_t - check endian); as in high_power_t -> make cast
	* 0x07 - SET_LED1, TBD
	* 0x08 - SET_LED2, TBD
 */
typedef enum command_type_t
{
	SET_TEMP = 0x01,
	SET_PH,
	SET_OUT1,
	SET_OUT2,
	SET_OUT3,
	SET_OUT4,
	SET_LED1,
	SET_LED2
} command_type_t;

void tBlink_led(void * pvParameters); //uint8_t nr, uint16_t delay_ms)
void tRead_temp(void * pvParameters);
void tRead_ph(void * pvParameters);
void tCalibrate_probe(void * pvParameters);
