/* Includes ------------------------------------------------------------------*/
#include "stm32f4xx_hal.h"
#include "usb_device.h"
#include "gpio.h"
#include "usbd_customhid.h"

/* Kernel includes. */
#include "FreeRTOS.h"
#include "task.h"
#include "timers.h"
#include "semphr.h"

/* Custome indudes */
#include "rtos_tasks.h"
#include "delay_timer.h"
#include "adc.h"
#include "rtc.h"

/* Private function prototypes -----------------------------------------------*/
void SystemClock_Config(void);

extern USBD_HandleTypeDef  *hUsbDevice_0;
extern uint32_t TIM7_CoreClock;
extern SemaphoreHandle_t semHighPower;
uint16_t a[] = {15,500};
uint16_t b[] = {14, 800}; 

int main(void)
{

  //HAL_Init();

  SystemClock_Config();
	HAL_NVIC_SetPriorityGrouping(NVIC_PRIORITYGROUP_4);
	NVIC_EnableIRQ(TIM7_IRQn);

	MX_TIM7_Init();
  MX_GPIO_Init();
  MX_USB_DEVICE_Init();
	NVIC_SetPriority(OTG_FS_IRQn, 5); //set USB interrupt priority to deal with freeRTOS 
	ADC_init();
	RTC_init();
	ADC_startConv();//////////NVIC_DisableIRQ(SysTick_IRQn)
	
	/* semaphore creation */
	//semHighPower = xSemaphoreCreateMutex();
	RtosDataAndTaskInit();
	/* task creation */
	//xTaskCreate( tBlink_led, "led1", configMINIMAL_STACK_SIZE, &a, 1, NULL );
	xTaskCreate( tBlink_led, "led2", configMINIMAL_STACK_SIZE, &a, PRIORITY_MAX-1, NULL );
//	xTaskCreate( tRead_temp, "temp", configMINIMAL_STACK_SIZE, NULL, 1, NULL );
//	xTaskCreate( tCalibrate_probe, "ph", configMINIMAL_STACK_SIZE, NULL, 2, NULL ); 
//	xTaskCreate( tController, "controller", configMINIMAL_STACK_SIZE, NULL, 1, NULL );
	RTC_setTime(18,20);

	
	vTaskStartScheduler();
	for(;;);

//uint8_t testBuff[64] = {0,9,0,0,0,0,0,0,0,'p','a','t','l','a','s',',','5','\n'};
//  while (1)
//  {
//		if(HAL_GPIO_ReadPin(GPIOA, GPIO_PIN_0) == GPIO_PIN_SET)
//		{
//			HAL_GPIO_WritePin(GPIOD, GPIO_PIN_15, GPIO_PIN_SET);
//			HAL_Delay(200);
//			HAL_GPIO_WritePin(GPIOD, GPIO_PIN_15, GPIO_PIN_RESET);
//			USBD_CUSTOM_HID_SendReport(hUsbDevice_0, testBuff, 64);
//		}
//		
//	}  

}


void SystemClock_Config(void)
{

  RCC_OscInitTypeDef RCC_OscInitStruct;
  RCC_ClkInitTypeDef RCC_ClkInitStruct;
	
  __PWR_CLK_ENABLE();

  __HAL_PWR_VOLTAGESCALING_CONFIG(PWR_REGULATOR_VOLTAGE_SCALE1);

  RCC_OscInitStruct.OscillatorType = RCC_OSCILLATORTYPE_HSE | RCC_OSCILLATORTYPE_LSI;
  RCC_OscInitStruct.HSEState = RCC_HSE_ON;
  RCC_OscInitStruct.PLL.PLLState = RCC_PLL_ON;
  RCC_OscInitStruct.PLL.PLLSource = RCC_PLLSOURCE_HSE;
  RCC_OscInitStruct.PLL.PLLM = 8;
  RCC_OscInitStruct.PLL.PLLN = 336;
  RCC_OscInitStruct.PLL.PLLP = RCC_PLLP_DIV2;
  RCC_OscInitStruct.PLL.PLLQ = 7;
	
	RCC_OscInitStruct.LSEState = RCC_LSI_ON;
	
  HAL_RCC_OscConfig(&RCC_OscInitStruct);

  RCC_ClkInitStruct.ClockType = RCC_CLOCKTYPE_SYSCLK|RCC_CLOCKTYPE_PCLK1
                              |RCC_CLOCKTYPE_PCLK2;
  RCC_ClkInitStruct.SYSCLKSource = RCC_SYSCLKSOURCE_PLLCLK;
  RCC_ClkInitStruct.AHBCLKDivider = RCC_SYSCLK_DIV1;
  RCC_ClkInitStruct.APB1CLKDivider = RCC_HCLK_DIV4;
  RCC_ClkInitStruct.APB2CLKDivider = RCC_HCLK_DIV2;
  HAL_RCC_ClockConfig(&RCC_ClkInitStruct, FLASH_LATENCY_5);
	
	RCC_PeriphCLKInitTypeDef PeriphClkInitStruct;
	PeriphClkInitStruct.PeriphClockSelection = RCC_PERIPHCLK_RTC;
  PeriphClkInitStruct.RTCClockSelection = RCC_RTCCLKSOURCE_LSI;
  HAL_RCCEx_PeriphCLKConfig(&PeriphClkInitStruct);

  //HAL_SYSTICK_Config(HAL_RCC_GetHCLKFreq()/1000);

  //HAL_SYSTICK_CLKSourceConfig(SYSTICK_CLKSOURCE_HCLK);

	//NVIC_DisableIRQ(SysTick_IRQn);
	
	/* Disable SysTick -> will be enabled by RTOS scheduler */
	SysTick->CTRL = 0;
	
  /* Calculate TIM7 clock value */
	TIM7_CoreClock = HAL_RCC_GetPCLK1Freq();
	if(RCC_ClkInitStruct.APB1CLKDivider > RCC_HCLK_DIV1)
		TIM7_CoreClock = HAL_RCC_GetPCLK1Freq()<<1;
	 
	 
  /* SysTick_IRQn interrupt configuration */
  //HAL_NVIC_SetPriority(SysTick_IRQn, 0, 0);
}



#ifdef USE_FULL_ASSERT


void assert_failed(uint8_t* file, uint32_t line)
{

}

#endif
