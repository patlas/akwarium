#include "delay_timer.h"

TIM_HandleTypeDef htim7;
uint32_t TIM7_CoreClock; //containc timer core clock

void MX_TIM7_Init(void){
	
	//TIM_HandleTypeDef htim7;
	TIM_MasterConfigTypeDef sMasterConfig;
	__TIM7_CLK_ENABLE();
	
	htim7.Instance = TIM7;
	htim7.Init.Prescaler = 84; // TIM7_CoreClock /(84 +1?) = 1MHz
	htim7.Init.ClockDivision = TIM_CLOCKDIVISION_DIV1; // TIM7_CoreClock
	htim7.Init.CounterMode = TIM_COUNTERMODE_UP;
	htim7.Init.Period = 5; // => 5us interrupt
	//htim7.Channel = HAL_TIM_ACTIVE_CHANNEL_1;
	
	htim7.Instance->CR1 |= TIM_CR1_CEN;
	htim7.Instance->DIER |= TIM_DIER_UIE;
	htim7.Instance->SR |= TIM_SR_UIF;
	HAL_TIM_Base_Init(&htim7);
	HAL_TIM_Base_Start(&htim7);
	HAL_TIM_Base_Start_IT(&htim7);
	
	
	sMasterConfig.MasterOutputTrigger = TIM_TRGO_ENABLE;
  sMasterConfig.MasterSlaveMode = TIM_MASTERSLAVEMODE_DISABLE;
  HAL_TIMEx_MasterConfigSynchronization(&htim7, &sMasterConfig);
	
	NVIC_ClearPendingIRQ(TIM7_IRQn);
	NVIC_SetPriority(TIM7_IRQn,4);
	//NVIC_EnableIRQ(TIM7_IRQn);
}

static volatile uint32_t tim7_tick = 0;
static uint32_t get_tim7_tick(void)
{
	return tim7_tick;
}

void tim7_inc_tick(void)
{
	tim7_tick++;
}

void tim7_delay(uint32_t _delay_us)
{
	tim7_tick=0;
	while(tim7_tick < _delay_us/5);
}

/* Override HAL_Delay to avoid SysTick implementation */
void HAL_Delay(__IO uint32_t Delay)
{
	tim7_delay(Delay*1000);
}

