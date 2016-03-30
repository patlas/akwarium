#include "stm32f4xx_hal.h"

#include <stdint.h>
#include "FreeRTOS.h"
#include "task.h"

#include "delay_timer.h"
#include "ds18b20.h"
#include "adc.h"

void tBlink_led(void * pvParameters); //uint8_t nr, uint16_t delay_ms)
void tRead_temp(void * pvParameters);
void tRead_ph(void * pvParameters);
void tCalibrate_probe(void * pvParameters);
