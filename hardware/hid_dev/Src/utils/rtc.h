#include "stm32f4xx_hal.h"
#include <stdint.h>

void RTC_init(void);
void RTC_setTime(const uint8_t hour, const uint8_t mins);
void RTC_getTime(uint8_t *hour, uint8_t *mins);
