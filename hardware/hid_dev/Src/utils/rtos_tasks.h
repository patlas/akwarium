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


void RtosDataAndTaskInit(void);

void tBlink_led(void * pvParameters); //uint8_t nr, uint16_t delay_ms)
void tRead_temp(void * pvParameters);
void tRead_ph(void * pvParameters);
void tCalibrate_probe(void * pvParameters);
void tController(void * pvParameters);
void tAutoTerm(void * pvParameters);
void tAutoCO2(void * pvParameters);
