#include "ds18b20.h"

#define pin1wire GPIOC->IDR//PINB
#define ddr1wire 1//DDRB
#define input	0
#define set1wire 	 GPIOC->MODER &= ~(3 << (input*2))//ddr1wire &= ~_BV(input) //input pullup
#define clear1wire GPIOC->MODER |= (1 << (input*2))//ddr1wire |= _BV(input) //output

uint8_t temp[8];
volatile uint8_t temp1;
volatile uint8_t temp2;

void ds18b20_init(void)
{
	GPIO_InitTypeDef GPIO_InitStruct;
	
  __GPIOC_CLK_ENABLE();
	GPIO_InitStruct.Pin = (1<<input);
  GPIO_InitStruct.Mode = GPIO_MODE_OUTPUT_PP; //GPIO_MODE_INPUT
  GPIO_InitStruct.Pull = GPIO_PULLUP;
  GPIO_InitStruct.Speed = GPIO_SPEED_LOW;
  HAL_GPIO_Init(GPIOC, &GPIO_InitStruct);
}

uint8_t ds18b20_resetPulse(void)
{
	clear1wire;
	DELAY_US(500);
	set1wire;
	DELAY_US(500);
	if(pin1wire & 1<<(input)) return 1;
	else return 0;
}

void ds18b20_sendBit(uint8_t bit)
{
	clear1wire;
	DELAY_US(5);
	if(bit) set1wire;
	DELAY_US(80);
	set1wire;
}


uint8_t ds18b20_receiveBit(void)
{
	clear1wire;
	DELAY_US(5); //2
	set1wire;
	DELAY_US(15);
	if(pin1wire & 1<<(input)) return 1;
	else return 0;
}


void ds18b20_sendByte(uint8_t data)
{
	int8_t index;
	for(index=0;index<8;index++) ds18b20_sendBit((data>>index) & 0x01);
	DELAY_US(100);
}

uint8_t ds18b20_receiveByte(void)
{
	uint8_t data = 0;
	int8_t index;
	for(index=0; index<8; index++)
	{
		data |= (ds18b20_receiveBit()<<index);
		DELAY_US(15);
	}
	return data;
}

void ds18b20_readTemp(void)
{
	uint8_t t1=0;
	uint8_t t2=0;

	if(ds18b20_resetPulse())
	{
		ds18b20_sendByte(0xCC);
		ds18b20_sendByte(0x44);
		DELAY_MS(750);
		ds18b20_resetPulse();

		ds18b20_sendByte(0xCC);
		ds18b20_sendByte(0xBE);
		t1 = ds18b20_receiveByte();
		t2 = ds18b20_receiveByte();
		ds18b20_resetPulse();
		temp1=(t2<<4 | t1>>4);// /10;
		///temp2= (((t1&0x0F)*25)/4);  //// aby wyświetlicz to *25 i /4
		temp2= (((t1&0x0F)*5)/8);
	}




}


