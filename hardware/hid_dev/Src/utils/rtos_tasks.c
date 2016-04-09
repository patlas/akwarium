#include "rtos_tasks.h"


/* data structs and semaphores */

/* start and stop time hold as minutes = 60*hour[24-format]+mins */
//struct _high_power{
//  uint16_t mStart1;
//	uint16_t mStop1;
//	uint16_t mStart2;
//	uint16_t mStop2;
//};

SemaphoreHandle_t semHighPower;
	
/* globals and queues */
QueueHandle_t usbInQueue;


/* RTOS data initializer and creator */
void RtosDataInit(void)
{
	usbInQueue = xQueueCreate(USB_QUEUE_LENGTH , TLV_STRUCT_SIZE);
	semHighPower = xSemaphoreCreateMutex();
	
	
}


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
		// send val over USB
		vTaskDelay(5000);
	}
}


void tRead_ph(void * pvParameters)
{
	uint8_t adc_read;
	
	for(;;)
	{
		if( (ADC1->SR & ADC_SR_EOC ) == ADC_SR_EOC)
		{
			// multiplied x10
			adc_read = (uint8_t)(ADC1->DR*33/4096.0f);
			//calculate pH using constans from calibraion
			// if constans = 0 zend NONE
			printf("pH = %d\r\n", adc_read);
		}
		vTaskDelay(60000);
	}
}

/* A the begining this taks is suspended. After wake up
 * get form queue? calib solution value?
 * or only get stable value and send it to android to recalculate.
 * Android replay with proper calib value to start auto CO2 or measure pH - NOT THAT APPROCHE
 */
//should have (almost?) highest priority
void tCalibrate_probe(void * pvParameters)
{
	uint16_t average_val = 0;
	uint16_t adc_read;
	uint16_t stability_index = 0;
	uint32_t calibSol = 0;
	
	for(;;)
	{
		/* Block indefinitely, after receive notification with calibSol start task */
		xTaskNotifyWait( 0x00, 0xffffffff, &calibSol, portMAX_DELAY );
		// write struct with sol -> val, sol->val used in another function to recalculate adc to pH val.
		
		for(;;)
		{
			if( (ADC1->SR & ADC_SR_EOC ) == ADC_SR_EOC)
			{
				adc_read = ADC1->DR;
				if(adc_read < (int)(average_val*1.1) && adc_read > (int)(average_val*0.9))
				{
					if(stability_index++ > 1000) //need 5s stability => 1000 * 5ms delay
					{
						adc_read = 2;
						//value stable - stop task, send info to android
						break;
					}
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
}


void tHighPowerOutput(void * pvParameters)
{
	
	for(;;)
	{
		if(xSemaphoreTake(semHighPower, portMAX_DELAY) == pdTRUE)
		{
			//recalculate current time to mins and start compare with proper time structures
			
			while(xSemaphoreGive( semHighPower ) != pdTRUE );
		}
		vTaskDelay(60000); //check output timing each 1min
	}
}

/* task controlling behaviour of LED1 output */
void tLED1(void * pvParameters)
{
	
	for(;;)
	{

		vTaskDelay(60000); //execute task each 1min
	}
}

/* task controlling behaviour of LED1 output */
void tLED2(void * pvParameters)
{
	
	for(;;)
	{

		vTaskDelay(60000); //execute task each 1min
	}
}


/* by default task is suspended - woke up after user set auto temp controll */
void tAutoTerm(void * pvParameters)
{
	
	for(;;)
	{

		vTaskDelay(60000); //check temp each 1min
	}
}

/* by default task is suspended - woke up after user set auto pH(Co2) controll */
void tAutoCO2(void * pvParameters)
{
	
	for(;;)
	{
		// check if callib structure (after callibration) is filled
		// write method to read pH (median filtering?)
		vTaskDelay(10000); //check pH each 10s
	}
	
}


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

void tController(void * pvParameters)
{
	uint8_t dataBuff[TLV_STRUCT_SIZE];
	tlv_t tlv;
	for(;;)
	{
		if( pdTRUE != xQueueReceive(usbInQueue, dataBuff, 0)) continue;
		printf("USB data received\n");
		
		ArrayToTLV(&tlv, dataBuff);
		
		switch(getTLVtype(&tlv))
		{
			case SET_TEMP:
				break;
			
			case SET_PH:
				break;
			
			case SET_OUT1:
				break;
			
			case SET_OUT2:
				break;
			
			case SET_OUT3:
				break;
			
			case SET_OUT4:
				break;
			
			case SET_LED1:
				break;
			
			case SET_LED2:
				break;
		}
		
	}
}

