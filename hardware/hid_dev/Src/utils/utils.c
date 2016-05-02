#include "utils.h"


uint16_t parseTemp(uint8_t *value)
{
	uint16_t temp = 100*value[0] + 10*value[1] + value[2];
	if(temp > 360)
	{
		//send info that set temp is to high -> turning off termostat
		return 0xFFFF;
	}
	else if (temp == 0)
	{
		//user disable termostat -> send confirmation
		return 0;
	}
	else
	{
		//correct temp - set termostat -> send confirmation
		return temp;
	}
	
}


uint8_t parsePH(uint8_t *value)
{
	uint8_t ph = 10*value[1] + value[2];
	if(ph > 140)
	{
		//send info that set temp is to high -> turning off termostat
		return 0xFF;
	}
	else if (ph == 0)
	{
		//TODO - user disable co2 -> send confirmation in task!
		return 0;
	}
	else
	{
		//TODO - correct pH - set co2 -> send confirmation in task!
		return ph;
	}
}


#include "stm32f4xx_hal.h" //TODO - unnecessary in the future?
void turnOnHeater(bool on)
{
	if(on == true)
	{
		// TODO - turn on heater
		HAL_GPIO_WritePin(GPIOD, 1<<13, GPIO_PIN_SET);
	}
	else
	{
		// TODO - turn off heater
		HAL_GPIO_WritePin(GPIOD, 1<<13, GPIO_PIN_RESET);
	}
}

void turnOnCO2(bool on)
{
	if(on == true)
	{
		// TODO - turn on co2
		HAL_GPIO_WritePin(GPIOD, 1<<12, GPIO_PIN_SET);
	}
	else
	{
		// TODO - turn off co2
		HAL_GPIO_WritePin(GPIOD, 1<<12, GPIO_PIN_RESET);
	}
}

void recalc_ph_coef(sol_t *calib_struct)
{
	//TODO - recalculate ph
	//...
	calib_struct->ph_coef=1;
}

