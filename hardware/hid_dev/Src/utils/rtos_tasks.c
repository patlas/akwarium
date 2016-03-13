#include "rtos_tasks.h"

#include <stdint.h>
#include "FreeRTOS.h"
#include "task.h"

void vApplicationStackOverflowHook(TaskHandle_t xTask, signed char *pcTaskName )
{
	//task stack overflow occured
}

void vApplicationMallocFailedHook( void )
{
	// memory allocation error occured - error in pvPortMalloc()
}


void tBlink_led(void * pvParameters)
{
	uint8_t nr = ((uint8_t*)pvParameters)[0];
	uint16_t delay_ms = ((uint16_t*)pvParameters)[1];
	
	for(;;)
	{
		HAL_GPIO_WritePin(GPIOD, 1<<nr, GPIO_PIN_SET);
		vTaskDelay(delay_ms);
		HAL_GPIO_WritePin(GPIOD, 1<<nr, GPIO_PIN_RESET);
		vTaskDelay(delay_ms);
	}
}
