/*
 * ds18b20.h
 *
 *  Created on: 27-05-2012
 *      Author: patlas
 */

#ifndef DS18B20_H_
#define DS18B20_H_

volatile unsigned char temp1;
volatile unsigned char temp2;

unsigned char ds18b20_resetPulse(void);
void ds18b20_sendBit(char bit);
unsigned char ds18b20_receiveBit(void);
void ds18b20_sendByte(unsigned char data);
unsigned char ds18b20_receiveByte(void);
void ds18b20_readTemp(void);


#endif /* DS18B20_H_ */
