#include "delay_timer.h"

TIM_HandleTypeDef htim7;

void MX_TIM7_Init(void){
	
	//TIM_HandleTypeDef htim7;
	TIM_MasterConfigTypeDef sMasterConfig;
	__TIM7_CLK_ENABLE();
	
	htim7.Instance = TIM7;
	htim7.Init.Prescaler = 40000;
	htim7.Init.ClockDivision = TIM_CLOCKDIVISION_DIV4; //jak narazie 5MHz
	htim7.Init.CounterMode = TIM_COUNTERMODE_UP;
	htim7.Init.Period = 100; // wartosc preloadu timera6
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
	NVIC_EnableIRQ(TIM7_IRQn);
}
