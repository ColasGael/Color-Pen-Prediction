/* Arduino library for digital weight scale of hx711
 *
 * hardware design: syyyd
 * available at http://syyyd.taobao.com
 *
 * library design: Weihong Guan (@aguegu)
 * http://aguegu.net
 *
 * modified for Arduino Radon : Gael Colas
 *
 * library host on
 * https://github.com/aguegu/Arduino
 *
 *  Created on: Oct 31, 2012
 * 
 *  Modified on : May 07, 2017
 */

#ifndef HX711_H_
#define HX711_H_

#include "Arduino.h"

class Hx711
{
public:
	Hx711(uint8_t pin_din, uint8_t pin_slk);
	Hx711();
	virtual ~Hx711();
	void begin(uint8_t pin_din, uint8_t pin_slk);
	long getValue();
	long averageValue(byte times = 25);
	void setOffset(long offset);
	void setScale(float scale = 1992.f);
	float getGram();

private:
	uint8_t _pin_dout;
	uint8_t _pin_slk;
	long _offset;
	float _scale;
};

#endif /* HX711_H_ */
