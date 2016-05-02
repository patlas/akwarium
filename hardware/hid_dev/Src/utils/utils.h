#include <stdint.h>
#include <stdbool.h>

/* start and stop time hold as minutes = 60*hour[24-format]+mins */
typedef struct{
  uint16_t mStart1;
	uint16_t mStop1;
	uint16_t mStart2;
	uint16_t mStop2;
} high_power_t;

typedef struct{
	uint8_t sol_val[2];		//[0] low Sol, [1] high Sol
	uint16_t sol_adc[2];	// -||-
	uint8_t ph_coef; //mV per 0.5pH
} sol_t;

uint16_t parseTemp(uint8_t *value);
uint8_t parsePH(uint8_t *value);
void turnOnHeater(bool on);
void turnOnCO2(bool on);
void recalc_ph_coef(sol_t *calib_struct);

