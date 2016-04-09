#include <stdint.h>
#include <string.h>

#define TLV_DATA_SIZE 21
#define TLV_STRUCT_SIZE 30


#pragma pack(push)
#pragma pack(1)
typedef struct tlv_t {
	uint8_t type;
	uint64_t length;
	uint8_t value[TLV_DATA_SIZE];
}tlv_t;
#pragma pack(pop)

/* Avaliable TYPEs in TLV frame (first byte)
	*
	* 0x01 - SET_TEMP,	data: 0x03,0x02,0x04 //(each uint8_t); 32,4 [st.C]	(AUTO_TERM)
	* 0x02 - SET_PH,		data: 0x00,0x07,0x08 //(each uint8_t); pH 7,8 			(AUTO_CO2)
	* 0x03 - SET_OUT1,	data: 0x0000,0x0000,0x0000, 0x0000 //(each uint16_t - check endian); as in high_power_t -> make cast
	* 0x04 - SET_OUT2,	data: 0x0000,0x0000,0x0000, 0x0000 //(each uint16_t - check endian); as in high_power_t -> make cast
	* 0x05 - SET_OUT3,	data: 0x0000,0x0000,0x0000, 0x0000 //(each uint16_t - check endian); as in high_power_t -> make cast
	* 0x06 - SET_OUT4,	data: 0x0000,0x0000,0x0000, 0x0000 //(each uint16_t - check endian); as in high_power_t -> make cast
	* 0x07 - SET_LED1, TBD
	* 0x08 - SET_LED2, TBD
 */
typedef enum command_type_t
{
	SET_TEMP = 0x01,
	SET_PH,
	SET_OUT1,
	SET_OUT2,
	SET_OUT3,
	SET_OUT4,
	SET_LED1,
	SET_LED2
} command_type_t;



void TLVtoArray(tlv_t *tlv, uint8_t *rawData);
void ArrayToTLV(tlv_t *tlv, uint8_t *rawData);
command_type_t getTLVtype(tlv_t *tlv);

	

