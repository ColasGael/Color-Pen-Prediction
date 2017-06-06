/* sample for digital weight scale of hx711.display with a 1602 liquid crtstal monitor
 * library design: Weihong Guan (@aguegu)
 * library host on
 *https://github.com/aguegu/ardulibs/tree/3cdb78f3727d9682f7fd22156604fc1e4edd75d1/hx711
 */

// Hx711.DOUT - pin #A2
// Hx711.SCK - pin #A3

// LCD.RS - pin 8
// LCD.En - pin 9
// LCD.D4 - pin 4
// LCD.D5 - pin 5
// LCD.D6 - pin 6
// LCD.D7 - pin 7

#include <LiquidCrystal.h>
#include <Hx711.h>

LiquidCrystal lcd(8, 9, 4, 5, 6, 7);
Hx711 scale(A2, A3);

void setup() {
  Serial.begin(9600);
  lcd.begin(16, 2);
}

void loop() {
  lcd.setCursor(0, 0);
  lcd.print(scale.getGram(), 1);
  lcd.print(" g");
  lcd.print("       ");
  delay(200);
}

