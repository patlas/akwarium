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

/* sol struct */


/* RTOS data initializer and creator */
static TaskHandle_t  	tController_handle;
static TaskHandle_t  	tRead_temp_handle;
static TaskHandle_t  	tRead_ph_handle;
static TaskHandle_t  	tCalibrate_handle;
static TaskHandle_t		tAutoTerm_handle;
static TaskHandle_t		tAutoCO2_handle;
static TaskHandle_t		tNotifier_handle;

void RtosDataAndTaskInit(void)
{
	usbInQueue = xQueueCreate(USB_QUEUE_LENGTH , TLV_STRUCT_SIZE);
	semHighPower = xSemaphoreCreateMutex();

	xTaskCreate( tCalibrate_probe, "Calibrate", configMINIMAL_STACK_SIZE, NULL, PRIORITY_MAX, &tCalibrate_handle ); // higher priority
	xTaskCreate( tNotifier, "Notifier", configMINIMAL_STACK_SIZE, NULL, PRIORITY_MAX, &tNotifier_handle ); // higher priority
	
	xTaskCreate( tController, "MainController", configMINIMAL_STACK_SIZE, NULL, PRIORITY_MAX-1, &tController_handle ); //higher priority -1 like all others
	xTaskCreate( tRead_temp, "ReadTemp", configMINIMAL_STACK_SIZE, NULL, PRIORITY_MAX-1, &tRead_temp_handle );	
	xTaskCreate( tAutoTerm,	"tAutoTerm", configMINIMAL_STACK_SIZE, NULL, PRIORITY_MAX-1, &tAutoTerm_handle ); 
	xTaskCreate( tRead_ph, "ReadPH", configMINIMAL_STACK_SIZE, NULL, PRIORITY_MAX-1, &tRead_ph_handle );	
	xTaskCreate( tAutoCO2,	"tAutoCO2", configMINIMAL_STACK_SIZE, NULL, PRIORITY_MAX-1, &tAutoCO2_handle ); 
}


void vApplicationStackOverflowHook(TaskHandle_t xTask, signed char *pcTaskName )
{
	//task stack overflow occured
	printf("STACK OVERFLOW!!!");
	for(;;);
}

void vApplicationMallocFailedHook( void )
{
	// memory allocation error occured - error in pvPortMalloc()
	printf("MALLOC ERROR!!! - pvPortMalloc()");
	for(;;);
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

		//uint8_t x,y;
		//RTC_getTime(&x,&y);
		vTaskDelay(delay_ms);
	}
}

extern char temp[8];
extern uint8_t temp1, temp2;
#include "usbd_customhid.h"
extern USBD_HandleTypeDef hUsbDeviceFS;//////////////////////
void tRead_temp(void * pvParameters)
{
	for(;;)
	{
		ds18b20_readTemp();
		// send val over USB - NO! send only if ask
		vTaskDelay(10000);//5000
uint8_t testBuff[64] = {0,9,0,0,0,0,0,0,0,'p','a','t','l','a','s',',','5','\n'};
//////////////////////////
//USBD_CUSTOM_HID_SendReport(&hUsbDeviceFS, testBuff, 11);
	}
}


