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

void turnOnHeater(bool on)
{
	if(on == true)
	{
		// TODO - turn on heater
	}
	else
	{
		// TODO - turn off heater
	}
}