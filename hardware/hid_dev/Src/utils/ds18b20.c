#include "ds18b20.h"

#define pin1wire 1//PINB
#define ddr1wire 1//DDRB
#define input	0
#define set1wire 1//ddr1wire &= ~_BV(input)
#define clear1wire 1//ddr1wire |= _BV(input)

char temp[8];

void _delay_us(int a)
{
}

void _delay_ms(int a)
{
}

unsigned char ds18b20_resetPulse(void)
{
	clear1wire;
	_delay_us(500);
	set1wire;
	_delay_us(500);
	if(pin1wire & 1<<(input)) return 1;
	else return 0;
}

void ds18b20_sendBit(char bit)
{
	clear1wire;
	_delay_us(5);
	if(bit) set1wire;
	_delay_us(80);
	set1wire;
}


unsigned char ds18b20_receiveBit(void)
{
	clear1wire;
	_delay_us(2);
	set1wire;
	_delay_us(15);
	if(pin1wire & 1<<(input)) return 1;
	else return 0;
}


void ds18b20_sendByte(unsigned char data)
{
	char index;
	for(index=0;index<8;index++) ds18b20_sendBit((data>>index) & 0x01);
	_delay_us(100);
}

unsigned char ds18b20_receiveByte(void)
{
	unsigned char data = 0;
	char index;
	for(index=0; index<8; index++)
	{
		data |= (ds18b20_receiveBit()<<index);
		_delay_us(15);
	}
	return data;
}

void ds18b20_readTemp(void)
{
	unsigned char t1=0;
	unsigned char t2=0;

	if(ds18b20_resetPulse())
	{
		ds18b20_sendByte(0xCC);
		ds18b20_sendByte(0x44);
		_delay_ms(750);
		ds18b20_resetPulse();

		ds18b20_sendByte(0xCC);
		ds18b20_sendByte(0xBE);
		t1 = ds18b20_receiveByte();
		t2 = ds18b20_receiveByte();
		ds18b20_resetPulse();
		temp1=(t2<<4 | t1>>4);// /10;
		temp2= (((t1&0x0F)*25)/4);  //// aby wyświetlicz to *25 i /4
	}




}