volatile uint8_t read_adc_ph = 0;
void tRead_ph(void * pvParameters)
{
	uint8_t adc_read;
	
	for(;;)
	{
		if( (ADC1->SR & ADC_SR_EOC ) == ADC_SR_EOC)
		{
			// multiplied x10
			adc_read = (uint8_t)(ADC1->DR*33/4096.0f);
			read_adc_ph = adc_read;
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

static sol_t calib_val = {{0,0}, {0,0}, 0};
void tCalibrate_probe(void * pvParameters)
{
	uint16_t average_val = 0;
	uint16_t adc_read;
	uint16_t stability_index = 0;
	uint32_t calibSol = 0xFFFFFFFF;
	
	for(;;)
	{
		/* Block indefinitely, after receive notification with calibSol start task */
		xTaskNotifyWait( 0x00, 0xffffffff, &calibSol, portMAX_DELAY );
		//calibSol -> 0x00xxxxxx (low) 0x10xxxxxx(high) latest 16b are calib Sol value multiplied x10 (e.g. 12.2 => 122)
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
						//adc_read = 2;
						//value stable - stop task, send info to android
						calib_val.sol_val[(calibSol>>24)&0x01] = (uint8_t)calibSol;
						calib_val.sol_adc[(calibSol>>24)&0x01] = (uint16_t)average_val;
						
						//check if both sol tested => calculate ph coefitient
						if(calib_val.sol_val[0] != 0 && calib_val.sol_val[1] !=0)
						{
							recalc_ph_coef(&calib_val);
						}
						xTaskNotify( tNotifier_handle, 0xAABBCCDD, eSetValueWithOverwrite );
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
volatile uint16_t termo_temp = 0; 
void tAutoTerm(void * pvParameters)
{
	
	for(;;)
	{
		// TODO - regular temp check (from global variable) if to low/high turn on/off heater
		if(termo_temp >= (temp1*10)+temp2)
		{
			turnOnHeater(false);
		}
		else
		{
			turnOnHeater(true);
		}
		
		vTaskDelay(60000); //check temp each 1min
	}
}

/* by default task is suspended - woke up after user set auto pH(Co2) controll */
volatile uint8_t co2_ph = 0; //multiplied x10
void tAutoCO2(void * pvParameters)
{
	
	for(;;)
	{
		// check if callib structure (after callibration) is filled
		// write method to read pH (median filtering?)
		uint8_t ph = (uint8_t)(read_adc_ph/calib_val.ph_coef);
		if(co2_ph <= ph)
		{
			turnOnCO2(false);
		}
		else
		{
			turnOnCO2(true);
		}
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
	* get PH
	* get temp
 */

void tController(void * pvParameters)
{
	uint8_t dataBuff[TLV_STRUCT_SIZE];
	tlv_t tlv;
	uint16_t temp;
	uint8_t ph, sol;
	uint32_t notify;
	for(;;)
	{
		if( pdTRUE != xQueueReceive(usbInQueue, dataBuff, 0)) continue;
		printf("USB data received\n");
		
		ArrayToTLV(&tlv, dataBuff);
		
		switch(getTLVtype(&tlv))
		{
			case SET_TEMP:
				temp = parseTemp(tlv.value);
				if(temp == 0xFFFF || temp == 0x0000)
				{
					//TODO - send info -> details in parseTemp method
					//TODO - turn off temp task -> remember to disable heater!!!
					vTaskSuspend(tAutoTerm_handle);
					turnOnHeater(false);
					printf("tController SET_TEMP: stop TERMOSTAT task.\n");
				}
				else
				{
					//TODO - turn on temp task -> turn on heater
					//TODO - after task resume immediately check temp and react!
					termo_temp = temp;
					vTaskResume(tAutoTerm_handle);
					printf("tController SET_TEMP: start TERMOSTAT task.\n");
				}
				break;
			
			case SET_PH:
				ph = parsePH(tlv.value);
				if(ph == 0xFF || ph == 0x00)
				{
					vTaskSuspend(tAutoCO2_handle);
					turnOnCO2(false);
					printf("tController SET_PH: stop AutoCO2 task.\n");
				}
				else
				{
					co2_ph = ph;
					vTaskResume(tAutoCO2_handle);
					printf("tController SET_PH: start AutoCO2 task.\n");
				}
				break;
			
			case CALLIBRATE:
				//calibSol -> 0x00xxxxxx (low) 0x01xxxxxx(high) latest 16b are calib Sol value multiplied x10 (e.g. 12.2 => 122)
				ph = parsePH(tlv.value);
				sol = tlv.value[0];
				if((ph == 0xFF || ph == 0x00) && sol>1)
				{
					printf("tController CALLIBRATE: error - wrong pH value!\n");
				}
				else
				{
					notify = ((uint32_t)sol<<24)&0x03000000;
					notify |= ph;
					xTaskNotify( tCalibrate_handle, notify, eSetValueWithOverwrite );

					printf("tController CALLIBRATE: notify=0x%x\n",notify);
				}
				break;
				
				
			default:
				printf("tController ANOTHER: %d\n",getTLVtype(&tlv));
				break;
		/*	case SET_OUT1:
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
				break;*/
		}
		
	}
}
tlv_t tlv;
void tNotifier(void * pvParameters)
{
	uint32_t notification = 0;
	union
	{
		uint8_t data[4];
		uint32_t notify;
	}cmd;
	for(;;)
	{
		xTaskNotifyWait( 0x00, 0xffffffff, &notification, portMAX_DELAY );
		cmd.notify = notification;
		buildTLVheader(&tlv, NOTIFY, cmd.data, 4);
		//buildTLVheader(&tlv, 0, cmd.data, 4);
		usb_send_tlv(&tlv);
		
		//USBD_CUSTOM_HID_SendReport(&hUsbDeviceFS, xxx, 11);
	}
}





