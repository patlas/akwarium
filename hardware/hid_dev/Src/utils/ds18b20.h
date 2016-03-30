/*
 * ds18b20.h
 *
 *  Created on: 27-05-2012
 *      Author: patlas
 */

#ifndef DS18B20_H_
#define DS18B20_H_

#include "stm32f4xx_hal.h"
#include "delay_timer.h"
#include "FreeRTOS.h"
#include "task.h"

#ifdef USE_RTOS
	#define DELAY_MS(delay_ms) vTaskDelay(delay_ms)
#else
	#define DELAY_MS(delay_ms) tim7_delay(delay_ms*1000)
#endif

#define DELAY_US(delay_us) tim7_delay(delay_us)

uint8_t ds18b20_resetPulse(void);
void ds18b20_sendBit(uint8_t bit);
uint8_t ds18b20_receiveBit(void);
void ds18b20_sendByte(uint8_t data);
uint8_t ds18b20_receiveByte(void);
void ds18b20_readTemp(void);


#endif /* DS18B20_H_ */
