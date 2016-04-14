#include "rtc.h"

static inline void enter_write_mode(void)
{
	// enable write mode
	RTC->WPR = 0xCA;
	RTC->WPR = 0x53;
	
	RTC->ISR |= RTC_ISR_INIT;
	// wait until INITF flag will be set
	while(!(RTC->ISR & RTC_ISR_INITF));
}

static inline void exit_write_mode(void)
{
	RTC->ISR &= ~RTC_ISR_INIT;
	RTC->WPR = 0xFF;
}

static inline void enable_lsi_clk(void)
{
	RCC->CSR |= RCC_CSR_LSION;
	while(!(RCC->CSR & RCC_CSR_LSIRDY));
}

void RTC_init(void)
{
	__HAL_RCC_RTC_ENABLE();
	
	enable_lsi_clk();
	enter_write_mode();
	// use LSI = 32kHz to generate 1Hz clock
	RTC->PRER = 249; // check value
	RTC->PRER |= (127<<16);
	// set example time 12:34 in 24h mode
	RTC->TR = 0x0000;
	RTC->TR &= ~RTC_TR_PM;
	RTC->TR |= (uint32_t)(1<<20 | 2<<16 | 3<<12 | 4<<8);
	
	//set 24h format
	RTC->CR &= ~RTC_CR_FMT;
	
	exit_write_mode();
}

//TODO - write set time fuction - remember about write enable/disable
void RTC_setTime(const uint8_t hour, const uint8_t mins)
{
	enter_write_mode();
	RTC->TR = (uint32_t)((hour/10)<<20 | (hour%10)<<16 | (mins/10)<<12 | (mins%10)<<8);
	exit_write_mode();
}

void RTC_getTime(uint8_t *hour, uint8_t *mins)
{
	uint32_t time_reg = RTC->TR;
	*hour = 10*((time_reg>>20)&0x03) + ((time_reg>>16)&0x0F);
	*mins = 10*((time_reg>>12)&0x07) + ((time_reg>>8)&0x0F);
}
