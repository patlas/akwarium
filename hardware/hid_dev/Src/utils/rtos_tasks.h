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
#include "utils.h"

#include "rtc.h"

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



void RtosDataAndTaskInit(void);

void tBlink_led(void * pvParameters); //uint8_t nr, uint16_t delay_ms)
void tRead_temp(void * pvParameters);
void tRead_ph(void * pvParameters);
void tCalibrate_probe(void * pvParameters);
void tController(void * pvParameters);
void tAutoTerm(void * pvParameters);
