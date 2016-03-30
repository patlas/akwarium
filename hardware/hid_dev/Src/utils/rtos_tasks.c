#include "rtos_tasks.h"

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

extern char temp[8];
void tRead_temp(void * pvParameters)
{
	for(;;)
	{
		ds18b20_readTemp();

		vTaskDelay(1000);
	}
}


void tRead_ph(void * pvParameters)
{
	for(;;)
	{
		
		
	}
}

void tCalibrate_probe(void * pvParameters)
{
	uint16_t average_val = 0;
	uint16_t adc_read;
	uint16_t stability_index = 0;
	
	for(;;)
	{
		if( (ADC1->SR & ADC_SR_EOC ) == ADC_SR_EOC)
		{
			adc_read = ADC1->DR;
			if(adc_read < (int)(average_val*1.1) && adc_read > (int)(average_val*0.9))
			{
				if(stability_index++ > 1000) //need 5s stability => 1000 * 5ms delay
					adc_read = 2;
					//value stable - stop task, send info to android
			}
			else
				stability_index = 0;

			average_val += adc_read;
			average_val /= 2;
			
			//ITM_SendChar(average_val);
			//ITM_SendChar(adc_read);
			printf("AD value = %d\r\n", average_val);
			//ADC_startConv();
		}
		vTaskDelay(5);
	}
}

