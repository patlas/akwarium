#include "tlv.h"



void TLVtoArray(tlv_t *tlv, uint8_t *rawData)
{
	//ustawia w kolejnosci najstarszy na najstarszym czyli
	//liczba 0x11170 (70000) byte[0]=0x01; byte[1]=0x11; byte[2]=0x70
	memcpy(rawData, tlv, TLV_STRUCT_SIZE);

}

void getTLVcommand(tlv_t *tlv, uint8_t *command)
{
	memcpy(command, tlv->value, tlv->length);
}

command_type_t getTLVtype(tlv_t *tlv)
{
	return (command_type_t)tlv->type;
}

void ArrayToTLV(tlv_t *tlv, uint8_t *rawData)
{
	memcpy((uint8_t*)tlv,rawData,TLV_STRUCT_SIZE);
	
	uint64_t tempLen = 0;
	
	/*
	tempLen |= rawData[1];
	tempLen<<=8;
	tempLen |= rawData[2];
	tempLen<<=8;
	tempLen |= rawData[3];
	tempLen<<=8;	
	tempLen |= rawData[4];
	tempLen<<=8;
	tempLen |= rawData[5];
	tempLen<<=8;
	tempLen |= rawData[6];
	tempLen<<=8;
	tempLen |= rawData[7];
	tempLen<<=8;	
	tempLen |= rawData[8];
	tlv->length = tempLen;*/

}

//cmd string could not be longer than TLV_DATA_SIZE!
void buildTLVheader(tlv_t *tlv, uint8_t* cmd, uint64_t full_cmd_length)
{
	// TODO - this implementation supports only command header
	tlv->type = 0;
	tlv->length = full_cmd_length;//cmd.length();
	memcpy(tlv->value, cmd, full_cmd_length);
}